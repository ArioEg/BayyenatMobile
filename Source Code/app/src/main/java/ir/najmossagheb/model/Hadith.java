package ir.najmossagheb.model;

/**
 * Created by r.kiani on 05/14/2015.
 */
public class Hadith {
    private int _id;
    private String text;
    private String source;
    private String author;
    private String tags;
    private boolean isNew = false;

    public Hadith() {}

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Hadith: [id="+_id+", text=" + text + "]";
    }
}
