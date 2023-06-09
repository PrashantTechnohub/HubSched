package com.NakshatraTechnoHub.HubSched.UtilHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class QRCodeGeneratorUtil {


    public static Bitmap generateQRCode( String response) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(response, BarcodeFormat.QR_CODE, 400, 400);

            return bitmap;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}