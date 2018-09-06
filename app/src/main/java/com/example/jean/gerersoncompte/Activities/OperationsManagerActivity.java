package com.example.jean.gerersoncompte.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.jean.gerersoncompte.AdapterItem.CheckboxItem;
import com.example.jean.gerersoncompte.Adapters.CheckboxListAdapter;
import com.example.jean.gerersoncompte.Adapters.OperationAdapter;
import com.example.jean.gerersoncompte.Constants;
import com.example.jean.gerersoncompte.Database.OperationDAO;
import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.GSCItems.Operation;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;
import com.example.jean.gerersoncompte.Views.EditTextDate;
import com.example.jean.gerersoncompte.Views.GSCDialog;
import com.example.jean.gerersoncompte.Views.SeekBarRangeValues;

import java.util.ArrayList;
import java.util.TreeSet;

public class OperationsManagerActivity extends GSCActivity
{
    private final static int MENU_FILTER = 1;
    private final static int MENU_SORT = 2;

    private ListView operationsList = null;
    private SearchView operationSearcher = null;

    private Account account = null;
    private int selectedOperation = -1;
    private View selectedView = null;
    private int selectedSortField = -1;

    private class OnClickUnselect implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            unselect();
        }
    }

    private class OnCancelUnselect implements DialogInterface.OnCancelListener
    {
        @Override
        public void onCancel(DialogInterface dialog)
        {
            unselect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations_manager);

        Intent intent = getIntent();
        account = (Account)intent.getSerializableExtra(Constants.extraAccount);

        operationSearcher = (SearchView) findViewById(R.id.ope_man_sea_nom);
        operationSearcher.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
                if(adapter != null)
                    adapter.getFilter().filter(newText);

                return false;
            }
        });

        operationsList = (ListView) findViewById(R.id.ope_man_liste_operations);
        operationsList.setAdapter(new OperationAdapter(this, new ArrayList<Operation>()));
        operationsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                processActionsDialog(view, position);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initOperations();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == Constants.requestNewOperation && resultCode == RESULT_OK)
        {
            account = (Account)data.getSerializableExtra(Constants.extraAccount);
        }
        else if(requestCode == Constants.requestModifyOperation && resultCode == RESULT_OK)
        {
            account = (Account)data.getSerializableExtra(Constants.extraAccount);
        }
    }

    public void initOperations()
    {
        OperationDAO opeDAO = OperationDAO.getInstance();
        if(account == null)
            return;

        ArrayList<Operation> operations = opeDAO.selectAll(account.getId());

        OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
        if(adapter == null)
            return;

        adapter.clear();
        for(Operation operation : operations)
        {
            if(operation == null)
                continue;

            adapter.add(operation);
        }
        adapter.updateFilterFields();
        adapter.notifyDataSetChanged();
    }

    public void processActionsDialog(View view, int position)
    {
        if(view != null) {
            selectedView = view;
            selectedOperation = position;
        }
        //openContextMenu(view);
        updateBackgroundView(true);

        final CharSequence[] items = {getString(R.string.label_modifier),
                                      getString(R.string.label_supprimer)};
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
        builder.setTitle("Sélectionner une action");
        builder.setCancelable(true);
        builder.setNeutralButton("Retour", new OperationsManagerActivity.OnClickUnselect());
        builder.setOnCancelListener(new OperationsManagerActivity.OnCancelUnselect());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CharSequence option = items[which];
                if(option == getString(R.string.label_modifier))
                {
                    processModifyOperation();
                    unselect();
                }
                else if(option == getString(R.string.label_supprimer))
                {
                    processDeleteOperation();
                    unselect();
                }
            }
        });
        builder.create().show();
    }

    public void processFilterOperations(View view)
    {
        showDialog(MENU_FILTER);
    }

    public void processSortOperations(View view)
    {
        showDialog(MENU_SORT);
    }

    public void processModifyOperation()
    {
        if(selectedOperation != -1)
        {
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            Operation operation = adapter.getItem(selectedOperation);
            if(operation != null)
            {
                TreeSet<CharSequence> categoryList = adapter.generateCategoryList();

                Intent operationCreationIntent = new Intent(OperationsManagerActivity.this, OperationCreationActivity.class);
                operationCreationIntent.putExtra(Constants.extraAccount, account);
                operationCreationIntent.putExtra(Constants.extraOperation, operation);
                operationCreationIntent.putExtra(Constants.extraCategories, categoryList);
                startActivityForResult(operationCreationIntent, Constants.requestModifyOperation);

                selectedOperation = -1;
                selectedView = null;
            }
            Log.d("MODIFY", "modifying operation");
        }
    }

    public void processDeleteOperation()
    {
        Log.d("DELETE", "delete operation");
        if(selectedOperation != -1)
        {
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            Operation operation = adapter.getItem(selectedOperation);
            unselect();
            if(operation != null)
            {
                OperationDAO opeDAO = OperationDAO.getInstance();
                opeDAO.delete(operation.getId());
                adapter.remove(operation);
                adapter.notifyDataSetChanged();
            }
            Log.d("DELETE", "deleted operation");
        }
    }

    public void processCreateOperation(View view)
    {
        OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
        TreeSet<CharSequence> categoryList = adapter.generateCategoryList();

        Intent operationCreationIntent = new Intent(OperationsManagerActivity.this, OperationCreationActivity.class);
        operationCreationIntent.putExtra(Constants.extraAccount, account);
        operationCreationIntent.putExtra(Constants.extraCategories, categoryList);
        startActivityForResult(operationCreationIntent, Constants.requestNewOperation);
    }

    @Override
    public Dialog onCreateDialog(int id)
    {
        AlertDialog dialog = null;
        switch(id)
        {
            case MENU_FILTER: {
                View view = getLayoutInflater().inflate(R.layout.dialog_operation_filter, null);
                initFilter(view);
                dialog = new GSCDialog(this, AlertDialog.THEME_TRADITIONAL);
                dialog.setTitle("Filtrer par");
                dialog.setCancelable(true);
                dialog.setView(view);
                ((GSCDialog)dialog).setPositiveButton(R.string.appliquer, new OnApplyFieldsFilter());
                ((GSCDialog)dialog).setNegativeButton(R.string.annuler, null);
                break;
            }

            case MENU_SORT: {
                OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
                selectedSortField = adapter != null ? adapter.getSortField() : -1;
                String[] items = getResources().getStringArray(R.array.sort_operation_list);

                AlertDialog.Builder builder = new GSCDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
                builder.setTitle("Trier par");
                builder.setSingleChoiceItems(items, selectedSortField, new OnSortSelectListener());
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.croissant, new OnSortAscListener());
                builder.setNegativeButton(R.string.decroissant, new OnSortDescListener());
                builder.setOnCancelListener(new OnSortCancelListener());
                dialog = builder.create();

                break;
            }
        }

        if(dialog != null)
            dialog.show();

        return super.onCreateDialog(id);
    }

    private void initFilter(View view)
    {
        //Spinner spinCategory = (Spinner) view.findViewById(R.id.dia_ope_fil_spin_categorie);
        View viewAllCategory = view.findViewById(R.id.dia_ope_fil_item_all_categorie);
        CheckBox checkAllCategory = (CheckBox) viewAllCategory.findViewById(R.id.item_spin_mul_check);
        ListView listCategories = (ListView) view.findViewById(R.id.dia_ope_fil_list_categorie);

        SeekBarRangeValues seekBarAmounts = (SeekBarRangeValues) view.findViewById(R.id.dia_ope_fil_sbr_montant);
        EditTextDate startExecDate = (EditTextDate) view.findViewById(R.id.dia_ope_fil_edit_date_begin);
        EditTextDate endExecDate = (EditTextDate) view.findViewById(R.id.dia_ope_fil_edit_date_end);
        OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
        if(adapter != null)
        {
            OperationAdapter.FilterFields fields = adapter.getFilterFields();

            //CATEGORIES
            Log.i("initFilter", "checkAll saved = " + String.valueOf(fields.checkAll));
            checkAllCategory.setChecked(fields.checkAll);
            final CheckboxListAdapter cblAdapter = new CheckboxListAdapter(this, adapter.generateCBIList());
            listCategories.setAdapter(cblAdapter);
            checkAllCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    cblAdapter.setCheckedAll(isChecked);
                    cblAdapter.notifyDataSetChanged();
                }
            });
            //AMOUNTS
            seekBarAmounts.setMinVal(fields.minAmount);
            seekBarAmounts.setMaxVal(fields.maxAmount);
            seekBarAmounts.setPos(fields.startAmount, fields.endAmount);

            //DATES
            startExecDate.setDate(fields.startExecDate);
            endExecDate.setDate(fields.endExecDate);
        }
    }

    private class OnApplyFieldsFilter implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            AlertDialog alertDialog = (AlertDialog)dialog;

            View viewAllCategory = alertDialog.findViewById(R.id.dia_ope_fil_item_all_categorie);
            CheckBox checkAllCategory = (CheckBox) viewAllCategory.findViewById(R.id.item_spin_mul_check);
            ListView listCategories = (ListView) alertDialog.findViewById(R.id.dia_ope_fil_list_categorie);
            CheckboxListAdapter cblAdapter = (CheckboxListAdapter) listCategories.getAdapter();
            SeekBarRangeValues seekBarAmounts = (SeekBarRangeValues) alertDialog.findViewById(R.id.dia_ope_fil_sbr_montant);
            EditTextDate startExecDate = (EditTextDate) alertDialog.findViewById(R.id.dia_ope_fil_edit_date_begin);
            EditTextDate endExecDate = (EditTextDate) alertDialog.findViewById(R.id.dia_ope_fil_edit_date_end);

            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            if(adapter != null)
            {
                OperationAdapter.FilterFields fields = adapter.getFilterFields();
                fields.checkAll = checkAllCategory.isChecked();
                for(int position = 0; position < cblAdapter.getCount(); position++)
                {
                    CheckboxItem cbi = cblAdapter.getItem(position);
                    fields.listCategoriesItems.put(cbi.name, cbi);
                    cbi.dump();
                }

                fields.startAmount = Tools.floatTruncated(seekBarAmounts.getPosLeft(), 2);
                fields.endAmount = Tools.floatTruncated(seekBarAmounts.getPosRight(), 2);

                String strSED = startExecDate.getDate();
                String strEED = endExecDate.getDate();
                fields.startExecDate = EditTextDate.dateExists(strSED) ? strSED : EditTextDate.DATE_DEFAULT;
                fields.endExecDate = EditTextDate.dateExists(strEED) ? strEED : EditTextDate.DATE_DEFAULT;

                adapter.applyFilterFields();
            }
        }
    }

    private class OnSortSelectListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            String[] items = getResources().getStringArray(R.array.sort_operation_list);
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            selectedSortField = which;
        }
    }

    private class OnSortAscListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            if(adapter != null)
            {
                if(selectedSortField != -1)
                    adapter.setSortField(selectedSortField);
                adapter.setSortAsc();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class OnSortDescListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            if(adapter != null)
            {
                if(selectedSortField != -1)
                    adapter.setSortField(selectedSortField);
                adapter.setSortDesc();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class OnSortCancelListener implements DialogInterface.OnCancelListener
    {
        @Override
        public void onCancel(DialogInterface dialog)
        {
            selectedSortField = -1;
        }
    }

    private void unselect()
    {
        updateBackgroundView(false);
        selectedView = null;
        selectedOperation = -1;
    }

    public void updateBackgroundView(boolean isHighLight)
    {
        if(selectedView != null && selectedOperation != -1)
        {
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            Operation operation = adapter.getItem(selectedOperation);
            if(operation != null)
            {
                int color;
                if(operation.isGain() && isHighLight)
                    color = ContextCompat.getColor(this, R.color.colorGreenHighLight);
                else if(operation.isGain() && !isHighLight)
                    color = ContextCompat.getColor(this, R.color.colorGreenLight);
                else if(isHighLight)
                    color = ContextCompat.getColor(this, R.color.colorRedHighLight);
                else
                    color = ContextCompat.getColor(this, R.color.colorRedLight);
                selectedView.setBackgroundColor(color);
            }
        }
    }
}
