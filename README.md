# Client-Server
### Homework for Lecture 33
This app has a `Server` class that accepts any number of incoming connections from a client (`Connection` class) and echoes client's messages. Each connection is assigned a standardized name (`client-N`) and added to activeConnections.

If client sends message `exit`, this connection is closed and server removes it from the active connections.
