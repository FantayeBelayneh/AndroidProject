package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    final String thisActivity = "Menu";
    Button btnBO, btnTO, btnRQ, btnExit;
    TextView txtWelcome;
    boolean isAdmin;
    int staffID, allowedBenefitDays;
    String emailAddress, staffName;

    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    private CommonTools commonTools;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toast.makeText(this, "Welcome to: " + thisActivity, Toast.LENGTH_LONG).show();

        btnBO = findViewById(R.id.btnbo);
        btnTO = findViewById(R.id.btnto);
        btnRQ = findViewById(R.id.btnrq);
        btnExit = findViewById(R.id.btnExit);
        txtWelcome = findViewById(R.id.txtWelcome);

        dbOperator = new dbOperations(Menu.this);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(Menu.this, "time_off_management", null, 1);
        database = dbOperations.getWritableDatabase();
        commonTools = new CommonTools(this);

        Bundle bundle = getIntent().getExtras();


        isAdmin = bundle.getBoolean("isAdmin");
        staffID = bundle.getInt("staffID");
        allowedBenefitDays = bundle.getInt("allowedBenefitDays");
        emailAddress = bundle.getString("emailAddress");
        staffName = bundle.getString("staffName");
        String welcomeMessage;

        welcomeMessage = "Welcome " + staffName;

        if (isAdmin == true)
        {
            welcomeMessage +=  '\n' +  " " + '\n' +" You are logged in as admin privileged user." ;
            welcomeMessage += '\n' + " " + '\n' + "You can use this app to book vacation days, edit and/or view them";
            welcomeMessage += '\n' + " " + '\n' + "You also can approve or reject staff vacation day requests";
            btnRQ.setEnabled(true);
        }
        else
        {
            welcomeMessage += '\n' + " " + '\n' + "You can use this app to book vacation days and view booked vacation days.";
            btnRQ.setEnabled(false);
        }
        txtWelcome.setText(welcomeMessage);


        btnBO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBO = new Intent(Menu.this, DatesManager.class);
                goBO.putExtra("mode","bo");
                goBO.putExtra("isAdmin", isAdmin);
                goBO.putExtra("workingTable", "BLACK_OUTS");
                startActivity(goBO);
            }
        });

        btnTO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTO = new Intent(Menu.this, DatesManager.class);
                goTO.putExtra("mode","to");
                goTO.putExtra("workingTable", "TIME_OFF");
                goTO.putExtra("staffID", staffID);
                goTO.putExtra("allowedBenefitDays",10);
                goTO.putExtra("emailAddress", emailAddress);
                startActivity(goTO);

            }
        });
        btnRQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRQ = new Intent(Menu.this, ViewRequests.class);
                goRQ.putExtra("workingTable", "TIME_OFF");
                startActivity(goRQ);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}