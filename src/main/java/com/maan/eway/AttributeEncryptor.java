package com.maan.eway;

import org.springframework.beans.factory.annotation.Autowired;

import com.maan.eway.auth.token.EncryDecryService;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {
    
	@Autowired
	private EncryDecryService endecryService;
    
 

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
        	return endecryService.encrypt(attribute);
        } catch (Exception e) {
            e.printStackTrace();
            return attribute;
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
        	/*IvParameterSpec ivParams = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));*/
        	/*
        	IvParameterSpec ivParams = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        	cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
            return Base64.getEncoder().encodeToString(cipher.doFinal(dbData.getBytes()));*/
        	return endecryService.decrypt(dbData);
        } catch (Exception e) {
            //throw new IllegalStateException(e);
        	return dbData;
        }
    }
} 
