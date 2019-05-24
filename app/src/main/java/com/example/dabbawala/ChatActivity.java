package com.example.dabbawala;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
Context context;

    Button mSendButton;


    EditText mMessageEdit;

    ArrayList<Chat> lstMessage = new ArrayList<>();
    ChatAdapter chatAdapter;
    TextView mEmptyListMessage;
   // FirebaseAuth auth;
    String TAG="CHAT";
    DatabaseReference mDatabaseReference;
    String fuserid,userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        //toolbar.setTitle("Chat");
        //setSupportActionBar(toolbar);
        context=this;
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        fuserid =getIntent().getStringExtra("fuserid");
        userid=getIntent().getStringExtra("userid");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        mRecyclerView = findViewById(R.id.messagesList);
        mRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
//        //linearLayoutManager.setStackFromEnd(false);
//        //linearLayoutManager.setStackFromEnd(true);
//       linearLayoutManager.setReverseLayout(true);
        LinearLayoutManager linearlayoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
       linearlayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearlayoutManager);

        mSendButton = findViewById(R.id.sendButton);
        mMessageEdit = findViewById(R.id.messageEdit);
        mEmptyListMessage = findViewById(R.id.emptyTextView);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

       // mRecyclerView.setAdapter(chatAdapter);

        com.example.dabbawala.ImeHelper.setImeOnDoneListener(mMessageEdit, new com.example.dabbawala.ImeHelper.DonePressedListener() {
            @Override
            public void onDonePressed() {
                onSendClick();
            }
        });
        mDatabaseReference.child(Constants.single_chat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstMessage.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String name = (String) dataSnapshot1.child("mName").getValue();
                    String message = (String) dataSnapshot1.child("mMessage").getValue();
                    String uid = (String) dataSnapshot1.child("mUid").getValue();//1
                    String toid = (String) dataSnapshot1.child("tUid").getValue();//2
                    String time = (String) dataSnapshot1.child("time").getValue();
                    // boolean isSender = (Boolean) dataSnapshot1.child("isSender").getValue();
                    Log.i(TAG, "onDataChange: uid"+uid);
                    Log.i(TAG, "onDataChange: toid"+toid);
                    Log.i(TAG, "onDataChange: gid=="+ fuserid);
                    if ((uid.equals(userid) && toid.equals(fuserid))
                            ||
                            (uid.equals(fuserid) && toid.equals(userid))) {
                        Log.i(TAG, "onDataChange: " + name + "mess" + message);
                        boolean sender=false;
                        if(uid.equals(userid)){
                            sender=true;
                        }

                        Chat chat = new Chat(name, message, uid, fuserid, time,null);
                        chat.setSender(sender);
                        lstMessage.add(chat);

                    }

                }
                attachRecyclerViewAdapter();

            }


        });
    }

    public void onSendClick(View v){
        if (FirebaseAuth.getInstance() != null) {
            String message=mMessageEdit.getText().toString();
            if(message.length()>0){
                String uid =userid;
                String name = "";
                onAddMessage(new Chat(name,message , uid, fuserid, Constants.getDateTime(),null));

                mMessageEdit.setText("");

            }

        } else {
            Toast.makeText(context, "Please login to send message", Toast.LENGTH_SHORT).show();
        }

    }
    public void onSendClick() {
        if (FirebaseAuth.getInstance() != null) {
            String message=mMessageEdit.getText().toString();
            if(message.length()>0){
                String uid =userid;
                String name ="";

                onAddMessage(new Chat(name,message , uid, fuserid, Constants.getDateTime(),null));

                mMessageEdit.setText("");

            }
        } else {
            Toast.makeText(context, "Please login to send message", Toast.LENGTH_SHORT).show();
        }

    }


    protected void onAddMessage(Chat chat) {
        mDatabaseReference.child(Constants.single_chat).getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                if (error != null) {
                    Log.e(TAG, "Failed to write message", error.toException());
                }
            }
        });
    }
    private void attachRecyclerViewAdapter() {

        chatAdapter = new ChatAdapter(lstMessage);
        mRecyclerView.setAdapter(chatAdapter);

        // Scroll to bottom on new messages
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
            }
        });
        //chatAdapter.notifyDataSetChanged();
//chatAdapter.notifyDataSetChanged();

            mEmptyListMessage.setVisibility(chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);


    }
}
