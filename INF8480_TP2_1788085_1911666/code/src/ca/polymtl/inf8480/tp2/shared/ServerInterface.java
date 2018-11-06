package ca.polymtl.inf8480.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServerInterface extends Remote {
    boolean acceptRequest(int nOperations) throws RemoteException;
    int calculate(Map<String, String> operations) throws RemoteException;
    int getCapacity() throws RemoteException;
}
