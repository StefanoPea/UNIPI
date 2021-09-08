public class Produttore implements Runnable {

    String Directory;
    CommonQueue commonqueue;

    public Produttore(String Directory, CommonQueue commonqueue){
        this.Directory = Directory;
        this.commonqueue = commonqueue;
    }
    
    public void run(){
       commonqueue.AddToList(Directory);
    }

}
