package com.example.chatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class chat extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem mchat,mcall,mstatus;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    androidx.appcompat.widget.Toolbar mtoolbar;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tabLayout = findViewById(R.id.include);
        mchat = findViewById(R.id.chat);
        mcall = findViewById(R.id.call);
        mstatus = findViewById(R.id.status);
        viewPager = findViewById(R.id.fragmentcontainer);

        mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        Drawable drawable= ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_more_vert_24);
        mtoolbar.setOverflowIcon(drawable);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition()==0 || tab.getPosition()==1 || tab.getPosition()==2)
                {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.profile :
                Intent intent = new Intent(chat.this,ProfileActivity.class);
                startActivity(intent);
                break;

            case  R.id.addStatus:
                Intent intent1 = new Intent(chat.this,AddStatus.class);
                startActivity(intent1);
                break;

            case R.id.deleteStatus:
                DocumentReference documentReference = firebaseFirestore.collection("Status").document(firebaseAuth.getUid());
                Map<String , Object> userdata = new HashMap<>();
                userdata.put("txt","");
                userdata.put("image","");
                userdata.put("status","");
                userdata.put("uid",firebaseAuth.getUid());
                documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(chat.this, "Deleted", Toast.LENGTH_SHORT).show();

                    }
                });
                DocumentReference documentReference1=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
                documentReference1.update("showoff","0").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //    Toast.makeText(getApplicationContext(),"Now User is Online",Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case R.id.Settings :
               // Toast.makeText(this, "Settings is Clicked", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
             //   Toast.makeText(getApplicationContext(),"Now User is Offline",Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            //    Toast.makeText(getApplicationContext(),"Now User is Online",Toast.LENGTH_SHORT).show();
            }
        });

    }

}