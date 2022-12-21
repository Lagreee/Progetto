import java.net.*;

public class ThreadWaitConnection extends Thread {
    Connessioni connessioni;
    ServerSocket serverSocket;

    public ThreadWaitConnection(ServerSocket serverSocket, Connessioni connessioni){
        this.serverSocket = serverSocket;
        this.connessioni = connessioni;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Rimani in ascolto di un client
                System.out.println("Started waiting on: " + serverSocket);
                Socket socket = serverSocket.accept();
                
                System.out.println("Connection accepted: "+ socket);
                connessioni.addClient(socket);
                /*
                ThreadConnessione t = new ThreadConnessione(socket, connessioni);
                t.start();
                 */
            } catch(Exception e){e.printStackTrace();}
            finally{}
        }
    }
}
