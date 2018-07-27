package com.example.jean.gerersoncompte;

import android.app.AlertDialog;
import android.content.Context;
import android.view.MotionEvent;

public class GSCDialog extends AlertDialog
{
    protected GSCDialog(Context context)
    {
        super(context);
    }

    protected GSCDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
    }

    protected GSCDialog(Context context, int themeResId)
    {
        super(context, themeResId);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }

    public static class GSCBuilder extends Builder
    {
        public GSCBuilder(Context context) {
            super(context);
        }

        public GSCBuilder(Context context, int themeResId) {
            super(context, themeResId);
        }

        public GSCDialog create ()
        {
            AlertDialog alertDialog = super.create();

        }
    }
}
