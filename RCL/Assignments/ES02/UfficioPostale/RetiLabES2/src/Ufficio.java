import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Ufficio{

    private ThreadPoolExecutor tpe;

    public Ufficio(int DimSecondaSala){

        // ArrayBlockingQueue: dimensione limitata, stabilita dal programmatore, simula la seconda stanza

        ArrayBlockingQueue<Runnable> SecondaSala = new ArrayBlockingQueue<>(DimSecondaSala);

        // Creazione del threadpool che simula gli sportelli dell'ufficio:

        tpe = new ThreadPoolExecutor(4,4,1000, TimeUnit.MILLISECONDS, SecondaSala);

    }


    public void GestioneCliente(Cliente cliente) throws InterruptedException {

        try {
            tpe.execute(cliente);
        } catch (RejectedExecutionException e) {
            tpe.getQueue().put(cliente); }
    }

    public void chiusura(){
        tpe.shutdown();
    }

}