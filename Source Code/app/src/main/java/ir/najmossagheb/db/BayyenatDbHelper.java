package ir.najmossagheb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ir.najmossagheb.model.Hadith;
import ir.najmossagheb.model.NodeWallpaper;
import ir.najmossagheb.model.Tag;

/**
 * Created by r.kiani on 05/14/2015.
 */
public class BayyenatDbHelper extends SQLiteOpenHelper {
    private static BayyenatDbHelper mInstance = null;
    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "BayyenatDB";

    SQLiteDatabase db;
    //TODO: Add Category to wallpapers...
    //Need to add category to wordpress plugin first...
    private static final String table_Wallpapers = "wallpapers";
    private static final String wallpapers_id = "_id";
    private static final String wallpapers_name = "name";
    private static final String wallpapers_url = "url";
    private static final String wallpapers_thumb = "thumb";
    private static final String wallpapers_selected = "sel";
    private static final String wallpapers_new = "isnew";
    private static final String[] COLUMNS_wallpapers = {wallpapers_id, wallpapers_name, wallpapers_url, wallpapers_thumb,wallpapers_selected, wallpapers_new};

    private static final String table_Ahadith = "ahadith";
    private static final String ahadith_id = "_id";
    private static final String ahadith_text = "text";
    private static final String ahadith_source = "source";
    private static final String ahadith_author = "author";
    private static final String ahadith_tags = "tags";
    private static final String ahadith_new = "isnew";
    private static final String[] COLUMNS_ahadith = {ahadith_id, ahadith_text, ahadith_source, ahadith_author, ahadith_tags,ahadith_new};

    private static final String table_Tags = "tags";
    private static final String tags_id = "_id";
    private static final String tags_tag = "tag";
    private static final String tags_selected = "sel";
    private static final String[] COLUMNS_tags = {tags_id, tags_tag, tags_selected};

    public static BayyenatDbHelper getInstance(Context context)
    {
        if(mInstance == null){
            mInstance = new BayyenatDbHelper(context);
        }
        return mInstance;
    }


    public BayyenatDbHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        String CREATE_AHADITH_TABLE = "CREATE TABLE " + table_Ahadith + "(" +
                                      ahadith_id + " INTEGER, " +
                                      ahadith_text + " TEXT,"+
                                      ahadith_source + " TEXT," +
                                      ahadith_author + " TEXT," +
                                      ahadith_tags + " TEXT, " +
                                      ahadith_new + " BOOL )";

        String CREATE_TAGS_TABLE = "CREATE TABLE " + table_Tags + "(" +
                                   tags_id + " INTEGER PRIMARY KEY, " +
                                   tags_tag + " TEXT," +
                                   tags_selected + " BOOL )";

        String CREATE_WALLPAPERS_TABLE = "CREATE TABLE " + table_Wallpapers + "(" +
                                    wallpapers_id + " INTEGER, " +
                                    wallpapers_name + " TEXT,"+
                                    wallpapers_url + " TEXT," +
                                    wallpapers_thumb + " TEXT, "+
                                    wallpapers_selected + " BOOL, " +
                                    wallpapers_new + " BOOL )";

