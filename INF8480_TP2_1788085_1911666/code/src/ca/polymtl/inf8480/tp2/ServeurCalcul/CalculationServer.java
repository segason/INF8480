package ca.polymtl.inf8480.tp2.ServeurCalcul;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
public class CalculationServer implements ServerInterface{
    private int capacity;
    private String mode;
    private int errorRate;
    private NomsInterface distantServerStub = null;
    public static void main(String[] args) {
    
    if(args.length == 5){
        System.out.println(Arrays.toString(args));
        CalculationServer calculationServer = new CalculationServer(Integer.parseInt(args[4]), args[0], args[1], Integer.parseInt(args[3]), args[2]);
        calculationServer.run();
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
          public void run() {
          try{
            
        calculationServer.getStub().removeServeur(args[0]);
            }catch(RemoteException e){
          }
    }
}));}else{
    System.out.println("Vous n'avez pas respecte le nombre d'arguments necessaires.");
}
        
        
    }



    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServerInterface stub = (ServerInterface) UnicastRemoteObject
                    .exportObject(this, 5000);

            Registry registry = LocateRegistry.createRegistry(5000);
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
    public CalculationServer(int capacity, String ipServeurCalcul, String mode, int errorRate, String ipServeurNoms){
        
        super();
        
        distantServerStub = loadServerStub(ipServeurNoms);
        try {

            distantServerStub.ajouterServeur(ipServeurCalcul, capacity); 
        }catch(Exception e) {
        }
        this.capacity = capacity;
        this.mode = mode;
        this.errorRate = errorRate;
        
    }

    @Override
    public boolean acceptRequest(int nOperations) throws RemoteException{
        int refusalRate = calculateRefusalRate(nOperations);
        int generatedRefusalRate = generateRandomRate();
        return generatedRefusalRate >= refusalRate;
    }


    private int calculateRefusalRate(int nOperations) throws RemoteException{
        return (nOperations - capacity)*100 / (4 * capacity);
    }

    private int generateRandomRate() {
        Random random = new Random();
        return random.nextInt(Constants.MAX_REFUSAL_RATE);
    }

    

    private boolean isMaliciousServer() {
    	boolean isSecure = mode.equals(Constants.MODE_SECURISE);
    	boolean malicious = computeMaliciousness();
        return !isSecure && malicious;
    }

    private boolean computeMaliciousness(){
        int generatedErrorRate = generateRandomRate();
        return generatedErrorRate < errorRate;
    }


    @Override
    public int getCapacity() throws RemoteException{
        return capacity;
    }

    public void setCapacity(int capacity) throws RemoteException{
        this.capacity = capacity;
    }


    public String getMode() throws RemoteException{
        return mode;
    }

    public void setMode(String mode) throws RemoteException{
        this.mode = mode;
    }

    public int getErrorRate() throws RemoteException{
        return errorRate;
    }

    public void setErroneousAnswerRate(int errorRate) throws RemoteException{
        this.errorRate = errorRate;
    }

    @Override
    public int calculate(Map<String, String> operations) throws RemoteException {
    	System.out.println(operations);
        if(isMaliciousServer())
            return -1;
        int result = 0;
        Iterator it = operations.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            String value = (String)pair.getValue();
            String[] parts = value.split(" ");
            if(parts[0].equals(Constants.PELL)){
                result += Operations.pell(Integer.parseInt((String) parts[1]));
            }
            else{
                result += Operations.prime(Integer.parseInt((String) parts[1]));
            }
            result %= Constants.MOD_VALUE;
        }
        
        System.out.println(result);
        return result;
    }
    
    private NomsInterface loadServerStub(String hostname) {
        NomsInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, 5000);
            stub = (NomsInterface) registry.lookup("serveurNoms");
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
    
    public NomsInterface getStub(){
      return distantServerStub;
    }


}
