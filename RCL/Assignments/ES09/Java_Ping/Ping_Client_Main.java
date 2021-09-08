public class Ping_Client_Main {
    public static void main(String[] args) {

        int port = 0;
        String serverName = null;

        //Controllo che i due argomenti siano corretti altrimenti restituisco l'errore
        try{serverName = args[0];} catch(Exception e){System.err.println("ERR -arg 0");System.exit(1);}
        try{port = Integer.parseInt(args[1]);} catch(Exception e){System.err.println("ERR - arg 1");System.exit(1);}

        Ping_Client client = new Ping_Client(serverName, port);
        client.start();
    }
}
