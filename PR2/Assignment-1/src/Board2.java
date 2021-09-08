import java.util.*;


public class Board2  <E extends Data> implements DataBoard<E> {


	/**
	 *
	 * Tipical Element: <MasterPassw, Owner, numCategories, {name_0,..., name_n}, {category_0,..., category_n}>
	 *
	 * Rep Invariant:   Masterpassw != null,
     *                  Owner != null,
     *                  categories != null
     *                  (numCategory == categories.size() == names.size() ) >= 0
     *                  (forall category in this.categories) ==> category != null
     *					(forall name in this.names) ==> name != null
     */

    private final String MasterPassw;
    private final String Owner;
    private ArrayList<String> names;
    private ArrayList<Category<E>> categories;
    private int numCategories;

    //-------------------------------------------------------------------------------------------------------------

    /** Metodo costruttore */

    public Board2(String owner, String Masterpassw) throws NullPointerException, IllegalArgumentException{

        if(owner == null || Masterpassw == null) throw new NullPointerException();
        this.MasterPassw = Masterpassw;
        this.Owner = owner;
        this.names = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.numCategories = 0;
    }

    //-------------------------------------------------------------------------------------------------------------

    /** Metodo che ritorna l'owner della board */

    public String getOwner() {

        return Owner;
    }

    //---------------------------------------------------------------------------------------------------------------


    /** Metodo che ritorna il numero di categorie presenti nella board */

    public int getNumCategories() {

        return numCategories;
    }




    //----------------------------------------------------------------------------------------------------------------


    /** Metodo che crea una nuova categoria all'interno della bacheca */

