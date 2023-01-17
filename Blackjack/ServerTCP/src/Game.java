import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


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

    void startGame(){
        gameInProgress = true;
        tavolo.BroadcastMsg("start;");
        Aspetta(2);
        // Deal the first card to players
        for (JGiocatore player : ListaGiocatori) {
            String carta = deck.PescaCarta();
            player.AddCarta(carta);

            tavolo.BroadcastMsg("add;" + player.getName() + ";" + player.getPosizione() + ";" + CardToGame(carta)+ ";");
            GameLog += "add;" + player.getName() + ";" + player.getPosizione() + ";" + carta;

            Aspetta(1);
            
        }
        
        //Il dealer pesca una carta
        String c = deck.PescaCarta();
        dealer.AddCarta(c);
        tavolo.BroadcastMsg("add;dealer;dealer;" + CardToGame(c) + ";");
        Aspetta(1);

        //Deal the second card to players
        for (JGiocatore player : ListaGiocatori) {
            String carta = deck.PescaCarta();
            player.AddCarta(carta);

            tavolo.BroadcastMsg("add;" + player.getName() + ";" + player.getPosizione() + ";" + CardToGame(carta) + ";");
            GameLog += "add;" + player.getName() + ";" + player.getPosizione() + ";" + carta;
        
            Aspetta(1);
        }

        //Il dealer pesca una carta
        String cartaCoperta = deck.PescaCarta();
        dealer.AddCarta(cartaCoperta);
        tavolo.BroadcastMsg("hiddenAdd;dealer;8;");
        Aspetta(1);

        // Allow players to take their turn
        for (JGiocatore player : ListaGiocatori) {
            while (player.wantsToHit()) {

                String carta = deck.PescaCarta();
                player.AddCarta(carta);
                tavolo.BroadcastMsg("add;" + player.getName() + ";" + player.getPosizione() + ";" + CardToGame(carta) + ";");
                GameLog += "add;" + player.getName() + ";" + player.getPosizione() + ";" + carta;

                if (player.isBust()) {
                    tavolo.BroadcastMsg("bust;" + player.getName() + player.getPosizione() + ";");
                    GameLog += "bust;" + player.getName() + player.getPosizione() + ";" + "\n";
                    // Player has gone over 21 and loses
                    break;
                }
            }
        }

        tavolo.BroadcastMsg("add;dealer;dealer;" + CardToGame(cartaCoperta) + ";");
        Aspetta(1);

        // Dealer takes their turn
        while (dealer.wantsToHit()) {
            String carta = deck.PescaCarta();
            dealer.AddCarta(carta);
            tavolo.BroadcastMsg("add;dealer;dealer;" + CardToGame(carta) + ";");
            if (dealer.isBust()) {
                // Dealer has gone over 21 and loses
                tavolo.BroadcastMsg("bust;dealer;8");
                GameLog += "bust;dealer;dealer\n";
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

        Aspetta(10);
        tavolo.BroadcastMsg("end;");
    }

    private void Aspetta(int secondi) {
        try {
            TimeUnit.SECONDS.sleep(secondi);
        } catch (InterruptedException e) {
            e.printStackTrace();
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


    String CardToGame(String carta){
        String[] cartaSeparata =  carta.split("-");
        String numero = cartaSeparata[0];
        String segno = cartaSeparata[1];
        if (numero.length() < 2) {
            numero = "0" + numero; 
        }
        return segno+numero;
    }
}
