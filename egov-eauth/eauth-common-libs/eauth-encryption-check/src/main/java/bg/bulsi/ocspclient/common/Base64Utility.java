package bg.bulsi.ocspclient.common;

import org.bouncycastle.util.encoders.Base64;

public class Base64Utility {

	public static String encode(String text) {
		byte[] textInBytes = text.getBytes();
		byte[] encodedCertificateBytes = Base64.encode(textInBytes);
		String encodedTextString = new String(encodedCertificateBytes);
		
		return encodedTextString;
	}
	
	
	public static String encode(byte[] inputBytes) {
		byte[] encodedCertificateBytes = Base64.encode(inputBytes);
		String encodedTextString = new String(encodedCertificateBytes);
		
		return encodedTextString;
	}
	
	
	public static String decode(String encodedText) {
		byte[] decodedTextButes = Base64.decode(encodedText);
		String decodedTextString = new String(decodedTextButes);
		
		return decodedTextString;
	}
	
	
	public static byte[] decode(byte[] inBytes) {
		byte[] decodedBytes = Base64.decode(inBytes);
		return decodedBytes;
	}
	
	
	public static void main(String[] args) {
		String text = "Jordan Jovkov";
		System.out.println("Original: " + text);
		
		String encodedString = Base64Utility.encode(text.getBytes());
		System.out.println("Encoded: " + encodedString);
		
		String decodedString = Base64Utility.decode(encodedString);
		System.out.println("Decoded: " + decodedString);
		
	}
	
}
