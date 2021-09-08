/*

Stefano Pea 561020

Definire un Server TimeServer, che:

invia su un gruppo di multicast dategroup, ad intervalli regolari,la data e l’ora.
attende tra un invio ed il successivo un intervallo di tempo simulata mediante il metodo sleep( ).
L’indirizzo IP di dategroup viene introdotto da linea di comando. Definire quindi un client TimeClient che si unisce a dategroup e riceve, per dieci volte consecutive, data ed ora, le visualizza, quindi termina
*/


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServer {

    public static void sendMsg(String ipAddress, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress dateGroup = InetAddress.getByName(ipAddress);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        String msg = sdf.format(new Date().getTime());

        byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, msg.length() , dateGroup, port);
        socket.send(packet);
        socket.close();
    }

    public static void main(String[] args){

        String address = args[0];
        int port = Integer.parseInt(args[1]);

        while(true){

            try{
                sendMsg(address, port);
                Thread.sleep(2000);
                
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }
}
