import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;
import java.io.IOException;

public class Echo_Server{
    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        Selector selector;

        int port = 6789;
        int limit = 256;

        System.out.println("Server starting ... listening on port " + port);

        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address);
        serverChannel.configureBlocking(false);
        selector = Selector.open();
        serverChannel.register(selector,SelectionKey.OP_ACCEPT);

        while(true){

            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator <SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {

                SelectionKey key = (SelectionKey) keyIterator.next();
                keyIterator.remove();

                try {
                    if (key.isAcceptable()) {

                        ServerSocketChannel server = (ServerSocketChannel ) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey Key2 = client.register(selector, SelectionKey.OP_READ);
                        ByteBuffer output = ByteBuffer.allocate(limit);
                        Key2.attach(output);

                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        System.out.println("Receiving message from " + client);
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        SelectionKey Key2 = client.register(selector, SelectionKey.OP_WRITE);
                        client.read(output);
                        Key2.attach(output);

                    } else if (key.isWritable()) {
                        byte[] message = " (echoed by server)".getBytes();
                        SocketChannel client = (SocketChannel) key.channel();
                        System.out.println("Sending back the message to " + client);
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        SelectionKey Key2 = client.register(selector, SelectionKey.OP_READ);
                        output.put(message);
                        output.flip();
                        client.write(output);
                        output.compact();
                        Key2.attach(output);

                    }

                } catch (IOException ex) { key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex){}
                }
            }
        }
    }
}
