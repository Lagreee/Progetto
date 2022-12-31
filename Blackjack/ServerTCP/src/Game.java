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

        // Deal cards to players
        for (int i = 0; i < 2; i++) {
            for (JGiocatore player : ListaGiocatori) {
                String carta = deck.PescaCarta();
                player.AddCarta(carta);

                tavolo.BroadcastMsg("add;" + player.getName() + ";" + carta);
                GameLog += "add;" + player.getName() + ";" + carta;
            }
        }

        // Allow players to take their turn
        for (JGiocatore player : ListaGiocatori) {
            while (player.wantsToHit()) {

                String carta = deck.PescaCarta();
                player.AddCarta(carta);
                tavolo.BroadcastMsg("add;" + player.getName() + ";" + carta);
                GameLog += "add;" + player.getName() + ";" + carta;

                if (player.isBust()) {
                    tavolo.BroadcastMsg("bust;" + player.getName());
                    GameLog += "bust;" + player.getName() + "\n";
                    // Player has gone over 21 and loses
                    break;
                }
            }
        }

        // Dealer takes their turn
        while (dealer.wantsToHit()) {
            String carta = deck.PescaCarta();
            dealer.AddCarta(carta);
            tavolo.BroadcastMsg("add;dealer;" + carta);
            if (dealer.isBust()) {
                // Dealer has gone over 21 and loses
                tavolo.BroadcastMsg("bust;dealer");
                GameLog += "bust;dealer\n";
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
        for (JGiocatore jGiocatore : ListaGiocatori) {
            if (winners.contains(jGiocatore)) {
                tavolo.BroadcastMsg("winner;" + jGiocatore.getName());
            } else {
                tavolo.BroadcastMsg("lose;" + jGiocatore.getName());
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
