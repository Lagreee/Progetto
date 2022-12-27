import java.io.*;

public class ReadingThread extends Thread {
    BufferedReader inputBuffer;
    Flags flags;

    public ReadingThread(BufferedReader buffer, Flags flags){
        inputBuffer = buffer;
        this.flags = flags;
    }

    @Override
    public void run() {
        while (!flags.getFlagEnd()) {
            try {
                //if(inputBuffer.ready()){
                    String s = inputBuffer.readLine();
                    System.out.println(s);

                    if (s != null) {
                        if (s.equals("forceEnd")) {
                            flags.setFlagEnd(false);
                        }
                    }
                //}
            } catch (IOException e) {
                System.out.println("il server si è disconnesso");
                flags.setFlagEnd(false);
                break;
            }
        }
    }

}
