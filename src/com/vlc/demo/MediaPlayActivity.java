package com.vlc.demo;

import com.vlc.mediaplayers.VLCMediaPlayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MediaPlayActivity extends Activity{

	private Button btn_media;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_media);
		
		btn_media = (Button) findViewById(R.id.btn_media);
		btn_media.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					VLCMediaPlayer.get().prepare("http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4").start();
//					VLCMediaPlayer.get().prepare("http://music.baidutt.com/up/kwcywdwp/spmmk.mp3").start();
				}  catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
}
