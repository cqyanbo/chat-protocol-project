/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.String;
import java.security.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author omarkabeer
 */
public class Security {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static final byte[] keyValue = 
        new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
    private Key AESKey;
    
    Security()
    {
        generateRSAkeys();
    }
    

    void generateRSAkeys()
    {
        try
        {
            // Generate a 512-bit Digital Signature Algorithm (RSA) key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(512);
            KeyPair keypair = keyGen.genKeyPair();
            privateKey = keypair.getPrivate();
            publicKey = keypair.getPublic();
            
            
            //generate AES key
            AESKey = new SecretKeySpec(keyValue, "AES");
            //
        }
        catch (java.security.NoSuchAlgorithmException e) {
    }
    }
    
    PublicKey getpublicRSAkey()
    {
        return publicKey;
    }
    
    PrivateKey getprivateRSAkey()
    {
        return privateKey;
    }
      //http://www.digizol.org/2009/10/java-encrypt-decrypt-jce-salt.html   
    Key getAESKey()
    {
        return AESKey;
    }
    //    //return byte array ofmd5
//    byte[] GenerateDigest()
//    {
//        try
//        {
//            String strID= "OmarKabeer12345";
//            byte[] bytesOfMessage = strID.getBytes("UTF-8");
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] thedigest = md.digest(bytesOfMessage);
//            return thedigest;
//        }
//        catch(Exception e)
//        {
//             throw new DigestException("couldn't make digest of partial content");
//        }
//    }
}
