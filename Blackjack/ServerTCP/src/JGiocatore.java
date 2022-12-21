import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JGiocatore {  
    JConnect connessioneClient;
    int posto; //per sapere che client è "seduto" dove
    float fish; //quanti soldi ha

    List<String> Mano = new ArrayList<String>();

    public JGiocatore(JConnect connessioneClient, float fish, int posto){
        this.connessioneClient = connessioneClient;
        this.fish = fish;
        this.posto = posto;
    }

    public void println(String messaggio){
        connessioneClient.out.println(messaggio);
    }

    public String AttendiRispota(){
        String messaggio = "Messaggio non ricevuto";

        try {
            messaggio = connessioneClient.in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messaggio;
    }

    void AddCarta(String Carta){
        Mano.add(Carta);
    }

    int PuntiMano(){
        int punti = 0;
        for (String carta : Mano) {
            punti += Integer.parseInt(carta.split(";")[0]);
        }
        return punti;
    }
}
