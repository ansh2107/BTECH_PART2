package com.example.gemswin.screancasttest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


public class MyBroadcastReceiver extends BroadcastReceiver {

	private SenderAsyncTask mSenderAsyncTask;
	@Override
	public void onReceive( Context context, Intent intent) {
		try {

			mSenderAsyncTask = new SenderAsyncTask(context);

			mSenderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			 
				Intent intent1 = new Intent(context, MyBroadcastReceiver.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, 234324243, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
				 AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ ( 20 * 1000), pendingIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
			
		}
		
		
		

	
	}
	

