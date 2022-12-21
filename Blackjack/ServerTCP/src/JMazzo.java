import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JMazzo {
    List<String> Mazzo = new ArrayList<String>();

    JMazzo(){
        CreaMazzo();
        shuffle();
    }

    public void shuffle(){
        Collections.shuffle(Mazzo);
    }

    public String PescaCarta(){

        if(Mazzo.size()<1)
            CreaMazzo();
         
        String carta = Mazzo.get(0);
        Mazzo.remove(0);

        return carta;
    }

    private void CreaMazzo(){
        Mazzo.clear();
        for (int j = 0; j < 2; j++) {
            for (int i = 1; i <= 13; i++) {
                Mazzo.add(i+";Cuori");
                Mazzo.add(i+";Quadri");
                Mazzo.add(i+";Fiori");
                Mazzo.add(i+";Picche");
            }
        }
        shuffle();
    }
}
