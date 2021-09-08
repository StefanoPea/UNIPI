

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Tutor {


    private final ReentrantLock Lab = new ReentrantLock();  // Lock per accesso al laboratorio
    private final ReentrantLock[] PCs;    // Array di lock: una per ogni pc del laboratorio.
    private final Condition Nuovo_PC_Libero = Lab.newCondition();  // Variabile che avverte se e' disponibile un nuovo pc
    private final Condition Lab_Disponibile = Lab.newCondition();  // Variabile che avverte se tutto il laboratorio e' disponibile
    private final Condition[] PC_Specifico_Disponibile;  // Array di variabili per avvertire quando un particolare pc si e' liberato
    private final Boolean[] Stato_PC; // Array di booleani che indica se ogni pc e' occupato oppure no (true = occupato, false = libero)
    private int PC_Disponibili; // Numero di PC liberi nel laboratorio

    // Variabili che contengono il numero di professori e tesisti che vorrebbero accedere al laboratorio
    // (utile per gestire le priorita')
    private int Professori = 0;
    private int Tesisti = 0;

    public Tutor(int NumPC) {

        PC_Disponibili = NumPC;
        Stato_PC = new Boolean[NumPC];
        PCs = new ReentrantLock[NumPC];
        PC_Specifico_Disponibile = new Condition[NumPC];
        //inizializzo i miei array
        for(int i = 0; i < NumPC; i++){
            Stato_PC[i] = false;
            PCs[i] = new ReentrantLock();
            PC_Specifico_Disponibile[i] = Lab.newCondition();

        }
    }


    public void RichiediPC(Utente utente) {

        if(utente.Tipologia.equals("Studente")) {
            Lab.lock();
            while(PC_Disponibili < 1 || Professori > 0 || Tesisti > 0){
                try {
                    Nuovo_PC_Libero.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < Stato_PC.length; i++) {
                if(!Stato_PC[i]){
                    utente.PcAssegnato = i;
                    break;
                }
            }

            Stato_PC[utente.PcAssegnato] = true;
            PC_Disponibili--;
            PCs[utente.PcAssegnato].lock();
            Lab.unlock();
            return;
        }

        if(utente.Tipologia.equals("Tesista")) {
            Lab.lock();
            Tesisti++;
            while(Stato_PC[utente.PcAssegnato] || Professori > 0) {
                try {
                    PC_Specifico_Disponibile[utente.PcAssegnato].await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Tesisti--;
            Stato_PC[utente.PcAssegnato] = true;
            PC_Disponibili--;
            PCs[utente.PcAssegnato].lock();
            Lab.unlock();
            return;
        }

        if(utente.Tipologia.equals("Professore")) {

            Lab.lock();
            Professori++;

            while(PC_Disponibili < Stato_PC.length){
                try {
                    Lab_Disponibile.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Professori--;

            for(int i = 0; i < Stato_PC.length; i++) {
                PCs[i].lock();
                Stato_PC[i] = true;
                PC_Disponibili--;
            }
            Lab.unlock();
        }
    }

    public void RilasciaPC(Utente utente) {

        if(utente.Tipologia.equals("Tesista") || utente.Tipologia.equals("Studente")) {

            Lab.lock();
            PCs[utente.PcAssegnato].unlock();
            Stato_PC[utente.PcAssegnato] = false;
            PC_Disponibili++;
            if(PC_Disponibili == Stato_PC.length){ Lab_Disponibile.signal();}
            Nuovo_PC_Libero.signal();
            PC_Specifico_Disponibile[utente.PcAssegnato].signal();
            Lab.unlock();

        } else {

            Lab.lock();
            for(int i = 0; i < Stato_PC.length; i++) {

                PCs[i].unlock();
                Stato_PC[i] = false;
                PC_Disponibili++;
                if(Professori == 0){

                    PC_Specifico_Disponibile[i].signal();
                    Nuovo_PC_Libero.signal();

                }
            }

            if(Professori > 0) Lab_Disponibile.signal();
            Lab.unlock();
        }
    }
}
