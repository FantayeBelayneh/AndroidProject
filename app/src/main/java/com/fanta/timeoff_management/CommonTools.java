package com.fanta.timeoff_management;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.widget.Toast;

public class CommonTools
{
    Context ctx;
    public CommonTools(Context ctx)
    {
        this.ctx = ctx;
    }
    protected void ShowMessages(String dialogTitle, String dialogMessage)
    {
        AlertDialog.Builder messaging = new AlertDialog.Builder(ctx);
        messaging.setTitle(dialogTitle);
        messaging.setMessage(dialogMessage);

        messaging.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface messageInterface, int which)
            {
                messageInterface.dismiss();
            }
        });
        messaging.create().show();
    }

    protected void ShowExceptionMessage(Exception x, String hint)
    {
        AlertDialog.Builder messaging = new AlertDialog.Builder(ctx);
        messaging.setTitle(hint);
        messaging.setMessage(x.getMessage().toString());

        messaging.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface messageInterface, int which)
            {
                messageInterface.dismiss();
            }
        });
        AlertDialog dialog = messaging.create();
        dialog.show();
    }

    protected void ShowToast(String textToDisplay)
    {
        Toast.makeText(ctx, textToDisplay, Toast.LENGTH_LONG);
    }
}
