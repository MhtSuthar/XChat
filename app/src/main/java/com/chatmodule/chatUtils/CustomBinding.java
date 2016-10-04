package com.chatmodule.chatUtils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mht on 24/9/16.
 */

public class CustomBinding {

    @BindingAdapter({"time"})
    public static void setTime(TextView txt, long time){
        txt.setText(ChatUtils.getTime(txt.getContext(), time));
    }
}
