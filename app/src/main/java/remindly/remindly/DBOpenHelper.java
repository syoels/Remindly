package remindly.remindly;

/**
 * Created by omer on 07/05/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;


public class DBOpenhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "our_db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "ALL_DATA";
    private static String PID_COLUMN = "PID TEXT PRIMARY KEY";
    private static String NAME = "NAME TEXT";
    private static String FB_URL = "FB_URL TEXT";
    private static String MM_PATH = "MM_PATH TEXT";
    private static String B_DATE = "B_DATE DATE";
    private static String WORK = "WORK TEXT";
    private static String WISHLIST_JSON = "WISHLIST_JSON TEXT";
    private static String LAST_SEEN = "LAST_SEEN TIMESTAMP";
    private static String COUNT = "COUNT INTEGER";
    private static String NOTIFICATIONS_JSON = "NOTIFICATIONS_JSON TEXT";



    private static final String TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    PID_COLUMN + ", " +
                    NAME + ", " +
                    FB_URL + ", " +
                    MM_PATH + ", " +
                    B_DATE + ", " +
                    WORK + ", " +
                    WISHLIST_JSON + ", " +
                    LAST_SEEN + ", " +
                    COUNT + ", " +
                    NOTIFICATIONS_JSON+ ", " +
                    ");";

    DBOpenhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insert(Person item) {
        ContentValues todoValues = new ContentValues();
        todoValues.put("TITLE", item.task);
        if (item.due != null) {
            todoValues.put("DUE", item.due.getTime());
        }
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, todoValues);
        db.close();
        return true;
    }

    public boolean delete(string pid) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "title = ?", new String[]{todoItem.task});
        db.close();
        return true;
    }
    public ArrayList<Person> lastSeen(int n) {

    }

    public boolean addNotification(int n) {

    }

    public boolean delNotification(int notIdx) {

    }

    public boolean editColumn(String pid, String cName, String val){

    }

    public boolean deleteAll(){

    }



    public ArrayList<TodoItem> GetAll() {
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<TodoItem> list = new ArrayList<TodoItem>();
        Date due;
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                int TEXT_IDX = 1;
                String title = cursor.getString(TEXT_IDX);
                int DUE_IDX = 2;
                long milliSecond = cursor.getLong(DUE_IDX);
                if (milliSecond != 0) {
                    due = new Date(cursor.getLong(2));
                    list.add(new TodoItem(due, title));
                } else {
                    list.add(new TodoItem(null, title));
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

}