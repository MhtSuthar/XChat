package com.chatmodule.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chatmodule.activity.ChatDetailActivity;
import com.chatmodule.R;
import com.chatmodule.activity.ChatListActivity;
import com.chatmodule.adapter.ChatListRecyclerAdapter;
import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.chatUtils.RecyclerTouchListener;
import com.chatmodule.databinding.FragmentChatListBinding;
import com.chatmodule.model.ChatListModel;
import com.chatmodule.model.ChatMessage;
import com.chatmodule.storage.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mht on 20/9/16.
 */
public class ChatListFragment extends Fragment {

    private static final String TAG = "ChatListFragment";
    private FragmentChatListBinding binding;
    private ChatListRecyclerAdapter chatListRecyclerAdapter;
    private String MESSAGE_RECEIVE_BROADCAST = "msg_receive_broadcast";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, null, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(((ChatListActivity)getActivity()) != null)
                    ((ChatListActivity)getActivity()).setOnlineStatus();
            }
        }, 3000);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAdapter();

        addTouchRecyclerItem();

        receiveBroadcastMsg();
    }

    void setAdapter(){
        chatListRecyclerAdapter = new ChatListRecyclerAdapter(getContext(), getChatList());
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(chatListRecyclerAdapter);
    }

    void addTouchRecyclerItem(){
        binding.recyclerView.addOnItemTouchListener(new RecyclerTouchListener(binding.recyclerView, new RecyclerTouchListener.OnRecyclerClickListener() {
            @Override
            public void onClick(View v, int position) {
                onItemClick(position);
            }
            @Override
            public void onLongClick(View v, int position) {

            }
        }));
    }

    private void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
        getActivity().startActivityForResult(intent, ChatUtils.REQUEST_CHAT_LIST);
    }

    /**
     *
     * @return getChatList Replace by chat main list
     */
    private List<ChatListModel> getChatList() {
        List<ChatListModel> list = new ArrayList<>();

        ChatListModel chatListModel = new ChatListModel();
        chatListModel.setName(SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, ""));
        list.add(chatListModel);

        return list;
    }

    public void refreshAdapter() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                chatListRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    //TODo Add pause and resume to start and stop receiver
    void receiveBroadcastMsg(){
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(MESSAGE_RECEIVE_BROADCAST));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("chatState") != null){

            }else if(intent.getSerializableExtra("chat") != null){
                refreshAdapter();
            }
        }
    };
}
