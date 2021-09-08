import java.io.Serializable;
import java.util.ArrayList;

public class request implements Serializable {

    /**
     * nome dell'utente che ha effettuato la richiesta e comando per il server
     */
    private String nick, command;

    /**
     * lista di argomenti necessari alla richiesta
     */
    private ArrayList<String> args;

    /**
     * Costruttore della request
     * @param nick nome dell'utente che ha effettuato la richiesta
     * @param command comando da eseguire
     * @param args argomenti necessari al comando
     */
    public request(String nick, String command, ArrayList<String> args){
        this.nick = nick;
        this.command = command;
        this.args = args;
    }

    /**
     * Getters
     */
    public String getCommand() {
        return command;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public String getNick() {
        return nick;
    }

    /**
     * Setters
     */
    public void setCommand(String command) {
        this.command = command;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

}
