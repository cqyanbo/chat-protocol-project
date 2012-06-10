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
	
	//security concept http://java.sun.com/developer/technicalArticles/Security/AES/AES_v1.html
    
   //main method to test security
	/*
    public static void main(String args[])throws java.io.IOException
        {
    	try
    	{
    		Security S = new Security();
    		byte [] digest = S.GenerateDigest("test");
    		System.out.println("digest is: "+digest);
    		String s;
    		s= "0001000000010000000000000000000000000000";
    		String encrypt = S.encrypt(s);
    		System.out.println("encrypted is: "+encrypt);
    		
    		String decypt = S.decrypt(encrypt);
    		System.out.println("decrypted is: "+decypt);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error");
    	}
    		
        }
      
	
	//store keys for AES
    private static final byte[] keyValue = 
        new byte[] { '1', '2', '3', '4', '5', 'O', 'K','S', 'e', 'c', 'u','r', 'e', '#', '@', 'k' };

    
    //Encrypts clear string
    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    //Decrypts  string
    public static String decrypt(String encryptedData) throws Exception {
    	try{
    		encryptedData.trim();
        Key key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    	}
    	catch(Exception e)
    	{
    		return "";
    	}
    }

    //generate ket for methods
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;

    }
    
    */
    //md5 hashing for digests
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
