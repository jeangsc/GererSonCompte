package com.example.jean.gerersoncompte.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.jean.gerersoncompte.GSCItems.Operation;
import com.example.jean.gerersoncompte.Tools;

import java.util.ArrayList;

/**
 * Created by V17 on 07/05/2018.
 */

public class OperationHistoryDAO extends BaseDAO
{
    public static final String TABLE_NAME = "OPERATIONS_HISTORY";
    protected static final String TABLE_KEY  = "ID";

    private static OperationHistoryDAO instance = null;
    public static OperationHistoryDAO getInstance()
    {
        return instance;
    }

    private OperationHistoryDAO(Context context)
    {
        super(context);
    }

    public static void init(Context context)
    {
        if(instance == null)
            instance = new OperationHistoryDAO(context);
    }

    public boolean insert(Operation operation)
    {
        if(operation == null)
            return false;

        open();
        if(mDb != null)
        {
            ContentValues values = new ContentValues();
            values.put(TABLE_KEY, operation.getId());
            values.put("ACCID", operation.getAccId());
            values.put("NAME", operation.getName());
            values.put("CATEGORIE", operation.getCategory());
            values.put("AMOUNT", operation.getAmount());
            values.put("ISGAIN", (operation.isGain() ? 1 : 0));
            values.put("EXECDATE", operation.getExecDate());
            values.put("SCHEDULEID", operation.getScheduleId());
            mDb.insert(TABLE_NAME, null, values);
            close();
            return true;
        }
        return false;
    }

    public void update(Operation operation)
    {
        if(operation == null)
            return;

        open();
        if(mDb != null) {
            ContentValues values = new ContentValues();
            values.put("NAME", operation.getName());
            values.put("CATEGORIE", operation.getCategory());
            values.put("AMOUNT", operation.getAmount());
            values.put("ISGAIN", (operation.isGain() ? 1 : 0));
            values.put("EXECDATE", operation.getExecDate());
            String whereClause = TABLE_KEY + " = ?";
            String[] whereArgs = new String[]{String.valueOf(operation.getId())};
            mDb.update(TABLE_NAME, values, whereClause, whereArgs);
            close();
        }
    }

    public void delete(long id)
    {
        open();
        if(mDb != null)
        {
            String whereClause = TABLE_KEY + " = ?";
            String[] whereArgs = new String[]{ String.valueOf(id)};
            mDb.delete(TABLE_NAME, whereClause, whereArgs);
            close();
        }
    }

    public Operation select(long id)
    {
        Operation operation = null;

        open();
        if(mDb == null)
            return null;

        String query = "SELECT " +
                TABLE_KEY + ", " +
                "ACCID, " +
                "NAME, " +
                "CATEGORIE, " +
                "AMOUNT, " +
                "ISGAIN, " +
                "EXECDATE, " +
                "SCHEDULEID " +
                "FROM " + TABLE_NAME + " " +
                "WHERE " + TABLE_KEY + " = ?";

        String[] whereArgs = new String[]{ String.valueOf(id)};
        Cursor cursor = mDb.rawQuery(query, whereArgs);


        if(cursor.moveToNext())
            operation = cursorToOperation(cursor);

        close();
        return operation;
    }

    public ArrayList<Operation> selectAll(long accid)
    {
        ArrayList<Operation> opeList = new ArrayList<Operation>();
        open();

        if(mDb == null)
            return opeList;

        String query = "SELECT " +
                TABLE_KEY + ", " +
                "ACCID, " +
                "NAME, " +
                "CATEGORIE, " +
                "AMOUNT, " +
                "ISGAIN, " +
                "EXECDATE, " +
                "SCHEDULEID " +
                "FROM " + TABLE_NAME + " " +
                "WHERE ACCID = ? " +
                "ORDER BY " + TABLE_KEY + " ASC";

        String[] whereArgs = new String[]{ String.valueOf(accid)};
        Cursor cursor = mDb.rawQuery(query, whereArgs);

        while(cursor.moveToNext())
        {
            Operation operation = cursorToOperation(cursor);
            if(operation != null)
                opeList.add(operation);
        }

        close();
        return opeList;
    }

    public Operation cursorToOperation(Cursor cursor)
    {
        Operation operation = new Operation();
        operation.setId(Tools.getLongFromColumn(cursor, "ID", 0L));
        operation.setAccId(Tools.getLongFromColumn(cursor, "ACCID", 0L));
        operation.setName(Tools.getStringFromColumn(cursor, "NAME", ""));
        operation.setCategory(Tools.getStringFromColumn(cursor, "CATEGORIE", ""));
        operation.setAmount(Tools.getFloatFromColumn(cursor, "AMOUNT", 0.0f));
        operation.setGain(Tools.getBooleanFromColumn(cursor, "ISGAIN", false));
        operation.setExecDate(Tools.getStringFromColumn(cursor, "EXECDATE", "01/01/2018"));
        operation.setScheduleId(Tools.getLongFromColumn(cursor, "SCHEDULEID", 0L));
        return operation;
    }
}
