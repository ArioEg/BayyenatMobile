package ir.najmossagheb.model;

import java.io.Serializable;
public class NodeWallpaper implements Serializable {
	private static final long serialVersionUID = 1L;

    //TODO: Implement getter & setter...
    public int id;
	public String name;
	public String author;
	public String thumbUrl;
	public String url;
    public boolean selected = false;
    public boolean isnew = false;
}