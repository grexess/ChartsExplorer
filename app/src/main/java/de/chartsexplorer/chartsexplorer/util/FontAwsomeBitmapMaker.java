package de.chartsexplorer.chartsexplorer.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class FontAwsomeBitmapMaker {

    public static Bitmap getBitmap(String image, String color, AssetManager am){

        Bitmap myBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(am, "fontawesome-webfont.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(color));
        paint.setTextSize(100);
        myCanvas.drawText(image, 50, 150, paint);
        return myBitmap;
    }

}
