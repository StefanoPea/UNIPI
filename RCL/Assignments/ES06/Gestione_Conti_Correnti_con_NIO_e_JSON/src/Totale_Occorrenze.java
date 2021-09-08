public class Totale_Occorrenze {

    // Variabili che vengono aggiornate con i dati riguardanti le causali

    private int numBonifici;
    private int numAccrediti;
    private int numBollettini;
    private int numF24;
    private int numPagoBancomat;



    public Totale_Occorrenze(){

        this.numBonifici = 0;
        this.numAccrediti = 0;
        this.numBollettini = 0;
        this.numF24 = 0;
        this.numPagoBancomat = 0;
    }


    synchronized void AddConteggio(int Bon, int Acc, int Bol, int F24, int PB){

        this.numBonifici = numBonifici + Bon;
        this.numAccrediti = numAccrediti + Acc;
        this.numBollettini = numBollettini + Bol;
        this.numF24 = numF24 + F24;
        this.numPagoBancomat = numPagoBancomat + PB;

    }

    public void Stampa_Occorrenze(){
        System.out.println("Numero di Bonifici = " + numBonifici + "\n" +
                "Numero di Accrediti = " + numAccrediti + "\n" +
                "Numero di Bollettini = " + numBollettini + "\n" +
                "Numero di F24 = " + numF24 + "\n" +
                "Numero di PagoBancomat = " + numPagoBancomat + "\n");
    }
}

