import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ThreadGiocatore extends Thread {
    JGiocatore giocatore;
    JTavolo tavolo;
    boolean isActive = true;

    public ThreadGiocatore(JGiocatore giocatore, JTavolo tavolo){
        this.giocatore = giocatore;
        this.tavolo = tavolo;
    }

    @Override
    public void run() {
        BufferedReader in = giocatore.connessioneClient.in;
        PrintWriter out = giocatore.connessioneClient.out;
        
        String request;
        String[] parts;
        String command;  // command
        List<String> arguments = new ArrayList<String>();
        
        out.println("Benvenuto al Tavolo: " + tavolo.nomeTavolo);
        

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
                    case "getNomiGiocatoriNelTavolo":
                        out.println(tavolo.getNomiGiocatori());
                        break;


                    case "quit":
                        tavolo.RemoveGiocatore(this);
                        isActive = false;
                        break;
                
                    default:
                        out.println("Invalid request");
                        break;
                }

            } catch (IOException e) {
                isActive = false;
            }
        }
    }
}
