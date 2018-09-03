package com.example.jean.gerersoncompte.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jean.gerersoncompte.GSCItems.Account;
import com.example.jean.gerersoncompte.GeneralDatas;
import com.example.jean.gerersoncompte.R;
import com.example.jean.gerersoncompte.Tools;

import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account>
{
    public AccountAdapter(Context context, List<Account> list)
    {
        super(context,0, list);
    }

    private static class ViewHolder
    {
        TextView textAccount;
        TextView textBalance;
        TextView textToCome;
        TextView textNextBalance;
        ImageView imageStar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Account acc = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.account_resume, parent, false);
            viewHolder.textAccount     = (TextView) convertView.findViewById(R.id.acc_res_nom);
            viewHolder.textBalance     = (TextView) convertView.findViewById(R.id.acc_res_value_solde);
            viewHolder.textToCome      = (TextView) convertView.findViewById(R.id.acc_res_value_a_venir);
            viewHolder.textNextBalance = (TextView) convertView.findViewById(R.id.acc_res_value_prochain_solde);
            viewHolder.imageStar       = (ImageView) convertView.findViewById(R.id.acc_res_image_etoile);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GeneralDatas gd = GeneralDatas.getInstance();
        boolean isCurrent = acc.getId() == gd.accCurId;

        String accName        = acc.getName();
        int    starVisibility = isCurrent ? View.VISIBLE : View.INVISIBLE;
        float  accBalance     = acc.getBalance();
        float  accToCome      = acc.getToCome();
        float  accNextBalance = acc.getBalance() + acc.getToCome();
        int    colorToCome    = Tools.getColorFromSign(accToCome, R.color.colorGreen, R.color.colorRed, R.color.colorBlack, getContext());

        viewHolder.textAccount.setText(accName);
        viewHolder.textBalance.setText(Tools.getFormattedAmount(accBalance, "EUR"));
        viewHolder.textToCome.setText(Tools.getFormattedAmount(accToCome, "EUR", true));
        viewHolder.textToCome.setTextColor(colorToCome);
        viewHolder.textNextBalance.setText(Tools.getFormattedAmount(accNextBalance, "EUR"));
        viewHolder.imageStar.setVisibility(starVisibility);

        return convertView;
    }
}
