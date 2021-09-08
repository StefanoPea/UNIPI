import java.io.IOException;
import java.rmi.NotBoundException;

public class clientMain {
    final static int DEFAULT_PORT = 6789;

    public static void main(String[] args) throws NotBoundException, IOException {
        int myPort = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                myPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Fornire il numero di porta come intero");
                System.exit(-1);
            }
        }
        // crea e avvia il client
        client client = new client(myPort);
        client.start();
    }
}