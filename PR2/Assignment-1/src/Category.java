import java.util.ArrayList;
import java.util.List;

public class Category <E extends Data> {

    private int numFriends;
    private  int numDati;
    private List<String> friends;
    private List<E> dati;

    //------------------------------------------------------------------------------------------------------------

    /**  Metodo costruttore di Category */

    public Category(){

        this.numFriends = 0;
        this.numDati = 0;
        this.friends = new ArrayList<>();
        this.dati = new ArrayList<>();
    }

    //-------------------------------------------------------------------------------------------------------------

    /** Metodo che aggiunge un amico alla collezione */

    public void addFriend(String friend) throws NullPointerException{

        if(friend == null) throw new NullPointerException();
        friends.add(friend);
        numFriends++;
    }

    //--------------------------------------------------------------------------------------------------------------

    /** Metodo che ritorna la lista degli amici che hanno accesso alla categoria */

    public ArrayList<String> getFriends() {

        return new ArrayList<String>(friends);
    }


    //---------------------------------------------------------------------------------------------------------------

    /** Metodo che rimuove un amico dalla categoria */

    public void removeFriend(String friend) throws NullPointerException{

        if(friend == null) throw new NullPointerException();
        friends.remove(friend);
        numFriends--;
    }

    //----------------------------------------------------------------------------------------------------------------

    /** Metodo che ritorna una copia di tutti i dati presenti nella categoria */

    public List<E> getDati() {

        return new ArrayList<>(dati);
    }

    //----------------------------------------------------------------------------------------------------------------

    /** Metodo che aggiunge un dato all categoria */

    public void addDato(E dato) throws NullPointerException{

        if(dato == null) throw new NullPointerException();
        dati.add(dato);
        numDati++;
    }

    //----------------------------------------------------------------------------------------------------------------

    /** Metodo che rimuove un dato dalla categoria */

    public void removeDato(E dato) throws NullPointerException{

        if(dato == null) throw new NullPointerException();
         dati.remove(dato);
        numDati--;
    }
}
