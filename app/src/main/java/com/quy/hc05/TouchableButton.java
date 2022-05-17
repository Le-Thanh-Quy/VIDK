package com.quy.hc05;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


public class TouchableButton extends androidx.appcompat.widget.AppCompatButton {

    public TouchableButton(Context context) {
        super(context);
    }

    public TouchableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }
}