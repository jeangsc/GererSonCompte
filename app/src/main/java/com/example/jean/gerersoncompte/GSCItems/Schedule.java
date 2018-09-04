package com.example.jean.gerersoncompte.GSCItems;

import com.example.jean.gerersoncompte.Constants;
import com.example.jean.gerersoncompte.Tools;

import java.util.HashMap;

/**
 * Created by V17 on 04/09/2018.
 */

public class Schedule
{
    private HashMap<Long, Operation> operations;

    private int unitTime;
    private int amountUnit;

    private long id;
    private long accId;
    private String dateBegin;
    private String dateEnd;
    private String opeName;
    private String opeCategory;
    private float opeAmount;
    private boolean opeIsGain;

    public Schedule(long accId)
    {
        operations = null;
        unitTime = Constants.UNIT_TIME_MONTH;
        amountUnit = 1;
        this.accId = accId;
        dateBegin = Tools.getDate();
        dateEnd = dateBegin;
        opeName = "Opération";
        opeCategory = "Catégorie";
        opeAmount = 1.00f;
        opeIsGain = true;
    }

    public void generateOperations()
    {
        operations = new HashMap<Long, Operation>();
        String opeExecDate = dateBegin;
        String dateLimit = Tools.addDays(dateEnd, 1);
        while(!Tools.expiredDate(opeExecDate, dateLimit))
        {
            Operation operation = new Operation(opeName, accId);
            operation.setCategory(opeCategory);
            operation.setAmount(opeAmount);
            operation.setGain(opeIsGain);
            operation.setExecDate(opeExecDate);
            operation.setSchedule(this);

            operations.put(operation.getId(), operation);
            opeExecDate = Tools.addUnit(opeExecDate, amountUnit, unitTime);
        }
    }

    public int getUnitTime() {
        return this.unitTime;
    }

    public void setUnitTime(int unitTime) {
        this.unitTime = unitTime;
    }

    public int getAmountUnit() {
        return this.amountUnit;
    }

    public void setAmountUnit(int amountUnit) {
        this.amountUnit = amountUnit;
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccId() {
        return this.accId;
    }

    public void setAccId(long accId) {
        this.accId = accId;
    }

    public String getDateBegin() {
        return this.dateBegin;
    }

    public void setDateBegin(String dateBegin) {
        this.dateBegin = dateBegin;
    }

    public String getDateEnd() {
        return this.dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getOpeName() {
        return this.opeName;
    }

    public void setOpeName(String opeName) {
        this.opeName = opeName;
    }

    public String getOpeCategory() {
        return this.opeCategory;
    }

    public void setOpeCategory(String opeCategory) {
        this.opeCategory = opeCategory;
    }

    public float getOpeAmount() {
        return this.opeAmount;
    }

    public void setOpeAmount(float opeAmount) {
        this.opeAmount = opeAmount;
    }

    public boolean isOpeIsGain() {
        return this.opeIsGain;
    }

    public void setOpeIsGain(boolean opeIsGain) {
        this.opeIsGain = opeIsGain;
    }


}
