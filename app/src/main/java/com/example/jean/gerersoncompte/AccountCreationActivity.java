package com.example.jean.gerersoncompte;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class AccountCreationActivity extends AppCompatActivity {

    private EditText m_editName = null;
    private EditText m_editBalance = null;
    private CheckBox m_checkCurrent = null;

    private final static String extraIsCurrent = "EXTRA_IS_CURRENT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        Intent intent = getIntent();
        boolean currentIsClickable = !intent.getBooleanExtra(extraIsCurrent, false);

        m_editName = (EditText) findViewById(R.id.acc_cre_edit_nom);
        m_editBalance = (EditText) findViewById(R.id.acc_cre_edit_solde);
        m_checkCurrent = (CheckBox) findViewById(R.id.acc_cre_check_courant);

        m_checkCurrent.setChecked(!currentIsClickable);
        m_checkCurrent.setClickable(currentIsClickable);

        setResult(RESULT_CANCELED);
    }

    public void processCreateAccount(View view)
    {
        if(!checkFields())
            return;

        try
        {
            String  accName = m_editName.getText().toString();
            float   accBalance = Float.valueOf(m_editBalance.getText().toString());
            boolean accCurrent = m_checkCurrent.isChecked();

            Account account = new Account(accName);
            account.setBalance(accBalance);

            AccountDAO accDAO = AccountDAO.getInstance();
            boolean succeed = accDAO.insert(account);

            if(succeed)
            {
                GeneralDatas gd = GeneralDatas.getInstance();
                if(accCurrent)
                    gd.accCurId = account.getId();

                gd.save();
                setResult(RESULT_OK);
                finish();
            }
        }

        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
    }

    public boolean checkFields()
    {
        String accBalanceStr = m_editBalance.getText().toString();
        if(accBalanceStr.isEmpty())
        {
            //creer message erreur
            return false;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Tools.autoClearFocus(this, event);
        return super.dispatchTouchEvent( event );
    }
}
