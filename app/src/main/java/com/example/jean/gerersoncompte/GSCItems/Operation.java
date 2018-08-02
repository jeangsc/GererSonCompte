package com.example.jean.gerersoncompte.GSCItems;

import com.example.jean.gerersoncompte.Database.OperationDAO;
import com.example.jean.gerersoncompte.Database.OperationHistoryDAO;

import java.io.Serializable;

public class Operation implements Serializable
{
    private long id;
    private long accId;
    private String name;
    private String category;
    private float amount;
    private boolean isGain;
    private String execDate;
    private transient boolean executed;

    public Operation()
    {
        this.id = 0;
        this.accId = 0;
        this.name = "";
        this.category = "";
        this.amount = 0.0f;
        this.isGain = false;
        this.execDate = "01/01/2018";
        this.executed = false;
    }

    public Operation(String name, long accId)
    {
        OperationDAO opeDAO = OperationDAO.getInstance();
        this.id = opeDAO.getSeqID();
        this.accId = accId;
        this.name = name;
        this.category = "";
        this.amount = 0.0f;
        this.isGain = false;
        this.execDate = "01/01/2018";
        this.executed = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccId() {
        return accId;
    }

    public void setAccId(long accId) {
        this.accId = accId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isGain() {
        return isGain;
    }

    public void setGain(boolean gain) {
        isGain = gain;
    }

    public String getExecDate() {
        return execDate;
    }

    public void setExecDate(String execDate) {
        this.execDate = execDate;
    }


    public boolean isExecuted() {
        return this.executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public float getSignedAmount()
    {
        return isGain ? amount : -amount;
    }

    public void archive()
    {
        OperationDAO opeDAO = OperationDAO.getInstance();
        OperationHistoryDAO opeHistDAO = OperationHistoryDAO.getInstance();

        opeDAO.delete(this.id);
        opeHistDAO.insert(this);
    }
}
