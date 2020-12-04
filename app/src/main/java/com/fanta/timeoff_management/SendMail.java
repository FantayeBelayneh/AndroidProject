package com.fanta.timeoff_management;

import android.os.AsyncTask;

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

public abstract class SendMail extends AsyncTask <URL, Integer, Long> implements SendMail_ {

    CommonTools commonTools;
    //@Override
    protected Long doInBackground() {


        Long dummy = (long) 1234 ;
        String host = "smtp.gmail.com";
        String from = "bela5510@mylaurier.ca";

        return dummy;
    }
}
