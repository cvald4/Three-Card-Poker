import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;

public class ClientNetwork extends Thread {

    private final String host;
    private final int port;
    private final Consumer<String> textCallback;
    private Consumer<PokerInfo> infoReceiver;
    private Socket socketClient;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private volatile boolean running = true;

    public ClientNetwork(String host, int port, Consumer<String> textCallback) {
        this.host = host;
        this.port = port;
        this.textCallback = textCallback;
    }

    @Override
    public void run() {
        try {
            socketClient = new Socket(host, port);
            output = new ObjectOutputStream(socketClient.getOutputStream());
            input = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);

            Platform.runLater(() -> textCallback.accept("Connected to server"));

            while (running) {
                try {
                    Object obj = input.readObject();
                    
                    if (obj instanceof PokerInfo) {
                        PokerInfo info = (PokerInfo) obj;
                        String msg;
                        
                        if (info.statusMessage != null) {
                            msg = info.statusMessage;
                        } else {
                            msg = "Received: " + info.messageType;
                        }
                        
                        Platform.runLater(() -> textCallback.accept(msg));

                        if (infoReceiver != null) {
                            Platform.runLater(() -> infoReceiver.accept(info));
                        }
                    } else {
                        Platform.runLater(() -> textCallback.accept("Received unknown from server"));
                    }
                } catch (Exception e) {
                    if (running) {
                        Platform.runLater(() -> textCallback.accept("Connection lost: " + e.getMessage()));
                    }
                    running = false;
                }
            }

        } catch (Exception e) {
            Platform.runLater(() -> textCallback.accept("Connection error: " + e.getMessage()));
        } finally {
            closeConnection();
        }
    }

    public void sendPokerInfo(PokerInfo info) {
        try {
            synchronized (output) {
                output.writeObject(info);
                output.flush();
            }
        } catch (Exception e) {
            Platform.runLater(() -> textCallback.accept("Send failed: " + e.getMessage()));
        }
    }

    public void closeConnection() {
        running = false;
        try {
            if (input != null) {
                input.close();
            }
        } catch (Exception ignored) {
        }
        
        try {
            if (output != null) {
                output.close();
            }
        } catch (Exception ignored) {
        }
        
        try {
            if (socketClient != null) {
                socketClient.close();
            }
        } catch (Exception ignored) {
        }
        
        Platform.runLater(() -> textCallback.accept("Disconnected from server"));
    }

    public void setInfoReceiver(Consumer<PokerInfo> receiver) {
        this.infoReceiver = receiver;
    }
}