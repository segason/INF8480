package ca.polymtl.inf8480.tp2.Repartiteur;
import java.util.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import javax.naming.NamingException;

public class Repartiteur {

	public static void main(String args[]) throws NamingException, IOException {
		String file="";
		int result=0;
		String IpServeurNoms=null;
		String mode=null;
		if(args.length== 5) {
			
		//recupere la requete du client
			file=args[0];
			IpServeurNoms=args[1];
			mode=args[2];
      String username = args[3];
      String password = args[4];
	
		//recupere les capacites et IP des serveurs, on les recupere du serveur de noms, a travers RMI
			CommunicationServeurNoms repertoireNoms= new CommunicationServeurNoms();
			NomsInterface stubNoms=repertoireNoms.loadServerStub(IpServeurNoms);
			
			stubNoms.ajouterRepartiteur(username,password);
			System.out.println("Repartiteur est authentifie: " + stubNoms.authenticate(username, password) );
      boolean restart = false;
      
      do{
          int nombreServeurs=stubNoms.getNombreServeurs();
			
      if(nombreServeurs < 1){
        System.out.println("Il n'y a pas de serveurs connectes en ce moment");
        return;
      }
			String[] IpServeurs=new String[nombreServeurs];
			
			IpServeurs=stubNoms.getIpServeurs();
			
			int[] CapaciteServeurs=new int[nombreServeurs];
			
			CapaciteServeurs=stubNoms.getCapaciteServeurs();
			
			System.out.println(file);
		//diviser le travail selon la capacite des serveurs
			Travail travail=new Travail(file);
			
			int[] NbLinesServer=new int[nombreServeurs];//tableau contient le nombre de lines de chaque serveur
			
			NbLinesServer=travail.diviserLines(CapaciteServeurs);//le nombre de lignes pour chaque serveur
			
		    List<Map<String , String>> dataServers  = new ArrayList<Map<String,String>>();
		    
			dataServers=travail.dataToSend(NbLinesServer, file);
			Repartiteur repartiteur=new Repartiteur();
			
			ServeurThread[] serveursThreads=new ServeurThread[nombreServeurs];
			
			serveursThreads=repartiteur.createThreads(IpServeurs, dataServers, mode);
			repartiteur.beginThreads(serveursThreads);
			
			long start = 0;
			if(mode.equals("SECURISE")) {
				start = System.nanoTime();
				result=repartiteur.resultSecurise(serveursThreads, stubNoms, repartiteur);
			}
			else if (mode.equals("NONSECURISE")) {
				start = System.nanoTime();
				result=repartiteur.resultNonSecurise(serveursThreads, stubNoms, repartiteur);
			}
			
			System.out.println("le résultat est: "+ result);
			System.out.println("le calcul a pris: "+ (System.nanoTime() - start) + "ns");
			
      
      int nombreServeursAfter =stubNoms.getNombreServeurs();
      restart = nombreServeurs != nombreServeursAfter;
      if(restart){  
        System.out.println("Panne detectee! Modification a la repartition des taches.");
      }
			
      }while(restart);
			
		}
		else {
			System.out.println("Vous devez donner les bons arguments.");
		}
	}
	
	public Repartiteur() {
		
	}
	
	private ServeurThread[] createThreads(String[] IpServeurs, List<Map<String , String>> dataServers, String mode) {
		ServeurThread[] serveurThread=new ServeurThread[IpServeurs.length];
		int i=0;
		System.out.println("nombre serveurs: "+IpServeurs.length);
		for (Map<String, String> data : dataServers) {
			serveurThread[i]=new ServeurThread(IpServeurs, data, mode, IpServeurs[i]);
			i+=1;
		}
		
		return serveurThread;
	}
	
