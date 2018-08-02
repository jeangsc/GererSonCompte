package com.example.jean.gerersoncompte.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by V17 on 07/05/2018.
 */

public abstract class BaseDAO extends SQLiteOpenHelper
{
    protected static final String dbname = "database.db";
    protected static final int version = 1;
    protected SQLiteDatabase mDb = null;

    //TABLE ACCOUNTS
    public static final String ACCOUNT_TABLE_NAME = AccountDAO.TABLE_NAME;
    public static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE " + ACCOUNT_TABLE_NAME + "(" +
               "ID BIGINT PRIMARY KEY," +
               "NAME VARCHAR(30)," +
               "BALANCE DECIMAL(15,2)," +
               "TOCOME DECIMAL(15,2)," +
               "CURRENCY VARCHAR(3));";
    public static final String ACCOUNT_TALBE_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE_NAME + ";";

    //TABLE OPERATIONS
    public static final String OPERATION_TABLE_NAME = OperationDAO.TABLE_NAME;
    public static final String OPERATION_TABLE_CREATE =
            "CREATE TABLE " + OPERATION_TABLE_NAME + "(" +
                    "ID BIGINT PRIMARY KEY," +
                    "ACCID BIGINT," +
                    "NAME VARCHAR(30)," +
                    "CATEGORIE VARCHAR(30)," +
                    "AMOUNT DECIMAL(15,2)," +
                    "ISGAIN SMALLINT," +
                    "EXECDATE VARCHAR(8));";
    public static final String OPERATION_TABLE_DROP = "DROP TABLE IF EXISTS " + OPERATION_TABLE_NAME + ";";

    //TABLE OPERATIONS_HISTORY
    public static final String OPERATION_HISTORY_TABLE_NAME = OperationHistoryDAO.TABLE_NAME;
    public static final String OPERATION_HISTORY_TABLE_CREATE =
            "CREATE TABLE " + OPERATION_HISTORY_TABLE_NAME + "(" +
                    "ID BIGINT PRIMARY KEY," +
                    "ACCID BIGINT," +
                    "NAME VARCHAR(30)," +
                    "CATEGORIE VARCHAR(30)," +
                    "AMOUNT DECIMAL(15,2)," +
                    "ISGAIN SMALLINT," +
                    "EXECDATE VARCHAR(8));";
    public static final String OPERATION_HISTORY_TABLE_DROP = "DROP TABLE IF EXISTS " + OPERATION_HISTORY_TABLE_NAME + ";";

    protected BaseDAO(Context context)
    {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(ACCOUNT_TABLE_CREATE);
        db.execSQL(OPERATION_TABLE_CREATE);
        db.execSQL(OPERATION_HISTORY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        dropTables(db);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        dropTables(db);
        onCreate(db);
    }

    public SQLiteDatabase open()
    {
        mDb = getWritableDatabase();
        return mDb;
    }

    public void close()
    {
        mDb.close();
    }

    public void dropTables(SQLiteDatabase db)
    {
        db.execSQL(ACCOUNT_TALBE_DROP);
        db.execSQL(OPERATION_TABLE_DROP);
        db.execSQL(OPERATION_HISTORY_TABLE_DROP);
    }
}
