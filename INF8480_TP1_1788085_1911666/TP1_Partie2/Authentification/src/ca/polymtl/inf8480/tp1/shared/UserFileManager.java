package ca.polymtl.inf8480.tp1.shared;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;


public class UserFileManager {
    private FileLockManager fileLockManager = new FileLockManager();

    public boolean compareChecksum(String fileName, String checksum){
        return getChecksum(fileName).equals(checksum);
    }

    public static String getChecksum(String fileName){
        String checksum = "";
        try{
            String content = getContent(fileName);
            byte[] messageBytes = content.getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(messageBytes);
            BigInteger bigInt = new BigInteger(1, digest);
            checksum = bigInt.toString(16);

        }catch(Exception exception){
            exception.printStackTrace();
        }

        return checksum;

    }

    public static boolean writeFile(String fileName, String content){
        FileWriter writer = null;
        BufferedWriter buffer = null;
        PrintWriter out = null;
        try {
            writer = new FileWriter(fileName, false);
            buffer = new BufferedWriter(writer);
            out = new PrintWriter(buffer);
            out.println(content);

            out.close();
            buffer.close();
            writer.close();
            return true;

        } catch (Exception e) {
            System.out.println("Impossible de creer le fichier");
            return false;
        }
        finally {
            try {
                out.close();
                buffer.close();
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }



    public static String getContent(String fileName){
        String content = null;
        File file = new File(System.getProperty("user.dir") + File.separator + fileName);

        BufferedReader br;
        StringBuilder stringBuilder;
        try {
            br = new BufferedReader(new FileReader(file));
            stringBuilder = new StringBuilder();
            String line = br.readLine();

            while (line != null){
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = br.readLine();
            }

            content = stringBuilder.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return content;

    }



    public boolean createFile(String fileName){
        try{
            File newFile = new File(fileName);
            if(!newFile.exists())
                return newFile.createNewFile();
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
        return false;
    }




}
