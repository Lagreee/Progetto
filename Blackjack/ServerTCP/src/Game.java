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
                Aspetta(1);
                
                if (player.isBust()) {
                    tavolo.BroadcastMsg("bust;" + player.getName() + ";"+  player.getPosizione() + ";");
                    GameLog += "bust;" + player.getName() + ";"+ player.getPosizione() + ";" + "\n";
                    Aspetta(1);
                    // Player has gone over 21 and loses
                    break;
                }
                
                if (player.Blackjack()) {
                    // Dealer has gone over 21 and loses
                    tavolo.BroadcastMsg("blackjack;" + player.getName() + ";"+  player.getPosizione() + ";");
                    GameLog += "blackjack;" + player.getName() + ";"+ player.getPosizione() + ";" + "\n";
                    Aspetta(1);
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
            Aspetta(1);

            if (dealer.isBust()) {
                // Dealer has gone over 21 and loses
                tavolo.BroadcastMsg("bust;dealer;dealer;");
                GameLog += "bust;dealer;dealer\n";
                Aspetta(1);
                break;
            }

            if (dealer.Blackjack()) {
                // Dealer has gone over 21 and loses
                tavolo.BroadcastMsg("blackjack;dealer;dealer;");
                GameLog += "blackjack;dealer;dealer;\n";
                Aspetta(1);
                break;
            }
        }

        // Determine the winners -> Da introdurre il pareggio
        //List<JGiocatore> winners = determineWinners();
        // Display the result of the game
        sendResults();

        for (JGiocatore jGiocatore : ListaGiocatori) {
            jGiocatore.ClearMano();
        }

        tavolo.BroadcastMsg("GameIsOver");
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

    private void sendResults() {

        String results = "results;";
        for (int i = 0; i < tavolo.GiocatoriAlTavolo.length; i++) {
            if (tavolo.GiocatoriAlTavolo[i] != null) {
                JGiocatore player = tavolo.GiocatoriAlTavolo[i].giocatore;
                if (!player.isBust()) {
                    if (dealer.isBust())
                        results += "win;";
                    else if(player.PuntiInMano() > dealer.PuntiInMano())
                        results += "win;";
                    else if (player.PuntiInMano() == dealer.PuntiInMano())
                        results += "draw;";
                    else
                        results += "lose;";
                }
                else
                    results += "lose;";
            }
            else
                results += ";";
        }
        tavolo.BroadcastMsg(results);
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
