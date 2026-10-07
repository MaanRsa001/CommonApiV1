package com.maan.eway.payment.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class IveriPaymentGatewayUtil {

	public static int getUnixTimeStampUTC() {
	    int unixTimeStamp;
	    java.time.Instant currentTime = java.time.Instant.now();
	    java.time.Instant unixEpoch = java.time.Instant.EPOCH;
	    unixTimeStamp = (int) java.time.Duration.between(unixEpoch, currentTime).getSeconds();
	    return unixTimeStamp;
	}
	
	public static String generateAuthenticationToken(String sharedSecret, String resource, String queryString, String data, String time) {
	    // Combine all input strings into a single byte array
	    byte[] sourceBytes = (time + resource + queryString + data).getBytes(StandardCharsets.UTF_8);

	    // Generate HMAC-SHA256 hash and return the result
	    return getHmacSha256(sharedSecret.getBytes(StandardCharsets.US_ASCII), sourceBytes);
	}

	// Sample HMAC-SHA256 method
	public static String getHmacSha256(byte[] key, byte[] data) {
	    try {
	        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
	        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(key, "HmacSHA256");
	        mac.init(secretKeySpec);

	        byte[] hash = mac.doFinal(data);
	        // Convert to a Base64 encoded string (or hex string if required)
	        return Base64.getEncoder().encodeToString(hash);
	    } catch (Exception e) {
	        throw new RuntimeException("Error generating HMAC-SHA256", e);
	    }
	}
	public static void main (String args[]) {
		IveriPaymentGatewayUtil u=new IveriPaymentGatewayUtil();
		String sharedSecret ="DWLszg-ED63xwES73st6SwXRS6DGO-iBhIonQQHqCBw";
		String resource="https://portal.nedsecure.co.za/api/merchant/configuration";
		String queryString="mode=test";
		String data="";
		String time=getUnixTimeStampUTC()+"";
		String authenticationToken = u.generateAuthenticationToken(sharedSecret,  resource,  queryString,  data,  time);
		System.out.println(authenticationToken);
	}
}
