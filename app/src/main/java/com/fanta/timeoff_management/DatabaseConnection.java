package com.fanta.timeoff_management;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseConnection  extends SQLiteOpenHelper {
    protected String DATABASE_NAME;
    private CommonTools commonTools;

    public DatabaseConnection(@Nullable Context context, @Nullable String DBname, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DBname, factory, version);
        DATABASE_NAME = DBname;
        commonTools = new CommonTools(context);
    }
    public DatabaseConnection(Context cx, String DBname,  int version) {
        super(cx, DBname,null, version);
        commonTools = new CommonTools(cx);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cmd;
        try {
            //Log.i("table creation", "create U");
            db.execSQL(createtablecommand());
            Log.i("table creation", "create BLACK_OUTS");
            cmd = "CREATE TABLE IF NOT EXISTS BLACK_OUTS (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, START_FROM VARCHAR(10), ENDING VARCHAR(10))";
            db.execSQL(cmd);
            Log.i("table creation", "TIME_OFF");
            cmd = "CREATE TABLE IF NOT EXISTS TIME_OFF ";
            cmd += " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, STAFF_ID VARCHAR(6), ";
            cmd += " START_FROM VARCHAR(10), ENDING VARCHAR(10), BENEFIT_DAYS INTEGER, ";
            cmd += " APPROVED VARCHAR(1) DEFAULT 0 CHECK( APPROVED IN ('0', '1', '2')) )";
            db.execSQL(cmd);
     /*       Log.i("table creation", "");
            cmd += " CREATE TABLE IF NOT EXISTS USERS  ( ";
            cmd += " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , ";
            cmd += " LOGINNAME VARCHAR(6) NOT NULL UNIQUE, ";
            cmd += " PASSWORD VARCHAR(6) NOT NULL, ";
            cmd += " STAFF_NAME VARCHAR(15) NOT NULL, ";
            cmd += " BENEFIT_DAYS INTEGER NOT NULL DEFAULT 10, EMAIL_ADDRESS VARCHAR(50), ";
            cmd += " ISAPPROVER INTEGER NOT NULL DEFAULT 0, ";
            cmd += " CHECK(ISAPPROVER IN (0,1)) )";
            db.execSQL(cmd);
            Log.i("table creation", "");*/
        }
        catch (Exception x)
        {
            commonTools.ShowExceptionMessage( x, "Table Creation");
        }

    }

    protected String createtablecommand()
    {
        String createCommand = "";

        createCommand += " CREATE TABLE IF NOT EXISTS USERS ( ";
        createCommand += " _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , ";
        createCommand += " LOGINNAME VARCHAR(6) NOT NULL UNIQUE, ";
        createCommand += " PASSWORD VARCHAR(6) NOT NULL, STAFF_NAME VARCHAR(15) NOT NULL, ";
        createCommand += " BENEFIT_DAYS INTEGER NOT NULL DEFAULT 10, ";
        createCommand += " ISAPPROVER INTEGER NOT NULL DEFAULT 0, EMAIL_ADDRESS VARCHAR(50),";
        createCommand += " CHECK(ISAPPROVER IN (0,1)) )";
        return  createCommand;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE USERS");
        onCreate(db);
    }


   }
