package com.sound.soundboard;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.nathanhaze.recordsound.R;


public class SoundBoardActivity extends Activity {
	static SharedPreferences sharedPreferences;
	static EditText  recordsound; 
	Button record ,stop, done, cancel, share ;
	static int numberRecording = 1;
	Button[] playButtons; 
	static MediaPlayer mp = new MediaPlayer();
    static String[] recordingNames;
    LinearLayout layoutbase;
    static AudioRecord test;  	
    static boolean recording;
    ImageView theButton;
    LinearLayout.LayoutParams param;
    TextView title;
    static Boolean isSDPresent;
	CharSequence text;
  	Toast toast;
  	ViewFlipper vf; 
  	String path;
    public static int position;
    public static boolean mainActivity;
    public Activity act;
    
  	
  	private ShareActionProvider mShareActionProvider;
    	
  	 private AdView adView; // The ad view used to load and display the ad.

     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        layoutbase =(LinearLayout) findViewById(R.id.mainScreen);
        mainActivity = true;
        param = new LinearLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        param.setMargins(0, 20, 0, 0);
        param.gravity = Gravity.CENTER;
        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        act = this;
        if(isSDPresent)
        {
        	path = Environment.getExternalStorageDirectory().getPath() + "/recordSound/";
        }
        else{
        	path = getFilesDir().toString()  + "/recordSound/";
         	//path = Environment.getRootDirectory().getPath() + "/recordSount_internal/";
         }
	     File soundDirectory = new File(path);
	     sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
	     if(!soundDirectory.isDirectory()){
	         soundDirectory.mkdirs(); 
	         SavePreferencesInt("Recordingcount", 0);
	      }
	        
	      numberRecording = sharedPreferences.getInt("Recordingcount", 0);
	        
