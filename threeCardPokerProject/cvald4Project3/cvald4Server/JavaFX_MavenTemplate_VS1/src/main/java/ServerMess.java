import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ServerMess {

    public interface GameResultListener {
        void onGameResult(int totalGames, String lastResult);
    }

    private Consumer<String> logCallback;
    private Consumer<Integer> clientCountCallback;
    private GameResultListener gameResultCallback; 

    private volatile boolean running = false;
    private TheServer serverThread;

    // Game State
    private Deck deck;
    private ArrayList<Card> playerHand;
    private ArrayList<Card> dealerHand;

    private int clientCount = 0;
    private int gamesPlayed = 0;

    public ServerMess(Consumer<String> logCallback) {
        this.logCallback = logCallback; 
    }

    public void setLogCallback(Consumer<String> cb) { this.logCallback = cb; }
    public void setClientCountCallback(Consumer<Integer> cb) { this.clientCountCallback = cb; }
    public void setGameResultCallback(GameResultListener cb) { this.gameResultCallback = cb; }

    private synchronized void updateClientCount(int change) {
        clientCount += change;
        if (clientCountCallback != null) {
            clientCountCallback.accept(clientCount);
        }
    }

    private synchronized void recordGameFinished(String result) {
        gamesPlayed++;
        if (gameResultCallback != null) {
            gameResultCallback.onGameResult(gamesPlayed, result);
        }
    }

    public boolean isRunning() { return running; }

    public void startServer(int port) {
        if (running) return;
        running = true;
        serverThread = new TheServer(port);
        serverThread.start();
    }

    public void stopServer() {
        running = false;
        log("Stopping server...");
        if (serverThread != null) {
            try {
                if (serverThread.serverSocket != null) serverThread.serverSocket.close();
            } catch (Exception ignored) {}
        }
    }

    private void log(String msg) {
        if (logCallback != null) logCallback.accept(msg);
    }

    class TheServer extends Thread {
        private final int port;
        ServerSocket serverSocket;

        TheServer(int port) { this.port = port; }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                log("Server listening on port " + port);

                while (running) {
                    Socket client = serverSocket.accept();
                    if (!running) break;

                    // Update count safely
                    updateClientCount(1);
                    
                    log("Client connected. Total: " + clientCount);
                    new ClientThread(client).start();
                }

            } catch (Exception e) {
                if (running) log("Server error: " + e.getMessage());
            }
        }
    }

    class ClientThread extends Thread {
        private final Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        ClientThread(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in  = new ObjectInputStream(socket.getInputStream());
                socket.setTcpNoDelay(true);

                log("Streams opened.");

                while (running && !socket.isClosed()) {
                    Object obj = in.readObject();
                    if (obj instanceof PokerInfo) {
                        PokerInfo req = (PokerInfo) obj;
                        PokerInfo resp = handleRequest(req);
                        out.writeObject(resp);
                        out.reset();

                        if (resp != null && ("RESULT".equals(resp.messageType) || "FOLD".equals(resp.messageType))) {
                            playerHand = null;
                            dealerHand = null;
                        }
                    }
                }
            } catch (Exception e) {
                log("Client disconnected.");
            } finally {
                // Update count safely
                updateClientCount(-1);
                
                log("Client removed. Total: " + clientCount);
                safeClose();
            }
        }

        private void safeClose() {
            try { if (in != null) in.close(); } catch (Exception ignored) {}
            try { if (out != null) out.close(); } catch (Exception ignored) {}
            try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        }
    }

    private PokerInfo handleRequest(PokerInfo req) {
        if (req == null) return null;
        switch (req.messageType) {
            case "BET":  return processBet(req);
            case "PLAY": return processPlay(req);
            case "FOLD": return processFold(req);
            default: return new PokerInfo();
        }
    }

    private PokerInfo processBet(PokerInfo req) {
        deck = new Deck();
        deck.shuffle();
        playerHand = deck.deal(3);
        dealerHand = deck.deal(3);

        PokerInfo info = new PokerInfo();
        info.messageType = "DEAL";
        info.playerCards = playerHand;
        info.statusMessage = "Dealt cards.";
        info.ante = req.ante;
        info.pairPlus = req.pairPlus;
        
        log("Processed BET. Ante: " + req.ante);
        return info;
    }

    private PokerInfo processPlay(PokerInfo req) {
        boolean dealerQualifies = GameLogic.dealerQualifies(dealerHand);
        int cmp = GameLogic.compareHands(dealerHand, playerHand);
        int pairPlusWin = (req.pairPlus > 0) ? GameLogic.calcWinnings(playerHand, req.pairPlus) : 0;
        
        int net = pairPlusWin;
        if (!dealerQualifies) {
            net += req.ante;
        } else {
            if (cmp > 0) net += (req.ante + req.playWager);
            else if (cmp < 0) net -= (req.ante + req.playWager);
        }

        PokerInfo resp = new PokerInfo();
        resp.messageType = "RESULT";
        resp.playerCards = playerHand;
        resp.dealerCards = dealerHand;
        resp.amountWonOrLost = net;
        resp.statusMessage = (net >= 0) ? "Won $" + net : "Lost $" + Math.abs(net);

        log("Send PLAY. Result: " + resp.statusMessage);

        recordGameFinished(resp.statusMessage);

        return resp;
    }

    private PokerInfo processFold(PokerInfo req) {
        int pairPlusWin = (req.pairPlus > 0) ? GameLogic.calcWinnings(playerHand, req.pairPlus) : 0;
        int net = -req.ante + pairPlusWin;

        PokerInfo resp = new PokerInfo();
        resp.messageType = "FOLD";
        resp.amountWonOrLost = net;
        resp.statusMessage = "Folded. Net: " + net;

        log("Send FOLD.");

        recordGameFinished("Player Folded");

        return resp;
    }
}