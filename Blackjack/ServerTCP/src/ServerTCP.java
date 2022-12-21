import java.io.IOException;
import java.net.ServerSocket;

public class ServerTCP {
    public static void main(String[] args) throws IOException
    {
        final int PORT = 8080;
        ServerSocket serverSocket = new ServerSocket(PORT);
        Connessioni connessioni = new Connessioni(serverSocket);
        
        ThreadWaitConnessioni x = new ThreadWaitConnessioni(serverSocket, connessioni);
        x.start();
    }
}
