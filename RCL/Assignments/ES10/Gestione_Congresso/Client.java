import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

    
        try {
            Registry registry = LocateRegistry.getRegistry(args[0]);
            IntCongresso stub = (IntCongresso) registry.lookup("Congresso");
            Scanner scanner = new Scanner(System.in);

            label:
            while(true) {

                System.out.println("Seleziona una richiesta:\n r -> richiesta di prenotaziona\n s -> stampa del calendario\n e -> esci dal programma");
                String req = scanner.nextLine();

                switch (req) {
                    case "r":

                            System.out.println("inserisci: giornata, sessione, posizione e nome relatore");
                            int giorno = Integer.parseInt(scanner.nextLine());
                            int sessione = Integer.parseInt(scanner.nextLine());
                            int posizione = Integer.parseInt(scanner.nextLine());
                            String relatore = scanner.nextLine();
                            String result = stub.prenota(giorno, sessione, posizione, relatore);
                            System.out.println(result);
                            break;

                    case "s":
                        String aux = stub.getCalendar();
                        System.out.println(aux);
                        break;
                    
                    case "e":
                        break label;
                }

            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
