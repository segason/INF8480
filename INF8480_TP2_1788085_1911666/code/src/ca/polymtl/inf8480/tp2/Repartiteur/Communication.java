package ca.polymtl.inf8480.tp2.Repartiteur;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class Communication {
	private String[] IpServeurs;
	public Communication () {
	}
	public void envoie(String donnee) {
		
	}
	
	public int calcul(ServerInterface stub, Map<String,String> data) throws RemoteException {
		return stub.calculate(data);	
	}
	public int getCapacity (ServerInterface stub) throws RemoteException{
		return stub.getCapacity();
	}
	
	public ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname, 5000);
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
}
