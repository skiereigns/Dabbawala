package com.example.dabbawala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListCartActivitiy extends Activity {
    List<Product> lstProducts = new ArrayList<>();

    SharedPreferences preference;
    DynamicListView mListView;
    String from;
    String TAG = "LA";
    AdapterCart myAdapter;
    JSONObject jsonCurrenCart = null;
    JSONArray jsonArray = null;
    double total_sum = 0;
    TextView txtDetails;
Button btnCheckout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out);
        String pid = getIntent().getStringExtra("pid");
        txtDetails = findViewById(R.id.txtDetails);
        btnCheckout=findViewById(R.id.btnCheckout);
        if (pid != null) {

            String pname = getIntent().getStringExtra("pname");
            String pdesc = getIntent().getStringExtra("pdesc");
            int pprice = getIntent().getIntExtra("pprice", 0);
            int pquantity = getIntent().getIntExtra("pquantity", 0);
            Product product = new Product();
            product.setProductid(pid);
            product.setResid(pdesc);
            product.setProduct_name(pname);
            product.setProduct_price(pprice);
            product.setProduct_quantity(pquantity);
            lstProducts.add(product);
btnCheckout.setVisibility(View.GONE);
            try {
                jsonCurrenCart = new JSONObject();
                jsonCurrenCart.put("pid", pid);
                jsonCurrenCart.put("pname", pname);
                jsonCurrenCart.put("pdesc", pdesc);
                jsonCurrenCart.put("pprice", pprice);
                jsonCurrenCart.put("pquantity", pquantity);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        try {

            try {
                jsonArray = new JSONArray(preference.getString("cart_items", null));
            } catch (Exception e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }

            int count = 0;
            while (count < jsonArray.length()) {

                JSONObject jsonObject = jsonArray.getJSONObject(count);
                Product product = new Product();
                product.setProductid(jsonObject.getString("pid"));
                product.setResid(jsonObject.getString("pdesc"));
                product.setProduct_name(jsonObject.getString("pname"));
                product.setProduct_price(jsonObject.getInt("pprice"));
                product.setProduct_quantity(jsonObject.getInt("pquantity"));

                lstProducts.add(product);
                total_sum = total_sum + jsonObject.getInt("pprice") * jsonObject.getInt("pquantity");
                count++;
            }
            if (jsonCurrenCart != null) {

                jsonArray.put(jsonCurrenCart);
                preference.edit().putString("cart_items", jsonArray.toString()).apply();
            }
            txtDetails.setText("Total Payble Amount: " + total_sum);
        } catch (Exception e) {
            e.printStackTrace();
        }


        mListView = findViewById(R.id.dynamiclistview);
        myAdapter = new AdapterCart(this, R.layout.custum_list_cart, lstProducts);

        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(myAdapter);
        animationAdapter.setAbsListView(mListView);
        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(300);
        mListView.setAdapter(animationAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(getBaseContext(), ChangeProductQuantity.class);
                intent1.putExtra("position", position);
                startActivity(intent1);
                finish();
            }
        });
        //  mListView.enableDragAndDrop();;
        //    mListView.enableSimpleSwipeUndo();
        SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(myAdapter, ListCartActivitiy.this,
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {

                            myAdapter.remove(lstProducts.get(position));
                            try {
                                jsonArray = new JSONArray(preference.getString("cart_items", null));
                                total_sum = total_sum - (jsonArray.getJSONObject(position).getInt("pprice") * jsonArray.getJSONObject(position).getInt("pquantity"));
                                txtDetails.setText("Total Payble Amount: " + total_sum);
                                jsonArray.remove(position);
                                preference.edit().putString("cart_items", jsonArray.toString()).apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                                jsonArray = new JSONArray();
                            }

                        }
                    }
                }
        );
        swipeUndoAdapter.setAbsListView(mListView);
        mListView.setAdapter(swipeUndoAdapter);
        mListView.enableSimpleSwipeUndo();
    }

    public void checkOut(View v) {
        Intent intent = new Intent(getBaseContext(), CheckOutActivity.class);
        intent.putExtra("total_cost", Double.toString(total_sum));
      //  Log.i(TAG, "checkOut: total sum" + total_sum);
        startActivity(intent);
        finish();
    }

    public void clearCart(View v) {
        preference.edit().remove("cart_items").commit();
        Toast.makeText(this, "Carts Cleared successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
