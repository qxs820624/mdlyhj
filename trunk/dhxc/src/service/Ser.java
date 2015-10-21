package service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.jingxunnet.cmusic.R;

  public class Ser extends Service{
	private MediaPlayer mp;
	  int musics [] = {R.raw.a ,R.raw.b ,R.raw.c ,R.raw.d};
	  int index;
   @Override
  public void onCreate() { 
	super.onCreate();
}    
   @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	   
		int n = intent.getIntExtra("puanduan",0);
		 index = intent.getIntExtra("index", 6);
		if(n == 101){
	    if(mp == null){
			 mp = MediaPlayer.create(this, musics[index]);
			 mp.setLooping(true);
		 }
		 mp.start();
		}
		if(n == 102){
		 onDestroy();
		}
		if(n == 103){
			 if(mp != null){
				   if(mp.isPlaying()){
					   mp.pause();
				   }
			 }
		}
		if(n == 104){
			 if(mp != null){
					   mp.start();				
			 }
		}
		
		return super.onStartCommand(intent, flags, startId);		
	 }   
   
   @Override
	public void onDestroy() {
	   if(mp != null){
		   if(mp.isPlaying()){
			   mp.stop();
			   mp.release();
		   }
		   mp = null;
	   }
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
