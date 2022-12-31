import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JTavolo extends Thread {
    final int TEMPO_TIMER = 60;
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

    @Override
    public void run() {
        //Loop di gioco
        while (true) { 
            boolean allPlayersReady = false;
            int timer = TEMPO_TIMER;

            //Aspetta 1m o che tutti i giocatori siano pronti
            while (!allPlayersReady && timer > -1) {
                
                // Se ci non ci sono giocatori salta il controllo
                if (numGiocatoriSeduti < 1) {
                    allPlayersReady = false;
                } else {
                    // Controlla se tutti i giocatori sono pronti
                    allPlayersReady = true;
                    for (ThreadGiocatore giocatore : GiocatoriAlTavolo) {
                        if (giocatore != null) {
                            if (!giocatore.isReady) {
                                allPlayersReady = false;
                                break;
                            }
                        }
                    }
                }
                BroadcastMsg(GiocatoriAlTavolo, "The game will start in " + timer-- + "s." );
                try {
                    sleep(1000); // sleep for 1 second
                } catch (InterruptedException e) {}
            }
            // è scaduto il timer o Tutti i player sono pronti
            logger.info("Starting the game...");
            if (numGiocatoriSeduti > 0) { //Se ci sono effettivamente giocatori
                
                //IL GIOCO PARTE CON TUTTI I GIOCATORI PRESENTI

                //Setta le variabili di stato InGame = true sia del tavolo che dei ThreadGiocatori
                isInGame = true;
                List<ThreadGiocatore> GiocatoriAttivi = new ArrayList<ThreadGiocatore>(); //Determina i Giocatori Attivi
                for (ThreadGiocatore TGiocatore : GiocatoriAlTavolo) {
                    if (TGiocatore != null) {
                        TGiocatore.setInGame(true);
                        GiocatoriAttivi.add(TGiocatore);
                    }
                }
                logger.info("The game started with these players: " + getNomiGiocatori());
                BroadcastMsg(GiocatoriAlTavolo, "The game started with these players: " + getNomiGiocatori());
                
                //Start a New Game
                Game game = new Game(this, GiocatoriAttivi );
                game.startGame();

                //Setta le variabili di stato InGame = false sia del tavolo che dei ThreadGiocatori
                isInGame = false;
                for (ThreadGiocatore threadGiocatore : GiocatoriAlTavolo) {
                    if (threadGiocatore != null) {
                        threadGiocatore.setReady(false);
                        threadGiocatore.setInGame(false);
                    }
                }
                logger.info("The game is over");
                BroadcastMsg(GiocatoriAlTavolo, "The game is over");
                
                
            }else{
                logger.info("The game did't start because at the end of the timer there 0 players active");
            }
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

    void BroadcastMsg(List<ThreadGiocatore> BroadcastList, String msg){
        if (BroadcastList.size() > 0) {
            for (ThreadGiocatore TGiocatore : BroadcastList) {
                TGiocatore.SendMsg(msg);
            }
        }  
    }

    void BroadcastMsg(ThreadGiocatore[] BroadcastArray, String msg){
        for (ThreadGiocatore TGiocatore : BroadcastArray) {
            if (TGiocatore != null)
                TGiocatore.SendMsg(msg);
        }
    }

    void BroadcastMsg(String msg){
        for (ThreadGiocatore TGiocatore : GiocatoriAlTavolo) {
            if (TGiocatore != null)
                TGiocatore.SendMsg(msg);
        }
    }
}
