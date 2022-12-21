import java.net.*;

public class ThreadWaitConnessioni extends Thread {
    Connessioni connessioni;
    ServerSocket serverSocket;
    //List<ThreadConnessione> threadList = new ArrayList<ThreadConnessione>();

    public ThreadWaitConnessioni(ServerSocket serverSocket, Connessioni connessioni){
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
                ThreadConnessione t = new ThreadConnessione(socket, connessioni);
                t.start();
                /*
                threadList.add(new ThreadConnessione(socket, connessioni));
                threadList.get(threadList.size()-1).start();
                 */
            } catch(Exception e){e.printStackTrace();}
            finally{}
        }
    }
}
