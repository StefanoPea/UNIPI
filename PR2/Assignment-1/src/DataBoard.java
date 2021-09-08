import java.util.*;

public interface DataBoard < E extends Data>{

    /**
     *
     * Tipical Element: <MasterPassw, Owner, numCategories, categories>
     *                  in cui:
     *                      -MasterPassw   =     password che permette sia l'accesso alla Board che a tutti i suoi
     *                                           metodi che ne modificano il contenuto (impostata durante la creazione)
     *                      -Owner         =     proprietario della Board (impostato durante la creazione)
     *                      -numCategories =     intero che rappresenta il numero di categorie presenti nella board
     *                      -categories    =     categorie presenti nella board (possono essere usate molteplici
     *                                           strutture dati per contenere le categorie)
     *
     *
     * Rep Invariant:   Masterpassw != null,
     *                  Owner != null,
     *                  categories != null
     *                  (numCategory == categories.size()) >= 0
     *                  (forall category in this.Categories) ==> category != null
     */


    //----------------------------------------------------------------------------------------------------------------

    /** Metodo che ritorna il nome del proprietario della board
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return Owner
     * @throws: nothing
     *
     * */

    public String getOwner();


    //----------------------------------------------------------------------------------------------------------------

    /** Metodo che ritorna il numero di categorie presenti nella board
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return numCategories
     * @throws: nothing
     *
     * */

    public int getNumCategories();

    //----------------------------------------------------------------------------------------------------------------

    /** Metodo che crea una nuova categoria all'interno della board
     *
     * @requires: passw = this.MasterPassw, passw != null, name != null,
     *            name non appartenente alle categorie
     * @modifies: this
     * @effects:  this.numCategory++, aggiunge name alla lista di categorie ;
     * @throws: InvalidActionException, IncorrectPasswordException, NullPointerException
     */

    public void createCategory(String name, String passw)
            throws InvalidActionException, IncorrectPasswordException, NullPointerException;


    //----------------------------------------------------------------------------------------------------------------

    /**
     * @requires: category appartenente a categories, category != null,
     * @modifies: nothing
     * @effects: ritorna una lista degli amici che hanno accesso alla categoria
     * @throws: InvalidActionException, NullPointerException
     */

    public ArrayList<String> getFriends(String category)
            throws InvalidActionException, NullPointerException;

    //-----------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che permette di rimuovere una categoria presente nella board
     *
     * @requires: Category appartenente a categories, Category != null, passw = this.MaterPassw, passw != null
     * @modifies: this.categories
     * @effects: numCategories--, rimuove Category da categories
     * @throws: InvalidActionException, NullPointerException, IncorrectPasswordException
     */

    public void removeCategory(String Category, String passw)
            throws InvalidActionException, NullPointerException, IncorrectPasswordException;

    //----------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che permette di aggiungere un amico alla lista di persone che possono visualizzare i contenuti della
     * bacheca
     *
     * @requires: nameCat appartenente a categories, nameCat != null, passw = this.MasterPassword, passw != null
     *            friend != null, friend non appartenente alla lista di amici che hanno accesso alla categoria
     * @modifies: this.categories
     * @effects: aggiunge friend alla categoria nameCat
     * @throws: InvalidActionException, NullPointerException, IncorrectPasswordException
     */

    public void addFriend(String nameCat, String passw, String friend)
            throws IncorrectPasswordException, InvalidActionException, NullPointerException;


    //----------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che permette di rimuovere un amico dalla lista di persone che possono visualizzare i contenuti della
     * bacheca
     *
     * @requires: nameCat appartenente a categories, nameCat != null, passw = this.MasterPassw, friend != null,
     *            friend appartenente alla lista di amici di nameCat, passw != null
     * @modifies: this.categories
     * @effects: rimuove friend dalla lista di amici che possono visualizzare la categoria
     * @throws: InvalidActionException, NullPointerException, IncorrectPasswordException
     */

    public void removeFriend(String nameCat, String passw, String friend)
            throws InvalidActionException, NullPointerException, IncorrectPasswordException;

    //-----------------------------------------------------------------------------------------------------------------

