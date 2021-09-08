import java.util.Scanner;


public class Main {
    public static void main(String[] args){

        Scanner input = new Scanner(System.in);
        double accuracy = input.nextDouble();

        Scanner input2 = new Scanner(System.in);
        int maxT = input2.nextInt();

        PICalc pc = new PICalc(accuracy,maxT);
        Thread thread = new Thread(pc);
        thread.start();


    }
}
