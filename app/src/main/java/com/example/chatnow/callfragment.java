package com.example.chatnow;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

public class callfragment extends Fragment {


    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    private int PERMISSION_CALL = 1;

    ImageView mimageviewofuser;


    FirestoreRecyclerAdapter<firebasemodel, callfragment.NoteViewholder> chatAdapter;


    RecyclerView mrecyclerview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.callfragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mrecyclerview = v.findViewById(R.id.recyclerviewofcall);


        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.getUid());
        FirestoreRecyclerOptions<firebasemodel> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<firebasemodel, callfragment.NoteViewholder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull callfragment.NoteViewholder noteViewholder, int i, @NonNull firebasemodel firebasemodel) {

                noteViewholder.particularusername.setText(firebasemodel.getName());
                String uri = firebasemodel.getImage();
                Picasso.get().load(uri).into(mimageviewofuser);

                noteViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      //  Toast.makeText(getActivity(), phonenumber, Toast.LENGTH_SHORT).show();

                        Intent intentnxt = new Intent(getActivity(),viewuser.class);
                        intentnxt.putExtra("uid",firebasemodel.getUid());
                        startActivity(intentnxt);

                    }
                });


            }

            @NonNull
            @Override
            public callfragment.NoteViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statuslayout, parent, false);
                return new callfragment.NoteViewholder(view);
            }
        };


        //------------------------------------------------------------

        ///-------------------------------------------------------------
        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAdapter);

        return v;


    }


    class NoteViewholder extends RecyclerView.ViewHolder {

        private TextView particularusername;


        public NoteViewholder(@NonNull View itemView) {
            super(itemView);

            particularusername = itemView.findViewById(R.id.nameofuserstatus);

            mimageviewofuser = itemView.findViewById(R.id.imageviewofuserstatus);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
    }


}

