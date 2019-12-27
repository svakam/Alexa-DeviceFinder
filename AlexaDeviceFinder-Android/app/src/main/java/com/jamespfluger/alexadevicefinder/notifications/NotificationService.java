package com.jamespfluger.alexadevicefinder.notifications;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.jamespfluger.alexadevicefinder.R;
import com.jamespfluger.alexadevicefinder.activities.LaunchActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class NotificationService {
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    private final int NOTIFICATION_ID = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(new Date()));
    private final String CHANNEL_ID = "4096";

    public NotificationService(Context context){
        this.context = context;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
    }

    public void issueNotification(String title, String message){
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);

        setDeviceToMaxVolume();

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            createNotificationChannel("Device Alert (Required)");
        }

         notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(false)
                            .setSound(defaultRingtoneUri)
                            .setChannelId(CHANNEL_ID);

        notificationManager.notify(Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(new Date())), notificationBuilder.build());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r =  RingtoneManager.getRingtone(context, Uri.parse("android.resource://com.jamespfluger.alexadevicefinder/" + R.raw.alert));
        r.play();
    }

    @RequiresApi(api= Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelName){
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);

        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        channel.setSound(Uri.parse("android.resource://com.jamespfluger.alexadevicefinder/" + R.raw.alert), att);

        notificationBuilder.setChannelId(CHANNEL_ID);
        notificationManager.createNotificationChannel(channel);
    }

    private void setDeviceToMaxVolume(){
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_RING, manager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
    }
}
