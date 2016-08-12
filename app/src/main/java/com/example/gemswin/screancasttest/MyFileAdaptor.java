package com.example.gemswin.screancasttest;

/**
 * Created by pc on 05-07-2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;


public class MyFileAdaptor extends ArrayAdapter {
    public Context context;
    public List<File> files;




    public MyFileAdaptor(Context context,List<File> files){



        super(context,R.layout.single_list,files);
        this.context=context;
        this.files=files;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return super.getView(position, convertView, parent);
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.single_list,parent,false);
        TextView textView1= (TextView) rowView.findViewById(R.id.label1);
        TextView textView2= (TextView) rowView.findViewById(R.id.label2);
        ImageView imageView= (ImageView) rowView.findViewById(R.id.image);

        File file=getItem(position);
        textView1.setText(file.getName());

        // find wether dir or file and set image

        //String s=value[position];

        if (file.isDirectory()){
            imageView.setImageResource(R.drawable.folder);
            textView2.setText(" ");
        }
        else{
            imageView.setImageResource(R.drawable.file);
            if (file.length()>0){
                long  i=file.length()/1000;

                if (i<=1024){
                    int size= (int) i;
                    textView2.setText(size+"Kb");
                }
                if (i>1024){
                    int size= (int) i/1000;
                    textView2.setText(size+"Kb");
                }

            }
        }

        return rowView;
    }
    @Override
    public File getItem(int i){
        return files.get(i);
    }
}
