import java.util.Random;

public class Utente implements Runnable {

    public int IndiceUtente;
    public String Tipologia;
    public int PcAssegnato = -1;
    private final int NumAccessi;
    private final Tutor tutor;


    public Utente(int NumPC, String tipo, Tutor Tutor, int ind){
        Random rand = new Random();
        tutor = Tutor;
        Tipologia = tipo;
        NumAccessi = rand.nextInt(20); //Limite massimo di accessi arbitrario
        IndiceUtente = ind;
        if(tipo.equals("Tesista")) PcAssegnato = rand.nextInt(NumPC);
    }

    public void run()
    {
        Random rand = new Random();
        for (int i = 0; i < NumAccessi; i++){

            if(this.Tipologia.equals("Professore")) {
                System.out.printf("%s %d ha richiesto il laboratorio\n",this.Tipologia, this.IndiceUtente);
            } else if(this.Tipologia.equals("Tesista")){
                System.out.printf("%s %d richiede il computer %d\n", this.Tipologia, this.IndiceUtente,this.PcAssegnato);
            } else {
                System.out.printf("%s %d richiede un computer\n", this.Tipologia, this.IndiceUtente);
            }


            tutor.RichiediPC(this);

            if(this.Tipologia.equals("Professore")) {
                System.out.printf("%s %d ha ottenuto il laboratorio \n",this.Tipologia, this.IndiceUtente);
            } else if(this.Tipologia.equals("Tesista")){
                System.out.printf("%s %d ha ottenuto il computer %d\n", this.Tipologia, this.IndiceUtente,this.PcAssegnato);
            } else{
                System.out.printf("%s %d ha ottenuto un computer \n", this.Tipologia, this.IndiceUtente);
            }

            int TempoAttesa = rand.nextInt(50); // limite massimo arbitrario di tempo al pc

            try {
                Thread.sleep(TempoAttesa);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tutor.RilasciaPC(this);

            if(this.Tipologia.equals("Professore")) {
                System.out.printf("%s %d ha terminato di utilizzare il laboratorio dopo %ds \n",this.Tipologia, this.IndiceUtente,TempoAttesa );
            } else {
                System.out.printf("%s %d ha terminato di utilizzare il computer dopo %ds \n", this.Tipologia, this.IndiceUtente, TempoAttesa);
            }

            //Periodo di attesa tra un accesso e l'altro al laboratorio
            try {
                Thread.sleep(rand.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}