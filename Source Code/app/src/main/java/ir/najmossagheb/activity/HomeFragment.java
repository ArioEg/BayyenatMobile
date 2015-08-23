package ir.najmossagheb.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import ir.najmossagheb.R;
import ir.najmossagheb.db.BayyenatDbHelper;
import ir.najmossagheb.preferences.ConfigurationManager;
import ir.najmossagheb.service.WallpaperService;


public class HomeFragment extends Fragment{

    Button btn = null;
    Handler mHandler;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mHandler = new Handler();

        btn = (Button) rootView.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BayyenatDbHelper db = BayyenatDbHelper.getInstance(getActivity());
                if(db.getAhadith().size() == 0 || db.getWallpapers().size() == 0)
                {
                    Toast.makeText(getActivity(),getString(R.string.alert_blank_db),Toast.LENGTH_SHORT).show();
                    return;
                } else if(db.getSelectedTags().size() == 0 || db.getSelectedWallpapers().size() == 0) {
                    Toast.makeText(getActivity(),getString(R.string.alert_no_rec_selected),Toast.LENGTH_SHORT).show();
                    return;
                }


                ConfigurationManager config = ConfigurationManager.getInstance(getActivity());
                if(config.isServiceStarted())
                {
                    getActivity().stopService(new Intent(getActivity().getApplicationContext(), WallpaperService.class));
                    mHandler.postDelayed(mRunnable, 500);
                    config.setAutoRefresh(false);
                } else if(!config.isServiceStarted()){
                    getActivity().startService(new Intent(getActivity().getApplicationContext(), WallpaperService.class));
                    mHandler.postDelayed(mRunnable, 500);
                    config.setAutoRefresh(true);
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link android.app.Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        mHandler.postDelayed(mRunnable, 500);
    }

    public void updateButton()
    {
        ConfigurationManager config = ConfigurationManager.getInstance(getActivity());
        if(config.isServiceStarted()) {
            btn.setText(getString(R.string.btn_service_stop));
        } else {
            btn.setText(getString(R.string.btn_service_start));
        }
    }


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateButton();
        }
    };

}
