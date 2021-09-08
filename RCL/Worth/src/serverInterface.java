import java.rmi.Remote;
import java.rmi.RemoteException;

public interface serverInterface extends Remote {

    /**
     * Metodo utilizzato per registrare un utente al servizio
     * @param nomeUtente nome dell'utente da registrare
     * @param password password dell'utente
     * @throws RemoteException
     */
    void register(String nomeUtente, String password) throws RemoteException;

    /**
     * Metodo utilizzato per controllare se e' presente un utente
     * @param nomeUtente nome dell'utente da ricercare
     * @return true se e' presente, false altrimenti
     * @throws RemoteException
     */
    boolean contains(String nomeUtente) throws RemoteException;

    /**
     * Metodo per registrarsi alle callback
     * @param ClientInterface
     * @throws RemoteException
     */
    void registerForCallback(notifyEventInterface ClientInterface) throws  RemoteException;

    /**
     * Metdo per disiscriversi dalle callback
     * @param ClientInterface
     * @throws RemoteException
     */
    void  unregisterForCallback  (notifyEventInterface ClientInterface) throws RemoteException;


}
