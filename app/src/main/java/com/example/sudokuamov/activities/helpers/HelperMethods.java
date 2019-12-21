package com.example.sudokuamov.activities.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.CancellationSignal;
import android.util.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class HelperMethods {
    private static final int RESIZED_WIDTH = 100;
    private static final int RESIZED_HEIGHT = 100;

    public static void ResizeImages(String sPath, String sTo) throws IOException {

        Bitmap photo = ThumbnailUtils.createImageThumbnail(
                new File(sPath),
                new Size(100, 100),
                new CancellationSignal());


        File f = new File(sTo);
        try {
            FileOutputStream out = new FileOutputStream(f);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //If we want to delete the userPhoto that is not the thumbnail. uncomment below code
        /*File file =  new File(sPath);
        file.delete();*/
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static Intent makeIntentForUserNameAndPhoto(String[] values, Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        intent.putExtra("nickName", values[0]);
        intent.putExtra("userPhotoPath", values[1]);
        intent.putExtra("userPhotoThumbPath", values[2]);

        return intent;
    }


}
