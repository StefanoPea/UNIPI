import java.util.ArrayList;
import java.util.HashMap;

public class card {

    /**
     * nome e descrizione della card
     */
    private String name, description;

    /**
     * lista in cui e' contenuta la card
     */
    private list card_list;

    public enum list{
        TODO,
        INPROGRESS,
        TOBEREVISED,
        FINISHED;

    }

    /**
     * lista che contiene la history della card
     */
    private ArrayList<String> card_history;


    /**
     * Costruttore della card
     * @param name nome della card
     * @param description descrizione della card
     */
    public card(String name, String description){
        this.name = name;
        this.description = description;
        this.card_list = list.TODO;
        this.card_history = new ArrayList<>();
        this.card_history.add("TODO");

    }


    /**
     * Metodo che converte una stringa in una lista
     *
     * @param lista stringa che indica la lista di riferimento
     * @return la lista corrispondente alla stringa
     */
    public list stringToList(String lista){

        switch (lista) {
            case "TODO":
                return list.TODO;
            case "INPROGRESS":
                return list.INPROGRESS;
            case "TOBEREVISED":
                return list.TOBEREVISED;
            case "FINISHED":
                return list.FINISHED;
            default:
                return null;
        }
    }


    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public list getCard_list() {
        return card_list;
    }

    public ArrayList<String> getCard_history() {
        return card_history;
    }


    /**
     * Setters
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setCard_list(list card_list) {
        this.card_list = card_list;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCard_history(ArrayList<String> card_history) {
        this.card_history = card_history;
    }


}
