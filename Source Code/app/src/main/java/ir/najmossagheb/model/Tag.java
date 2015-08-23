package ir.najmossagheb.model;

import java.util.Comparator;

import ir.najmossagheb.adapter.HadithTagAdapter;

public class Tag extends HadithTagAdapter.Row {
    private int _id;
    private String tag;
    private boolean isselected;

    public Tag() {}

    public int getId() {

        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getTag() {
        return tag.trim();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSelected() {
        return isselected;
    }

    public void setSelected(boolean isselected) {
        this.isselected = isselected;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
