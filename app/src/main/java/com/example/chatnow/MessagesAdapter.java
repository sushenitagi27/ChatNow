package com.example.chatnow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND = 1;
    int ITEM_RECEIVER = 2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewholder(view);
        }
        else
        {

            View view = LayoutInflater.from(context).inflate(R.layout.receiverchatlayout,parent,false);
            return new ReceiverViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);
        if(holder.getClass()==SenderViewholder.class)
        {
            SenderViewholder viewHolder = (SenderViewholder)holder;
            viewHolder.textviewmessage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());
        }
        else
        {
            ReceiverViewholder viewHolder = (ReceiverViewholder) holder;
            viewHolder.textviewmessage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());

        }

    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid()))
        {
            return ITEM_SEND;
        }
        else
        {
            return ITEM_RECEIVER;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    class  SenderViewholder extends RecyclerView.ViewHolder
    {

        TextView textviewmessage;
        TextView timeofmessage;
        public SenderViewholder(@NonNull View itemView) {
            super(itemView);
            textviewmessage = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }
    class  ReceiverViewholder extends RecyclerView.ViewHolder
    {

        TextView textviewmessage;
        TextView timeofmessage;
        public ReceiverViewholder(@NonNull View itemView) {
            super(itemView);
            textviewmessage = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }
}
