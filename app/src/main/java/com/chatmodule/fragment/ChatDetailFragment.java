package com.chatmodule.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chatmodule.activity.ChatListActivity;
import com.chatmodule.R;
import com.chatmodule.adapter.ChatDetailRecyclerAdapter;
import com.chatmodule.chatUtils.ChatUtils;
import com.chatmodule.databinding.FragmentChatDetailBinding;
import com.chatmodule.model.ChatMessage;
import com.chatmodule.storage.ChatConversationHandler;
import com.chatmodule.storage.SharedPreferenceUtil;
import com.chatmodule.xmpp.readMsg.ReadReceipt;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mht on 20/9/16.
 */
public class ChatDetailFragment extends Fragment {

    private FragmentChatDetailBinding binding;
    private ChatDetailRecyclerAdapter chatDetailRecyclerAdapter;
    private List<ChatMessage> list = new ArrayList<>();
    private ChatConversationHandler mChatConversationHandler;
    private String MESSAGE_RECEIVE_BROADCAST = "msg_receive_broadcast";
    private Random random;
    private static final String TAG = "ChatDetailFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_detail, null, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
        ((AppCompatActivity) getActivity()).setTitle("");

        mChatConversationHandler = new ChatConversationHandler();

        setAdapter();

        sendReadStatus();

        binding.imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.edtMsg.getText().toString().trim().equals("")){
                    sendTextMessage(binding.edtMsg.getText().toString());
                }
            }
        });

        binding.edtMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!ChatListActivity.getmService().xmpp.connection.isConnected()){
                    ChatListActivity.getmService().xmpp.connect("onCreate");
                }else
                    ChatListActivity.getmService().xmpp.setCurrentState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        receiveBroadcastMsg();
    }

    private void sendReadStatus() {
        ArrayList<ChatMessage> mList = mChatConversationHandler.getChatMessages(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "")+"@"+SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, ""));
        for (int i = 0; i < mList.size(); i++) {
            if(!mList.get(i).isRead) {
                Message message = new Message(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, ""));
                ReadReceipt read = new ReadReceipt(mList.get(i).msgid);
                //ChatListActivity.getmService().xmpp.setMsgReadState();
                message.addExtension(read);
                try {
                    ChatListActivity.getmService().xmpp.connection.sendPacket(message);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Update Read Message
         *
         * */
        mChatConversationHandler.updateChatMsgAsRead(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "")+"@"+SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, ""));
    }

    //52#(+)#9992221111  for sender
    //53#(+)#7778889999  for receiver
    public void sendTextMessage(String msg) {
        random = new Random();

        final ChatMessage chatMessage = new ChatMessage(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, ""),
                SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, ""),
                msg, "" + random.nextInt(1000), true);
        chatMessage.setMsgID();
        chatMessage.isMine = true;
        chatMessage.body = msg;
        chatMessage.Date = ChatUtils.getCurrentDate();
        chatMessage.TimeMill =  ChatUtils.getTimeStamp();
        chatMessage.isRead = true;
        chatMessage.MsgType = "0";
        chatMessage.msg_status = ChatUtils.isOnline(getContext()) ? "0" : "-1";
        //Chat Uniq Id
        chatMessage.chat_id = SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "")+"@"+SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, "");
        //Chat Uniq Id
        chatMessage.senderName = "Sender name";
        list.add(chatMessage);
        mChatConversationHandler.addMsg(chatMessage);
        chatDetailRecyclerAdapter.notifyItemInserted(list.size());
        binding.edtMsg.setText("");
        binding.recyclerView.scrollToPosition(list.size() - 1);
        ChatListActivity.getmService().xmpp.sendMessage(chatMessage);
    }


    void setAdapter(){
        chatDetailRecyclerAdapter = new ChatDetailRecyclerAdapter(getContext(), getChatList());
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemAnimator itemAnimator = binding.recyclerView.getItemAnimator();
        itemAnimator.setAddDuration(300);
        itemAnimator.setRemoveDuration(300);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setAdapter(chatDetailRecyclerAdapter);
        binding.recyclerView.scrollToPosition(list.size()-1);
    }

    /**
     *
     * @return getChatList Replace by chat detail list
     */
    private List<ChatMessage> getChatList() {
        list.addAll(mChatConversationHandler.getAllMessage());
        return list;
    }

    public void refreshAdapter() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                chatDetailRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    void receiveBroadcastMsg(){
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(MESSAGE_RECEIVE_BROADCAST));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("chatState") != null){
                /**
                 * Got Typing Status
                 */
                 setTyping(intent);
                //Toast.makeText(getContext(), intent.getStringExtra("chatState"), Toast.LENGTH_SHORT).show();
            }else if(intent.getBooleanExtra(ChatUtils.EXTRA_MSG_DELIVERED, false)){
                /**
                 * Got Deliverd and Update Deliverd Message
                 */
                changeMsgStatusAndRefresh(intent);
            }else if(intent.getSerializableExtra("chat") != null){

                binding.includeToolbar.toolbarSubTitle.setVisibility(View.GONE);
                binding.includeToolbar.toolbarSubTitle.setText("");

                ChatMessage message = (ChatMessage) intent.getSerializableExtra("chat");
                list.add(message);
                chatDetailRecyclerAdapter.notifyItemInserted(list.size());
                binding.recyclerView.scrollToPosition(list.size() - 1);
                /**
                 * Update Read Message
                 */
                mChatConversationHandler.updateChatMsgAsRead(SharedPreferenceUtil.getString(ChatUtils.XMPP_USERNAME, "")+"@"+SharedPreferenceUtil.getString(ChatUtils.XMPP_RECEIVER, ""));
            }
        }
    };


    private void setTyping(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //TransitionManager.beginDelayedTransition(binding.includeToolbar.linToolbar);
        }
        if(intent.getStringExtra("chatState").contains("typing")){
            binding.includeToolbar.toolbarSubTitle.setVisibility(View.VISIBLE);
            binding.includeToolbar.toolbarSubTitle.setText("Typing...");
        }else{
            binding.includeToolbar.toolbarSubTitle.setVisibility(View.GONE);
            binding.includeToolbar.toolbarSubTitle.setText("");
        }
    }

    void changeMsgStatusAndRefresh(Intent intent){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).msgid.equals(intent.getStringExtra(ChatUtils.EXTRA_MSG_ID))){
                list.get(i).msg_status = "1";
            }
        }
        refreshAdapter();
    }
}
