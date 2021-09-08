import java.util.ArrayList;

public class cards {

    /**
     * Nome della lista di cards
     */
    private final String name;

    /**
     * Lista delle cards
     */
    private final ArrayList<card> cards;

    /**
     * numero di cards nella lista
     */
    private int nCards;


    /**
     * Costruttore della lista di cards
     * @param name nome della card
     *
     */
    public cards(String name){
        this.name = name;
        this.cards = new ArrayList<>();
        this.nCards = 0;
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public ArrayList<card> getCards() {
        return cards;
    }

    public int getnCards() {
        return nCards;
    }


    /**
     * Metodo per aggiungere una card alla lista
     * @param card card da aggiungere alla lista
     */
    public void addCard(card card){
        this.cards.add(card);
        nCards++;
    }

    
    /**
     * Metodo per rimuovere una card dalla lista
     * @param card card da rimuovere dalla lista
     */
    public void removeCard(card card){
        this.cards.remove(card);
        nCards--;
    }

}
