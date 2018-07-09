package com.example.jean.gerersoncompte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by V17 on 02/05/2018.
 */

public class OperationAdapter extends ArrayAdapter<Operation> implements Filterable
{
    private List<Operation> operations;
    private List<Operation> operationsAll;
    OperationFilter operationFilter;

    public OperationAdapter(Context context, List<Operation> list)
    {
        super(context,0, list);
        this.operations = list;
        this.operationsAll = list;
    }

    private static class ViewHolder
    {
        TextView textName;
        TextView textCategorie;
        TextView textAmount;
        TextView textExecDate;
    }

    @Override
    public void remove(Operation object)
    {
        if(operations != operationsAll)
            operations.remove(object);
        super.remove(object);
    }

    @Override
    public void add(Operation object)
    {
        if(operations != operationsAll)
            operations.add(object);
        super.add(object);
    }

    @Override
    public void clear()
    {
        if(operations != operationsAll)
            operations.clear();
        super.clear();
    }

    @Override
    public int getCount()
    {
        return operations.size();
    }

    @Override
    public Operation getItem(int position)
    {
        return operations.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Operation ope = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.operation_resume, parent, false);
            viewHolder.textName = (TextView) convertView.findViewById(R.id.ope_res_label_nom);
            viewHolder.textCategorie = (TextView) convertView.findViewById(R.id.ope_res_value_categorie);
            viewHolder.textAmount = (TextView) convertView.findViewById(R.id.ope_res_value_montant);
            viewHolder.textExecDate = (TextView) convertView.findViewById(R.id.ope_res_value_execution);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String opeName      = ope.getName();
        String opeCategorie = ope.getCategory();
        float  opeAmount    = ope.getSignedAmount();
        String opeExecDate  = ope.getExecDate();

        int green = getContext().getResources().getColor(R.color.colorGreenLight);
        int red = getContext().getResources().getColor(R.color.colorRedLight);

        viewHolder.textName.setText(opeName);
        viewHolder.textCategorie.setText(opeCategorie);
        viewHolder.textAmount.setText(Tools.getFormattedAmount(opeAmount, "EUR", true));
        viewHolder.textExecDate.setText(opeExecDate);
        convertView.setBackgroundColor(ope.isGain() ? green : red);

        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        if(operationFilter == null)
            operationFilter = new OperationFilter();
        return operationFilter;
    }

    private class OperationFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();
            if(constraint != null)
            {
                ArrayList<Operation> filterList = new ArrayList<Operation>();
                for (Operation operation : operationsAll)
                {
                    String name = Tools.stripAccents(operation.getName().toLowerCase());
                    if(constraint.length() > name.length())
                        continue;

                    if(!name.substring(0, constraint.length()).equals(constraint))
                        continue;

                    filterList.add(operation);
                }
                results.values = filterList;
                results.count = filterList.size();
            }
            else
            {
                results.values = operationsAll;
                results.count = operationsAll.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            operations = (ArrayList<Operation>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged()
    {
        this.setNotifyOnChange(false);
        Collections.sort(operations, new Comparator<Operation>()
        {
            @Override
            public int compare(Operation o1, Operation o2)
            {
                return o1.getExecDate().compareTo(o2.getExecDate());
            }
        });
        this.setNotifyOnChange(true);
        super.notifyDataSetChanged();
    }
}

