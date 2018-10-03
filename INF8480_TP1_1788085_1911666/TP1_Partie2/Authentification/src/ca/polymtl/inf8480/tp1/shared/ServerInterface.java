package ca.polymtl.inf8480.tp1.shared;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ServerInterface extends Remote {
    boolean new1(String login, String password) throws RemoteException;
    boolean verify(String login, String password) throws RemoteException;
    boolean create(String fileName, String username, String password) throws RemoteException;
    ArrayList syncLocalDirectory(String username, String password) throws RemoteException;
    UserFile lock(String fileName,  String checkSum, String username, String password) throws RemoteException;
    HashMap list(String username, String password) throws RemoteException;
    String get(String fileName, String checkSum, String username, String password) throws RemoteException;
    boolean push(String fileName, String content, String username, String password) throws RemoteException;
}

