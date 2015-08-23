package ir.najmossagheb.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import ir.najmossagheb.R;
import ir.najmossagheb.adapter.HadithTagAdapter;
import ir.najmossagheb.db.BayyenatDbHelper;
import ir.najmossagheb.model.Tag;
import ir.najmossagheb.model.TagComparator;


public class AhadithFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    BayyenatDbHelper db = BayyenatDbHelper.getInstance(this.getActivity());
    ListView lv;
    HadithTagAdapter adapter;
    View view;


    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;

    public AhadithFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.fragment_ahadith, container, false);

        mGestureDetector = new GestureDetector(this.getActivity(), new SideIndexGestureListener());

        List<Tag> tags = db.getTags();
        Collections.sort(tags,new TagComparator());

        List rows = new ArrayList();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        for(Tag tag:tags){
            String firstLetter = tag.getTag().substring(0,1);

            if(numberPattern.matcher(firstLetter).matches() || tag.getId() == 1)
                firstLetter = "#";

            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter;//.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem); start = end + 1;
            }

            if (!firstLetter.equals(previousLetter)) {
                rows.add(new HadithTagAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }

            rows.add(tag);
            previousLetter = firstLetter;
        }

        adapter = new HadithTagAdapter(this.getActivity(),rows, this);

        lv = (ListView) view.findViewById(R.id.lv_list);
        lv.setAdapter(adapter);

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter;//.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        updateList();

        //TODO: Use this later...
        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Clicked!","Item Clicked");
            }
        });*/

        return view;
    }

    //TODO: !!
    /*@Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        else {
            return false;
        }
    }*/

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton == null || lv == null) return;

        int pos = lv.getPositionForView(compoundButton);
        if (pos == ListView.INVALID_POSITION) return;

        Tag clicked = db.readTag((int)adapter.getItemId(pos));
        if(clicked == null){
            Log.d("Error","Something went wrong!");
            return;
        }
        clicked.setSelected(b);
        db.updateTag(clicked);
    }

    class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // we know already coordinates of first touch
            // we know as well a scroll distance
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;
            // when the user scrolls within our side index
            // we can show for every position in it a proper
            // item in the country list
            if (sideIndexX >= 0 && sideIndexY >= 0)
            {
                displayListItem();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) view.findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;
        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);
        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size())
        {
            Object[] indexItem = alphabet.get(itemPosition);
            int subitemPosition = sections.get(indexItem[0]);
            //ListView listView = (ListView) findViewById(android.R.id.list);
            lv.setSelection(subitemPosition);
        }
    }

    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) view.findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) { return; }
        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }
        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();
            tmpTV = new TextView(this.getActivity());
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }
        sideIndexHeight = sideIndex.getHeight();
        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();
                // and can display a proper item it country list
                displayListItem();
                return false;
            }
        });
    }
}
