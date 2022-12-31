import java.io.IOException;
import java.net.ServerSocket;

public class ServerTCP {
    public static void main(String[] args) throws IOException
    {
        final int PORT = 8080;
        ServerSocket serverSocket = new ServerSocket(PORT);
        
        ConnectionManager.getInstance().AddTavolo("Tavolo1");
        ConnectionManager.getInstance().AddTavolo("Tavolo2");
        //ConnectionManager.getInstance().AddTavolo("Tavolo3");
        
        ThreadWaitConnection ThreadAspettaConnessioni = new ThreadWaitConnection(serverSocket);
        ThreadAspettaConnessioni.start();
   
    }
}
