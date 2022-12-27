import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class BlackjackClient {

    public static String ClientName = "Client";

    public static void main(String[] args) throws IOException {
        Random r = new Random();
        Scanner scan = new Scanner(System.in);
        Flags flags = new Flags();
        ClientName += r.nextInt(0,1024);

        //Prova a instaurare una connessione TCP sulla porta 8080 (LocalHost)
        InetAddress addr = InetAddress.getByName(null);
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, 8080);
    
        try {
            //Print Informazioni Connessione
            System.out.println("socket = " + socket);

            //Creo i canali di comunicazione con il server
            BufferedReader TCP_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter TCP_Out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

            ReadingThread ThreadLettura = new ReadingThread(TCP_in, flags);
            ThreadLettura.start();
            
            //Continua a inviare messaggi fino a quando il messaggio non è "END"
            String outString = "";

            while (!outString.equals("END") && flags.getFlagEnd() == false) {
                outString = scan.nextLine();
                System.out.println("Inviato!");
                TCP_Out.println(outString);         
            }
        } finally {
            
            flags.setFlagEnd(true);
            System.out.println("closing...");
            socket.close();
        }
        scan.close();
    }
}