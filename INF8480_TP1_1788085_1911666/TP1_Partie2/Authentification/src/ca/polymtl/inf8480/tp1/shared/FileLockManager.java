package ca.polymtl.inf8480.tp1.shared;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileLockManager {
    private HashMap<String, String> lockMetadata = new HashMap<>();
    private final static String METADATA = "metadata.txt";

    public HashMap<String, String> getLockMetadata() {
        return lockMetadata;
    }

    public void setLockMetadata(HashMap<String, String> lockMetadata) {
        this.lockMetadata = lockMetadata;
    }

    public FileLockManager(){
        readLockMetadata();
    }

    private void readLockMetadata(){
        // TODO Auto-generated method stub
        File file = new File(System.getProperty("user.dir") + File.separator + METADATA);
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
            String[] lockInfo;
            String fileName;
            String username;
            while ((line = br.readLine()) != null) {
                lockInfo = line.split(" ");
                fileName = lockInfo[0];
                if(lockInfo.length == 2)
                    username= lockInfo[1];
                else
                    username = "";
                lockMetadata.put(fileName, username);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void updateMetadata(String fileName, String username){
        lockMetadata.put(fileName, username);
        rewriteMetadata(false);
    }


    public void rewriteMetadata(boolean append){
        File file = new File(System.getProperty("user.dir") + File.separator + METADATA);
        FileWriter writer = null;
        BufferedWriter buffer = null;
        PrintWriter out = null;
        try {
            writer = new FileWriter(file, append);
            buffer = new BufferedWriter(writer);
            out = new PrintWriter(buffer);
            Iterator it = lockMetadata.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry) it.next();
                out.println(pair.getKey()+ " " + pair.getValue());
            }
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

    public void addToMetaData(String fileName, String username){
        lockMetadata.put(fileName, username);
        rewriteMetadata(true);
    }

    public boolean lock(String fileName, String checkSum, String username){
        if(lockMetadata.containsKey(fileName)) {
            String lockOwner = getLockOwner(fileName);
            if (lockOwner.equals("") || lockOwner.equals(username)){
                if(UserFileManager.getChecksum(fileName) != checkSum){
                    updateMetadata(fileName, username);
                }
                updateMetadata(fileName, username);
                return true;
            }
        }
        return false;

    }


    public void removeLock(String fileName){
        updateMetadata(fileName, "");
    }

    public String getLockOwner(String filename){
        return lockMetadata.get(filename);
    }
}

