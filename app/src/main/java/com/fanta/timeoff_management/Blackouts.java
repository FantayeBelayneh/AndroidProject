package com.fanta.timeoff_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Blackouts extends AppCompatActivity {

    ListView lvBO;

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

        String query = " SELECT _id, START_FROM, ENDING FROM BLACK_OUTS ";

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

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.layout_blackouts, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find fields to populate in inflated template
            //TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
            //TextView tvPriority = (TextView) view.findViewById(R.id.tvPriority);



            TextView starting =  view.findViewById(R.id.txtFrom);
            TextView ending = view.findViewById(R.id.txtTo);
            // Extract properties from cursor
            String start_ = cursor.getString(cursor.getColumnIndexOrThrow("START_FROM"));
            String end_ = cursor.getString(cursor.getColumnIndexOrThrow("ENDING"));
            // Populate fields with extracted properties
            starting.setText(start_);
            ending.setText(end_);
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
}