import java.net.Socket;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


// Define our client class
public class client 
{
    // Constants
    private final static int    PORT_NUMBER = 6666;
    private final static String ADDRESS     = "localhost";
    private final static int    SLEEP_TIME  = 2;

    public static void main(String[] args) throws Exception
    {
        Socket      socket = new Socket(ADDRESS, PORT_NUMBER);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        // Send message to server, then clean stream
        writer.println("Hello server!");
        writer.flush();
        
        // Wait some time
        TimeUnit.SECONDS.sleep(SLEEP_TIME);

        // Close the socket
        socket.close();
    }
}