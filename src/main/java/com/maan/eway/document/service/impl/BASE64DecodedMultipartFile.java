package com.maan.eway.document.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class BASE64DecodedMultipartFile implements MultipartFile {
	
	private byte[] data;
	private String filename;
	public BASE64DecodedMultipartFile(byte[] data,String filename) {
		this.data=data;
		this.filename=filename;
	} 

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return filename;
	}

	@Override
	public String getOriginalFilename() {
		// TODO Auto-generated method stub
		return filename;
	}

	@Override
	public String getContentType() {
		if(data!=null  ||data.length==0) {
			Tika tika = new Tika();
			return tika.detect(data);
		}else
			return null;
	}

	@Override
	public boolean isEmpty() {
		return (data==null||data.length==0)?true:false;
	}

	@Override
	public long getSize() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(data);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub

	}

}
