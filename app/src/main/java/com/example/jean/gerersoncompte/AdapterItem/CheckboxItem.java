package com.example.jean.gerersoncompte.AdapterItem;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class CheckboxItem
{
    public String name;
    private boolean isChecked;

    public CheckboxItem(String name)
    {
        this.name = name;
        this.isChecked = true;
    }

    public CheckboxItem(String name, boolean isChecked)
    {
        this.name = name;
        this.isChecked = isChecked;
    }

    public CheckboxItem(CheckboxItem cbi)
    {
        name = cbi.name;
        isChecked = cbi.isChecked;
    }

    public boolean isChecked()
    {
        return isChecked;
    }

    public void setChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    public void setItemCheck(CheckBox cbItem)
    {
        cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("isChecked", String.valueOf(b));
                Log.i("name", name);
                Log.i("adress", String.valueOf(this));
                isChecked = b;
            }
        });
        cbItem.setChecked(isChecked);
    }

    public void dump()
    {
        Log.i("Dump name", name);
        Log.i("Dump checked", String.valueOf(isChecked));
    }
}
