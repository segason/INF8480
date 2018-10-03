package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Client {
	public static void main(String[] args) {
		String distantHostname = null;

		if (args.length > 1) {
			distantHostname = args[0];
		}

		Client client = new Client(distantHostname);
		client.run(args[1]);
	}

	FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void run(String args) {
		int x=Integer.parseInt(args);
		appelNormal(x);

		if (localServerStub != null) {
			appelRMILocal(x);
		}

		if (distantServerStub != null) {
			appelRMIDistant(x);
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

	private void appelNormal(int x) {
		long start = System.nanoTime();
		int size=(int)Math.pow(10,x );
		byte[] b=new byte[size];
		localServer.execute(b,x);
		long end = System.nanoTime();

		System.out.println("Temps écoulé appel normal: " + (end - start)
				+ " ns");
		System.out.println("Résultat appel normal: valeur de x=" + x);
	}

	private void appelRMILocal(int x) {
		try {
			long start = System.nanoTime();
			int size=(int)Math.pow(10,x );
			byte[] b=new byte[size];
			localServerStub.execute(b, x);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI local: " + (end - start)
					+ " ns");
			System.out.println("Résultat appel normal: valeur de x=" + x);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void appelRMIDistant(int x) {
		try {
			long start = System.nanoTime();
			int size=(int)Math.pow(10,x );
			byte[] b=new byte[size];
			distantServerStub.execute(b, x);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI distant: "
					+ (end - start) + " ns");
			System.out.println("Résultat appel normal: valeur de x=" + x);
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}
}
