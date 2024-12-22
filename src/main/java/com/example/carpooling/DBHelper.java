package com.example.carpooling;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MyDatabase.db";

    // Constructor
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS =
                "CREATE TABLE Users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "password TEXT , " +
                        "username TEXT UNIQUE,"+
                        "user_type TEXT,"+
                        "rating_as_passenger FLOAT,"+
                        "times_driven TEXT,"+
                        "email TEXT UNIQUE)";
        db.execSQL(CREATE_TABLE_USERS);

        String CREATE_TABLE_CARS =
                "CREATE TABLE Cars (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT UNIQUE,"+
                        "price TEXT,"+
                        "rating_as_driver FLOAT,"+
                        "active INTEGER DEFAULT 0,"+
                        "how_many_people INTEGER,"+
                        "car_name TEXT,"+
                        "startTime TEXT,"+
                        "times_driving INTEGER,"+
                        "address_from TEXT,"+
                        "address_to TEXT)";
        db.execSQL(CREATE_TABLE_CARS);

        String CREATE_TABLE_DRIVES=
                "CREATE TABLE Drives("+
                "requestId INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "driverUsername TEXT,"+
                "passengerUsername TEXT,"+
                        "status INTEGER)";
        db.execSQL(CREATE_TABLE_DRIVES);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Cars");
        db.execSQL("DROP TABLE IF EXISTS Drives");

        onCreate(db);
    }

    @SuppressLint("Range")
    public boolean hasRoute(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Cars)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;
        boolean columnExists3 = false;

        while (cursor.moveToNext()) {
            if ("address_from".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists1 = true;
            }
            if ("startTime".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists2 = true;
            }
            if ("address_to".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists3= true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN address_from TEXT");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN startTime Text");
        }
        if (!columnExists3) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN address_to TEXT");
        }

        String query = "SELECT COUNT(*) FROM Cars WHERE username = ? AND address_from IS NOT NULL AND address_to IS NOT NULL AND startTime IS NOT NULL " +
                "AND address_from != '' AND address_to != '' AND startTime != ''";

        cursor = null;
        boolean hasRoute = false;

        try {
            cursor = db.rawQuery(query, new String[]{username});

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                hasRoute = count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return hasRoute;
    }

    @SuppressLint("Range")
    public String returnRating(String username,int type){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean columnExists1 = false;
        String ratingAsString = "";
        if(type==0){
            Cursor cursor = db.rawQuery("PRAGMA table_info(Users)", null);
            while (cursor.moveToNext()) {
               String columnName = cursor.getString(cursor.getColumnIndex("name"));
                if ("rating_as_passenger".equals(columnName)) {
                    columnExists1 = true;
                }
            }
            cursor.close();

            if (!columnExists1) {
                db.execSQL("ALTER TABLE Users ADD COLUMN rating_as_passenger FLOAT DEFAULT 0.0");
            }

            Cursor cursor1 = db.rawQuery(
                    "SELECT rating_as_passenger FROM Users WHERE username = ?",
                    new String[]{username}
            );
            if (cursor1.moveToFirst()) {
                float rating = cursor1.getInt(cursor1.getColumnIndex("rating_as_passenger"));

                ratingAsString = String.valueOf(rating);
            }

            cursor1.close();

        }else if(type==1){
            Cursor cursor = db.rawQuery("PRAGMA table_info(Cars)", null);
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(cursor.getColumnIndex("name"));
                if ("rating_as_driver".equals(columnName)) {
                    columnExists1 = true;
                }
            }
            cursor.close();

            if (!columnExists1) {
                db.execSQL("ALTER TABLE Cars ADD COLUMN rating_as_driver FLOAT DEFAULT 0.0");
            }
            Cursor cursor1 = db.rawQuery(
                    "SELECT rating_as_driver FROM Cars WHERE username = ?",
                    new String[]{username}
            );
            if (cursor1.moveToFirst()) {
                float rating = cursor1.getInt(cursor1.getColumnIndex("rating_as_driver"));

                ratingAsString = String.valueOf(rating);
            }

            cursor1.close();
        }

        db.close();
        return ratingAsString;
    }


    public void deleteDrive(String driverUsername, String passengerUsername) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsAffected = db.delete("Drives",
                "driverUsername = ? AND passengerUsername = ?",
                new String[]{driverUsername, passengerUsername});

        if (rowsAffected > 0) {
            Log.d("DB Operation", "Deleted drive for driver: " + driverUsername + ", passenger: " + passengerUsername);
        } else {
            Log.d("DB Operation", "No drive found for driver: " + driverUsername + ", passenger: " + passengerUsername);
        }

        db.close();
    }

    public void deleteRoute(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE Cars SET address_from = CASE WHEN address_from != '' THEN '' ELSE address_from END, " +
                "address_to = CASE WHEN address_to != '' THEN '' ELSE address_to END " +
                "WHERE username = ?";

        db.execSQL(query, new String[]{username});
        db.close();
    }


    @SuppressLint("Range")
    public boolean insertDriverLocations(String username,String startTime,String start,String end) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put("startTime", startTime);
        values.put("address_to", end);
        values.put("address_from", start);

        Cursor cursor = db.rawQuery("SELECT * FROM Cars WHERE username = ?", new String[]{username});
        boolean valid=false;
        if (cursor.getCount() > 0) {
            valid= db.update("Cars", values, "username = ?", new String[]{username}) > 0;
        }
        cursor.close();
        db.close();
        return valid;
    }

    @SuppressLint("Range")
    public void addDriverToCarsTable(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Cars)", null);
        boolean columnExists = false;

        while (cursor.moveToNext()) {
            if ("active".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists = true;
            }
        }
        cursor.close();

        if (!columnExists) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN active INTEGER DEFAULT 0");
        }

        cursor = db.rawQuery("SELECT * FROM Cars WHERE username = ?", new String[]{username});

        if (cursor.getCount() > 0) {
            // Driver exists, no need to insert
        } else {
            // User does not exist, insert a new record
            ContentValues values = new ContentValues();
            values.put("username", username);
            db.insert("Cars", null, values);
        }

        cursor.close();
        db.close();
    }

    @SuppressLint("Range")
    public void savePassengerRating(String passengerUsername,String driverUsername, float newRating) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Users)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;

        while (cursor.moveToNext()) {
            if ("rating_as_passenger".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists1 = true;
            }
            if ("times_driven".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists2 = true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Users ADD COLUMN rating_as_passengers FLOAT DEFAULT 0.0");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Users ADD COLUMN times_driven INTEGER DEFAULT 0");
        }

        cursor = db.rawQuery(
                "SELECT rating_as_passenger, times_driven FROM Users WHERE username = ?",
                new String[]{passengerUsername}
        );

        if (cursor.moveToFirst()) {
            float currentRating = cursor.getFloat(cursor.getColumnIndex("rating"));
            int numRides = cursor.getInt(cursor.getColumnIndex("times_driven"));

            numRides++;

            float newCumulativeRating = ((currentRating * (numRides - 1)) + newRating) / numRides;

            ContentValues values = new ContentValues();
            values.put("rating_as_passenger", newCumulativeRating);
            values.put("times_driven", numRides);
            db.update("Users", values, "username = ?", new String[]{passengerUsername});
        }

        cursor.close();
        db.close();
    }

    @SuppressLint("Range")
    public void saveDriverRating(String passengerUsername,String driverUsername,float newRating) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Cars)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;

        while (cursor.moveToNext()) {
            if ("rating_as_driver".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists1 = true;
            }
            if ("times_driving".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists2 = true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN rating_as_driver FLOAT DEFAULT 0.0");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN times_driving INTEGER DEFAULT 0");
        }

        cursor = db.rawQuery("SELECT rating_as_driver, times_driving FROM Cars WHERE username = ?",
                new String[]{driverUsername});

        if (cursor.moveToFirst()) {
            float currentRating = cursor.getFloat(cursor.getColumnIndex("rating_as_driver"));
            int numDrives = cursor.getInt(cursor.getColumnIndex("times_driving"));

            numDrives++;

            float newCumulativeRating = ((currentRating * (numDrives - 1)) + newRating) / numDrives;

            ContentValues contentValues = new ContentValues();
            contentValues.put("rating_as_driver", newCumulativeRating);
            contentValues.put("times_driving", numDrives);
            db.update("Cars", contentValues, "username = ?", new String[]{driverUsername});
        }

        cursor.close();
        db.close();
    }
    @SuppressLint("Range")
    public void updateDriveStatus(String clientUsername, String driverUsername, int newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Drives)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;
        boolean columnExists3 = false;

        while (cursor.moveToNext()) {
            if ("passengerUsername".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists1 = true;
            }
            if ("driverUsername".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists2 = true;
            }
            if ("status".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists3 = true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Drives ADD COLUMN passengerUsername TEXT");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Drives ADD COLUMN driverUsername TEXT");
        }
        if (!columnExists3) {
            db.execSQL("ALTER TABLE Drives ADD COLUMN status INTEGER");
        }

        cursor = db.rawQuery(
                "SELECT * FROM Drives WHERE passengerUsername = ? AND driverUsername = ?",
                new String[]{clientUsername, driverUsername}
        );

        ContentValues values = new ContentValues();
        values.put("status", newStatus);


        if (cursor != null && cursor.moveToFirst()) {
            db.update("Drives", values,
                    "passengerUsername = ? AND driverUsername = ?",
                    new String[]{clientUsername, driverUsername});
        } else {
            long result = db.insert("Drives", null, values);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    public int getDriveStatus(String passengerUsername, String driverUsername) {
        SQLiteDatabase db = this.getReadableDatabase();
        int status = 0;

        String query = "SELECT status FROM Drives WHERE passengerUsername = ? AND driverUsername = ?";
        Cursor cursor = db.rawQuery(query, new String[]{passengerUsername, driverUsername});

        if (cursor.moveToFirst()) {
            status = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return status;
    }

    @SuppressLint("Range")
    public boolean checkIfLocationsSet(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Cars)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;

        while (cursor.moveToNext()) {
            String columnName = cursor.getString(cursor.getColumnIndex("name"));
            if ("address_from".equals(columnName)) {
                columnExists1 = true;
            }
            if ("address_to".equals(columnName)) {
                columnExists2 = true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN address_from TEXT");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN address_to TEXT");
        }

        cursor = db.rawQuery(
                "SELECT address_from, address_to FROM Cars WHERE username = ?",
                new String[]{username}
        );

        boolean hasInfo = false;
        if (cursor.moveToFirst()) {
            String addressFrom = cursor.getString(cursor.getColumnIndex("address_from"));
            String addressTo = cursor.getString(cursor.getColumnIndex("address_to"));

            hasInfo = addressFrom != null && !addressFrom.isEmpty() &&
                    addressTo != null && !addressTo.isEmpty();
        }

        cursor.close();
        return hasInfo;
    }

    @SuppressLint("Range")
    public boolean hasCarInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Cars)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;
        boolean columnExists3 = false;

        while (cursor.moveToNext()) {
            if ("car_name".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists1 = true;
            }
            if ("price".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists2 = true;
            }
            if ("how_many_people".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists3 = true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN car_name TEXT");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN price INTEGER DEFAULT 0");
        }
        if (!columnExists3) {
            db.execSQL("ALTER TABLE Cars ADD COLUMN how_many_people INTEGER DEFAULT 4");
        }

        cursor = db.rawQuery(
                "SELECT car_name, price, how_many_people FROM Cars WHERE username = ?",
                new String[]{username}
        );

        boolean hasInfo = false;
        if (cursor.moveToFirst()) {
            String carName = cursor.getString(cursor.getColumnIndex("car_name"));
            String carPrice = cursor.getString(cursor.getColumnIndex("price"));
            String carPeople = cursor.getString(cursor.getColumnIndex("how_many_people"));

            hasInfo = carName != null && !carName.isEmpty() &&
                    carPrice != null && !carPrice.isEmpty() &&
                    carPeople != null && !carPeople.isEmpty();
        }

        cursor.close();
        return hasInfo;
    }

    @SuppressLint("Range")
    public car getCarInfo(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Cars WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("car_name"));
            int price = cursor.getInt(cursor.getColumnIndex("price"));
            int people = cursor.getInt(cursor.getColumnIndex("how_many_people"));
            cursor.close();
            return new car(name, price, people);
        }
        cursor.close();
        return null;
    }

    @SuppressLint("Range")
    public boolean setCarInfo(String username, String carName, int carPrice, int carPeople) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("car_name", carName);
        values.put("price", carPrice);
        values.put("how_many_people", carPeople);

        Cursor cursor = db.rawQuery("SELECT * FROM Cars WHERE username = ?", new String[]{username});
        boolean success;

        if (cursor.getCount() > 0) {
            success = db.update("Cars", values, "username = ?", new String[]{username}) > 0;
        } else {
            success = db.insert("Cars", null, values) != -1;
        }

        cursor.close();
        return success;
    }

    public boolean updateUserType(String username, String newUserType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_type", newUserType);  // Set the new user type

        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ?", new String[]{username});
        boolean valid=false;
        if (cursor.getCount() > 0) {
            valid= db.update("Users", values, "username = ?", new String[]{username}) > 0;
        }
        db.close();
        cursor.close();
        return valid;
    }

    public int checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        int isValid=0;
        if(cursor!=null && cursor.moveToFirst()){
            isValid=1;
        }

        if(cursor!=null){
            cursor.close();
        }

        db.close();
        return isValid;
    }

    public boolean checkUserUnique(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Users WHERE username = ? AND email = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, email});

        boolean isUnique = !cursor.moveToFirst();
        cursor.close();
        return isUnique;
    }

    public void setActive(String username, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("active", status);

        int rowsAffected = db.update("Cars", values, "username = ?", new String[]{username});

        if (rowsAffected == 0) {
            Log.d("DB Operation", "No user found with username: " + username);
        }

        db.close();
    }

    public int returnActive(String username) {
        int active = 0;  // Default value if no result found
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT active FROM Cars WHERE username = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            active = cursor.getInt(cursor.getColumnIndexOrThrow("active"));
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return active;
    }

    public boolean insertUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username", username);
        values.put("password", password);
        values.put("email", email);

        long result = db.insert("Users", null, values);
        db.close();
        return result != -1;
    }

    public boolean addDriveRequest(String usernamePassenger, String usernameDriver, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("passengerUsername", usernamePassenger);
        values.put("driverUsername", usernameDriver);
        values.put("status", status);

        long result = db.insert("Drives", null, values);
        db.close();
        return result != -1;
    }

    @SuppressLint("Range")
    public List<request> returnRequests(String username){
        List<request> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA table_info(Drives)", null);
        boolean columnExists1 = false;
        boolean columnExists2 = false;
        boolean columnExists3 = false;

        while (cursor.moveToNext()) {
            if ("passengerUsername".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists1 = true;
            }
            if ("driverUsername".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists2 = true;
            }
            if ("status".equals(cursor.getString(cursor.getColumnIndex("name")))) {
                columnExists3 = true;
            }
        }
        cursor.close();

        if (!columnExists1) {
            db.execSQL("ALTER TABLE Drives ADD COLUMN passengerUsername TEXT");
        }
        if (!columnExists2) {
            db.execSQL("ALTER TABLE Drives ADD COLUMN driverUsername TEXT");
        }
        if (!columnExists3) {
            db.execSQL("ALTER TABLE Drives ADD COLUMN status INTEGER");
        }

        String query = "SELECT * FROM Drives WHERE driverUsername = ? AND status = 2";

        cursor = db.rawQuery(query, new String[]{username});
        if (cursor.moveToFirst()) {
            do {
                request data = new request();
                data.setUsername(cursor.getString(cursor.getColumnIndex("passengerUsername")));
                data.setRating(this.returnRating(cursor.getString(cursor.getColumnIndex("passengerUsername")),0));

                resultList.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return resultList;
    }

    @SuppressLint("Range")
    public List<option> returnOptions(String start) {
        List<option> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Cars " +
                "WHERE active = 1 AND " +
                "address_from IS NOT NULL AND address_from != '' AND " +
                "address_to IS NOT NULL AND address_to != '' AND " +
                "startTime IS NOT NULL AND startTime != '' AND " +
                "address_from = ?";

        Cursor cursor = null;

        cursor = db.rawQuery(query, new String[]{start});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                option data = new option();
                data.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                data.setRating(cursor.getFloat(cursor.getColumnIndex("rating_as_driver")));
                data.setPrice(cursor.getInt(cursor.getColumnIndex("price")));
                data.setCarName(cursor.getString(cursor.getColumnIndex("car_name")));
                data.setFrom(cursor.getString(cursor.getColumnIndex("address_from")));
                data.setTo(cursor.getString(cursor.getColumnIndex("address_to")));
                data.setTime(cursor.getString(cursor.getColumnIndex("startTime")));

                resultList.add(data);
            } while (cursor.moveToNext());

            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return resultList;
    }


}