package ca.dal.distributed.dpg1.Controllers.UserInterfaceModule.Main;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    public String hash(String value) throws NoSuchAlgorithmException{
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(value.getBytes(),0,value.length());  
        return new BigInteger(1,m.digest()).toString(16); 
    }
    
}
