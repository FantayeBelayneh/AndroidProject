package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DatesManager extends AppCompatActivity {

    protected ListView lvBO;
    protected TextView start_, ending_, _id;

    protected boolean rowSelected = false, isAdmin, optionsEnabled   ;
    protected String staffID,  selectedStart, selectedEnding, selectedID,query,  workingTable, mode, allowedBenefitDays ;
    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    private CommonTools commonTools;
    private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackouts);

        mode = "to";
        workingTable = "TIME_OFF";
        isAdmin = false;
        staffID = "2";
        allowedBenefitDays = "10";

        if (isAdmin == false && mode == "bo")
        {
            optionsEnabled = false;
        }
        else  if (isAdmin == true && mode == "bo")
        {
            optionsEnabled = true;
        }
        else
        {
            optionsEnabled = true;
        }

        Cursor cursor;
        dbOperator = new dbOperations(DatesManager.this);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(DatesManager.this, "time_off_management", null, 1);
        database = dbOperations.getWritableDatabase();
        commonTools = new CommonTools(this);
        lvBO = findViewById(R.id.lvBlackOuts);


        query = " SELECT _id, START_FROM, ENDING FROM " + workingTable;
        RefreshListView();
        lvBO.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > adapter, View view,int position, long arg){
                rowSelected = true;
                start_ = view.findViewById(R.id.txtFrom);
                ending_ = view.findViewById(R.id.txtTo);
                _id = view.findViewById(R.id.txtid);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        rowSelected = false;
        RefreshListView();
    }

    protected void RefreshListView()
    {
        try
        {
            cursor = database.rawQuery( query, null);
            cursor.moveToFirst();
            Log.i("-adapter", String.valueOf(cursor.getCount()));

            //final BOAdapter bo_dapter = new BOAdapter( Blackouts.this, cursor );
            final CursorAdapter bo_dapter = new CursorAdapter(DatesManager.this, cursor);
            try {
                lvBO.setAdapter (bo_dapter);
                bo_dapter.notifyDataSetChanged();
            }
            catch (Exception k)
            {
                commonTools.ShowExceptionMessage(k, "lv");
            }
        }
        catch (Exception x)
        {
            commonTools.ShowExceptionMessage(x, "reading table bo");
        }
    }


    public class CursorAdapter extends androidx.cursoradapter.widget.CursorAdapter {
        public CursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.layout_blackouts, parent, false);
        }
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView starting =  view.findViewById(R.id.txtFrom);
            TextView ending = view.findViewById(R.id.txtTo);
            TextView rec_id = view.findViewById(R.id.txtid);
            String start_ = cursor.getString(cursor.getColumnIndexOrThrow("START_FROM"));
            String end_ = cursor.getString(cursor.getColumnIndexOrThrow("ENDING"));
            String rid = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            starting.setText(start_);
            ending.setText(end_);
            rec_id.setText(rid);
        }
    }



    class BO_period
    {
        String starting_from;
        String ending;

        public BO_period(String starting_from, String ending)
        {
            this.starting_from = starting_from;
            this.ending = ending;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bo_menu, menu);

        return true;
    }

////////////////
    public boolean onOptionsItemSelected(MenuItem item) {

        if (optionsEnabled == true)
        {
            switch (item.getItemId())
            {
                case R.id.addbo:

                    try {
                        Intent goBO = new Intent(DatesManager.this, EditDates.class);
                        goBO.putExtra("mode", mode);
                        goBO.putExtra("dataState", "new");
                        goBO.putExtra("workingTable", workingTable);
                        if (mode == "to")
                        {
                            goBO.putExtra("staffID", staffID);
                            goBO.putExtra("allowedBenefitDays", allowedBenefitDays);

                        }

                        startActivity(goBO);
                    } catch (Exception y) {
                        commonTools.ShowExceptionMessage(y, "show new activity");
                    }
                    return true;
                case R.id.deletebo:

                    if (rowSelected == false) {
                        commonTools.ShowMessages("BO Period", "Please click on your selection ");
                    } else {
                        String delQuery = "DELETE FROM " + workingTable + " WHERE _id = " + _id.getText().toString();
                        database.execSQL(delQuery);
                        RefreshListView();
                    }
                    return true;
                case R.id.editbo:

                    if (rowSelected == false) {
                        commonTools.ShowMessages("BO Period", "Please click on your selection ");
                    } else {
                        //commonTools.ShowMessages("Arg builder", "Load bundle");
                        Intent goBO = new Intent(DatesManager.this, EditDates.class);
                        goBO.putExtra("mode", mode);
                        goBO.putExtra("dataState", "edit");
                        goBO.putExtra("ID_fieldValue", _id.getText().toString());
                        //commonTools.ShowMessages("Arg builder", " _id = " + _id.getText().toString());
                        goBO.putExtra("startingDate", start_.getText().toString());
                        goBO.putExtra("endingDate", ending_.getText().toString());
                        goBO.putExtra("workingTable", workingTable);
                        if (mode == "to") goBO.putExtra("staffID", staffID);

                        startActivity(goBO);

                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }




}