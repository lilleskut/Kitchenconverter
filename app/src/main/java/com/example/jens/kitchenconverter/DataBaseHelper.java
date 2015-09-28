package com.example.jens.kitchenconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.jens.kitchenconverter/databases/";

    private static String DB_NAME = "kitchenConverter.db";

    private static final int DATABASE_VERSION = 1;

    // constants for units table
    private static final String TABLE_UNITS = "units";
    private static final String UNITS_KEY_ID = "_id";
    private static final String UNITS_KEY_UNIT = "unit";
    private static final String UNITS_KEY_DIMENSION = "dimension";
    private static final String UNITS_KEY_FACTOR = "factor";

    private static final String[] UNITS_COLUMNS = {UNITS_KEY_ID, UNITS_KEY_UNIT, UNITS_KEY_DIMENSION, UNITS_KEY_FACTOR};

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    /**
     * Creates an empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }


    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }


    // add unit to units table

    public void addUnit(Unit unit) {
        // for logging
        Log.d("addUnit",unit.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create Contentvalues to add "key" column/value
        ContentValues values = new ContentValues();
        values.put(UNITS_KEY_UNIT,unit.getUnit()); // get unit name
        values.put(UNITS_KEY_DIMENSION,unit.getDimension()); // get dimension name
        values.put(UNITS_KEY_FACTOR,unit.getFactor()); // get factor

        // 3. insert
        db.insert(TABLE_UNITS,
                null,// nullColumnHack
                values);

        // 4. close
        db.close();
    }

    // read unit from units table

    public Unit getUnit(int id) {

        // 1. Get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. Build query
        Cursor cursor =
                db.query(TABLE_UNITS,
                        UNITS_COLUMNS,
                        " _id= ?",
                        new String[] { String.valueOf(id) },
                        null,
                        null,
                        null,
                        null);

        // 3. If we got results get the first one
        if ( cursor != null )
            cursor.moveToFirst();

        // 4. Build Unit object
        Unit unit = new Unit();

        unit.setId(Integer.parseInt(cursor.getString(0)));
        unit.setUnit(cursor.getString(1));
        unit.setDimension(cursor.getString(2));
        unit.setFactor(Float.parseFloat(cursor.getString(3)));

        //log
        Log.d("getUnit(" + id + ")", unit.toString());

        // 5. return Unit
        return unit;
    }

    public List<Unit> getAllUnits() {
        List<Unit> units = new LinkedList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_UNITS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build unit and add it to list
        Unit unit;
        if(cursor.moveToFirst()) {
            do {
                unit = new Unit();
                unit.setId(Integer.parseInt(cursor.getString(0)));
                unit.setUnit(cursor.getString(1));
                unit.setDimension(cursor.getString(2));
                unit.setFactor(Float.parseFloat(cursor.getString(3)));

                // add unit to units
                units.add(unit);
            } while (cursor.moveToNext());
        }

        Log.d("getAllUnits()", units.toString());

        cursor.close();

        return units;
    }

    // update single unit

    public int updateUnit(Unit unit) {

        Log.d("updateUnit",unit.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(UNITS_KEY_UNIT,unit.getUnit());
        values.put(UNITS_KEY_DIMENSION,unit.getDimension());
        values.put(UNITS_KEY_FACTOR,unit.getFactor());

        // 3. updating row
        int i = db.update(TABLE_UNITS,
                    values,
                    UNITS_KEY_ID + " = ?",
                    new String[] {String.valueOf(unit.getId()) });

        // 4. close
        db.close();

        return i;
    }


    // delete single unit

    public void deleteUnit(Unit unit) {
        // 1. get reference to writeable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_UNITS,
                UNITS_KEY_ID+" = ?",
                new String[] { String.valueOf(unit.getId()) });

        // 3. close
        db.close();

        Log.d("deleteUnit", unit.toString());
    }

    // convert list of units to string

    public String toString(List<Unit> l) {
        StringBuilder sb = new StringBuilder("(");
        String sep = "";
        for (Object object : l) {
            sb.append(sep).append(object.toString());
            sep = "-";
        }
        return sb.append(")").toString();
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
