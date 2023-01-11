import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ThreadConnection extends Thread {
    JConnect connessioneClient;
    JTavolo tavolo;
    boolean isActive = true;

    final String HELP_STRING = "I metodi disponibili sono:" +
            "\n - 'getInfoTavoli' -> Ritorna le informazione relative al numero di giocatori presenti ai tavoli" +
            "\n - 'getInfoTavoliCmp' -> Ritorna le informazione relative al numero di giocatori presenti ai tavoli" +
            "\n - 'getTableNameList' -> Ritorna le informazione relative ai nomi dei tavoli" +
            "\n - 'connectToTable', Args[0] = 'nometavolo' -> Prova a connetterti al tavolo desiderato" +
            "\n - 'quit' -> Disconnetti il client";

    public ThreadConnection(JConnect connessioneClient) {
        this.connessioneClient = connessioneClient;
    }

    @Override
    public void run() {

        BufferedReader in = connessioneClient.in;
        PrintWriter out = connessioneClient.out;

        String request;
        String[] parts;
        String command; // command
        List<String> arguments = new ArrayList<String>();
        String name = "";

        if (connessioneClient.id.equals("")) {
            do {
                out.println("getName");
                try {
                    name = in.readLine();
                } catch (IOException e) {
                    name = "error";
                    isActive = false;
                    ConnectionManager.getInstance().RemoveClient(this);
                }
            } while (name.equals(""));
            connessioneClient.id = name;
        }

        out.println(HELP_STRING);

        while (isActive) {
            try {
                if (in.ready()) {
                    request = in.readLine();
                    System.out.println(request);
                    parts = request.split("[\\[\\],]");
                    command = parts[0]; // command
                    arguments.clear();
                    ;
                    if (parts.length > 1) {
                        for (int i = 1; i < parts.length; i++)
                            arguments.add(parts[i]); // ricevi argomenti
                    }

                    switch (command) {

                        case "getInfoTavoli":
                            out.println(ConnectionManager.getInstance().getInfoTavoli());
                            break;

                        case "getInfoTavoliCmp":
                            out.println(ConnectionManager.getInstance().getInfoTavoliCmp());
                            break;

                        case "getTableNameList":
                            out.println(ConnectionManager.getInstance().getTableNameList());
                            break;

                        case "connectToTable":
                            if (arguments.size() == 1) {
                                String nomeTavolo = arguments.get(0);
                                if (ConnectionManager.getInstance().getTableNameList().contains(nomeTavolo)) {
                                    ConnectionManager.getInstance().MoveClientTo(nomeTavolo, this);
                                    isActive = false;
                                } else {
                                    out.println("Invalid Table Name");
                                }
                            } else {
                                out.println("Invalid Number of Args");
                            }

                            break;

                        case "quit":
                            ConnectionManager.getInstance().ClientEndConnection(this);
                            isActive = false;
                            break;

                        case "help":
                            out.println(HELP_STRING);
                            break;

                        default:
                            out.println("Invalid request");
                            break;

                    }
                }
            } catch (IOException e) {
                isActive = false;
                ConnectionManager.getInstance().ClientEndConnection(this);
            }
        }
    }

}
