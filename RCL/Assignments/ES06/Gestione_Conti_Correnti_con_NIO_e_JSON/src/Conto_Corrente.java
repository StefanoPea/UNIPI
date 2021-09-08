import java.util.LinkedList;

public class Conto_Corrente {

    private String intestatario;
    private final LinkedList<Movimento_Conto> movimenti = new LinkedList<>();

    public Conto_Corrente(){
        super();
    }

    public void addMovimento(Movimento_Conto movimento){
        movimenti.add(movimento);
    }

    public String getIntestatario(){
        return this.intestatario;
    }

    public LinkedList<Movimento_Conto> getMovimenti(){
        return this.movimenti;
    }

    public void setIntestatario(String Intestatario){
        this.intestatario = Intestatario;
    }

}

