package com.example.jean.gerersoncompte;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by V17 on 13/03/2018.
 */

public class Tools
{
    public static String getFormattedAmount(float amount, String currency, boolean plusSign)
    {
        String sign = (plusSign == true && amount > 0.0f) ? "+" : "";
        String result = sign + getFormattedAmount(amount, currency);
        return result;
    }

    public static String getFormattedAmount(float amount, String currency)
    {
        String intPart, decPart;
        intPart = String.format("%,d",  (int)(amount)).replace(',', ' ');
        decPart = String.format("%.2f", amount);
        decPart = decPart.substring(decPart.length()-2, decPart.length());
        String result = intPart + "," + decPart + " " + currency;
        return result;
    }

    public static int getColorById(Context context, int id)
    {
        return ContextCompat.getColor(context, id);
    }

    public static int getColorFromSign(float amount, int idColPos, int idColNeg, int idColNone, Context context)
    {
        int color = (amount > 0.0f) ? getColorById(context, idColPos) :
                    ((amount < 0.0f) ? getColorById(context, idColNeg) :
                            getColorById(context, idColNone));
        return color;
    }

    public static String getDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatted = format.format(calendar.getTime());
        return dateFormatted;
    }

    public static Object getObjFromFile(Context context, String path)
    {
        Object obj = null;
        try
        {
            FileInputStream fis = context.openFileInput(path);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));

            if (ois != null)
            {
                obj = ois.readObject();
                ois.close();
            }

        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return obj;
    }

    public static boolean setObjToFile(Context context, String path, Object object)
    {
        if(context == null)
            return false;

        boolean succeed = false;
        try
        {
            FileOutputStream fos = context.openFileOutput(path, context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));

            if (oos != null)
            {
                oos.writeObject(object);
                oos.close();
                succeed = true;
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return succeed;
    }

    public static String addDays(String date, int days)
    {
        return date;
    }

    public static long getMillis(String dateStr)
    {
        Date date = null;
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            date = formattedDate.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return (date != null) ? date.getTime() : -1;
    }

    public static boolean expiredDate(String execDate, String refDate)
    {
        long execDateMillis = getMillis(execDate);
        long refDateMillis = getMillis(refDate);

        if(execDateMillis == -1 || refDateMillis == -1)
            return false;

        return execDateMillis <= refDateMillis;
    }

    public static String getStringFromColumn(Cursor cursor, String colName, String valueDef)
    {
        int idx = cursor.getColumnIndex(colName);
        return (idx != -1) ? cursor.getString(idx) : valueDef;
    }

    public static float getFloatFromColumn(Cursor cursor, String colName, float valueDef)
    {
        int idx = cursor.getColumnIndex(colName);
        return (idx != -1) ? cursor.getFloat(idx) : valueDef;
    }

    public static long getLongFromColumn(Cursor cursor, String colName, long valueDef)
    {
        int idx = cursor.getColumnIndex(colName);
        return (idx != -1) ? cursor.getLong(idx) : valueDef;
    }

    public static boolean getBooleanFromColumn(Cursor cursor, String colName, boolean valueDef)
    {
        int idx = cursor.getColumnIndex(colName);
        return (idx != -1) ? (cursor.getInt(idx) == 0 ? false : true) : valueDef;
    }

    public static void autoClearFocus(AppCompatActivity activity, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = activity.getCurrentFocus();
            if (v instanceof EditText || v instanceof EditTextErrorChecker)
            {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }

    public static void autoClearFocus(GSCDialog dialog, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = dialog.getCurrentFocus();
            if (v instanceof EditText || v instanceof EditTextErrorChecker)
            {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
    }



    public static String stripAccents(String str)
    {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static int compareDates(String date1, String date2)
    {
        if(date1.length() != 10 || date2.length() != 10)
            return 0;
        String format1 = date1.substring(6, 10) + date1.substring(3, 5) + date1.substring(0, 2);
        String format2 = date2.substring(6, 10) + date2.substring(3, 5) + date2.substring(0, 2);
        return format1.compareTo(format2);
    }

    public static int getYear(String date)
    {
        if(date.length() < 10)
            return -1;
        return Integer.valueOf(date.substring(6, 10));
    }

    public static int getMonth(String date)
    {
        if(date.length() < 5)
            return -1;
        return Integer.valueOf(date.substring(3, 5));
    }

    public static int getDay(String date)
    {
        if(date.length() < 2)
            return -1;
        return Integer.valueOf(date.substring(0, 2));
    }

    public static String floatFormat(float val, int decimals)
    {
        if(decimals <= 0)
            return String.valueOf(val);

        String valFormat = "%." + String.valueOf(decimals)+ "f";
        return String.format(valFormat, val).replace(',', '.');
    }

    public static float floatTruncated(float val, int decimals)
    {
        String strVal = floatFormat(val, decimals);
        return Float.valueOf(strVal);
    }
}
