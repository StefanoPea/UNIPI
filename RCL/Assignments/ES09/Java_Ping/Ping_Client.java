import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Ping_Client {

    private final int port;
    private final int serverPort;
    private final String serverName;
    private final int timeout;

    long delay;
    String msg;

    //Variabili per il riassunto
    int packetsReceived = 0;
    int lostPercentage;
    int min = 2000;     //imposto il minimo ritardo iniziale al massimo valore di attesa del client
    int max = -1;
    long sum = 0;
    float avg = 0;

    public Ping_Client(String serverName, int serverPort){
        this.port = 30000;
        this.timeout = 2000;
        this.serverPort = serverPort;
        this.serverName = serverName;
    }

    public void start() {

        for (int i = 0; i < 10; i++) {

            try (DatagramSocket client = new DatagramSocket(port)) {

                Date date = new Date();
                long startingDate = date.getTime();

                //Costruisco il messaggio da inviare al server
                msg = "PING " + i + " " + startingDate;

                byte[] pingBuffer = msg.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packetToSend = new DatagramPacket(pingBuffer, msg.length(), InetAddress.getByName(this.serverName), this.serverPort);

                client.send(packetToSend);

                // imposto il timeout
                client.setSoTimeout(timeout);

                byte[] buffer = new byte[msg.length()];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                client.receive(receivedPacket);

                Date date2 = new Date();
                long finalDate = date2.getTime();

                // ritardo
                delay = finalDate - startingDate;

                System.out.println(msg + " RTT: " + delay + " ms");

                //Aggiorno le variabili per la stampa del riassunto
                packetsReceived++;
                if (delay < min)  min = (int) delay;
                if (delay > max)  max = (int) delay;
                sum = (sum + delay);

            } catch (SocketTimeoutException e) {
                System.out.println(msg + " RTT: *" );
            } catch (BindException e) {
                System.out.println("Porta già occupata");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //percentuale di pacchetti persi
        lostPercentage = 100 - ((packetsReceived*100)/10);
        //Media dei ritardi
        avg = ((float)sum/(float)packetsReceived);

        System.out.println("---- PING Statistics ----");
        System.out.printf("10 packets transmitted, %d packets received, %d%% packet loss\n", packetsReceived, lostPercentage);
        System.out.printf("round-trip (ms) min/avg/max = %d/%.2f/%d",min, avg, max);
    }
}
