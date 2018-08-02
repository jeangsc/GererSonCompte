package com.example.jean.gerersoncompte.Views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jean.gerersoncompte.Adapters.SpinnerMultipleAdapter;
import com.example.jean.gerersoncompte.GSCItems.SpinnerMultipleItem;
import com.example.jean.gerersoncompte.R;

import java.util.ArrayList;

/**
 * Created by V17 on 30/07/2018.
 */

public class SpinnerMultiple extends RelativeLayout
{
    public static final String CHOICE_DEFAULT = "Tout sélectionner";
    public static final int CHOICE_MAX_LENGTH = 30;

    private RelativeLayout baseLayout;
    private TextView itemsChoosen;
    private ImageButton dropDownButton;

    private ArrayList<SpinnerMultipleItem> itemsList;
    private int checkCounter;

    public SpinnerMultiple(Context context)
    {
        super(context);
        init(null, 0);
    }

    public SpinnerMultiple(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public SpinnerMultiple(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr)
    {
        itemsList = new ArrayList<SpinnerMultipleItem>();
        checkCounter = 0;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_multiple, this, true);
        baseLayout = (RelativeLayout) view.findViewById(R.id.spi_mul_rlo);
        itemsChoosen = (TextView) view.findViewById(R.id.spi_mul_text_choix);
        dropDownButton = (ImageButton)view.findViewById(R.id.spi_mul_but_dropdown);

        dropDownButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LinearLayout layout = (LinearLayout)LayoutInflater.from(getContext())
                        .inflate(R.layout.popup_spinner_multiple, (ViewGroup)findViewById(R.id.popup_spi_mul_llo));
                PopupWindow popup = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
                popup.setBackgroundDrawable(new BitmapDrawable());
                popup.setTouchable(true);
                popup.setOutsideTouchable(true);


                ListView list = (ListView) layout.findViewById(R.id.popup_spi_mul_list);
                SpinnerMultipleAdapter adapter = new SpinnerMultipleAdapter(getContext(), itemsList);
                list.setAdapter(adapter);
                popup.showAsDropDown(baseLayout);


            }
        });

        addItem(CHOICE_DEFAULT);
        addItem("Toto 2");
        updateText();
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        dropDownButton.requestLayout();
        dropDownButton.getLayoutParams().height = dropDownButton.getMeasuredHeight();
        dropDownButton.getLayoutParams().width = dropDownButton.getLayoutParams().height;
    }

    public void checkItem(int index)
    {
        if(index < itemsList.size())
        {
            itemsList.get(index).setItemChecked(true);
            if (index == 0)
            {
                for (int i = 1; i < itemsList.size(); i++)
                    itemsList.get(i).setItemChecked(true);
                checkCounter = itemsList.size()-1;
            }
            else
            {
                checkCounter++;
                if (checkCounter == itemsList.size() - 1)
                    itemsList.get(0).setItemChecked(true);
            }
            updateText();
        }
    }

    public void uncheckItem(int index)
    {
        if(index < itemsList.size())
        {
            itemsList.get(index).setItemChecked(false);
            if(index == 0)
            {
                for (int i = 1; i < itemsList.size(); i++)
                    itemsList.get(i).setItemChecked(false);
                checkCounter = 0;
            }
            else
            {
                itemsList.get(0).setItemChecked(false);
                checkCounter--;
            }
            updateText();
        }
    }

    public void updateText()
    {
        String strChoice = "";
        if(isChecked(0))
        {
            strChoice = CHOICE_DEFAULT;
        }
        else
        {
            boolean hasFirst = false;
            for(int i = 1; i < itemsList.size(); i++)
            {
                if(isChecked(i))
                {
                    String item = getItem(i);
                    if(strChoice.length() + item.length() < CHOICE_MAX_LENGTH)
                    {
                        strChoice += (hasFirst ? "," : "");
                        strChoice += item;
                        hasFirst = true;
                    }
                    else
                    {
                        strChoice += "...";
                        break;
                    }
                }
            }
        }
        itemsChoosen.setText(strChoice);
    }

    public void clearItems()
    {
        checkCounter = 0;
        itemsList.clear();
    }

    public void addItem(String item)
    {
        itemsList.add(new SpinnerMultipleItem(item, true));
        checkCounter++;
    }

    public void addItem(int index, String item)
    {
        itemsList.add(index, new SpinnerMultipleItem(item, true));
        checkCounter++;
    }

    public void removeItem(String item)
    {
        int index = itemsList.indexOf(item);
        removeItem(index);
    }

    public void removeItem(int index)
    {
        if(isChecked(index))
            checkCounter--;
        itemsList.remove(index);
    }

    public String getItem(int index)
    {
        if(index < itemsList.size())
            return itemsList.get(index).getItemName();
        return null;
    }

    public boolean isChecked(int index)
    {
        if(index < itemsList.size())
            return itemsList.get(index).isItemChecked();
        return false;
    }

    public int getItemsSize()
    {
        return itemsList.size();
    }
}
