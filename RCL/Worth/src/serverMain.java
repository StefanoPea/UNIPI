import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


class serverMain {

    final static int DEFAULT_PORT = 6789;

    public static void main(String[] args) throws  RemoteException {

        int myPort = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                myPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Fornire il numero di porta come intero");
                System.exit(-1);
            }
        }
        // crea e avvia il server
        server server = new server(myPort);

        //creazione dello stub per RMI
        serverInterface stub = (serverInterface) UnicastRemoteObject.exportObject( server, 0);
        LocateRegistry.createRegistry(1099);
        Registry registry = LocateRegistry.getRegistry(1099);
        registry.rebind("ServerRMI",stub);

        server.start();

    }

}