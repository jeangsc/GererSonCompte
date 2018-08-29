package com.example.jean.gerersoncompte.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.jean.gerersoncompte.GSCItems.SpinnerMultipleItem;
import com.example.jean.gerersoncompte.R;

import java.util.List;

/**
 * Created by V17 on 30/07/2018.
 */

public class SpinnerMultipleAdapter extends ArrayAdapter<SpinnerMultipleItem>
{
    private OnCheckListener checkListener;

    public interface OnCheckListener
    {
        public void onCheck(int index);
        public void onUncheck(int index);
    }

    public SpinnerMultipleAdapter(Context context, List<SpinnerMultipleItem> list)
    {
        super(context,0, list);
    }

    private static class ViewHolder
    {
        TextView itemText;
        CheckBox itemCheck;
        int position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        SpinnerMultipleItem item = getItem(position);
        SpinnerMultipleAdapter.ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new SpinnerMultipleAdapter.ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_checkbox_list, parent, false);
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
            viewHolder.position = position;
            viewHolder.itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkListener == null)
                        return;

                    if (isChecked)
                        checkListener.onCheck(position);
                    else
                        checkListener.onUncheck(position);
                }
            });
        }

        return convertView;
    }

    public void setOnCheckListener(OnCheckListener listener)
    {
        this.checkListener = listener;
    }
}
