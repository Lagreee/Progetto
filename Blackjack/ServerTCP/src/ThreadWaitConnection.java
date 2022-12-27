import java.net.*;

public class ThreadWaitConnection extends Thread {

    ServerSocket serverSocket;
    int numClient = 0;

    public ThreadWaitConnection(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Rimani in ascolto di un client
                System.out.println("Started waiting on: " + serverSocket);
                Socket socket = serverSocket.accept();
            
                System.out.println("Connection accepted: "+ socket);
                //Aggiungi il client al Connection Manager
                ConnectionManager.getInstance().AddClient(new JConnect("Client"+numClient++, socket));
                
            } catch(Exception e){e.printStackTrace();}
            finally{}
        }
    }
}
