import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;


public class ClientHandler implements Runnable {
    // this class for reading messges and broadcasting then deleting user after completion
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter out; 
    private BufferedReader in; 
    private String username;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.clientSocket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            // Setup streams for communication with the client.
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // The first line a client sends is their username.
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }
            System.out.println(username + " has joined the chat.");
            broadcastMessage("[SERVER] " + username + " has joined the chat.");

            String clientMessage;
            // The main loop for reading messages from the client.
            while ((clientMessage = in.readLine()) != null) {
                // broadcasting messages it to all other clients when message is recieved
                String broadcastMessage = "[" + username + "]: " + clientMessage;
                broadcastMessage(broadcastMessage);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + username + ". " + e.getMessage());
        } finally {
            removeClient();
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }

    private void removeClient() {
        synchronized (clients) {
            clients.remove(this);
        }
        System.out.println(username + " has left the chat.");
        broadcastMessage("[SERVER] " + username + " has left the chat.");
    }
}
