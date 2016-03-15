package com.vlc.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 网络请求 api demo
 * 
 * @author chen_fulei
 *
 */
public class MainActivity extends Activity{

//	private final String URL = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";
	
	private Button btn_play;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		
		btn_play = (Button) findViewById(R.id.btn_play);
		btn_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, VideoPlayActivity.class));
			}
		});
		
	}

}
