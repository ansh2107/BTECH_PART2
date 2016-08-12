
/*
 * Copyright (c) 2015 Renard Wellnitz.
 *
 *  This file is part of ScreenShare.
 *
 *     Foobar is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.example.gemswin.screancasttest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gemswin.screancasttest.ScreenCastLib.DecoderAsyncTask;
import com.example.gemswin.screancasttest.ScreenCastLib.MediaCodecFactory;
import com.example.gemswin.screancasttest.ScreenCastLib.OnVideoSizeChangedListener;
import com.example.gemswin.screancasttest.ScreenCastLib.VideoChunk;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity_Reciever extends Activity implements ReceiverAsyncTask.ReceiverListener, TextureView.SurfaceTextureListener, OnVideoSizeChangedListener {

	private static final String LOG_TAG = MainActivity_Reciever.class.getSimpleName();
	private ReceiverAsyncTask mTask;
	private final MediaCodecFactory mMediaCodecFactory = new MediaCodecFactory(0,0);
	private DecoderAsyncTask mDecoderAsyncTask;
	TextureView mTextureView;
	private PrefManager pref;

	Dialog dialogDoubt;

	String message = "";
	Button submit1;
	EditText name1,class1, doubt1;

	String nameString,itemValue;
	ArrayList<String> planetList;
	private ListView mainListView ;
	private ArrayAdapter<String> listAdapter ;
	Dialog dialogDoubt1;
	JSONParser jParser5 = new JSONParser();
	String	userget,classget;
	String	passget;
	String	portget;
	JSONObject json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_reciever);
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
		TextView text = (TextView) findViewById(R.id.textView);


		planetList = new ArrayList<String>();

		mTextureView = (TextureView)findViewById(R.id.textureView);
		mTextureView.setSurfaceTextureListener(this);
		mTextureView.requestLayout();
		mTextureView.invalidate();
		mTextureView.setOpaque(false);



		pref = new PrefManager(getApplicationContext());
		text.setText(ip);



		mTask = new ReceiverAsyncTask(getApplicationContext(),this);
		mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTask.cancel(true);
	}


	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
		Log.i(LOG_TAG, "onSurfaceTextureAvailable (" + width + "/" + height + ")");
		try {
			final Surface surface = new Surface(surfaceTexture);
			mDecoderAsyncTask = new DecoderAsyncTask(mMediaCodecFactory, surface, this);
			mDecoderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}  //skip

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	} //skip

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

	@Override
	public void onVideoChunk(VideoChunk chunk) {

        if(mDecoderAsyncTask!=null) {
			mDecoderAsyncTask.addChunk(chunk);
		}
	}//skip

    @Override
    public void onConnectionLost() {
        if(!isDestroyed()){
            mTask = new ReceiverAsyncTask(getApplicationContext(),this);
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }//skip

    @Override
	public void onVideoSizeChanged(int videoWidth, int videoHeight) {
		// Get the SurfaceView layout parameters
		Log.i(LOG_TAG, "onVideoSizeChange (" + videoWidth + "/" + videoHeight + ")");
		android.view.ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();
		float videoProportion = (float) videoWidth / (float) videoHeight;
		// Get the width of the screen

		int screenWidth = mTextureView.getWidth();
		int screenHeight = mTextureView.getHeight();
		float screenProportion = (float) screenWidth / (float) screenHeight;
		if (videoProportion > screenProportion) {
			//video is wider than our screen
			lp.width = screenWidth;
			lp.height = (int) ((float) screenWidth / videoProportion);
		} else {
			lp.width = (int) (videoProportion * (float) screenHeight);
			lp.height = screenHeight;
		}
		// Commit the layout parameters
		mTextureView.setLayoutParams(lp);

	} // end of videosize  //skip

    @Override
    public void onVideoEnded(List<Pair<Long, Integer>> chunksTimeSeries) {

    }




/*

//jo bhi ho raha idhar ho raha
	protected void DoubtBox () {
		// TODO Auto-generated method stub

		dialogDoubt = new Dialog(MainActivity_Reciever.this);
		dialogDoubt.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDoubt.setContentView(R.layout.activity_doubt);



	//	name1 = (EditText)dialogDoubt.findViewById(R.id.name_reg1);
		class1 = (EditText)dialogDoubt.findViewById(R.id.class_reg1);
		doubt1 = (EditText)dialogDoubt.findViewById(R.id.doubt_reg1);

		submit1 = (Button) dialogDoubt.findViewById(R.id.doubtsubmit);


		submit1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {





				doubtstring = doubt1.getText().toString();  //username
				//password = pass.getText().toString();
			//	nameString = 	name1.getText().toString();
				classget = 	class1.getText().toString();

				new login().execute();

				dialogDoubt.dismiss();


			}
		});


		dialogDoubt.show();
	}     //end of doubt box


	protected void DoubtBox1 () {
		// TODO Auto-generated method stub

		dialogDoubt1 = new Dialog(MainActivity_Reciever.this);
		dialogDoubt1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDoubt1.setContentView(R.layout.activity_doubtlist);  //here

		mainListView = (ListView)dialogDoubt1.findViewById(R.id.mainListView);


		//list ki shuruat




		*/
