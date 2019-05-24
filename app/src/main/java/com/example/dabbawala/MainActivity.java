package com.example.dabbawala;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    LocationManager locationManager;
    SharedPreferences prefrences;
    ArrayList<String> lstUsers = new ArrayList<>();
    ArrayList<String> lstUsersID = new ArrayList<>();
    NavigationView navigationView;
    EditText txtSearch;
    Adapter1 adapter1;
    FirebaseAuth auth;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ButterKnife.bind(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        context = this;
        prefrences = PreferenceManager.getDefaultSharedPreferences(this);

        auth = FirebaseAuth.getInstance();

        txtSearch = findViewById(R.id.txtSearch);
        listView = findViewById(R.id.list_items);
        adapter1 = new Adapter1(context, R.layout.custum_list, lstUsers);
        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                Intent intent1 = new Intent(getBaseContext(), ChatActivity.class);
                                intent1.putExtra("fuserid", lstUsersID.get(position));
                                intent1.putExtra("userid", prefrences.getString("userid",""));
                                startActivity(intent1);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Intent intent = new Intent(getBaseContext(), ViewMenu.class);
                                intent.putExtra("resid", lstUsersID.get(position));
                                startActivity(intent);

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("What do you want?").setPositiveButton("Chat", dialogClickListener)
                        .setNegativeButton("View Items", dialogClickListener).show();

            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0,
                        0, this);
            } else if (locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0,
                        0, this);
            } else {


                Toast.makeText(getApplicationContext(), "Enable Location", Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        getUsers(null);
    }

    DatabaseReference mDatabaseReference;
    StorageReference mStorageReference;

    Context context;
    String search = null;

    public void searchUsers(View v) {
        search = txtSearch.getText().toString();
        if (search.length() > 0) {
            getUsers(search);
        } else {
            Toast.makeText(context, "Please enter name to search", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUsers(final String search) {


        mDatabaseReference.child(Constants.resturant).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstUsersID.clear();
                lstUsers.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: " + dataSnapshot1.getValue());
                    Log.i(TAG, "onDataChange: " + dataSnapshot1.child("resname").getValue());

                    try {
                        if (search != null) {
                            String name = (String) dataSnapshot1.child("resname").getValue();
                            if (name.toLowerCase().contains(search.toLowerCase())) {


                                if (name != null) {
                                    lstUsers.add(dataSnapshot1.child("resname").getValue().toString()+
                                            "\nAddress:"+dataSnapshot1.child("resaddress").getValue().toString());
                                   lstUsersID.add(dataSnapshot1.getKey());
                                    adapter1.notifyDataSetChanged();
                                }
                            }


                        } else {
                            String name = (String) dataSnapshot1.child("resname").getValue();

                            if (name != null) {
                                lstUsers.add(dataSnapshot1.child("resname").getValue().toString()+
                                        "\nAddress:"+dataSnapshot1.child("resaddress").getValue().toString());
                                lstUsersID.add(dataSnapshot1.getKey());
                                adapter1.notifyDataSetChanged();
                            }


                        }


                    } catch (Exception e) {

                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    String token_id;


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: " + location.getLatitude() + "," + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        prefrences.edit().putString("latitude", Double.toString(latitude)).putString("longitude", Double.toString(longitude)).apply();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    double latitude, longitude;


    @Override
    protected void onResume() {
        super.onResume();
        checkRegID();

    }

    public void saveTokeinID(String refreshedToken) {
        SharedPreferences prefrences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefrences.getBoolean("reg", false)) {
            if (prefrences.getString("token_id", null) == null) {
                if (refreshedToken != null) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(Constants.DATABASE_PATH_UPLOADS);

                    //String name, String userid, String url, String phone, String email, String vehicle_id, String dob, String password)
                    User user = new User(prefrences.getString("name", null),
                            prefrences.getString("userid", null),
                            prefrences.getString("image", null),
                            prefrences.getString("phone", null),
                            prefrences.getString("email", null),
                            prefrences.getString("vehicle_no", null),
                            prefrences.getString("dob", null),
                            prefrences.getString("password", null),
                            refreshedToken, "user");

                    myRef.child("users").child(prefrences.getString("userid", null)).setValue(user);

                    prefrences.edit().putString("token_id", refreshedToken).commit();
                    Log.i(TAG, "saveTokeinID: token UPDdaated");
                }

            }
        }

//        } else {
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference(Constants.DATABASE_PATH_UPLOADS);
//
//            //String name, String userid, String url, String phone, String email, String vehicle_id, String dob, String password)
//            User user = new User(prefrences.getString("name", null),
//                    prefrences.getString("userid", null),
//                    prefrences.getString("image", null),
//                    prefrences.getString("phone", null),
//                    prefrences.getString("email", null),
//                    prefrences.getString("vehicle_no", null),
//                    prefrences.getString("dob", null),
//                    prefrences.getString("password", null), refreshedToken);
//            myRef.child("users").child(prefrences.getString("userid", null)).setValue(user);
//            prefrences.edit().putString("token_id", refreshedToken).commit();
//
//        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);

    }

    public void checkRegID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            navigationView.getMenu().findItem(R.id.nav_sign_in).setTitle(auth.getCurrentUser().getDisplayName());
            View hView = navigationView.getHeaderView(0);
            ImageView nav_user = (ImageView) hView.findViewById(R.id.imageView);
            TextView txtEmail = (TextView) hView.findViewById(R.id.txtEmail);
            TextView txtName = (TextView) hView.findViewById(R.id.txtName);
            txtEmail.setText(prefrences.getString("email", ""));
            txtName.setText(prefrences.getString("name", ""));
            try {
                //nav_user.setImageResource(R.mipmap.ic_launcher);
                Picasso.with(this).load(prefrences.getString("image", "")).into(nav_user);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (playServicesAvailable()) {
            if (prefrences.getString("token_id", null) == null) {
                Log.i(TAG, "onCreate: " + token_id);
                token_id = FirebaseInstanceId.getInstance().getToken();
                saveTokeinID(token_id);
            }
        } else {
            Log.i(TAG, "sendNotificationToUser: no play services");
            // ... log error, or handle gracefully
        }
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean playServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w("SA", "storage permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
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


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int RC_SIGN_IN = 123;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_in) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                item.setTitle(auth.getCurrentUser().getDisplayName());
                FirebaseUser user = auth.getCurrentUser();
                Intent intent1 = new Intent(getBaseContext(), SignedInActivity.class);
                startActivity(intent1);
                // already signed in
            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()


                                .setTosUrl("https://superapp.example.com/terms-of-service.html")
                                .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                                .setAvailableProviders(
                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),

                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

                                        ))
                                .build(),
                        RC_SIGN_IN);
                // not signed in
            }
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                prefrences.edit().clear().commit();
                finish();
                Toast.makeText(context, "Logout success", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_admin) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                Intent intent1 = new Intent(getBaseContext(), AdminPanel.class);

                startActivity(intent1);
            }
        } else if (id == R.id.nav_res) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                Intent intent1 = new Intent(getBaseContext(), ResLogin.class);

                startActivity(intent1);
            }
        }else if (id == R.id.nav_my_cart) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                Intent intent1 = new Intent(getBaseContext(), ListCartActivitiy.class);

                startActivity(intent1);
            }
        }else if (id == R.id.nav_current_orders) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                Intent intent1 = new Intent(getBaseContext(), ViewCurrentOrdersUser.class);

                startActivity(intent1);
            }
        }else if (id == R.id.nav_history) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                Intent intent1 = new Intent(getBaseContext(), ViewPastOrdersUser.class);

                startActivity(intent1);
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    String path;
    String TAG = "MAINACTIVITY";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "Sign up successfull", Toast.LENGTH_SHORT).show();


                Intent intent1 = new Intent(getBaseContext(), SignedInActivity.class);

                startActivity(intent1);

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    String receiver_email, receiver_token;

    public void showSnackbar(final int id) {
        Toast.makeText(getBaseContext(), id, Toast.LENGTH_SHORT).show();


    }


}
