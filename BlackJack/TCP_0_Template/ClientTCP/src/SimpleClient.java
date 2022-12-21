import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class SimpleClient {

    public static String ClientName = "Client";

    public static void main(String[] args) throws IOException {
        Random r = new Random();
        Scanner scan = new Scanner(System.in);
        Flags flags = new Flags();
        ClientName += r.nextInt(0,1024);

        //Crea una socket con sulla porta 8080 (LocalHost)
        InetAddress addr = InetAddress.getByName(null);
        System.out.println("addr = " + addr);
        
        //Prova a instaurare una connessione TCP
        Socket socket = new Socket(addr, 8080);
        
        //Presentati:
        String nameString = "";
        while(nameString == "" || nameString == "0" | nameString == "END"){
            System.out.println("Nome [Non può essere vuoto, 0 o END]:");
            nameString = scan.nextLine();
        }
    
        try {
            
            //Print Informazioni Connessione
            System.out.println("socket = " + socket);

            //Creo i canali di comunicazione con il server
            BufferedReader TCP_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ThreadLettura tLettura = new ThreadLettura(TCP_in, flags);
            tLettura.start();
            
            PrintWriter TCP_Out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            

            TCP_Out.println("Name:" + nameString);
            
            System.out.println("Sintassi Messaggio: [Destinatario(0->Broadcast, END->Disconnettiti)];[Messaggio(non vuoto)]");
            
            //Continua a inviare messaggi fino a quando il messaggio non è "END"
            Boolean valid = false;
            String[] Strings = new String[0];
            String outString = "";
            String destString = "";

            while (!destString.equals("END") && flags.getFlagEnd() == false) {
                while (!valid) {
                    outString = scan.nextLine();
                    Strings = outString.split(";");
                    valid = true;

                    if (Strings.length != 2) {
                        valid = false;
                    }else{
                        if (Strings[1].equals(""))
                            valid = false;
                    }
                    

                    if(!valid){
                        System.out.println("Formato messaggio errato.[Destinatario(0->Broadcast, END->Disconnettiti)];[Messaggio(non vuoto)]");
                    }
                }

                destString = Strings[0];
                outString = Strings[1];

                if(destString.equals("END")){
                    TCP_Out.println(destString + ";0");
                    flags.setFlagEnd(true);
                }
                else{
                    //Invia il messaggio
                    System.out.println("Inviato!");
                    TCP_Out.println(destString + ";" +outString);
                }
                
                valid = false;            
            }
        } finally {
            
            flags.setFlagEnd(true);
            System.out.println("closing...");
            socket.close();
        }
        scan.close();
    }
}