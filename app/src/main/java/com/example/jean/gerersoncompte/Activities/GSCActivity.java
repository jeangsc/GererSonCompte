package com.example.jean.gerersoncompte.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.example.jean.gerersoncompte.ActivityManager;
import com.example.jean.gerersoncompte.Tools;

public class GSCActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActivityManager am = ActivityManager.getInstance();
        am.setVisibility(this, true);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ActivityManager am = ActivityManager.getInstance();
        am.setVisibility(this, true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ActivityManager am = ActivityManager.getInstance();
        am.setVisibility(this, true);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        ActivityManager am = ActivityManager.getInstance();
        am.setVisibility(this, false);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ActivityManager am = ActivityManager.getInstance();
        am.removeActivity(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }
}
