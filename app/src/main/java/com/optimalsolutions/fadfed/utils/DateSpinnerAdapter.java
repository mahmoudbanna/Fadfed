package com.optimalsolutions.fadfed.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.optimalsolutions.fadfed.R;

public class DateSpinnerAdapter extends ArrayAdapter<String>{

    protected FragmentActivity context;

    public DateSpinnerAdapter(FragmentActivity context, int resource) {

        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View element = super.getView(position, convertView, parent);
        ((TextView) element).setTextColor(context.getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        element.setLayoutParams(layoutParams);
        return element;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View element =super.getDropDownView(position, convertView, parent);
        element.setPadding(15, 30, 0, 30);
        return element;
    }

    @TargetApi(11)
    public void setData(String[] data) {
        clear();
        if (data != null) {
            //If the platform supports it, use addAll, otherwise add in loop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                addAll(data);
            }else{
                for(String item: data){
                    add(item);
                }
            }
        }
    }
}