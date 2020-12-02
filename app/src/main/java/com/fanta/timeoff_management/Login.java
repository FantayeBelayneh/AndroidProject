package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    Button btnExit, btnLogin;
    EditText txtLogin, txtPassword;
    CommonTools commonTools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        commonTools = new CommonTools(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnExit = findViewById(R.id.btnExit);
        txtPassword = findViewById(R.id.etPassword);
        txtLogin = findViewById(R.id.etLoginName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    validateLogin();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Toast.makeText(Login.this, "You are exiting!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    } // end of onCreate

    protected void validateLogin()
    {
        String sLoginName = txtLogin.getText().toString().trim();
        String sPassword = txtPassword.getText().toString().trim();

        if (sLoginName.length() == 0)
        {
            commonTools.ShowMessages("Login Id","Please enter your login ID");
        }
        else if (sPassword.length() == 0)
        {
            commonTools.ShowMessages("Password","Please enter your password");
        }
    }
}