package jp.obanet.gpsreminder;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public SQLiteHelper(Context context){
        this(context, DB_NAME, null, DB_VERSION);
    }
    public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);

        db = getWritableDatabase();
    }

    public static final String DB_NAME = "gpsreminder.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "checked_places";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MEMO = "memo";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_ZOOM = "zoom";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_BEARING = "bearing";
    public static final String COLUMN_TILT = "tilt";
    public static final String COLUMN_NOTIFIED = "notified";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                    "CREATE TABLE " + TABLE_NAME  + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_MEMO + " TEXT, " +
                    COLUMN_DISTANCE + " INTEGER, " +
                    COLUMN_ZOOM + " REAL, " +
                    COLUMN_LAT + " REAL, " +
                    COLUMN_LNG + " REAL, " +
                    COLUMN_BEARING + " REAL, " +
                    COLUMN_TILT + " REAL, " +
                    COLUMN_NOTIFIED + " INTEGER DEFAULT 0)"
        );
    }

    public Map<Long, CheckedPlace> getCheckedPlaceMap() {
        Map<Long, CheckedPlace> placeMap = new LinkedHashMap<Long, CheckedPlace>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, SQLiteHelper.COLUMN_ID);
        if(cursor.moveToFirst()){
            do{
                CheckedPlace checkedPlace = new CheckedPlace(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_LNG)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_DISTANCE)),
                        (float)cursor.getDouble(cursor.getColumnIndex(COLUMN_ZOOM)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_MEMO)),
                        (float)cursor.getDouble(cursor.getColumnIndex(COLUMN_BEARING)),
                        (float)cursor.getDouble(cursor.getColumnIndex(COLUMN_TILT)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFIED)));
                placeMap.put(Long.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_ID))), checkedPlace);
            }while(cursor.moveToNext());
        }

        return placeMap;
    }

    public long insertCheckedPlace(CheckedPlace checkedPlace){
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_NAME, checkedPlace.getName());
        values.put(SQLiteHelper.COLUMN_LAT, checkedPlace.getLat());
        values.put(SQLiteHelper.COLUMN_LNG, checkedPlace.getLng());
        values.put(SQLiteHelper.COLUMN_DISTANCE, checkedPlace.getDistance());
        values.put(SQLiteHelper.COLUMN_ZOOM, checkedPlace.getZoom());
        values.put(SQLiteHelper.COLUMN_MEMO, checkedPlace.getMemo());
        values.put(SQLiteHelper.COLUMN_BEARING, checkedPlace.getBearing());
        values.put(SQLiteHelper.COLUMN_TILT, checkedPlace.getTilt());
        values.put(SQLiteHelper.COLUMN_NOTIFIED, checkedPlace.getNotified());
        long id = db.insertOrThrow(SQLiteHelper.TABLE_NAME, null, values);

        return id;
    }

    public int updateCheckedPlace(CheckedPlace checkedPlace){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, checkedPlace.getId());
        values.put(COLUMN_NAME, checkedPlace.getName());
        values.put(COLUMN_LAT, checkedPlace.getLat());
        values.put(COLUMN_LNG, checkedPlace.getLng());
        values.put(COLUMN_DISTANCE, checkedPlace.getDistance());
        values.put(COLUMN_ZOOM, checkedPlace.getZoom());
        values.put(COLUMN_MEMO, checkedPlace.getMemo());
        values.put(SQLiteHelper.COLUMN_TILT, checkedPlace.getTilt());
        values.put(SQLiteHelper.COLUMN_NOTIFIED, checkedPlace.getNotified());
        values.put(COLUMN_NOTIFIED, checkedPlace.getNotified());
        int result = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(checkedPlace.getId())});
        return result;
    }

    public int deleteCheckedPlace(CheckedPlace checkedPlace){
        int result = db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(checkedPlace.getId())});
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
