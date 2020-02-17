package com.github.aoirint.campmusicplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.github.aoirint.campmusicplayer.db.ArtworkCacheManager;
import com.github.aoirint.campmusicplayer.db.MusicDatabase;
import com.github.aoirint.campmusicplayer.music.MusicPlayer;
import com.github.aoirint.campmusicplayer.service.MediaPlayerService;

public class CampMusicPlayer extends Application implements ServiceConnection {
    public MusicDatabase musicDatabase;
    public ArtworkCacheManager artworkCacheManager;
    public MusicPlayer musicPlayer;

    Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = getApplicationContext();
        musicDatabase = new MusicDatabase(context);
        artworkCacheManager = new ArtworkCacheManager(context);
        musicPlayer = new MusicPlayer(context);

        startService();
    }

    void startService() {
        Intent intent = new Intent(this, MediaPlayerService.class).setAction("start");
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void sendUpdateNotification() {
        if (messenger == null) startService();

        try {
            messenger.send(Message.obtain(null, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void closeNotification() {
        unbindService(this);
        stopService(new Intent(this, MediaPlayerService.class));

        musicPlayer.clearMediaPlayer();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        messenger = null;
    }

}
