package com.example.jean.gerersoncompte.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.jean.gerersoncompte.AdapterItem.CheckboxItem;
import com.example.jean.gerersoncompte.R;

import java.util.List;

/**
 * Created by V17 on 29/08/2018.
 */

public class CheckboxListAdapter extends ArrayAdapter<CheckboxItem>
{
    public CheckboxListAdapter(Context context, List<CheckboxItem> list)
    {
        super(context,0, list);
    }

    private static class ViewHolder
    {
        TextView itemText;
        CheckBox itemCB;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Log.i("getView", "getView");
        CheckboxItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_checkbox_list, parent, false);
            viewHolder.itemText  = (TextView) convertView.findViewById(R.id.item_spin_mul_text);
            viewHolder.itemCB = (CheckBox) convertView.findViewById(R.id.item_spin_mul_check);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(item != null)
        {
            item.dump();
            viewHolder.itemText.setText(item.name);
            item.setItemCheck(viewHolder.itemCB);
        }

        return convertView;
    }

    public void setCheckedAll(boolean isChecked)
    {
        Log.i("SetCheckAll", "isChecked = " + String.valueOf(isChecked));
        for(int position = 0; position < super.getCount(); position++)
        {
            CheckboxItem item = getItem(position);
            item.setChecked(isChecked);
        }
    }
}
