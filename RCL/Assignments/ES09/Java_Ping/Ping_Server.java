import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

public class Ping_Server{

    private final long seed;
    private final int port;

    public Ping_Server(int port, long seed){
        this.port = port;
        this.seed = seed;
    }

    public void start(){

        Random random = new Random(seed);

        try (DatagramSocket serversocket = new DatagramSocket(port)) {

            byte[] buffer = new byte[256];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            String msg;

            while (true) {

                serversocket.receive(receivedPacket);
                msg = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                //Variabile che tiene il valore in base al quale decidere se perdere o rispedire il messaggio
                int lost = random.nextInt(4);
                //Variabile che tiene il valore casuale di ritardo per la risposta(bound == 300)
                int delay = random.nextInt(300);

                String action;

                //(lost == 0 -> messaggio perso)
                if (lost != 0) action = "delayed";
                else action = "not sent";

                if(action.equals("not sent")){
                    System.out.println(receivedPacket.getSocketAddress().toString().substring(1) +
                             "> " + new String(receivedPacket.getData()) + " ACTION: " + action);
                } else {
                    System.out.println(receivedPacket.getSocketAddress().toString().substring(1) +
                           "> " +  new String(receivedPacket.getData()) + " ACTION: " + action+ " " + delay + " ms");
                }

                // Perdita di pacchetti ~ 25%
                if(action.equals("delayed")){

                    // Simulazione ritardo di attesa
                    Thread.sleep(delay);

                    byte[] replyBuffer = msg.getBytes();

                    DatagramPacket packetToSend = new DatagramPacket(replyBuffer, replyBuffer.length, receivedPacket.getAddress(), receivedPacket.getPort());
                    serversocket.send(packetToSend);

                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
