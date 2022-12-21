import java.io.*;
import java.net.*;

public class ThreadConnessione extends Thread {
    Connessioni connessioni;
    Socket connectionSocket;
    String ClientName;

    String inString;
    
    String[] Strings;
    String destString;
    String msgString;

    public ThreadConnessione(Socket socket, Connessioni connessioni){
        connectionSocket = socket;
        this.connessioni = connessioni;
    }

    @Override
    public void run() {

        try {           
            //Creo lo stream dal quale continuerà a ricevere il server
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            //Leggo il nome del Client
            inString = in.readLine();
            ClientName = inString.split(":")[1];

            //Registro il client nelle connessioni
            connessioni.addClient(ClientName, connectionSocket);
            //Comunico agli altri client che questo client si è registrato
            connessioni.BroadcastMsg(ClientName, ClientName + " è arrivato");

            //Loop
            while (true) {
                //Leggi da buffer della connessioni
                inString = in.readLine();
                //Se arriva il messaggio END
                Strings = inString.split(";");

                if(Strings.length > 1){
                    destString = Strings[0];
                    msgString = Strings[1];

                    if (destString.equals("END")){
                        //Comunica che il client si è disconnesso, rimuovilo dalle connessioni ed esci dal Loop
                        connessioni.BroadcastMsg(ClientName, ClientName+ " si è disconnesso");
                        connessioni.removeConnectionByID(ClientName);
                        break;
                    }else if (destString.equals("0")) {
                        //Inoltra in broadcast a tutti il messaggio
                        connessioni.BroadcastMsg(ClientName, ClientName+": "+msgString);
                    }else{
                        //Invia messaggio solo al destinatario
                        connessioni.SendMsgTo(ClientName, destString, msgString);
                    }
                    
                    //Print sulla console il messaggio
                    System.out.println("Messaggio da " + ClientName + ": [" + inString + "]");

                }
                else{
                    connessioni.SendMsgTo(ClientName, ClientName, "Formato Messaggio non valido");
                }
                                
               
            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println(ClientName + " ha interrotto forzatamente la comunicazione");
        } finally {
            System.out.println(ClientName + " si è disconnesso");
            try {
                connectionSocket.close();
            } catch (IOException e) {e.printStackTrace();}
        }
        }
}