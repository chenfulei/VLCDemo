package com.vlc.demo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class VideoPlayActivity extends Activity implements IVLCVout.Callback, LibVLC.HardwareAccelerationError {
	private final String URL = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";
	// display surface
	private SurfaceView mSurface;
	private SurfaceHolder holder;

	// media player
	private LibVLC libvlc;
	private MediaPlayer mMediaPlayer = null;
	private int mVideoWidth;
	private int mVideoHeight;
	private final static int VideoSizeChanged = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_video);

		mSurface = (SurfaceView) findViewById(R.id.surface);
		holder = mSurface.getHolder();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setSize(mVideoWidth, mVideoHeight);
	}

	@Override
	protected void onResume() {
		super.onResume();
		createPlayer(URL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		releasePlayer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releasePlayer();
	}

	/*************
	 * Surface
	 *************/
	private void setSize(int width, int height) {
		mVideoWidth = width;
		mVideoHeight = height;
		if (mVideoWidth * mVideoHeight <= 1)
			return;

		if (holder == null || mSurface == null)
			return;

		// get screen size
		int w = getWindow().getDecorView().getWidth();
		int h = getWindow().getDecorView().getHeight();

		// getWindow().getDecorView() doesn't always take orientation into
		// account, we have to correct the values
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		if (w > h && isPortrait || w < h && !isPortrait) {
			int i = w;
			w = h;
			h = i;
		}

		float videoAR = (float) mVideoWidth / (float) mVideoHeight;
		float screenAR = (float) w / (float) h;

		if (screenAR < videoAR)
			h = (int) (w / videoAR);
		else
			w = (int) (h * videoAR);

		// force surface buffer size
		holder.setFixedSize(mVideoWidth, mVideoHeight);

		// set display size
		LayoutParams lp = mSurface.getLayoutParams();
		lp.width = w;
		lp.height = h;
		mSurface.setLayoutParams(lp);
		mSurface.invalidate();
	}

	/*************
	 * Player
	 *************/

	private void createPlayer(String media) {
		releasePlayer();
		try {
			// Create LibVLC
			// TODO: make this more robust, and sync with audio demo
			ArrayList<String> options = new ArrayList<String>();
			options.add("--http-reconnect");
	        options.add("--network-caching=2000");
			libvlc = new LibVLC(options);
			libvlc.setOnHardwareAccelerationError(this);
			holder.setKeepScreenOn(true);

			// Create media player
			mMediaPlayer = new MediaPlayer(libvlc);
			mMediaPlayer.setEventListener(mPlayerListener);

			// Set up video output
			final IVLCVout vout = mMediaPlayer.getVLCVout();
			vout.setVideoView(mSurface);
			// vout.setSubtitlesView(mSurfaceSubtitles);
			vout.addCallback(this);
			vout.attachViews();

//			Media m = new Media(libvlc, media);
			Media m = new Media(libvlc, AndroidUtil.LocationToUri(media));
			mMediaPlayer.setMedia(m);
			mMediaPlayer.play();
		} catch (Exception e) {
			Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
		}
	}

	// TODO: handle this cleaner
	private void releasePlayer() {
		if (libvlc == null)
			return;
		mMediaPlayer.stop();
		final IVLCVout vout = mMediaPlayer.getVLCVout();
		vout.removeCallback(this);
		vout.detachViews();
		holder = null;
		libvlc.release();
		libvlc = null;

		mVideoWidth = 0;
		mVideoHeight = 0;
	}

	/*************
	 * Events
	 *************/

	private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);

	@Override
	public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum,
			int sarDen) {
		if (width * height == 0)
			return;

		// store video size
		mVideoWidth = width;
		mVideoHeight = height;
		setSize(mVideoWidth, mVideoHeight);
	}

	@Override
	public void onSurfacesCreated(IVLCVout vlcVout) {

	}

	@Override
	public void onSurfacesDestroyed(IVLCVout vlcVout) {

	}

	private static class MyPlayerListener implements MediaPlayer.EventListener {
		private WeakReference<VideoPlayActivity> mOwner;

		public MyPlayerListener(VideoPlayActivity owner) {
			mOwner = new WeakReference<VideoPlayActivity>(owner);
		}

		@Override
		public void onEvent(MediaPlayer.Event event) {
			VideoPlayActivity player = mOwner.get();

			switch (event.type) {
			case MediaPlayer.Event.EndReached:
				Log.d("", "MediaPlayerEndReached");
				player.releasePlayer();
				break;
			case MediaPlayer.Event.Playing:
			case MediaPlayer.Event.Paused:
			case MediaPlayer.Event.Stopped:
			default:
				break;
			}
		}
	}

	@Override
	public void eventHardwareAccelerationError() {
		Log.e("", "Error with hardware acceleration");
		this.releasePlayer();
		Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
	}

}
