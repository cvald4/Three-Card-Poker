# 🃏 Three Card Poker (Client & Server)

A full-stack, networked digital implementation of the classic casino table game, **Three Card Poker**. This project features a robust client-server architecture, allowing multiple users to connect and play concurrently using a rich JavaFX graphical interface.

---

## ✨ Features

### Client
*   **Interactive UI:** Multi-scene JavaFX layout seamlessly navigating between a Welcome configuration screen, the main Game board, and a post-hand Results screen.
*   **Custom Betting:** Support for standard Ante wagers ($5, $10, $20, $25) and optional Pair Plus side bets ($0, $5, $10, $15, $20).
*   **Dynamic Theming:** Toggle the UI aesthetic between "Classic Green" and "Ocean Blue".
*   **Real-Time Status:** A built-in console window displays network events and game state updates to the player.

### Server
*   **Authoritative Game Logic:** The server securely generates a standard 52-card deck, deals hands, evaluates card ranks (Straight Flush, Three of a Kind, etc.), checks for dealer qualification, and calculates all payouts.
*   **Graphical Dashboard:** A dedicated UI (`server_intro.fxml`, `server_status.fxml`) allows server administrators to configure listening ports, view real-time logs, and monitor server health.
*   **Live Metrics Tracking:** Automatically tracks connected clients, total hands dealt, and the outcome of the most recent game.

---

## ⚙️ Concurrency, Multithreading & Synchronization

To ensure real-time responsiveness and accommodate multiple players simultaneously, this application relies on a strictly managed multithreaded architecture. 

### Server-Side Threading
*   **Non-Blocking Listener:** The server utilizes a primary `TheServer` thread that continuously listens for incoming `Socket` connections on the specified port without blocking the main application.
*   **Concurrent Client Handlers:** Upon a successful connection, a new `ClientThread` is spawned for that specific user This enables the server to evaluate hands and route game data for multiple independent sessions simultaneously

### Client-Side Threading
*   **Dedicated Network Thread:** The client initializes a `ClientNetwork` thread that runs continuously in the background, listening for incoming `PokerInfo` objects from the server This ensures the JavaFX UI remains responsive and fluid while waiting for server operations.

### Synchronization & Thread Safety
*   **State Integrity:** On the server, critical shared resources—such as `clientCount` and `gamesPlayed`—are modified using `synchronized` methods (`updateClientCount()` and `recordGameFinished()`). This prevents race conditions when multiple `ClientThread`s attempt to update global metrics simultaneously.
*   **Thread-Safe I/O:** When the client sends wagers or fold decisions, the `sendPokerInfo()` method wraps the `ObjectOutputStream.writeObject()` call in a `synchronized (output)` block to guarantee that serialized payload transmission is atomic and uninterrupted.
*   **Safe UI Updates:** Because JavaFX throws exceptions if UI components are modified outside the main application thread, both the client and server heavily utilize `Platform.runLater()`. This queues UI updates (such as appending logs, revealing cards, or updating metric labels) to execute safely on the JavaFX Application Thread.

---

## 📖 Game Rules & Payouts

1.  **The Deal:** Both the player and the dealer receive 3 cards. 
2.  **The Decision:** The player can either **Fold** (forfeit the Ante) or **Play** (match the Ante).
3.  **Dealer Qualification:** The dealer must hold a **Queen-high or better** to qualify.
    *   *If the dealer does not qualify:* The Play bet pushes, and the Ante bet pays 1:1.
    *   *If the dealer qualifies:* Hands are compared. Standard poker hand rankings apply.
4.  **Pair Plus:** An independent side bet that pays out if the player is dealt a Pair or better, utilizing standard casino pay tables (e.g., Straight Flush pays 40:1).

---

## 🚀 Installation & Setup

### Prerequisites
*   Java Development Kit (JDK) 11 or higher.
*   JavaFX SDK configured in your IDE or build environment.

### 1. Running the Server
1.  Launch the `Server.java` main class.
2.  Input a port number (default `5555`) and click **Start Server**.
3.  Monitor active connections and logs via the server status dashboard.

### 2. Running the Client
1.  Launch the `ClientApp.java` main class.
2.  Enter the server's IP address (use `127.0.0.1` for local testing) and the corresponding port.
3.  Click **Connect** to begin your session. 

---

## 🔮 Future Roadmap: Low-Latency Systems Migration

As this project evolves into a highly concurrent, production-grade system, the roadmap includes optimizations targeted at proprietary trading-level latency and throughput:
*   **C++ Backend Migration:** Transitioning the authoritative server logic to C++ to leverage aggressive memory management and hardware-level optimizations. 
*   **WebSockets & Zero-Copy Networking:** Replacing standard Java serialization via `ObjectOutputStream` with a WebSockets architecture utilizing raw byte buffers or Cap'n Proto for lightning-fast payload parsing.
*   **Lock-Free Data Structures:** Replacing standard `synchronized` block bottlenecks on global state with atomic variables and lock-free queues to handle massive spikes in simultaneous client actions.
