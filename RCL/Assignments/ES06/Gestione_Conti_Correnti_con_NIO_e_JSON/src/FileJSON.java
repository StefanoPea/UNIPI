import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FileJSON {

    // Tutti i possibili nomi degli utenti
    enum Nomi{
        Will, Issac, Mason, Titus, Kenton, Houston, Tobias, Riley, Eldridge, Josue, Ron, Malcolm, Gregory, Kurt, Cruz,
        Leroy, Dee, Ollie, Chauncey, Chana, Evelyne, Christie, Ricarda, Aurora, Mavis, Glayds, Cristal, Ariana, Maisha,
        Hassie, Pilar, Albertha, Indira, Kazuko, Versie, Anjelica, Sherlyn, Annett, Johana;

        // Metodo che ritorna un elemento casuale della lista
        public static FileJSON.Nomi getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    public FileJSON(){

        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File("/home/stefano/Desktop/test.json");

        Conto_Corrente_List list = new Conto_Corrente_List();

        LinkedList<Conto_Corrente> aux = new LinkedList<>();

        Random rand = new Random();

        LocalDate from = LocalDate.of(2018, 1, 1);
        LocalDate to = LocalDate.now();
        long days = from.until(to, ChronoUnit.DAYS);

        // BOUND IMPOSTATO DI DEFAULT A 20
        for (int i = 0; i <= rand.nextInt(20); i++) {

            Conto_Corrente conto = new Conto_Corrente();
            conto.setIntestatario(FileJSON.Nomi.getRandom().toString());

            for (int j = 0; j < rand.nextInt(20); j++) {

                // Generazione data casuale
                long randomDays = ThreadLocalRandom.current().nextLong(days + 1);
                LocalDate randomDate = from.plusDays(randomDays);

                // Creazione dei movimenti del conto
                Movimento_Conto movimento = new Movimento_Conto();
                movimento.setCausale(Movimento_Conto.Causale.getRandom());
                movimento.setDate(randomDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                conto.addMovimento(movimento);
            }

            aux.add(conto);
        }

        list.setConti(aux);

        try {
            objectMapper.writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

