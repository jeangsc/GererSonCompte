package com.example.jean.gerersoncompte.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;

/**
 * Created by V17 on 12/07/2018.
 */

public class SeekBarRangeValues extends RelativeLayout
{
    private SeekBarRange seekBar = null;
    private EditText editLeft = null;
    private EditText editRight = null;

    public SeekBarRangeValues(Context context)
    {
        super(context);
        init(null, 0);
    }

    public SeekBarRangeValues(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public SeekBarRangeValues(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.seek_bar_range_values, this, true);
        seekBar = (SeekBarRange) view.findViewById(R.id.seek_bar_rng_vals_sbr);
        editLeft = (EditText) view.findViewById(R.id.seek_bar_rng_vals_edit_left);
        editRight = (EditText) view.findViewById(R.id.seek_bar_rng_vals_edit_right);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SeekBarRangeValues, defStyle, 0);

        seekBar.setMinVal(a.getFloat(R.styleable.SeekBarRangeValues_seekBarMinVal, 0.0f));
        seekBar.setMaxVal(a.getFloat(R.styleable.SeekBarRangeValues_seekBarMaxVal, 100.0f));
        seekBar.setPosLeft(a.getFloat(R.styleable.SeekBarRangeValues_seekBarPosLeft, seekBar.getMinVal()));
        seekBar.setPosRight(a.getFloat(R.styleable.SeekBarRangeValues_seekBarPosRight, seekBar.getMaxVal()));

        a.recycle();

        //editDate
        editLeft.setText(Tools.floatFormat(seekBar.getPosLeft(), 2));
        editRight.setText(Tools.floatFormat(seekBar.getPosRight(), 2));

        seekBar.setOnChangeListener(new SeekBarRange.OnChangeListener()
        {
            @Override
            public void onCursorChanged(int cursorFocused)
            {
                if(cursorFocused == SeekBarRange.FOCUS_CURSOR_LEFT)
                    editLeft.setText(Tools.floatFormat(seekBar.getPosLeft(), 2));

                if(cursorFocused == SeekBarRange.FOCUS_CURSOR_RIGHT)
                    editRight.setText(Tools.floatFormat(seekBar.getPosRight(), 2));
            }
        });

        editLeft.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus == false)
                    validateEditLeft();
            }
        });

        editLeft.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateEditLeft();
                    return true;
                }
                return false;
            }
        });

        editRight.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus == false)
                    validateEditRight();
            }
        });

        editRight.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    validateEditRight();
                    return true;
                }
                return false;
            }
        });
    }

    private void validateEditLeft()
    {
        String strVal = editLeft.getText().toString();
        if(!strVal.isEmpty())
        {
            float newValue = Float.valueOf(strVal);
            seekBar.setPosLeft(newValue);
            editLeft.setText(Tools.floatFormat(seekBar.getPosLeft(), 2)); //correction from seekBar
        }
    }

    private void validateEditRight()
    {
        String strVal = editRight.getText().toString();
        if(!strVal.isEmpty())
        {
            float newValue = Float.valueOf(strVal);
            seekBar.setPosRight(newValue);
            editRight.setText(Tools.floatFormat(seekBar.getPosRight(), 2));
        }
    }

    public void setMinVal(float val)
    {
        seekBar.setMinVal(val);
    }

    public void setMaxVal(float val)
    {
        seekBar.setMaxVal(val);
    }

    public void setPos(float lVal, float rVal)
    {
        seekBar.setPos(lVal, rVal);
        editLeft.setText(Tools.floatFormat(seekBar.getPosLeft(), 2));
        editRight.setText(Tools.floatFormat(seekBar.getPosRight(), 2));
    }

    public float getPosLeft()
    {
        return seekBar.getPosLeft();
    }

    public float getPosRight()
    {
        return seekBar.getPosRight();
    }
}
