import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.*;


class server implements serverInterface{
    /**
     * dimensione del buffer utilizzato per la lettura
     */
    private final int BUFFER_DIMENSION = 2056;

    /**
     * comando utilizzato dal client per comunicare la fine della comunicazione
     */
    private final String EXIT_CMD = "exit";

    /**
     * porta su cui aprire il listening socket
     */
    private final int port;

    /**
     * numero di client con i quali è aperta una connessione
     */
    public int numberActiveConnections;

    /**
     * lista dei client da notificare in seguito ad un evento
     */
    private final List <notifyEventInterface> clients;

    /**
     * classe utilizzata per generare indirizzo e porta di un progetto
     */
    private final chatAddress chat;

    /**
     * lista dei progetti presenti nel server
     */
    public projects projects = new projects();

    /**
     * lista degli utenti registrati al servizio
     */
    public users users = new users();

    /**
     * Mappa utilizzata per restituire la descrizione dell'errore al client
     */
    public HashMap<Integer, String> errors;


    /**
     * Costruttore della classe
     * @param port porta su cui aprire il listening socket
     */
    public server(int port){
        this.port = port;
        clients = new ArrayList<>();
        this.chat = new chatAddress();

        //riempio la lista degli errori
        errors = new HashMap<>();
        errors.put(1, "ERROR: Not logged in");
        errors.put(2, "ERROR: Already logged in");
        errors.put(3, "ERROR: Can't login");
        errors.put(4, "ERROR: Can't logout");
        errors.put(5, "ERROR: Project already present");
        errors.put(6, "ERROR: Not a member");
        errors.put(7, "ERROR: User not registered");
        errors.put(8, "ERROR: Invalid card move");
        errors.put(9, "ERROR: Can't cancel project");
        errors.put(10, "ERROR: User already registered");
        errors.put(11, "ERROR: Null arguments");
        errors.put(12, "ERROR: Wrong number of arguments");
        errors.put(13, "ERROR: Operation not supported");



    }


