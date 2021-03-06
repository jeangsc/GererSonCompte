package com.example.jean.gerersoncompte.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.jean.gerersoncompte.AdapterItem.CheckboxItem;
import com.example.jean.gerersoncompte.GSCItems.Operation;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;
import com.example.jean.gerersoncompte.Views.EditTextDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by V17 on 02/05/2018.
 */

public class OperationAdapter extends ArrayAdapter<Operation> implements Filterable
{
    public final static byte SORT_NAME     = 0;
    public final static byte SORT_CATEGORY = 1;
    public final static byte SORT_AMOUNT   = 2;
    public final static byte SORT_EXECDATE = 3;

    public final static byte SORT_ASC = 1;
    public final static byte SORT_DESC = -1;

    private List<Operation> operations;
    private List<Operation> operationsAll;

    private OperationFilter operationFilter;
    private FilterFields filterFields;
    private CharSequence searchSeq;
    private int sortField;
    private int sortSide;
    private boolean updateSort;

    public OperationAdapter(Context context, List<Operation> list)
    {
        super(context,0, list);
        operations = list;
        operationsAll = list;
        operationFilter = null;
        filterFields = new FilterFields();
        searchSeq = null;
        sortField = SORT_EXECDATE;
        sortSide = SORT_ASC;
        updateSort = true;
    }

    private static class ViewHolder
    {
        TextView textName;
        TextView textCategorie;
        TextView textAmount;
        TextView textExecDate;
    }

    public class FilterFields
    {
        //filter category
        public boolean checkAll;
        public HashMap<CharSequence, CheckboxItem> listCategoriesItems;

        //filter amount
        public float minAmount;
        public float maxAmount;
        public float startAmount;
        public float endAmount;

        //filter dates
        public String startExecDate;
        public String endExecDate;

        FilterFields()
        {
            checkAll = true;
            listCategoriesItems = new HashMap<CharSequence, CheckboxItem>();
            reset();
            startAmount = minAmount;
            endAmount = maxAmount;

            startExecDate = EditTextDate.DATE_DEFAULT;
            endExecDate = EditTextDate.DATE_DEFAULT;
        }

        void reset()
        {
            //listCategories.clear();
            minAmount = -10.0f;
            maxAmount = 10.0f;
        }
    }

    public FilterFields getFilterFields()
    {
        return filterFields;
    }

    public void updateFilterFields()
    {
        if(filterFields.startAmount == filterFields.minAmount)
            filterFields.startAmount = Float.NEGATIVE_INFINITY;

        if(filterFields.endAmount == filterFields.maxAmount)
            filterFields.endAmount = Float.POSITIVE_INFINITY;

        filterFields.reset();

        TreeSet<CharSequence> setCategories = new TreeSet<CharSequence>();
        for(Operation operation : operationsAll)
        {
            //category
            setCategories.add(operation.getCategory());

            //amount
            float amount = operation.getSignedAmount();
            if(amount > filterFields.maxAmount)
                filterFields.maxAmount = amount;
            else if(amount < filterFields.minAmount)
                filterFields.minAmount = amount;
        }

        HashMap<CharSequence, CheckboxItem> listCategoriesItemsTemp = new HashMap<CharSequence, CheckboxItem>();
        for(CharSequence category : setCategories)
        {
            CheckboxItem cbi = filterFields.listCategoriesItems.get(category);
            if(cbi == null)
                listCategoriesItemsTemp.put(category, new CheckboxItem(category.toString()));
            else
                listCategoriesItemsTemp.put(category, cbi);
        }
        filterFields.listCategoriesItems = listCategoriesItemsTemp;

        filterFields.startAmount = Math.max(filterFields.minAmount, filterFields.startAmount);
        filterFields.endAmount = Math.min(filterFields.maxAmount, filterFields.endAmount);

        getFilter().filter(searchSeq);
    }

    public void applyFilterFields()
    {
        getFilter().filter(searchSeq);
    }

