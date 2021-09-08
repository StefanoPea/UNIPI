import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class TimeClient {

    private final int port;
    private final String address;


    public TimeClient(String address, int port) {
        this.address = address;
        this.port = port;
    }



    public void receiveMsg(String ip, int port) {

        byte[] buffer = new byte[1024];
        try {

            MulticastSocket socket = new MulticastSocket(port);
            InetAddress group = InetAddress.getByName(ip);
            socket.joinGroup(group);

            for(int i = 0; i < 10; i++){

                System.out.println("Waiting for multicast message...");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), packet.getOffset(),packet.getLength());
                System.out.println("[Multicast UDP message received] >> "+ msg);

            }

            socket.leaveGroup(group);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start(){

        receiveMsg(address, port);
    }

    public static void main(String[] args) {

        String address = args[0];
        int port = Integer.parseInt(args[1]);

        TimeClient client = new TimeClient(address, port);
        client.start();
    }


}
