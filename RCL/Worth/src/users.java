import java.util.ArrayList;

public class users {

        /**
         * Lista degli utenti del sistema
         */
        public ArrayList<user> users;

        /**
         * Costruttore della classe
         */
        public users(){
                this.users = new ArrayList<>();
        }


        /**
         * Metodo personalizzato per controllare se un utente appartiene alla lista
         * @param name nome dell'utente da ricercare
         * @return true se esiste, false altrimenti
         */
        public boolean mycontain(String name){
                for (user u:this.users) {
                        if (u.getName().equals(name)) return true;
                }
                return false;
        }


        /**
         * Getters
         */
        public ArrayList<user> getUsers() {
                return users;
        }

        public user getUser(String name){
                for (user u:
                     users) {
                        if (u.getName().equals(name)){
                                return u;
                        }

                }
        return null;
        }


        /**
         * Aggiunge un utente alla lista
         * @param user utente da aggiungere
         */
        public void addUser(user user){
                this.users.add(user);
        }

        /**
         * Setter
         */
        public void setUsers(ArrayList<user> users) {
                this.users = users;
        }
}
