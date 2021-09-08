import java.io.Serializable;

public class user implements Serializable {

    /**
     * stato dell'utente
     */
    public enum status{
        ONLINE,
        OFFLINE
    }

    /**
     * nome e password associate all'utente
     */
    private  String name, password;


    /**
     * status dell'utente
     */
    private status user_status;


    /**
     * Costruttori della classe
     * @param name nome dell'utente
     * @param password password associata all'utente
     */
    public user(String name, String password){
        this.name = name;
        this.password = password;
        this.user_status = status.OFFLINE;
    }

    public user(){
        super();
    }

    /**
     * Getters
     */
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public status getStatus(){
        return this.user_status;
    }

    /**
     * Converte lo stato dell'utente in una stringa, utile per alcune operazioni
     * @return una stringa riferita allo stato dell'utente
     */
    public String getStatusString(){

        if(this.user_status == null){return "OFFLINE";}
        if(this.user_status.equals(status.ONLINE)) return "ONLINE";
        else return "OFFLINE";
    }

    /**
     * Setters
     */
    public void setUser_status(status user_status) {
        this.user_status = user_status;
    }

    public void setOnline(){
        this.user_status = status.ONLINE;
    }

    public void setOffline(){
        this.user_status = status.OFFLINE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
