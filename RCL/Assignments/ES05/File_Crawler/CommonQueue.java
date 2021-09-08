import java.io.File;
import java.util.LinkedList;

public class CommonQueue {

    LinkedList<String> queue;
    public Boolean FineProduzione = false;

    public CommonQueue(LinkedList<String> queue){
        this.queue = queue;
    }

    //Metodo per aggiungere ricorsivamente alla lista tutte le sotto-directory contenute nella directory passata come argomento
    synchronized void AddToList(String Directory){
        File dir = new File(Directory);
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isDirectory()){
                queue.add(file.getAbsolutePath());
                AddToList(file.getAbsolutePath());
            }
        }
        this.FineProduzione = true;
    }

    //Metodo per rimuovere dalla lista un elemento e stampare tutti i file contenuti in quella directory
    synchronized void RemoveFromList(){
        if(!queue.isEmpty()){
            String aux;
            aux = queue.remove();
            File dir = new File(aux);
            File[] files = dir.listFiles();
            for(File file : files){
                if(file.isDirectory()){
                    System.out.println("Directory: " + file.getName() + "\n");
                } else{
                    System.out.println("File: " + file.getName() + "\n");
                }
            }
        }
    }

}


