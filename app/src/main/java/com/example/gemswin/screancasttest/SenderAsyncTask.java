
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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.example.gemswin.screancasttest.ScreenCastLib.VideoChunk;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;


public class SenderAsyncTask extends AsyncTask<Void,Void,Void> {


	private static final String LOG_TAG = SenderAsyncTask.class.getSimpleName();
	 public static   List<String> mIp;
	Context context;
	JSONParser jparser = new JSONParser();
	List<String> ipArray ;
	JSONObject json;
	public static DataOutputStream[] dataOut;
	LinkedBlockingDeque<VideoChunk> mVideoChunks = new LinkedBlockingDeque<VideoChunk>();
	PrefManager pref;
	public void addChunk(VideoChunk chunk) {

        synchronized (mVideoChunks) {

            mVideoChunks.addFirst(chunk);
            if(mVideoChunks.size()>2) {
                Log.i(LOG_TAG, "Chunks: " + mVideoChunks.size());

            }

        }
	}

	SenderAsyncTask(Context context){
		 this.context = context;


		 pref =  new PrefManager(context);
	}

	public void setDataOutput(DataOutputStream[] d){

		dataOut = d;

	}

	@Override
	protected Void doInBackground(Void... params) {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		//String portget = MainActivity.portString;

		List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		params1.add(new BasicNameValuePair("class", pref.getclass()));

		String url = "http://176.32.230.250/anshuli.com/ScreenCast/getIPs.php";



		json = jparser.makeHttpRequest(url, "POST", params1);


		try {
			// Checking for SUCCESS TAG

			//forjson.clear();

			String heading = "";
			String message = "";
			//Toast.makeText(MainActivity.this, (CharSequence) json, 1).show();
			ipArray = new ArrayList<String>();

			JSONArray account = json.getJSONArray("IPs");
			for (int i = 0; i < account.length(); i++) {
				json = account.getJSONObject(i);

				String IpString = json.getString("IP");


				if (!IpString.equals("0.0.0.0"))
					ipArray.add(IpString);
				// forjson.add(Roll+"-"+ NAME);
				//categories_description.add(description);


			}

		}  catch (Exception e)
		{
			e.printStackTrace();
		}


			int port = Integer.parseInt(pref.getSerialNo());



		java.net.Socket socket=null;



	//	ip.add("192.168.15.105");
	//	ip.add("192.168.15.103");
int len=ipArray.size();
		/*	try {
				for(int i=0;i<ip.size();i++)
				socket[i] = new java.net.Socket(ip.get(i),port);
			//	socket1 = new java.net.Socket("192.168.15.105", port);
				 // connect to server
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			dataOut = new DataOutputStream[len];
			try {
				for(int i=0;i<len;i++){
					socket = new java.net.Socket(ipArray.get(i),port);
					dataOut[i] = new DataOutputStream(socket.getOutputStream());
				}

			//	dataOut1 = new DataOutputStream(socket1.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (!isCancelled()) {





				VideoChunk chunk = null;
				try {
					//Log.d(LOG_TAG, "waiting for data to send");
					chunk = mVideoChunks.takeLast();
					//Log.d(LOG_TAG,"got data. writing to socket");
					int length = chunk.getData().length;
					for(int i=0;i<ipArray.size();i++) {

						dataOut[i].writeInt(length);
						dataOut[i].writeInt(chunk.getFlags());
						dataOut[i].writeLong(chunk.getTimeUs());
						dataOut[i].write(chunk.getData());
						dataOut[i].flush();

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		return null;
	}

}
