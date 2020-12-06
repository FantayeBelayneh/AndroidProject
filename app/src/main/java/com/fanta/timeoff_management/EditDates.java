package com.fanta.timeoff_management;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class EditDates extends AppCompatActivity    {

    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    CommonTools commonTools;
    String workingTable, dataset;
    boolean canSave = false;


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
        dbOperator = new dbOperations(EditDates.this);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(EditDates.this, "time_off_management", null, 1);
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
        workingTable = bundle.getString("workingTable", "workingTable");



        if (dataState.equals("edit"))
        {
            ID_fieldValue = bundle.getString("ID_fieldValue");
            //commonTools.ShowMessages("edit Data", "before if ID = " + bundle.getString("ID_fieldValue")  );
            startingDate = bundle.getString("startingDate");
            endingDate = bundle.getString("endingDate").toString();
        }

        if (dataState.equals("edit"))
        {
            tvStartingDate.setText(startingDate);
            tvEndingDate.setText(endingDate);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case "bo":
                        switch (dataState) {
                            case "new":
                                try {
                                    canSave = datesInOrder(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == false)
                                        canSave = datesEqual(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == true) {
                                        String sqlCommand = "INSERT INTO " + workingTable + " (START_FROM, ENDING) VALUES ( '";
                                        sqlCommand += tvStartingDate.getText().toString() + "', '" + tvEndingDate.getText().toString() + "')";
                                        database.execSQL(sqlCommand);
                                        finish();
                                    } else {
                                        commonTools.ShowMessages("Save Data", "Data not saved (does this ever happen?)");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "edit":

                                try {
                                    canSave = datesInOrder(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == false)
                                        canSave = datesEqual(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == true) {
                                        String sqlCommand = "UPDATE " + workingTable + " SET START_FROM = '" + tvStartingDate.getText().toString();
                                        sqlCommand += "' , ENDING = '" + tvEndingDate.getText().toString();
                                        sqlCommand += "' WHERE _id =" + String.valueOf(ID_fieldValue);
                                        database.execSQL(sqlCommand);
                                        finish();
                                    } else {
                                        commonTools.ShowMessages("Save Data", "Data not saved (does this ever happen?)");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        break;
                    ////////////////////////*********************************//////////////////////////////
                    case "to":
                        switch (dataState) {
                            case "new":
                                try {
                                    int z = getCountOfWorkingDays(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    canSave = datesInOrder(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == false)
                                        canSave = datesEqual(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == true) {
                                        String sqlCommand = "INSERT INTO " + workingTable + " (START_FROM, ENDING) VALUES ( '";
                                        sqlCommand += tvStartingDate.getText().toString() + "', '" + tvEndingDate.getText().toString() + "')";
                                        database.execSQL(sqlCommand);
                                        finish();
                                    } else {
                                        commonTools.ShowMessages("Save Data", "Data not saved (does this ever happen?)");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "edit":

                                try {
                                    canSave = datesInOrder(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == false)
                                        canSave = datesEqual(tvStartingDate.getText().toString(), tvEndingDate.getText().toString());
                                    if (canSave == true) {
                                        String sqlCommand = "UPDATE " + workingTable + " SET START_FROM = '" + tvStartingDate.getText().toString();
                                        sqlCommand += "' , ENDING = '" + tvEndingDate.getText().toString();
                                        sqlCommand += "' WHERE _id =" + String.valueOf(ID_fieldValue);
                                        database.execSQL(sqlCommand);
                                        finish();
                                    } else {
                                        commonTools.ShowMessages("Save Data", "Data not saved (does this ever happen?)");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
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
                            if (datesEqual(tvStartingDate.getText().toString()  , tvEndingDate.getText().toString()) == false)
                            {
                                commonTools.ShowMessages("Dates not in chronological order", "Forcing change of ending date");
                            }
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
                            if (datesEqual(tvStartingDate.getText().toString()  , tvEndingDate.getText().toString()) == false)
                            {
                                commonTools.ShowMessages("Dates not in chronological order", "Forcing change of starting date");
                            }
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

    protected boolean computeLegibility(String sdt1, String sdt2) throws ParseException {
        Date dt1, dt2;

       /* dt1 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt1);
        dt2 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt2);*/
        dt1 = new Date( Integer.parseInt(sdt1.substring(0,4))-1900, Integer.parseInt(sdt1.substring(5,7))-1, Integer.parseInt(sdt1.substring(8,10)));
        dt2 = new Date( Integer.parseInt(sdt2.substring(0,4))-1900, Integer.parseInt(sdt2.substring(5,7))-1, Integer.parseInt(sdt2.substring(8,10)));
        return  false;
    }


    protected int getCountOfWorkingDays(String sdt1, String sdt2) throws ParseException {
        int days = 0;
        int theDt;
        Date dt1, dt2;
        int tempDt = 0;
        Log.i("Date matters", " 1 was here"  + sdt1  );  //+ "   "  + sdt1.substring(6,2)

        //dt1 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt1);
        //dt2 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt2);

        dt1 = new Date( Integer.parseInt(sdt1.substring(0,4))-1900, Integer.parseInt(sdt1.substring(5,7))-1, Integer.parseInt(sdt1.substring(8,10)));
        dt2 = new Date( Integer.parseInt(sdt2.substring(0,4))-1900, Integer.parseInt(sdt2.substring(5,7))-1, Integer.parseInt(sdt2.substring(8,10)));
        Log.i("Date matters", "dt1 = " + dt1.toString()  + "  " + dt2.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        while (dt2.after(dt1)) {
            if (dt1.toString().substring(0,3).toString().equals("Sat"))   { }
            else if (dt1.toString().substring(0,3).toString().equals("Sun")) { }
            else  { days++; }
            c.setTime(sdf.parse(sdt1));
            c.add(Calendar.DATE, 1);
            sdt1 = sdf.format(c.getTime());
            dt1 = new Date(Integer.parseInt(sdt1.substring(0, 4)) - 1900, Integer.parseInt(sdt1.substring(5, 7)) - 1, Integer.parseInt(sdt1.substring(8, 10)));
        }
        Log.i("Date matters", "Num of working days = " + days);
        return days;
    }

    protected  boolean datesInOrder(String sdt1, String sdt2) throws ParseException {
        Date dt1, dt2;

     /*   dt1 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt1);
        dt2 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt2);*/
        dt1 = new Date( Integer.parseInt(sdt1.substring(0,4))-1900, Integer.parseInt(sdt1.substring(5,7))-1, Integer.parseInt(sdt1.substring(8,10)));
        dt2 = new Date( Integer.parseInt(sdt2.substring(0,4))-1900, Integer.parseInt(sdt2.substring(5,7))-1, Integer.parseInt(sdt2.substring(8,10)));

        if (dt2.after(dt1))
        {
            return  true;
        }
        else
        {
            return  false;
        }
    }

    protected boolean datesEqual(String sdt1, String sdt2) throws ParseException {
        Date dt1, dt2;

        dt1 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt1);
        dt2 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt2);

        if (dt1.getYear() == dt2.getYear())
        {
            if (dt1.getMonth() == dt2.getMonth())
            {
                if(dt1.getDay() == dt2.getDay())
                {
                    return true;
                }
            }
        }
        return false;
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