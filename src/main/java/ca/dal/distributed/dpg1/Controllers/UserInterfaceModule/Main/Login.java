package ca.dal.distributed.dpg1.Controllers.UserInterfaceModule.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

public class Login {

    Hashing encrypt = new Hashing();

    public boolean checkLogin(String userName, String PassWord, String securityAnswer)
            throws FileNotFoundException, NoSuchAlgorithmException {

        HashMap<String, String> credentials = new HashMap<>();
        HashMap<String, String> credentialsSecurity = new HashMap<>();

        File file = new File("User_Profile.txt");
        Scanner f = new Scanner(file);

        while (f.hasNext()) {
            String[] cred_info = f.nextLine().split(" ");
            credentials.put(cred_info[0], cred_info[1]);
            credentialsSecurity.put(cred_info[0], cred_info[2]);
        }

        if (credentials.containsKey(encrypt.hash(userName))) {
            if (credentials.get(encrypt.hash(userName)).equals(encrypt.hash(PassWord))) {
                if (credentialsSecurity.get(encrypt.hash(userName)).equals(encrypt.hash(securityAnswer))) {
                    return true;
                }
            }

        }
        return false;

    }
}