    /**
     * avvia l'esecuzione del server
     */
    public void start() throws RemoteException {

        // cerco di ripristinare lo stato precedente del server
        try {
            restoreBackup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.numberActiveConnections = 0;
        try (
                ServerSocketChannel s_channel = ServerSocketChannel.open()){
            s_channel.socket().bind(new InetSocketAddress(this.port));
            s_channel.configureBlocking(false);
            Selector sel = Selector.open();
            s_channel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.printf("Server: waiting for connections on port %d\n", this.port);
            while(true){
                if (sel.select() == 0)
                    continue;
                // insieme delle chiavi corrispondenti a canali pronti
                Set<SelectionKey> selectedKeys = sel.selectedKeys();
                // iteratore dell'insieme sopra definito
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {               // ACCETTABLE
                        /*
                         * accetta una nuova connessione creando un SocketChannel per la
                         * comunicazione con il client che la richiede
                         */
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel c_channel = server.accept();
                        c_channel.configureBlocking(false);
                        System.out.println("Server: new connection accepted from the client: " + c_channel.getRemoteAddress());
                        System.out.printf("Server: number of open connections: %d\n",++this.numberActiveConnections);

                        // callback per la lista degli utenti
                        for (user s :
                                this.users.getUsers()) {
                            update(s, 3);
                        }

                        this.registerRead(sel, c_channel);
                    }

                    else if (key.isReadable()){                  // READABLE
                        this.readClientMessage(sel, key);
                    }

                    else if (key.isWritable()) {                 // WRITABLE
                        this.answer(sel, key);
                    }

                }
            }
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    /**
     * registra l'interesse all'operazione di READ sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param c_channel socket channel relativo al client
     * @throws IOException se si verifica un errore di I/O
     */
    private void registerRead(Selector sel, SocketChannel c_channel) throws IOException {

        // crea il buffer
        ByteBuffer length= ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer message= ByteBuffer.allocate(BUFFER_DIMENSION);
        ByteBuffer[] bfs = {length, message};
        // aggiunge il canale del client al selector con l'operazione OP_READ
        // e aggiunge l'array di bytebuffer [length, message] come attachment
        c_channel.register(sel, SelectionKey.OP_READ, bfs);
    }


    /**
     * legge il messaggio inviato dal client e registra l'interesse all'operazione di WRITE sul selettore
     *
     * @param sel selettore utilizzato dal server
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void readClientMessage(Selector sel, SelectionKey key) throws IOException, ClassNotFoundException {
        /*
         * accetta una nuova connessione creando un SocketChannel per la comunicazione con il client che
         * la richiede
         */
        SocketChannel c_channel = (SocketChannel) key.channel();
        // recupera l'array di bytebuffer (attachment)
        ByteBuffer[] bfs = (ByteBuffer[]) key.attachment();
        c_channel.read(bfs);
        if (!bfs[0].hasRemaining()){
            bfs[0].flip();
            int l = bfs[0].getInt();

            if (bfs[1].position() == l) {
                bfs[1].flip();

                ByteArrayInputStream bis = new ByteArrayInputStream(bfs[1].array());
                ObjectInput in = new ObjectInputStream(bis);

                request req = (request) in.readObject();

                String cmd = req.getCommand();

                System.out.printf("Server: received %s\n", cmd);

                if (cmd.equals(this.EXIT_CMD)){
                    System.out.println("Server: connection closed with the client " + c_channel.getRemoteAddress());

                    c_channel.close();
                    key.cancel();
                }
                else {
                    /*
                     * aggiunge il canale del client al selector con l'operazione OP_WRITE
                     * e aggiunge il messaggio ricevuto come attachment (aggiungendo la risposta addizionale)
                     */

                    c_channel.register(sel,SelectionKey.OP_WRITE, req);
                }
            }
        }
    }

    /**
     * scrive il buffer sul canale del client
     *
     * @param key chiave di selezione
     * @throws IOException se si verifica un errore di I/O
     */
    private void answer(Selector sel, SelectionKey key) throws IOException {
        SocketChannel c_channel = (SocketChannel) key.channel();

        //prendo la request che mi arriva dal client

        request req = (request) key.attachment();
        ByteBuffer bbAnsw = null;

        String command = req.getCommand();

        response resp;
        ByteArrayOutputStream bos;
        ObjectOutputStream oos;
        byte[] data;


        //operazioni che effettua il server

        switch (command) {

            case "login":
                if (req.getArgs().size() != 2) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.login(req.getArgs().get(0), req.getArgs().get(1));

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "logout":
                if (req.getArgs().size() != 1) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.logout(req.getArgs().get(0));

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "listProjects":
                if (req.getArgs().size() != 0) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.listProjects(req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "createProject":
                if (req.getArgs().size() != 1) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.createProject(req.getArgs().get(0), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "addMember":
                if (req.getArgs().size() != 2) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.addMember(req.getArgs().get(0), req.getArgs().get(1), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "showMembers":
                if (req.getArgs().size() != 1) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.showMembers(req.getArgs().get(0), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "showCards":
                if (req.getArgs().size() != 1) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.showCards(req.getArgs().get(0), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "showCard":
                if (req.getArgs().size() != 2) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.showCard(req.getArgs().get(0), req.getArgs().get(1), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "addCard":
                if (req.getArgs().size() < 3) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    ArrayList<String> descr = new ArrayList<>();
                    int desc_lenght = req.getArgs().size();
                    for(int i = 2; i < desc_lenght; i++){
                        descr.add(req.getArgs().get(i));
                    }
                    resp = this.addCard(req.getArgs().get(0), req.getArgs().get(1), descr, req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "moveCard":
                if (req.getArgs().size() != 4) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.moveCard(req.getArgs().get(0), req.getArgs().get(1), req.getArgs().get(2), req.getArgs().get(3), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                 data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "getCardHistory":
                if (req.getArgs().size() != 2) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.getCardHistory(req.getArgs().get(0), req.getArgs().get(1), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "cancelProject":
                if (req.getArgs().size() != 1) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    resp = this.cancelProject(req.getArgs().get(0), req.getNick());

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "readMessages":
                resp = this.readMessages(req.getNick());
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                 data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "sendMessage":
                resp = this.sendMessage(req.getNick());
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "register":
                if (req.getArgs().size() != 2) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(this.errors.get(12));
                    resp = new response(12, tmp);
                } else {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add("ok");
                    resp = new response(0, tmp);

                }
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            case "help":
                resp = this.help();
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;

            default:
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(this.errors.get(13));
                resp = new response(13, tmp);
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(resp);
                oos.flush();
                data = bos.toByteArray();
                bbAnsw = ByteBuffer.wrap(data);
                break;
        }

        //mando indietro la risposta
        c_channel.write(bbAnsw);

        if (!bbAnsw.hasRemaining()) {
            bbAnsw.clear();
            this.registerRead(sel, c_channel);
        }

    }

    //-------------------------------------------- OPERATIONS ----------------------------------------------------------

    /**
     *  Metodo per richiedere le varie operazioni disponibili
     */
    private response help() {

        response resp;
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("\nHere's the server commands:\n" +
                "listUsers -> List all users registered on the platform \n" +
                "register 'userName' 'password' -> register a user to the platform \n" +
                "readMessages 'project_name' -> read all the messages sent to the project chat since the last check \n" +
                "sendMessage 'project_name' 'message' -> send the message to the project chat identified by the project name \n" +
                "login 'userName' 'password' -> login the user \n" +
                "logout 'userName' -> logout the user \n" +
                "listOnlineUsers -> list all the users with status Online \n" +
                "listProjects -> list all the projects on the platform \n" +
                "createProject 'project_name' -> create a project named 'project_name' \n" +
                "addMember 'project_name' 'userName' -> add the user 'userName' to the project 'project_name' \n" +
                "showMembers 'project_name' -> list all the members of a particular project \n" +
                "showCards 'project_name' -> list all the cards of a particular project \n" +
                "showCard 'project_name' 'card_name' -> show the card identified by 'card_name' in the project 'project_name' \n" +
                "addCard 'project_name' 'card_name' 'description' -> add the card to the project with a description \n" +
                "moveCard 'project_name' 'card_name' 'starting_list' 'destination_list' -> move the card to 'destination_list' \n" +
                "getCardHistory 'project_name' 'card_name' -> get the card history \n" +
                "cancelProject 'project_name' -> delete the project called 'project_name', but only if all the cards are done ");
        resp = new response(0, tmp);
        return resp;
    }


    /**
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response readMessages(String nickReq){
        return getResponse(nickReq);
    }

    /**
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    private response getResponse(String nickReq) {
        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null){
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        if(this.users.getUser(nickReq).getStatusString().equals("OFFLINE")){
            tmp.add(this.errors.get(1));
            return new response(1, tmp);
        }
        tmp.add("SUCCESS: operation successful");
        return new response(0, tmp);
    }

    /**
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response sendMessage(String nickReq){
        return getResponse(nickReq);
    }

    /**
     * metodo per effettuare il login al sistema
     * @param nickUtente nome dell'utente che vuole effettuare il login
     * @param password password fornita dall'utente
     * @return una response con la lista degli argomenti per il multicast delle chat
     * @throws RemoteException
     */
    synchronized response login(String nickUtente, String password) throws RemoteException {

        if(this.users.getUser(nickUtente) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        user user = users.getUser(nickUtente);

        if (this.users.getUser(nickUtente).getStatusString().equals("ONLINE")){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(2));
            return new response(2, tmp);
        }

        response resp;

        if(nickUtente == null || password == null|| !password.equals(user.getPassword())){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(3));

            resp = new response(3, tmp);
            return resp;
        }

        ArrayList<String> projectsAndAddresses = new ArrayList<>();
        int nProg = 0;

        for (project p:
                projects.getProjects()) {
            if(p.getMembers().containsMember(nickUtente)){
                nProg++;
                projectsAndAddresses.add(p.getName());
                projectsAndAddresses.add(p.getChat_address());
                projectsAndAddresses.add(String.valueOf(p.getPort()));
            }
        }

        user.setOnline();
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("SUCCESS: login successful");
        resp = new response(0, tmp);
        resp.setnProg(nProg);
        resp.setAddresses(projectsAndAddresses);

        update(user, 1);
        return resp;
    }


    /**
     * Metodo per effettuare il logout di un utente
     * @param nickUtente nome dell'utente che vuole effettuare il logout
     */
    synchronized response logout(String nickUtente){

        if(this.users.getUser(nickUtente) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        response resp;
        user user = users.getUser(nickUtente);
        ArrayList<String> tmp = new ArrayList<>();

        if(nickUtente == null || this.users.getUser(nickUtente).getStatusString().equals("OFFLINE")){

            tmp.add(this.errors.get(4));

            resp = new response(4, tmp);
            return resp;
        }
        user.setOffline();

        try {
            update(user, 2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        tmp.add("SUCCESS: logout successful");
        resp = new response(0, tmp);
        return resp;

    }


    /**
     * Metodo per ricevere la lista dei progetti presenti nel sistema
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response listProjects(String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        response resp;
        ArrayList<String> list = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            list.add(this.errors.get(1));

            resp = new response(1, list);
            return resp;
        }
        for (project p: this.projects.getProjects()) {
            if(p.getMembers().containsMember(nickReq)){
                list.add(p.getName());
            }

        }
        resp = new response(0, list);
        return resp;
    }


    /**
     * Metodo utilizzato per creare un nuovo progetto
     * @param projectName nome da assegnare al progetto
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    synchronized response createProject(String projectName, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        response resp;

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
           ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(1));

            resp = new response(1, tmp);
            return resp;
        }


        String newChatAddress = this.chat.returnAddress();
        int port = this.chat.returnPort();

        if(this.projects.myContains(projectName)){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(5));

            resp = new response(5, tmp);
            return resp;
        }

        project new_prog = new project(projectName, newChatAddress, port);

        projects.add_prog(new_prog);

        member member = new member(nickReq);
        new_prog.getMembers().addMember(member);


        //mando indietro il nome, il chat address e la porta
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("SUCCESS: project created successfully");
        tmp.add(new_prog.getName());
        tmp.add(new_prog.getChat_address());
        tmp.add(Integer.toString(new_prog.getPort()));
        resp = new response(0, tmp);


        ArrayList<String> new_addr = new ArrayList<>();
        new_addr.add(projectName);
        new_addr.add(newChatAddress);
        new_addr.add(String.valueOf(port));

        resp.setnProg(1);
        resp.setAddresses(new_addr);

        backupProjects();

        return resp;

    }


    /**
     * Metodo per aggiungere un membro ad un progetto
     * @param projectName nome del progetto a cui aggiungere il membro
     * @param nickUtente nome dell'utente da aggiungere al progetto
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    synchronized response addMember(String projectName, String nickUtente, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        response resp;
        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            tmp.add(this.errors.get(1));

            resp = new response(1, tmp);
            return resp;
        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        if(!this.users.mycontain(nickUtente)){
            tmp.add(this.errors.get(7));
            return new response(7, tmp);
        }

        member new_member = new member(nickUtente);
        this.projects.get_prog(projectName).getMembers().addMember(new_member);
        tmp.add("SUCCESS: " + new_member.getName() + " added to the project");

        backupProjects();

        resp = new response(0, tmp);
        return resp;
    }


    /**
     * Metodo per ricevere la lista dei membri che partecipano ad un progetto
     * @param projectName nome del progetto
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response showMembers(String projectName, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){

            tmp.add(this.errors.get(1));

            return new response(1, tmp);
        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        for (member m:
                this.projects.get_prog(projectName).getMembers().getMembers()) {
            tmp.add(m.getName());
        }
        return new response(0, tmp);
    }


    /**
     * Metodo per ricevere la lista delle card persenti in un progetto
     * @param projectName nome del progetto
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response showCards(String projectName, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            tmp.add(this.errors.get(1));

            return new response(1, tmp);
        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        HashSet<card> all_cards = new HashSet<>();
        all_cards.addAll(this.projects.get_prog(projectName).getFinished().getCards());
        all_cards.addAll(this.projects.get_prog(projectName).getInprogress().getCards());
        all_cards.addAll(this.projects.get_prog(projectName).getToberevised().getCards());
        all_cards.addAll(this.projects.get_prog(projectName).getTodo().getCards());

        for (card c:
             all_cards) {
            tmp.add(c.getName());
        }
        return new response(0, tmp);
    }


    /**
     * Metodo per mostrare una card appartenente al progetto
     * @param projectName nome del progetto
     * @param cardName nome della card da mostrare
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response showCard(String projectName, String cardName, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            tmp.add(this.errors.get(1));

            return new response(1, tmp);

        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        StringBuilder str = new StringBuilder();

        str.append(this.projects.get_prog(projectName).getCard(cardName).getName()).append(", ")
                .append(this.projects.get_prog(projectName).getCard(cardName).getDescription()).append(", ")
                .append(this.projects.get_prog(projectName).getCard(cardName).getCard_history()).append(";\n");


        tmp.add(str.toString());

        return new response(0, tmp);

    }


    /**
     * Metodo per aggiungere una card ad un progetto
     * @param projectName nome del progetto
     * @param cardName nome della card da aggiungere
     * @param descrizione descrizione relativa alla card
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    synchronized response addCard(String projectName, String cardName, ArrayList<String> descrizione, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            tmp.add(this.errors.get(1));

            return new response(1, tmp);
        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }


        StringBuilder str = new StringBuilder();
        for (String s:
             descrizione) {
            str.append(s).append(" ");
        }


        card new_card = new card(cardName, str.toString());
        this.projects.get_prog(projectName).addCard(new_card);

        backupProjects();

        tmp.add("SUCCESS: " + cardName + " added to the project");
        return new response(0, tmp);


    }

    /**
     * Metodo per muovere una card da una lista ad un' altra
     * @param projectName nome del progetto
     * @param cardName nome della card
     * @param listaPartenza nome della lista di partenza della card
     * @param listaDestinazione nome della lista di arrivo della card
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    synchronized response moveCard(String projectName, String cardName, String listaPartenza, String listaDestinazione, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            tmp.add(this.errors.get(1));

            return new response(1, tmp);

        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        card card_tmp = this.projects.get_prog(projectName).getCard(cardName);
        card.list start = card_tmp.stringToList(listaPartenza);
        card.list finish = card_tmp.stringToList(listaDestinazione);

        if(moveToList(listaPartenza, listaDestinazione)){
            this.projects.get_prog(projectName).getSet(start).removeCard(card_tmp);

            this.projects.get_prog(projectName).getSet(finish).addCard(card_tmp);

            card_tmp.getCard_history().add(listaDestinazione);
            tmp.add(card_tmp.getName() + " moved to " + listaDestinazione);

            deleteCard(projectName, cardName, listaPartenza);
            backupProjects();

            return new response(0, tmp);

        } else{
            tmp.add(this.errors.get(8));
            return new response(8, tmp);

        }

    }


    /**
     * Metodo per controllare se e' possibile muovere una card in una particolare lista
     * @param listaDiPartenza nome della lista di partenza
     * @param listaDiArrivo nome della lista di arrivo
     * @return true se e' possibile, false altrimenti
     */
    public boolean moveToList(String listaDiPartenza, String listaDiArrivo){
        if(listaDiPartenza.equals("TODO") && (listaDiArrivo.equals("TOBEREVISED") || listaDiArrivo.equals("DONE"))){
            return false;
        }
        else if(listaDiPartenza.equals("INPROGRESS") && (listaDiArrivo.equals("TODO"))){
            return false;
        }
        else if(listaDiPartenza.equals("TOBEREVISED") && (listaDiArrivo.equals("TODO"))){
            return false;
        }
        else return !listaDiPartenza.equals("FINISHED");
    }


    /**
     * Metodo per ritornare la history dei movimenti di una card
     * @param projectName nome del progetto a cui appartiene la card
     * @param cardName nome della card
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    response getCardHistory(String projectName, String cardName, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(1));

            return new response(1, tmp);

        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        ArrayList<String> history = this.projects.get_prog(projectName).getCard(cardName).getCard_history();

        return new response(0, history);

    }

    /**
     * Metodo per eliminare un progetto al sistema
     * @param projectName nome del progetto da eliminare
     * @param nickReq nome dell'utente che ha effettuato la richiesta
     */
    synchronized response cancelProject(String projectName, String nickReq){

        if(this.users.getUser(nickReq) == null){
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(this.errors.get(11));
            return new response(11, tmp);
        }

        ArrayList<String> tmp = new ArrayList<>();

        if(this.users.getUser(nickReq) == null || this.users.getUser(nickReq).getStatusString().equals("OFFLINE") ){
            tmp.add(this.errors.get(1));

            return new response(1, tmp);

        }

        if(!this.projects.get_prog(projectName).getMembers().containsMember(nickReq)){
            tmp.add(this.errors.get(6));
            return new response(6, tmp);
        }

        if(this.projects.get_prog(projectName).getTodo().getnCards() != 0 ||
                this.projects.get_prog(projectName).getInprogress().getnCards() != 0 ||
                this.projects.get_prog(projectName).getToberevised().getnCards() != 0){

            tmp.add(this.errors.get(9));
            return new response(9, tmp);

        }

        project tmp_prog = this.projects.get_prog(projectName);
        this.projects.getProjects().remove(tmp_prog);

        try {
            deleteProject(projectName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tmp.add("SUCCESS: " + tmp_prog.getName() + " deleted successfully");
        return new response(0, tmp);

    }


    /**
     * Metodo usato per controllare se un utente appartiene al sistema
     * @param nomeUtente nome dell'utente da ricercare
     * @return true se appartiene, false altrimenti
     * @throws RemoteException
     */
    public boolean contains(String nomeUtente) throws RemoteException {
        return this.users.mycontain(nomeUtente);
    }

    /**
     * Metodo per registrare un utente al servizio
     * @param nomeUtente nome dell'utente da registrare
     * @param password password dell'utente
     * @throws RemoteException
     */
    public synchronized void register(String nomeUtente, String password) throws RemoteException{

        ArrayList<String> tmp = new ArrayList<>();

        if(nomeUtente == null || password == null){
            tmp.add(this.errors.get(11));
             new response(11, tmp);
        }else{
            user new_user = new user(nomeUtente, password);

            users.addUser(new_user);

            update(new_user, 2);

            try {
                backupUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tmp.add("SUCCESS: Registration completed successfully");
            new response(0, tmp);
        }

    }

    //------------------------------ CALLBACK -----------------------------------------------------


    public synchronized void registerForCallback(notifyEventInterface ClientInterface) throws RemoteException {
        if (!clients.contains(ClientInterface)){
            clients.add(ClientInterface);
            System.out.println("New client registered." );
        }
    }

    public void unregisterForCallback(notifyEventInterface Client) throws RemoteException {
        if (clients.remove(Client)) {
            System.out.println("Client unregistered");
        }
        else {
            System.out.println("Unable to unregister client.");
        }
    }


    /**
     * Metodo usato per dare un update in seguito a login/logout di un utente
     * @param user nome dell'utente che ha effettuato il login/logout
     * @param updt intero utilizzato per stabilire se si tratta di un login o di un logout
     * @throws RemoteException
     */
    public void update(user user, int updt) throws RemoteException {
        doCallbacks(user, updt);
    }


    /**
     * Metodo utilizzato per effettuare il callback
     * @param user nome dell'utente che ha effettuato il login/logout
     * @param updt intero utilizzato per stabilire se si tratta di un login o di un logout
     * @throws RemoteException
     */
    private synchronized void doCallbacks(user user, int updt) throws RemoteException {
        System.out.println("Starting callbacks.");
        for (notifyEventInterface client : clients) {

            client.notifyEvent(user, updt);
        }
        System.out.println("Callbacks complete.");
    }


    //-------------------------------- PERSISTENZA SISTEMA -------------------------------------------------------

    /**
     * Metodo per effettuare il backup degli utenti
     * @throws IOException
     */
    public void backupUsers() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File("./users.json");
            file.createNewFile();
            objectMapper.writeValue(file, this.users);

        }catch(IOException e){
            e.printStackTrace();
        }

    }


    /**
     * Metodo per ripristinare lo stato del sistema
     * @throws IOException
     */
    public void  restoreBackup() throws IOException {

        // recover users
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File("./users.json");
        if(file.length() != 0){
            this.users = objectMapper.readValue(file ,users.class);

        }

        // recover projects
        File dir = new File("./projects");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isDirectory()){

                    project p = new project(child.getName(), this.chat.returnAddress(), this.chat.returnPort());
                    this.projects.add_prog(p);

                    //cartelle dentro ad ogni progetto
                    File subdir = new File("./projects/" + child.getName());
                    File[] subdirectoryListing = subdir.listFiles();
                    if(subdirectoryListing != null){
                        for (File subchild : subdirectoryListing){
                            if(subchild.isDirectory()){

                                //varie cartelle delle card
                                File subsubdir = new File("./projects/" + child.getName() + "/" + subchild.getName());
                                File[] subsubdirectoryListing = subsubdir.listFiles();
                                if(subsubdirectoryListing != null){
                                    for (File subsubchild : subsubdirectoryListing){
                                        if(!subsubchild.isDirectory()){
                                            StringTokenizer strtk = new StringTokenizer(subsubchild.getName(), ".");
                                            card card = new card(strtk.nextToken(), null);

                                            LinkedHashMap<String, ArrayList<String>> hist;
                                            hist = objectMapper.readValue(subsubchild, LinkedHashMap.class);
                                            Map.Entry<String,ArrayList<String>> entry = hist.entrySet().iterator().next();
                                            String key = entry.getKey();
                                            card.setCard_history(hist.get(key));
                                            card.setDescription(key);
                                            p.addCard(card);
                                        }
                                    }
                                }


                            }
                            else if(subchild.getName().equals("members.json")){

                                p.setMembers(objectMapper.readValue(subchild, members.class));

                            }
                        }
                    }


                }
            }
        }

    }


    /**
     *  Metodo per eliminare la cartella di un progetto
     * @param projectName nome del progetto da eliminare
     * @throws IOException
     */
    public void deleteProject(String projectName) throws IOException {

        Path dir = Paths.get("./projects/" + projectName);
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);


    }


    /**
     * Metodo utilizzatto per eliminare una card
     * @param projectName nome del progetto a cui appartiene la card
     * @param cardName nome della card
     * @param listaDiPartenza nome della lista da cui eliminare la card
     */
    public void deleteCard(String projectName, String cardName, String listaDiPartenza){

        String path = "./projects/" + projectName + "/" + listaDiPartenza;
        File theDir = new File(path);
        for (File file:
                Objects.requireNonNull(theDir.listFiles())) {

            if(file.getName().equals(cardName + ".json")){
                file.delete();
            }
        }

    }


    /**
     * Metodo per effettuare il backup dei progetti
     */
    public void backupProjects(){

        for (project p:
                projects.getProjects()) {
            String path = "./projects/" + p.getName();
            File theDir = new File(path);
            if (!theDir.exists()){
                theDir.mkdirs();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {

                //members
                File file = new File("./" + path + "/members.json");
                file.createNewFile();
                objectMapper.writeValue(file,p.getMembers());

                //cards
                File ToDo = new File("./" + path + "/TODO");
                if (!ToDo.exists()){
                    ToDo.mkdirs();
                }
                File InProgress = new File("./" + path + "/INPROGRESS");
                if (!InProgress.exists()){
                        InProgress.mkdirs();
                }
                File ToBeRevised = new File("./" + path + "/TOBEREVISED");
                if (!ToBeRevised.exists()){
                    ToBeRevised.mkdirs();
                }
                File Finished = new File("./" + path + "/FINISHED");
                if (!Finished.exists()){
                    Finished.mkdirs();
                }

                //card
                for (card c: p.getTodo().getCards()) {
                    File todo = new File("./" + path + "/TODO" + "/" + c.getName() +".json");
                    todo.createNewFile();
                    LinkedHashMap<String, ArrayList<String>> tmp = new LinkedHashMap<>();
                    tmp.put(c.getDescription(), c.getCard_history());
                    objectMapper.writeValue(todo, tmp);
                }
                for (card c: p.getInprogress().getCards()) {
                    File inprog = new File("./" + path + "/INPROGRESS" + "/"  + c.getName() +".json");
                    inprog.createNewFile();
                    LinkedHashMap<String, ArrayList<String>> tmp = new LinkedHashMap<>();
                    tmp.put(c.getDescription(), c.getCard_history());
                    objectMapper.writeValue(inprog, tmp);

                }
                for (card c: p.getToberevised().getCards()) {
                    File toberev = new File("./" + path + "/TOBEREVISED" + "/"  + c.getName() +".json");
                    toberev.createNewFile();
                    LinkedHashMap<String, ArrayList<String>> tmp = new LinkedHashMap<>();
                    tmp.put(c.getDescription(), c.getCard_history());
                    objectMapper.writeValue(toberev, tmp);

                }
                for (card c: p.getFinished().getCards()) {
                    File finished = new File("./" + path + "/FINISHED" + "/"  + c.getName() + ".json");
                    finished.createNewFile();
                    LinkedHashMap<String, ArrayList<String>> tmp = new LinkedHashMap<>();
                    tmp.put(c.getDescription(), c.getCard_history());
                    objectMapper.writeValue(finished, tmp);

                }

            }catch(IOException e){
                e.printStackTrace();
            }

        }

    }

}