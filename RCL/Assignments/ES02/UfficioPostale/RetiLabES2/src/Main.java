/*
Simulare il flusso di clienti in un ufficio postale che ha 4 sportelli. Nell'ufficio esiste:

     ●  un'ampia sala d'attesa in cui ogni persona può entrare liberamente. Quando entra, ogni persona prende il numero
        dalla numeratrice e aspetta il proprio turno in questa sala.

     ●  una seconda sala, meno ampia, posta davanti agli sportelli, in cui si può entrare solo a gruppi di k persone

     ●  Una persona si mette quindi prima in coda nella prima sala, poi passa nella seconda sala.

     ●  Ogni persona impiega un tempo differente per la propria operazione allo sportello.
        Una volta terminata l'operazione, la persona esce dall'ufficio.

Scrivere un programma in cui:

     ●  l'ufficio viene modellato come una classe JAVA, in cui viene attivato un ThreadPool di dimensione uguale al
        numero degli sportelli

     ●  la coda delle persone presenti nella sala d'attesa è gestita esplicitamente dal programma

     ●  la seconda coda (davanti agli sportelli) è quella gestita implicitamente dal ThreadPool

     ●  ogni persona viene modellata come un task, un task che deve essere assegnato ad uno dei thread associati agli
        sportelli

     ●  si preveda di far entrare tutte le persone nell'ufficio postale, all'inizio del programma

Facoltativo:

        prevedere il caso di un flusso continuo di clienti e la possibilità che l'operatore chiuda lo sportello stesso
        dopo che in un certo intervallo di tempo non si presentano clienti al suo sportello.
*/


import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws InterruptedException {


        // Scan degli argomenti necessari al programma (k = capienza seconda stanza, NumClienti = numero clienti
        // nell'ufficio postale)

        Scanner input0 = new Scanner(System.in);
        int k = input0.nextInt();

        Scanner input1 = new Scanner(System.in);
        int NumClienti = input1.nextInt();



        // Creazione degli utenti e inserimento nella prima stanza

        ArrayList<Cliente> PrimaStanza = new ArrayList<>(NumClienti);

        for (int i = 0; i < NumClienti; i++) {

            int id = i +1;
            Cliente cliente = new Cliente("Cliente " + id);
            PrimaStanza.add(cliente);
        }


        // Creazione Ufficio Postale e tentativo di inserimento dei clienti nella seconda stanza: se la stanza non puo'
        // accettare altri utenti si attende fino a che non si libera un posto.

        Ufficio ufficio = new Ufficio(k);

        while (!PrimaStanza.isEmpty()) {

            Cliente cliente = PrimaStanza.remove(0);
            ufficio.GestioneCliente(cliente);

        }

        // Chiusura dell'ufficio

        ufficio.chiusura();}}

