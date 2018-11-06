
package ca.polymtl.inf8480.tp2.ServeurNoms;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.util.HashMap;


public class ServeurNoms implements NomsInterface{
	
	private String repartiteurUsername=null;
	private String repartiteurPassword=null;
	//private String[] ipServeurs=null;
	private List<String> ipServeurs = new ArrayList<String>();
	//private int[] capaciteServeurs=null;
	private List<Integer> capaciteServeurs = new ArrayList<Integer>();
  private HashMap<String, String> map = new HashMap<>();
	public static void main(String[] args) {
        ServeurNoms serveurNoms = new ServeurNoms();
        serveurNoms.run();
    }

    public ServeurNoms() {
        super();
    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            NomsInterface stub = (NomsInterface) UnicastRemoteObject
                    .exportObject(this, 5000);

            Registry registry = LocateRegistry.createRegistry(5000);
            registry.rebind("serveurNoms", stub);
            System.out.println("Serveur de noms est pret.");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
	@Override
	public void ajouterRepartiteur(String username, String password) throws RemoteException {
		// TODO Auto-generated method stub
   map.put(username, password);
		
	}
 
  @Override 
  public boolean authenticate(String username, String password){
  if(map.containsKey(username))
            return map.get(username).equals(password);
        return false;
    
  }

	@Override
	public int getNombreServeurs() throws RemoteException {
		// TODO Auto-generated method stub
		//int[] capaciteServeurs1 = new int[ capaciteServeurs.size() ];
		//capaciteServeurs.toArray( capaciteServeurs1 );
		
		if(capaciteServeurs!=null)
			return capaciteServeurs.size();
		else return 0;
	}

	@Override
	public String[] getIpServeurs() throws RemoteException {
		// TODO Auto-generated method stub
		String[] ipServeurs1 = new String[ ipServeurs.size() ];
		ipServeurs.toArray( ipServeurs1 );
		return ipServeurs1;
	}

	@Override
	public int[] getCapaciteServeurs() throws RemoteException {
		// TODO Auto-generated method stub
		int[] capaciteServeurs1 = capaciteServeurs.stream()
				  .mapToInt(Integer::intValue)
				  .toArray();
                       
		return capaciteServeurs1;
	}

	@Override
	public void removeServeur(String IP) throws RemoteException {
     this.ipServeurs.remove(IP);
     this.capaciteServeurs.remove(this.capaciteServeurs.size() - 1);
     System.out.println("Server @" + IP + " removed");
     System.out.println("Servers:" + this.ipServeurs);
       
		
	}

	@Override
	public void ajouterServeur(String IP, int capacite) throws RemoteException {
		// TODO Auto-generated method stub
		ipServeurs.add(IP);
		capaciteServeurs.add(capacite);
    System.out.println("Server @" + IP +" added");
				
	}
 
  public void catchFailure(){
      
  }

}
