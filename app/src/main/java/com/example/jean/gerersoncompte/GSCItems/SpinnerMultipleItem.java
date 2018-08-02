package com.example.jean.gerersoncompte.GSCItems;

/**
 * Created by V17 on 30/07/2018.
 */

public class SpinnerMultipleItem
{
    private String itemName;
    private boolean itemChecked;

    public SpinnerMultipleItem(String name)
    {
        itemName = name;
        itemChecked = true;
    }

    public SpinnerMultipleItem(String name, boolean checked)
    {
        itemName = name;
        itemChecked = checked;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isItemChecked() {
        return this.itemChecked;
    }

    public void setItemChecked(boolean itemChecked) {
        this.itemChecked = itemChecked;
    }
}
