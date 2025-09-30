import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    //class for allowing user to connect to the server and handling client messages
    // The server's IP address 
    private static final String SERVER_ADDRESS = "127.0.0.1";
    // The server's port.
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.out.print("Enter your username: ");
        try (
            // Setup resources that will be auto-closed.
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String username = consoleReader.readLine();
            out.println(username);

            System.out.println("Connected to the chat server! You can start typing messages.");
            System.out.println("Type 'exit' to quit.");

            // Create and start a new thread for listening to server messages.
            // This allows the client to listen for incoming messages and send msgs
            new Thread(new ServerListener(socket)).start();

            // Main loop to read user input from the console and send it to the server.
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                out.println(userInput);
            }

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
        System.out.println("You have been disconnected.");
    }

    //helper class that runs in its own for listening to messages
    private static class ServerListener implements Runnable {
        private Socket socket;
        private BufferedReader in;

        public ServerListener(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
            }
        }
    }
}
