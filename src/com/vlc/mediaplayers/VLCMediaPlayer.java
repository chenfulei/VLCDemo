package com.vlc.mediaplayers;

import java.util.ArrayList;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.EventListener;
import org.videolan.libvlc.util.AndroidUtil;

import android.util.Log;

/**
 * 
 * @author chen_fulei
 *
 */
public class VLCMediaPlayer {
	
	private static class Holder {

        private static final VLCMediaPlayer instance = new VLCMediaPlayer();
    }
	
	private LibVLC mLibVLC;
	private MediaPlayer mMediaPlayer;
	
	private final MediaPlayer.EventListener mMediaPlayerListener = new EventListener() {
		
		@Override
		public void onEvent(Event event) {
			Log.e("", "............................."+event.type);
		}
	};
	
	private VLCMediaPlayer() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("--http-reconnect");
        options.add("--network-caching=2000");
        mLibVLC = new LibVLC(options);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.setEventListener(mMediaPlayerListener);
    }
	
	public LibVLC getLibVlcInstance() {
	    return mLibVLC;
	}

    public MediaPlayer getMediaPlayerInstance() {
        return mMediaPlayer;
    }

    public static VLCMediaPlayer get() {
        return Holder.instance;
    }
    
    public void start() throws IllegalStateException {
        if (!getMediaPlayerInstance().isPlaying()) {
            getMediaPlayerInstance().play();
        }
    }
    
    public void pause() throws IllegalStateException {
        if (getMediaPlayerInstance().isPlaying()) {
            getMediaPlayerInstance().pause();
        }
    }
    
    public VLCMediaPlayer prepare(String path) {
        Media media = new Media(mLibVLC, AndroidUtil.LocationToUri(path));
        getMediaPlayerInstance().setMedia(media);
        return this;
    }
}
