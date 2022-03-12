package com.example.chatnow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class statusfragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;

    ImageView mimageviewofuser;


    FirestoreRecyclerAdapter<firebasemodel, statusfragment.NoteViewholder> chatAdapter;


    RecyclerView mrecyclerview;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.statusfragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mrecyclerview = v.findViewById(R.id.recyclerviewofstatus);

        CollectionReference documentReference = firebaseFirestore.collection("Status");
        List<String> list ;



        Query query = firebaseFirestore.collection("Users").whereNotEqualTo("showoff","0");
        FirestoreRecyclerOptions<firebasemodel> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        chatAdapter = new FirestoreRecyclerAdapter<firebasemodel, statusfragment.NoteViewholder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull statusfragment.NoteViewholder noteViewholder, int i, @NonNull firebasemodel firebasemodel) {

                noteViewholder.particularusername.setText(firebasemodel.getName());
                String uri = firebasemodel.getImage();
                Picasso.get().load(uri).into(mimageviewofuser);

                noteViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(),ViewProfilePic.class);
                        //intent.putExtra("name",firebasemodel.getName());
                        intent.putExtra("uid",firebasemodel.getUid());
                        intent.putExtra("choice","2");
                       // intent.putExtra("imageuri",firebasemodel.getImage());
                       // intent.putExtra("phno",firebasemodel.getPhonenumber());
                        startActivity(intent);
                    }
                });


            }

            @NonNull
            @Override
            public statusfragment.NoteViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statuslayout,parent,false);
                return new NoteViewholder(view);
            }
        };


        //------------------------------------------------------------

        ///-------------------------------------------------------------
        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAdapter);

        return  v;


    }


    class NoteViewholder extends  RecyclerView.ViewHolder {

        private TextView particularusername;



        public  NoteViewholder(@NonNull View itemView){
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
        if (chatAdapter!=null)
        {
            chatAdapter.stopListening();
        }
    }
}
