package ca.polymtl.inf8480.tp1.client;

import java.io.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import ca.polymtl.inf8480.tp1.shared.*;

import javax.imageio.IIOException;

public class Client {

    private String username = null;
    private  String password = null;
    private Scanner reader = new Scanner(System.in);
    private final static String CLIENT_LOGIN = "login.txt";
    private final static String ip = "132.207.12.118";
    public static void main(String[] args) {

        String distantHostname = ip;
        if (args.length > 0) {

            Client client = new Client(distantHostname);
            String action = args[0];
            switch (action){
                case "create":
                    client.create(args[1]);
                    break;
                case "list":
                    client.list();
                    break;
                case "get":
                    client.get(args[1]);
                    break;
                case "lock":
                    client.lock(args[1]);
                    break;
                case "push":
                    client.push(args[1]);
                    break;
                case "syncLocalDirectory":
                    client.syncLocalDirectory();
                    break;
                default:
                    break;

            }

        }


    }

    private ServerInterface distantServerStub = null;

    public Client(String distantServerHostname) {
        super();


        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        if (distantServerHostname != null) {
            distantServerStub = loadServerStub(distantServerHostname);
        }

        getLogin();
        if((username == null && password == null) || (username.equals("") && password.equals("")))
            new1();

    }


    public void getLogin(){
        File file = new File(System.getProperty("user.dir") + File.separator + CLIENT_LOGIN);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            String[] login;
            while ((line = br.readLine()) != null) {
                login = line.split(" ");
                username = login[0];
                password = login[1];
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveClientLogin(){
        File file = new File(System.getProperty("user.dir") + File.separator + CLIENT_LOGIN);
        FileWriter writer = null;
        BufferedWriter buffer = null;
        PrintWriter out = null;
        try {
            writer = new FileWriter(file, false);
            buffer = new BufferedWriter(writer);
            out = new PrintWriter(buffer);
            out.println(username + " " + password);
            out.close();

        } catch (Exception e) {
            System.out.println("Impossible de creer le fichier");
        }
        finally{

            try {
                if(out != null)
                    out.close();
                if(buffer != null)
                    buffer.close();
                if(writer != null)
                    writer.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    private ServerInterface loadServerStub(String hostname) {
        ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServerInterface) registry.lookup("server");
        } catch (NotBoundException e) {
            System.out.println("Erreur: Le nom '" + e.getMessage()
                    + "' n'est pas défini dans le registre.");
        } catch (AccessException e) {
            System.out.println("Erreur: " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        return stub;
    }

    public void syncLocalDirectory(){
        try{
            ArrayList<UserFile> files = distantServerStub.syncLocalDirectory(username, password);
            try{
                FileWriter fileWriter;
                for (int i = 0; i < files.size(); i++) {
                    File newFile = new File(files.get(i).getFileName());
                    if(!newFile.exists()){
                        newFile.createNewFile();
                    }
                    UserFileManager.writeFile(files.get(i).getFileName(), files.get(i).getContent());
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }

        }catch (RemoteException exception){
            exception.printStackTrace();
        }
    }

    public void push(String fileName){
        File existingFile = new File(fileName);
        if(!existingFile.exists()){
            System.out.println("Ce fichier n'existe pas dans votre repertoire.");
            return;
        }
        try{

            boolean pushed = distantServerStub.push(fileName, UserFileManager.getContent(fileName), username, password);
            if(pushed){
                System.out.println(fileName + " a ete envoye au serveur");
            }
            else{
                System.out.println("operation refusee : vous devez d'abord verrouiller le fichier.");
            }

        }catch (RemoteException remoteException){
            remoteException.printStackTrace();
        }
    }


    public void lock(String fileName){
        try{
            File existingFile = new File(fileName);
            String checksum = "";
            if(!existingFile.exists()){
                System.out.println("Ce fichier n'existe pas dans votre repertoire.");
            }
            else{
                checksum = UserFileManager.getChecksum(fileName);
                UserFile lockedFile = distantServerStub.lock(fileName, checksum, username, password);
                String lockedBy = lockedFile.getLockUsername();
                if(lockedBy != null && !lockedBy.equals(username)){
                    System.out.println(fileName + " a deja ete verouille par " + lockedBy);
                }
                else{
                    System.out.println(fileName + " verrouille");
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void get(String fileName){
        try{
            File existingFile = new File(fileName);
            if(!existingFile.exists()){
                existingFile.createNewFile();
            }

            String fileContent = distantServerStub.get(fileName, UserFileManager.getChecksum(fileName), username, password);
            if(fileContent != null){
                UserFileManager.writeFile(fileName, fileContent);
            }
            System.out.println(fileName + " synchronise");
        }catch (IOException ioexception){
            ioexception.printStackTrace();
        }
    }

    public void create(String fileName){
        try{
            boolean created = distantServerStub.create(fileName, username, password);
            if(created == true){
                System.out.println(fileName + " ajoute " + created);
            }
            else
                System.out.println(fileName + " existe deja");
        }catch (RemoteException exception){
            exception.printStackTrace();
        }
    }

    public void list(){
        try{
            HashMap<String, String> filesInfo = distantServerStub.list(username, password);
            if(filesInfo != null && filesInfo.size() != 0){
                Iterator it = filesInfo.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry) it.next();
                    System.out.print(pair.getKey());
                    if(pair.getValue() == null || pair.getValue().equals(""))
                        System.out.println(" non verrouille");
                    else
                        System.out.println(" verrouille par " + pair.getValue());

                }
            }
        }catch (RemoteException ex){
            ex.printStackTrace();
        }
    }

    public void new1(){
        System.out.println("Vous devez d'abord vous enregistrer.");
        System.out.println();

        try{
            boolean isNew = false;
            String userName = null;
            String passWord = null;
            do{
                System.out.println("Veuillez saisir le nom d'utilisateur: " );
                userName = reader.next();
                System.out.println("Veuillez saisir le mot de passe: " );
                passWord = reader.next();
                isNew = distantServerStub.new1(userName, passWord);
                if(isNew == false)
                    System.out.println("Ce login est deja pris. Veuillez reessayer.");

            }while(isNew == false);

            username = userName;
            password = passWord;
            System.out.println(username + " " + password);


        }catch (RemoteException remoteException){
            remoteException.printStackTrace();
        }
        System.out.println("Vous pouvez maintenant utiliser l'application");
        System.out.println();
        saveClientLogin();

    }






}

