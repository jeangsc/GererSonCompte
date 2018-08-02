package com.example.jean.gerersoncompte.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jean.gerersoncompte.R;

/**
 * Created by V17 on 02/07/2018.
 */

public class EditTextErrorChecker extends android.support.v7.widget.AppCompatEditText
{
    public static final byte FILTER_NONE        = 0b0000;
    public static final byte FILTER_NOT_EMPTY   = 0b0001;
    public static final byte FILTER_NOT_ZERO    = 0b0010;
    public static final byte FILTER_DATE_EXISTS = 0b0100;

    private final float textFactor = 0.4f;

    private TextView textError = null;
    private byte filter;


    public EditTextErrorChecker(Context context)
    {
        super(context);
        init(context, null);
    }

    public EditTextErrorChecker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public EditTextErrorChecker(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        createTextError();
        if(attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.EditTextErrorChecker,
                    0, 0);

            try
            {
                filter = (byte)a.getInteger(R.styleable.EditTextErrorChecker_filter, (int)FILTER_NONE);
                setTextError(a.getString(R.styleable.EditTextErrorChecker_textError));
            }
            finally
            {
                a.recycle();
            }
        }
        else
            filter = FILTER_NONE;

        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                    check();
            }
        });
    }

    private void createTextError()
    {
        if(textError != null)
            return;
        textError = new TextView(getContext());
        textError.setTextSize(getTextSize() * textFactor);
        textError.setTextColor(getResources().getColor(R.color.colorRedError));
        textError.setVisibility(INVISIBLE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                                             RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_LEFT, this.getId());
        params.addRule(RelativeLayout.BELOW, this.getId());
        textError.setLayoutParams(params);
    }

    public void setTextError(String strError)
    {
        if(textError != null)
            textError.setText(strError);
    }

    public void setError(boolean isError)
    {
        if(textError != null)
        {
            int visibility = isError ? VISIBLE : INVISIBLE;
            textError.setVisibility(visibility);
        }
    }

    public TextView getTextError()
    {
        return textError;
    }

    public boolean check()
    {
        String str = getText().toString();

        boolean isValid = true;
        isValid = isValid && ((filter & FILTER_NOT_EMPTY)   == 0b0000 || checkFilterNotEmpty(str)  );
        isValid = isValid && ((filter & FILTER_NOT_ZERO)    == 0b0000 || checkFilterNotZero(str)   );
        isValid = isValid && ((filter & FILTER_DATE_EXISTS) == 0b0000 || checkFilterDateExists(str));

        if(isValid)
            setError(false);
        else
            setError(true);

        return isValid;
    }

    public boolean checkFilterNotEmpty(String str)
    {
        return !str.isEmpty();
    }

    public boolean checkFilterNotZero(String str)
    {
        if(!str.isEmpty())
            return Float.valueOf(str) != 0.0f;
        return true;
    }

    public boolean checkFilterDateExists(String str)
    {
        return EditTextDate.dateExists(str);
    }

    public void setFilter(byte filter)
    {
        this.filter = filter;
    }
}
