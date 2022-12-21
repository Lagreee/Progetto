import java.io.IOException;
import java.net.ServerSocket;

public class ServerTCP {
    public static void main(String[] args) throws IOException
    {
        final int PORT = 8080;
        ServerSocket serverSocket = new ServerSocket(PORT);
        Connessioni connessioni = new Connessioni(serverSocket);
        
        ThreadWaitConnection ConnectionManager = new ThreadWaitConnection(serverSocket, connessioni);
        ConnectionManager.start();

        GameManager gameManager = new GameManager(connessioni);
        gameManager.startGame();        
    }
}
