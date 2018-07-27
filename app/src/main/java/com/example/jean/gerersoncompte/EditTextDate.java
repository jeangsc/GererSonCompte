package com.example.jean.gerersoncompte;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class EditTextDate extends RelativeLayout
{
    public static final String DATE_DEFAULT = "__/__/____";
    public static final String DATE_MIN = "01/01/1900";
    public static final String DATE_MAX = "31/12/9999";

    private EditTextErrorChecker editDate = null;
    private ImageButton buttonDate = null;

    private DateTextWatcher dateTextWatcher = null;

    private class DateTextWatcher implements TextWatcher
    {
        private int selector;
        private boolean forceChange;
        private String strDate;

        DateTextWatcher()
        {
            this.selector = 0;
            this.forceChange = false;
            this.strDate = editDate != null ? editDate.getText().toString() : DATE_DEFAULT;
        }

        public void setForceChange(boolean forceChange)
        {
            this.forceChange = forceChange;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            Log.e(TAG, "on text changed");
            Log.e("Change", "start = " + String.valueOf(start));
            Log.e("Change", "before = " + String.valueOf(before));
            Log.e("Change", "count = " + String.valueOf(count));
            Log.e("Change", "str = " + s);

            if(forceChange)
                return;

            if(start == 10)
                return;

            String newStr = s.toString();
            strDate = s.toString().substring(0, start);

            int strDateLength = strDate.length();
            int i = start;
            selector = start;
            int newInserted = 0;
            int newErased = before - count;

            while(i < newStr.length() && strDateLength < 10)
            {
                boolean isNewInsertion = i < start + count;
                if(!isNewInsertion && newErased > 0)
                {
                    strDate += DATE_DEFAULT.charAt(strDateLength);
                    strDateLength++;
                    newErased--;
                }
                else if(strDateLength == 2 || strDateLength == 5)
                {
                    strDate += "/";
                    strDateLength++;
                }
                else if(newStr.substring(i, i+1).matches("[0-9_]"))
                {
                    if(isNewInsertion || newInserted <= 0)
                    {
                        strDate += newStr.charAt(i);
                        strDateLength++;
                    }
                    newInserted += isNewInsertion ? 1 : -1;
                    i++;
                }
                else //ignored
                    i++;

                if(isNewInsertion)
                    selector = strDateLength;
            }
            if((selector == 2 || selector == 5) && count > 0)
                selector++;
            else if((selector == 3 || selector == 6) && count == 0)
                selector--;

            while(strDateLength < 10)
            {
                strDate += DATE_DEFAULT.charAt(strDateLength);
                strDateLength++;
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            Log.e(TAG, "on text changed");
            Log.e("After", "str = " + s.toString());
            Log.e("After", "strDate = " + strDate);

            if(!forceChange)
            {
                forceChange = true;
                editDate.setText(strDate);
                editDate.setSelection(selector);
                forceChange = false;
            }
            //update();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            Log.e(TAG, "on text changed");
        }
    }

    public EditTextDate(Context context)
    {
        super(context);
        init(null, 0);
    }

    public EditTextDate(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public EditTextDate(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr)
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_text_date, this, true);

        editDate = (EditTextErrorChecker) view.findViewById(R.id.edit_text_date_edit);
        buttonDate = (ImageButton)view.findViewById(R.id.edit_text_date_button);

        //editDate
        dateTextWatcher = new DateTextWatcher();
        editDate.setText(Tools.getDate());
        editDate.addTextChangedListener(dateTextWatcher);
        editDate.setFilter(EditTextErrorChecker.FILTER_DATE_EXISTS);

        RelativeLayout currentLayout = (RelativeLayout) findViewById(R.id.edit_text_date_rlo);
        currentLayout.addView(editDate.getTextError());

        //buttonDate
        buttonDate.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v)
            {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        String strDate = String.format("%02d", dayOfMonth) + "/" +
                                         String.format("%02d", (month+1)) + "/" +
                                         String.format("%04d", year);
                        dateTextWatcher.setForceChange(true);
                        editDate.setText(strDate);
                        dateTextWatcher.setForceChange(false);
                    }
                };
                int year, month, day;
                String strDate = getDate();
                if(dateExists(strDate))
                {
                    year = Tools.getYear(strDate);
                    month = Tools.getMonth(strDate)-1;
                    day = Tools.getDay(strDate);
                }
                else
                {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }
                DatePickerDialog dialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
                dialog.show();
            }
        });
    }

    public String getDate()
    {
        return (editDate != null) ? editDate.getText().toString() : "";
    }

    public void setDate(String date)
    {
        if(editDate != null)
            editDate.setText(date);
    }

    private static boolean checkFormat(String strDate)
    {
        String regex = "^[0-9]{2}/[0-9]{2}/[0-9]{4}$";
        return strDate.matches(regex);
    }

    public static boolean dateExists(String strDate)
    {
        if(!checkFormat(strDate))
            return false;

        int day = Integer.valueOf(strDate.substring(0, 2));
        int month = Integer.valueOf(strDate.substring(3, 5));
        int year = Integer.valueOf(strDate.substring(6, 10));

        int[] daysByMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if(year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) //february finishes the 29th
            daysByMonth[1]++;

        if(year < 1900)
            return false;
        if(month < 1 || month > 12)
            return false;
        if(day < 1 || day > daysByMonth[month-1])
            return false;

        return true;
    }

    public EditTextErrorChecker getEditText()
    {
        return editDate;
    }
}
