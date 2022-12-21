import java.io.*;
import java.net.*;

public class JConnect {
    String id;
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public JConnect(String id, Socket socket){
        this.id = id;
        this.socket = socket;
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

    public String getId() {
        return id;
    }

}
