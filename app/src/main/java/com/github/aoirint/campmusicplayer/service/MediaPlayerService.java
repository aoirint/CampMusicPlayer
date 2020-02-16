package com.github.aoirint.campmusicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.Nullable;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.music.MusicPlayer;
import com.github.aoirint.campmusicplayer.util.BitmapUtil;

import java.io.IOException;

public class MediaPlayerService extends Service {
    Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();

        messenger = new Messenger(new MediaPlayerHandler());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("play");
        intentFilter.addAction("pause");
        intentFilter.addAction("previous");
        intentFilter.addAction("next");
        registerReceiver(new MediaPlayerBroadcastReceiver(), intentFilter);

        updateNotification();
    }

    void updateNotification() {
        String channelId = "default";
        Context context = getApplicationContext();
        String channelTitle = context.getString(R.string.app_name);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);

            builder = new Notification.Builder(this, channelId);
        }
        else {
            builder = new Notification.Builder(this);
        }

        CampMusicPlayer app = (CampMusicPlayer) getApplication();
        MusicPlayer musicPlayer = app.musicPlayer;

        PendingIntent mainActivityIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        PendingIntent prevIntent = PendingIntent.getBroadcast(getApplicationContext(), 3, new Intent("previous"), 0);
        PendingIntent nextIntent = PendingIntent.getBroadcast(getApplicationContext(), 4, new Intent("next"), 0);

        Music music = musicPlayer.getCurrentMusic();
        String title = "";
        String description = "";
        Bitmap artwork = null;
        int color = 0xFFFFFFFF;
        if (music != null) {
            title = music.title;
            description = music.album.name + "-" + music.album.artist.name;

            try {
                artwork = app.artworkCacheManager.loadOrCreate(music.getUri());
                // color = BitmapUtil.calcAverage(artwork);
                // TODO: save in database
                color = BitmapUtil.calcCommon(artwork);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Notification.Action prevAction = new Notification.Action(R.drawable.ic_skip_previous_7f7f7f_32dp, "Previous", prevIntent);
        Notification.Action nextAction = new Notification.Action(R.drawable.ic_skip_next_7f7f7f_32dp, "Next", nextIntent);
        Notification.Action playAction;
        if (musicPlayer.isPlaying()) {
            PendingIntent pauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, new Intent("pause"), 0);
            playAction = new Notification.Action(R.drawable.ic_pause_7f7f7f_32dp, "Pause", pauseIntent);
        }
        else {
            PendingIntent playIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent("play"), 0);
            playAction = new Notification.Action(R.drawable.ic_play_arrow_white_32dp, "Play", playIntent);
        }
        Notification.MediaStyle style = new Notification.MediaStyle();

        builder.setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_music_note_7f7f7f_32dp)
                .setStyle(style)
                .setContentIntent(mainActivityIntent);

        if (music != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setColorized(true);
            }

            builder.setStyle(style.setShowActionsInCompactView(0,1,2))
                    .setColor(color)
                    .setLargeIcon(artwork)
                    .addAction(prevAction)
                    .addAction(playAction)
                    .addAction(nextAction);
        }


        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }


    void updateView() {
        CampMusicPlayer app = (CampMusicPlayer) getApplication();
    }

    class MediaPlayerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateView();
                    updateNotification();
                    break;
            }
        }
    }

}
