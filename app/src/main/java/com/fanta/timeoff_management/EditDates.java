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
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class EditDates extends AppCompatActivity    {

    Button btnClose, btnSelectStart, btnSelectEnd, btnSave;
    TextView tvStartingDate, tvEndingDate;
    CalendarView calDate;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    private CommonTools commonTools;
    protected SendMail sendEmail;


    String workingTable,   selectedDate = "", mode = null, dataState = null, startingDate, endingDate, ID_fieldValue, staffID  ;
    boolean canSave = false, waitOver;
    int allowedDays=0, allocatedDays = 0, daysCount=0;

    Date today;


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

        if (mode.equals("to"))
        {
            staffID = bundle.getString("staffID");
            allowedDays = Integer.parseInt(bundle.getString("allowedBenefitDays"));

        }

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
            @Override
            public void onClick(View v) {

                Log.i("BoxVal", tvStartingDate.getText().toString() );


                if (tvStartingDate.getText().toString().equals("") || tvEndingDate.getText().toString().equals(""))
                {
                    commonTools.ShowMessages("Blank input fields", "Please select dates from the calendar."  );
                    return;
                }


                 switch (mode) {
                    case "bo":
                        switch (dataState) {
                            case "new":

                                try {
                                    canSave = computeEligibility();
                                    if (canSave == true)
                                    {
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

                    case "to":
                        switch (dataState) {

                            case "new":
                                commonTools.ShowMessages("save", "100");
                                Log.i("SaveButton", "100");
                                try {
                                    canSave = computeEligibility();
                                    if (canSave== true)
                                    {
                                        Log.i("insert sql", "Before insertion");
                                        String sqlCommand = "INSERT INTO " + workingTable + " (START_FROM, ENDING, BENEFIT_DAYS, STAFF_ID ) VALUES ( '";
                                        sqlCommand += tvStartingDate.getText().toString() + "', '" + tvEndingDate.getText().toString() ;
                                        sqlCommand += "', " + daysCount +  ", '" + staffID +"')";
                                        database.execSQL(sqlCommand);
                                        Log.i("insert sql", sqlCommand);
                                        commonTools.ShowMessages("compile email", "to email");
                                        sendEmail = new SendMail(Integer.parseInt(staffID), tvStartingDate.getText().toString(), tvEndingDate.getText().toString(), EditDates.this, 0);
                                        //sendEmail = new SendMail(3, "now", "then",  EditDates.this);
                                        sendEmail.execute();
                                        finish();
                                    } else {
                                        commonTools.ShowMessages("Save Data", "Your selections are not saved");
                                    }
                                } catch (ParseException e) {
                                    Log.i("SaveButton", e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            case "edit":

                                try {
                                    canSave = computeEligibility();
                                    if (canSave == true) {
                                        String sqlCommand = "UPDATE " + workingTable + " SET START_FROM = '" + tvStartingDate.getText().toString();
                                        sqlCommand += "' , ENDING = '" + tvEndingDate.getText().toString();
                                        sqlCommand += "', BENEFIT_DAYS = " + String.valueOf(daysCount) + ", APPROVED = '0' " ;
                                        sqlCommand += " WHERE _id =" + String.valueOf(ID_fieldValue);
                                        database.execSQL(sqlCommand);
                                        sendEmail = new SendMail(Integer.parseInt(staffID), tvStartingDate.getText().toString(), tvEndingDate.getText().toString(), EditDates.this, 1);
                                        sendEmail.execute();
                                        finish();
                                    } else {
                                        commonTools.ShowMessages("Save Data", "Your selections are not saved");
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

    protected boolean computeEligibility() throws ParseException {
        String day1, day2;
        boolean noOverlapExists;
        boolean notInBlackOutPeriod;

        try
        {
            day1 =tvStartingDate.getText().toString();
            day2 = tvEndingDate.getText().toString();
            datesInOrder(day1, day2);
            datesEqual(day1, day2);
            allocatedDays = countAllocatedDays();

            daysCount = getCountOfWorkingDays(day1, day2);
            noOverlapExists = checkForOverlaps(day1, day2);
            notInBlackOutPeriod = checkForBlackoutPeriod(day1, day2);

            Log.i("var_eval  daysCount ", String.valueOf(daysCount));
            Log.i("var_eval  noOverlapExists ", String.valueOf(noOverlapExists));
            Log.i("var_eval  notInBlackOutPeriod ", String.valueOf(notInBlackOutPeriod));

            if (noOverlapExists == false)
            {
                commonTools.ShowMessages("Conflicting Schedule", "Your choices conflict with existing arrangements. Please review.");
                return  false;
            }
            else if (notInBlackOutPeriod == false)
            {
                commonTools.ShowMessages("Conflicting Schedule", "Your choices occur within a blackout period. Please review.");
                return false;
            }
            else
            {
                if (allowedDays - (daysCount + allocatedDays) >= 0 )
                {
                    return true;
                }
                else
                {
                    commonTools.ShowMessages("Allocated Days", "You have exceeded your allowances.");
                    return false;
                }
            }
        }
        catch (Exception z)
        {
            commonTools.ShowMessages("Data validation", z.getMessage().toString() );
            return false;
        }

    }

    protected boolean checkForBlackoutPeriod(String sdt1, String sdt2)
    {
        String query;
        int iResult;
        Cursor cursor;

        query = " SELECT * FROM BLACK_OUTS WHERE " ;
        query+= " START_FROM BETWEEN date('" + sdt1 + "')  AND date('" + sdt2 + "') " ;
        query+= " OR ENDING  BETWEEN date('" + sdt1 + "')  AND date('" + sdt2 + "') " ;
        cursor = database.rawQuery(query, null);
        Log.i("BO_CHECK1", query);
        cursor.moveToFirst();
        try {
            iResult = cursor.getCount();
        }
        catch (Exception z)
        {
            iResult = 0;
        }
        Log.i("read_counts", "#b1 Count = " +iResult);
        query = "SELECT * FROM BLACK_OUTS WHERE " ;
        query+= " date('" + sdt1 + "') BETWEEN START_FROM AND ENDING AND ";
        query+= " date('" + sdt2 + "') BETWEEN START_FROM AND ENDING ";
        cursor = database.rawQuery(query, null);
        Log.i("BO_CHECK1", query);
        cursor.moveToFirst();
        try {
            iResult += cursor.getCount();
        }
        catch (Exception z)
        {
            iResult = 0;
        }
        Log.i("read_counts", "#b2 Count = " +iResult);
        if (iResult > 0 )
        {
            return false;
        }
        else
        {
            return true;
        }

    }


    protected boolean checkForOverlaps(String sdt1, String sdt2)
    {
        String query;
        int iResult;
        String filter;

        if (dataState.equals("edit"))
        {
            filter = " AND _id != " + ID_fieldValue;
        }
        else
        {
            filter = "";
        }

        query = "SELECT * FROM TIME_OFF WHERE date(START_FROM) BETWEEN " ;
        query+= " DATE('" + sdt1 + "') AND DATE('" + sdt2 + "') ";
        query+= " AND STAFF_ID =  " + staffID + filter ;

        Log.i("SQL 1", query);

        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        try {
            iResult = cursor.getCount();
        }
        catch (Exception z)
        {
            iResult = 0;
        }

        Log.i("read_counts", "#1 Count = " +iResult);
        query = "SELECT * FROM TIME_OFF WHERE date(ENDING) BETWEEN " ;
        query+= " DATE('" + sdt1 + "') AND DATE('" + sdt2 + "')";
        query+= " AND STAFF_ID =  " + staffID  + filter;
        Log.i("SQL 1", query);

        cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        try {
            iResult += cursor.getCount();
        }
        catch (Exception z)
        {
            iResult += 0;
        }
        Log.i("read_counts", "#2 Count = " +iResult);
        if (iResult > 0 )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    protected int countAllocatedDays()
    {
        String query;
        query = "SELECT SUM(BENEFIT_DAYS) FROM TIME_OFF WHERE STAFF_ID = " + staffID ;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
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
       // while (dt2.after(dt1)) {
        while ( dt1.compareTo(dt2) <= 0)
        {
            if (dt1.toString().substring(0,3).toString().equals("Sat"))   { }
            else if (dt1.toString().substring(0,3).toString().equals("Sun")) { }
            else  { days++; }
            c.setTime(sdf.parse(sdt1));
            c.add(Calendar.DATE, 1);
            sdt1 = sdf.format(c.getTime());
            dt1 = new Date(Integer.parseInt(sdt1.substring(0, 4)) - 1900, Integer.parseInt(sdt1.substring(5, 7)) - 1, Integer.parseInt(sdt1.substring(8, 10)));
        }
        Log.i("Counting Days", "Num of working days = " + String.valueOf(days));
        return days;
    }

    protected  boolean datesInOrder(String sdt1, String sdt2) throws ParseException {
        Date dt1, dt2;

       dt1 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt1);
       dt2 =  new SimpleDateFormat("yyyy-mm-dd").parse(sdt2);

       dt1 = new Date( Integer.parseInt(sdt1.substring(0,4))-1900, Integer.parseInt(sdt1.substring(5,7))-1, Integer.parseInt(sdt1.substring(8,10)));
       dt2 =new Date( Integer.parseInt(sdt2.substring(0,4))-1900, Integer.parseInt(sdt2.substring(5,7))-1, Integer.parseInt(sdt2.substring(8,10)));

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