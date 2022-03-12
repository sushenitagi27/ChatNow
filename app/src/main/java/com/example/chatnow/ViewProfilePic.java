package com.example.chatnow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewProfilePic extends AppCompatActivity {

    ImageButton mbackbtnofviewprofilepic;
    ImageView mprofilepic;
    Intent intent;
    String userid;
    EditText mtextofstatus;
    ImageView msinglestatusimage,mcommitedstatusimage;

    ProgressBar mprogressbarofviewprofilepic;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String ImageURIAccesstoken;
    FirebaseFirestore firebaseFirestore;
    TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_pic);

        mbackbtnofviewprofilepic=findViewById(R.id.backbtnofviewprofilepic);
        mprofilepic = findViewById(R.id.profilepic);
        mtextofstatus =findViewById(R.id.textofStatus);
        msinglestatusimage = findViewById(R.id.singlestatusimage);
        mcommitedstatusimage = findViewById(R.id.commitedstatusimage);
        mprogressbarofviewprofilepic =findViewById(R.id.progrssbarofprofilepic);
        heading = findViewById(R.id.myapptext);
        intent = getIntent();
        userid = intent.getStringExtra("uid");
        String choice = intent.getStringExtra("choice");


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        if(choice.contentEquals("1"))
        {
            mprogressbarofviewprofilepic.setVisibility(View.VISIBLE);
            storageReference.child(userid).child("Profile pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageURIAccesstoken = uri.toString();
                    Picasso.get().load(uri).into(mprofilepic);
                }
            });
            mprogressbarofviewprofilepic.setVisibility(View.INVISIBLE);
        }
        else
            {
                heading.setText("User Status");
                mprogressbarofviewprofilepic.setVisibility(View.VISIBLE);
                firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection("Status").document(userid);

                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String txt = value.getString("txt");
                        if(!txt.isEmpty())
                        {
                            mtextofstatus.setVisibility(View.VISIBLE);
                            mtextofstatus.setText(value.getString("txt"));
                        }
                        String s = value.getString("status");
                        if(s.contentEquals("1"))
                        {
                            msinglestatusimage.setVisibility(View.VISIBLE);
                        }
                        else if(s.contentEquals("2"))
                        {
                            mcommitedstatusimage.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mcommitedstatusimage.setVisibility(View.INVISIBLE);
                            msinglestatusimage.setVisibility(View.INVISIBLE);
                        }
                        Picasso.get().load(Uri.parse( value.getString("image"))).into(mprofilepic);
                        mprogressbarofviewprofilepic.setVisibility(View.INVISIBLE);
                    }
                });


            }



        mbackbtnofviewprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

}