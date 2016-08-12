package com.example.gemswin.screancasttest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class TakeAttendence extends Activity {
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendence);

        for(int i = 1; i<=130;i++)
        {
            GridLayout linear1 = (GridLayout) findViewById(R.id.linearlayout);
            b = new Button(this);
            b.setText(Integer.toString(i))
            ;
            b.setTextSize(10);
            b.setLayoutParams(new LinearLayout.LayoutParams(135, LinearLayout.LayoutParams.WRAP_CONTENT));
            linear1.addView(b);
            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Button l = (Button)v;
                    Toast.makeText(getApplicationContext(), "Yipee.."+ l.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}
