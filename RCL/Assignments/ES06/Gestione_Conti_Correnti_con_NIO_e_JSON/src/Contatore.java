public class Contatore implements Runnable{

    Totale_Occorrenze occorrenze;
    private final Conto_Corrente conto;

    // Variabili che contengono i valori parziali delle causali
    private int numBonifici;
    private int numAccrediti;
    private int numBollettini;
    private int numF24;
    private int numPagoBancomat;



    public Contatore(Conto_Corrente conto, Totale_Occorrenze occorrenze){

        this.occorrenze = occorrenze;
        this.conto = conto;
        this.numBonifici = 0;
        this.numAccrediti = 0;
        this.numBollettini = 0;
        this.numF24 = 0;
        this.numPagoBancomat = 0;
    }

    public void run(){

        for ( Movimento_Conto mov : this.conto.getMovimenti()) {

            switch (mov.getCausale().toString()) {
                case "Bonifico" -> numBonifici++;
                case "Accredito" -> numAccrediti++;
                case "Bollettino" -> numBollettini++;
                case "F24" -> numF24++;
                case "PagoBancomat" -> numPagoBancomat++;
            }

        }

        // Aggiungo i valori parziali all'oggetto condiviso
        occorrenze.AddConteggio(this.numBonifici, this.numAccrediti, this.numBollettini, this.numF24, this.numPagoBancomat);

    }

}
