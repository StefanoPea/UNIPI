import java.rmi.Remote;
import java.rmi.RemoteException;

public interface notifyEventInterface extends Remote {


    /**
     * Metodo invocato dal  server per effettuare
     *  una callback a un client remoto.
     * @param user nome dell'utente
     * @param updt intero che indica se si tratta di un login o di un logout
     * @throws RemoteException
     */
    void notifyEvent(user user, int updt) throws RemoteException;
}
