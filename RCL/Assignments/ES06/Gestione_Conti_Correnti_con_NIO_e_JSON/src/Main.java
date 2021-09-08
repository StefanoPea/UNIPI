/*

* Creare un file contenente oggetti che rappresentano i conti correnti di una banca.
* Ogni conto corrente contiene il nome del correntista ed una lista di movimenti.
* I movimenti registrati per un conto corrente sono relativi agli ultimi 2 anni, quindi possono essere molto numerosi.
* Per ogni movimento vengono registrati la data e la causale del movimento.
* L'insieme delle causali possibili è fissato: Bonfico, Accredito, Bollettino, F24, PagoBancomat.
* Rileggere il file e trovare, per ogni possibile causale, quanti movimenti hanno quella causale.
* Progettare un'applicazione che attiva un insieme di thread.
* Uno di essi legge dal file gli oggetti “conto corrente” e li passa, uno per volta, ai thread presenti in un thread pool.
* Ogni thread calcola il numero di occorrenze di ogni possibile causale all'interno di quel conto corrente ed aggiorna un contatore globale.
* Alla fine il programma stampa per ogni possibile causale il numero totale di occorrenze.
* Utilizzare NIO per l'interazione con il file e JSON per la serializzazione.

*/


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        //Genero il File JSON
        FileJSON file = new FileJSON();

        // Creo il contatore globale
        Totale_Occorrenze occorrenze = new Totale_Occorrenze();

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        // Creo il thread che ha il compito di leggere il file e distribuire i vari conti correnti al threadpool
        Lettore lettore = new Lettore(new File("/home/stefano/Desktop/test.json"),occorrenze,tpe);
        Thread reader = new Thread(lettore);
        reader.start();

        try { reader.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        occorrenze.Stampa_Occorrenze();

    }
}





