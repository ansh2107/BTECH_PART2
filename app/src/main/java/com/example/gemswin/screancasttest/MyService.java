package com.example.gemswin.screancasttest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

public class MyService extends Service  {

   public PrefManager pref;
    public SenderAsyncTask mSenderAsyncTask;
    JSONParser jparser = new JSONParser();
    List<String> ipArray ;
    // private static final String LOG_TAG = SenderAsyncTask.class.getSimpleName();
    // public static   List<String> mIp;
    Context context;
    // public static DataOutputStream[] dataOut;
    LinkedBlockingDeque<VideoChunk> mVideoChunks = new LinkedBlockingDeque<VideoChunk>();

    public MyService() {
	}

    public void addChunk(VideoChunk chunk) {
        synchronized (mVideoChunks) {
//            boolean isKeyFrame = (chunk.getFlags() & MediaCodec.BUFFER_FLAG_KEY_FRAME) == MediaCodec.BUFFER_FLAG_KEY_FRAME;
//            if (isKeyFrame) {
//
//
//                Log.i(LOG_TAG, "adding keyframe");
//                VideoChunk configChunk = null;
//                for(VideoChunk c: mVideoChunks){
//                    boolean containsConfig = (chunk.getFlags() & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
//                    if(containsConfig){
//                        configChunk = c;
//                        break;
//                    }
//                }
//                mVideoChunks.clear();
//                if(configChunk!=null){
//                    mVideoChunks.add(configChunk);
//                }
//            }
            mVideoChunks.addFirst(chunk);
            if(mVideoChunks.size()>2) {
                Log.i("abc", "Chunks: " + mVideoChunks.size());
            }
        }
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	@Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        pref	= new PrefManager(getApplication());
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    	// For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

        try {



            new getNewIP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
       
   
    }
 
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
       // getNewIP.cancel(true);
        
    }



    public class getNewIP extends AsyncTask<String, Void, String> {
        JSONObject json;




        /** progress dialog to show user that the backup is processing. */
        /** application context. */

        protected void onPreExecute() {

        }



        @Override
        protected String doInBackground(String... urls)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("class", pref.getclass()));

            String url = "http://176.32.230.250/anshuli.com/ScreenCast/getIPs.php";



            json = jparser.makeHttpRequest(url, "POST", params);


            try {
                // Checking for SUCCESS TAG

                //forjson.clear();

                String	heading="";
                String message="";
                //Toast.makeText(MainActivity.this, (CharSequence) json, 1).show();
                ipArray = new ArrayList<String>();

                JSONArray account= json.getJSONArray("IPs");
                for(int i = 0; i < account.length(); i++)
                {
                    json =account.getJSONObject(i);

                    String IpString = json.getString("IP");



                    if(!IpString.equals("0.0.0.0"))
                        ipArray.add(IpString);
                    // forjson.add(Roll+"-"+ NAME);
                    //categories_description.add(description);



                }

                java.net.Socket socket=null;
                DataOutputStream[] dataOut = new DataOutputStream[ipArray.size()];
                try {
                    for(int i=0;i<ipArray.size();i++){
                        int k = Integer.parseInt(pref.getSerialNo());
                        String l = ipArray.get(i);
                        socket = new java.net.Socket(ipArray.get(i),Integer.parseInt(pref.getSerialNo()));
                        dataOut[i] = new DataOutputStream(socket.getOutputStream());
                    }


                    //	dataOut1 = new DataOutputStream(socket1.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

              /*  mSenderAsyncTask.mIp = ipArray;
                mSenderAsyncTask.dataOut = dataOut;*/


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


               /* mSenderAsyncTask = new SenderAsyncTask(getApplication(),ipArray);

                mSenderAsyncTask.setDataOutput(dataOut);*/

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {



            //JSONArray jsonArray;



        }






    }



}
