/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Basic;
import java.lang.String;
import java.security.*;

import javax.crypto.spec.IvParameterSpec;
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
    
   
    public static void main(String args[])
            throws java.io.IOException
        {
    	try
    	{
    		Security S = new Security();
    		byte [] digest = S.GenerateDigest("test");
    		System.out.println("digest is: "+digest);
    		String encrypt = encrypt("Omar");
    		System.out.println("encrypted is: "+encrypt);
    		String decypt = decrypt(encrypt);
    		System.out.println("decrypted is: "+decypt);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error");
    	}
    		
        }
    //http://www.digizol.org/2009/10/java-encrypt-decrypt-jce-salt.html   
    private static final String ALGO = "AES";
    private static final byte[] keyValue = 
        new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
    'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;

    }
    
  //Digest return byte array of md5
  public byte[] GenerateDigest(String input)
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
