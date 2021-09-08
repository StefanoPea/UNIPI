/**
 *
 * PING è una utility per la valutazione delle performance della rete utilizzata per verificare la raggiungibilità
 * di un host su una rete IP e per misurare il round trip time (RTT) per i messaggi spediti da un host mittente verso
 * un host destinazione.
 *
 * -    lo scopo di questo assignment è quello di implementare un server PING ed un corrispondente client PING che
 *      consenta al client di misurare il suo RTT verso il server.
 * -    la funzionalità fornita da questi programmi deve essere simile a quella della utility PING disponibile in tutti
 *      i moderni sistemi operativi. La differenza fondamentale è che si utilizza UDP per la comunicazione tra client e
 *      server, invece del protocollo ICMP (Internet Control Message Protocol).
 * -    inoltre, poichè l'esecuzione dei programmi avverrà su un solo host o sulla rete locale ed in entrambe i casi sia
 *      la latenza che la perdita di pacchetti risultano trascurabili, il server deve introdurre un ritardo artificiale
 *      ed ignorare alcune richieste per simulare la perdita di pacchetti.
 *
 * Il Ping Client:
 *
 * -    accetta due argomenti da linea di comando: nome e porta del server. Se uno o più argomenti risultano scorretti,
 *      il client termina, dopo aver stampato un messaggio di errore del tipo ERR -arg x, dove x è il numero
 *      dell'argomento.
 * -    utilizza una comunicazione UDP per comunicare con il server ed invia 10 messaggi al server, con il seguente
 *      formato:
 *
 *                                                  PING seqno timestamp
 *
 * in cui seqno è il numero di sequenza del PING (tra 0-9) ed il timestamp (in millisecondi) indica quando il messaggio
 * è stato inviato
 *
 * -    non invia un nuovo PING fino che non ha ricevuto l'eco del PING precedente, oppure è scaduto un timeout.
 * -    stampa ogni messaggio spedito al server ed il RTT del ping oppure un "*ù" se la risposta non è stata ricevuta
 *      entro 2 secondi.
 * -    dopo che ha ricevuto la decima risposta (o dopo il suo timeout), il client stampa un riassunto simile a quello
 *      stampato dal PING UNIX
 *                                               ---- PING Statistics ----
 *
 *                                 10 packets transmitted, 7 packets received, 30% packet loss
 *
 *                                         round-trip (ms) min/avg/max = 63/190.29/290
 *
 * il RTT medio è stampato con 2 cifre dopo la virgola
 *
 */

public class Ping_Server_Main {
    public static void main(String[] args) {

        int port = 0;
        long seed = 0;

        //Controllo che i due argomenti siano corretti altrimenti restituisco l'errore
        try{port = Integer.parseInt(args[0]);} catch(Exception e){System.err.println("ERR - arg 0");System.exit(1);}
        try{seed = Long.parseLong(args[1]);} catch(Exception e){System.err.println("ERR - arg 1");System.exit(1);}

        Ping_Server server = new Ping_Server(port, seed);
        server.start();
    }
}
