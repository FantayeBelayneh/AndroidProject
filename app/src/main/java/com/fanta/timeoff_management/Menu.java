package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    Button btnBO, btnTO, btnRQ, btnExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnBO = findViewById(R.id.btnbo);
        btnTO = findViewById(R.id.btnto);
        btnRQ = findViewById(R.id.btnrq);
        btnExit = findViewById(R.id.btnExit);

/*
        mode = "to";
        workingTable = "TIME_OFF";
        isAdmin = false;
        staffID = "2";
        allowedBenefitDays = "10";
        emailAddress = "time_off_management@outlook.com";

      */
        final String isAdmin = "no";
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
                startActivity(goTO);
            }
        });
        btnRQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRQ = new Intent(Menu.this, EditDates.class);
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