package com.fanta.timeoff_management;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbOperations  {
    public SQLiteDatabase workingDB;
    protected DatabaseConnection dbCon;
    protected CommonTools commonTools;
    public dbOperations(Context cx)
    {
        dbCon = new DatabaseConnection(cx, "time_off_management", 1);
        commonTools = new CommonTools(cx);
        workingDB = dbCon.getWritableDatabase();
    }
    protected boolean validateLogin(SQLiteDatabase db,  String user, String password)
    {
        Log.i("VALIDATE_LOGIN", "?????");
        String searchQuery;
        Log.i("VALIDATE_LOGIN 000", user + " " + password);
        searchQuery = "SELECT * FROM USERS WHERE LOGINNAME = '" + user + "' AND PASSWORD = '" + password + "'";
        Log.i("VALIDATE_LOGIN 100", searchQuery);
        Cursor c = db.rawQuery(searchQuery, null);
        Log.i("VALIDATE_LOGIN 200", searchQuery);
        Log.i("VALIDATE_LOGIN", searchQuery);
        if (c.getCount()== 1)
        {
            Log.i("VALIDATE_LOGIN", "valid login credential");
            return  true;
        }
        else
        {
            Log.i("VALIDATE_LOGIN", "invalid login credential");
            return  false;
        }
    }

    protected boolean isAdminCredential(SQLiteDatabase db,  String user)
    {
        String searchQuery;
        int iRetVal;
        searchQuery = "SELECT ISAPPROVER FROM USERS WHERE LOGINNAME = '" + user + "'";
        Cursor c = db.rawQuery(searchQuery, null);
        Log.i("VALIDATE_LOGIN", searchQuery);
        c.moveToNext();
        iRetVal = c.getInt(0);

        if (iRetVal== 1)
        {
            Log.i("VALIDATE_LOGIN", "admin credential");
            return  true;
        }
        else
        {
            Log.i("VALIDATE_LOGIN", "non-admin credential");
            return  false;
        }
    }

    protected UserProfile getUserProfile(SQLiteDatabase db,  String user)
    {
        UserProfile retVal = new UserProfile();
       int iRetVal;
        String searchQuery;
        searchQuery = "SELECT _id, BENEFIT_DAYS, EMAIL_ADDRESS, STAFF_NAME, ISAPPROVER FROM USERS WHERE LOGINNAME = '" + user + "'";
        Cursor c = db.rawQuery(searchQuery, null);
        c.moveToNext();
        iRetVal = c.getCount();

         if (iRetVal== 1)
        {
            retVal.userId = c.getInt(0);
            retVal.BenefitDays = c.getInt(1);
            retVal.emailAddress = c.getString(2);
            retVal.staffName = c.getString(3);
            retVal.AdminUser = c.getInt(4);
            //commonTools.ShowMessages("Profile staffname", retVal.staffName);

            retVal.validated = true;
        }

        return  retVal;
    }
}
