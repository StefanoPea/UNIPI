import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Message{

  private final String header;
  private byte[] body = null;

  public Message(String filename){

    File file = new File("/home/stefano/Desktop/TEST/" + filename);

    // Creo il body del messaggio
    try{
      body = Files.readAllBytes(Paths.get(String.valueOf(file.toPath())));
    } catch(IOException e){
      e.printStackTrace();
    }

    //Creo l'header del messaggio
    header = switch(getFileExtension(filename)){
      case "jpg" -> "HTTP/1.1 200 OK\r\n" + "Content-Type:image/jpeg\r\n\r\n";
      case "gif" -> "HTTP/1.1 200 OK\r\n" + "Content-Type:image/gif\r\n\r\n";
      case "json" -> "HTTP/1.1 200 OK\r\n" + "Content-Type:image/json\r\n\r\n";
      case "html" -> "HTTP/1.1 200 OK\r\n" + "Content-Type:image/html\r\n\r\n";
      default -> throw new IllegalStateException("Unexpected value: " + getFileExtension(filename));
    };


  }


  // Metodo che ritorna l'estensione di un file
  public static String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    //Metodo per inviare il messaggio sull'OutputStream
    public void sendMessage(OutputStream outputStream) throws IOException{
      DataOutputStream out = new DataOutputStream(outputStream);
      out.writeBytes(header);
      out.write(body);
      out.flush();
      out.close();
      System.out.println("Message sent ");
    }
}
