package com.fanta.timeoff_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Editbo extends AppCompatActivity    {

    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    CommonTools commonTools;


    Button btnClose, btnSelectStart, btnSelectEnd, btnSave;
    TextView tvStartingDate, tvEndingDate;
    CalendarView calDate;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    String selectedDate = "";
    String mode = null, dataState = null;
    String startingDate, endingDate;
    String ID_fieldValue;
    boolean waitOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbo);

        Cursor cursor;
        dbOperator = new dbOperations(Editbo.this);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(Editbo.this, "time_off_management", null, 1);
        database = dbOperations.getWritableDatabase();
        commonTools = new CommonTools(this);

        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);
        btnSelectStart = findViewById(R.id.selectStartingDate);
        btnSelectEnd = findViewById(R.id.SelectEndDate);
        tvStartingDate = findViewById(R.id.dtStart);
        tvEndingDate = findViewById(R.id.dtEnd);
        calDate = findViewById(R.id.calDate);
        Date SelectedDate;



        Bundle bundle = getIntent().getExtras();

        mode = bundle.getString("mode");
        dataState = bundle.getString("dataState").toString();

        if (mode.equals("edit"))
        {
            ID_fieldValue = bundle.getString("ID_fieldValue");
            startingDate = bundle.getString("startingDate");
            endingDate = bundle.getString("endingDate").toString();
        }


        commonTools.ShowMessages("edit Data", "before if " + ID_fieldValue );
        if (dataState.equals("edit"))
        {
            tvStartingDate.setText(startingDate);
            tvEndingDate.setText(endingDate);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode)
                {
                    case "borange":
                        switch (dataState)
                        {
                            case "new":
                                try {
                                    if ( datesInOrder(tvStartingDate.getText().toString()  , tvEndingDate.getText().toString()) == true)
                                    {
                                        String sqlCommand = "INSERT INTO BLACK_OUTS (START_FROM, ENDING) VALUES ( '" ;
                                        sqlCommand += tvStartingDate.getText().toString() + "', '" + tvEndingDate.getText().toString() + "')";
                                        database.execSQL(sqlCommand);
                                        //commonTools.ShowMessages("save complete", "Saved!");
                                        finish();
                                    }
                                    else
                                    {
                                        commonTools.ShowMessages("Save Data", "Data not saved (does this ever happen?)");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "edit":

                                try {
                                    if ( datesInOrder(tvStartingDate.getText().toString()  , tvEndingDate.getText().toString()) == true)
                                    {
                                        String sqlCommand = "UPDATE BLACK_OUTS SET START_FROM = '" + tvStartingDate.getText().toString() ;
                                        sqlCommand += "' , ENDING = '" + tvEndingDate.getText().toString() ;
                                        sqlCommand +=  "' WHERE _id =" +  String.valueOf(ID_fieldValue) ;
                                        //commonTools.ShowMessages("Save Data", sqlCommand);
                                        database.execSQL(sqlCommand);
                                        finish();
                                    }
                                    else
                                    {
                                        commonTools.ShowMessages("Save Data", "Data not saved (does this ever happen?)");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        break;
                    case "timeoff":
                         commonTools.ShowMessages("Save Data", "under construction");
                     break;
                }
            }
        });


        calDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Date dt = new Date(year -1900, month  , dayOfMonth);
                selectedDate = dateFormatter.format(dt);
            }
        });

        btnSelectStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tvStartingDate.setText(selectedDate);
                if (tvStartingDate.getText().length() > 0)
                {
                    try {
                        if ( datesInOrder(tvStartingDate.getText().toString()  , tvEndingDate.getText().toString()) == false)
                        {
                            commonTools.ShowMessages("Dates not in chronological order", "Forcing change of ending date");
                            tvEndingDate.setText(selectedDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnSelectEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tvEndingDate.setText(selectedDate);
                if (tvEndingDate.getText().length() > 0)
                {
                    try {
                        if ( datesInOrder(tvStartingDate.getText().toString()  , tvEndingDate.getText().toString()) == false)
                        {
                            commonTools.ShowMessages("Dates not in chronological order", "Forcing change of starting date");
                            tvStartingDate.setText(selectedDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    protected  boolean datesInOrder(String sdt1, String sdt2) throws ParseException {
        Date dt1, dt2;

        dt1 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt1);
        dt2 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt2);

        if (dt2.after(dt1))
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }

    class updateUI extends AsyncTask<String, String, String>
    {

        public updateUI()
        {
            commonTools.ShowMessages("edit Data", "initiation");
        }
        @Override
        protected String doInBackground(String... strings) {
            if (dataState == "edit")
            {
                commonTools.ShowMessages("edit Data", "in if block");
                tvStartingDate.setText(startingDate);
                tvEndingDate.setText(endingDate);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            waitOver = true;
        }
    }
}