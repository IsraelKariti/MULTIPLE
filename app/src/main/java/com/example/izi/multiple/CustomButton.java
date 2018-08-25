package com.example.izi.multiple;

import android.content.Context;

public class CustomButton extends android.support.v7.widget.AppCompatButton {
    public int layer;
    public int databaseID;
    public boolean expand;
    public int indexInRow;

    // variable only for LogActivity
    public int countAppearancesPerDay;

    public CustomButton(Context context) {
        super(context);
        expand = true;
        countAppearancesPerDay = 0;
    }
}
