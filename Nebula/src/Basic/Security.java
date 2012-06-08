/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basic;
import java.lang.String;
import java.security.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.*;
import javax.crypto.Cipher; 
import sun.misc.*;
import java.security.spec.InvalidKeySpecException;
/**
 *
 * 
 */
public class Security {
    
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static final byte[] keyValue = 
    		new byte[] { 'T', 'h', 'i', 's', 'I', 's', 'A', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
    private static Key AESKey;
    
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
    
    public PublicKey getpublicRSAkey()
    {
        return publicKey;
    }
    
    public PrivateKey getprivateRSAkey()
    {
        return privateKey;
    }
    
    //http://www.digizol.org/2009/10/java-encrypt-decrypt-jce-salt.html   
    public  byte[] encrypt(String valueToEnc) throws Exception
    {
        Key key = getAESKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        return encValue;
    }
    
    public static String decrypt(byte[] decordedValue) throws Exception {
        Key key = getAESKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    
    public static Key getAESKey()
    {
        return AESKey;
    }

    
  //Digest return byte array of md5
  byte[] GenerateDigest(String input)
    {
        try        {
            String strID= input;
            byte[] bytesOfMessage = strID.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            return thedigest;
        }
        catch(Exception e)
        {
        	byte[] temp= {0};
             return temp;
        }
    }
}
