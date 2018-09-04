package com.example.jean.gerersoncompte.GSCItems;

import android.util.Log;

import com.example.jean.gerersoncompte.Database.AccountDAO;
import com.example.jean.gerersoncompte.Database.OperationDAO;
import com.example.jean.gerersoncompte.Database.OperationHistoryDAO;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.Tools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by V17 on 14/03/2018.
 */

public class Account implements Serializable
{
    private long id;
    private String name;
    private float balance;
    private float toCome;
    private String currency;

    public Account()
    {
        this.id = 0;
        this.name = "";
        this.balance = 0.0f;
        this.toCome = 0.0f;
        this.currency = "EUR";
    }

    public Account(String name)
    {
        AccountDAO accDAO = AccountDAO.getInstance();
        this.id = accDAO.getSeqID();
        this.name = name;
        this.balance = 0.0f;
        this.toCome = 0.0f;
        this.currency = "EUR";
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public float getBalance()
    {
        return this.balance;
    }

    public void setBalance(float balance)
    {
        this.balance = balance;
    }

    public float getToCome() {
        return this.toCome;
    }

    public void setToCome(float toCome) {
        this.toCome = toCome;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void updateOperations()
    {
        GeneralDatas gd = GeneralDatas.getInstance();
        OperationDAO opeDAO = OperationDAO.getInstance();
        OperationHistoryDAO opeHistDAO = OperationHistoryDAO.getInstance();
        AccountDAO accDAO = AccountDAO.getInstance();

        toCome = 0.0f;
        String todayDate = gd.lastDate;
        String tomorrowDate = Tools.addDays(todayDate, 1);

        ArrayList<Operation> opeList = opeDAO.selectAll(this.id);
        for(Operation operation : opeList)
        {
            updateOperation(operation, todayDate, tomorrowDate);
        }
        accDAO.update(this);
    }

    public void updateOperation(Operation operation, String todayDate, String tomorrowDate)
    {
        if(operation == null)
            return;

        Log.i("Tomorrow", tomorrowDate);

        String execDate = operation.getExecDate();
        if(operation.isExecuted())
        {
            operation.archive();
        }
        else if(Tools.expiredDate(execDate, todayDate))
        {
            balance += operation.getSignedAmount();
            operation.setExecuted(true);
            operation.archive();
        }
        else if(Tools.expiredDate(execDate, tomorrowDate))
        {
            Log.i("Tomorrow", "validated");
            toCome += operation.getSignedAmount();
        }
    }

    public void update()
    {
        AccountDAO accDAO = AccountDAO.getInstance();
        accDAO.update(this);
    }
}
