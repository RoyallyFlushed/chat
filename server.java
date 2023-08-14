import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.concurrent.TimeUnit;


// Define server class
public class server 
{
    // Constants
    private final static int PORT_NUMBER    = 6666;
    private final static int LIFE_TIME      = 7;

    // Define static entry point
    public static void main(String[] args) throws Exception
    {
        ServerSocket    serverSocket    = null;
        Socket          socket          = null;

        serverSocket = new ServerSocket(PORT_NUMBER);

        // Successfully set up server socket
        System.out.println("Listening on Port " + PORT_NUMBER);

        // Establish the connection
        socket = serverSocket.accept();

        // Successfully established connection
        System.out.println("Connection established!");

        // Establish a new stream reader for the current socket
        InputStreamReader   sreader = new InputStreamReader(socket.getInputStream());
        BufferedReader      breader = new BufferedReader(sreader);

        // Read the message from the client and print it.
        String message = breader.readLine();
        System.out.println("Client: " + message);

        // Wait some time before closing the socket
        TimeUnit.SECONDS.sleep(LIFE_TIME);

        // Close the socket
        socket.close();
        serverSocket.close();

        // Successfully closed server
        System.out.println("Server Closed");
    }

}