public class Consumatore implements Runnable{

    CommonQueue commonqueue;

    public Consumatore(CommonQueue commonqueue){
        this.commonqueue = commonqueue;
    }

    public void run(){
        while(!commonqueue.queue.isEmpty() || !commonqueue.FineProduzione) {
            commonqueue.RemoveFromList();
        }
    }

}


