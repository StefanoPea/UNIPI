import java.util.ArrayList;

public class projects {

    /**
     * Lista di tutti i progetti
     */
    public ArrayList<project> projects;

    /**
     * Costruttore della lista
     */
    public projects(){
        this.projects = new ArrayList<>();
    }

    /**
     * Getter
     * @return la lista dei progetti
     */
    public ArrayList<project> getProjects() {
        return projects;
    }

    /**
     * Aggiunge un progetto alla lista
     * @param prog progetto da aggiungere
     */
    public void add_prog(project prog){
        this.projects.add(prog);
    }

    /**
     * Ricerca un progetto tramite il suo nome
     * @param prog_name nome del progetto da ricercare
     * @return il progetto richiesto, oppure null se non presente
     */
    public project get_prog(String prog_name){
        for(project p : this.projects){
            if(p.getName().equals(prog_name)){
                return p;
            }
        }
        return null;
    }

    /**
     * Metodo personalizzato per controllare se esite un progetto con nome 'progName'
     * @param progName nome del progetto da ricercare
     * @return true se esiste, false altrimenti
     */
    public boolean myContains(String progName){
        for (project p:
             this.projects) {
            if (p.getName().equals(progName)){
                return true;
            }
        }
        return false;
    }

}