    /**
     *  Metodo che permette di inserire un dato in bacheca se si rispettano i controlli di identita'
     *
     * @requires:   categoria appartenente a categories, categoria != null, passw = this.MasterPassw, dato != null,
     *              passw != null, dato non appartenente alla categoria
     * @modifies:   this.categories
     * @effects:    inserisce il dato in 'categoria', numCategory++ e dato.setCategory(categoria)
     * @throws:     IllegalArgumentException, NullPointerException, IncorrectPasswordException
     */

    public boolean put(String passw, E dato, String categoria)
            throws NullPointerException, InvalidActionException, IncorrectPasswordException;

//--------------------------------------------------------------------------------------------------------------------

    /**
     *  Metodo che ottiene una copia del dato in bacheca se vengono rispettati i controlli di identità
     *
     * @requires: passw = this.Masterpassw, dato != null, dato appartenente alla board, passw != null
     * @modifies: nothing
     * @effects:  ritorna una copia del dato
     * @throws: InvalidActionException, NullPointerException, IncorrectPasswordException
     */

    public E get(String passw, E dato)
            throws InvalidActionException, NullPointerException, IncorrectPasswordException;



//------------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che rimuove e restituisce un dato della bacheca
     *
     * @requires: passw = this.MasterPassw, dato != null, dato appartenente alla board, passw != null
     * @modifies: this
     * @effects: rimuove e restituisce il dato dalla categoria
     * @throws: IncorrectPasswordException, InvalidActionException, NullPointerException
     */

    public E remove(String passw, E dato)
            throws IncorrectPasswordException, InvalidActionException, NullPointerException;


//--------------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna una lista che contiene tutti gli elementi di una categoria
     *
     * @requires: passw = this.Masterpassw, passw != null, category != null, category appartiene a categories
     * @modifies: nothing
     * @effects: ritorna i dati appartenenti a category
     * @throws:  IncorrectPasswordException, NullPointerException, InvalidActionException
     *
     */

    public List<E> getDataCategory(String passw, String category)
            throws IncorrectPasswordException, NullPointerException, InvalidActionException;


//--------------------------------------------------------------------------------------------------------------------

    /**
     *  Metodo che restituisce un iteratore (senza remove) che genera tutti i dati in bacheca ordinati rispetto al
     *  numero di like.
     *
     * @requires: passw = this.MasterPassw, passw != null
     * @modifies: nothing
     * @effects: restituisce un iteratore per i dati ordinati in base ai like
     * @throws: NullPointerException, IncorrectPasswordException
     *
     */

    public Iterator<E> getIterator(String passw)
            throws NullPointerException, IncorrectPasswordException;

//---------------------------------------------------------------------------------------------------------------------
    /**
     * Metodo che aggiunge un like da parte di un utente presente nella lista di amici che hanno accesso alla categoria
     * che contiene il dato
     *
     * @requires: friend != null, dato != null, friend appartiene alla lista amici della categoria del dato, dato
     *            presente nella board
     * @modifies: dato.likes, dato.numLikes
     * @effects: aggiungi friend a dato.likes, numLikes++
     * @throws: InvalidActionException, NullPointerException
     */

     void insertLike(String friend, E dato)
             throws InvalidActionException, NullPointerException;


//---------------------------------------------------------------------------------------------------------------------
    /**
     * Metodo che ritorna una copia di tutti i dati a cui ha accesso un amico
     *
     * @requires: friend != null
     * @modifies: nothing
     * @effects: ritorna un iteratore che mostra tutti i dati condivisi con 'friend'
     * @throws: NullPointerException, IllegalArgumentException
     *
     */

    public Iterator<E> getFriendIterator(String friend) throws NullPointerException;


 //--------------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che rimuove un like da un dato se l'amico ha accesso a quest'ultimo e se lo ha gia' messo in precedenza
     *
     * @requires: friend != null, dato != null, dato appartenente a board, friend appartenente a dato.friends
     * @modifies: this
     * @effects: rimuove friend da dato.likes , dato.numLikes--
     * @throws: InvalidActionException, NullPointerException
     */


    public void removeLike(String friend, Data dato)
            throws InvalidActionException,NullPointerException;

}