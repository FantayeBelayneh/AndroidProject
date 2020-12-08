package com.fanta.timeoff_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Login extends AppCompatActivity {

    Button btnExit, btnLogin;
    EditText txtLogin, txtPassword;
    CommonTools commonTools;


    //EditText login;
    //EditText password;
    String login_dp_key = "sp_key_login";
    UserProfile userProfile;
    int iDelinquentAttempts =1;
    protected String dialogMessage = "", dialogTitle = "";
    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    SharedPreferences sharedPreferences ;
    public int USER_TYPE = 0;
    public final int isAdmin = 1;
    public final int nonAdmin = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("LoginID", MODE_PRIVATE);

        dbOperator = new dbOperations(Login.this);
        myDB = dbOperator.workingDB;
        commonTools = new CommonTools(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnExit = findViewById(R.id.btnExit);
        txtPassword = findViewById(R.id.etPassword);
        txtLogin = findViewById(R.id.etLoginName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor LoginID = sharedPreferences.edit();
                Log.i("userid", txtLogin.getText().toString());
                LoginID.putString(login_dp_key, txtLogin.getText().toString());
                LoginID.commit();
                if (qualifyCredentials() ==  true)
                {
                    if (validateLoginCredentials())
                    {
                        getUserProfile();
                        SendMail sm = new SendMail() ;
                        sm.execute();
                    }
                }


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

    protected void getUserProfile()
    {
        userProfile = dbOperator.getUserProfile(myDB, txtLogin.getText().toString());
        userProfile.AdminUser = USER_TYPE;
    }

    private boolean validateLoginCredentials()
    {
        boolean validateLogin = false;
        try
        {
            String thisUser = txtLogin.getText().toString();
            Log.i("credent 000", "You are logging in as admin priveleged user.");
            validateLogin = dbOperator.validateLogin(myDB, thisUser, txtPassword.getText().toString());
            if (validateLogin == false)
            {
                Log.i("credent 100", "You are logging in as admin priveleged user.");
                dialogMessage = "Please enter correct user id and password";
                dialogMessage+= '\n' + "A maximum of 3 attempts is allowed before this app terminates.";
                dialogMessage += '\n' + "This is attempt # " + iDelinquentAttempts;
                dialogTitle = "Invalid login credential were provided.";
                warnOnDelinquencies(); // created to get access to the delinquency count
            }
            if (validateLogin)
            {
                Log.i("credent 200", "You are logging in as admin priveleged user.");
                dialogTitle = "Admin user";
                boolean adminUser = dbOperator.isAdminCredential(myDB, thisUser);
                if (adminUser == true)
                {
                    Log.i("credent 210", "You are logging in as admin priveleged user.");
                    dialogMessage = "This is admin user";
                    Log.i("credent", "You are logging in as admin priveleged user.");
                    commonTools.ShowMessages(dialogTitle,dialogMessage);
                    USER_TYPE = isAdmin;
                }
                else
                {
                    Log.i("credent 220", "You are logging in as admin priveleged user.");
                    dialogMessage = "Not admin user";
                    Log.i("credent", "You can view your booked time-offs and book additional time");
                    commonTools.ShowMessages(dialogTitle,dialogMessage);
                }
            }

        }
        catch (Exception x)
        {
           Log.i("credent", x.getMessage().toString());
            commonTools.ShowExceptionMessage(x, "Credential Validation");
        }
        return validateLogin;
    }
    private boolean qualifyCredentials()
    {
        boolean bRetValue = false;
        String warningTitle = "Credential value inspection";
        String sLoginName = txtLogin.getText().toString(), sPassword = txtPassword.getText().toString();
        if (sLoginName.trim().length() == 0)
        {
            commonTools.ShowMessages(warningTitle, "Please key in your user id");
        }
        else if (sLoginName.trim().length() > 6)
        {
            commonTools.ShowMessages(warningTitle, "User id invalid - maximum length is 6");
        }
        else if (sPassword.trim().length() == 0)
        {
            commonTools.ShowMessages(warningTitle, "Please key in your password");
        }
        else if (sPassword.trim().length() > 6)
        {
            commonTools.ShowMessages(warningTitle, "Password maximumn length is 6");
        }
        else
        {
            bRetValue = true;
        }
        return bRetValue;
    }
    private void warnOnDelinquencies()
    {
        iDelinquentAttempts++;
        commonTools.ShowMessages(dialogTitle,dialogMessage);
        if (iDelinquentAttempts > 3) finish();
    }


    public class SendMail extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            Log.i("mailgen",  "in doinback");
            try {
                String host =  "smtp.office365.com";    // "smtp.gmail.com";
                String from = "time_off_management@outlook.com";
                String pass = "Dec$$2020";
                Properties props = System.getProperties();
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.user", from);
                props.put("mail.smtp.password", pass);
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                String[] to = {"time_off_management@outlook.com"};
                String subject = "Surprise";
                String content_message = "Dinner has been announced";
                Session session = Session.getDefaultInstance(props, null);
                MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
        } catch (MessagingException e) {
            e.printStackTrace();
        }

            InternetAddress[] toAddress = new InternetAddress[to.length ];
            for (int i = 0; i < to.length; i++) {
            toAddress[i] = new InternetAddress(to[i]);
            }

        System.out.println(Message.RecipientType.TO);
        for (int i = 0; i < toAddress.length; i++) {
            try {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        try {
            message.setSubject(subject);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            message.setContent(content_message, "text/html; charset=\"UTF-8\"");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            Log.i("mailgen",    "Transport  connect");
            transport.connect(host, from, pass);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            Log.i("mailgen",  " send message");
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            Log.i("mailgen",   e.getMessage().toString());
            e.printStackTrace();
        }
        try {
            Log.i("mailgen",  " closing");
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
                Log.i("mailgen",  "exit");

            }
            catch (Exception z)
            {
                commonTools.ShowMessages("Async",z.getMessage().toString());
            }
            return "hello";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("mailgen",  "post ");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("mailgen",  "pre");
        }
    }

}