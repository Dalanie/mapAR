package de.dala.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.dala.common.EvaluationPicture;
import de.dala.common.GPSPoint;
import de.dala.common.MapItObject;
import de.dala.common.MapItObjectExtension;

/**
 * The DatabaseHandler for the communication between Client and Client-Database
 *
 * @author Daniel Langerenken based on
 *         http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */
public class DatabaseHandler extends SQLiteOpenHelper implements
        IDatabaseHandler {

    /**
     * Database Name and Version
     */
    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "mapit_reverse_database";

    /**
     * Table names
     */
    private static final String TABLE_MAPIT_OBJECTS = "mapit_objects";
    private static final String TABLE_PICTURES = "pictures";

    /**
     * Mapit-Objects Table column names
     */
    private static final String mapit_object_id = "id";
    private static final String mapit_object_description = "description";
    private static final String mapit_object_polygon = "polygon";

    /**
     * Pictures Table column names
     */
    private static final String pictures_parameter = "parameter";
    private static final String pictures_id = "id";

    private static SQLiteDatabase db;
    private static DatabaseHandler instance;

    /**
     * @return the singleton instance.
     */
    public static synchronized IDatabaseHandler getInstance() {
        return instance;
    }

    /*
         * (non-Javadoc)
         * @see android.database.sqlite.SQLiteOpenHelper#close()
         */
    @Override
    public synchronized void close() {
        if (instance != null){
            db.close();
        }
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
 * Retrieves a thread-safe instance of the singleton object {@link DatabaseHandler} and opens the database
 * with writing permissions.
 *
 * @param context the context to set.
 */
    public static void init(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context);
            db = instance.getWritableDatabase();
        }
    }


    /*
     * Creating Tables(non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMapItObjectTable = "CREATE TABLE " + TABLE_MAPIT_OBJECTS
                + "(" + mapit_object_id + " INTEGER PRIMARY KEY,"
                + mapit_object_description + " TEXT," + mapit_object_polygon
                + " BLOB)";
        String createPictureTable = "CREATE TABLE " + TABLE_PICTURES + "("
                + pictures_id + " INTEGER PRIMARY KEY," + pictures_parameter
                + " BLOB)";
        db.execSQL(createMapItObjectTable);
        db.execSQL(createPictureTable);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * Drop older table if existed
		 */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAPIT_OBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);

		/*
		 * Create tables again
		 */
        onCreate(db);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.uni_bremen.mapAR.database.IDatabaseHandler#addMapItObject(de
     * .uni_bremen.MapItReverse.Common.MapItObject)
     */
    @Override
    public long addMapItObject(MapItObject mapItObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(mapit_object_description, mapItObject.description);
            if (mapItObject.polygonPoints != null) {
                values.put(mapit_object_polygon,
                        toBlob(mapItObject.polygonPoints));
            }
			/*
			 * Inserting Row
			 */
            return db.insert(TABLE_MAPIT_OBJECTS, null, values);
			/*
			 * Closing database connection
			 */
        } catch (IOException e) {
            Log.e("DatabaseHandler", e.getMessage());
        } finally {
            db.close();
        }

        return -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.uni_bremen.mapAR.database.IDatabaseHandler#removeMapItObject
     * (de.uni_bremen.mapAR.common.MapItObject)
     */
    @Override
    public boolean removeMapItObject(MapItObject mapItObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAPIT_OBJECTS, mapit_object_id + " = ?",
                new String[] { mapItObject.id + "" });
        db.close();
        return true;
    }

    @Override
    public boolean removeMapItObjectById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAPIT_OBJECTS, mapit_object_id + " = ?",
                new String[] { id + "" });
        db.close();
        return true;
    }

    @Override
    public boolean removeAllMapItObjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAPIT_OBJECTS, null, new String[] {});
        db.close();
        return true;
    }

    @Override
    public MapItObjectExtension getMapItObjectById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MAPIT_OBJECTS + " WHERE "
                + mapit_object_id + " = '" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
		/*
		 * looping through all rows and adding to list
		 */
        MapItObjectExtension object = null;
        if (cursor.moveToFirst()) {
            do {
                object = getMapItElementByCursor(cursor);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return object;
    }

    @Override
    public List<MapItObjectExtension> getAllMapItObjects() {
        List<MapItObjectExtension> elements = new ArrayList<MapItObjectExtension>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + mapit_object_id + ","
                + mapit_object_description + "," + mapit_object_polygon
                + " FROM " + TABLE_MAPIT_OBJECTS + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);

		/*
		 * looping through all rows and adding to list
		 */
        if (cursor.moveToFirst()) {
            do {
                elements.add(getMapItElementByCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return elements;
    }

    @SuppressWarnings("unchecked")
    private MapItObjectExtension getMapItElementByCursor(Cursor cursor) {
        long mapItId = cursor.getLong(0);
        String description = cursor.getString(1);

        ArrayList<GPSPoint> geoPoints = new ArrayList<GPSPoint>();
        try {
            geoPoints = (ArrayList<GPSPoint>) fromBlob(cursor.getBlob(2));
        } catch (IOException e) {
            Log.e("DatabaseHandler", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("DatabaseHandler", e.getMessage());
        }

        MapItObjectExtension mapItObject = new MapItObjectExtension(geoPoints,
                description);
        mapItObject.id = mapItId;
        return mapItObject;
    }

    /**
     * Write the object to a Base64 string. based on
     * http://stackoverflow.com/questions
     * /134492/how-to-serialize-an-object-into-a-string
     */
    private static byte[] toBlob(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return baos.toByteArray();
    }

    /** Read the object from Base64 string. */
    private static Object fromBlob(byte[] data) throws IOException,
            ClassNotFoundException {
        if (data != null) {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.uni_bremen.mapAR.database.IDatabaseHandler#addMapItObject(de
     * .uni_bremen.MapItReverse.Common.MapItObject)
     */
    @Override
    public long addPicture(EvaluationPicture picture) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(pictures_parameter, toBlob(picture));
			/*
			 * Inserting Row
			 */
            return db.insert(TABLE_PICTURES, null, values);
			/*
			 * Closing database connection
			 */
        } catch (IOException e) {
            Log.e("DatabaseHandler", e.getMessage());
        } finally {
            db.close();
        }
        return -1;
    }

    @Override
    public List<EvaluationPicture> getAllPictures() {
        List<EvaluationPicture> elements = new ArrayList<EvaluationPicture>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + pictures_parameter + " FROM "
                + TABLE_PICTURES + ";";
        Cursor cursor = db.rawQuery(selectQuery, null);

		/*
		 * looping through all rows and adding to list
		 */
        if (cursor.moveToFirst()) {
            do {
                EvaluationPicture picture = getEvaluationPictureFromCursor(cursor);
                if (picture != null) {
                    elements.add(picture);
                } else {
                    System.out.println("Picture was null");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return elements;
    }

    private EvaluationPicture getEvaluationPictureFromCursor(Cursor cursor) {
        EvaluationPicture picture;
        try {
            picture = (EvaluationPicture) fromBlob(cursor.getBlob(0));
            return picture;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
