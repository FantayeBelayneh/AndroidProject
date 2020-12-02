package com.fanta.timeoff_management;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class BlackOutDates
{

    public ArrayList<blackOutPeriod> BO_days;
    public BlackOutDates()
    {
        BO_days = new ArrayList<blackOutPeriod>();
    }

    public void add(Context ctx,  String StartFrom, String EndingAt)
    {
        blackOutPeriod boPeriod = new blackOutPeriod(ctx, StartFrom, EndingAt);
        BO_days.add(boPeriod);
    }

    class blackOutPeriod
    {
        public TextView StartFrom;
        public TextView EndingAt;

        public View borow;

        public blackOutPeriod(Context ctx, String StartFrom_, String EndingAt_)
        {
            StartFrom = new TextView(ctx);
            EndingAt = new TextView(ctx);

            StartFrom.setText(StartFrom_);
            EndingAt.setText(EndingAt_);
        }

    }

}
