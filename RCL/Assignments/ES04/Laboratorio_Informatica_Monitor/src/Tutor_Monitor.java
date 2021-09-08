import java.util.ArrayList;
import java.util.Random;

public class Tutor_Monitor {

    private int professori = 0; //Numero di professori in coda
    private int tesisti = 0; //Numero di tesisti in coda
    private final int PC_Totali; //Numero totale di PC
    ArrayList<Integer> Elenco_PC; // Lista degli oggetti condivisi dagli utenti ( PC )
    Random random = new Random();



    public Tutor_Monitor(int NumPC){

        PC_Totali = NumPC;
        Elenco_PC = new ArrayList<>(NumPC);
        for(int i = 0; i < NumPC; i++){
            Elenco_PC.add(i);
        }
    }

    synchronized void RichiediPC(Utente utente){

        if(utente.Tipologia.equals("Studente")){
            while(Elenco_PC.isEmpty() || tesisti > 0 || professori > 0){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Rimuovo un pc libero dalla lista e lo assegno allo studente
            utente.PcAssegnato = Elenco_PC.remove(random.nextInt(Elenco_PC.size()));
        }

        if(utente.Tipologia.equals("Tesista")){
            tesisti++;
            while(professori > 0 || !Elenco_PC.contains(utente.PcAssegnato)){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            tesisti--;
            // Rimuovo il pc necessario al tesista dalla lista e glielo assegno
            Elenco_PC.remove(Integer.valueOf(utente.PcAssegnato));
        }


        if(utente.Tipologia.equals("Professore")){
            professori++;
            while(Elenco_PC.size() < PC_Totali){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            professori--;
            // Rimuovo tutti i pc dalla lista
            Elenco_PC.clear();
        }

    }

    synchronized void RilasciaPC(Utente utente){

        if(utente.Tipologia.equals("Studente") || utente.Tipologia.equals("Tesista")){
            //Rimetto il PC dello Studente o del Tesista nella lista e avviso gli utenti in attesa
            Elenco_PC.add(utente.PcAssegnato);
            this.notifyAll();
        }

        if(utente.Tipologia.equals("Professore")){
           // //Rimetto tutti i PC prenotati da un Professore nella lista e avviso gli utenti in attesa
            for(int i = 0; i < PC_Totali; i++){
                Elenco_PC.add(i);
            }
            this.notifyAll();
        }
    }

}