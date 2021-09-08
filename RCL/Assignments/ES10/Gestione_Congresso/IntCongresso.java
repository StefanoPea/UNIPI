import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IntCongresso extends Remote{

     String prenota(int giornata, int sessione, int posto, String relatore) throws RemoteException;

     String getCalendar() throws RemoteException;

}

