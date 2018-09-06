package com.example.jean.gerersoncompte.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.jean.gerersoncompte.Constants;
import com.example.jean.gerersoncompte.Database.OperationDAO;
import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.GSCItems.Operation;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;
import com.example.jean.gerersoncompte.Views.EditTextDate;
import com.example.jean.gerersoncompte.Views.EditTextErrorChecker;

import java.util.TreeSet;

public class OperationCreationActivity extends GSCActivity
{
    private EditTextErrorChecker editName       = null;
    private EditTextErrorChecker editCategory   = null;
    private ImageButton buttonDropCategories         = null;
    private EditTextErrorChecker editAmount     = null;
    private RadioGroup radGrpSide               = null;
    private EditTextErrorChecker editECExecDate = null;
    private EditTextDate editExecDate           = null;

    private Account account = null;
    private Operation operation = null;
    private TreeSet<CharSequence> categories = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_creation);
        RelativeLayout currentLayout = (RelativeLayout) findViewById(R.id.ope_cre_rlo);

        Intent intent = getIntent();

        account = (Account)intent.getSerializableExtra(Constants.extraAccount);
        operation = (Operation)intent.getSerializableExtra(Constants.extraOperation);
        categories = (TreeSet<CharSequence>) intent.getSerializableExtra(Constants.extraCategories);

        editName = (EditTextErrorChecker) findViewById(R.id.ope_cre_edit_nom);
        editCategory = (EditTextErrorChecker) findViewById(R.id.ope_cre_edit_categorie);
        buttonDropCategories = (ImageButton) findViewById(R.id.ope_cre_but_drop_categories);
        editAmount = (EditTextErrorChecker) findViewById(R.id.ope_cre_edit_montant);
        radGrpSide = (RadioGroup) findViewById(R.id.ope_cre_rad_grp_sens);
        editExecDate = (EditTextDate) findViewById(R.id.ope_cre_edit_execdate);
        editECExecDate = editExecDate.getEditText();

        editName.addToLayout(currentLayout);
        editCategory.addToLayout(currentLayout);
        editAmount.addToLayout(currentLayout);

        initOperation();

        setResult(RESULT_CANCELED);
    }

    private void initOperation()
    {
        if(operation != null)
        {
            editName.setText(operation.getName());
            editCategory.setText(operation.getCategory());
            editAmount.setText(Tools.floatFormat(operation.getAmount(), 2));
            radGrpSide.check(operation.isGain() ? R.id.ope_cre_rad_btn_credit : R.id.ope_cre_rad_btn_debit);
            editExecDate.setDate(operation.getExecDate());
        }
    }

    public void processDropCategories(View view)
    {
        if(categories.isEmpty())
            return;

        CharSequence[] arrayCategories = categories.toArray(new CharSequence[categories.size()]);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.support_simple_spinner_dropdown_item, arrayCategories);
        final ListPopupWindow popup = new ListPopupWindow(this);
        popup.setAnchorView(editCategory);
        popup.setAdapter(adapter);
        popup.setWidth(editCategory.getMeasuredWidth());
        popup.setHeight(editCategory.getMeasuredHeight() * 5);
        popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                CharSequence category = (CharSequence)popup.getListView().getAdapter().getItem(position);
                editCategory.setText(category);
                popup.dismiss();
            }
        });
        popup.show();
    }

    public void processCreateOperation(View view)
    {
        if(!checkFields())
            return;

        try
        {
            String  opeName = editName.getText().toString();
            String  opeCategory = editCategory.getText().toString();
            float   opeAmount = Float.valueOf(editAmount.getText().toString());
            boolean opeIsGain = radGrpSide.getCheckedRadioButtonId() == R.id.ope_cre_rad_btn_credit;
            String  opeExecDate = editExecDate.getDate();

            boolean isNew = false;
            if(operation == null)
            {
                isNew = true;
                operation = new Operation(opeName, account.getId());
            }
            else
                operation.setName(opeName);
            operation.setCategory(opeCategory);
            operation.setAmount(opeAmount);
            operation.setGain(opeIsGain);
            operation.setExecDate(opeExecDate);

            OperationDAO opeDAO = OperationDAO.getInstance();
            boolean succeed = false;
            if(isNew)
                succeed = opeDAO.insert(operation);
            else
                succeed = opeDAO.update(operation);

            if(succeed)
            {
                GeneralDatas gd = GeneralDatas.getInstance();
                String todayDate = gd.lastDate;
                String tomorrowDate = Tools.addDays(todayDate, 1);
                account.updateOperation(operation, todayDate, tomorrowDate);
                account.update();

                Intent result = new Intent();
                result.putExtra(Constants.extraAccount, account);
                setResult(RESULT_OK, result);

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
        if(account == null)
            return false;

        boolean valid = true;
        valid = editName.check() && valid;
        valid = editCategory.check() && valid;
        valid = editAmount.check() && valid;
        valid = editECExecDate.check() && valid;

        return valid;
    }
}
