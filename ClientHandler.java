
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable 
{    
    // Define a static array to contain all of the clientHandlers
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    
    private Socket          socket;
    private BufferedReader  bReader;
    private BufferedWriter  bWriter;
    private String          clientUsername;


    // Constructor for the client handler class
    public ClientHandler(Socket socket)
    {
        // Try to establish new client handler
        try {
            // Set the socket
            this.socket = socket;

            // Establish input and output readers from new streams
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Set the username
            this.clientUsername = bReader.readLine();

            // Add this new client handler to the static array
            clientHandlers.add(this);
            
            // Broadcast new client joining to every client
            broadcast("SERVER: " + clientUsername + " has connected!");
        } catch (IOException e) {
            disconnectClient(socket, bReader, bWriter);
        }
    }

    // Sends out a broadcast message to all connected clients
    public void broadcast(String message)
    {
        // Loop through all of the connected clients and send a new message
        for (ClientHandler clientHandler : clientHandlers) {
            // Try to send the message
            try {
                // Only send the message to clients that aren't the sender
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    // Write the message with the client writer, then write a new line, then flush to send
                    clientHandler.bWriter.write(message);
                    clientHandler.bWriter.newLine();
                    clientHandler.bWriter.flush();
                }
            } catch (IOException e) {
                disconnectClient(socket, bReader, bWriter);
            }
        }
    }

    // Handles removing the current client handler
    public void removeClientHanlder()
    {
        // Remove this client handler from the static array
        clientHandlers.remove(this);

        // Send out a message to the other clients
        broadcast("SERVER: " + clientUsername + " has disconnected!");
    }

    // Disconnects the current client
    public void disconnectClient(Socket socket, BufferedReader bReader, BufferedWriter bWriter)
    {
        // Remove the client handler
        removeClientHanlder();

        // Try to close all resources
        try {
            if (bReader != null) bReader.close();
            if (bWriter != null) bWriter.close();
            if (socket  != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Runnable needs to have run method so that the thread can run our code
    @Override
    public void run()
    {
        String message;

        // While the socket is alive, try receive new messages
        while (socket.isConnected()) {
            // Try to get new messages
            try {
                // Get the message from the buffered reader
                message = bReader.readLine();

                // If the message sent was empty, throw IOException
                if (message == null) throw new IOException();

                // Relay the message to every client
                broadcast(message);
            } catch (IOException e) {
                disconnectClient(socket, bReader, bWriter);

                // Break out of the loop
                break;
            }
        }
    }

}
