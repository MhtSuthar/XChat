package com.chatmodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.chatmodule.R;
import com.chatmodule.fragment.ChatDetailFragment;
import com.chatmodule.fragment.ChatListFragment;

/**
 * Created by mht on 20/9/16.
 */
public class ChatDetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_frame);

        replace(new ChatDetailFragment());
    }

    void replace(Fragment fragment){
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, fragment, fragment.getTag()).commit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
