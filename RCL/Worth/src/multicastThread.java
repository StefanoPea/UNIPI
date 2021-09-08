import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class multicastThread {

    /**
     * Nome del progetto e indirizzo
     */
    private final String progName, ipAddress;

    /**
     * porta relativa al progetto
     */
    private final int port;

    /**
     * Socket multicast
     */
    private MulticastSocket socket;

    /**
     * Stringbuilder per costruire la chat da mandare all'utente
     */
    private final StringBuilder chat;

    /**
     * Thread che si mette in ascolto
     */
    private Thread thread;


    /**
     * Costruttore della classe
     * @param progName nome del progetto
     * @param ipAddress indirizzo del progetto
     * @param port porta del progetto
     */
    public multicastThread(String progName, String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
        this.progName = progName;
        this.socket = null;
        this.chat = new StringBuilder();
        this.thread = null;
    }


    /**
     * metodo per inizializzare la socket e aggiungersi al gruppo della chat
     * @throws IOException
     */
    public void createSocket() throws IOException {
        socket = new MulticastSocket(this.getPort());
        socket.joinGroup(InetAddress.getByName(this.getIpAddress()));
    }


    /**
     * Metodo che fa partire il thread
     */
    public void startListening(){
        listener ct = new listener(this.chat, this.socket);
        thread = new Thread(ct);
        thread.start();
    }

    /**
     * metodo che stoppa il thread in ascolto
     */
    public void stopListening(){

            this.thread.interrupt();
            this.socket.close();
    }

    /**
     * Getters
     */
    public String getChat() {return chat.toString();}

    public MulticastSocket getSocket() {return socket;}

    public String getIpAddress(){ return this.ipAddress; }

    public int getPort(){ return this.port; }

    public String getProgName() {return progName;}

}
