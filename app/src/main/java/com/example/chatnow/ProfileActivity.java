package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    EditText mviewusername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    TextView movetoupdateprofile;

    FirebaseFirestore firebaseFirestore;
    ImageView mviewuserimageinimage;
    EditText mphonenumber;

    StorageReference storageReference;

    private String ImageURIAccesstoken;

    androidx.appcompat.widget.Toolbar mtoolbarofprofile;
    ImageButton mbackbuttonviewprofile;
    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mviewuserimageinimage = findViewById(R.id.viewuserimageinimageview);
        mviewusername = findViewById(R.id.viewusername);
        movetoupdateprofile = findViewById(R.id.movetoupdateprofile);
        mphonenumber = findViewById(R.id.phonenumber);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mtoolbarofprofile = findViewById(R.id.toolbarofviewprofile);
        mbackbuttonviewprofile = findViewById(R.id.backbtnofviewprofile);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        setSupportActionBar(mtoolbarofprofile);

        mbackbuttonviewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Profile pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIAccesstoken = uri.toString();
                Picasso.get().load(uri).into(mviewuserimageinimage);
            }
        });


        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                mviewusername.setText(value.getString("name"));
                mphonenumber.setText(value.getString("phno"));
            }
        });
     /*   databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userprofile muserprofile = snapshot.getValue(userprofile.class);
                mviewusername.setText(muserprofile.getUsername());
                mphonenumber.setText(muserprofile.getPhonenumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
            }
        });
*/
        movetoupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,UpdateProfile.class);

                intent.putExtra("nameofuser",mviewusername.getText().toString());
                intent.putExtra("phno",mphonenumber.getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());

        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               // Toast.makeText(ProfileActivity.this, "Now user is offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(ProfileActivity.this, "Now user is online", Toast.LENGTH_SHORT).show();
            }
        });
    }
}