	private void beginThreads(ServeurThread[] serveurThreads) {
        for(int i = 0; i < serveurThreads.length; i++){
			new Thread(serveurThreads[i]).start();
        }
        
	}
//on utilise une fonction recursive, pour renvoyer le calcul aux autres serveurs
	private int resultSecurise(ServeurThread[] serveurThreads, NomsInterface stubNoms, Repartiteur repartiteur) throws RemoteException {
		int result= 0;
		int i=0;
		String IP=null;
		Map<String, String> data=null;
    int nombreServeurs = stubNoms.getNombreServeurs();
		for(ServeurThread t:serveurThreads) {
			while(t.getResultModeSecurise()==0){
        int updateNServeurs = stubNoms.getNombreServeurs();
        System.out.print("");
        if(updateNServeurs != nombreServeurs){
            return 0;
        }
      }
				
       System.out.println("");
       result= result+t.getResultModeSecurise();
			
		}
		return result % 4000;
		
	}
	
	private int resultNonSecurise(ServeurThread[] serveurThreads, NomsInterface stubNoms, Repartiteur repartiteur) throws RemoteException {
		int result= 0;
		
		String IP=null;
		ArrayList<Integer> results=new ArrayList<>();
		Map<String, String> data=null;
		int partielResult=0;
   
    int nombreServeurs = stubNoms.getNombreServeurs();
		for(ServeurThread t:serveurThreads) {
			while(t.getResult().size()!=serveurThreads.length) {
      int updateNServeurs = stubNoms.getNombreServeurs();
        System.out.print("");
        if(updateNServeurs != nombreServeurs){
            return 0;
        }
      }
   System.out.println("");
			results=t.getResult();
			
			System.out.println(results);
			int occurrences;
			for(int i=0; i<results.size();i++) {
				occurrences = Collections.frequency(results, results.get(i));
				if( occurrences == 1) {
					IP=t.getIP(i);
				}
				else if( occurrences >= 2) {
					partielResult=results.get(i);
				}
			}
			
			System.out.println("partiel: " + partielResult);
			
			
			
					result=(result+partielResult)%4000;
			
            
			
		}
		
		if(IP!=null) {
			// a modifier
			//stubNoms.removeServeur(IP);
			System.out.println("il y a un serveur malicieux voici son adresse ip: "+ IP);
			
		}
		else {
			System.out.println("il n'y a pas de serveur malicieux");
			
		}
		return result;
	}
	
	
	public static String[] removeIp(String[] input, String deleteMe) {
	    List result = new LinkedList();

	    for(String item : input)
	        if(!deleteMe.equals(item))
	            result.add(item);

	    return (String[]) result.toArray(input);
	}
	
	public static int[] removeCapacite(int[] input, int deleteMe) {
		int[] capacites=new int[input.length-1];
		int i=0;

	    for(int item : input) {
	        if(item==deleteMe) {
	            continue;
	        }
	        else {
	        	capacites[i]=item;
	    		i++;
	        }
	    }
	    return capacites;
	}
	
	
	private int findIndex( String[] list, String value) {
		int i=0;
	    for(String ip: list) { 
	         if(ip.equals(value)) {
	             return i;
	         }
	    i++;
	    }
	    return (Integer) null;
	}
	
	private List<Map<String , String>> dataToResend(Map<String,String> dataServer, int[]capaciteServeurs){
		List<Map<String , String>> dataServers = null;
		int sum = IntStream.of(capaciteServeurs).sum();
		int[] SizeServers=new int[capaciteServeurs.length];
		int NbLines=dataServer.size();
		int i1=0;
		while(i1<SizeServers.length) {
			int NbLinesForOneServer= (int) capaciteServeurs[i1]*NbLines/sum;
			i1+=1;
			SizeServers[i1]=NbLinesForOneServer;
		}
		int c=0;
		int c2=0;
		int c3=0;
		for(int i: SizeServers) {
		
				for(String key: dataServer.keySet()) {
					if(c2*c3 <= c && c < (i+c3)) {
						dataServer.put(key,dataServer.get(key));
						c++;
					}
					
				}
				dataServers.add(c2,dataServer);
				c3=c;
				c2++;
		}
		return null;
		
	}
}
