package com.example.jean.gerersoncompte.Activities;

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

import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.Adapters.AccountAdapter;
import com.example.jean.gerersoncompte.Database.AccountDAO;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;

import java.util.ArrayList;

public class AccountsManagerActivity extends AppCompatActivity
{
    private final byte deleteOption = 2;
    private ListView accountsList = null;
    private int selectedAccount = -1;
    private View selectedView = null;
    private Account currentAccount = null;
    private boolean keepSelection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_manager);

        accountsList = (ListView) findViewById(R.id.acc_man_liste_comptes);
        accountsList.setAdapter(new AccountAdapter(this, new ArrayList<Account>()));
        accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                processMenuAccount(view, position);
            }
        });
        registerForContextMenu(accountsList);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initAccounts();
    }

    public void initAccounts()
    {
        GeneralDatas gd = GeneralDatas.getInstance();
        AccountDAO accDAO = AccountDAO.getInstance();
        AccountAdapter adapter = (AccountAdapter) accountsList.getAdapter();

        if(adapter == null)
            return;

        adapter.clear();
        ArrayList<Account> accList = accDAO.selectAll();
        for(Account account : accList)
        {
            if(account != null)
            {
                if (account.getId() == gd.accCurId)
                    currentAccount = account;

                adapter.add(account);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void processMenuAccount(View view, int position)
    {
        if(view != null) {
            selectedView = view;
            selectedAccount = position;
        }
        openContextMenu(view);
    }

    public void processDefineCurrentAccount()
    {
        if(selectedAccount != -1)
        {
            AccountAdapter adapter = (AccountAdapter) accountsList.getAdapter();
            Account acc = adapter.getItem(selectedAccount);
            if(acc == null)
                return;

            setCurrentAccount(acc);
            adapter.notifyDataSetChanged();
        }
    }

    public void setCurrentAccount(Account account)
    {
        GeneralDatas gd = GeneralDatas.getInstance();
        currentAccount = account;
        gd.accCurId = account.getId();
        gd.save();
    }

    public void processModifyAccount()
    {

    }

    public void processDeleteAccount()
    {
        Log.d("DELETE", "delete account");
        Log.d("DELETE", "selectedAccount = " + String.valueOf(selectedAccount));
        if(selectedAccount != -1)
        {
            AccountAdapter adapter = (AccountAdapter) accountsList.getAdapter();
            Account account = adapter.getItem(selectedAccount);
            if(account != currentAccount && account != null)
            {
                AccountDAO accDao = AccountDAO.getInstance();
                accDao.delete(account.getId());
                adapter.remove(account);
                adapter.notifyDataSetChanged();
                selectedView = null;
                selectedAccount = -1;
            }
            Log.d("DELETE", "deleted account");
        }
    }

    public void processCreateAccount(View view)
    {
        Intent accountCreationIntent = new Intent(AccountsManagerActivity.this, AccountCreationActivity.class);
        startActivity(accountCreationIntent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d("ContextMenu", "OnCreateContextMenu");
        if(v.getId() == R.id.acc_man_liste_comptes && selectedView != null)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.account_options, menu);
            menu.getItem(deleteOption).getSubMenu().setHeaderTitle("Êtes-vous sûr(e)?");

            selectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorHighLight));
        }
        /*else if(v.getId() == R.id.acc_opt_supprimer)
        {
            Log.i("Clicked supprimer", "Clicked");
        }*/
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Log.d("SELECT ITEM", "on item selected");
        Log.d("SELECT ITEM", "id selected: " + String.valueOf(item.getItemId()));
        switch (item.getItemId()) {

            case R.id.acc_opt_compte_courant: {
                Log.d("SELECT ITEM", "define");
                processDefineCurrentAccount();
                break;
            }

            case R.id.acc_opt_modifier: {
                Log.d("SELECT ITEM", "modif");
                processModifyAccount();
                break;
            }
            case R.id.acc_opt_supprimer: {
                Log.d("SELECT ITEM", "delete");
                keepSelection = true;
                break;
            }
            case R.id.acc_opt_supprimer_oui: {
                Log.d("SELECT ITEM", "delete oui");
                processDeleteAccount();
                break;
            }
            case R.id.acc_opt_supprimer_non: {
                Log.d("SELECT ITEM", "delete non");
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu)
    {
        super.onContextMenuClosed(menu);
        Log.d("ContextMenu", "onContextMenuClosed");
        if(!keepSelection)
        {
            if(selectedView != null)
                selectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
            selectedView = null;
            selectedAccount = -1;
        }
        keepSelection = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }
}
