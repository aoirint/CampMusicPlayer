package com.github.aoirint.campmusicplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.Nullable;

import com.github.aoirint.campmusicplayer.CampMusicPlayer;
import com.github.aoirint.campmusicplayer.R;
import com.github.aoirint.campmusicplayer.activity.main.MainActivity;
import com.github.aoirint.campmusicplayer.db.data.Music;
import com.github.aoirint.campmusicplayer.music.MusicQueuePlayer;
import com.github.aoirint.campmusicplayer.util.BitmapUtil;

import java.io.IOException;

public class MediaPlayerService extends Service {
    Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();

        messenger = new Messenger(new MediaPlayerHandler());
    }

    void updateNotification() {
        String channelId = "default";
        Context context = getApplicationContext();
        String channelTitle = context.getString(R.string.app_name);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);

            builder = new Notification.Builder(this, channelId);
        }
        else {
            builder = new Notification.Builder(this);
            builder.setPriority(Notification.PRIORITY_MAX);
        }

        CampMusicPlayer app = (CampMusicPlayer) getApplication();
        MusicQueuePlayer musicQueuePlayer = app.musicQueuePlayer;

        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), 0);
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 0, new Intent(this, MediaPlayerService.class).setAction("previous"), 0);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, new Intent(this, MediaPlayerService.class).setAction("next"), 0);
        PendingIntent closePendingIntent = PendingIntent.getService(this, 0, new Intent(this, MediaPlayerService.class).setAction("close"), 0);

        Music music = musicQueuePlayer.getCurrentMusic();
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

        Notification.Action prevAction = new Notification.Action(R.drawable.ic_skip_previous_7f7f7f_32dp, "Previous", prevPendingIntent);
        Notification.Action nextAction = new Notification.Action(R.drawable.ic_skip_next_7f7f7f_32dp, "Next", nextPendingIntent);
        Notification.Action closeAction = new Notification.Action(R.drawable.ic_close_7f7f7f_32dp, "Close", closePendingIntent);
        Notification.Action playAction;
        if (musicQueuePlayer.isPlaying()) {
            PendingIntent pauseIntent = PendingIntent.getService(this, 0, new Intent(this, MediaPlayerService.class).setAction("pause"), 0);
            playAction = new Notification.Action(R.drawable.ic_pause_7f7f7f_32dp, "Pause", pauseIntent);
        }
        else {
            PendingIntent playIntent = PendingIntent.getService(this, 0, new Intent(this, MediaPlayerService.class).setAction("play"), 0);
            playAction = new Notification.Action(R.drawable.ic_play_arrow_white_32dp, "Play", playIntent);
        }
        Notification.MediaStyle style = new Notification.MediaStyle();

        builder.setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_music_note_7f7f7f_32dp)
                .setStyle(style)
                .setTicker(null)
                .setOnlyAlertOnce(true)
                .setContentIntent(mainActivityPendingIntent);

        if (music != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setColorized(true);
            }

            builder.setStyle(style.setShowActionsInCompactView(0,1,2,3))
                    .setColor(color)
                    .setLargeIcon(artwork)
                    .addAction(prevAction)
                    .addAction(playAction)
                    .addAction(nextAction);
        }
        else {
            builder.setStyle(style.setShowActionsInCompactView(0));
        }
        builder.addAction(closeAction);

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CampMusicPlayer app = (CampMusicPlayer) getApplicationContext();

        switch (intent.getAction()) {
            case "start":
                updateNotification();
                break;
            case "play":
                if (app.musicQueuePlayer.isPausing()) {
                    app.musicQueuePlayer.resume();
                }
                else {
                    app.musicQueuePlayer.play();
                }
                break;
            case "pause":
                app.musicQueuePlayer.pause();
                break;
            case "previous":
                if (! app.musicQueuePlayer.isBeginning()) {
                    if (app.musicQueuePlayer.isPlaying()) {
                        app.musicQueuePlayer.play(); // reset
                    }
                    else {
                        app.musicQueuePlayer.reset();
                    }
                }
                else {
                    app.musicQueuePlayer.goPrev();
                }
                break;
            case "next":
                app.musicQueuePlayer.goNext(false);
                break;
            case "close":
                app.musicQueuePlayer.stop();
                stopForeground(true);
                stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CampMusicPlayer app = (CampMusicPlayer) getApplicationContext();
        app.musicQueuePlayer.stop();
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
