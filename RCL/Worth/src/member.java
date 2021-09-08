public class member {

    /**
     * Nome del membro
     */
    String name;

    /**
     * Costruttori della classe
     * @param name nome da assegnare al membro del progetto
     */
    public member(String name){
        this.name = name;
    }

    public member(){
        super();
    }

    /**
     * Getter
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter
     * @param name nome da settare del membro
     */
    public void setName(String name) {
        this.name = name;
    }

}
