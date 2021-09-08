import java.io.Serializable;
import java.util.ArrayList;

public class response implements Serializable {

    /**
     * codice relativo al successo o al fallimento della richiesta
     */
    private int status_code;

    /**
     * argomenti ricevuti dal server
     */
    ArrayList<String> args;

    /**
     * variabile utilizzata per ritornare indietro il numero di progetti su cui mettersi in ascolto per la chat
     * a seguito di un login
     */
    private int nProg;

    /**
     * lista che contiene in ordine <nomeProgetto, IndirizzoChat, porta> su cui mettersi in ascolto dopo un login
     */
    private ArrayList<String> addresses;


    /**
     * Costruttore della response
     * @param status_code codice relativo al successo o al fallimento della richiesta
     * @param args argomenti da rimandare indietro al client
     */
    public response(int status_code, ArrayList<String> args){
        this.args = new ArrayList<>(args);
        this.status_code = status_code;
    }


    /**
     * Getters
     */
    public ArrayList<String> getAddresses() {
        return addresses;
    }

    public int getnProg() {
        return nProg;
    }

    public int getStatus_code() {
        return status_code;
    }

    public ArrayList<String> getArgs() {
        return args;
    }


    /**
     * Setters
     */
    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public void setAddresses(ArrayList<String> addresses) {
        this.addresses = addresses;
    }

    public void setnProg(int nProg) {
        this.nProg = nProg;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }
}
