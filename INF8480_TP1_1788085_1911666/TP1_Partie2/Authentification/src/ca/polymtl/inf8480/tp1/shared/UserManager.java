package ca.polymtl.inf8480.tp1.shared;

import java.io.*;
import java.util.HashMap;

public class UserManager {
    private HashMap<String, String> credentials = new HashMap<>();
    private final static String CREDENTIALS_FILE_NAME = "clientMetadata.txt";

    public UserManager() {
        readCredentialsFromFile();
    }

    private void readCredentialsFromFile() {
        // TODO Auto-generated method stub
        File file = new File(System.getProperty("user.dir") + File.separator + CREDENTIALS_FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            String[] userInfo;
            String user;
            String userPassWord;
            while ((line = br.readLine()) != null) {
                userInfo = line.split(" ");
                if(userInfo[0] != null || !userInfo[0].equals("null")){
                    user = userInfo[0];
                    userPassWord = userInfo[1];
                    credentials.put(user, userPassWord);
                }
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean verify(String username, String password) {
        if(credentials.containsKey(username))
            return credentials.get(username).equals(password);
        return false;
    }

    public boolean createNewUser(String username, String password){
        if(!credentials.containsKey(username)) {
            writeToMetadata(username, password);
            credentials.put(username, password);
            return true;
        }
        return false;
    }

    public void writeToMetadata(String username, String password){
        File file = new File(System.getProperty("user.dir") + File.separator + CREDENTIALS_FILE_NAME);
        FileWriter writer = null;
        BufferedWriter buffer = null;
        PrintWriter out = null;
        try {

            writer = new FileWriter(file, true);
            buffer = new BufferedWriter(writer);
            out = new PrintWriter(buffer);
            out.println(username + " " + password);
            out.close();

        } catch (Exception e) {
            System.out.println("Impossible de creer le fichier");
        }
        finally{

            try {
                if(out != null)
                    out.close();
                if(buffer != null)
                    buffer.close();
                if(writer != null)
                    writer.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
