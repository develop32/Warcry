package com.jsneideris.warcry.lib;

import java.security.MessageDigest;
import java.security.SecureRandom;;

public class Encryption
{
	private static SecureRandom random = new SecureRandom();
	
	public static String randomToken()
	{
		byte[] bytes = new byte[16];
		random.nextBytes(bytes);
		return bytesToHex(bytes);
	}
	
	// http://stackoverflow.com/a/9855338/2970947
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	private static String bytesToHex(byte[] bytes) 
	{
		char[] hexChars = new char[bytes.length * 2];
		
		for (int j = 0; j < bytes.length; j++) 
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		
		return new String(hexChars);
	}
	
	public static String encrypt(String text) 
	{
		try 
		{
		    MessageDigest md = MessageDigest.getInstance("SHA-256");
		    md.update("cantsee".getBytes());
		    md.update(text.getBytes());

		    byte[] out = md.digest();
		    return bytesToHex(out);
		} 
		catch (Exception e) 
		{
			System.out.println("Could not encrypt password");
		}
		
		return "";
	}
}
