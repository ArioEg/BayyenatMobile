package ir.najmossagheb.service;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ir.najmossagheb.R;
import ir.najmossagheb.db.BayyenatDbHelper;
import ir.najmossagheb.model.Hadith;
import ir.najmossagheb.model.NodeWallpaper;
import ir.najmossagheb.model.Tag;
import ir.najmossagheb.preferences.ConfigurationManager;
import ir.najmossagheb.util.Utility;

import static android.content.Context.MODE_PRIVATE;

public class WallpaperService extends Service {
    Handler mHandler;
    WallpaperService mInstance;
    int i=0;

    Context context;

    public WallpaperService() {
        mHandler = new Handler();
        context = this;
        mHandler.postDelayed(mRunnable,500);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            ChangeWallpaper();
            mHandler.postDelayed(mRunnable,Long.parseLong(ConfigurationManager.getInstance(context).getRefreshFreq())*60*1000);
        }
    };

    public void ChangeWallpaper()
    {
        ConfigurationManager config = ConfigurationManager.getInstance(this);
        if(!config.isAutoRefresh()) {
            mHandler.removeCallbacksAndMessages(mRunnable);
            return;
        }

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        List<NodeWallpaper> wallpapers = BayyenatDbHelper.getInstance(this).getSelectedWallpapers();
        if(wallpapers.size() == 0) return;
        int wprand = random.nextInt(wallpapers.size());
        NodeWallpaper wp = wallpapers.get(wprand);

        List<Tag> tags = BayyenatDbHelper.getInstance(this).getSelectedTags();
        if(tags.size() == 0) return;
        int tagrand = random.nextInt(tags.size());

        List<Hadith> ahadith = BayyenatDbHelper.getInstance(this).getHadithByTag(tags.get(tagrand));
        if(ahadith.size() == 0) return;
        int hadithrand = random.nextInt(ahadith.size());
        Hadith hd = ahadith.get(hadithrand);

        Bitmap bitmap = BitmapFactory.decodeFile(wp.url.replace("file:///",""));
        Bitmap drawableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        //TODO: Add this to Preferences...
        if (drawableBitmap.getWidth() > Utility.getScreenWidth(this) || drawableBitmap.getHeight() > Utility.getScreenHeight(this))
        {
            drawableBitmap = Utility.scaleBitmap(drawableBitmap,this);
        }


        Canvas canvas = new Canvas( drawableBitmap ); // Create Canvas for Rendering to Bitmap
        // load the font and setup paint instance for drawing
        Typeface tf = Typeface.createFromAsset( getAssets(), "MjFlow.ttf" ); // Create the Typeface from Font File
        Paint paint = new Paint(); // Create Android Paint Instance
        paint.setAntiAlias( true ); // Enable Anti Alias
        paint.setTextSize((float) (canvas.getHeight() * 0.03)); // Set Text Size
        paint.setColor(0xffffffff); // Set ARGB (White, Opaque)
        paint.setTypeface(tf); // Set Typeface

        Paint paint2 = new Paint(); // Create Android Paint Instance
        paint2.setAntiAlias(true); // Enable Anti Alias
        paint2.setColor(0x33000000); // Set ARGB (White, Opaque)


        int w=0,h=0;
        if(!config.isDrawScreenRect())
        {
            w = drawableBitmap.getWidth();
            h= drawableBitmap.getHeight();
        }
        else
        {
            w = Utility.getScreenWidth(this);
            h = Utility.getScreenHeight(this);
        }

        int colStart = (int) (w * 0.1);
        int colWidth = (int) (w - colStart * 2 - w * 0.05);
        int rowStart = (int) (h * 0.1);

        //TODO: Test portrait pictures ...
        //This is for landscape ones...
        if(drawableBitmap.getWidth() > drawableBitmap.getHeight())
        {
            float sw = ((float)640 / 520) * drawableBitmap.getHeight();
            float s = drawableBitmap.getWidth() - sw;
            colStart += (int) (s/2) + (!config.isDrawScreenRect()?(sw-colWidth)/2:(random.nextInt(5) * (sw-colWidth-w * 0.08)/5));
            if(!config.isDrawScreenRect()) colWidth -= s/2;
        }else if(drawableBitmap.getWidth() < drawableBitmap.getHeight()) {
            float sh = ((float) 520 / 640) * drawableBitmap.getWidth();
            float s = drawableBitmap.getHeight() - sh;
            rowStart += (int) (s / 2);
        }

        String text = hd.getText();
        text.replace("?","? ");
        text.replace("،","، ");
        text.replace("\n"," ");
        String[] words = text.split(" ");

        String line="";
        int lineCount = 0;

        int ex_height=0;
        if(hd.getAuthor() != null && hd.getAuthor() != "")
        {
            ex_height += paint.getTextSize()*1.5;
        }
        if(hd.getSource() != null && hd.getSource() != "")
        {
            ex_height += paint.getTextSize()*1.5;
        }

        canvas.drawRect(new Rect(colStart,rowStart,colStart+colWidth+10, (int) (Math.ceil(paint.measureText(text)/colWidth)*paint.getTextSize()*1.8 +rowStart)+ex_height),paint2);

        if(hd.getAuthor() != null && hd.getAuthor() != "")
        {
            Typeface tfAutor = Typeface.createFromAsset( getAssets(), "NaskhB.ttf" ); // Create the Typeface from Font File
            paint.setColor(getResources().getColor(R.color.colorAccent));
            paint.setTypeface(tfAutor); // Set Typeface
            canvas.drawText(hd.getAuthor(),colStart+colWidth-paint.measureText(hd.getAuthor()), (float) (paint.getTextSize()*1.5 +rowStart),paint);
            lineCount++;
        }

        paint.setColor(0xffffffff); // Set ARGB (White, Opaque)
        paint.setTypeface(tf); // Set Typeface
        for (int i = 0; i <words.length; i++) {
            line = "";
            while (paint.measureText(line) < colWidth && i < words.length)
            {
                line = line + " " + words[i];
                if(i+1 < words.length && (paint.measureText(line + " " + words[i+1]) <= colWidth))
                    i++;
                else break;
            }
            lineCount++;

            canvas.drawText(line,colStart+colWidth-paint.measureText(line), (float) (lineCount*paint.getTextSize()*1.5 +rowStart),paint);
        }

        if(hd.getSource() != null && hd.getSource() != "")
        {
            float currentY = (float) ((lineCount)*paint.getTextSize()*1.5 +rowStart+paint.getTextSize()/2);
            lineCount =0;
            Typeface tfSource = Typeface.createFromAsset( getAssets(), "NaskhB.ttf" ); // Create the Typeface from Font File
            paint.setColor(getResources().getColor(R.color.colorAccent));
            paint.setTextSize(paint.getTextSize()/2);
            paint.setTypeface(tfSource); // Set Typeface
            //lineCount++;
            //canvas.drawText(hd.getSource(),colStart+colWidth-paint.measureText(hd.getSource()), (float) (lineCount*paint.getTextSize()*1.5 +rowStart),paint);

            text = hd.getSource();
            text.replace("?","? ");
            text.replace("،","، ");
            text.replace("\n"," ");
            words = text.split(" ");
            for (int i = 0; i <words.length; i++) {
                line = "";
                while (paint.measureText(line) < colWidth && i < words.length)
                {
                    line = line + " " + words[i];
                    if(i+1 < words.length && (paint.measureText(line + " " + words[i+1]) <= colWidth))
                        i++;
                    else break;
                }
                lineCount++;
                canvas.drawText(line,colStart+colWidth-paint.measureText(line),currentY + ((float) (lineCount*paint.getTextSize()*1.5 )),paint);
            }
        }

        final WallpaperManager wpManager = WallpaperManager.getInstance(this);
        if (wpManager == null) {
            Toast.makeText(this, getString(R.string.change_wallpaper_error), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            wpManager.setBitmap(drawableBitmap);
            //Toast.makeText(this, "Wallpaper Set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new ConfigurationManager(this).setServiceStarted(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, getString(R.string.service_stopped), Toast.LENGTH_LONG).show();
        new ConfigurationManager(this).setServiceStarted(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO:delete this line...
        Toast.makeText(this, getString(R.string.service_started), Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
