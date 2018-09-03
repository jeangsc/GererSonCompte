package com.example.jean.gerersoncompte.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.Tools;

import java.util.ArrayList;

/**
 * Created by V17 on 07/05/2018.
 */

public class AccountDAO extends BaseDAO
{
    public static final String TABLE_NAME = "ACCOUNTS";
    protected static final String TABLE_KEY  = "ID";

    protected long seqID;

    private static AccountDAO instance = null;
    public static AccountDAO getInstance()
    {
        return instance;
    }
    private AccountDAO(Context context)
    {
        super(context);
        this.seqID = getSeqIDFromDB();
    }

    public static void init(Context context)
    {
        if(instance == null)
            instance = new AccountDAO(context);
    }

    public boolean insert(Account account)
    {
        if(account == null)
            return false;

        open();
        if(mDb != null)
        {
            ContentValues values = new ContentValues();
            values.put("ID", account.getId());
            values.put("NAME", account.getName());
            values.put("BALANCE", account.getBalance());
            values.put("TOCOME", account.getToCome());
            values.put("CURRENCY", account.getCurrency());
            mDb.insert(TABLE_NAME, null, values);
            close();
            return true;
        }
        return false;
    }

    public boolean update(Account account)
    {
        if(account == null)
            return false;

        open();
        if(mDb != null) {
            ContentValues values = new ContentValues();
            values.put("NAME", account.getName());
            values.put("BALANCE", account.getBalance());
            values.put("TOCOME", account.getToCome());
            values.put("CURRENCY", account.getCurrency());
            String whereClause = TABLE_KEY + " = ?";
            String[] whereArgs = new String[]{String.valueOf(account.getId())};
            mDb.update(TABLE_NAME, values, whereClause, whereArgs);
            close();
            return true;
        }
        return false;
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

    public Account select(long id)
    {
        Account account = null;

        open();
        if(mDb == null)
            return null;

        String query = "SELECT " +
                "ID, " +
                "NAME, " +
                "BALANCE, " +
                "TOCOME, " +
                "CURRENCY " +
                "FROM " + TABLE_NAME + " " +
                "WHERE " + TABLE_KEY + " = ?";

        String[] whereArgs = new String[]{ String.valueOf(id)};
        Cursor cursor = mDb.rawQuery(query, whereArgs);


        if(cursor.moveToNext())
            account = cursorToAccount(cursor);

        close();
        return account;
    }

    public ArrayList<Account> selectAll()
    {
        ArrayList<Account> accList = new ArrayList<Account>();
        open();

        if(mDb == null)
            return accList;

        String query = "SELECT " +
                "ID, " +
                "NAME, " +
                "BALANCE, " +
                "TOCOME, " +
                "CURRENCY " +
                "FROM " + TABLE_NAME + " " +
                "ORDER BY " + TABLE_KEY + " ASC";

        Cursor cursor = mDb.rawQuery(query, null);

        while(cursor.moveToNext())
        {
            Account account = cursorToAccount(cursor);
            if(account != null)
                accList.add(account);
        }

        close();
        return accList;
    }

    public long getSeqIDFromDB()
    {
        long id = 1;
        open();

        if(mDb == null)
            return id;

        String query = "SELECT MAX(" + TABLE_KEY + ") " +
                "FROM " + TABLE_NAME;

        Cursor cursor = mDb.rawQuery(query, null);
        if(cursor.moveToNext())
            id = cursor.getLong(0) + 1;

        close();
        return id;
    }

    public long getSeqID()
    {
        return seqID++;
    }

    public Account cursorToAccount(Cursor cursor)
    {
        Account account = new Account();
        account.setId(Tools.getLongFromColumn(cursor, "ID", 0L));
        account.setName(Tools.getStringFromColumn(cursor, "NAME", ""));
        account.setBalance(Tools.getFloatFromColumn(cursor, "BALANCE", 0.0f));
        account.setToCome(Tools.getFloatFromColumn(cursor, "TOCOME", 0.0f));
        account.setCurrency(Tools.getStringFromColumn(cursor, "CURRENCY", "EUR"));
        return account;
    }
}
