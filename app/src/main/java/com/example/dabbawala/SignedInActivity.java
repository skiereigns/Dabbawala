package com.example.dabbawala;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;



public class SignedInActivity extends AppCompatActivity {
  String name,email,phone,url_image=null,providerId,userid,dob,vehicle_no,password;
    ImageView imgUser;
    EditText txtPhone,txtDob,txtVehicleNo,txtPassword;
    SharedPreferences sharedPreferences;
ImageView imgQRCode;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FloatingActionButton fab;
    Context context;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);
        context=this;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgUser=(ImageView)findViewById(R.id.imgUser);
        imgQRCode=(ImageView)findViewById(R.id.imgQrcode);
txtDob=(EditText)findViewById(R.id.txtDob) ;
txtPassword=(EditText)findViewById(R.id.txtPassword) ;
txtPhone=(EditText)findViewById(R.id.txtPhone) ;
txtVehicleNo=(EditText)findViewById(R.id.txtVehicleNo) ;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        auth = FirebaseAuth.getInstance();
        userid = auth.getUid();
        name = auth.getCurrentUser().getDisplayName();
        email = auth.getCurrentUser().getEmail();
        try{
            url_image = auth.getCurrentUser().getPhotoUrl().toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            phone = auth.getCurrentUser().getPhoneNumber();

        }catch (Exception e){
            e.printStackTrace();
        }

        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constants.DATABASE_PATH_UPLOADS);
if(url_image!=null){
    Picasso.with(this).load(url_image).into(imgUser);
}

        fab =  findViewById(R.id.fab);

        myRef.child(Constants.users).child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.i("SA", "onDataChange: " + dataSnapshot1.getKey()+"::"+dataSnapshot1.getValue());
                    if (dataSnapshot1.getKey().equals("vehicle_id"))
                        vehicle_no = (String) dataSnapshot1.getValue();
                    if (dataSnapshot1.getKey().equals("name"))
                        name = (String) dataSnapshot1.getValue();
                    if (dataSnapshot1.getKey().equals("phone"))
                        phone = (String) dataSnapshot1.getValue();
                    if (dataSnapshot1.getKey().equals("email"))
                        email = (String) dataSnapshot1.getValue();

                    if (dataSnapshot1.getKey().equals("url"))
                        url_image = (String) dataSnapshot1.getValue();
                    if (dataSnapshot1.getKey().equals("password"))
                        password = (String) dataSnapshot1.getValue();
                    if (dataSnapshot1.getKey().equals("dob"))
                        dob = (String) dataSnapshot1.getValue();



                    count++;


                }
                Log.i("SA", "onDataChange: "+count);
                Log.i("SA", "onCreate: count"+count);
                if (count > 0) {
                    try {
                        Log.i("SA", "onCreate: phone="+phone);
                        txtPhone.setText(phone);
                        txtPassword.setText(password);
                        txtVehicleNo.setText(vehicle_no);
                        txtDob.setText(dob);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("userid",userid)
                                .putBoolean("reg",true)
                                .putString("name",name)
                                .putString("dob",dob)
                                .putString("email",email)
                                .putString("password",password)
                                .putString("vehicle_no",vehicle_no)
                                .putString("image",url_image)
                                .putString("phone",phone)
                                .apply();

                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                             @Override
                                                                             public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                                                                                 // Do whatever you want when the date is selected.
                                                                                String dd=Integer.toString(i2);
                                                                                 if(Integer.toString(i2).length()<2){
                                                                                     dd="0"+i2;
                                                                                 }
                                                                                 String mm=Integer.toString(i1);
                                                                                 if(Integer.toString(i1).length()<2){
                                                                                     mm="0"+(i1+1);
                                                                                 }else{
                                                                                     mm=""+(i1+1);
                                                                                 }
                                                                                 txtDob.setText(dd+"/"+mm+"/"+i);

                                                                             }
                                                                         },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.setYearRange(1900, 2009); // You can add your value for YEARS_IN_THE_FUTURE.
txtDob.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       datePickerDialog.show(getFragmentManager(),"hello");
    }
});


    }
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w("SA", "storage permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,       Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, 5);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        5);
            }
        };


        Snackbar.make(fab, R.string.permission_storage_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }
public void save(View v){

   phone=txtPhone.getText().toString();
    dob=txtDob.getText().toString();
    vehicle_no=txtVehicleNo.getText().toString();
    password=txtPassword.getText().toString();
    if(!TextUtils.isEmpty(vehicle_no)){
        if(!TextUtils.isEmpty(password)){
            if(!TextUtils.isEmpty(phone)){
                if(!TextUtils.isEmpty(dob)){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(Constants.DATABASE_PATH_UPLOADS);

                    //String name, String userid, String url, String phone, String email, String vehicle_id, String dob, String password)
                    User user=new User(name,userid,url_image,phone,email,vehicle_no,dob,password,null,"user");
                    myRef.child(Constants.users).child(userid).setValue(user);

                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("userid",userid)
                            .putBoolean("reg",true)
                            .putString("name",name)
                            .putString("dob",dob)
                            .putString("email",email)
                            .putString("password",password)
                            .putString("vehicle_no",vehicle_no)
                            .putString("image",url_image)
                            .putString("phone",phone)
                            .apply();

                    Toast.makeText(this, "Account Creation success", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(context, "Please check dob", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Please check phone no.", Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(this, "Password cannot be blank!", Toast.LENGTH_SHORT).show();
        }
    }else{
        Toast.makeText(this, "Vehicle No. cannot be blank!", Toast.LENGTH_SHORT).show();
    }

}

}
