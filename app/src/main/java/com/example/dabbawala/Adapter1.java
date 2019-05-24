package com.example.dabbawala;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class Adapter1 extends ArrayAdapter<String> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Adapter1(Context context, int resID, List items) {
        super(context, resID, items);                       
    }

    @SuppressLint("NewApi")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        v.setBackgroundColor(Color.argb(220, 74,121, 181));
        v.setBackgroundResource(R.drawable.bg_button);
       
       // if (position == 1) {
            ((TextView) v).setTextColor(Color.GREEN); 
       // }s
        return v;
    }

}