import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;

public class Test
{
	public static void main(String[] args)
	{
		System.out.println("========encode==========");
		 JSONObject obj1=new JSONObject();
		  obj1.put("name","foo");
		  obj1.put("num",new Integer(100));
		  obj1.put("balance",new Double(1000.21));
		  obj1.put("is_vip",new Boolean(true));
		  obj1.put("nickname",null);
		  System.out.println(obj1);
		  
		  System.out.println("========decode==========");
		  JSONParser parser = new JSONParser();
		  Object o = new Object();
		try {
			o = parser.parse(obj1.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Map a = (Map)o;
		  System.out.println("======the elements of map======");
		  System.out.println(a.toString());
		  System.out.println("======the value of balance======");
		  System.out.println(a.get("balance"));
	}
}