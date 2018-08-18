package com.example.izi.multiple;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

public class CustomView extends android.support.v7.widget.AppCompatTextView {
    int x;
    int y;
    int alpha;
    boolean down;
    boolean up;
    int counter;
    int green;
    Context context;
    int count;
    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        alpha = 0;
        counter = 0;
        down = false;
        green = 229;
        this.context = context;
        count = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setBackgroundColor(Color.argb(255, 179, green, 252));
        if(counter < 15){
            if(green == 229){
                counter++;
                down = false;
            }
            if(green == 255){
                down = true;
            }
            if(down) {
                green--;
            }
            else {
                green++;
            }
        }
    }

    public void startAnimation(){
        alpha = 130;
        down = false;
        counter = 0;
        green = 229;
        invalidate();
    }
}