	      recordingNames = new String [numberRecording];
	      getStarted();    
    } 
    
    public void getStarted(){
        playButtons = new Button[numberRecording]; 
	    for(int i= 0; i < numberRecording; i++){
	    	   Log.d("INDEX" , Integer.toString(i));
	    	   playButtons[i] = new Button(this);
		       playButtons[i].setTextColor(Color.parseColor("#ffffff")); 
		       playButtons[i].setTextSize(20);
		       playButtons[i].setTextColor(getResources().getColor(R.color.white));
		       playButtons[i].setLayoutParams(param);	 
		       playButtons[i].setBackgroundResource(R.drawable.apptheme_btn_default_holo_light); 
	    	   layoutbase.addView(playButtons[i]);  
	   	       playButtons[i].setText(sharedPreferences.getString(Integer.toString(i), "add a sound"));   	
	   	       playButtons[i].setOnClickListener(new MyClickListener(i, true, true));
		       playButtons[i].setOnLongClickListener(new MyClickListener(i, false, true));
	    }	
    }

    
    public void createButton(String name){
	      int i = numberRecording - 1;
 	      playButtons[i] = new Button(this);
	      playButtons[i].setTextColor(getResources().getColor(R.color.white));
	      playButtons[i].setTextColor(Color.parseColor("#ffffff")); 
	      playButtons[i].setTextSize(20);
	      playButtons[i].setLayoutParams(param);	 
	      playButtons[i].setBackgroundResource(R.drawable.apptheme_btn_default_holo_light); 
 	      layoutbase.addView(playButtons[i]);   	
	      playButtons[i].setOnLongClickListener(new MyClickListener(i, false, true));
     	  playButtons[i].setText(name);  
	      playButtons[i].setOnClickListener(new MyClickListener(i, true, true));
    }
    
    
    public void addRecording(){
  	      numberRecording++;
	      SavePreferencesInt("Recordingcount", numberRecording);
	    	  
	      Button [] newArray = new Button[numberRecording];
	      System.arraycopy(playButtons, 0, newArray, 0, playButtons.length);
	      playButtons = newArray;
	    	  
	      String [] newArray2 = new String[numberRecording];
	      System.arraycopy(recordingNames, 0, newArray2, 0, recordingNames.length);
	      recordingNames = newArray2;
	    	  	    	    
	      int i = numberRecording - 1;
	      createFile(i);
  }
    

    
    public void createFile(int i){
        File soundFile = new File(path , "RECORDING" + Integer.toString(i) + ".mp4");
        if(!soundFile.exists()){
        OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(soundFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
    	
    }


	public void initiatePopupWindow(int index, String pos, Button button, boolean editing) {
	    try {
	    	this.setTitle("Record Sound");
	    	vf = (ViewFlipper) findViewById( R.id.viewFlipper );
	    	vf.showNext();
	        recordsound = (EditText)findViewById(R.id.soundname);
	        
	        if(sharedPreferences.contains(Integer.toString(index)) ){
	             recordsound.setText(sharedPreferences.getString(Integer.toString(index), "problem"));
	        }
	    
	        // Create the adView
	        adView = new AdView(this, AdSize.BANNER, "ca-app-pub-2377934805759836/4988399360");
	        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);

	        p.addRule(RelativeLayout.BELOW, R.id.cancel);
	        
	        RelativeLayout layout = (RelativeLayout)findViewById(R.id.popup_element);

	        // Add the adView to it
	        layout.addView(adView, p);
	        
	        AdRequest adRequest = new AdRequest();
	        // Initiate a generic request to load it with an ad
	       // adView.loadAd(adRequest.addTestDevice(AdRequest.TEST_EMULATOR));
	        adView.loadAd(adRequest);
	     recordsound.setText("recording " + index);
	     
	     record = (Button) findViewById(R.id.record);
		 stop = (Button) findViewById(R.id.stop);
		 done = (Button) findViewById(R.id.done);
		 theButton = (ImageView)findViewById(R.id.micImage);
		 cancel = (Button)findViewById(R.id.cancel);
		 share  = (Button)findViewById(R.id.share);
		 
		 record.setOnClickListener(new MyClickListener(index,false, editing));
		 stop.setOnClickListener(new MyClickListener(index,  false, editing));
		 done.setOnClickListener(new MyClickListener(index, false, editing));
		 cancel.setOnClickListener(new MyClickListener(index, false, editing));
		 share.setOnClickListener(new MyClickListener(index, false, editing));
		 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void audioPlayer(String path, String fileName) throws IOException{		
		if (mp != null) {
            mp.reset(); 
          //  mp.release();
        }

	    try {
	        mp.setDataSource(path+"/"+fileName);
	    } catch (IllegalArgumentException e) {
	        // TODO Auto-generated catch block
	    	//mp.reset();
	    	
	        e.printStackTrace();
	    } catch (IllegalStateException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    try {

	        mp.prepare();
	    } catch (IllegalStateException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }  
	    mp.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                                    mp.start();
            }                
            });
    }
	
    private void SavePreferences(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
      }
    
    private void SavePreferencesInt(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
      }

    private class MyClickListener implements OnClickListener, OnLongClickListener {
       // private int position; // index position
      //  private Button buttonName; // a button
        private boolean play;  // is this a play button?
        private boolean editing; // editing a recording
        private int position;
        public MyClickListener(int _position, boolean play, boolean editing) {
           Log.d("INDEX", "click " + Integer.toString(_position));
           this.play = play;  // if it a play button
           this.editing = editing;
           this.position = _position; //index
        //   this.buttonName = buttonName; // button
        }
    
        public void onClick(View v) {	
            if(play ==false){		  	
        	if(v == record){
        	   if(recording == true){
       	    	Toast recordingDouble = Toast.makeText(SoundBoardActivity.this,
    	    			"Already Recording", Toast.LENGTH_LONG);
                recordingDouble.show();
        
        	   }
               if(recording==false){
            	   
            	    theButton.setImageResource(R.drawable.redlight);
            	    animation();
            	   
          	    	Toast recordingStarted = Toast.makeText(SoundBoardActivity.this,
        	    			"Recording", Toast.LENGTH_LONG);
                    recordingStarted.show();	   
                    recording = true;
         	        test = new AudioRecord("/recordSound/RECORDING" + position);    	  
        		try {
        			//cherryArray[position].start();
        			test.start();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}  
               }
        	}
            if(v == stop){
       	    	Toast stopRecordingMessage = Toast.makeText(SoundBoardActivity.this,
    	    			"Recording Completed", Toast.LENGTH_LONG);
                stopRecordingMessage.show();
            	stopRecoring();
            	theButton.clearAnimation();
            	theButton.setImageResource(R.drawable.greenlight);
        	}
            if(v == done){
       	    	Toast saving = Toast.makeText(SoundBoardActivity.this,
    	    			"Saving", Toast.LENGTH_LONG);
            	theButton.clearAnimation();
            	theButton.setImageResource(R.drawable.greenlight);
       	    	if(!editing){
       	    	    addRecording();
       	    	}
                saving.show();
            	stopRecoring();
    	    	sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
    	    	SavePreferences(Integer.toString(position), recordsound.getText().toString());
    	    	if(!editing){
    	            createButton(sharedPreferences.getString(Integer.toString(position), "problem"));
    	    	}
    	    	act.setTitle("Sound Board");
    	    	vf.showNext();
    	     } 
            if(v==cancel){
            	if(recording==true){
            		stopRecoring();
            	}
            	mainActivity = true;
    	    	act.setTitle("Sound Board");
            	vf.showNext();
            	
            }
            
            if(v==share){
                File soundFile = new File(path , "RECORDING" + Integer.toString(position) + ".mp4");
                if(soundFile.isFile()){
                	share(soundFile);
                }	
            }
            } // end of false
            
            if(play == true){

					try {
						audioPlayer(path , "RECORDING" + position + ".mp4" );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
            	
            }   // end of true
            } // end of method
        
 	   public boolean onLongClick(View v) {
 		  if(isSDPresent)
 	        {
	 	  	  initiatePopupWindow(position, recordingNames[position], playButtons[position], true);
	           mainActivity = false;
 	        }
 		  else{
	     	 toast.show();
 		  }
 	   return true;

 	           } // end mothod
         } // end of class
    
    public void stopRecoring(){
    	
        if(recording ==true){
	    try {
	    	//cherryArray[position].stop();
	    	test.stop();
	    	recording = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        }
    } 	

 
    private void animation(){
    	Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink);
        theButton.startAnimation(blink);
        
    }
    
    public void share(File sendFile){
    	Intent shareIntent = new Intent();
    	shareIntent.setAction(Intent.ACTION_SEND);
    	Uri recording = Uri.fromFile(sendFile);
    	shareIntent.putExtra(Intent.EXTRA_STREAM, recording);
    	shareIntent.setType("video/mp4");
    	startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.app_name)));
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	File soundFile;
    //	Intent i = new Intent(Intent.ACTION_VIEW);
    	switch (item.getItemId()) {
	    	case R.id.share:
	    		soundFile = new File(path , "RECORDING" + Integer.toString(position) + ".mp4");
                if(soundFile.isFile()){
                	share(soundFile);
                }
	        break;
	        case R.id.setRingTone:
	        	soundFile = new File(path , "RECORDING" + Integer.toString(position) + ".mp4");
                if(soundFile.isFile()){
                	setRingTone(soundFile);
                }
            break;
	        case R.id.addRecording:
		 		initiatePopupWindow(numberRecording, "", null, false);
		        mainActivity = false;
            break;
    	}
        return(true);
    }
    
 // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    
    private void setRingTone(File ringtone){
    	ContentValues values = new ContentValues();
    	values.put(MediaStore.MediaColumns.DATA, ringtone.getAbsolutePath());
    	values.put(MediaStore.MediaColumns.TITLE, "A Recording"); //You will have to populate 
    	values.put(MediaStore.MediaColumns.SIZE, 215454);
    	values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
    	values.put(MediaStore.Audio.Media.ARTIST, "Me"); //You will have to populate this
    	values.put(MediaStore.Audio.Media.DURATION, 230);
    	values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
    	values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
    	values.put(MediaStore.Audio.Media.IS_ALARM, false);
    	values.put(MediaStore.Audio.Media.IS_MUSIC, false);

    	//Insert it into the database
    	Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtone.getAbsolutePath());
    	Uri newUri = getContentResolver().insert(uri, values);

    	RingtoneManager.setActualDefaultRingtoneUri(
    	  SoundBoardActivity.this,
    	  RingtoneManager.TYPE_RINGTONE,
    	  newUri
    	);
    }
    @Override
    public void onPause() {
        super.onPause(); 
        
    }

    
    @Override
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
    
	 // 2.0 and above
    @Override
    public void onBackPressed() {
    	if(!mainActivity){
        	vf.showNext();
    	}
    }

    // Before 2.0
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(!mainActivity){
            	vf.showNext();
	    	}
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    } // end of class


