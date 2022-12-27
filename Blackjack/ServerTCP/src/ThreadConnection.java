import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ThreadConnection extends Thread {
    JConnect connessioneClient;
    JTavolo tavolo;
    boolean isActive = true;

    public ThreadConnection(JConnect connessioneClient) {
        this.connessioneClient = connessioneClient;
    }

    @Override
    public void run() {

        BufferedReader in = connessioneClient.in;
        PrintWriter out = connessioneClient.out;
        
        String request;
        String[] parts;
        String command;  // command
        List<String> arguments = new ArrayList<String>();

        out.println("I metodi disponibili sono:"+
                                    "\n - 'getInfoTavoli' -> Ritorna le informazione relative al numero di giocatori presenti ai tavoli"+
                                    "\n - 'connectToTable', Args[0] = 'nometavolo' -> Prova a connetterti al tavolo desiderato"+
                                    "\n - 'quit' -> Disconnetti il client");

        while (isActive) {            
            try {
                request = in.readLine();
                parts = request.split("[\\[\\],]");
                command = parts[0];  // command
                arguments.clear();; 
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++)
                        arguments.add(parts[i]); //ricevi argomenti
                }

                switch (command) {
                    
                    case "getInfoTavoli":
                        out.println(ConnectionManager.getInstance().getInfoTavoli());
                        break;
                        
                    case "getTableNameList":
                        out.println(ConnectionManager.getInstance().getTableNameList());
                        break;
                        
                    case "connectToTable":
                        if (arguments.size() == 1) {
                            String nomeTavolo = arguments.get(0);
                            if (ConnectionManager.getInstance().getTableNameList().contains(nomeTavolo)) {
                                ConnectionManager.getInstance().MoveClientTo(nomeTavolo, this);
                                isActive = false;
                            }else{out.println("Invalid Table Name");}
                        }
                        else{out.println("Invalid Number of Args");}

                        break;

                    case "quit":
                        ConnectionManager.getInstance().ClientEndConnection(this);
                        isActive = false;
                        break;
                
                    case "help":
                        out.println("I metodi disponibili sono:"+
                                    "\n - 'getInfoTavoli' -> Ritorna le informazione relative al numero di giocatori presenti ai tavoli"+
                                    "\n - 'connectToTable', Args[0] = 'nometavolo' -> Prova a connetterti al tavolo desiderato"+
                                    "\n - 'quit' -> Disconnetti il client");
                        break;
                    
                    default:
                        out.println("Invalid request");
                        break;
                        
                }
            } catch (IOException e) {
                isActive = false;
                ConnectionManager.getInstance().ClientEndConnection(this);
            } 
        }
    }
}
