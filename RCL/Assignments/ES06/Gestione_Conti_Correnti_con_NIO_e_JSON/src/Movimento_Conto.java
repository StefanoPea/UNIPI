public class Movimento_Conto {

    private Causale causale;
    private String date;

    // Tutte le possibili causali
    enum Causale{
        Bonifico,
        Accredito,
        Bollettino,
        F24,
        PagoBancomat;

        //Metodo che ritorna un elemento casuale della lista
        public static Causale getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    public Movimento_Conto(){ super();}

    public String getDate(){
     return this.date;
    }

    public Causale getCausale(){
        return this.causale;
    }

    public void setDate(String date){ this.date = date; }

    public void setCausale(Causale causale){
        this.causale = causale;
    }

}

