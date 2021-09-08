import java.rmi.RemoteException;
import java.util.ArrayList;

public class CongressoImpl implements IntCongresso {

    public ArrayList<String[][]> evento;
    String[][] giornata1;
    String[][] giornata2;
    String[][] giornata3;
    public int postiLiberi;

    CongressoImpl() throws RemoteException{

        evento = new ArrayList<>();


        giornata1 = new String[12][5];
        giornata2 = new String[12][5];
        giornata3 = new String[12][5];

        postiLiberi = 180;

        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 5; j++) {

                giornata1[i][j] = " _ ";
                giornata2[i][j] = " _ ";
                giornata3[i][j] = " _ ";


            }
        }

        evento.add(giornata1);
        evento.add(giornata2);
        evento.add(giornata3);

    }



    public String prenota(int giornata, int sessione, int posto, String relatore) throws RemoteException {

        String result;

        if((giornata<1||giornata>3) || (sessione<1||sessione>12)||(posto<1||posto>5)){
            result = "Parametri non corretti";
            return result;
        }

        if(postiLiberi == 0){
            result = "Nessun posto disponibile";
            return result;
        }

        if(giornata == 1){
            if(!giornata1[sessione-1][posto-1].equals(" _ ")){
                result = "posto gia' occupato";
                return result;

            }
            giornata1[sessione-1][posto-1] = relatore;
            postiLiberi--;
        }
        else if(giornata == 2){
            if(!giornata2[sessione-1][posto-1].equals(" _ ")){
                result = "posto gia' occupato";
                return result;

            }
            giornata2[sessione-1][posto-1] = relatore;
            postiLiberi--;

        }
        else {
        
            if(!giornata3[sessione-1][posto-1].equals(" _ ")){
                result = "posto gia' occupato";
                return result;
            }
            giornata3[sessione-1][posto-1] = relatore;
            postiLiberi--;


        }

        result = "operazione effettuata con successo";
        return result;
    }

    public String getCalendar(){

        StringBuilder calendar = new StringBuilder();
        int day = 1;

        for (String[][] aux2: evento) {
            calendar.append("Giornata ").append(day).append("\n\n");
            for(int i = 0; i < 12; i++) {
                calendar.append("Sessione ").append(i + 1).append("\n");
                for(int j = 0; j < 5; j++) {
                    calendar.append(" | ").append(aux2[i][j]);
                }
                calendar.append(" | \n");
            }
            calendar.append("\n");
            day++;
        }
        return calendar.toString();
    }

}

