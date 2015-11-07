package com.example.jens.kitchenconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.

    private static final String TAG = "DataBaseHelper";
    private static final Double zeroThreshold = 0.000000001;
    private static final String DATABASE_NAME = "kitchenConverter.db";
    private static final int DATABASE_VERSION = 1;

    private String pathToSaveDBFile;

    // constants for units table
    private static final String TABLE_UNITS = "units";
    private static final String UNITS_KEY_ID = "_id";
    private static final String UNITS_KEY_NAME = "NAME";
    private static final String UNITS_KEY_DIMENSION = "DIMENSION";
    private static final String UNITS_KEY_FACTOR = "FACTOR";
    private static final String UNITS_KEY_BASE = "BASE";

    private static final String TABLE_DENSITIES = "densities";
    private static final String DENSITIES_KEY_ID = "_id";
    private static final String DENSITIES_KEY_SUBSTANCEID = "SUBSTANCEID";
    private static final String DENSITIES_KEY_DENSITY = "DENSITY";

    private static final String TABLE_SUBSTANCES = "substances";
    private static final String SUBSTANCES_KEY_ID = "_id";
    private static final String SUBSTANCES_KEY_NAME = "NAME";



    private SQLiteDatabase myDataBase;
    private final Context myContext;

    // general database manipulation
    public DataBaseHelper(Context context, String filePath) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        pathToSaveDBFile = filePath + "/" + DATABASE_NAME;
    }
    public void prepareDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            Log.d(TAG, "Database exists");
            int currentDBVersion = getVersionId();
            Log.d(TAG,"currentDBVersion="+currentDBVersion);
            if (DATABASE_VERSION > currentDBVersion) {
                Log.d(TAG, "Database version is higher than old");
                deleteDB();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
    public void revertDataBase() throws IOException {
        Log.d(TAG, "Database is going to be reverted from assets");
        deleteDB();
        try {
            copyDataBase();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private boolean checkDataBase() {

        boolean checkDB = false;

        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }
    private void copyDataBase() throws IOException{

        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open(DATABASE_NAME);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer))>0){
            os.write(buffer, 0, length);
        }

        //Close the streams
        os.flush();
        os.close();
        is.close();

    }
    public void deleteDB() {
        File file = new File(pathToSaveDBFile);
        if(file.exists()) {
            file.delete();
            Log.d(TAG,"Database deleted");
        }
    }
    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT version_id FROM dbVersion";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int v =  cursor.getInt(0);
        cursor.close();
        db.close();
        return v;
    }

    // add elements
    public void addUnit(Unit unit) {
        // for logging
        Log.d("addUnit",unit.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. create Contentvalues to add "key" column/value
        ContentValues values = new ContentValues();
        values.put(UNITS_KEY_NAME,unit.getName()); // get unit name
        values.put(UNITS_KEY_DIMENSION,unit.getDimension()); // get dimension name
        values.put(UNITS_KEY_FACTOR, unit.getFactor()); // get factor
        values.put(UNITS_KEY_BASE, unit.getBase() ? 1 : 0);

        // 3. insert
        db.insert(TABLE_UNITS,
                null,// nullColumnHack
                values);

        // 4. close
        db.close();
    }


    public void addDensity (Density density) { // add density/substance

        Log.d("addDensity", density.toString());


        String substanceName = density.getSubstance();

        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);


        // insert into substances table
        ContentValues valuesSubs = new ContentValues();
        valuesSubs.put(SUBSTANCES_KEY_NAME, substanceName);
        long substanceId = db.insert(TABLE_SUBSTANCES,
                null,
                valuesSubs);

        // insert into densities table
        ContentValues valuesDens = new ContentValues();
        valuesDens.put(DENSITIES_KEY_SUBSTANCEID, substanceId);
        valuesDens.put(DENSITIES_KEY_DENSITY, density.getDensity());
        db.insert(TABLE_DENSITIES,
                null,
                valuesDens);

        db.close();
    }

    public boolean substanceExists (String sub) { // check whether substance name exists in substances table

        String query = "SELECT 1 FROM " + TABLE_SUBSTANCES + " WHERE " + SUBSTANCES_KEY_NAME + "=? COLLATE NOCASE LIMIT 1";

        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = db.rawQuery(query, new String[] { sub });
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // update elements
    public int updateUnit(Unit unit) {

        Log.d("updateUnit",unit.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(UNITS_KEY_NAME,unit.getName());
        values.put(UNITS_KEY_DIMENSION,unit.getDimension());
        values.put(UNITS_KEY_FACTOR,unit.getFactor());
        values.put(UNITS_KEY_BASE, unit.getBase() ? 1 : 0);

        // 3. updating row
        int i = db.update(TABLE_UNITS,
                values,
                UNITS_KEY_ID + " = ?",
                new String[] {String.valueOf(unit.getId()) });

        // 4. close
        db.close();

        return i;
    }
    public void updateDensity(Density density) {

        Log.d("updateDensity",density.toString());

        density.getId(); // id in densities table



        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);


        ContentValues valuesSubs = new ContentValues();
        valuesSubs.put(SUBSTANCES_KEY_NAME, density.getSubstance());

        // update substances table
        long substanceId = db.update(TABLE_SUBSTANCES,
                valuesSubs,
                SUBSTANCES_KEY_ID + " = (SELECT " + DENSITIES_KEY_SUBSTANCEID +
                                        " FROM " + TABLE_DENSITIES +
                                        " WHERE " + DENSITIES_KEY_ID + "= ? )",
                new String[] { String.valueOf(density.getId()) });

        // update densities table
        ContentValues valuesDens = new ContentValues();
        valuesDens.put(DENSITIES_KEY_DENSITY, density.getDensity());
        db.update(TABLE_DENSITIES,
                valuesDens,
                DENSITIES_KEY_ID + " = ?",
                new String[] { String.valueOf(density.getId()) });

        db.close();

    }

    // get/update base unit, get base density
    private Unit getBaseUnit(String dimension) {

        String[] dimensions = myContext.getResources().getStringArray(R.array.dimensions_array);

        if(!Arrays.asList(dimensions).contains(dimension)){
            throw new IllegalArgumentException("Dimension is not one of the permittable dimension names");
        }

        String query = "SELECT * FROM " + TABLE_UNITS + " WHERE " + UNITS_KEY_DIMENSION +" = ? AND " + UNITS_KEY_BASE + "= 1 LIMIT 1";

        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, new String[]{dimension});

        Unit baseUnit = new Unit(myContext);

        if(cursor.moveToFirst()) {
                baseUnit.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNITS_KEY_ID))));
                baseUnit.setName(cursor.getString(cursor.getColumnIndex(UNITS_KEY_NAME)));
                baseUnit.setDimension(cursor.getString(cursor.getColumnIndex(UNITS_KEY_DIMENSION)));
                baseUnit.setFactor(Double.parseDouble(cursor.getString(cursor.getColumnIndex(UNITS_KEY_FACTOR))));
                baseUnit.setBase(cursor.getInt(cursor.getColumnIndex(UNITS_KEY_BASE)) != 0);
        }

        cursor.close();


        return baseUnit;
    }
    public void updateBaseUnit(Unit unit) {

        Double factor = unit.getFactor();
        String dimension = unit.getDimension();
        Unit oldBaseUnit = getBaseUnit(dimension);

        oldBaseUnit.setBase(false);
        updateUnit(oldBaseUnit);

        if ( factor>= zeroThreshold || factor <= -zeroThreshold ) { // avoid division by 0
            Double multiplier = 1/factor;

            SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

            // update units table
            String queryUnits = "UPDATE " + TABLE_UNITS + " SET " + UNITS_KEY_FACTOR + " = " + UNITS_KEY_FACTOR + "* ? WHERE "+UNITS_KEY_DIMENSION
                    + "=?";
            Cursor cursor = db.rawQuery(queryUnits, new String[]{ String.valueOf(multiplier), dimension });
            cursor.moveToFirst();
            cursor.close();

            // update densities table
            Double density_multiplier = 1.0;

            switch ( dimension ) {
                case "mass":
                    density_multiplier = multiplier;
                    break;
                case "volume":
                    density_multiplier = factor;
                    break;
                default:
                    break;
            }

            String queryDensities = "UPDATE " + TABLE_DENSITIES + " SET " + DENSITIES_KEY_DENSITY + " = " + DENSITIES_KEY_DENSITY + "* ? WHERE " + DENSITIES_KEY_DENSITY + " NOT NULL";
            cursor = db.rawQuery(queryDensities, new String[]{ String.valueOf(density_multiplier) });
            cursor.moveToFirst();
            cursor.close();
            db.close();
        }

        unit.setBase(true);
        unit.setFactor(1.0);
        updateUnit(unit);

    }
    public String getBaseDensity() {
        return getBaseUnit("mass").getName() + " / " + getBaseUnit("volume").getName();
    }



    // delete elements
    public void deleteUnit(Unit unit) {
        if( !unit.getBase()) { // do not delete base units

            SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

            db.delete(TABLE_UNITS,
                    UNITS_KEY_ID + " = ?",
                    new String[]{String.valueOf(unit.getId())});

            db.close();

            Log.d("deleteUnit", unit.toString());
        }
    }
    public void deleteDensity(Density density) {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        String substance=density.getSubstance();

        db.delete(TABLE_DENSITIES,
                DENSITIES_KEY_ID+" = ?",
                new String[] { String.valueOf(density.getId()) });

        db.delete(TABLE_SUBSTANCES,
                SUBSTANCES_KEY_NAME+" = ?",
                new String[] { substance });

        db.close();

        Log.d("deleteDensity", density.toString());
    }

    // get lists of elements
    public List<Unit> getAllUnits() {
        List<Unit> units = new LinkedList<>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_UNITS;

        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build unit and add it to list
        Unit unit;
        if(cursor.moveToFirst()) {
            do {
                unit = new Unit(myContext);
                unit.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNITS_KEY_ID))));
                unit.setName(cursor.getString(cursor.getColumnIndex(UNITS_KEY_NAME)));
                unit.setDimension(cursor.getString(cursor.getColumnIndex(UNITS_KEY_DIMENSION)));
                unit.setFactor(Double.parseDouble(cursor.getString(cursor.getColumnIndex(UNITS_KEY_FACTOR))));
                unit.setBase(cursor.getInt(cursor.getColumnIndex(UNITS_KEY_BASE)) != 0);

                // add unit to units
                units.add(unit);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return units;
    }
    public List<Unit> getUnitsDimension(String dimension) {
        List<Unit> units = new LinkedList<>();

        String[] dimensions = myContext.getResources().getStringArray(R.array.dimensions_array);

        if(!Arrays.asList(dimensions).contains(dimension)){
            throw new IllegalArgumentException("Dimension is not one of the permittable dimension names");
        }


        String query = "SELECT * FROM " + TABLE_UNITS + " WHERE " + UNITS_KEY_DIMENSION +" = ?";



        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, new String[] { dimension });

        // 3. go over each row, build unit and add it to list
        Unit unit;
        if(cursor.moveToFirst()) {
            do {
                unit = new Unit(myContext);
                unit.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UNITS_KEY_ID))));
                unit.setName(cursor.getString(cursor.getColumnIndex(UNITS_KEY_NAME)));
                unit.setDimension(cursor.getString(cursor.getColumnIndex(UNITS_KEY_DIMENSION)));
                unit.setFactor(Double.parseDouble(cursor.getString(cursor.getColumnIndex(UNITS_KEY_FACTOR))));
                unit.setBase(cursor.getInt(cursor.getColumnIndex(UNITS_KEY_BASE)) != 0);

                // add unit to units
                units.add(unit);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return units;
    }
    public List<Density> getAllDensities() {
        List<Density> densities = new LinkedList<>();

        // 1. build the query

        String query = "SELECT " + TABLE_DENSITIES + "." + DENSITIES_KEY_ID + ", "
                                 + TABLE_SUBSTANCES + "." + SUBSTANCES_KEY_NAME + ", "
                                 + TABLE_DENSITIES + "." + DENSITIES_KEY_DENSITY
                                 + " FROM " + TABLE_DENSITIES + " INNER JOIN " + TABLE_SUBSTANCES
                                 + " ON " + TABLE_DENSITIES + "." + DENSITIES_KEY_SUBSTANCEID + "=" + TABLE_SUBSTANCES + "." + SUBSTANCES_KEY_ID;

        // 2. get reference to writable DB
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build unit and add it to list
        Density density;
        if(cursor.moveToFirst()) {
            do {
                density = new Density(myContext);
                density.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DENSITIES_KEY_ID))));
                density.setSubstance(cursor.getString(cursor.getColumnIndex(SUBSTANCES_KEY_NAME)));
                if ( cursor.getString(cursor.getColumnIndex(DENSITIES_KEY_DENSITY)) != null ) {
                    density.setDensity(Double.parseDouble(cursor.getString(cursor.getColumnIndex(DENSITIES_KEY_DENSITY))));
                }

                // add unit to units
                densities.add(density);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return densities;
    }


    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onConfigure(SQLiteDatabase database) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            database.setForeignKeyConstraintsEnabled(true);
        } else {
            database.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

