import java.util.ArrayList;
import java.util.List;

public class Game {
    JTavolo tavolo;
    List<JGiocatore> ListaGiocatori = new ArrayList<JGiocatore>();
    JDealer dealer = new JDealer();
    JMazzo deck = new JMazzo();

    String GameLog = "";

    Boolean gameInProgress;
    final int TIMER_WAIT_GAME_SECONDS = 15;

    Game(JTavolo tavolo, List<ThreadGiocatore> ListaTGiocatori) {
        this.tavolo = tavolo;

        for (ThreadGiocatore threadGiocatore : ListaTGiocatori) {
            ListaGiocatori.add(threadGiocatore.giocatore);
        }
    }

    void startGame() {
        System.out.println("Game Started!");
        gameInProgress = true;

        // Deal the first card to players
        for (JGiocatore player : ListaGiocatori) {
            String carta = deck.PescaCarta();
            player.AddCarta(carta);

            tavolo.BroadcastMsg("add;" + player.getName() + ";" + player.getPosizione() + ";" + carta);
            GameLog += "add;" + player.getName() + ";" + player.getPosizione() + ";" + carta;
        }
        
        //Il dealer pesca una carta
        String c = deck.PescaCarta();
        dealer.AddCarta(c);
        tavolo.BroadcastMsg("add;dealer;8;" + c);

        //Deal the second card to players
        for (JGiocatore player : ListaGiocatori) {
            String carta = deck.PescaCarta();
            player.AddCarta(carta);

            tavolo.BroadcastMsg("add;" + player.getName() + ";" + player.getPosizione() + ";" + carta);
            GameLog += "add;" + player.getName() + ";" + player.getPosizione() + ";" + carta;
        }

        //Il dealer pesca una carta
        String cartaCoperta = deck.PescaCarta();
        dealer.AddCarta(cartaCoperta);
        tavolo.BroadcastMsg("hiddenAdd;dealer;8;");

        // Allow players to take their turn
        for (JGiocatore player : ListaGiocatori) {
            while (player.wantsToHit()) {

                String carta = deck.PescaCarta();
                player.AddCarta(carta);
                tavolo.BroadcastMsg("add;" + player.getName() + ";" + player.getPosizione() + ";" + carta);
                GameLog += "add;" + player.getName() + ";" + player.getPosizione() + ";" + carta;

                if (player.isBust()) {
                    tavolo.BroadcastMsg("bust;" + player.getName() + player.getPosizione() + ";");
                    GameLog += "bust;" + player.getName() + player.getPosizione() + ";" + "\n";
                    // Player has gone over 21 and loses
                    break;
                }
            }
        }

        tavolo.BroadcastMsg("show;dealer;8;" + cartaCoperta);

        // Dealer takes their turn
        while (dealer.wantsToHit()) {
            String carta = deck.PescaCarta();
            dealer.AddCarta(carta);
            tavolo.BroadcastMsg("add;dealer;8;" + carta);
            if (dealer.isBust()) {
                // Dealer has gone over 21 and loses
                tavolo.BroadcastMsg("bust;dealer;8");
                GameLog += "bust;dealer;8\n";
                break;
            }
        }

        // Determine the winners -> Da introdurre il pareggio
        List<JGiocatore> winners = determineWinners();
        // Display the result of the game
        displayResults(winners);

        for (JGiocatore jGiocatore : ListaGiocatori) {
            jGiocatore.ClearMano();
        }

    }

    private void displayResults(List<JGiocatore> winners) {
        for (JGiocatore player : ListaGiocatori) {
            if (winners.contains(player)) {
                tavolo.BroadcastMsg("winner;" + player.getName() + ";" + player.getPosizione() + ";");
            } else {
                tavolo.BroadcastMsg("lose;" + player.getName() + ";" + player.getPosizione() + ";");
            }

        }
    }

    private List<JGiocatore> determineWinners() {
        List<JGiocatore> ListaVincitori = new ArrayList<>();

        for (JGiocatore jGiocatore : ListaGiocatori) {
            if (!jGiocatore.isBust()) {
                if (dealer.isBust())
                    ListaVincitori.add(jGiocatore);
                else if (jGiocatore.PuntiInMano() >= dealer.PuntiInMano())
                    ListaVincitori.add(jGiocatore);

            }
        }
        return ListaVincitori;
    }

}
