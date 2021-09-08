import java.util.ArrayList;

public class chatAddress {

    /**
     * interi che compongono un indirizzo multicast
     */
    private int int1, int2, int3, int4;

    /**
     * lista degli indirizzi generati
     */
    private final ArrayList<String> generatedAddresses;

    /**
     * porta su cui mettersi in ascolto
     */
    private int port;

    /**
     * costruttore della classe
     */
    public chatAddress(){
        this.int1 = 224;
        this.int2 = this.int3 = this.int4 = 0;
        this.generatedAddresses = new ArrayList<>();
        this.port = 4000;
    }


    /**
     * metodo per ritornare una porta
     * @return la prossima porta disponibile
     */
    public int returnPort(){
        this.port++;
        return this.port;
    }


    /**
     * Metodo per ritornare un indirizzo
     * @return il prossimo indirizzo disponibile
     */
    public String returnAddress(){
        StringBuilder address = new StringBuilder();
        address.append(int1).append(".").append(int2).append(".").append(int3).append(".").append(int4);

        generatedAddresses.add(address.toString());

        if(int4 < 255){
            int4++;
        }
        else if(int3 < 255){
            int3++;
            int4 = 1;
        }
        else if(int2 < 255){
            int4 = 1;
            int3 = 1;
            int2++;
        }
        else if(int1<239){
            int4 = 1;
            int3 = 1;
            int2 = 1;
            int1++;
        }

        return address.toString();
    }


    /**
     * Getter
     */
    public ArrayList<String> getGeneratedAddresses() {
        return generatedAddresses;
    }
}
