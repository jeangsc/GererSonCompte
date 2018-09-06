package com.example.jean.gerersoncompte.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean.gerersoncompte.ActivityManager;
import com.example.jean.gerersoncompte.Constants;
import com.example.jean.gerersoncompte.Database.AccountDAO;
import com.example.jean.gerersoncompte.Database.OperationDAO;
import com.example.jean.gerersoncompte.Database.OperationHistoryDAO;
import com.example.jean.gerersoncompte.GSCItems.Account;
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
import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends GSCActivity
{
    private TextView textAccount = null;
    private TextView textBalance = null;
    private TextView textToCome = null;
    private TextView textNextBalance = null;

    private Account account = null;
    private Timer timerMidnight = null;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loadGeneralDatas();
        initDB();

        updateDate();
        textAccount = (TextView) findViewById(R.id.men_value_nom);
        textBalance = (TextView) findViewById(R.id.men_value_solde);
        textToCome  = (TextView) findViewById(R.id.men_value_avenir);
        textNextBalance = (TextView) findViewById(R.id.men_value_prochainsolde);

        copyDBFile();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        initTimer();
        //si le chargement de compte a réussi
        if(loadAccount())
        {
            updateView();
        }
        else //sinon, on en créé un
        {
            Intent accountCreationIntent = new Intent(MenuActivity.this, AccountCreationActivity.class);
            accountCreationIntent.putExtra(Constants.extraIsCurrent, true);
            startActivityForResult(accountCreationIntent, Constants.requestCreateAccount);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == Constants.requestCreateAccount && resultCode != RESULT_OK)
            finish();
    }

    public void updateView()
    {
        String accountName = (account != null) ? account.getName() : "";
        float  balance     = (account != null) ? account.getBalance() : 0.0f;
        float  toCome      = (account != null) ? account.getToCome() : 0.0f;
        float  nextBalance = balance + toCome;
        String currency    = (account != null) ? account.getCurrency() : "EUR";
        int    colorToCome = Tools.getColorFromSign(toCome, R.color.colorGreen, R.color.colorRed, R.color.colorBlack, this);

        textAccount.setText(accountName);
        textBalance.setText(Tools.getFormattedAmount(balance, currency));
        textToCome.setText(Tools.getFormattedAmount(toCome, currency, true));
        textToCome.setTextColor(colorToCome);
        textNextBalance.setText(Tools.getFormattedAmount(nextBalance, currency));
    }

    private void initTimer()
    {
        if(timerMidnight == null)
        {
            TimerTask midnightTask = new TimerTask()
            {
                @Override
                public void run()
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            processTimerMidnight();
                        }
                    });
                }
            };
            timerMidnight = new Timer();
            timerMidnight.schedule(midnightTask, 1000, 2000);
        }
    }


    private void processTimerMidnight()
    {
        ActivityManager am = ActivityManager.getInstance();
        if(am.appVisible())
        {
            CharSequence cs = "Midnight proceed";
            Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_LONG).show();
        }

        CountDownTimer countDown = new CountDownTimer(1000, 1000)
        {
            @Override
            public void onFinish() {
                ActivityManager am = ActivityManager.getInstance();
                if(am.appVisible())
                {
                    CharSequence cs = "Midnight done";
                    Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_LONG).show();
                }
                updateDate();
            }

            @Override
            public void onTick(long millisUntilFinished)
            {

            }
        };
        countDown.start();
        //CharSequence cs = "Midnight proceed";
        //Toast.makeText(getApplicationContext(), cs, Toast.LENGTH_LONG).show();

    }

    public void processAccountDetails(View v)
    {
        Intent accountDetailsIntent = new Intent(MenuActivity.this, AccountDetailsActivity.class);
        accountDetailsIntent.putExtra(Constants.extraAccount, account);
        startActivity(accountDetailsIntent);
    }

    public void processButtonOperations(View v)
    {
        Intent operationsManagerIntent = new Intent(MenuActivity.this, OperationsManagerActivity.class);
        operationsManagerIntent.putExtra(Constants.extraAccount, account);
        startActivity(operationsManagerIntent);
    }

    public void processButtonSchedules(View v)
    {
        Intent schedulesManagerIntent = new Intent(MenuActivity.this, SchedulesManagerActivity.class);
        schedulesManagerIntent.putExtra(Constants.extraAccount, account);
        startActivity(schedulesManagerIntent);
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
}
