package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewRequests extends AppCompatActivity {

    Button btnExtit;
    TextView heading;
    ListView lvVacations;

    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    private CommonTools commonTools;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        btnExtit = findViewById(R.id.btnExit);
        heading = findViewById(R.id.heading);
        lvVacations = findViewById(R.id.lvVacations);

        Cursor cursor;
        dbOperator = new dbOperations(ViewRequests.this);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(ViewRequests.this, "time_off_management", null, 1);
        database = dbOperations.getWritableDatabase();
        commonTools = new CommonTools(this);
        RefreshListView();

        btnExtit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* lvVacations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                TextView selection = v.findViewById(R.id.txtid);
               int rec_id =  Integer.parseInt(selection.getText().toString());

                AlertDialog.Builder actOnRequest = new AlertDialog.Builder(ViewRequests.this);
                actOnRequest.setTitle("Accept/Reject Request");
                actOnRequest.setMessage( "Click on Yes to Accept or No to reject!");

                actOnRequest.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface messageInterface, int which)
                    {
                        messageInterface.dismiss();
                    }
                });
                AlertDialog dialog = actOnRequest.create();
                dialog.show();


            }
        });*/
    }

    protected void RefreshListView()
    {
        try
        {
            String query = "SELECT A._id, START_FROM, ENDING, A.BENEFIT_DAYS, STAFF_NAME FROM TIME_OFF A ";
            query += " INNER JOIN USERS B ON  A.STAFF_ID  = B._id WHERE APPROVED = 0 " ;


            cursor = database.rawQuery( query, null);
            cursor.moveToFirst();
            Log.i("-adapter", String.valueOf(cursor.getCount()));

            //final BOAdapter bo_dapter = new BOAdapter( Blackouts.this, cursor );
            final ViewRequests.CursorAdapter bo_dapter = new ViewRequests.CursorAdapter(ViewRequests.this, cursor);
            try {
                lvVacations.setAdapter (bo_dapter);
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
            return LayoutInflater.from(context).inflate(R.layout.layouts_view_requests, parent, false);
        }
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView starting =  view.findViewById(R.id.txtFrom);
            TextView ending = view.findViewById(R.id.txtTo);
            TextView rec_id = view.findViewById(R.id.txtid);
            TextView days = view.findViewById(R.id.txtdays);
            TextView staff_name = view.findViewById(R.id.staff_name);

            String start_ = cursor.getString(cursor.getColumnIndexOrThrow("START_FROM"));
            String end_ = cursor.getString(cursor.getColumnIndexOrThrow("ENDING"));
            String rid = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            String staff_ = cursor.getString(cursor.getColumnIndexOrThrow("STAFF_NAME"));
            String days_ = cursor.getString(cursor.getColumnIndexOrThrow("BENEFIT_DAYS"));

            starting.setText(start_);
            ending.setText(end_);
            staff_name.setText(staff_);
            rec_id.setText(rid);
            days.setText(days_ + " days");

        }
    }
}