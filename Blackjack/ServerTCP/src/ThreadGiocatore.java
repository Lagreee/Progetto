import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ThreadGiocatore extends Thread {
    JConnect connessioneClient;
    JGiocatore giocatore;
    JTavolo tavolo;
    boolean isActive = true; // Thread is active
    boolean isReady = false; // Player is ready to play
    boolean inGame = false; // Player is in game

    final String HELP_STRING = "I metodi disponibili sono:" +
            "\n - 'getNomiGiocatoriNelTavolo' -> Ritorna le informazione relative ai nomi dei giocatori e il loro posto nel tavolo"
            +
            "\n - 'setReady', Args[0] = '0/1' -> Imposta se il giocatore è pronto a giocare o no" +
            "\n - 'quit' -> Disconnetti il client";

    public ThreadGiocatore(JGiocatore giocatore, JTavolo tavolo) {
        this.giocatore = giocatore;
        this.tavolo = tavolo;
    }

    @Override
    public void run() {
        BufferedReader in = giocatore.connessioneClient.in;
        PrintWriter out = giocatore.connessioneClient.out;

        String request;
        String[] parts;
        String command; // command
        List<String> arguments = new ArrayList<String>();

        out.println("Connected;" + tavolo.nomeTavolo);

        while (isActive) {
            try {
                
                if (giocatore.disconnected) {
                    tavolo.Disconnect(this);
                    isActive = false;                    
                    System.out.println(giocatore.getName() + " si è disconnesso");
                    break;
                }

                while (!inGame && in.ready()) { // If player is in game don't interact with it

                    request = in.readLine();
                    parts = request.split("[\\[\\],]");
                    command = parts[0]; // command
                    arguments.clear();
                    
                    if (parts.length > 1) {
                        for (int i = 1; i < parts.length; i++)
                            arguments.add(parts[i]); // ricevi argomenti
                    }

                    switch (command) {
                        case "getNomiGiocatoriNelTavolo":
                            out.println(tavolo.getStringNomiGiocatori());
                            break;

                        case "setReady":
                            if (arguments.size() == 1) {
                                if (arguments.get(0).equals("0"))
                                    setReady(false);
                                else if (arguments.get(0).equals("1"))
                                    setReady(true);
                                else
                                    out.println("Invalid arg");
                            } else
                                out.println("Invalid Number of Args");
                            break;

                        case "help":
                            out.println(HELP_STRING);
                            break;

                        case "quit":
                            tavolo.RemoveGiocatore(this);
                            isActive = false;
                            break;

                        default:
                            out.println("Invalid request");
                            break;
                    }
                }
            } catch (IOException e) {
                isActive = false;
            }
        }
    }

    public void setInGame(boolean b) {
        inGame = b;
    }

    public void setReady(boolean b) {
        isReady = b;
    }

    public void SendMsg(String msg) {
        giocatore.println(msg);
    }

    public void setActive(boolean b) {
        isActive = b;
    }

}
