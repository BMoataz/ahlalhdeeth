package com.example.master.ahlalhdeeth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.master.ahlalhdeeth.R;

public class MyCustomTextView extends TextView {


    public MyCustomTextView(Context context) {
        super(context);
    }

    public MyCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public MyCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean myfont_content = sharedPref.getBoolean("myfont_content", false);

        if (myfont_content) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomFontTextView, 0, 0);
//            context.getText()
            try {
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/DroidNaskh-Regular.ttf"));
            } finally {
                a.recycle();
            }
        }
    }
}
