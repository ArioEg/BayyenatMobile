package ir.najmossagheb.activity;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ir.najmossagheb.R;
import ir.najmossagheb.core.persian.ConvertFarsiStyle;
import ir.najmossagheb.db.BayyenatDbHelper;
import ir.najmossagheb.model.Hadith;
import ir.najmossagheb.model.NodeCategory;
import ir.najmossagheb.model.NodeWallpaper;
import ir.najmossagheb.model.Tag;
import ir.najmossagheb.net.RestClient;
import ir.najmossagheb.net.RestClientHandler;
import ir.najmossagheb.preferences.ConfigurationManager;
import ir.najmossagheb.preferences.Preferences;
import ir.najmossagheb.service.WallpaperService;
import ir.najmossagheb.util.FileUtils;
import ir.najmossagheb.util.NetworkUtil;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, RestClientHandler.OnRestResponseHandler, View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private RequestParams wpParams;
    private RequestParams hdParamas;

    private static MainActivity mInstance = null;

    private static final int SETTINGS_RESULT = 1;

    ProgressDialog progressDialog;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private static ImageLoader mImageLoader;

    private Fragment currentFragment;

    BayyenatDbHelper db = BayyenatDbHelper.getInstance(this);

    public static MainActivity getInstance() {
        if(mInstance == null) mInstance =  new MainActivity();
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locale locale = new Locale("fa");
        Locale.setDefault(locale);
        Configuration configL = new Configuration();
        configL.locale = locale;
        getBaseContext().getResources().updateConfiguration(configL,
                getBaseContext().getResources().getDisplayMetrics());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        //TODO: Test on API >= 17
        /*if(Build.VERSION.SDK_INT >= 17 ){
            if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR){
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }*/

        wpParams = new RequestParams();
        wpParams.put("action","nm_filemanager_get_files_list");
        wpParams.put("page","1");
        wpParams.put("per_page","100");

        hdParamas = new RequestParams();
        hdParamas.put("action","quotescollection");
        hdParamas.put("orderby","quoto_id");
        hdParamas.put("order","ASC");
        hdParamas.put("start","0");
        hdParamas.put("num_quotes","100");

        Tag tag = new Tag();
        tag.setTag("همه");
        db.createTag(tag);



        BitmapDisplayer displayer = getResources().getBoolean(R.bool.config_enable_image_fade_in)
                ? new FadeInBitmapDisplayer(getResources().getInteger(R.integer.config_fade_in_time))
                : new SimpleBitmapDisplayer();

        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(displayer)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .offOutOfMemoryHandling()
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(options)
                .build();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        drawerFragment.updateDrawer();
        // display the first navigation drawer view on app launch
        displayView(0);
    }


    public String getSdCardPath() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.config_external_storage_folder) + "/");
        if(!dir.exists()) dir.mkdirs();
        return Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.config_external_storage_folder) + "/";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO: RTL Drawer
        /*if (item != null && id == android.R.id.home) {
            if (drawerFragment.isDrawerOpen(Gravity.RIGHT)) {
                drawerFragment.closeDrawer(Gravity.RIGHT);
            } else {
                drawerFragment.openDrawer(Gravity.RIGHT);
            }
            return true;
        }*/

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            displayView(3);
            return true;
        }

        if(id == R.id.action_reload){

            reloadAction();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reloadAction()
    {
        if (!NetworkUtil.getNetworkState(this)) {
            Toast.makeText(this, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = super.getResources().getString(R.string.config_wallpaper_manifest_url);
        Toast.makeText(this,"درحال دریافت پس زمینه ها...",Toast.LENGTH_SHORT).show();
        final RestClientHandler handler = new RestClientHandler(this,RestClientHandler.REST_WP);
        RestClient.post(this, url, wpParams, handler);

        Toast.makeText(this,"درحال دریافت احادیث...",Toast.LENGTH_SHORT).show();
        final RestClientHandler handler2 = new RestClientHandler(this,RestClientHandler.REST_HD);
        RestClient.post(this, url, hdParamas, handler2);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        TextView notify = (TextView) view.findViewById(R.id.notify);
        notify.setVisibility(View.GONE);
        if(position == 1)   //Ahadith
        {
            db.setAhadithChecked();
        }
        else if(position ==2)
        {
            db.setWallpapersChecked();
        }

        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new AhadithFragment();
                title = getString(R.string.title_ahadith);
                break;
            case 2:
                fragment = new WallpaperFragment();
                title = getString(R.string.title_wallpapers);
                break;
            case 3:
                Intent i = new Intent(getApplicationContext(),Preferences.class);
                startActivityForResult(i, SETTINGS_RESULT);
                return;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            currentFragment = fragment;

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGS_RESULT)
        {
            ConfigurationManager config = ConfigurationManager.getInstance(this);
            if(config.isAutoRefresh())
            {
                if(db.getAhadith().size() == 0 || db.getWallpapers().size() == 0)
                {
                    Toast.makeText(this,getString(R.string.alert_blank_db),Toast.LENGTH_SHORT).show();
                    config.setAutoRefresh(false);
                    return;
                } else if(db.getSelectedTags().size() == 0 || db.getSelectedWallpapers().size() == 0) {
                    Toast.makeText(this,getString(R.string.alert_no_rec_selected),Toast.LENGTH_SHORT).show();
                    config.setAutoRefresh(false);
                    return;
                }

                if(!config.isServiceStarted()) {
                    startService(new Intent(getApplicationContext(), WallpaperService.class));
                    if(currentFragment instanceof HomeFragment)
                    {
                        currentFragment.onResume();
                    }
                }

            }
            else
            {
                if(config.isServiceStarted()) {
                    stopService(new Intent(getApplicationContext(), WallpaperService.class));
                    if(currentFragment instanceof HomeFragment) {
                        currentFragment.onResume();
                    }
                }
            }

        }
    }

    @Override
    public void onResponseWP(ArrayList<NodeWallpaper> response) {
        for(NodeWallpaper wp:response)
        {
            db.createWallpaper(wp);
        }

        GetXMLTask task = new GetXMLTask(this);
        task.execute(db.getNewWallpapers().toArray());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("بروزرسانی");
        progressDialog.setMessage("در حال دریافت ");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        //progressDialog.setIcon(R.drawable.arrow_stop_down);
        progressDialog.setCancelable(true);
        progressDialog.show();

        drawerFragment.updateDrawer();
    }

    @Override
    public void onResponseHD(ArrayList<Hadith> response) {
        for(Hadith hd:response)
        {
            db.createHadith(hd);
        }
        drawerFragment.updateDrawer();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * Called when a fragment is attached to the activity.
     *
     * @param fragment
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    private class GetXMLTask extends AsyncTask<Object, Integer, Bitmap> {
        private Context context;
        int noOfURLs;
        int downloaded = 1;
        public GetXMLTask(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Object... wps) {
            noOfURLs = wps.length;
            Bitmap map = null;
            File file,thumb;

            File thumbPath = new File(getSdCardPath() + "Thumb/");
            thumbPath.mkdirs();

            FileOutputStream out;
            for (Object obj : wps) {
                NodeWallpaper wp=(NodeWallpaper) obj;
                file = new File(getSdCardPath() + wp.name);
                thumb = new File(getSdCardPath() + "Thumb/" + wp.name);

                    try {
                        if(!file.exists()) {
                            map = downloadImage(wp.url);
                            if(map != null) {
                                out = new FileOutputStream(file);
                                map.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();
                                wp.url = "file:///" + file.getAbsolutePath();
                            }
                        }
                        //TODO:remove this
                        wp.url = "file:///" + file.getAbsolutePath();
                        if(!thumb.exists()) {
                            map = downloadImage(wp.thumbUrl);
                            if(map != null) {
                                out = new FileOutputStream(thumb);
                                map.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();
                                wp.thumbUrl = "file:///" + thumb.getAbsolutePath();
                            }
                        }
                        //TODO:remove this
                        wp.thumbUrl = "file:///" + thumb.getAbsolutePath();

                        db.updateWallpaper(wp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                downloaded++;
            }
            return null;
        }

        private Bitmap downloadImage(String urlString) {

            int count = 0;
            Bitmap bitmap = null;

            URL url;
            InputStream inputStream = null;
            BufferedOutputStream outputStream = null;

            try {
                url = new URL(urlString);
                URLConnection connection = url.openConnection();
                int lenghtOfFile = connection.getContentLength();

                inputStream = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

                outputStream = new BufferedOutputStream(dataStream);

                byte data[] = new byte[512];
                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    /*publishing progress update on UI thread.
                    Invokes onProgressUpdate()*/
                    publishProgress((int)((total*100)/lenghtOfFile));

                    // writing data to byte array stream
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;

                byte[] bytes = dataStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                FileUtils.close(inputStream);
                FileUtils.close(outputStream);
            }
            return bitmap;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
            progressDialog.setMessage(" در حال دریافت " + downloaded + "/" + noOfURLs);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressDialog.dismiss();
            super.onPostExecute(bitmap);
        }
    }
}
