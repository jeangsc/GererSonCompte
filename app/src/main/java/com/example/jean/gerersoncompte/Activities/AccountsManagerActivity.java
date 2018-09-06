package com.example.jean.gerersoncompte.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jean.gerersoncompte.Adapters.AccountAdapter;
import com.example.jean.gerersoncompte.Constants;
import com.example.jean.gerersoncompte.Database.AccountDAO;
import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.R;

import java.util.ArrayList;

public class AccountsManagerActivity extends GSCActivity
{
    private ListView accountsList = null;
    private int selectedAccount = -1;
    private View selectedView = null;
    private Account currentAccount = null;

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
        setContentView(R.layout.activity_accounts_manager);

        accountsList = (ListView) findViewById(R.id.acc_man_liste_comptes);
        accountsList.setAdapter(new AccountAdapter(this, new ArrayList<Account>()));
        accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
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

    public void processActionsDialog(View view, int position)
    {
        if(view != null)
        {
            selectedView = view;
            selectedAccount = position;
        }
        selectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorHighLight));

        final CharSequence[] items = {getString(R.string.label_compte_courant),
                                getString(R.string.label_modifier),
                                getString(R.string.label_supprimer)};
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
        builder.setTitle("Sélectionner une action");
        builder.setCancelable(true);
        builder.setNeutralButton("Retour", new OnClickUnselect());
        builder.setOnCancelListener(new OnCancelUnselect());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CharSequence option = items[which];
                if(option == getString(R.string.label_compte_courant))
                {
                    processDefineCurrentAccount();
                    unselect();
                }
                else if(option == getString(R.string.label_modifier))
                {
                    processModifyAccount();
                    unselect();
                }
                else if(option == getString(R.string.label_supprimer))
                {
                    deleteValidation();
                }
            }
        });
        builder.create().show();
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
        if(selectedAccount != -1)
        {
            AccountAdapter adapter = (AccountAdapter) accountsList.getAdapter();
            Account account = adapter.getItem(selectedAccount);

            if (account != null)
            {
                Intent accountCreationIntent = new Intent(AccountsManagerActivity.this, AccountCreationActivity.class);
                accountCreationIntent.putExtra(Constants.extraAccount, account);
                accountCreationIntent.putExtra(Constants.extraIsCurrent, (account == currentAccount) );
                startActivity(accountCreationIntent);
                selectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
                selectedView = null;
                selectedAccount = -1;
            }
        }
    }

    public void processDeleteAccount()
    {
        Log.d("DELETE", "delete account");
        Log.d("DELETE", "selectedAccount = " + String.valueOf(selectedAccount));
        if(selectedAccount != -1)
        {
            AccountAdapter adapter = (AccountAdapter) accountsList.getAdapter();
            Account account = adapter.getItem(selectedAccount);
            unselect();
            if(account != currentAccount && account != null)
            {
                AccountDAO accDao = AccountDAO.getInstance();
                accDao.delete(account.getId());
                adapter.remove(account);
                adapter.notifyDataSetChanged();
            }
            Log.d("DELETE", "deleted account");
        }
    }

    public void processCreateAccount(View view)
    {
        Intent accountCreationIntent = new Intent(AccountsManagerActivity.this, AccountCreationActivity.class);
        startActivity(accountCreationIntent);
    }

    private void deleteValidation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
        builder.setTitle("Etes-vous sûr?");
        builder.setCancelable(true);
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                processDeleteAccount();
            }
        });
        builder.setNegativeButton("Non", new OnClickUnselect());
        builder.setOnCancelListener(new OnCancelUnselect());
        builder.create().show();
    }

    private void unselect()
    {
        if(selectedView != null)
            selectedView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparent));
        selectedView = null;
        selectedAccount = -1;
    }
}
