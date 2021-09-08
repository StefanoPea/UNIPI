/*

Scrivere un programma JAVA che implementi un server Http che gestisca richieste di trasferimento di file di diverso
tipo (es. immagini jpeg, gif) provenienti da un browser web.

Il  server:

- Sta in ascolto su una porta nota al client (es. 6789)
- Gestisce richieste Http di tipo GET alla Request URL localhost:port/filename
- Le connessioni possono essere non persistenti.
- Usare le classi Socket e ServerSocket per sviluppare il programma server
- Per inviare al server le richieste, utilizzare un qualsiasi browser

*/

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main{
  public static void main(String[] args) throws Exception{

    try(ServerSocket server = new ServerSocket(6789)){
      System.out.println("Server Running...");
      ExecutorService pool = Executors.newFixedThreadPool(20);
      while(true){
        pool.execute(new Server(server.accept()));
      }
    }

  }

}
