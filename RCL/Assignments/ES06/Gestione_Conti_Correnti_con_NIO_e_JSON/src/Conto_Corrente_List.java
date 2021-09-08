import java.util.LinkedList;

public class Conto_Corrente_List {

    private LinkedList<Conto_Corrente> conti = new LinkedList<>();

    public Conto_Corrente_List(){super();}

    public LinkedList<Conto_Corrente> getConti(){
        return this.conti;
    }

    public void setConti(LinkedList<Conto_Corrente> conti){
        this.conti = conti;
    }
}