/*String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
				"Jupiter", "Saturn", "Uranus", "Neptune"};
		planetList.addAll( Arrays.asList(planets) );*//*


		// Create ArrayAdapter using the planet list.
		listAdapter = new ArrayAdapter<String>(MainActivity_Reciever.this, R.layout.simplerow, planetList);
		mainListView.setAdapter(listAdapter);

		mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   final int pos, long id) {
				// TODO Auto-generated method stub
				itemValue = (String) mainListView.getItemAtPosition(pos);
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:

								new delete().execute();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								//No button clicked
								break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(arg1.getContext());
				builder.setMessage("Are you sure to delete this doubt ?").setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();

				return true;

			}
		});




		dialogDoubt1.show();

	}     //end of doubt box
*/





/*


//start of async task

	private class delete extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(MainActivity_Reciever.this);

		*//** progress dialog to show user that the backup is processing. *//*
		*//** application context. *//*

		protected void onPreExecute() {
			this.dialog.setMessage("Deleting Doubt...");
			this.dialog.show();
		}




		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));

			params.add(new BasicNameValuePair("doubtValue", itemValue));
			params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/deleteDoubt.php";


			json = jParser5.makeHttpRequest(log, "POST", params);






			//visible
			return null;



		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result)
		{  //gone
			// //System.out.println("RESULT : " + result);

			this.dialog.dismiss();
			try {
				// Checking for SUCCESS TAG




				//Toast.makeText(MainActivity_Reciever.this, (CharSequence) json, 1).show();

				String account= json.getString("client");
				if(account.equals("FAILED"))
					Toast.makeText(MainActivity_Reciever.this, "Doubt deleting Failed", Toast.LENGTH_SHORT).show();
				else if(account.equals("SUCCESS")) {
					Toast.makeText(MainActivity_Reciever.this, "Doubt Successfully Deleted.", Toast.LENGTH_SHORT).show();
					//Intent intent = new Intent(DoubtActivity.this, LoginActivity.class);

					//startActivity(intent);
					//finish();
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}






		}
	}*/
	/*private class login extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(MainActivity_Reciever.this);

		*//** progress dialog to show user that the backup is processing. *//*
		*//** application context. *//*

		protected void onPreExecute() {
			this.dialog.setMessage("Sending Doubt...");
			this.dialog.show();
		}




		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));
			params.add(new BasicNameValuePair("doubt", doubtstring));
			params.add(new BasicNameValuePair("name", pref.getName()));
			params.add(new BasicNameValuePair("class", classget));
			params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/setDoubt.php";


			json = jParser5.makeHttpRequest(log, "POST", params);






			//visible
			return null;



		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result)
		{  //gone
			// //System.out.println("RESULT : " + result);

			this.dialog.dismiss();
			try {
				// Checking for SUCCESS TAG




				//Toast.makeText(MainActivity_Reciever.this, (CharSequence) json, 1).show();

				String account= json.getString("client");
				if(account.equals("FAILED"))
					Toast.makeText(MainActivity_Reciever.this, "Doubt Sending Failed", Toast.LENGTH_SHORT).show();
				else if(account.equals("SUCCESS")) {
					Toast.makeText(MainActivity_Reciever.this, "Successfully Submitted.", Toast.LENGTH_SHORT).show();
					//Intent intent = new Intent(DoubtActivity.this, LoginActivity.class);

					//startActivity(intent);
					//finish();
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}






		}
	}    // end of async
*/
//start of async task 2

	/*private class viewdoubttask extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog = new ProgressDialog(MainActivity_Reciever.this);

		*//** progress dialog to show user that the backup is processing. *//*
		*//** application context. *//*

		protected void onPreExecute() {
			this.dialog.setMessage("Opening DoubtList...");
			this.dialog.show();
		}




		@Override
		protected String doInBackground(String... urls)
		{


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//  params.add(new BasicNameValuePair("roll_number", username));
			//params.add(new BasicNameValuePair("doubt", doubtstring));
			params.add(new BasicNameValuePair("name", pref.getName()));
			params.add(new BasicNameValuePair("roll", pref.getSerialNo()));

			String log = "http://176.32.230.250/anshuli.com/ScreenCast/seeDoubtList.php";  // change php file name


			json = jParser5.makeHttpRequest(log, "POST", params);



			try {
				// Checking for SUCCESS TAG
				planetList.clear();
				JSONArray account= json.getJSONArray("client");
				for(int i = 0; i < account.length(); i++)
				{
					json =account.getJSONObject(i);



					String doubt = json.getString("DOUBT");

					planetList.add(doubt);
				}



			}
			catch (Exception e)
			{
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
			}


			//visible
			return null;



		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result)
		{  //gone
			// //System.out.println("RESULT : " + result);


			this.dialog.dismiss();

               DoubtBox1();






		}
	}*/








}
