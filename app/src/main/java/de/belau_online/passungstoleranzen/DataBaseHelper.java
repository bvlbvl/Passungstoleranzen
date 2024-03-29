package de.belau_online.passungstoleranzen;

/**
 * Created by MV on 25.04.2017.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

//took from http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
public class DataBaseHelper extends SQLiteOpenHelper {
    // The Android's default system path of your application database.
    //private static String DB_PATH = "/data/data/de.belauonline.passungstoleranzen/databases/";
    private static String DB_PATH = "";

    private static String DB_NAME = "toleranzen.sqlite";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     * */
    public void createDataBase() throws IOException, FileNotFoundException, SecurityException {

        boolean dbExist = checkDataBase1();

        if (!dbExist) {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();
            this.close();

            try {
                //copy database from asset
                copyDataBase();
                Log.e(TAG, "createDatabase database created");

            } /*catch (IOException e) {

                throw new Error("Error copying database");

            }*/
            catch (FileNotFoundException e){
                throw  new Error("Error: File not Found", e);
            }
            catch (SecurityException e){
                throw new Error("Security exception", e);
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            throw new Error("database does't exist yet");
            // database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase1()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException, SecurityException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;


        File outFile = new File(DB_PATH, DB_NAME);
        //test file object
        boolean canread = outFile.canRead();
        boolean canwrite = outFile.canWrite();
        boolean isfile = outFile.isFile();

//        outFile.createNewFile();

        // Open the empty db as the output stream
        FileOutputStream myOutput;
        myOutput = new FileOutputStream(outFile);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public boolean openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.CREATE_IF_NECESSARY);
        return myDataBase != null;

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    // Add your public helper methods to access and get content from the
    // database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd
    // be easy
    // to you to create adapters for your views.
}

