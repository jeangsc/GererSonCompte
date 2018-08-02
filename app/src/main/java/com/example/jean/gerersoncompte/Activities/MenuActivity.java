package com.example.jean.gerersoncompte.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.Database.AccountDAO;
import com.example.jean.gerersoncompte.Database.OperationDAO;
import com.example.jean.gerersoncompte.Database.OperationHistoryDAO;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity
{
    private TextView m_textAccount = null;
    private TextView m_textBalance = null;

    private final static String extraIsCurrent = "EXTRA_IS_CURRENT";
    private final static String extraAccount = "EXTRA_ACCOUNT";
    private final static int requestCreateAccount = 1;

    private Account account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loadGeneralDatas();
        initDB();

        updateDate();
        m_textAccount = (TextView) findViewById(R.id.men_value_nom);
        m_textBalance = (TextView) findViewById(R.id.men_value_solde);

        copyDBFile();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //si le chargement de compte a réussi
        if(loadAccount())
        {
            updateView();
        }
        else //sinon, on en créé un
        {
            Intent accountCreationIntent = new Intent(MenuActivity.this, AccountCreationActivity.class);
            accountCreationIntent.putExtra(extraIsCurrent, true);
            startActivityForResult(accountCreationIntent, requestCreateAccount);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == requestCreateAccount && resultCode != RESULT_OK)
            finish();
    }

    public void updateView()
    {
        String accountName = (account != null) ? account.getName() : "";
        float  balance     = (account != null) ? account.getBalance() : 0.0f;

        m_textAccount.setText(accountName);
        m_textBalance.setText(Tools.getFormattedAmount(balance, "€"));
    }

    public void processButtonCompte(View v)
    {

    }

    public void processButtonOperations(View v)
    {
        Intent operationsManagerIntent = new Intent(MenuActivity.this, OperationsManagerActivity.class);
        operationsManagerIntent.putExtra(extraAccount, account);
        startActivity(operationsManagerIntent);
    }

    public void processButtonComptes(View v)
    {
        Intent accountsManagerIntent = new Intent(MenuActivity.this, AccountsManagerActivity.class);
        startActivity(accountsManagerIntent);
    }

    public void loadGeneralDatas()
    {
        GeneralDatas gd = GeneralDatas.getInstance();
        if(!gd.isLoaded)
        {
            gd.setContext(this);
            gd.load();
        }
    }

    public void updateDate()
    {
        GeneralDatas gd = GeneralDatas.getInstance();
        String newDate = Tools.getDate();

        if(gd.lastDate.isEmpty() || !newDate.equals(gd.lastDate))
        {
            gd.lastDate = newDate;
            updateAccounts();
        }
        //Log.i("Date", date);
    }

    public void updateAccounts()
    {
        AccountDAO accDAO = AccountDAO.getInstance();
        ArrayList<Account> accList = accDAO.selectAll();
        for(Account account : accList)
        {
            if(account != null)
                account.updateOperations();
        }
    }

    public boolean loadAccount()
    {
        GeneralDatas gd = GeneralDatas.getInstance();
        AccountDAO accDAO = AccountDAO.getInstance();
        account = accDAO.select(gd.accCurId);
        boolean succeed = account != null;

        return succeed;
    }

    private void initDB()
    {
        AccountDAO.init(this);
        OperationDAO.init(this);
        OperationHistoryDAO.init(this);
    }

    private void copyDBFile()
    {
        File fileInt = getDatabasePath("database.db");
        File dirExt = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + getPackageName() + "/db");
        File fileExt = new File(dirExt.getAbsolutePath() + "/database.db");

        Log.i("Intern path", fileInt.getAbsolutePath());
        Log.i("Extern path", fileExt.getAbsolutePath());
        dirExt.mkdirs();

        try
        {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))
            {
                Log.i("peripherique", "oui");
                fileExt.createNewFile();
                FileChannel src = new FileInputStream(fileInt).getChannel();
                FileChannel dst = new FileOutputStream(fileExt).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            else
            {
                Log.i("peripherique", "non");
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }
}
