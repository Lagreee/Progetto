import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JGiocatore {  
    JConnect connessioneClient;
    List<String> Mano = new ArrayList<String>();
    int posizione = -1;
    boolean disconnected = false;

    public JGiocatore(JConnect connessioneClient, int posizione){
        this.connessioneClient = connessioneClient;
        this.posizione = posizione;
    }

    public void ClearMano() {
        Mano.clear();
    }

    public void println(String messaggio){
        connessioneClient.out.println(messaggio);
    }

    public String AttendiRispota(){
        String messaggio = "Messaggio non ricevuto";

        try {
            messaggio = connessioneClient.in.readLine();
        } catch (IOException e) {
            //e.printStackTrace();
            messaggio = "stay";
            disconnected = true;
        }

        return messaggio;
    }

    void AddCarta(String Carta){
        Mano.add(Carta);
    }

    int PuntiInMano() {
        int punti = 0;
        for (String carta : Mano) {
            int cardValue = Integer.parseInt(carta.split("-")[0]);
            if (cardValue >= 10)
                punti += 10;
            else
                punti += cardValue;
        }
        return punti;
    }

    public boolean wantsToHit() {
        boolean wantsToHit = false;
        String risposta = "null";
        
        connessioneClient.SendMsg("requestMove;");
        
        risposta = AttendiRispota();

        if(risposta.equals("hit")){
            wantsToHit = true;
        }
        else if (risposta.equalsIgnoreCase("stop")) {
            wantsToHit = false;
        }
        else{
            wantsToHit = false;
        }

        return wantsToHit;
    }

    public boolean isBust() {
        boolean isBust = false;
        int valoreMano = PuntiInMano();

        if (valoreMano > 21)
            isBust = true;

        return isBust;
    }

    public String getName() {
        return connessioneClient.id;
    }

    public int getPosizione() {
        return posizione;
    }

    public boolean Blackjack() {
        boolean b = false;

        if (PuntiInMano() == 21) {
            b = true;
        }

        return b;
    }
}
