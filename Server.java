import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


// Define server class
public class Server 
{
    // Constants
    private final static int PORT_NUMBER    = 6666;

    // Properties
    private ServerSocket serverSocket       = null;


    // Establishes a new Server object and sets the serverSocket
    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    // Method starts running our server
    public void start()
    {
        // Try to establish connection
        try {
            // While the server socket isn't closed, accept new requests
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                System.out.println("New connection! IP: " + socket.getInetAddress());

                // Create a new client handler with the new connection socket
                ClientHandler clientHandler = new ClientHandler(socket);

                // Run the clientHandler in a new thread
                Thread thread = new Thread(clientHandler);

                // Start the thread
                thread.start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void close()
    {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main entry point for the server
    public static void main(String[] args) throws IOException
    {
        // Create a new server socket & then a new instance of our Server with the server socket
        ServerSocket    serverSocket    = new ServerSocket(PORT_NUMBER);
        Server          server          = new Server(serverSocket);

        // Start the server
        server.start();
    }

}