        db.execSQL(CREATE_AHADITH_TABLE);
        db.execSQL(CREATE_TAGS_TABLE);
        db.execSQL(CREATE_WALLPAPERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + table_Ahadith);
        db.execSQL("DROP TABLE IF EXISTS " + table_Tags);
        db.execSQL("DROP TABLE IF EXISTS " + table_Wallpapers);
        this.onCreate(db);
    }

    public void createTag(Tag tag)
    {
        if (tagExists(tag.getTag())) return;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(tags_id,tag.getId());
        values.put(tags_tag,tag.getTag());
        values.put(tags_selected,(tag.isSelected())?1:0);

        db.insert(table_Tags,null,values);
        //db.close();
    }

    public void createHadith(Hadith hadith)
    {
        if(this.hadithExists(hadith.getId())) return;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ahadith_id,hadith.getId());
        values.put(ahadith_text,hadith.getText());
        values.put(ahadith_source,hadith.getSource());
        values.put(ahadith_author,hadith.getAuthor());
        values.put(ahadith_tags,hadith.getTags());
        values.put(ahadith_new,hadith.isNew());

        try{
            String[] hadithTags = hadith.getTags().split(",");
            for (String hadithTag : hadithTags) {

                    Tag tag = new Tag();
                    tag.setTag(hadithTag);
                    tag.setSelected(false);
                    this.createTag(tag);
            }
        }
        catch (Exception e){}

        db.insert(table_Ahadith,null,values);
        //db.close();
    }

    public void createWallpaper(NodeWallpaper wallpaper)
    {
        if(this.wallpaperExists(wallpaper.id)) return;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(wallpapers_id,wallpaper.id);
        values.put(wallpapers_name,wallpaper.name);
        values.put(wallpapers_url,wallpaper.url);
        values.put(wallpapers_thumb,wallpaper.thumbUrl);
        values.put(wallpapers_selected,(wallpaper.selected)?1:0);
        values.put(wallpapers_new,(wallpaper.isnew)?1:0);

        db.insert(table_Wallpapers,null, values);
    }

    public boolean tagExists(String tag) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.query(table_Tags,COLUMNS_tags,tags_tag + " = ?",new String[] {tag},null,null,null,null);

        String sql ="SELECT * FROM "+table_Tags+" WHERE "+ tags_tag +"='"+tag+"'";
        Cursor cursor= db.rawQuery(sql,null);

        if(cursor.getCount() > 0) return true;
        return false;
    }

    public boolean hadithExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.query(table_Tags,COLUMNS_tags,tags_tag + " = ?",new String[] {tag},null,null,null,null);

        String sql ="SELECT * FROM "+table_Ahadith+" WHERE "+ ahadith_id +"="+String.valueOf(id);
        Cursor cursor= db.rawQuery(sql,null);

        if(cursor.getCount() > 0) return true;
        return false;
    }

    public boolean wallpaperExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.query(table_Tags,COLUMNS_tags,tags_tag + " = ?",new String[] {tag},null,null,null,null);

        String sql ="SELECT * FROM "+table_Wallpapers+" WHERE "+ wallpapers_id +"="+String.valueOf(id);
        Cursor cursor= db.rawQuery(sql,null);

        if(cursor.getCount() > 0) return true;
        return false;
    }

    public Tag readTag(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table_Tags,COLUMNS_tags,tags_id + " = ?",new String[] {String.valueOf(id)},null,null,null,null);

        if(cursor != null) cursor.moveToFirst();

        Tag tag = new Tag();
        tag.setId(Integer.parseInt(cursor.getString(0)));
        tag.setTag(cursor.getString(1));
        tag.setSelected((Integer.parseInt(cursor.getString(2))==1));

        return tag;
    }

    public Hadith readHadith(int id)
    {
        SQLiteDatabase db = this. getReadableDatabase();

        Cursor cursor = db.query(table_Ahadith,COLUMNS_ahadith,ahadith_id + " = ?",new String[] {String.valueOf(id)},null,null,null,null);
        if(cursor != null) cursor.moveToFirst();

        Hadith hadith = new Hadith();
        hadith.setId(Integer.parseInt(cursor.getString(0)));
        hadith.setText(cursor.getString(1));
        hadith.setSource(cursor.getString(2));
        hadith.setAuthor(cursor.getString(3));
        hadith.setTags(cursor.getString(4));
        hadith.setNew(Integer.parseInt(cursor.getString(5))==1);
        return hadith;
    }

    public NodeWallpaper readWallpaper(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table_Wallpapers,COLUMNS_wallpapers,wallpapers_id + " = ?", new String[] {String.valueOf(id)},null,null,null,null);
        if(cursor != null) cursor.moveToFirst();

        NodeWallpaper wallpaper = new NodeWallpaper();
        wallpaper.id = Integer.parseInt(cursor.getString(0));
        wallpaper.name = cursor.getString(1);
        wallpaper.url = cursor.getString(2);
        wallpaper.thumbUrl = cursor.getString(3);
        wallpaper.selected = (Integer.parseInt(cursor.getString(4))==1);

        return wallpaper;
    }

    public List getTags()
    {
        List Tags= new LinkedList();
        String query = "SELECT * FROM " + table_Tags;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        Tag tag = null;
        if(cursor.moveToFirst()){
            do{
                tag = new Tag();
                tag.setId(Integer.parseInt(cursor.getString(0)));
                tag.setTag(cursor.getString(1));
                tag.setSelected((Integer.parseInt(cursor.getString(2))==1));
                Tags.add(tag);
            }while (cursor.moveToNext());
        }
        return Tags;
    }

    public List getHadithByTag(Tag tag)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        List Ahadith= new LinkedList();
        String w = ahadith_tags + " like \'%"+tag.getTag()+"%\' or " +
                   ahadith_tags + " like \'" + tag.getTag() + "%\' or " +
                   ahadith_tags + " like \'%" + tag.getTag() + "\'";

        String sql;
        if(tag.getId() != 1)
            sql ="SELECT * FROM "+table_Ahadith+" WHERE " + w;
        else
            sql ="SELECT * FROM "+table_Ahadith;

        Cursor cursor= db.rawQuery(sql,null);

        Hadith hadith = null;
        if(cursor.moveToFirst()){
            do{

                hadith = new Hadith();
                hadith.setId(Integer.parseInt(cursor.getString(0)));
                hadith.setText(cursor.getString(1));
                hadith.setSource(cursor.getString(2));
                hadith.setAuthor(cursor.getString(3));
                hadith.setTags(cursor.getString(4));
                hadith.setNew(Integer.parseInt(cursor.getString(5))==1);

                Ahadith.add(hadith);

            }while (cursor.moveToNext());
        }
        return Ahadith;
    }

    public List getSelectedTags()
    {
        if(readTag(1).isSelected()) return this.getTags();
        List Tags= new LinkedList();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql ="SELECT * FROM "+table_Tags+" WHERE "+ tags_selected +"=1";
        Cursor cursor= db.rawQuery(sql,null);

        Tag tag = null;
        if(cursor.moveToFirst()){
            do{
                tag = new Tag();
                tag.setId(Integer.parseInt(cursor.getString(0)));
                tag.setTag(cursor.getString(1));
                tag.setSelected((Integer.parseInt(cursor.getString(2))==1));
                Tags.add(tag);
            }while (cursor.moveToNext());
        }

        return Tags;
    }

    public int getNewAhadith()
    {
        String query = "SELECT * FROM " + table_Ahadith + " WHERE " + ahadith_new + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor == null) return 0;
        else return cursor.getCount();
    }

    public void setAhadithChecked()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ahadith_new,0);

        db.update(table_Ahadith, values, ahadith_new + " = ?", new String[] { String.valueOf(1) });
        //db.close();
    }

    public List getAhadith()
    {
        List Ahadith = new LinkedList();
        String query = "SELECT * FROM " + table_Ahadith;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Hadith hadith = null;
        if(cursor.moveToFirst()){
            do{

                hadith = new Hadith();
                hadith.setId(Integer.parseInt(cursor.getString(0)));
                hadith.setText(cursor.getString(1));
                hadith.setSource(cursor.getString(2));
                hadith.setAuthor(cursor.getString(3));
                hadith.setTags(cursor.getString(4));
                hadith.setNew(Integer.parseInt(cursor.getString(5))==1);

                Ahadith.add(hadith);

            }while (cursor.moveToNext());
        }
        return Ahadith;
    }

    public List getNewWallpapers()
    {
        String query = "SELECT * FROM " + table_Wallpapers + " WHERE " + wallpapers_new + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List Wallpapers = new LinkedList();
        NodeWallpaper wallpaper;
        if(cursor.moveToFirst())
        {
            do{
                wallpaper = new NodeWallpaper();
                wallpaper.id = Integer.parseInt(cursor.getString(0));
                wallpaper.name = cursor.getString(1);
                wallpaper.url = cursor.getString(2);
                wallpaper.thumbUrl = cursor.getString(3);
                wallpaper.selected = (Integer.parseInt(cursor.getString(4))==1);

                Wallpapers.add(wallpaper);
            }while(cursor.moveToNext());
        }
        return Wallpapers;
    }

    public List getSelectedWallpapers()
    {
        String query = "SELECT * FROM " + table_Wallpapers + " WHERE " + wallpapers_selected + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        List Wallpapers = new LinkedList();
        NodeWallpaper wallpaper;
        if(cursor.moveToFirst())
        {
            do{
                wallpaper = new NodeWallpaper();
                wallpaper.id = Integer.parseInt(cursor.getString(0));
                wallpaper.name = cursor.getString(1);
                wallpaper.url = cursor.getString(2);
                wallpaper.thumbUrl = cursor.getString(3);
                wallpaper.selected = (Integer.parseInt(cursor.getString(4))==1);

                Wallpapers.add(wallpaper);
            }while(cursor.moveToNext());
        }
        return Wallpapers;
    }

    public int getNewWallpapersCount()
    {
        return getNewWallpapers().size();
    }

    public void setWallpapersChecked()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(wallpapers_new,0);

        db.update(table_Wallpapers, values, wallpapers_new + " = ?", new String[] { String.valueOf(1) });
        //db.close();
    }

    public List getWallpapers()
    {
        List Wallpapers = new LinkedList();
        String query = "SELECT * FROM " + table_Wallpapers;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        NodeWallpaper wallpaper;
        if(cursor.moveToFirst())
        {
            do{
                wallpaper = new NodeWallpaper();
                wallpaper.id = Integer.parseInt(cursor.getString(0));
                wallpaper.name = cursor.getString(1);
                wallpaper.url = cursor.getString(2);
                wallpaper.thumbUrl = cursor.getString(3);
                wallpaper.selected = (Integer.parseInt(cursor.getString(4))==1);

                Wallpapers.add(wallpaper);
            }while(cursor.moveToNext());
        }
        return Wallpapers;
    }

    public int updateTag(Tag tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(tags_tag,tag.getTag());
        values.put(tags_selected,(tag.isSelected())?1:0);

        int i = db.update(table_Tags, values, tags_id + " = ?", new String[] { String.valueOf(tag.getId()) });
        //db.close();
        return i;
    }

    public int updateHadith(Hadith hadith)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ahadith_text,hadith.getText());
        values.put(ahadith_source,hadith.getSource());
        values.put(ahadith_author,hadith.getAuthor());
        values.put(ahadith_tags,hadith.getTags());
        values.put(ahadith_new,hadith.isNew());

        int i = db.update(table_Ahadith, values, ahadith_id + " = ?", new String[] { String.valueOf(hadith.getId()) });
        //db.close();

        return i;
    }

    public int updateWallpaper(NodeWallpaper wallpaper)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(wallpapers_name,wallpaper.name);
        values.put(wallpapers_url,wallpaper.url);
        values.put(wallpapers_thumb,wallpaper.thumbUrl);
        values.put(wallpapers_selected,(wallpaper.selected)?1:0);

        int i = db.update(table_Wallpapers, values, wallpapers_id + " = ?", new String[] { String.valueOf(wallpaper.id) });
        //db.close();
        return i;
    }

    public void deleteTag(Tag tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(table_Tags, tags_id + " = ?", new String[] {String.valueOf(tag.getId())});
        //db.close();
    }

    public void deleteHadith(Hadith hadith)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(table_Ahadith, ahadith_id + " = ?", new String[] { String.valueOf(hadith.getId()) });
        //db.close();
    }

    //TODO: implement update and delete for wallpapers...

}
