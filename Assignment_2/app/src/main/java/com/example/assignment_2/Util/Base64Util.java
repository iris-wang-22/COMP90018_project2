package com.example.assignment_2.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Util {
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String bitMapToBase64(Bitmap bmp) throws IOException {
        String imageB64 = null;
        if (bmp != null){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bmp.recycle();

            bos.flush();
            bos.close();

            byte[] byteArray = bos.toByteArray();
            imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);
        }
        return imageB64;
    }



}
