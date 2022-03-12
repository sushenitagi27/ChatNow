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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddStatus extends AppCompatActivity {

    ImageView imageView;
    ImageButton mbackbutton;
    CheckBox msingle,mcommited;
    EditText mstatustext;
    ImageButton send;
    int pick_image=13;
    String s="0";
    String text;

    private FirebaseAuth firebaseAuth;


    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageAccessToken;

    private FirebaseFirestore firebaseFirestore;

    private Uri imagepath;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_status);
        imageView = findViewById(R.id.click);
        mbackbutton = findViewById(R.id.backbtnofaddstatus);
        msingle = findViewById(R.id.single);
        mcommited = findViewById(R.id.commited);
        send = findViewById(R.id.sendstatus);
        mstatustext = findViewById(R.id.statustext);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,pick_image);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (msingle.isChecked())
                {
                    s="1";
                }
                else if(mcommited.isChecked())
                {
                    s="2";
                }
                else
                {
                    Toast.makeText(AddStatus.this, "First decide your personal Status before Uploading Your Status", Toast.LENGTH_SHORT).show();
                    return;
                }

                    sendDataForNewUser();

                finish();


            }
        });

    }
    private void sendDataForNewUser()
    {
        sendDatatorealtimedatabase();

    }
    private void sendDatatorealtimedatabase()
    {

        text = mstatustext.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        userprofile userprofile = new userprofile(text,firebaseAuth.getUid());
        databaseReference.setValue(userprofile);
        //Toast.makeText(this, "User addded Successfully", Toast.LENGTH_SHORT).show();
        sendImagetoStorage();


    }
    private  void sendImagetoStorage()
    {
        StorageReference imageref = storageReference.child(firebaseAuth.getUid()).child("Status pic");

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
                     //   Toast.makeText(AddStatus.this, "URI get Success", Toast.LENGTH_SHORT).show();

                        sendDatatocloudFirestore();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddStatus.this, "Uri got Failed", Toast.LENGTH_SHORT).show();
                    }
                });
               // Toast.makeText(AddStatus.this, "Image is uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStatus.this, "Image not uploaded ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendDatatocloudFirestore() {


        DocumentReference documentReference = firebaseFirestore.collection("Status").document(firebaseAuth.getUid());
        Map<String , Object> userdata = new HashMap<>();
        userdata.put("txt",text);
        userdata.put("image",ImageAccessToken);
        userdata.put("status",s);
        userdata.put("uid",firebaseAuth.getUid());
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddStatus.this, "Status Uploaded", Toast.LENGTH_SHORT).show();

            }
        });

        DocumentReference documentReference1 =firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference1.update("showoff","1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //    Toast.makeText(getApplicationContext(),"Now User is Online",Toast.LENGTH_SHORT).show();
            }
        });
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == pick_image && resultCode == RESULT_OK) {
            imagepath = data.getData();
            imageView.setImageURI(imagepath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}