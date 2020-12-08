package com.fanta.timeoff_management;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<String, String, String> {



    protected  dbOperations dbOperator;
    protected SQLiteDatabase myDB;
    public SQLiteDatabase database;
    private DatabaseConnection dbOperations;
    private CommonTools commonTools;
    protected SendMail sendEmail;


    private String Subject;
    public String Staff_Name;
    public String emailType;
    public String emailAddress;


    private String mailBody;

    public SendMail(int staffID, String dtFrom, String dtTo, Context cx, int RequestType)
    {
        Cursor cursor;
        String query;
        String staffName = "";
        String requestMode;


        if (RequestType == 0)
        {
            requestMode = " has submitted new request for vacation.";
            Subject = "Vacation Request";
        }
        else
        {
            requestMode = " has amended previous vacation request. ";
            Subject = "Vacation Request - revised";
        }

        dbOperator = new dbOperations(cx);
        myDB = dbOperator.workingDB;
        dbOperations = new DatabaseConnection(cx, "time_off_management", null, 1);
        database = dbOperations.getWritableDatabase();
        commonTools = new CommonTools(cx);

        //commonTools.ShowMessages("compile email", "100");
        query = "SELECT STAFF_NAME, EMAIL_ADDRESS FROM USERS WHERE _id = " +staffID;
        cursor = database.rawQuery(query, null);
        //commonTools.ShowMessages("compile email", "100");
        cursor.moveToFirst();
        staffName = cursor.getString(0);
        emailAddress = cursor.getString(1);
        //commonTools.ShowMessages("compile email", "100 staffName" + staffName);


        mailBody =  staffName + requestMode + " The dates are from " + dtFrom + " to " + dtTo ;
        mailBody += "\n Please review request and take the necessary action.";
        //commonTools.ShowMessages("compile email", "email body" + mailBody);
    }



    @Override
    protected String doInBackground(String... strings) {

        Log.i("mailgen",  "in doinback");
         try {
            String host =  "smtp.office365.com";    // "smtp.gmail.com";
            String from = "time_off_management@outlook.com";
            String pass = "";
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            String[] to = {"cook_time_off_management@outlook.com"};
            String subject = Subject;
            String content_message = mailBody;
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);

            to[0] = emailAddress;
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

    private String SetEmailContent()
    {
        String content = "";
        if (emailType == "VacationRequest")
        {
            content = "A new vacation request has been received. Please take action";
        }
        else if (emailType == "Approval")
        {
            content = "Your recent vacation request has been approved.";
        }
        else if (emailType == "Reject")
        {
            content = "Your recent vacation request has been rejected.";
        }
        return content;
    }

}