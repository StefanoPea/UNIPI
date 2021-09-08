/*
si scriva un programma JAVA che:

- Riceve in input un filepath che individua una directory D.
- Stampa le informazioni del contenuto di quella directory e, ricorsivamente, tutti i file contenuti nelle sottodirectory di D.

Il programma deve essere strutturato come segue:

- Attiva un thread produttore ed un insieme di k thread consumatori.
- Il produttore comunica con i consumatori mediante una coda.
- Il produttore visita ricorsivamente la directory data ed, eventualmente tutte le sottodirectory e mette nella coda
  il nome di ogni directory individuata.
- I consumatori prelevano dalla coda i nomi delle directories e stampano il loro contenuto.
- La coda deve essere realizzata con una LinkedList. Ricordiamo che una Linked List non è una struttura thread-safe.

Dalle API JAVA “Note that the implementation is not synchronized. If multiple threads access a linked list concurrently,
and at least one of the threads modifies the list structurally, it must be synchronized externally”.
*/

import java.util.LinkedList;

public class Main {

    public static void main(String args[]){

        //args[0] -> filepath directory
        //args[1] -> numero di thread consumatori

        LinkedList<String> directories = new LinkedList<>();
        CommonQueue commonqueue = new CommonQueue(directories);

        //Creazione del thread Produttore
        Produttore produttore = new Produttore(args[0], commonqueue);
        Thread aux = new Thread(produttore);
        aux.start();

        //Creazione dei thread Consumatori
        for(int i = 0; i < Integer.parseInt(args[1]); i++){
            Consumatore consumatore = new Consumatore(commonqueue);
            Thread aux2 = new Thread(consumatore);
            aux2.start();
        }

    }

}

