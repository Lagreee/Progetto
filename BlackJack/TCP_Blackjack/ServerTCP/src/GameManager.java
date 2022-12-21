import java.util.ArrayList;
import java.util.List;

public class GameManager {
    Connessioni connessioni;
    List<JGiocatore> ListaGiocatori = new ArrayList<JGiocatore>();
    List<String> Banco = new ArrayList<String>();
    JMazzo mazzo = new JMazzo(); 

    GameManager(Connessioni connessioni){
        this.connessioni = connessioni;
        for (JConnect jConnect : connessioni.ListaConnessioni) {
            ListaGiocatori.add(new JGiocatore(jConnect, 100, ListaGiocatori.size() + 1));
        }

    }

    void startGame(){
        for (int i = 0; i < ListaGiocatori.size(); i++) {
            String cartaPescata = mazzo.PescaCarta();
            ListaGiocatori.get(i).AddCarta(cartaPescata);
            connessioni.BroadcastMsg("", i+"/aggiungiCarta/"+cartaPescata );
        }
    }

}
