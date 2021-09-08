import java.io.IOException;
import java.net.DatagramPacket;

import java.net.MulticastSocket;


public class listener implements Runnable{


    /**
     * Stringbuilder per ritornare la chat del progetto
     */
    private final StringBuilder chat;

    /**
     * Socket su cui mettersi in ascolto
     */
    private final MulticastSocket socket;


    /**
     * Costruttore della classe
     * @param chat
     * @param socket
     */
    public listener(StringBuilder chat, MulticastSocket socket) {
        this.chat = chat;
        this.socket = socket;
    }


    /**
     * Metodo run del thread che rimane in attesa di messaggi della chat
     */
    public void run() {
        while(true){
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            try {
                this.socket.receive(dp);
            } catch (IOException e) {
                this.socket.close();
                break;
            }
            String received = new String(dp.getData(), 0, dp.getLength());
            chat.append(received).append("\n");
        }
    }

}
