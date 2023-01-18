import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ConnectionManager {
    private static ConnectionManager instance = null;

    private ConnectionManager() {
        //SET LOGGING UTILS
        
        /*
        logger = Logger.getLogger("LogConnections");
        try {
            fileHandler = new FileHandler("Blackjack/ServerTCP/log/LogConnections.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.info("Connection Manager avviato");
        } catch (Exception e) {
            e.printStackTrace();
        }
          */
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    // VARIABILI
    Logger logger;
    FileHandler fileHandler;
    
    //Tavoli
    private List<JTavolo> ListaTavoli = new ArrayList<>();

    // Giocatori che non hanno ancora scelto il loro tavolo
    private List<ThreadConnection> Clients = new ArrayList<ThreadConnection>();

    public boolean AddClient(JConnect connClient) {
        ThreadConnection tempTC = new ThreadConnection(connClient);
        boolean b = Clients.add(tempTC);
        tempTC.start();
        //logger.info("Client ["+ connClient.getId()+"] aggiunto alla lista");
        return b;
    }

    public void RemoveClient(ThreadConnection tc){
        Clients.remove(tc);
        //logger.info("Client ["+ tc.connessioneClient.getId()+"] rimosso dalla lista");
    }

    public boolean AddTavolo(String nomeTavolo){
        boolean b = true;
        
        if (getTableNameList() != null) {
            if (!getTableNameList().contains(nomeTavolo)) {
                JTavolo newTavolo = new JTavolo(nomeTavolo);
                ListaTavoli.add(newTavolo);
                newTavolo.start();
                //logger.info("Aggiunto il tavolo [" + nomeTavolo + "]");
            }
            else{
                b = false;
                //logger.warning("Impossibile creare un altro tavolo con nome [" + nomeTavolo + "] poichè già presente");
            }
        }
        else{//La lista è vota
            ListaTavoli.add(new JTavolo(nomeTavolo));
            //logger.info("Aggiunto il tavolo [" + nomeTavolo + "]");
        }
        

        return b;
    }

    public String getInfoTavoli() {
        String risposta = "StatoTavolo;";
        for (JTavolo tavolo : ListaTavoli) {
            risposta += tavolo.GetInfoTavolo() + ";";
        }
        return risposta;
    }

    public String getInfoTavoliCmp() {
        String risposta = "";
        for (JTavolo tavolo : ListaTavoli) {
            risposta += tavolo.GetInfoTavoloCmp() + "\n";
        }
        return risposta;
    }

    public List<String> getTableNameList() {
        List<String> tableNameList = new ArrayList<String>();

        for (JTavolo tavolo : ListaTavoli) {
            tableNameList.add(tavolo.nomeTavolo);
        }

        return tableNameList;
    }

    public boolean MoveClientTo(String nomeTavolo, ThreadConnection tc) {
        //logger.info("Spostando il client [" + tc.connessioneClient.id + "] nel tavolo " + nomeTavolo);
        boolean b = false;
        
        if(getTavoloByName(nomeTavolo).AddGiocatore(tc.connessioneClient)){
            RemoveClient(tc);
            b = true;
        }
        
        return b;
    }

    private JTavolo getTavoloByName(String nomeTavolo){
        JTavolo t = null;

        for (JTavolo tavolo : ListaTavoli) {
            if(tavolo.nomeTavolo.equals(nomeTavolo)){
                t = tavolo;
                break;
            }
        }

        return t;
    }

    public void ClientEndConnection(ThreadConnection tc) {
        Clients.remove(tc);
        try {
            tc.connessioneClient.socket.close();
            System.out.println("Chiusura client " + tc.connessioneClient.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //logger.info("Client ["+ tc.connessioneClient.getId()+"] ha chiuso la connessione");
    }
    
}