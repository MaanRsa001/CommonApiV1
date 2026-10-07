package com.maan.eway.payment.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ApigwClient {
    String baseUrl;
    String apiKey;
    String apiSecret;

    public  ApigwClient(String baseUrl, String apiKey, String apiSecret){
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

    }
    public  Map<String,Object> computeHeader( JsonObject dataMap) {
        Map<String,Object> header = new HashMap<>();
        String encodekey = Base64.getEncoder().encodeToString((apiKey).getBytes());
        String authToken = "SELCOM "+ encodekey;
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date now = new Date();
        String timestamp = sdfDate.format(now);
        
        String message = "", signed_fields = "";
    
        List<String> keys = new ArrayList<String>();
        List<String> serializedJson = new ArrayList<String>();
        serializedJson.add("timestamp="+timestamp);
    
        for (Object key : dataMap.keySet()) {
            String keyStr = (String)key;
            System.out.println("keyStr"+keyStr);
            String keyvalue = dataMap.get(keyStr)==null ?"":dataMap.get(keyStr).getAsString();
            serializedJson.add(keyStr+"="+keyvalue);
            keys.add(keyStr);
    
        }
    
        message =  String.join("&", serializedJson);
        signed_fields =  String.join(",", keys);
    
        try {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
        
            sha256_HMAC.init(secret_key);
       
    
        String digest = new String(Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes())));
        
    
        header.put("Content-type", "application/json");
        header.put("Authorization", authToken);
        header.put("Digest-Method", "HS256");
        header.put("Digest", digest);
        header.put("Timestamp", timestamp);
        header.put("Signed-Fields", signed_fields);
    
        return header;
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return header;
            
        }
    
    }
    
    public  JsonObject postFunc(String path, JsonObject jsonData){
        Map <String,Object> header = computeHeader(jsonData);
        String url = this.baseUrl + path;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            Gson gson = new Gson();
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity( jsonData.toString());

            System.out.println(url);
            System.out.println( jsonData.toString());
            for (Object key : header.keySet()) {
                request.addHeader(key.toString(), header.get(key).toString());
                System.out.println(key.toString()+":"+ header.get(key).toString());
            }

            request.setEntity(params);
            HttpResponse hresp  = httpClient.execute(request);

            HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
            System.out.println("Url"+ url + "output"+ apiOutput.toString());
            System.out.println("output"+ apiOutput.toString());

            return new Gson().fromJson(apiOutput, JsonObject.class);
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        }finally {
        	if(httpClient!=null)
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 
    }

    public  JsonObject getFunc(String path, JsonObject jsonData){
        Map<String, Object> header = computeHeader(jsonData);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        List<String> qstring_list = new ArrayList<String>();
        
        for (String key : jsonData.keySet()) {
            qstring_list.add(key.toString() + "="+jsonData.get(key).getAsString());
            
        }

        try {
            String url = this.baseUrl + path + "?" + String.join("&", qstring_list);
            HttpGet request = new HttpGet(url);

            for (Object key : header.keySet()) {
                request.addHeader(key.toString(), header.get(key).toString());
            }

            HttpResponse hresp  = httpClient.execute(request);

            HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
        
            

            return new Gson().fromJson(apiOutput, JsonObject.class);
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        }finally {

        	if(httpClient!=null)
				try {
					httpClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}  
  

    }

    public JsonObject deleteFunc(String path, JsonObject jsonData){
        Map<String,Object> header = computeHeader(jsonData);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        List<String> qstring_list = new ArrayList<String>();

        for (String key : jsonData.keySet()) {
            qstring_list.add(key.toString() + "="+jsonData.get(key).getAsString());
        }

        try {
            String url = this.baseUrl + path + "?" + String.join("&", qstring_list);
            HttpDelete request = new HttpDelete(url);

            for (Object key : header.keySet()) {
                request.addHeader(key.toString(), header.get(key).toString());
            }

            HttpResponse hresp  = httpClient.execute(request);

            HttpEntity httpEntity = hresp.getEntity();
            String apiOutput = EntityUtils.toString(httpEntity);
            return new Gson().fromJson(apiOutput, JsonObject.class);
        } catch (Exception ex) {
            JsonObject err = new JsonObject();
            err.addProperty("error", ex.getMessage());
            return err;
        } 

        
  

    }
    
}
