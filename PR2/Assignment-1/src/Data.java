import java.util.ArrayList;


public class Data implements Comparable<Data>{

    /**
     *
     *
     *  Tipical Element:    < Owner, Category, Post, numLike, ArrayList<String> likes >
     *                      in cui:
     *                          -Owner        = colui che ha creato il dato (da specificare durante la creazione)
     *                          -Category     = categoria a cui appartiene il dato (inizialmente null)
     *                          -Post         = contenuto vero e proprio del dato in forma di stringa (da specificare
     *                                          durante la creazione)
     *                          -numLike      = numero di like associati al dato
     *                          -likes        = lista contenente tutti gli amici che hanno messo like al dato
     *
     *
     *  Rep Invariant:      Owner != null,
     *                      Post != null,
     *                      (numLike = likes.size()) >= 0,
     *                      forall (element in likes) => element != null
     */

    private String Owner;
    private String Category;
    private String Post;
    private int numLike;
    private ArrayList<String> likes;

    //-----------------------------------------------------------------------------------------------------

    /**
     * Costruttore della classe Data
     *
     * @requires:   owner != null, post != null
     * @modifies:   this
     * @effects:    this.Owner = owner
     *              this.Post = post;
     *              this.Category = null;
     *              this.likes = new ArrayList<String>();
     *              this.numLike = 0;
     * @throws:     NullPointerException
     */


    public Data(String owner, String post) throws NullPointerException{

        if(owner == null || post == null) throw new NullPointerException();
        this.Owner = owner;
        this.Post = post;
        this.Category = null;
        this.likes = new ArrayList<>();
        this.numLike = 0;
    }

    //------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna il numero di like di un post
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return numLike
     * @throws: nothing
     */

    public int GetLike(){

        return numLike;
    }

    //----------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna la categoria a cui appartiene il post
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return Category (se il dato non e' ancora stato inserito in una Board ci si aspetta null)
     * @throws: nothing
     */

    public String GetCategory(){

        return Category;
    }

    //---------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna il proprietario del dato
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return Owner
     * @throws: nothing
     */

    public String GetOwner(){

        return Owner;
    }

    //----------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna una lista di amici che ha messo like ad un post
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return likes
     * @throws: nothing
     */

    public ArrayList<String> GetLikes(){

        return new ArrayList<>(likes);
    }

    //------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna il contenuto del post
     *
     * @requires: nothing
     * @modifies: nothing
     * @effects: return Post
     * @throws: nothing
     */

    public String Display(){

        return this.Post;
    }


    //-------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che permette ad un amico di aggiungere un like al dato
     *
     * @requires:   friend nella lista amici che hanno accesso alla categoria e friend != null,
     *              friend non presente nella lista di chi ha gia' messo like
     * @modifies:   this
     * @effects:    this.numLike++, aggiunge friend alla lista degli amici che hanno messo like.
     * @throws:     InvalidActionException, NullPointerException.
     */

    public void insertlike(String friend)
            throws InvalidActionException, NullPointerException{

        if(friend == null) throw new NullPointerException();
        if(likes.contains(friend)) throw new InvalidActionException("Hai gia' messo like a questo elemento");
        this.numLike++;
        this.likes.add(friend);
    }

    //------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che permette ad un amico di rimuovere un like al dato
     *
     * @requires: friend nella lista amici e != null, friend presente nella lista di
     *            chi ha gia' messo like
     * @modifies: this
     * @effects: this.numLike--, rimuove friend dalla lista degli amici che hanno messo like.
     * @throws:  InvalidActionException, NullPointerException.
     */

    public void removeLike(String friend)
            throws InvalidActionException, NullPointerException{

        if(friend == null) throw new NullPointerException();
        if(!likes.contains(friend)) throw new InvalidActionException("Non hai ancora messo like a questo elemento");
        this.numLike--;
        this.likes.remove(friend);
    }

    //---------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che ritorna una copia del dato
     *
     * @requires: dato != null
     * @modifies: nothing
     * @effects: this.likes = dato.likes;
     *           this.numLike = dato.numLike;
     *           this.Owner = dato.Owner;
     *           this.Category = dato.Category;
     *           this.likes = dato.likes;
     * @throws: NullPointerException
     */

    public Data(Data dato) throws NullPointerException{

        if(dato == null) throw new NullPointerException();
        this.likes = dato.likes;
        this.numLike = dato.numLike;
        this.Owner = dato.Owner;
        this.Category = dato.Category;


    }

    //--------------------------------------------------------------------------------------------------------------

    /**
     * Metodo usato per comparare due oggetti di tipo Data
     *
     * @requires: data != null
     * @modifies: nothing
     * @effects: return -(numLike - data.numLike);
     * @throws: NullPointerException
     */

    @Override
    public int compareTo(Data dato) throws NullPointerException{

        if(dato == null) throw new NullPointerException();
        return -(numLike - dato.numLike);
    }

    //---------------------------------------------------------------------------------------------------------------

    /**
     * Metodo che imposta la categoria del dato
     *
     * @requires: categoria != null
     * @modifies: this
     * @effects: this.Category = categoria
     * @throws: NullPointerException
     */

    public void setCategory(String categoria) throws NullPointerException{

       if(categoria == null) throw new NullPointerException();
       this.Category = categoria;
    }
}
