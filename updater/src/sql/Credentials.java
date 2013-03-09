/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import java.io.*;
import sql.Connector;

/**
 *
 * @author Alex Hughes
 */
public class Credentials implements Serializable {

    public static void saveCredentials(Credentials cre) {
        try {

            FileOutputStream fileOut;
            if (cre instanceof DBCredentials) {
                fileOut = new FileOutputStream("DBCredentials.dat");
            } else {
                fileOut = new FileOutputStream("StructCred.dat");
            }
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(cre);
            out.close();
            fileOut.close();

            if (Connector.LOGGER) {
                System.out.println("DBCredentials Saved Succesfully");
                System.out.println("Struct Credentials Saved Succesfully");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("*** DBCredentials not found ***");
            System.err.println("*** Struct Credentials not found ***");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("*** Error while saving dbcredentials ***");
            System.err.println("*** Error while saving structcredentials ***");
        }
    }

    public static Credentials loadCredentials(String aFile) {
        Credentials cre = null;

        try {
            FileInputStream fileIn = new FileInputStream(aFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            Object obj = in.readObject();

            if (obj instanceof DBCredentials) {
                cre = (DBCredentials) obj;
            } else {
                //cre = (DBStruct) obj;
            }

            in.close();
            fileIn.close();

            if (Connector.LOGGER) {
                if (cre instanceof DBCredentials) {
                    System.out.println("DBCredentials loaded succesfully");
                } else {
                    System.out.println("Struct Credentials loaded succesfully");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("*** Credentials not found ***");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("*** Error while loading credentials ***");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("*** Credentials' file probably corrupted ***");
        }
        return cre;
    }
}
