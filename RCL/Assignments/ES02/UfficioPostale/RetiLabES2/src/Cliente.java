// Per simulare i clienti dell'ufficio ho utilizzato l'implementazione di Task vista a lezione con qualche piccola
// modifica

public class Cliente implements Runnable {
    private String name;
    public Cliente(String name) {this.name=name;}
    public void run() {
        System.out.printf("%s:  %s preso in carico\n",
                Thread.currentThread().getName(),name);
        try{
            Long duration=(long)(Math.random()*10);
            System.out.printf("%s:  %s: Tempo di servizio %d secondi\n",
                    Thread.currentThread().getName(),name,duration);
            Thread.sleep(duration);
        }
        catch (InterruptedException e) {e.printStackTrace();}
        System.out.printf("%s: %s servito  \n",
                Thread.currentThread().getName(),name);
    }
}