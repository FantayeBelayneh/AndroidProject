package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

public class DataCollector extends AppCompatActivity {

    Button btnClose, btnSelectStart, btnSelectEnd, btnSave;
    TextView tvStartingDate, tvEndingDate;
    CalendarView calDate;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    private CommonTools commonTools;


    String workingTable,   selectedDate = "", mode = null, dataState = null, startingDate, endingDate, ID_fieldValue, staffID  ;
    boolean canSave = false, waitOver;
    int allowedDays=0, allocatedDays = 0, daysCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collector);
    }
}