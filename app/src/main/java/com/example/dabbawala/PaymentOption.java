package com.example.dabbawala;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;


public class PaymentOption extends Activity implements OnItemSelectedListener {
    Spinner bank_name;
    Button pay, pick_date;
    String userid, amount;
    EditText card_number, name, show_date, cvv_number;
    private int year = 0;
    private int month = 0;
    private int day = 0;
    static final int DATE_PICKER_ID = 1111;
    EditText txtAmount;

    TextView tvCurrent;
    String total_sum;

    SharedPreferences preference;
    String TAG = "PAyment";
    Context ctx;
    DatabaseReference mDatabaseReference;
String resid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_option);
        bank_name = (Spinner) findViewById(R.id.select_bank);
        pay = (Button) findViewById(R.id.payment);
        card_number = (EditText) findViewById(R.id.cardnumber);
        name = (EditText) findViewById(R.id.name_on_card);
        show_date = (EditText) findViewById(R.id.date);
        cvv_number = (EditText) findViewById(R.id.cvv_number);
        pick_date = (Button) findViewById(R.id.pick_date);
        txtAmount = (EditText) findViewById(R.id.txtamount);
        tvCurrent = (TextView) findViewById(R.id.tvCurrentAMount);
        total_sum = getIntent().getStringExtra("total_cost");
        Log.i(TAG, "onCreate: " + total_sum);
        ctx = this;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                PaymentOption.this, R.array.bank_name,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank_name.setAdapter(adapter);
        bank_name.setOnItemSelectedListener(this);

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        userid = preference.getString("userid", "");
        tvCurrent.setText("Total Amount Payble: " + total_sum + " Rs");
        txtAmount.setText(total_sum);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        pay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if ((card_number.getText().toString().equals("") || card_number
                        .getText().toString().length() < 16)
                        || name.getText().toString().equals("")
                        || show_date.getText().toString().equals("")
                        || cvv_number.getText().toString().equals("")
                        || cvv_number.getText().toString().length() < 3) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill all the details with valid Entry!",
                            Toast.LENGTH_SHORT).show();
                } else {

                    int count = 0;
                    JSONArray jsonArray;
                    preference = PreferenceManager.getDefaultSharedPreferences(ctx);
                    try {

                        try {
                            jsonArray = new JSONArray(preference.getString("cart_items", null));
                        } catch (Exception e) {
                            e.printStackTrace();
                            jsonArray = new JSONArray();
                        }

                    resid=  jsonArray.getJSONObject(0).getString("pdesc");



                        String booking_id = Constants.getDateTimeID();
                        Map map = new HashMap();
                        map.put("resid",resid);
                        map.put("userid",FirebaseAuth.getInstance().getUid());
                        map.put("name",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        map.put("latitude", preference.getString("latitude", "0.0"));
                        map.put("longitude", preference.getString("longitude", "0.0"));
                        map.put("token_id", preference.getString("token_id", null));
                        map.put("status", "pending");
                        map.put("phone", getIntent().getStringExtra("phone"));
                        map.put("delivery_address", getIntent().getStringExtra("delivery_address"));
                        map.put("total_amount",total_sum);
                        mDatabaseReference.child(Constants.booking_details).child(booking_id).setValue(map);

                        while (count < jsonArray.length()) {


                            JSONObject jsonObject = jsonArray.getJSONObject(count);
                            Proudcts product = new Proudcts();
                            product.setBooking_id(booking_id);
                            product.setPid(jsonObject.getString("pid"));
                            product.setProductname(jsonObject.getString("pname"));
                            product.setUserid(FirebaseAuth.getInstance().getUid());
                            product.setDesc(jsonObject.getString("pdesc"));
product.setQuantity(Integer.parseInt(jsonObject.getString("pquantity")));

                            product.setToken_id(preference.getString("token_id", null));
                            product.setCost(Integer.parseInt(jsonObject.getString("pprice")));
                            mDatabaseReference.child(Constants.booking_details).child(booking_id).child(Constants.items).push().setValue(product);

                            count++;
                        }
                        // prefrences.edit().putString("latitude", Double.toString(latitude)).putString("longitude", Double.toString(longitude)).apply();





                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (count > 0) {
                        preference.edit().remove("cart_items").commit();
                        Toast.makeText(getApplicationContext(),
                                "Payment done successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please check cart",
                                Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            }
        });

        pick_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                // On button click show datepicker dialog
                showDialog(DATE_PICKER_ID);

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View arg1, int position,
                               long arg3) {
        switch (parent.getId()) {
            case R.id.select_bank:

                bank_name.setSelection(position);

                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month, day);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Show selected date
            show_date.setText(new StringBuilder().append(month + 1).append("-")
                    .append(day).append("-").append(year).append(" "));

        }
    };


}