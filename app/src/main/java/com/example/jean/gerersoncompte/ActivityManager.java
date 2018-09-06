package com.example.jean.gerersoncompte;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by V17 on 06/09/2018.
 */

public class ActivityManager
{
    private HashMap<Context, Boolean> activitiesVisibilities;
    private static final ActivityManager ourInstance = new ActivityManager();

    private ActivityManager()
    {
        activitiesVisibilities = new HashMap<Context, Boolean>();
    }

    public static ActivityManager getInstance()
    {
        return ourInstance;
    }

    public void setVisibility(Context context, boolean isVisible)
    {
        activitiesVisibilities.put(context, isVisible);
    }

    public void removeActivity(Context context)
    {
        activitiesVisibilities.remove(context);
    }

    public boolean appVisible()
    {
        for(Boolean isVisible : activitiesVisibilities.values())
        {
            if(isVisible)
                return true;
        }
        return false;
    }
}
