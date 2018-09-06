package com.example.jean.gerersoncompte.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.example.jean.gerersoncompte.Constants;
import com.example.jean.gerersoncompte.Database.AccountDAO;
import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;
import com.example.jean.gerersoncompte.Views.EditTextErrorChecker;

public class AccountCreationActivity extends GSCActivity
{
    private EditTextErrorChecker m_editName = null;
    private EditTextErrorChecker m_editBalance = null;
    private CheckBox m_checkCurrent = null;

    private Account account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.acc_cre_rlo);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra(Constants.extraAccount);
        boolean currentIsClickable = !intent.getBooleanExtra(Constants.extraIsCurrent, false);

        m_editName = (EditTextErrorChecker) findViewById(R.id.acc_cre_edit_nom);
        m_editBalance = (EditTextErrorChecker) findViewById(R.id.acc_cre_edit_solde);
        m_checkCurrent = (CheckBox) findViewById(R.id.acc_cre_check_courant);

        m_checkCurrent.setChecked(!currentIsClickable);
        m_checkCurrent.setClickable(currentIsClickable);

        m_editName.addToLayout(layout);
        m_editBalance.addToLayout(layout);

        initAccount();

        setResult(RESULT_CANCELED);
    }

    private void initAccount()
    {
        if(account != null)
        {
            m_editName.setText(account.getName());
            m_editBalance.setText(Tools.floatFormat(account.getBalance(), 2));
        }
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

            boolean isNew = false;
            if(account == null)
            {
                account = new Account(accName);
                isNew = true;
            }
            else
                account.setName(accName);
            account.setBalance(accBalance);

            AccountDAO accDAO = AccountDAO.getInstance();
            boolean succeed = false;
            if(isNew)
                succeed = accDAO.insert(account);
            else
                succeed = accDAO.update(account);

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
        boolean valid = true;
        valid = m_editName.check() && valid;
        valid = m_editBalance.check() && valid;
        return valid;
    }
}
