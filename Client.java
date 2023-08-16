import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


// Define our client class
public class Client 
{
    // Constants
    private final static int    PORT_NUMBER = 6666;
    private final static String ADDRESS     = "localhost";
    
    private Socket          socket;
    private BufferedReader  bReader;
    private BufferedWriter  bWriter;
    private String          username;

    
    // Constructor for the class
    public Client(Socket socket, String username)
    {
        // Try to set the resources
        try {
            this.socket     = socket;
            this.bReader    = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bWriter    = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username   = username;
        } catch (IOException e) {
            e.printStackTrace();

            // Disconnect the client if something went wrong
            disconnect(socket, bReader, bWriter);
        }
    }

    // Sets up core loop for sending new messages to server
    public void sendInput()
    {
        // Try to start sending messages
        try {
            // Send the username over first
            bWrite(this, username);

            // Establish a new scanner object to read from stdin
            Scanner scanner = new Scanner(System.in);

            // While the socket is alive, wait for new input and send
            while (socket.isConnected()) {
                // Get new message from input
                String message = scanner.nextLine();

                // Write the message to the buffered writer and send
                bWrite(this, username + ": " + message);
            }

            // Close the scanner
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();

            // Disconnect the client if something went wrong
            disconnect(socket, bReader, bWriter);
        }
    }

    // Handles disconnecting the client and closing all resources
    public void disconnect(Socket socket, BufferedReader bReader, BufferedWriter bWriter)
    {
        // Try to close all resources
        try {
            if (bReader != null) bReader.close();
            if (bWriter != null) bWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles receiving messages from the server
    public void listenIncoming()
    {
        // Create a new Thread object with a new Runnable class
        new Thread(new Runnable() {
            
            // Define run method for runnable class
            @Override
            public void run()
            {
                String message;

                // While the socket is still alive, try get new messages from server
                while (socket.isConnected()) {
                    // Try get messages from server
                    try {
                        // Get the message from the buffered reader and output
                        message = bReader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                        
                        // Disconnect the socket if an error occurred
                        disconnect(socket, bReader, bWriter);

                        // Break out of the loop
                        break;
                    }
                }
            }

        }).start(); // Start the thread
    }

    // Helper function to send message using buffered reader
    public static void bWrite(Client client, String message) throws IOException
    {
        client.bWriter.write(message);
        client.bWriter.newLine();
        client.bWriter.flush();
    }

    // Main entry point for program
    public static void main(String[] args) throws IOException
    {
        // Create a new scanner object to read from stdin
        Scanner scanner = new Scanner(System.in);

        // Output message to collect username
        System.out.println("Enter a username: ");

        // Get username from input
        String username = scanner.nextLine();

        // Create a new socket and client
        Socket socket = new Socket(ADDRESS, PORT_NUMBER);
        Client client = new Client(socket, username);

        // Listen for incoming messages on a new thread, and send any posted messages to server
        client.listenIncoming();
        client.sendInput();

        // Close the scanner
        scanner.close();
    }
}