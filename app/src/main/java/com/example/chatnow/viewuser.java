package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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

import java.security.Permission;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.P;

public class viewuser extends AppCompatActivity {

    private int PERMISSION_CALL=1;
    EditText mviewusername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    FirebaseFirestore firebaseFirestore;
    ImageView mviewuserimageinimage;

    StorageReference storageReference;
    String PhoneNumber;

    private String ImageURIAccesstoken;

    Intent intent;
    EditText phonenumberofviewuser;
    ImageButton imageButton;

    androidx.appcompat.widget.Toolbar mtoolbarofprofile;
    ImageButton mbackbuttonviewprofile;
    FirebaseStorage firebaseStorage;

    String userid;


    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewuser);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                createpersonalisedadd();
            }
        });



        mviewuserimageinimage = findViewById(R.id.viewuserimageinimageviewseenbyothers);
        mviewusername = findViewById(R.id.viewusernameseenbythers);
        imageButton = findViewById(R.id.logoofphoneuserviewprofile);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mtoolbarofprofile = findViewById(R.id.toolbarofviewuserprofile);
        mbackbuttonviewprofile = findViewById(R.id.backbtnofuserviewprofile);
        phonenumberofviewuser = findViewById(R.id.phonenumberseenbyothers);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        setSupportActionBar(mtoolbarofprofile);
        intent= getIntent();
         userid=intent.getStringExtra("uid");
       // PhoneNumber = intent.getStringExtra("phno");


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(viewuser.this, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED)
                {
                    String dial = "tel:"+phonenumberofviewuser.getText().toString();
                    startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
                }
                else {
                    requestcallpermission();
                }

            }
        });



        mviewuserimageinimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(viewuser.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");

                    Intent intent =(new Intent(viewuser.this,ViewProfilePic.class));
                    intent.putExtra("uid",userid);
                    intent.putExtra("choice","1");
                    startActivity(intent);
                }



            }
        });




        mbackbuttonviewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        storageReference = firebaseStorage.getReference();
        storageReference.child(userid).child("Profile pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIAccesstoken = uri.toString();
                Picasso.get().load(uri).into(mviewuserimageinimage);
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(userid);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String txt = value.getString("txt");
                mviewusername.setText(value.getString("name"));
                phonenumberofviewuser.setText(value.getString("phno"));
            }
        });


    }

    private void createpersonalisedadd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        createinterstialadd(adRequest);

    }

    private  void createinterstialadd(AdRequest adRequest)
    {
        InterstitialAd.load(this,"ca-app-pub-8858964509845968/7470334592", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                Intent intent =(new Intent(viewuser.this,ViewProfilePic.class));
                                intent.putExtra("uid",userid);
                                intent.putExtra("choice","1");
                                startActivity(intent);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }
    private void requestcallpermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(viewuser.this,Manifest.permission.CALL_PHONE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Call Permission needed")
                    .setMessage("This permission is necessary to make a call")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(viewuser.this,new String[] {Manifest.permission.CALL_PHONE},PERMISSION_CALL);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CALL_PHONE},PERMISSION_CALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Call Cannot be placed", Toast.LENGTH_SHORT).show();
            }
        }
        
    }
}