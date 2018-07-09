package com.example.jean.gerersoncompte;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class OperationsManagerActivity extends AppCompatActivity
{
    private ListView operationsList = null;
    private SearchView operationSearcher = null;

    private final static String extraAccount = "EXTRA_ACCOUNT";
    private final static int requestNewOperation = 1;

    private Account account = null;
    private int selectedOperation = -1;
    private View selectedView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations_manager);

        Intent intent = getIntent();
        account = (Account)intent.getSerializableExtra(extraAccount);

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
                processMenuOperation(view, position);
            }
        });
        registerForContextMenu(operationsList);
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
        if(requestCode == requestNewOperation && resultCode == RESULT_OK)
        {
            account = (Account)data.getSerializableExtra(extraAccount);
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
        adapter.notifyDataSetChanged();
    }

    public void processMenuOperation(View view, int position)
    {
        if(view != null) {
            selectedView = view;
            selectedOperation = position;
        }
        openContextMenu(view);
    }

    public void processFilterOperations(View view)
    {
        showDialog(1);
    }

    public void processSortOperations(View view)
    {
        showDialog(2);
    }

    public void processSortByName(View view)
    {
        Log.i("Sort", "Name");
    }

    public void processSortByCategory(View view)
    {
        Log.i("Sort", "Category");
    }

    public void processSortByAmount(View view)
    {
        Log.i("Sort", "Amount");
    }

    public void processSortByExecDate(View view)
    {
        Log.i("Sort", "ExecDate");
    }

    public void processModifyOperation()
    {

    }

    public void processDeleteOperation()
    {
        Log.d("DELETE", "delete operation");
        if(selectedOperation != -1)
        {
            OperationAdapter adapter = (OperationAdapter) operationsList.getAdapter();
            Operation operation = adapter.getItem(selectedOperation);
            if(operation != null)
            {
                OperationDAO opeDAO = OperationDAO.getInstance();
                opeDAO.delete(operation.getId());
                adapter.remove(operation);
                adapter.notifyDataSetChanged();
                selectedOperation = -1;
                selectedView = null;
            }
            Log.d("DELETE", "deleted operation");
        }
    }

    public void processCreateOperation(View view)
    {
        Intent operationCreationIntent = new Intent(OperationsManagerActivity.this, OperationCreationActivity.class);
        operationCreationIntent.putExtra(extraAccount, account);
        startActivityForResult(operationCreationIntent, requestNewOperation);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(selectedView != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.operation_options, menu);
            updateBackgroundView(true);
        }
    }

    @Override
    public Dialog onCreateDialog(int id)
    {
        Dialog menu = null;
        switch(id)
        {
            case 1:
                menu = new Dialog(this);
                menu.setTitle("Filtrer par");
                menu.setContentView(R.layout.dialog_operation_filter);
                break;

            case 2:
                menu = new Dialog(this);
                menu.setTitle("Trier par");
                menu.setContentView(R.layout.dialog_operation_sort);
                break;
        }
        return menu;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Log.d("SELECT ITEM", "on item selected");
        Log.d("SELECT ITEM", "id selected: " + String.valueOf(item.getItemId()));
        switch (item.getItemId()) {

            case R.id.ope_opt_modifier: {
                Log.d("SELECT ITEM", "modif");
                processModifyOperation();
                break;
            }
            case R.id.ope_opt_supprimer: {
                Log.d("SELECT ITEM", "delete");
                processDeleteOperation();
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu)
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }
}
