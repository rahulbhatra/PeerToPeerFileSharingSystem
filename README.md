# 📁 Peer-to-Peer File Sharing System
This project implements a Java-based Peer-to-Peer (P2P) file-sharing system demonstrating both centralized and decentralized search architectures. Designed as part of a distributed systems coursework, the system supports multiple peers sharing files directly with each other using TCP sockets and a central indexing server (Napster-style) or via query flooding (Gnutella-style).

## 🔧 Features
1. **Central Indexing Server:** Maintains a directory of peers and shared files. Handles peer registration and file discovery requests.
2. **Peer Node:** Acts as both client and server. Shares files, discovers others’ files, and supports direct peer-to-peer transfers.
3. **Decentralized Discovery (Gnutella-style):** Option to search files via neighbor query propagation without relying on a central server.
4. **Concurrent Transfers:** Supports multiple simultaneous uploads and downloads via multi-threaded design.
5. **Auto Directory Sync:** Peers monitor their local shared directory and auto-update the index server on file changes.
6. **Performance Evaluation:** Includes scripts and results comparing parallel vs. sequential downloads.

### 🚀 How to Run
1. Make the script executable: ```chmod +x run.sh```
2. Execute the script: ```./run.sh```


### 📚 Use Case
This project is built for educational purposes to demonstrate fundamental P2P file sharing concepts, including peer discovery, direct transfer, and protocol trade-offs. It’s ideal for students learning distributed systems or networking.
