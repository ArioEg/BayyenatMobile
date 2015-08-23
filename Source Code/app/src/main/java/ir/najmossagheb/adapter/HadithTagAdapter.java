package ir.najmossagheb.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ir.najmossagheb.R;
import ir.najmossagheb.model.Tag;

/**
 * Created by r.kiani on 05/15/2015.
 */
public class HadithTagAdapter extends BaseAdapter {
    CompoundButton.OnCheckedChangeListener listener;
    Context mContext;
    List<Row> list;

    public static abstract class Row {
    }

    public static final class Section extends Row {
        public final String text;

        public Section(String text) {
            this.text = text;
        }
    }

    public HadithTagAdapter(Context context, List<Row> list,CompoundButton.OnCheckedChangeListener listener) {
        mContext = context;
        this.listener = listener;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) instanceof Tag)
            return 0;
        else
            return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.toArray().length;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        if(getItem(i) instanceof Tag)
            return ((Tag)list.get(i)).getId();
        else
            return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //TODO: Use view holder...
        if(getItemViewType(i) == 0) {//Row
            Tag tag = (Tag)getItem(i);
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.hadith_tag_list_row, null);
            }

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(tag.isSelected());
            checkBox.setOnCheckedChangeListener(listener);

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(tag.getTag());
        }
        else { //Section
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.hadith_tag_list_section, null);
            }
            Section section = (Section)getItem(i);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            textView.setText(section.text);
        }

        return view;
    }
}
