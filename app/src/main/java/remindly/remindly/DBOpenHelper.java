package remindly.remindly;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "our_db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "ALL_DATA";
    private static String PID = "PID TEXT PRIMARY KEY";
    private static String NAME = "NAME TEXT";
    private static String FB_URL = "FB_URL TEXT";
    private static String MM_PATH = "MM_PATH TEXT";
    private static String B_DATE = "B_DATE TIMESTAMP";
    private static String WORK = "WORK TEXT";
    private static String WISHLIST_JSON = "WISHLIST_JSON TEXT";
    private static String LAST_SEEN = "LAST_SEEN TIMESTAMP";
    private static String COUNT = "COUNT INTEGER";
    private static String NOTIFICATIONS_JSON = "NOTIFICATIONS_JSON TEXT";

    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    PID + ", " +
                    NAME + ", " +
                    FB_URL + ", " +
                    MM_PATH + ", " +
                    B_DATE + ", " +
                    WORK + ", " +
                    WISHLIST_JSON + ", " +
                    LAST_SEEN + ", " +
                    COUNT + ", " +
                    NOTIFICATIONS_JSON +
                    ");";

    DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insert(Person p) throws JSONException {

        ContentValues personValues = new ContentValues();

        String PID = (p.pid.length() > 0) ? p.pid : null,
            NAME = (p.name.length() > 0) ? p.name : null,
            FB_URL = (p.fbPicUrl.length() > 0) ? p.fbPicUrl : null,
            MM_PATH = (p.mmPicPath.length() > 0) ? p.mmPicPath : null,
            WORK = (p.work.length() > 0) ? p.work : null;

        String WISHLIST_JSON = null, NOTIFICATIONS_JSON = null;
        if (p.wishList.length() > 0) {
            WISHLIST_JSON = p.wishList.toString();
        }
        if (p.notifications.length() > 0) {
            WISHLIST_JSON = p.notifications.toString();
        }
        Date B_DATE = (p.bDate != null) ? p.bDate : null,
        LAST_SEEN = (p.lastSeen != null) ? p.lastSeen : null;
        int COUNT = p.count;

        personValues.put("PID", PID);
        personValues.put("NAME", NAME);
        personValues.put("FB_URL", FB_URL);
        personValues.put("MM_PATH", MM_PATH);
        personValues.put("B_DATE", B_DATE.toString());
        personValues.put("WORK", WORK);
        personValues.put("WISHLIST_JSON", WISHLIST_JSON);
        personValues.put("LAST_SEEN", LAST_SEEN.toString());
        personValues.put("COUNT", COUNT);
        personValues.put("NOTIFICATIONS_JSON", NOTIFICATIONS_JSON);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, personValues);
        db.close();
        return true;
    }

    public boolean deletePerson(String pid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "PID = ?", new String[]{pid});
        db.close();
        return true;
    }
    public ArrayList<Person> lastSeen(int n) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Person> pips = new ArrayList<Person>();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY LAST_SEEN DESC LIMIT " + String.valueOf(n);
        Cursor crsr = db.rawQuery(query, null);
        if (crsr.getCount() > 0 && crsr.moveToFirst()) {
            do {
                Person p = new Person();
                p.pid = crsr.getString(1);
                p.name = crsr.getString(2);
                p.fbPicUrl = crsr.getString(3);
                p.mmPicPath = crsr.getString(4);
                p.bDate = new Date(crsr.getString(5));
                p.work = crsr.getString(6);
                p.wishList = new JSONArray(crsr.getString(7));
                p.lastSeen = new Date(crsr.getString(8)); //TODO: how to create date from string? http://stackoverflow.com/questions/16208121/java-create-date-object-using-a-value-string
                p.count = crsr.getInt(9);
                p.notifications = new JSONArray(crsr.getString(10));
                pips.add(p);
            }
            while (crsr.moveToNext());
        }
        db.close();
        return pips;
    }

    public boolean addNotification(String pid, int n) {
        JSONArray arr;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME, new String[] {
                        NOTIFICATIONS_JSON}, PID + "=" + pid,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            try {
                arr = new JSONArray(mCursor.getString(10));

            }catch(Exception e){
                Log.d("MymeDB","JSON PARSE ERROR");
                return false;
            }
            //arr.put();
            db = this.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put(NOTIFICATIONS_JSON, arr.toString());
            Boolean SUCC = db.update(TABLE_NAME, args, PID + "=" + pid, null) > 0;
            db.close();
            return SUCC;
        }
        return false;
    }

    public boolean delNotification(String pid, int notIdx) throws JSONException {
        JSONArray arr;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME, new String[] {
                        NOTIFICATIONS_JSON}, PID + "=" + pid,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            try {
                arr = new JSONArray(mCursor.getString(10));

            }catch(Exception e){
                Log.d("MymeDB", "JSON PARSE ERROR");
                return false;
            }
            arr.remove(notIdx);
            db = this.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put(NOTIFICATIONS_JSON, arr.toString());
            Boolean SUCC = db.update(TABLE_NAME, args, PID + "=" + pid, null) > 0;
            db.close();
            return SUCC;
        }
        return false;
    }

    Set<String> stringCols = new HashSet<String>((Arrays.asList(new String[]{"PID", "NAME TEXT", "FB_URL TEXT", "MM_PATH TEXT", "B_DATE DATE", "WORK TEXT", "WISHLIST_JSON TEXT", "LAST_SEEN TIMESTAMP", "COUNT INTEGER", "NOTIFICATIONS_JSON TEXT"})));
    public boolean editColumn(String pid, String cName, String val){
        if(stringCols.contains(cName)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put(cName, val);
            Boolean SUCC = db.update(TABLE_NAME, args, PID + "=" + pid, null) > 0;
            db.close();
            return SUCC;
        }
        return false;
    }

    public boolean deleteAll(){
        String delAllQuery = "TRUNCATE TABLE" + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(delAllQuery);
        db.close();
        return true;
    }



}