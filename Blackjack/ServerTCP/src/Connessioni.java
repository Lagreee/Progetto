import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Connessioni {
    ServerSocket serverSocket;
    List<JConnect> ListaConnessioni = new ArrayList<JConnect>();
    int numClient = 0;

    public Connessioni(ServerSocket sIn){
        serverSocket = sIn;
    }
    
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    void addClient(String id, Socket socket){
        ListaConnessioni.add(new JConnect(id, socket));
    }

    void addClient(Socket socket){
        ListaConnessioni.add(new JConnect("Client"+numClient++, socket));
    }

    void BroadcastMsg(String senderId, String msg){
        System.out.println("Broadcast Message from " + senderId);
        for (JConnect jConnect : ListaConnessioni) {
            if (!jConnect.getId().equals(senderId)){
                jConnect.SendMsg(msg);
                System.out.println("Sent ["+ msg +"] to (" + jConnect.id + ")");
            }
        }
    }

    void removeConnectionByID(String id){
        /*
        for (JConnect jConnect : ListaConnessioni) {
            if(jConnect.getId().equals(id)){
                ListaConnessioni.remove(jConnect);
            }
        }
        */
        for (int i = 0; i < ListaConnessioni.size(); i++) {
            if (ListaConnessioni.get(i).getId().equals(id)) {
                ListaConnessioni.remove(ListaConnessioni.get(i));
            }
        }
    }

    public void SendMsgTo(String Mittente, String Destinatario, String messaggio) {
        Boolean trovato = false;
        int x = 0;
        JConnect temp = ListaConnessioni.get(x);
        
        while (!trovato && x < ListaConnessioni.size()) {
            temp = ListaConnessioni.get(x);
            if (temp.getId().equals(Destinatario)) {
                trovato = true;
                break;                
            }
            x++;
        }

        if (trovato) {
            temp.SendMsg(Mittente + " whispers:" + messaggio);
        }
    }
}
