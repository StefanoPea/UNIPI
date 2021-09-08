/*

Il laboratorio di Informatica del Polo Marzotto è utilizzato da tre tipi di utenti, studenti, tesisti e professori ed
ogni utente deve fare una richiesta al tutor per accedere al laboratorio.
I computers del laboratorio sono numerati da 1 a 20. Le richieste di accesso sono diverse a seconda del tipo
dell'utente:

a)  i professori accedono in modo esclusivo a tutto il laboratorio, poichè hanno necessità di utilizzare tutti i
    computers per effettuare prove in rete.

b)  i tesisti richiedono l'uso esclusivo di un solo computer, identificato dall'indice i, poichè su quel computer è
    installato un particolare software necessario per lo sviluppo della tesi.

c)  gli studenti richiedono l'uso esclusivo di un qualsiasi computer.

I professori hanno priorità su tutti nell'accesso al laboratorio, i tesisti hanno priorità sugli studenti.
Nessuno però può essere interrotto mentre sta usando un computer.

Scrivere un programma JAVA che simuli il comportamento degli utenti e del tutor.
Il programma riceve in ingresso il numero di studenti, tesisti e professori che utilizzano il laboratorio ed attiva un
thread per ogni utente.
Ogni utente accede k volte al laboratorio, con k generato casualmente.
Simulare l'intervallo di tempo che intercorre tra un accesso ed il successivo e l'intervallo di permanenza in
laboratorio mediante il metodo sleep.
Il tutor deve coordinare gli accessi al laboratorio. Il programma deve terminare quando tutti gli utenti hanno
completato i loro accessi al laboratorio.

*/

public class Main {
    public static void main (String[] args) {

        final int Studenti = Integer.parseInt(args[0]);
        final int Tesisti = Integer.parseInt(args[1]);
        final int Professori = Integer.parseInt(args[2]);
        final int NumPC = 20; // numero di pc nel laboratorio


        Tutor_Monitor tutor = new Tutor_Monitor(NumPC);


        System.out.printf("Creazione di %d studenti\n", Studenti);
        System.out.printf("Creazione di %d tesisti\n", Tesisti);
        System.out.printf("Creazione di %d Professori\n", Professori);

        for(int i = 0; i < Studenti; i++) {
            Utente studente = new Utente(NumPC, "Studente", tutor, i);
            Thread aux = new Thread(studente);
            aux.start();
        }

        for(int i = 0; i < Tesisti; i++) {
            Utente tesista = new Utente(NumPC, "Tesista", tutor, i);
            Thread aux = new Thread(tesista);
            aux.start();
        }

        for(int i = 0; i<Professori; i++) {
            Utente professore = new Utente(NumPC,"Professore", tutor, i);
            Thread aux = new Thread(professore);
            aux.start();
        }
    }
}