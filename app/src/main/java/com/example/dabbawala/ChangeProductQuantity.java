package com.example.dabbawala;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChangeProductQuantity extends AppCompatActivity {
    TextView tvProductDetails;
    JSONArray jsonArray;
    SharedPreferences preferences;
    int position;
    double total_sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_product_quantity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        position = getIntent().getIntExtra("position", 0);
        tvProductDetails = findViewById(R.id.tvProductDetails);

        try {
            jsonArray = new JSONArray(preferences.getString("cart_items", null));

            int quantity = jsonArray.getJSONObject(position).getInt("pquantity") + 1;
            int pprice = jsonArray.getJSONObject(position).getInt("pprice");
            String pname = jsonArray.getJSONObject(position).getString("pname");
            String pdesc = jsonArray.getJSONObject(position).getString("pdesc");
            total_sum =  (jsonArray.getJSONObject(position).getInt("pprice")
                    * quantity);


            updateQuantity(quantity, total_sum, pname, pdesc, pprice);


        } catch (Exception e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void updateQuantity(int quantity, double total_sum, String pname, String pdesc, int pprice) {
        tvProductDetails.setText("Product Name: " + pname
              //  + "\nProduct Desc: " + pdesc
                + "\nProduct Price: " + pprice
                + "\nTotal Qunatity: " + quantity
                + "\nTotal Price: " + total_sum
        );

    }

    public void plus(View v) {

        try {
            jsonArray = new JSONArray(preferences.getString("cart_items", null));

            int quantity = jsonArray.getJSONObject(position).getInt("pquantity") + 1;
            int pprice = jsonArray.getJSONObject(position).getInt("pprice");
            String pname = jsonArray.getJSONObject(position).getString("pname");
            String pdesc = jsonArray.getJSONObject(position).getString("pdesc");
            total_sum =  (jsonArray.getJSONObject(position).getInt("pprice")
                    * quantity);
            JSONObject jsonCurrenCart;
            jsonCurrenCart = new JSONObject();
            jsonCurrenCart.put("pid", jsonArray.getJSONObject(position).getString("pid"));
            jsonCurrenCart.put("pname", pname);
            jsonCurrenCart.put("pdesc", pdesc);
            jsonCurrenCart.put("pprice", pprice);
            jsonCurrenCart.put("pquantity", quantity);
            jsonArray.put(position, jsonCurrenCart);
            updateQuantity(quantity, total_sum, pname, pdesc, pprice);

            preferences.edit().putString("cart_items", jsonArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
    }

    public void minus(View v) {
        try {
            jsonArray = new JSONArray(preferences.getString("cart_items", null));

            int quantity = jsonArray.getJSONObject(position).getInt("pquantity") -1;
            if(quantity>0){
                int pprice = jsonArray.getJSONObject(position).getInt("pprice");
                String pname = jsonArray.getJSONObject(position).getString("pname");
                String pdesc = jsonArray.getJSONObject(position).getString("pdesc");
                total_sum =  (jsonArray.getJSONObject(position).getInt("pprice")
                        * quantity);
                JSONObject jsonCurrenCart;
                jsonCurrenCart = new JSONObject();
                jsonCurrenCart.put("pid", jsonArray.getJSONObject(position).getString("pid"));
                jsonCurrenCart.put("pname", pname);
                jsonCurrenCart.put("pdesc", pdesc);
                jsonCurrenCart.put("pprice", pprice);
                jsonCurrenCart.put("pquantity", quantity);
                jsonArray.put(position, jsonCurrenCart);
                updateQuantity(quantity, total_sum, pname, pdesc, pprice);

                preferences.edit().putString("cart_items", jsonArray.toString()).apply();
            }else{
                Toast.makeText(this, "Please enter positive quantity", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
    }
}
