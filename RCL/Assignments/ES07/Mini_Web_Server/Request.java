import java.util.Scanner;

public class Request{

  private final String filename;

  public Request(Scanner scanner){
    String request = scanner.nextLine();
    //Ottengo il nome del file richiesto
    filename = request.split(" ")[1];
  }

  public String getFilename(){
    return filename;
  }
}
