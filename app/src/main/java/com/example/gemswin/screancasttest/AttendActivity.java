package com.example.gemswin.screancasttest;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

public class AttendActivity extends Activity {
    Button newattreg,putatt,checkatt;
    JSONParser jParser6 = new JSONParser();
    JSONObject json1;
    PrefManager pref;
    JSONParser jParser7 = new JSONParser();
    JSONObject json2;
    Dialog dialognewatt,dialogputatt;
    String studentsClass,putClass;
    String numStudents;
    String subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend);
        pref = new PrefManager(getApplicationContext());
        newattreg = (Button) findViewById(R.id.newattreg);
        putatt = (Button) findViewById(R.id.putatt);
        checkatt = (Button) findViewById(R.id.checkatt);

        newattreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBox();
            }
        });

        putatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxPut();
            }
        });

        checkatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    protected void DialogBox() {
        // TODO Auto-generated method stub

        dialognewatt = new Dialog(AttendActivity.this);
        dialognewatt.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialognewatt.setContentView(R.layout.dialogatt);

        Button submit = (Button) dialognewatt.findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                studentsClass = ((EditText) dialognewatt.findViewById(R.id.studentsClass)).getText().toString();
                numStudents = ((EditText) dialognewatt.findViewById(R.id.numStudents)).getText().toString();
                //          subject = ((EditText) dialognewatt.findViewById(R.id.subject)).getText().toString();
                new newAttendence().execute();


                dialognewatt.dismiss();


            }
        });

        dialognewatt.show();
    }

    protected void DialogBoxPut() {
        // TODO Auto-generated method stub

        dialogputatt = new Dialog(AttendActivity.this);
        dialogputatt.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogputatt.setContentView(R.layout.dialogput);

        Button submit = (Button) dialogputatt.findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                putClass = ((EditText) dialogputatt.findViewById(R.id.studentput)).getText().toString();
                //numStudents = ((EditText) dialogputatt.findViewById(R.id.numStudents)).getText().toString();
                //          subject = ((EditText) dialognewatt.findViewById(R.id.subject)).getText().toString();
                new putAttendence().execute();


                dialogputatt.dismiss();


            }
        });

        dialogputatt.show();
    }

    private class newAttendence extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(AttendActivity.this);

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }


        @Override
        protected String doInBackground(String... urls) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("ip", downloadupdate));

            params.add(new BasicNameValuePair("class",studentsClass ));
            params.add(new BasicNameValuePair("number",numStudents ));
          // params.add(new BasicNameValuePair("table",studentsClass ));
            //  params.add(new BasicNameValuePair("name", nameString));

            String log1 = "http://176.32.230.250/anshuli.com/ScreenCast/newattreg.php";


            json1 = jParser6.makeHttpRequest(log1, "POST", params);


            //visible
            return null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {  //gone
            // //System.out.println("RESULT : " + result);

            this.dialog.dismiss();
            try {
                // Checking for SUCCESS TAG


                //Toast.makeText(MainActivity.this, (CharSequence) json, 1).show();
                String account = json1.getString("client");
                if (account.equals("FAILED"))
                    Toast.makeText(AttendActivity.this, "Class Not Registered", Toast.LENGTH_SHORT).show();
                else if (account.equals("SUCCESS")) {
                    Toast.makeText(AttendActivity.this, "Class Registered.", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Network connection Error!!!", Toast.LENGTH_LONG).show();

            }


        }
    }

    private class putAttendence extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(AttendActivity.this);

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */

        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }


        @Override
        protected String doInBackground(String... urls) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("ip", downloadupdate));

            params.add(new BasicNameValuePair("class",putClass ));
            // params.add(new BasicNameValuePair("table",studentsClass ));
            //  params.add(new BasicNameValuePair("name", nameString));

            String log1 = "http://176.32.230.250/anshuli.com/ScreenCast/putattreg.php";


            json2 = jParser7.makeHttpRequest(log1, "POST", params);


            //visible
            return null;


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {  //gone
            // //System.out.println("RESULT : " + result);

            this.dialog.dismiss();
            try {
                // Checking for SUCCESS TAG




                JSONArray account = json2.getJSONArray("ROLL");
          //      for (int i = 0; i < account.length(); i++) {
                    json2 = account.getJSONObject(account.length()-1);

                    String IpString = json2.getString("ROLL_NUMBER");
                    pref.setNumber(IpString);



//                }

                if (IpString != null)
                    Toast.makeText(getApplicationContext(), "Roll Numbers Fetched", Toast.LENGTH_LONG).show();
                Intent i = new Intent(AttendActivity.this,TakeAttendence.class);
                startActivity(i);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Roll Numbers Not Fetched", Toast.LENGTH_LONG).show();

            }


        }
    }


}
