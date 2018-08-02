package com.example.jean.gerersoncompte.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.view.MotionEvent;

import com.example.jean.gerersoncompte.Tools;

public class GSCDialog extends AlertDialog
{
    public GSCDialog(Context context)
    {
        super(context);
    }

    public GSCDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    public GSCDialog(Context context, int themeResId)
    {
        super(context, themeResId);
    }

    public void setPositiveButton(int textId, final OnClickListener listener)
    {
        setButton(BUTTON_POSITIVE, getContext().getString(textId), listener);
    }

    public void setNegativeButton(int textId, final OnClickListener listener)
    {
        setButton(BUTTON_NEGATIVE, getContext().getString(textId), listener);
    }

    @Override
    public void setCancelable(boolean cancelable)
    {
        super.setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelable);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }
}
