import java.io.*;

public class ThreadLettura extends Thread {
    BufferedReader inputBuffer;
    Flags flags;

    public ThreadLettura(BufferedReader buffer, Flags flags){
        inputBuffer = buffer;
        this.flags = flags;
    }

    @Override
    public void run() {
        while (!flags.getFlagEnd()) {
            try {
                if(inputBuffer.ready()){
                    String s = inputBuffer.readLine();
                    System.out.println(s);

                    if (s.equals("forceEnd")) {
                        flags.setFlagEnd(false);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {}
        }
    }

}
