import java.util.Calendar;

public class PICalc implements Runnable{

    private double  accuracy;
    private int maxT;

    public PICalc(double accuracy, int maxT){
        this.accuracy = accuracy;
        this.maxT = maxT;
    }

    public void run(){
        int i = 0;
        double pi = 0;
        long tstart = Calendar.getInstance().getTimeInMillis();
        while(Math.abs((pi*4) - Math.PI) > accuracy && Calendar.getInstance().getTimeInMillis() - tstart < maxT){
            pi += Math.pow(-1, i) / (2 * i + 1);
            i++;
        }
        pi = pi * 4;
        System.out.println(pi);

    }

}
