import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Echo_Client {
    public static void main(String[] args) {

        String address = "localhost";
        int port = 6789;

        int limit = 256;

        Scanner scanner = new Scanner(System.in);
        SocketChannel socket;

        try {
            socket = SocketChannel.open(new InetSocketAddress(address, port));
            socket.configureBlocking(true);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        while(true) {

            String word = scanner.nextLine();
            if(word.equals("quit")) break;

            ByteBuffer out = ByteBuffer.allocate(limit);
            ByteBuffer in = ByteBuffer.allocate(limit);

            out.put(word.getBytes());
            out.flip();

            try {
                socket.write(out);
                socket.read(in);
            } catch (IOException e) {
                scanner.close();
                e.printStackTrace();
                try {
                    socket.close();
                    break;
                } catch(IOException ee) {
                    ee.printStackTrace();
                    return;
                }
            }

            //Stampa del messaggio ricevuto dal server
            System.out.println(new String(in.array()));
        }

        System.out.println("Closing the collection...");
    }
}