    @Override
    public void remove(Operation object)
    {
        updateSort = true;
        if(operations != operationsAll)
            operations.remove(object);
        super.remove(object);
        updateFilterFields();
    }

    @Override
    public void add(Operation object)
    {
        updateSort = true;
        if(operations != operationsAll)
            operations.add(object);

        super.add(object);
    }

    @Override
    public void clear()
    {
        updateSort = true;
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
            searchSeq = constraint;
            ArrayList<Operation> filterList = new ArrayList<Operation>();
            for (Operation operation : operationsAll)
            {
                if(constraint != null)
                {
                    String name = Tools.stripAccents(operation.getName().toLowerCase());
                    String constraintStr = Tools.stripAccents(constraint.toString().toLowerCase());
                    if (constraintStr.length() > name.length())
                        continue;

                    if (!name.substring(0, constraintStr.length()).equals(constraintStr))
                        continue;
                }

                CheckboxItem cbi = filterFields.listCategoriesItems.get(operation.getCategory());
                if(cbi != null && cbi.isChecked() == false)
                    continue;

                float amount = operation.getSignedAmount();
                if(amount < filterFields.startAmount || amount > filterFields.endAmount)
                    continue;

                String execDate = operation.getExecDate();
                if(filterFields.startExecDate != EditTextDate.DATE_DEFAULT)
                {
                    if(Tools.compareDates(execDate, filterFields.startExecDate) < 0)
                        continue;
                }
                if(filterFields.endExecDate != EditTextDate.DATE_DEFAULT)
                {
                    if(Tools.compareDates(execDate, filterFields.endExecDate) > 0)
                        continue;
                }

                filterList.add(operation);
            }
            results.values = filterList;
            results.count = filterList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            updateSort = true;
            operations = (ArrayList<Operation>) results.values;
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged()
    {
        Log.i("FIELD SORT", String.valueOf(sortField));
        Log.i("SIDE SORT", String.valueOf(sortSide));

        if(updateSort)
        {
            this.setNotifyOnChange(false);
            Collections.sort(operations, new Comparator<Operation>() {
                @Override
                public int compare(Operation o1, Operation o2) {
                    if (sortField == SORT_NAME)
                        return (sortSide * o1.getName().compareTo(o2.getName()));
                    if (sortField == SORT_CATEGORY)
                        return (sortSide * o1.getCategory().compareTo(o2.getCategory()));
                    if (sortField == SORT_AMOUNT) {
                        int compareSide = o1.getSignedAmount() == o2.getSignedAmount() ? 0 :
                                o1.getSignedAmount() > o2.getSignedAmount() ? 1 : -1;
                        return sortSide * compareSide;
                    }
                    if (sortField == SORT_EXECDATE)
                        return sortSide * Tools.compareDates(o1.getExecDate(), o2.getExecDate());
                    return 0;
                }
            });
            this.setNotifyOnChange(true);
            updateSort = false;
        }
        super.notifyDataSetChanged();
    }

    public void setSortField(int field)
    {
        if(sortField == field)
            return;

        updateSort = true;
        sortField = field;
    }

    public int getSortField()
    {
        return sortField;
    }

    public void setSortAsc()
    {
        if(sortSide == SORT_ASC)
            return;

        updateSort = true;
        sortSide = SORT_ASC;
    }

    public void setSortDesc()
    {
        if(sortSide == SORT_DESC)
            return;

        updateSort = true;
        sortSide = SORT_DESC;
    }

    public ArrayList<CheckboxItem> generateCBIList()
    {
        ArrayList<CheckboxItem> result = new ArrayList<CheckboxItem>();
        for(CheckboxItem cbi : filterFields.listCategoriesItems.values())
        {
            result.add(new CheckboxItem(cbi));
        }
        return result;
    }

    public TreeSet<CharSequence> generateCategoryList()
    {
        return new TreeSet<CharSequence>(filterFields.listCategoriesItems.keySet());
    }
}

