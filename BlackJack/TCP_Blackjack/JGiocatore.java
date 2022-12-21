import java.io.*;
import java.net.*;

public class JGiocatore {
    String nome;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    int posto; //per sapere che client è "seduto" dove
    float fish; //quanti soldi ha

    public JGiocatore(String nome, Socket socket, float fish){
        this.nome = nome;
        this.socket = socket;
        this.fish = fish;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendMsg(String msg){
        out.println(msg);
    }

    public String getNome() {
        return nome;
    }



}
