import java.util.ArrayList;
import java.util.List;

public class JDealer {
    List<String> Mano = new ArrayList<String>();

    public JDealer() {
    }

    void AddCarta(String Carta) {
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

        if (PuntiInMano() <= 16)
            wantsToHit = true;

        return wantsToHit;
    }

    public boolean isBust() {
        boolean isBust = false;

        if (PuntiInMano() > 21)
            isBust = true;

        return isBust;
    }
}
