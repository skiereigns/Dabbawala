package com.example.dabbawala;

import java.util.List;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

public class AdapterCart extends ArrayAdapter<Product> implements UndoAdapter {

    private List<Product> data;
    Context ctx;
    private static LayoutInflater inflater = null;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public AdapterCart(Context context, int resID, List<Product> items) {
        super(context, resID, items);
        ctx = context;
        data = items;

        inflater = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

//    public Adapter1(Activity a, ArrayList<Product> d) {
//        activity = a;
//        data = d;
//        ctx = activity.getApplicationContext();
//        inflater = (LayoutInflater) activity.
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        // Create ImageLoader object to download and show image in list
//        // Call ImageLoader constructor to initialize FileCache
//
//    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        return data.size();
    }


    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getUndoView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder {

        public TextView txtID, txtQuantity;
        public ImageButton btnIncrease, btnDescrease;


    }
    ViewHolder holder;
    String TAG="Adaopter";
    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;


        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.custum_list_cart, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtID = vi.findViewById(R.id.list_row_draganddrop_textview);
            holder.txtQuantity = vi.findViewById(R.id.txtQuantity);
            holder.btnIncrease = vi.findViewById(R.id.btnIncrease);
            holder.btnDescrease = vi.findViewById(R.id.btnDescrease);


            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        holder.txtQuantity.setText("Quantity: " + data.get(position).getProduct_quantity());
        holder.txtID.setText("Name: " + data.get(position).getProduct_name() +

                 "\nCost: " + data.get(position).getProduct_price());

        
        //        holder.btnIncrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                data.get(position).setProduct_quantity(data.get(position).getProduct_quantity() + 1);
//                Log.i(TAG, "onClick: "+data.get(position).getProduct_quantity());
//                holder.txtQuantity.setText( data.get(position).getProduct_quantity());
//
//            }
//        });
//        holder.btnDescrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                data.get(position).setProduct_quantity(data.get(position).getProduct_quantity() - 1);
//                holder.txtQuantity.setText( data.get(position).getProduct_quantity());
//            }
//        });

        return vi;
    }


}