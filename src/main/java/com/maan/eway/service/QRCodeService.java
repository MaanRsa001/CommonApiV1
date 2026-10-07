package com.maan.eway.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.util.Map;
import java.util.HashMap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Service
public class QRCodeService {

	public byte[] generateQRCode(String text) throws Exception {

		System.out.println("QR TEXT: " + text);
		System.out.println("QR TEXT LENGTH: " + text.length());
		
		int width = 1000;
	    int height = 1000;
	    
	    Map<EncodeHintType, Object> hints = new HashMap<>();
	    
	 // Good balance between data capacity and error correction
	    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

	    // White border around QR
	    hints.put(EncodeHintType.MARGIN, 4);

	    BitMatrix bitMatrix = new MultiFormatWriter().encode(
	            text,
	            BarcodeFormat.QR_CODE,
	            width,
	            height,
	            hints
	    );

        BufferedImage qrImage =
                MatrixToImageWriter.toBufferedImage(bitMatrix);

        System.out.println("QR IMAGE WIDTH: " + qrImage.getWidth());
        System.out.println("QR IMAGE HEIGHT: " + qrImage.getHeight());
        
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ImageIO.write(
                qrImage,
                "PNG",
                outputStream
        );

        return outputStream.toByteArray();
    }
}
