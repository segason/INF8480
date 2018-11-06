package ca.polymtl.inf8480.tp2.Repartiteur;
import java.util.*;
import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class ServeurThread implements Runnable{
	private String[] ipServeurs;
	private Map<String,String> dataServer;
	private ArrayList<Integer> result=new ArrayList<>();
	private String mode;
	private String ip;
	private int resultSecurise=0;
	public ServeurThread(String[] ipServeurs, Map<String,String> dataServer, String mode, String ip) {
		this.ipServeurs=ipServeurs;
		this.dataServer=dataServer;
		this.mode=mode;
		this.ip=ip;
	}
 
	public void run() {
		Communication communication = new Communication();
		ServerInterface stubServeur1;
		int i=0;
		if(this.mode.equals("NONSECURISE")) {
			
					for(String IP: ipServeurs) {
							
							try {
								int resultSecurise=0;
								int n = 0;
								stubServeur1 = communication.loadServerStub(IP);
								int capacity= communication.getCapacity(stubServeur1);
								int nLinesToSend =(int)( (5.0/100.0)*4*capacity + (float)capacity);
								List<Map<String, String>> data= split(this.dataServer, nLinesToSend);
								for(Map<String,String> morceau: data) {
									while(!stubServeur1.acceptRequest(nLinesToSend));
									resultSecurise= resultSecurise + stubServeur1.calculate(morceau);
									
								}
								
								result.add(resultSecurise);
								i++;
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								System.out.println("Probleme detecte !");
							}
					}
					
		}
   
   
		else if(this.mode.equals("SECURISE")) {
			try {
					int resultSecurise=0;
					int n = 0;
					stubServeur1 = communication.loadServerStub(this.ip);
					int capacity= communication.getCapacity(stubServeur1);
					int nLinesToSend =(int) ((5.0/100.0)*4*capacity + (float)capacity);
					List<Map<String, String>> data= split(this.dataServer, nLinesToSend);
					for(Map<String,String> morceau: data) {
						while(!stubServeur1.acceptRequest(nLinesToSend));
						resultSecurise= resultSecurise + stubServeur1.calculate(morceau);
						n += morceau.size();
					}
					this.resultSecurise=resultSecurise;
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			
								System.out.println("Probleme detecte !");
      
		}
		}
					
	}
 
 
	public int getResultModeSecurise() {
		return this.resultSecurise;
	}
	public ArrayList<Integer> getResult() {
		return this.result;
	}
	public Map<String,String> getDataNotsend(){
		return this.dataServer;
	}
	public String getIP(int i) {
		return this.ipServeurs[i];
	}
	public int getIpServeursNb() {
		return this.ipServeurs.length;
	}
	public Map<String,String> getData(){
		return this.dataServer;
	}
	public List<Map<String, String>> split(Map<String, String> original, int nLines) {

        int counter = 0;
        int lcounter = 0;
        List<Map<String, String>> listOfSplitMaps = new ArrayList<Map<String, String>> ();
        Map<String, String> splitMap = new HashMap<> ();

        for (Map.Entry<String, String> m : original.entrySet()) {
            if (counter < nLines) {
                    splitMap.put(m.getKey(), m.getValue());
                    counter++;
                    lcounter++;

                    if (counter == nLines || lcounter == original.size()) {
                            counter = 0;
                            listOfSplitMaps.add(splitMap);
                            splitMap = new HashMap<> ();
                    }
            }
        }

        return listOfSplitMaps;
	}
}
