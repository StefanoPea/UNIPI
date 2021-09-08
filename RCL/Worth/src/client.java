import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class client implements notifyEventInterface{

    /**
     * Dimensione del buffer
     */
    private final int BUFFER_DIMENSION = 2056;


    /**
     * Comandi relativi alle varie operazioni
     */
    private final String EXIT_CMD = "exit";

    private final String REGISTER_CMD = "register";

    private final String SENDMSG_CMD= "sendMessage";

    private final String READMSGS_CMD = "readMessages";

    private final String LOGIN_CMD = "login";

    private final String LOGOUT_CMD = "logout";

    private final String CREATE_PROJ_CMD= "createProject";


    /**
     * utente loggato al momento
     */
    private String user;

    /**
     * porta su cui il server è in ascolto
     */
    private final int port;

    /**
     * Intero utilizzato per la chiusura del client, 0 se non e' in uscita, 1 se effettuo il logout prima di uscire,
     * 2 se esco
     */
    private int exit;


    /**
     * Costruttore della classe
     * @param port porta
     */
    public client(int port) {
        this.port = port;
        this.exit = 0;
        this.user = null;
    }


    /**
     * Byte array che contiene il messaggio da inviare alla chat
     */
    public byte[] buf;

    /**
     * Lista che contiene le varie informazioni per il multicast
     */
    private ArrayList<multicastThread> multicastSockets;


    /**
     * Lista locale degli utenti
     */
    private HashMap<String, String> localUsers;


    /**
     * Metodo start del client
     * @throws NotBoundException
     * @throws IOException
     */
    public void start() throws NotBoundException, IOException {

        multicastSockets = new ArrayList<>();

        this.localUsers = new HashMap<>();

        Registry registry = LocateRegistry.getRegistry(1099);
        serverInterface stub = (serverInterface) registry.lookup("ServerRMI");

        notifyEventInterface callbackObj =  this;
        notifyEventInterface stub2 = (notifyEventInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
        stub.registerForCallback(stub2);



        try (SocketChannel client = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), port))) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Client: connected");
            System.out.println("Type 'exit' to quit the program or 'help' for a list of commands:");

            while (this.exit != 2) {

                String msg = consoleReader.readLine().trim();
                request req;

                //gestisco la chiusura
                if (msg.equals(this.EXIT_CMD)) {
                    this.exit = 1;

                }


                StringTokenizer defaultTokenizer = new StringTokenizer(msg);
                String command = defaultTokenizer.nextToken();
                ArrayList<String> args = new ArrayList<>();


                // gestisco il login
                if(command.equals(this.LOGIN_CMD)){
                    this.user = defaultTokenizer.nextToken();
                    args.add(this.user);


                }

                // gestisco la registrazione
                if(command.equals(this.REGISTER_CMD)){
                    String name = defaultTokenizer.nextToken();
                    String psw = defaultTokenizer.nextToken();
                    args.add(name);
                    args.add(psw);

                    if(!stub.contains(name)){
                        stub.register(name, psw);
                    }
                    else {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add("ERROR: user already registered");
                        response resp = new response(10,tmp);
                        System.out.println("Server response: " + resp.getArgs());
                        continue;
                    }

                }

                //------------------------------------------------------------------------------------------

                if(command.equals("listUsers")){

                    System.out.println("User | Status");
                    System.out.println("-------------");
                    for (String name: this.localUsers.keySet()) {
                        String value = this.localUsers.get(name);
                        System.out.println(name + " | " + value);
                    }
                    continue;
                }

                if(command.equals("listOnlineUsers")){
                    System.out.println("User | Status");
                    System.out.println("-------------");
                    for (String name: this.localUsers.keySet()) {
                        String value = this.localUsers.get(name);
                        if(value.equals("ONLINE")){
                            System.out.println(name + " | " + value);

                        }
                    }
                    continue;
                }


                //--------------------------------------------------------------------------------------------



                // Gestione chiusura con logout
                if(this.exit == 2 && this.user != null){
                    req = new request(this.user, "exit", args);
                } else if(this.exit == 1 && this.user != null){
                    String ar = this.user;
                    args.add(ar);
                    req = new request(this.user, "logout", args);
                    this.exit++;
                } else{
                    while(defaultTokenizer.hasMoreTokens()){
                        String ar = defaultTokenizer.nextToken();
                        args.add(ar);
                    }
                    req = new request(this.user, command, args);
                }


                //serializzo la request per essere inviata al server
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(req);
                oos.flush();
                byte[] data = baos.toByteArray();


                // la prima parte del messaggio contiene la lunghezza della request
                ByteBuffer length = ByteBuffer.allocate(Integer.BYTES);
                length.putInt(baos.toByteArray().length);
                length.flip();
                client.write(length);
                length.clear();

                // la seconda parte del messaggio contiene la request da inviare
                ByteBuffer readBuffer = ByteBuffer.wrap(data);

                client.write(readBuffer);
                readBuffer.clear();


                if(this.exit == 1){
                    this.exit++;
                    continue;

                }

                //leggo la response ricevuta dal server
                ByteBuffer reply = ByteBuffer.allocate(BUFFER_DIMENSION);
                client.read(reply);
                reply.flip();

                int status;

                ByteArrayInputStream bis = new ByteArrayInputStream(reply.array());
                ObjectInput in = new ObjectInputStream(bis);

                response resp = (response) in.readObject();


                //gestisco la creazione di un progetto
                if(command.equals(this.CREATE_PROJ_CMD)){

                    if(resp.getStatus_code() == 0){
                        //mi metto in ascolto per la chat del progetto
                        multicastThread mi = new multicastThread(resp.getAddresses().get(0), resp.getAddresses().get(1), Integer.parseInt(resp.getAddresses().get(2)));
                        mi.createSocket();
                        mi.startListening();
                        multicastSockets.add(mi);
                    }

                }



                // gestisco l'invio di messaggi alla chat di gruppo
                if(command.equals(this.SENDMSG_CMD)){

                    if(this.user == null){
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add("ERROR: Null arguments");
                        resp =  new response(11, tmp);
                    }else{
                        DatagramPacket pkt;

                        String prog = req.getArgs().get(0);
                        StringBuilder messaggioChat = new StringBuilder();

                        messaggioChat.append(req.getNick()).append(": ");
                        int count = 0;
                        for (String s:
                                req.getArgs()) {
                            if(count == 0){
                                count++;
                                continue;

                            }
                            messaggioChat.append(s).append(" ");
                            count++;
                        }

                        buf = new byte[1024];
                        buf = messaggioChat.toString().getBytes(StandardCharsets.UTF_8);

                        for (multicastThread m:
                                this.multicastSockets) {
                            if(m.getProgName().equals(prog)){
                                pkt = new DatagramPacket(buf, buf.length, InetAddress.getByName(m.getIpAddress()),m.getPort());
                                m.getSocket().send(pkt);
                            }
                        }
                    }


                }



                //gestisco la lettura di messaggi relativi ad una chat
                if(command.equals(this.READMSGS_CMD)){

                    if(this.user == null){
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add("ERROR: Null arguments");
                        resp =  new response(11, tmp);
                    } else{
                        String prog = req.getArgs().get(0);

                        for (multicastThread m:
                                multicastSockets) {
                            if(m.getProgName().equals(prog)){

                                System.out.println("----------------- " + prog + " -----------------");
                                System.out.println(m.getChat());
                                System.out.println("---------------- end chat ----------------");
                            }
                        }
                    }



                }


                status = resp.getStatus_code();

                //mi metto in ascolto delle varie chat
                if(command.equals(this.LOGIN_CMD)){
                    int nProg = resp.getnProg();
                    for(int i = 0; i < 3*nProg; i=i+3){
                        multicastThread mi = new multicastThread(resp.getAddresses().get(i), resp.getAddresses().get(i+1) ,Integer.parseInt(resp.getAddresses().get(i+2)));
                        mi.createSocket();
                        mi.startListening();
                        multicastSockets.add(mi);

                    }

                    //setto this.user al corretto utente
                    if(status == 0){
                        this.user = req.getNick();
                    }

                }

                //modifico this.user in seguito a logout
                if(command.equals(this.LOGOUT_CMD)){
                    this.user = null;
                }

                //stampo la risposta ricevuta dal server
                System.out.println("Server response: " + resp.getArgs());
                reply.clear();

            }
            System.out.println("Client: closing");

            //chiudo il client

            stub.unregisterForCallback(stub2);

            for (multicastThread mi:
                 this.multicastSockets) {
                mi.stopListening();
            }

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /**
     * Metodo che viene richiamato dal servente
     * @param user nome dell'utente
     * @param updt intero che indica se si tratta di un login o di un logout
     * @throws RemoteException
     */
    public void notifyEvent(user user, int updt) throws RemoteException {

        //login user
        if(updt == 1){
            this.localUsers.put(user.getName(), "ONLINE");

        //logout user
        } else if(updt == 2) {
            this.localUsers.put(user.getName(), "OFFLINE");

        //utilizzato per l'invio iniziale degli utenti
        }else if(updt == 3){
            this.localUsers.put(user.getName(), user.getStatusString());
        }
    }


}
