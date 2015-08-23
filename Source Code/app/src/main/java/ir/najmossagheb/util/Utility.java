package ir.najmossagheb.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by r.kiani on 05/23/2015.
 */
public class Utility {
    public static int getScreenWidth(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        return size.y;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap,Context context)
    {
        int outW,outH;
        int inW = bitmap.getWidth(),inH = bitmap.getHeight();

        if(inW > inH)
        {
            outH = getScreenHeight(context);
            outW = (inW * outH) / inH;
        }
        else
        {
            outW = getScreenWidth(context);
            outH = (inH * outW) / inW;
        }

        return Bitmap.createScaledBitmap(bitmap,outW,outH,false);
    }
}
