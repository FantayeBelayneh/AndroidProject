package com.fanta.timeoff_management;



        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import androidx.annotation.Nullable;

public class DatabaseConnection  extends SQLiteOpenHelper {
    protected String DATABASE_NAME;


    public DatabaseConnection(@Nullable Context context, @Nullable String DBname, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBname, factory, version);
        DATABASE_NAME = DBname;
    }
    public DatabaseConnection(Context cx, String DBname,  int version) {
        super(cx, DBname,null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cmd;
        db.execSQL(createtablecommand());
        cmd = "CREATE TABLE BLACK_OUTS (BO_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, START_FROM VARCHAR(10), ENDING VARCHAR(10))";
        db.execSQL(cmd);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE USERS");
        onCreate(db);
    }


    protected String createtablecommand()
    {
        String createCommand = "";

        createCommand += " CREATE TABLE USERS ( ";
        createCommand += " ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , ";
        createCommand += " LOGINNAME VARCHAR(6) NOT NULL UNIQUE, ";
        createCommand += " PASSWORD VARCHAR(6) NOT NULL, ";
        createCommand += " BENEFIT_DAYS INTEGER NOT NULL DEFAULT 10, ";
        createCommand += " ISAPPROVER INTEGER NOT NULL DEFAULT 0, ";
        createCommand += " CHECK(ISAPPROVER IN (0,1)) )";
        return  createCommand;
    }

    protected void insertTestData(SQLiteDatabase db)
    {
        String insertCommand = "";
        insertCommand += " INSERT INTO USERS (LOGINNAME, PASSWORD) VALUES ";
        insertCommand += "    ('Andrew','1234'),";
        insertCommand += "    ('Bill','1234'),";
        insertCommand += "    ('Chad','1234'), ";
        insertCommand += "    ('Donald','1234'), ";
        insertCommand += "    ('Erick','2345'), ";
        insertCommand += "    ('Frank','34567')";

        db.execSQL(insertCommand);

        Log.i("insertion", "Test data inserted");

    }
}
