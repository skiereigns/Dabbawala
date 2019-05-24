package com.example.dabbawala;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by inspirin on 10/16/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    String TAG = "FIS";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


        //prefrences.edit().putString("token_id",refreshedToken).commit();
        saveTokeinID(refreshedToken);
    }
    public void saveTokeinID(String refreshedToken){
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
//                    prefrences.getString("password", null), refreshedToken,"user");
//            myRef.child("users").child(prefrences.getString("userid", null)).setValue(user);
//            prefrences.edit().putString("token_id", refreshedToken).commit();
//
//        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);

    }
}
