package com.sound.soundboard;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;


public class clicker implements OnClickListener {

    int pos;

    public void NatigationOnClick(int pos) {
        this.pos = pos;
    }

    public void onClick(View v) {

    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

}



/*
private void showDeleteDialog(final int position){
	
	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
      
    // Setting Dialog Title
    alertDialog.setTitle("What you want to do?");

    // Setting Dialog Message
    alertDialog.setMessage("Delete or Edit the current sound/button?");

    // Setting Icon to Dialog
    //alertDialog.setIcon(R.drawable.delete);

    // On pressing Settings button
    alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog,int which) {
			  initiatePopupWindow(position, recordingNames[position], playButtons[position]);
        }
    });

    // on pressing cancel button
    alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	File file = new File(path , "RECORDING" + Integer.toString(position) + ".mp4");
        	file.delete();
        	numberRecording --;
        	SavePreferencesInt("Recordingcount", numberRecording);
        	playButtons[position].setVisibility(View.GONE);
        	
        	String[] temp = new String[numberRecording];
        	int k =0;
        	for(int i=0; i<= numberRecording; i++){
        		if(i != position){ k++;}
            		temp[i] = recordingNames[k];
        			SavePreferences(Integer.toString(i), recordingNames[k]);
        	}
        	recordingNames = temp;	
        }
    });

    // Showing Alert Message
    alertDialog.show();
}

*/