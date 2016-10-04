package com.chatmodule.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatmodule.R;
import com.chatmodule.databinding.ListItemChatDetailBinding;
import com.chatmodule.model.ChatMessage;

import java.util.List;

/**
 * Created by ubuntu on 19/4/16.
 */
public class ChatDetailRecyclerAdapter extends RecyclerView.Adapter<ChatDetailRecyclerAdapter.ViewHolder> {

    private List<ChatMessage> mListPatient;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ListItemChatDetailBinding binding;

        public ViewHolder(View rowView) {
            super(rowView);
            binding = DataBindingUtil.bind(rowView);
        }
        public ListItemChatDetailBinding getBinding() {
            return binding;
        }
    }

    public ChatDetailRecyclerAdapter(Context context, List<ChatMessage> mListPatient) {
        this.mListPatient = mListPatient;
        mContext = context;
    }


    @Override
    public ChatDetailRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat_detail, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatMessage interestModel = mListPatient.get(position);
        holder.getBinding().setChatDetail(interestModel);
        if(interestModel.msg_status.equals("-1")){
            //TODO change image pending like
            holder.getBinding().imgMsgStatus.setImageResource(R.drawable.send_check);
        } else if(interestModel.msg_status.equals("0")){
            holder.getBinding().imgMsgStatus.setImageResource(R.drawable.send_check);
        }else if(interestModel.msg_status.equals("1")){
            holder.getBinding().imgMsgStatus.setImageResource(R.drawable.delivered_check);
        }else if(interestModel.msg_status.equals("2")){
            holder.getBinding().imgMsgStatus.setImageResource(R.drawable.read_check);
        }
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mListPatient.size();
    }

}
