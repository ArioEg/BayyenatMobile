package ir.najmossagheb.model;

import java.util.Comparator;

public class TagComparator implements Comparator<Tag> {
    @Override
    public int compare(Tag o1, Tag o2) {
        return o1.getTag().compareTo(o2.getTag());
    }
}
