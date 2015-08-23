package ir.najmossagheb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ir.najmossagheb.activity.WallpaperFragment;
import ir.najmossagheb.preferences.ConfigurationManager;
import ir.najmossagheb.service.WallpaperService;

/**
 * Created by r.kiani on 05/23/2015.
 */
public class autoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConfigurationManager config = ConfigurationManager.getInstance(context);
        if(config.isAutoRefresh())
            context.startService(new Intent(context, WallpaperService.class));
    }
}
