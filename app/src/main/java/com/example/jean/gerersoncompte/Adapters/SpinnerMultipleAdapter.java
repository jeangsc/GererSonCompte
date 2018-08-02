package com.example.jean.gerersoncompte.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jean.gerersoncompte.GSCItems.SpinnerMultipleItem;
import com.example.jean.gerersoncompte.R;

import java.util.List;

/**
 * Created by V17 on 30/07/2018.
 */

public class SpinnerMultipleAdapter extends ArrayAdapter<SpinnerMultipleItem>
{
    public SpinnerMultipleAdapter(Context context, List<SpinnerMultipleItem> list)
    {
        super(context,0, list);
    }

    private static class ViewHolder
    {
        TextView itemText;
        CheckBox itemCheck;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SpinnerMultipleItem item = getItem(position);
        SpinnerMultipleAdapter.ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new SpinnerMultipleAdapter.ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_multiple, parent, false);
            viewHolder.itemText  = (TextView) convertView.findViewById(R.id.item_spin_mul_text);
            viewHolder.itemCheck = (CheckBox) convertView.findViewById(R.id.item_spin_mul_check);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (SpinnerMultipleAdapter.ViewHolder) convertView.getTag();
        }

        if(item != null)
        {
            viewHolder.itemText.setText(item.getItemName());
            viewHolder.itemCheck.setChecked(item.isItemChecked());
        }

        return convertView;
    }
}
