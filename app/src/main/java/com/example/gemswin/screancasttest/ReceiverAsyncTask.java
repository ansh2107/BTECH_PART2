
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
import android.os.AsyncTask;
import android.util.Log;


import com.example.gemswin.screancasttest.ScreenCastLib.VideoChunk;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by renard on 22/12/14.
 */
public class ReceiverAsyncTask extends AsyncTask<Void,VideoChunk,Void> {

	Context context;
	private static final String LOG_TAG = ReceiverAsyncTask.class.getSimpleName();


	interface ReceiverListener {
		void onVideoChunk(VideoChunk chunk);
        void onConnectionLost();
    }
	ReceiverAsyncTask(Context context, ReceiverListener mListener){
		this.context = context;
		this.mListener = mListener;


		pref =  new PrefManager(context);
	}


	private ServerSocket mServerSocket;
	private final ReceiverListener mListener;

	PrefManager pref;


	@Override
	protected Void doInBackground(Void... params) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        final Socket accept;

		try {
			//String portget = LoginActivity.portget;
			mServerSocket = new ServerSocket(Integer.parseInt(pref.getSerialNo()));
			Log.i(LOG_TAG, "waiting for connection");
			accept = mServerSocket.accept();
			Log.i(LOG_TAG, "Socket accepted");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		final DataInputStream inputStream;
		try {
			inputStream = new DataInputStream(accept.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		while(!isCancelled()){
			try {
				int length  = inputStream.readInt();
				int flags  = inputStream.readInt();
				long timeUs  = inputStream.readLong();
				byte[] data = new byte[length];
				inputStream.readFully(data, 0, length);
				VideoChunk chunk = new VideoChunk(data,flags,timeUs);
                publishProgress(chunk);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onConnectionLost();
    }

    @Override
	protected void onProgressUpdate(VideoChunk... values) {
		super.onProgressUpdate(values);
		mListener.onVideoChunk(values[0]);
	}
}
