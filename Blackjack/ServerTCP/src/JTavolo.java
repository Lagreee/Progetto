import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JTavolo extends Thread {
    final int MAX_GIOCATORI = 7;
    String nomeTavolo;
    boolean isInGame = false;

    String NomeFileLog;
    Logger logger;
    FileHandler fileHandler;  

    private ThreadGiocatore[] GiocatoriAlTavolo = new ThreadGiocatore[7];
    int numGiocatoriSeduti = 0;

    public JTavolo(String nome) {
        nomeTavolo = nome;

        NomeFileLog = "LogTavolo"+nomeTavolo;
        logger = Logger.getLogger("NomeFileLog");
        try {
            fileHandler = new FileHandler("Blackjack/ServerTCP/log/"+NomeFileLog+".log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();  
            fileHandler.setFormatter(formatter);   
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }  
    }

    public String GetInfoTavolo(){
        String risposta = nomeTavolo + " (" + numGiocatoriSeduti +"/7) - In game: " + isInGame;
        return risposta;
    }

    public String GetInfoTavoloCmp(){
        String risposta = nomeTavolo + " (" + numGiocatoriSeduti +"/7) - In game: " + isInGame;
        for (String giocatore : getNomiGiocatori()) {
            risposta += "\n\t " + giocatore;
        }
        return risposta;
    }

    public synchronized boolean AddGiocatore(JConnect connGiocatore){
        boolean b = false;
        
        if(numGiocatoriSeduti < 7){
            int pos = getPrimaPosLibera();

            JGiocatore g = new JGiocatore(connGiocatore);
            ThreadGiocatore tg = new ThreadGiocatore(g, this);
            GiocatoriAlTavolo[pos] = tg;
            tg.start();

            numGiocatoriSeduti++;

            logger.info("Giocatore ["+ connGiocatore.getId()+"] aggiunto al tavolo in posizione ("+ pos +")");
            b = true;
        }

        return b;
    }

    private int getPrimaPosLibera() {
        int n = 0;
        while(n < 7 ){
            if (GiocatoriAlTavolo[n] == null) {
                break;
            }
            n++;
        }
        return n;
    }

    public synchronized void RemoveGiocatore(ThreadGiocatore tg) {
        int pos = GetPosNelTavolo(tg);
        GiocatoriAlTavolo[pos] = null;
        numGiocatoriSeduti--;
        logger.info("Giocatore ["+ tg.getName() +"] rimosso dal tavolo in posizione ("+ pos +")");
        ConnectionManager.getInstance().AddClient(tg.giocatore.connessioneClient);
    }

    private int GetPosNelTavolo(ThreadGiocatore tg) {
        int n = 0;
        while(n < 7 ){
            if (GiocatoriAlTavolo[n] == tg) {
                break;
            }
            n++;
        }
        return n;
    }

    public List<String> getNomiGiocatori() {
        List<String> listaDiRisposta = new ArrayList<String>();
        for (int i = 0; i < GiocatoriAlTavolo.length; i++) {
            if(GiocatoriAlTavolo[i] != null)
            listaDiRisposta.add(GiocatoriAlTavolo[i].giocatore.connessioneClient.id + "(Posto " + (i+1) + ")");
        }
        return listaDiRisposta;
    }
}
