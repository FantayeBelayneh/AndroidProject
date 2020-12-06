package com.fanta.timeoff_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Blackouts extends AppCompatActivity {

    ListView lvBO;
    TextView start_, ending_, _id;
    String query;


    boolean rowSelected = false;
    String selectedStart, selectedEnding, selectedID;
    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    CommonTools commonTools;

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackouts);


        Cursor cursor;
        dbOperator = new dbOperations(Blackouts.this);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(Blackouts.this, "time_off_management", null, 1);
        database = dbOperations.getWritableDatabase();
        commonTools = new CommonTools(this);
        lvBO = findViewById(R.id.lvBlackOuts);


        query = " SELECT _id, START_FROM, ENDING FROM BLACK_OUTS ";

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
            final CursorAdapter bo_dapter = new CursorAdapter(Blackouts.this, cursor);
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


    class BOAdapter extends ArrayAdapter<BO_period> {

        private Context mContext;
        private LayoutInflater mLayoutInflater = null;
        Cursor cxx;

        public BOAdapter(Context ctx, Cursor cx  ) {
            super(ctx, 0);
            Log.i("in adapter", "100");
            mContext = ctx;
            cxx =cx;

        }

        public long getItemId(int position)
        {
            Log.i("in adapter", "200");
            cxx.moveToPosition(position);
            return cxx.getLong(0);

        }
        @Override
        public int getCount()
        {
            Log.i("in adapter", "300");
            int z = 0;
            try {
                z = cxx.getCount();
            }
            catch (Exception q)
            {
                commonTools.ShowExceptionMessage(q, "get count");
            }
            return    z ; //cursor.getCount();
        }
        @Override
        public BO_period getItem(int position)
        {
            Log.i("in adapter", "400");
            return new BO_period(cxx.getString(0), cxx.getString(1));
            //return mlist.get(position) ;
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            Log.i("in adapter", "500");
            LayoutInflater inflater = Blackouts.this.getLayoutInflater();
            View result ;
            result = inflater.inflate(R.layout.layout_blackouts, parent, false); //null);

            TextView starting =  result.findViewById(R.id.txtFrom);
            TextView ending = result.findViewById(R.id.txtTo);

            BO_period period = getItem(position);
            starting.setText(period.starting_from); // get the string at position
            ending.setText(period.ending);

            return result;
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

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addbo:
                commonTools.ShowMessages("BO Period", "Adding Menu");

                try {
                    Intent goBO = new Intent(Blackouts.this, Editbo.class);
                    goBO.putExtra("mode", "borange");
                    goBO.putExtra("dataState", "new");
                    startActivity(goBO);
                }
                catch (Exception y)
                {
                    commonTools.ShowExceptionMessage(y, "show new activity");
                }

                return true;
            case R.id.deletebo:
                if (rowSelected == false)
                {
                    commonTools.ShowMessages("BO Period", "Please click on your selection ");
                }
                else
                {
                    String delQuery = "DELETE FROM BLACK_OUTS WHERE _id = " + _id.getText().toString();
                    database.execSQL(delQuery);
                    RefreshListView();
                }
                return true;

            case R.id.editbo:

                if (rowSelected == false)
                {
                    commonTools.ShowMessages("BO Period", "Please click on your selection ");
                }
                else
                {
                    Intent goBO = new Intent(Blackouts.this, Editbo.class);
                    goBO.putExtra("mode", "borange");
                    goBO.putExtra("dataState", "edit");
                    goBO.putExtra("ID_fieldValue",  _id.getText().toString());
                    goBO.putExtra("startingDate", start_.getText().toString());
                    goBO.putExtra("endingDate", ending_.getText().toString());
                    //commonTools.ShowMessages("passing id", _id.getText().toString());
                    startActivity(goBO);

                }
                return true;
            default:
                return false;
        }
    }

}