package com.chatmodule.chatUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chatmodule.R;
import com.chatmodule.activity.ChatListActivity;
import com.chatmodule.model.ChatMessage;
import com.chatmodule.storage.ChatConversationHandler;

import java.util.ArrayList;

/**
 * Created by mht on 28/9/16.
 */

public class NotificationUtils {

    public NotificationUtils() {

    }

    public void generateNotification(Context context) {

        //Log.e("newList", "newList --> " + newList.size());

        ArrayList<ChatMessage> mUnreadList = new ChatConversationHandler().getUnreadMessages();

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < mUnreadList.size(); i++) {
            list.add(mUnreadList.get(i).body);
        }

        StringBuilder builder = new StringBuilder();
        for (String value : list) {
            builder.append(value);
        }
        String text = builder.toString();


        String summaryText = "";
        String chat = "chat";
        if (list.size() > 1) {
            chat = "chats";
        }
        /*if (list.size() == 1) {
            summaryText = list.size() + " message from " + chat;
        } else {
            summaryText = list.size() + " messages from " + chat;
        }*/
        summaryText = list.size() + " message";


        Intent resultIntent = new Intent(context, ChatListActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("chatNotification", true);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        if (list.size() > 0) {
            Bitmap icon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_smily_face);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setSummaryText(summaryText);
            // Sets a title for the Inbox style big view
            inboxStyle.setBigContentTitle(context.getString(R.string.app_name));
            // Moves events into the big view
            for (int i = 0; i < list.size(); i++) {
                inboxStyle.addLine(list.get(i));
            }
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(summaryText)
                    .setSmallIcon(getNotificationIcon())
                    .setLargeIcon(icon1)
                    .setAutoCancel(true)
                    .setStyle(inboxStyle)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND).build();
            mNotificationManager.notify(100, notification);
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(100);
        }
    }

    public int getNotificationIcon() {
        boolean whiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_smily_face : R.drawable.ic_smily_face;
    }
}
