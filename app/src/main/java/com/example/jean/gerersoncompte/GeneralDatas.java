package com.example.jean.gerersoncompte;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by V17 on 26/04/2018.
 */

class GeneralDatas implements Serializable
{

    private static final GeneralDatas ourInstance = new GeneralDatas();

    private transient Context context;
    public  transient boolean isLoaded; //flag is loaded

    public long accCurId; //current account
    public String lastDate; //last updated date

    static GeneralDatas getInstance()
    {
        return ourInstance;
    }

    private GeneralDatas()
    {
        this.context = null;
        this.accCurId = 0;
        this.isLoaded = false;
        this.lastDate = "";
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public boolean load()
    {
        boolean succeed = false;
        String fileName = context.getResources().getString(R.string.fichier_donnees_generales);
        Object obj = Tools.getObjFromFile(context, fileName);
        GeneralDatas copy = (obj != null) ? (GeneralDatas)obj : null;
        if(copy != null)
        {
            this.accCurId = copy.accCurId;
            this.lastDate = copy.lastDate;
            succeed = true;
        }
        isLoaded = true;
        return succeed;
    }

    public boolean save()
    {
        boolean succeed = false;
        if(context == null)
            return succeed;

        String fileName = context.getResources().getString(R.string.fichier_donnees_generales);
        Tools.setObjToFile(context, fileName, this);

        return succeed;
    }
}
