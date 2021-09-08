import java.util.Iterator;



public class test_board_2 {

    private static final String USER_1 = "Giorno";
    private static final String USER_2 = "Bruno";
    private static final String USER_3 = "Robert";
    private static final String PW_1 = "buongiorno";
    private static final String PW_2 = "donuts";
    private static final String PW_3 = "Joestar";

    public static void main(String[] args) throws InvalidActionException, IncorrectPasswordException {

        DataBoard<Data> prima = new Board2<>(USER_1, PW_1);
        DataBoard<Data> seconda = new Board2<>(USER_2, PW_2);
        DataBoard<Data> terza = new Board2<>(USER_3, PW_3);

        Data dato1 = new Data(USER_1, "stringa di test");
        Data dato2 = new Data(USER_1, "ulteriore stringa di test");
        Data dato3 = new Data(USER_1, "un'alta...");
        Data dato4 = new Data(USER_1, " ma quante sono?");
        Data dato5 = new Data(USER_1, "sto finendo le idee...");
        Data dato6 = new Data(USER_1, "boh");
        Data dato7 = new Data(USER_1, "ok basta");


        //-----------------------------------------Test per le board--------------------------------------------------//

        System.err.println("\n-----TEST BOARD 2-----\n");



        // Creo una board con una password null, ottengo NullPointerException

        try{

            Board2<Data> aux1 = new Board2<>(USER_1, null);

        } catch (NullPointerException e) {
            System.err.println("- Creo una board con una password null, ottengo NullPointerException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch (Exception e){
            System.err.println("ERROR:   " + e);
        }


        // Creo una Board con nome utente null

        try{

            Board2<Data> aux1 = new Board2<>(null, "test");

        } catch (NullPointerException e) {
            System.err.println("- Creo una board con nome utente null, ottengo NullPointerException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch (Exception e){
            System.err.println("ERROR:   " + e);
        }


        //-----------------------------------------Test per le categorie----------------------------------------------//

        System.err.println("\n-----TEST CATEGORIE-----\n");

        prima.createCategory("prima",PW_1);
        prima.createCategory("seconda",PW_1);

        seconda.createCategory("prima", PW_2);
        seconda.createCategory("seconda", PW_2);

        terza.createCategory("prima", PW_3);

        prima.addFriend("prima", PW_1, "Mista");

        prima.put(PW_1, dato1, "prima");
        prima.put(PW_1, dato3, "prima");
        prima.put(PW_1, dato5, "seconda");

        // Inserisco una categoria con nome null, ottengo NullPointerException

        try{

            prima.createCategory(null, "PW_1");
        } catch (NullPointerException e){
            System.err.println("- Creo una categoria con nome null, ottengo NullPointerException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch (Exception e){
            System.err.println("ERROR:   " + e);
        }

        // Inserisco una categoria con una password sbagliata, ottengo IncorrectPasswordException

        try{

            prima.createCategory("wow", "PW_2");
        } catch (IncorrectPasswordException e){
            System.err.println("- Inserisco una categoria con una password sbagliata, ottengo IncorrectPasswordException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch (Exception e){
            System.err.println("ERROR:   " + e);
        }

        // Inserisco una categoria con password null, ottengo NullPointerException

        try{

            prima.createCategory("wow", null);
        } catch (NullPointerException e){
            System.err.println("- Inserisco una categoria con password null, ottengo NullPointerException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch (Exception e){
            System.err.println("ERROR:   " + e);
        }

        // creo una categoria con lo stesso nome di un'altra, ottengo InvalidActionException

        try{

            prima.createCategory("prima", PW_1);
        } catch (InvalidActionException e){
            System.err.println("- Creo una categoria con lo stesso nome di un'altra, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch (Exception e){
            System.err.println("ERROR:   " + e);
        }

        // rimuovo una categoria non presente nella board, ottengo InvalidActionException

        try{

            prima.removeCategory("terza", PW_1);
        } catch(InvalidActionException e){
            System.err.println("- Rimuovo una categoria non presente nella board, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        //-----------------------------------TEST PER GLI AMICI--------------------------------------------
        System.err.println("\n-----TEST AMICI-----\n");

        //Aggiungo un amico alla categoria che e' gia' presente in quest'ultima

        try{

            prima.addFriend("prima", PW_1, "Mista");
        } catch(InvalidActionException e){
            System.err.println("- Aggiungo un amico alla categoria in cui e' gia' presente, ottengo InvalidActionException ");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        //Rimuovo un amico non presente nella categoria

        try{

            prima.removeFriend("prima", PW_1, "Diavolo");
        } catch(InvalidActionException e){
            System.err.println("- Rimuovo un amico non presente nella categoria, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        // Cerco di ottenere la lista di amici che hanno accesso ad una Board inesistente, ottengo InvalidActionExeption

        try{

            prima.getFriends("super");
        } catch(InvalidActionException e){
            System.err.println("- Cerco di ottenere la lista di amici che hanno accesso ad una Board inesistente, ottengo InvalidActionExeption");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        //Rimuovo un amico da una categoria non esistente

        try{

            prima.removeFriend("super", PW_1, "Mista");
        } catch(InvalidActionException e){
            System.err.println("- Rimuovo un amico da una categoria non esistente, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        //-------------------------------TEST SUI DATI-----------------------------------------------------------
        System.err.println("\n-----TEST DATI-----\n");

        // inserisco un dato gia' presente nella categoria

        try{

            prima.put(PW_1, dato1, "prima");
        } catch(InvalidActionException e){
            System.err.println("- Inserisco un dato gia' presente nella categoria, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        // ottengo un dato non presente in board, ottengo InvalidActionException

        try{

            prima.get(PW_1, dato2);
        } catch(InvalidActionException e){
            System.err.println("- Ottengo un dato non presente in board, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        // rimuovo un dato non presente nella board

        try{
            prima.remove(PW_1, dato4);
        } catch(InvalidActionException e){
            System.err.println("- Rimuovo un dato non presente nella board, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        // cerco di ottenere la lista dei dati presenti in una bacheca non esistente, ottengo InvalidActionException

        try{
            prima.getDataCategory(PW_1, "super");
        } catch(InvalidActionException e){
            System.err.println("- Cerco di ottenere la lista dei dati presenti in una bacheca non esistente, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        //-------------------------------------TEST LIKE-----------------------------------------------------------
        System.err.println("\n-----TEST LIKE-----\n");

        // un utente che non ha accesso alla board che contiene il dato prova a mettere like , ottengo InvalidActionException

        try{
            prima.insertLike("Diavolo", dato1);
        } catch(InvalidActionException e){
            System.err.println("- Un utente che non ha accesso alla board che contiene il dato prova a mettere like, ottengo InvalidActionException ");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        // un utente che ha accesso al dato prova a mettere like dopo averlo gia' messo, ottengo InvalidActionException

        prima.insertLike("Mista", dato1);

        try{
            prima.insertLike("Mista", dato1);
        } catch(InvalidActionException e){
            System.err.println("- Un utente che ha accesso al dato prova a mettere like dopo averlo gia' messo, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        // un utente che ha accesso al dato prova a rimuovere un like prima di averlo messo, ottengo InvalidActionException

        prima.removeLike("Mista", dato1);

        try{
            prima.removeLike("Mista", dato1);
        } catch(InvalidActionException e){
            System.err.println("- Un utente che ha accesso al dato prova a rimuovere un like prima di averlo messo, ottengo InvalidActionException");
            System.err.println("OK:   " + e);
            System.err.println();

        } catch(Exception e){
            System.err.println("ERROR:   " + e);
        }

        //----------------------------------TEST ITERATORI---------------------------------------------------------

        // test getIterator

        System.out.println("\n-----TEST ITERATORE BACHECA 2-----\n");
        Iterator<Data> aux = prima.getIterator(PW_1);
        while(aux.hasNext()) {
            Data el = aux.next();
            System.out.println(el.Display());
        }

        // test getFriendIterator

        System.out.println("\n-----TEST ITERATORE AMICO-----\n");
        aux = prima.getFriendIterator("Mista");
        while(aux.hasNext()) {
            Data el = aux.next();
            System.out.println(el.Display());
        }

    }
}
