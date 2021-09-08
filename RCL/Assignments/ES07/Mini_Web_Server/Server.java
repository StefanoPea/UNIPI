import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable{

  private final Socket socket;

  public Server(Socket socket){
    this.socket = socket;
  }

  public void run(){

    System.out.println("Connected: " + socket);

    try{
      Scanner scanner = new Scanner(socket.getInputStream());

      //Creo la richiesta
      Request request = new Request(scanner);

      //Creo il messaggio da inviare al Client
      Message message = new Message(request.getFilename());

      //Invio il messaggio al Client
      message.sendMessage(socket.getOutputStream());

      //Chiudo la socket
      socket.close();

    } catch(IOException e){
        System.err.println("Error: " + socket);
        e.printStackTrace();

      }
  }
}