    public void createCategory(String name, String passw)
            throws InvalidActionException, IncorrectPasswordException, NullPointerException{

        if(name == null || passw == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(names.contains(name)) throw new InvalidActionException("Categoria gia' presente");

        names.add(name);
        Category<E> cat = new Category<>();
        categories.add(cat);
        numCategories++;
    }


   //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che ritorna la lista degli amici che hanno accesso alla categoria "category"*/

    public ArrayList<String> getFriends(String category)
            throws InvalidActionException, NullPointerException, IllegalArgumentException{

        if(category == null) throw new NullPointerException();
        if(!names.contains(category)) throw new InvalidActionException("Categoria non presente");
        return new ArrayList<String>(categories.get(names.indexOf(category)).getFriends());
    }

    //-----------------------------------------------------------------------------------------------------------------

    /** Metodo che rimuove una categoria presente nella board */

    public void removeCategory(String name, String passw)
            throws InvalidActionException, IncorrectPasswordException, NullPointerException{

        if(name == null || passw == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(!names.contains(name)) throw new InvalidActionException("Categoria non presente");
        categories.remove(names.indexOf(name));
        names.remove(name);
        numCategories--;
    }


    //-----------------------------------------------------------------------------------------------------------------



    /** Metodo che aggiunge un amico ad una categoria */

    public void addFriend(String nameCat, String passw, String friend)
            throws IncorrectPasswordException, InvalidActionException, NullPointerException{

        if(nameCat == null || passw == null || friend == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(!names.contains(nameCat)) throw new InvalidActionException("Categoria non presente");
        if(categories.get(names.indexOf(nameCat)).getFriends().contains(friend))
            throw new InvalidActionException("Amico gia' presente");
        categories.get(names.indexOf(nameCat)).addFriend(friend);
    }


    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che rimuove un amico da una categoria */

    public void removeFriend(String nameCat, String passw, String friend)
            throws IncorrectPasswordException, InvalidActionException, NullPointerException, IllegalArgumentException{

        if(nameCat == null || passw == null || friend == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(!names.contains(nameCat)) throw new InvalidActionException("Categoria non presente");
        if(categories.get(names.indexOf(nameCat)).getFriends().contains(friend))
            throw new InvalidActionException("Amico gia' presente");
        categories.get(names.indexOf(nameCat)).removeFriend(friend);
    }


    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che aggiunge un dato in Bacheca */

    public boolean put(String passw, E dato, String categoria)
            throws IllegalArgumentException, InvalidActionException, NullPointerException, IncorrectPasswordException{

        if(passw == null || dato == null || categoria == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(!names.contains(categoria)) throw new InvalidActionException("Categoria non presente");
        if(categories.get(names.indexOf(categoria)).getDati().contains(dato))
            throw new InvalidActionException("dato gia' presente");
        categories.get(names.indexOf(categoria)).addDato(dato);
        dato.setCategory(categoria);
        return true;
    }

    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che ottiene una copia del dato in bacheca*/

    public E get(String passw, E dato) throws IncorrectPasswordException, InvalidActionException, NullPointerException{

        if(passw == null || dato == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(dato.GetCategory() == null) throw new InvalidActionException("Dato non presente nella Board");
        E aux = null;
        for(Category<E> i : categories) {
            if(i.getDati().contains(dato)) aux = dato;
        }
        if (aux == null) throw new InvalidActionException("Dato non presente nella board");
        return aux;

    }

    //-----------------------------------------------------------------------------------------------------------------

    /** Metodo che rimuove un dato dalla bacheca */

    public E remove(String passw, E dato)
            throws IncorrectPasswordException, InvalidActionException, NullPointerException{

        if(passw == null || dato == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        if(dato.GetCategory() == null) throw new InvalidActionException("Dato non presente nella Board");
        E aux = null;
        for(Category<E> i : categories) {
            if(i.getDati().contains(dato)){
                aux = dato;
                i.removeDato(dato);
            }
        }
        if (aux == null) throw new InvalidActionException("Dato non presente nella board");
        return aux;
    }


    //----------------------------------------------------------------------------------------------------------------


    /** Crea la lista dei dati in bacheca contenuta in una determinata categoria */

    public List<E> getDataCategory(String passw, String category)
            throws IncorrectPasswordException, NullPointerException, InvalidActionException{

        if(passw == null || category == null) throw new NullPointerException();
        if(!names.contains(category)) throw new InvalidActionException("Categoria non presente");
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        Category<E> aux = this.categories.get(names.indexOf(category));
        return aux.getDati();
    }


    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che ritorna una copia di tutti i dati in bacheca in ordine di like */

    public Iterator<E> getIterator(String passw)
            throws NullPointerException, IncorrectPasswordException{

        if(passw == null) throw new NullPointerException();
        if(!this.MasterPassw.equals(passw)) throw new IncorrectPasswordException("Password Errata");
        List<E> aux = new ArrayList<>();
        for (Category<E> cat : categories) {
            aux.addAll(cat.getDati());
        }
        Collections.sort(aux);
        return aux.iterator();
    }


    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che aggiunge un like da parte di un utente nella lista amici */

    public void insertLike(String friend, Data dato)
            throws InvalidActionException,NullPointerException{

        if(dato == null || friend == null) throw new NullPointerException();
        if(dato.GetCategory() == null) throw new InvalidActionException("Dato non presente nella Board");
        if(!categories.get(names.indexOf(dato.GetCategory())).getFriends().contains(friend)) throw new
                InvalidActionException("Non puoi visualizzare questo dato");

        boolean found = false;
        for (Category<E> cat: categories) {
            if(cat.getDati().contains(dato)) found = true;
        }
        if(!found)throw new InvalidActionException("dato non presente");
        if(dato.GetLikes().contains(friend))
            throw new InvalidActionException("Hai gia' messo like a questo post");
        dato.insertlike(friend);
    }


    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che ritorna una copia non modificabile di tutti i dati a cui ha accesso un amico */

    public Iterator<E> getFriendIterator(String friend) throws NullPointerException{


        if(friend == null) throw new NullPointerException();
        List<E> aux = new ArrayList<>();
        for (Category<E> cat : categories) {
            if(cat.getFriends().contains(friend)) {
                aux.addAll(cat.getDati());
            }
        }
        return aux.iterator();
    }


    //-----------------------------------------------------------------------------------------------------------------


    /** Metodo che rimuove un like assegnato ad un dato da un utente sulla lista amici */

    public void removeLike(String friend, Data dato)
            throws NullPointerException, InvalidActionException {

        if(dato == null || friend == null) throw new NullPointerException();
        if(dato.GetCategory() == null) throw new InvalidActionException("Dato non presente nella Board");
        if(!categories.get(names.indexOf(dato.GetCategory())).getFriends().contains(friend))
            throw new InvalidActionException("Non puoi visualizzare questo dato");
        boolean found = false;
        for (Category<E> cat: categories) {
            if(cat.getDati().contains(dato)) found = true;
        }
        if(!found) throw new InvalidActionException("dato non presente");
        if(!dato.GetLikes().contains(friend))
            throw new InvalidActionException("Non hai ancora messo like a questo dato");
        dato.removeLike(friend);
    }

}
