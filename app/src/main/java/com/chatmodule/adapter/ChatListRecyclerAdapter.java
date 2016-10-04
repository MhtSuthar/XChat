package com.chatmodule.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.chatmodule.R;
import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.chatUtils.CircleTransform;
import com.chatmodule.databinding.ListItemChatBinding;
import com.chatmodule.model.ChatListModel;
import com.chatmodule.storage.ChatConversationHandler;
import com.chatmodule.storage.SharedPreferenceUtil;

import java.util.List;

/**
 * Created by ubuntu on 19/4/16.
 */
public class ChatListRecyclerAdapter extends RecyclerView.Adapter<ChatListRecyclerAdapter.ViewHolder> {

    private static final int ANIM_DURATION = 200;
    private List<ChatListModel> mListPatient;
    private Context mContext;
    private int lastPos = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ListItemChatBinding binding;

        public ViewHolder(View rowView) {
            super(rowView);
            binding = DataBindingUtil.bind(rowView);
        }
        public ListItemChatBinding getBinding() {
            return binding;
        }
    }

    public ChatListRecyclerAdapter(Context context, List<ChatListModel> mListPatient) {
        this.mListPatient = mListPatient;
        mContext = context;
    }


    @Override
    public ChatListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatListModel interestModel = mListPatient.get(position);
        holder.getBinding().setChatListModel(interestModel);
        Glide.with(mContext).load(R.drawable.ic_dummy ).
                transform(new CircleTransform(mContext)).into(holder.getBinding().imgUser);
        int unreadCount = new ChatConversationHandler().getUnreadMsgCount(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "")+"@"+SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, ""));
        if(unreadCount > 0){
            holder.getBinding().txtUnreadMsg.setVisibility(View.VISIBLE);
            holder.getBinding().txtUnreadMsg.setText(""+unreadCount);
        }else{
            holder.getBinding().txtUnreadMsg.setVisibility(View.GONE);
        }
        holder.getBinding().executePendingBindings();
        animateStackByStack(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mListPatient.size();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    private void animateStackByStack(View view, final int pos) {
        if(pos > lastPos) {
            view.animate().cancel();
            view.setTranslationY(100);
            view.setAlpha(0);
            view.animate().alpha(1.0f).translationY(0).setDuration(ANIM_DURATION).setStartDelay(100);
            lastPos = pos;
        }
    }

}
