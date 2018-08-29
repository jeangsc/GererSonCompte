package com.example.jean.gerersoncompte.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jean.gerersoncompte.R;

import java.util.List;

/**
 * Created by V17 on 29/08/2018.
 */

public class CheckboxListAdapter extends ArrayAdapter<CheckboxListAdapter.CheckboxItem>
{
    public CheckboxListAdapter(Context context, List<CheckboxItem> list)
    {
        super(context,0, list);
    }

    public class CheckboxItem
    {
        private CheckBox itemCheck;

        public String name;
        private boolean isChecked;

        CheckboxItem(String name, boolean isChecked)
        {
            itemCheck = null;
            this.name = name;
            this.isChecked = isChecked;
        }

        public boolean isChecked()
        {
            return isChecked;
        }

        public void setChecked(boolean isChecked)
        {
            this.isChecked = isChecked;
            if(itemCheck != null)
                itemCheck.setChecked(isChecked);
        }
    }

    private static class ViewHolder
    {
        TextView itemText;
        CheckBox itemCheck;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        CheckboxItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_checkbox_list, parent, false);
            viewHolder.itemText  = (TextView) convertView.findViewById(R.id.item_spin_mul_text);
            viewHolder.itemCheck = (CheckBox) convertView.findViewById(R.id.item_spin_mul_check);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(item != null)
        {
            viewHolder.itemText.setText(item.name);
            item.itemCheck = viewHolder.itemCheck;
            item.setChecked(item.isChecked());
        }

        return convertView;
    }

    public void setCheckedAll(boolean isChecked)
    {
        for(int position = 0; position < super.getCount(); position++)
        {
            CheckboxItem item = getItem(position);
            item.setChecked(isChecked);
        }
    }

    public CheckboxItem generateItem(String name, boolean isChecked)
    {
        return new CheckboxItem(name, isChecked);
    }
}
