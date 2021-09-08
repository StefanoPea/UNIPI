import java.util.ArrayList;

public class members {

    /**
     *  lista dei membri di un progetto
     */
    public ArrayList<member> members;

    /**
     * Costruttore della classe
     */
    public members(){
        this.members = new ArrayList<>();
    }

    /**
     * @return la lista dei membri
     */
    public ArrayList<member> getMembers() {
        return members;
    }

    /**
     * Metodo per aggiungere un membro alla lista
     * @param m membro da aggiungere alla lista di membri
     *
     */
    public void addMember(member m){
        this.members.add(m);
    }

    /**
     * Metodo per controllare se un utente con nome 'name' appartiene alla lista
     * @param name nome del membro
     * @return true se appartiene, false altrimenti
     */
    public boolean containsMember(String name){
        for (member m:
             this.members) {
            if(m.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

}
