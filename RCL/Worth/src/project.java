public class project{

    /**
     * Nome ddel progetto e chat associata
     */
    private final String name, chat_address;

    /**
     * porta multicast relativa al progetto
     */
    private int port;

    /**
     * liste delle cards
     */
    private cards todo, inprogress, toberevised, finished;

    /**
     * lista dei membri del progetto
     */
    private members members;


    /**
     * Costruttore del progetto
     * @param name nome associato al progetto
     * @param chat_address indirizzo della chat
     * @param port porta associata al progetto
     */
    public project(String name, String chat_address, int port) {
        this.name = name;
        this.chat_address = chat_address;
        this.todo = new cards("TODO");
        this.inprogress = new cards("INPROGRESS");
        this.toberevised = new cards("TOBEREVISED");
        this.finished = new cards("FINISHED");
        this.members = new members();
        this.port = port;

    }


    /**
     * Getters
     */
    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public cards getSet(card.list list){
        switch (list) {
            case TODO:
                return this.todo;
            case INPROGRESS:
                return this.inprogress;
            case TOBEREVISED:
                return this.toberevised;
            case FINISHED:
                return this.finished;
        }
        return null;
    }

    public members getMembers() {
        return this.members;
    }

    public cards getFinished() {
        return finished;
    }

    public cards getInprogress() {
        return inprogress;
    }

    public cards getToberevised() {
        return toberevised;
    }

    public cards getTodo() {
        return todo;
    }

    public String getChat_address() {
        return chat_address;
    }

    public card getCard(String cardName){
        for(card c : this.todo.getCards()){
            if(c.getName().equals(cardName)){
                return c;
            }
        }
        for(card c : this.toberevised.getCards()){
            if(c.getName().equals(cardName)){
                return c;
            }
        }
        for(card c : this.inprogress.getCards()){
            if(c.getName().equals(cardName)){
                return c;
            }
        }
        for(card c : this.finished.getCards()){
            if(c.getName().equals(cardName)){
                return c;
            }
        }
        return  null;
    }


    /**
     * Setters
     */
    public void setPort(int port) {
        this.port = port;
    }

    public void setMembers(members members) {
        this.members = members;
    }

    public void setFinished(cards finished) {
        this.finished = finished;
    }

    public void setInprogress(cards inprogress) {
        this.inprogress = inprogress;
    }

    public void setToberevised(cards toberevised) {
        this.toberevised = toberevised;
    }

    public void setTodo(cards todo) {
        this.todo = todo;
    }


    /**
     * Aggiunge una nuova card al progetto direttamente nella lista TODO
     * @param card
     */
    public void addCard(card card){
        this.todo.addCard(card);
    }

}
