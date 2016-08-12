package com.example.gemswin.screancasttest;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;


public class FileBrowser extends ListActivity {

    String downloadIp,filename;
    Dialog dialogReg;
    EditText ipAddress;
    int portFTP;
    private String path;
    public List<File> files = new ArrayList();
    public List<File> dirs = new ArrayList();
    /*********  work only for Dedicated IP ***********/
    static final String FTP_HOST= "192.168.15.50:211";

    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "FTPUser";

    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS  ="ankit";
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        // Use the current directory as title
        //path = "/storage/sdcard";
        path= Environment.getExternalStorageDirectory().getAbsolutePath();
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        // Read all files sorted into the values-array


        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        File[] list = dir.listFiles();
        for (File file : list) {
            if (file.isDirectory()) {
                dirs.add(file);
            } else {
                files.add(file);
            }
        }
        Collections.sort(dirs);
        Collections.sort(files);
        dirs.addAll(files);
//String[] stringValues=values.toArray(new String[values.size()]);//to convert list to srting and list be decared as List<String> values = new ArrayList();
        // Put the data into the list
        MyFileAdaptor adapter = new MyFileAdaptor(this, dirs);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {


        MyFileAdaptor my = new MyFileAdaptor(this, dirs);
         file = my.getItem(position);


        if (file.isDirectory()) {
            Intent intent = new Intent(this, FileBrowser.class);
            intent.putExtra("path", file.getPath());
            startActivity(intent);
        } else {
            String name = file.getName();
            if (name.endsWith(".pdf")) {
              // DialogBox();
                Intent intent = new Intent(FileBrowser.this, PdfFileRenderer.class);
                intent.putExtra("pdfpath", file.getPath());
                startActivity(intent);
            }else{
                Toast.makeText(this, file.getPath() + " is not suported", Toast.LENGTH_LONG).show();
            }

        }


    }

  /*  public void uploadFile(File fileName){




    }*/

    public class uploadFile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            FTPClient con = null;

            try
            {
                con = new FTPClient();
                con.connect(downloadIp,portFTP);

                if (con.login("FTPUser", "ankit"))
                {
                    con.enterLocalPassiveMode(); // important!
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                   // String data = "/sdcard/Download/a.pdf";
                    String fileSave = file.getPath();
                    String splitter[] = fileSave.split("/");
                    int counter = fileSave.split("/").length - 1;
                    String name = splitter[counter];
                    FileInputStream in = new FileInputStream(new File(file.getPath()));
                    boolean result = con.storeFile("/"+name, in);
                    in.close();
                    if (result) Log.v("upload result", "succeeded");
                    con.logout();
                    con.disconnect();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            Intent intent = new Intent(FileBrowser.this, PdfFileRenderer.class);
            intent.putExtra("pdfpath", file.getPath());
            startActivity(intent);
        }

        private ProgressDialog dialog = new ProgressDialog(FileBrowser.this);

        /** progress dialog to show user that the backup is processing. */
        /** application context. */

        protected void onPreExecute() {
            this.dialog.setMessage("Uploading file...");
            this.dialog.show();
        }




    }

    protected void DialogBox () {
        // TODO Auto-generated method stub

        dialogReg = new Dialog(FileBrowser.this);
        dialogReg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReg.setContentView(R.layout.upload_dialog);

        Button submit = (Button)dialogReg.findViewById(R.id.submit_up);



        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                downloadIp= ((EditText)dialogReg.findViewById(R.id.ip_up)).getText().toString();


                portFTP=   Integer.parseInt(((EditText) dialogReg.findViewById(R.id.port_up)).getText().toString());
                //filename= ((EditText)dialogReg.findViewById(R.id.filename)).getText().toString();




                new uploadFile().execute();




                dialogReg.dismiss();




            }
        });

        dialogReg.show();
    }


}