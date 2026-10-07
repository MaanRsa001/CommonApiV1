package com.maan.eway.document.service.impl;

import java.io.InputStream;
import java.io.OutputStream;

public class SyncPipe implements Runnable{
	
	private final InputStream istrm;
	private final OutputStream ostrm;
	
	public SyncPipe(InputStream istrm,OutputStream ostrm) {
		this.istrm = istrm;
		this.ostrm = ostrm;
	}

	@Override
	public void run() {

		try {
			final byte[] buffer = new byte[1024];
			
			for(int length = 0;(length = istrm.read(buffer))!= -1;) {
				ostrm.write(buffer, 0, length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
