import java.io.*;
import java.net.*;

public class JConnect {
    String id;
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    InputStream RawIn;
    OutputStream RawOut;

    //ObjectInputStream objIn;
    //ObjectOutputStream objOut;

    float fish = 0;

    public JConnect(String id, Socket socket){
        this.id = id;
        this.socket = socket;
        try {
            RawIn = socket.getInputStream();
            RawOut = socket.getOutputStream();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

            /*
            objIn = new ObjectInputStream(RawIn);
            objOut = new ObjectOutputStream(RawOut);
            */

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
