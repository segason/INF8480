package ca.polymtl.inf8480.tp2.Repartiteur;

import ca.polymtl.inf8480.tp2.shared.NomsInterface;
import ca.polymtl.inf8480.tp2.shared.ServerInterface;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Travail {
	private String file="";
	public Travail(String file1) {
		file=file1;
	}
	
	public int[] diviserLines(int[] capaciteServeurs) throws IOException {
		
		int sum = IntStream.of(capaciteServeurs).sum();
		int[] sizeFileServer=new int[capaciteServeurs.length];
		int NbLines=count();
		int i1=0;
		while(i1<sizeFileServer.length) {
			int NbLinesForOneServer= (int) capaciteServeurs[i1]*NbLines/sum;
			
			sizeFileServer[i1]=NbLinesForOneServer;
			i1+=1;
		}
		sizeFileServer[sizeFileServer.length-1]=sizeFileServer[sizeFileServer.length-1]+(NbLines-IntStream.of(sizeFileServer).sum());
		return sizeFileServer;
		
	}
	
	//"/Users/djemi/Desktop/tp2_inf8480/operations-3216.txt"
	public int count() throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(file));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	
	public List<Map<String , String>> dataToSend(int[]NblinesServer, String file) throws IOException{
		String line=null;
		Map<String,String> dataServer = new HashMap<String, String>();
		List<Map<String , String>> dataServers  = new ArrayList<Map<String,String>>();
		int c=0;
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int c2=0;
		int c3=0;
		//String[] line2;
		//System.out.println(NblinesServer.length);
		for(int i: NblinesServer) {
			
			while(c3 <= c && c < (i+c3)) {
				line = bufferedReader.readLine();
				//line2=line.split(" ");
				dataServer.put(Integer.toString(c), line);
				c++;
				//System.out.println(line2[0]+ ",,," + line2[1]);
				//System.out.println(dataServer.size());
			}
			dataServers.add(c2,dataServer);
			//System.out.println(dataServer);
			//System.out.println(dataServer.size());
			c3=c;
			c2++;
			dataServer = new HashMap<String, String>();
			
			
		}
		
		//System.out.println(dataServers);
		return dataServers;
	}
	//cette methode redivise le travail a donne aux serveurs. Lorsque un serveur tombe on donne son travail aux autre serveurs selon leurs capacités
	
}











