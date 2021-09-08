import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Lettore implements Runnable{

    private final ThreadPoolExecutor tpe;
    private final File file;
    private final Totale_Occorrenze occorrenze;

    public Lettore(File file, Totale_Occorrenze occorrenze, ThreadPoolExecutor tpe){

        this.tpe = tpe;
        this.file = file;
        this.occorrenze = occorrenze;
    }

    public void run() {

        ObjectMapper objectMapper = new ObjectMapper();
        Conto_Corrente_List list = null;

        try {
            list = objectMapper.readValue(file,Conto_Corrente_List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        for(Conto_Corrente conto: list.getConti()){
            Contatore cnt = new Contatore(conto,this.occorrenze);
            tpe.execute(cnt);
        }

        tpe.shutdown();

        try {
            tpe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}



