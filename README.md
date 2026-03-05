# ♟️ Multiplayer Chess — Java TCP Sockets

A fully-featured, real-time 2-player Chess game playable over a network directly in the terminal. Built from scratch in Java using TCP sockets and multithreading — no external libraries.

---

## 🎮 Demo

```
    a   b   c   d   e   f   g   h
  +---+---+---+---+---+---+---+---+
8 | ♜ | ♞ | ♝ | ♛ | ♚ | ♝ | ♞ | ♜ | 8
  +---+---+---+---+---+---+---+---+
7 | ♟ | ♟ | ♟ | ♟ | ♟ | ♟ | ♟ | ♟ | 7
  +---+---+---+---+---+---+---+---+
6 |   |   |   |   |   |   |   |   | 6
  +---+---+---+---+---+---+---+---+
5 |   |   |   |   |   |   |   |   | 5
  +---+---+---+---+---+---+---+---+
4 |   |   |   |   | ♙ |   |   |   | 4
  +---+---+---+---+---+---+---+---+
3 |   |   |   |   |   |   |   |   | 3
  +---+---+---+---+---+---+---+---+
2 | ♙ | ♙ | ♙ | ♙ |   | ♙ | ♙ | ♙ | 2
  +---+---+---+---+---+---+---+---+
1 | ♖ | ♘ | ♗ | ♕ | ♔ | ♗ | ♘ | ♖ | 1
  +---+---+---+---+---+---+---+---+
    a   b   c   d   e   f   g   h

Your turn (WHITE)
Enter move (e.g. e2 e4) or 'resign':
```

---

## ✨ Features

- **Real-time multiplayer** over TCP sockets — play across two terminals or two machines
- **Complete chess rules** implemented from scratch:
  - All 6 piece types with correct movement
  - Check & checkmate detection
  - Stalemate detection
  - Castling (kingside & queenside)
  - En passant
  - Pawn promotion (auto-queen)
- **Multithreaded server** — handles both players simultaneously on separate threads
- **Move validation** — illegal moves are rejected with an error message
- **Resign** support

---

## 🏗️ Architecture

```
Player 1 (Client)  ←── TCP Socket ──→  GameServer  ←── TCP Socket ──→  Player 2 (Client)
```

- **GameServer** — listens on port 5555, accepts exactly 2 connections, assigns colors (WHITE/BLACK), and relays moves between players
- **GameClient** — connects to server, handles user input, validates moves locally, renders the board
- **Board** — maintains game state as a 2D array of Piece objects, handles move application and deep copying for safe simulation
- **MoveValidator** — filters pseudo-legal moves by simulating each move and checking if it leaves the king in check
- **Piece hierarchy** — abstract `Piece` base class extended by `King`, `Queen`, `Rook`, `Bishop`, `Knight`, `Pawn`

---

## 📁 Project Structure

```
ChessGame/
├── client/
│   └── GameClient.java       # Player client — input, rendering, networking
├── server/
│   └── GameServer.java       # Server — accepts 2 players, relays moves
├── game/
│   ├── Board.java            # Game state, move application, check detection
│   ├── Piece.java            # Abstract base class for all pieces
│   ├── MoveValidator.java    # Legal move filtering
│   └── pieces/
│       ├── King.java
│       ├── Queen.java
│       ├── Rook.java
│       ├── Bishop.java
│       ├── Knight.java
│       └── Pawn.java
└── utils/
    └── Display.java          # Terminal board rendering, notation parsing
```

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher
- Two terminal windows (or two machines on the same network)

### Compile
```bash
javac -d out game/Piece.java game/Board.java game/pieces/*.java game/MoveValidator.java utils/Display.java server/GameServer.java client/GameClient.java
```

### Run

**Terminal 1 — Start the server:**
```bash
java -cp out server.GameServer
```

**Terminal 2 — Player 1:**
```bash
java -cp out client.GameClient
```

**Terminal 3 — Player 2:**
```bash
java -cp out client.GameClient
```

> To play over a network, Player 2 runs:
> ```bash
> java -cp out client.GameClient <server-ip>
> ```

### How to Move
Enter moves in standard chess notation: `[from] [to]`
```
e2 e4     → moves piece from e2 to e4
g1 f3     → moves knight from g1 to f3
resign    → forfeit the game
```

---

## 🧠 Key Concepts Used

| Concept | Where |
|---|---|
| TCP Sockets (`ServerSocket`, `Socket`) | `GameServer.java`, `GameClient.java` |
| Multithreading (`Thread`, `Runnable`) | `GameServer.java` — one thread per player |
| Abstract classes & Inheritance | `Piece.java` → all 6 piece classes |
| Recursive check detection | `Board.isInCheck()`, `MoveValidator` |
| Deep copy / immutable state | `Board.deepCopy()` for move simulation |
| OOP Design | Entire project structure |

---

## 🛠️ Built With

- **Java 23**
- **Java Networking** (`java.net`)
- **Java I/O** (`java.io`)
- No external libraries

---

## 👩‍💻 Author

**Siri** — [github.com/siricodez](https://github.com/siricodez)
