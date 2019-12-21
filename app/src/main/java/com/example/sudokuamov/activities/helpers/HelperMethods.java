package com.example.sudokuamov.activities.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class HelperMethods {
    private static final int RESIZED_WIDTH = 100;
    private static final int RESIZED_HEIGHT = 100;

    public static void ResizeImages(String sPath, String sTo) throws IOException {

        Bitmap photo = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CancellationSignal signal = new CancellationSignal();
                /** @hide */
                photo = ThumbnailUtils.createImageThumbnail(
                        new File(sPath),
                        new Size(RESIZED_WIDTH, RESIZED_HEIGHT),
                        signal);

                signal.throwIfCanceled();
            } else {
                //The deprecated createImageThumbnail is not working properly for some reason
                //photo = ThumbnailUtils.createImageThumbnail(sPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                photo = BitmapFactory.decodeFile(sPath);
                photo = getResizedBitmap(photo, RESIZED_HEIGHT, RESIZED_WIDTH);
            }
            if (photo != null) {
                try {
                    File f = new File(sTo);

                    //The output stream to write the compressed the user photo
                    FileOutputStream out = new FileOutputStream(f);

                    photo.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (OperationCanceledException c) {
            c.printStackTrace();
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

        //Rotate is needed here because somehow the bitmap factory is getting my image rotated
        matrix.postRotate(-90);

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
