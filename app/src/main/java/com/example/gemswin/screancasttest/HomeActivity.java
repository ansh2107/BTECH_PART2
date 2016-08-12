package com.example.gemswin.screancasttest;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public class HomeActivity extends Activity {

    Button update;
    Button regHome;
    Button search;

    Dialog dialogReg,dialogRegupdate;
   JSONParser jParser5 = new JSONParser();
    JSONParser jParser6 = new JSONParser();
    JSONObject json;
    JSONObject json1;
    String classupdate,portupdate;
    PrefManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = new PrefManager(getApplicationContext());

        //String pending_status =  DataHolderClass.getInstance().getDistributor_id();




        update = (Button) findViewById(R.id.update);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUpdate();
            }
        });



    }
    protected void DialogUpdate () {
        // TODO Auto-generated method stub

        dialogReg = new Dialog(HomeActivity.this);
        dialogReg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReg.setContentView(R.layout.dialogupdate);

        Button submit = (Button)dialogReg.findViewById(R.id.submitupdate);



        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            /*    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
             //   String ip1 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                downloadupdate= Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

*/
                portupdate=  ((EditText)dialogReg.findViewById(R.id.portupdate)).getText().toString();

                classupdate=  ((EditText)dialogReg.findViewById(R.id.classSelect)).getText().toString();



                new Update().execute();




                dialogReg.dismiss();




            }
        });

        dialogReg.show();
    }



    private class Update extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(HomeActivity.this);

        /** progress dialog to show user that the backup is processing. */
        /** application context. */

        protected void onPreExecute() {
            this.dialog.setMessage("Updating...");
            this.dialog.show();
        }




        @Override
        protected String doInBackground(String... urls)
        {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("ip", downloadupdate));
            params.add(new BasicNameValuePair("port", portupdate));
            params.add(new BasicNameValuePair("class", classupdate));
            //  params.add(new BasicNameValuePair("name", nameString));
            pref.setBatch(classupdate);
            String log = "http://176.32.230.250/anshuli.com/ScreenCast/updateClient.php";


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




                //Toast.makeText(MainActivity.this, (CharSequence) json, 1).show();

                String account= json.getString("client");
                if(account.equals("FAILED"))
                    Toast.makeText(HomeActivity.this, "Port Updation Failed", Toast.LENGTH_SHORT).show();
                else if(account.equals("SUCCESS")) {

                    pref.setClass(classupdate);
                    pref.setSerialNo(portupdate);
                    Toast.makeText(HomeActivity.this, "Successfully Updated.", Toast.LENGTH_SHORT).show();


                    Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Network connection Error!!!",Toast.LENGTH_LONG).show();
            }






        }
    }

}
