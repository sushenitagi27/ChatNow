package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Authentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfile extends AppCompatActivity {


    private CardView mgetuserimage;
    private ImageView mgetuserimageinimageview;
    private  static  int pick_image=123;
    private Uri imagepath;

    private EditText mgetusername;

    private android.widget.Button msaveprofile;

    private FirebaseAuth firebaseAuth;
    private String name;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageAccessToken;

    private FirebaseFirestore firebaseFirestore;

    Intent intent;
    String PhoneNumber;
    ProgressBar mprogressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mgetusername = findViewById(R.id.getusername);
        mgetuserimage = findViewById(R.id.getimage);
        mgetuserimageinimageview = findViewById(R.id.getuserimageinsideimageview);

        msaveprofile = findViewById(R.id.saveProfile);
        mprogressbar = findViewById(R.id.progressbarinfo_setprofile);

        intent = getIntent();
        PhoneNumber= intent.getStringExtra("phno");
        mgetuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,pick_image);
            }
        });

        msaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=mgetusername.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(SetProfile.this, "Name is empty", Toast.LENGTH_SHORT).show();
                }
                else if(imagepath==null)
                {
                    Toast.makeText(SetProfile.this, "Image is empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mprogressbar.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mprogressbar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SetProfile.this,chat.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    private void sendDataForNewUser()
    {
        sendDatatorealtimedatabase();

    }
    private void sendDatatorealtimedatabase()
    {

        name = mgetusername.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        userprofile userprofile = new userprofile(name,firebaseAuth.getUid(),PhoneNumber);
        databaseReference.setValue(userprofile);
        Toast.makeText(this, "User addded Successfully", Toast.LENGTH_SHORT).show();
        sendImagetoStorage();


    }
    private  void sendImagetoStorage()
    {
        StorageReference imageref = storageReference.child(firebaseAuth.getUid()).child("Profile pic");

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ByteArrayOutputStream bcyteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,bcyteArrayOutputStream);
        byte[] data = bcyteArrayOutputStream.toByteArray();

        // putting image

        UploadTask uploadTask = imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageAccessToken = uri.toString();
                      //  Toast.makeText(SetProfile.this, "URI get Success", Toast.LENGTH_SHORT).show();

                        sendDatatocloudFirestore();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Toast.makeText(SetProfile.this, "Uri got Failed", Toast.LENGTH_SHORT).show();
                    }
                });
               // Toast.makeText(SetProfile.this, "Image is uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              //  Toast.makeText(SetProfile.this, "Image not uploaded ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendDatatocloudFirestore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());

        Map<String , Object> userdata = new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",ImageAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");
        userdata.put("phno",PhoneNumber);

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SetProfile.this, "Data on cloud firestore Success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Intent intent3 = new Intent(SetProfile.this, MainActivity.class);
                startActivity(intent3);
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == pick_image && resultCode == RESULT_OK) {
            imagepath = data.getData();
            mgetuserimageinimageview.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}

