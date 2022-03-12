package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class specificchat extends AppCompatActivity {

    EditText mgetmessaege;
    ImageButton msendmessage;

    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecifichat;
    ImageView mimageviewofspecificuser;
    TextView mnameofspecificuser;

    private String enteredmessage;
    Intent intent;
    String mreceivername,sendername,mreceiveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    String senderroom,receiverroom;
    String PhoneNumber;

    ImageButton mbackbtnofspecificchat;
    RecyclerView mmessagerecycler;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;


   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);

        mgetmessaege=findViewById(R.id.getmessage);
        msendmessagecardview = findViewById(R.id.cardviewofsendmessage);
        msendmessage = findViewById(R.id.imageviewofsendmessage);
        mtoolbarofspecifichat=findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser = findViewById(R.id.nameofspecificuser);
        mimageviewofspecificuser = findViewById(R.id.specificuserimageinimageview);
        mbackbtnofspecificchat = findViewById(R.id.backbtnofspecificchat);

        messagesArrayList = new ArrayList<>();
        mmessagerecycler = findViewById(R.id.recyclerviewofspecificchat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecycler.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(specificchat.this,messagesArrayList);
        mmessagerecycler.setAdapter(messagesAdapter);
        intent = getIntent();
       firebaseAuth = FirebaseAuth.getInstance();
       firebaseDatabase = FirebaseDatabase.getInstance();
       calendar = Calendar.getInstance();
       simpleDateFormat = new SimpleDateFormat("hh:mm a");

       msenderuid = firebaseAuth.getUid();
       mreceiveruid =  getIntent().getStringExtra("receiveruid");
       mreceivername = getIntent().getStringExtra("name");
       PhoneNumber = getIntent().getStringExtra("phno");


       setSupportActionBar(mtoolbarofspecifichat);
        mtoolbarofspecifichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Toast.makeText(specificchat.this, "Tool bar is clicked", Toast.LENGTH_SHORT).show();
                Intent intentnxt = new Intent(specificchat.this,viewuser.class);
                intentnxt.putExtra("uid",mreceiveruid);
                intentnxt.putExtra("phno",PhoneNumber);

                startActivity(intentnxt);


            }
        });


       senderroom = msenderuid+mreceiveruid;
       receiverroom = mreceiveruid+msenderuid;


       DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messagesAdapter = new MessagesAdapter(specificchat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        mbackbtnofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mnameofspecificuser.setText(mreceivername);
        String uri = intent.getStringExtra("imageuri");
        if(uri.isEmpty())
        {
            Toast.makeText(this, "Null is received", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Picasso.get().load(uri).into(mimageviewofspecificuser);
        }


        msendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage = mgetmessaege.getText().toString();
                if(enteredmessage.isEmpty())
                {
                    Toast.makeText(specificchat.this, "Enter message first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Date date = new Date();
                    currenttime= simpleDateFormat.format(calendar.getTime());
                    Messages messages = new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference()
                                    .child("chats")
                                    .child(receiverroom)
                                    .child("messages")
                                    .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });
                    mgetmessaege.setText(null);


                }
            }
        });

    }
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }
    }
}