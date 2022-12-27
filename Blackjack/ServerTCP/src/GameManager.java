import java.util.ArrayList;
import java.util.List;

public class GameManager {
    Old_Connessioni connessioni;
    List<JGiocatore> ListaGiocatori = new ArrayList<JGiocatore>();
    JDealer dealer = new JDealer();
    JMazzo deck = new JMazzo();

    Boolean gameInProgress;
    final int TIMER_WAIT_GAME_SECONDS = 15;

    GameManager(Old_Connessioni connessioni) {
        this.connessioni = connessioni;
    }

    void startGame() {
        //Registra Giocatori connessi al gioco
        for (int i = this.TIMER_WAIT_GAME_SECONDS; i > 1; i--) {
            System.out.println(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game Started!");

        for (JConnect jConnect : connessioni.ListaConnessioni) {
            ListaGiocatori.add(new JGiocatore(jConnect));
            System.out.println("Player Adedd!");
        }

        gameInProgress = true;

        while (gameInProgress) {
            // Deal cards to players
            for (int i = 0; i < 2; i++) {
                for (JGiocatore player : ListaGiocatori) {
                    String carta = deck.PescaCarta();
                    player.AddCarta(carta);
                    connessioni.BroadcastMsg("Server", "add;" + player.getName() + ";" + carta);
                }
            }
            
            

            // Allow players to take their turn
            for (JGiocatore player : ListaGiocatori) {
                while (player.wantsToHit()) {
                    
                    String carta = deck.PescaCarta();
                    player.AddCarta(carta);
                    connessioni.BroadcastMsg("Server", "add;" + player.getName() + ";" + carta);
                    
                    if (player.isBust()) {
                        connessioni.BroadcastMsg("Server", "bust;" + player.getName() );
                        // Player has gone over 21 and loses
                        break;
                    }
                }
            }

            // Dealer takes their turn
            while (dealer.wantsToHit()) {
                String carta = deck.PescaCarta();
                dealer.AddCarta(carta);
                connessioni.BroadcastMsg("Server", "add;dealer;" + carta);
                if (dealer.isBust()) {
                    // Dealer has gone over 21 and loses
                    connessioni.BroadcastMsg("Server", "bust;dealer");
                    break;
                }
            }
            
            // Determine the winners -> Da introdurre il pareggio
            System.out.print("Calcolo Vincitori:");
            List<JGiocatore> winners = determineWinners();
            
            // Display the result of the game
            displayResults(winners);

            System.out.println("Gioco Finito");
            gameInProgress = false;
        }

    }

    private void displayResults(List<JGiocatore> winners) {
        for (JGiocatore jGiocatore : ListaGiocatori) {
            if (winners.contains(jGiocatore)) {
                connessioni.BroadcastMsg("Server", "winner;" + jGiocatore.getName());       
            }else{
                connessioni.BroadcastMsg("Server", "lose;" + jGiocatore.getName()); 
            }
        
        }
    }

    private List<JGiocatore> determineWinners() {
        List<JGiocatore> ListaVincitori = new ArrayList<>();

        for (JGiocatore jGiocatore : ListaGiocatori) {
            if(!jGiocatore.isBust()){
                if (dealer.isBust())
                    ListaVincitori.add(jGiocatore);
                else if (jGiocatore.PuntiInMano() >= dealer.PuntiInMano()) 
                    ListaVincitori.add(jGiocatore);
                
            }
        }
        return ListaVincitori;
    }

}
