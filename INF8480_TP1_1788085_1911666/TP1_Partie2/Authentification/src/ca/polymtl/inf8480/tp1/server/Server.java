package ca.polymtl.inf8480.tp1.server;

import ca.polymtl.inf8480.tp1.shared.*;

import java.io.File;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Server implements ServerInterface {

    private UserManager userManager = new UserManager();
    private UserFileManager userFileManager = new UserFileManager();
    private FileLockManager fileLockManager = new FileLockManager();

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    public Server() {
        super();
    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServerInterface stub = (ServerInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("server", stub);
            System.out.println("Server ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lance ?");
            System.err.println();
            System.err.println("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @Override
    public boolean new1(String username, String password) throws RemoteException{
        return userManager.createNewUser(username, password);
    }


    @Override
    public boolean verify(String username, String password) throws RemoteException {
        return userManager.verify(username, password);
    }

    @Override
    public boolean create(String fileName, String username, String password) throws RemoteException{
        if(verify(username, password))
            if(userFileManager.createFile(fileName)){
                fileLockManager.addToMetaData(fileName, "");
                return true;
            }
        return false;
    }

    @Override
    public HashMap<String, String> list(String username, String password) throws RemoteException{
        if(verify(username, password))
            return fileLockManager.getLockMetadata();
        return null;
    }

    @Override
    public UserFile lock(String fileName,  String checkSum, String username, String password) throws RemoteException{
        UserFile fileToLock = new UserFile(fileName, fileLockManager.getLockOwner(fileName), UserFileManager.getContent(fileName));
        if(verify(username, password))
            if(fileLockManager.lock(fileName, checkSum, username)){
                fileToLock.setLockUsername(username);
                return fileToLock;
            }

        return fileToLock;
    }


    @Override
    public ArrayList<UserFile> syncLocalDirectory(String username, String password) throws RemoteException{
        if(verify(username, password)) {
            Set<String> existingFiles = fileLockManager.getLockMetadata().keySet();
            ArrayList<UserFile> files = new ArrayList<>();
            for (String setEntry : existingFiles) {
                UserFile userFile = new UserFile(setEntry, fileLockManager.getLockOwner(setEntry), UserFileManager.getContent(setEntry));
                files.add(userFile);
            }
            return files;
        }
        return null;
    }


    @Override
    public String get(String fileName, String checkSum, String username, String password) throws RemoteException{
        if(verify(username, password))
            return userFileManager.compareChecksum(fileName, checkSum) ? null : UserFileManager.getContent(fileName);
        return null;
    }

    @Override
    public boolean push(String fileName, String content, String username, String password) throws RemoteException{
        if(verify(username, password)) {
            if (fileLockManager.getLockOwner(fileName).equals(username)){
                fileLockManager.removeLock(fileName);
                return userFileManager.writeFile(fileName, content);
            }

            else if (!fileLockManager.getLockOwner(fileName).equals(username))
                return false;
        }
        return false;
    }


}
