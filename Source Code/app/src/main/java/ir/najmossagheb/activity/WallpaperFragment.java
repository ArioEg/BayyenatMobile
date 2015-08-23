package ir.najmossagheb.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import ir.najmossagheb.R;
import ir.najmossagheb.core.GridFragment;
import ir.najmossagheb.core.com.jess.ui.TwoWayAdapterView;
import ir.najmossagheb.db.BayyenatDbHelper;
import ir.najmossagheb.model.NodeCategory;
import ir.najmossagheb.model.NodeWallpaper;


public class WallpaperFragment extends GridFragment {

    private static ImageLoader mImageLoader;
    private ArrayList<NodeWallpaper> mData;
    private int mPosition = -1;
    private boolean mIgnoreSelection = false;
    BayyenatDbHelper db = BayyenatDbHelper.getInstance(this.getActivity());

    public WallpaperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("WallpaperFragment","onCreate...");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.setRetainInstance(false);

        mData = new ArrayList<>();

        List<NodeWallpaper> wallpapers = db.getWallpapers();
        for(NodeWallpaper wp:wallpapers){
            mData.add(wp);
        }

        if (this.mData != null && !this.mData.isEmpty()) {
            super.setData(this.mData);
        }
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent, LayoutInflater mInflater) {
        View view = mInflater.inflate(R.layout.row_wallpaper_item, null, false);
        ImageView thumb = (ImageView) view.findViewById(R.id.wp_thumb);
        ImageView check = (ImageView) view.findViewById(R.id.check);
        //TextView title = (TextView) view.findViewById(R.id.wp_title);

        //if (!this.mUseImageTitle) {
            //view.findViewById(R.id.wp_title_bg).setVisibility(View.GONE);
        //}

        final NodeWallpaper node = this.mData.get(position);
        //title.setText(node.name);
        if(!node.selected)
            check.setVisibility(View.GONE);

        ImageLoader.getInstance().displayImage(node.thumbUrl, thumb);

        return view;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need to
     * access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id) {
        NodeWallpaper w = mData.get(position);
        w.selected = !w.selected;

        if(w.selected)
            view.findViewById(R.id.check).setVisibility(View.VISIBLE);
        else
            view.findViewById(R.id.check).setVisibility(View.GONE);

        db.updateWallpaper(w);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }
}
