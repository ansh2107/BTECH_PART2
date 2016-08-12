package com.example.gemswin.screancasttest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class CanvasMain extends Activity implements GestureDetector.OnDoubleTapListener,GestureDetector.OnGestureListener {
    CanvasView canvasView;
    Button clear,undo,redo,newCanvas;
    ImageButton close;
   private LinearLayout linearLayout;
    ImageView imageView;
    private GestureDetectorCompat detector;
    public int i;
   public  String fileName;
   public  Integer pageNo;
    public  String page;
    public String filePath;
   public static String[] data1;
    Spinner colour,options,canvasPngs;
    ArrayAdapter<CharSequence> adapter,adapter1;
    ArrayAdapter<String> adapter2;
    static boolean canvasck=false;
    static boolean canvaspngselck=false;
    static int Canvasno=0;
    String[] canvasname;
    static int canvasSelno=0;
    static int canvasPgNo;
    ArrayList<String> canvasNameArray=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setContentView(new CanvasView(this,null));


        if (getIntent().hasExtra("fileName")){
            fileName = getIntent().getStringExtra("fileName");
            pageNo = getIntent().getExtras().getInt("pageNo");
            page=String.valueOf(pageNo);
            data1=new String[2];
            data1[0]=fileName;
            data1[1]=page;
            Log.d("canvasmain", "pageno=" + data1[1]);
            Log.d("canvasmain", "fileName=" + data1[0]);
            canvasck=false;
        }else{
            canvasck=true;
        }


        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_canvas_main);

        //canvasView=new CanvasView(this,null);

        detector = new GestureDetectorCompat(this, this);
        detector.setOnDoubleTapListener(this);
        canvasView= (CanvasView) findViewById(R.id.canvasxml);
        clear= (Button) findViewById(R.id.clear);
        linearLayout= (LinearLayout) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.image);
        close= (ImageButton) findViewById(R.id.close);
        undo= (Button) findViewById(R.id.undo);
        redo= (Button) findViewById(R.id.redo);
        newCanvas= (Button) findViewById(R.id.newCanvas);


        //spinner
        colour= (Spinner) findViewById(R.id.colour);
        adapter=ArrayAdapter.createFromResource(this,R.array.Colour,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colour.setAdapter(adapter);

        options= (Spinner) findViewById(R.id.options);
        adapter1=ArrayAdapter.createFromResource(this,R.array.Options,android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(adapter1);


        directoryck();
        canvasPngs= (Spinner) findViewById(R.id.canvasPngs);
        adapter2=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,canvasNameArray);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        canvasPngs.setAdapter(adapter2);

        filePath= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+fileName+File.separator+fileName+"_"+pageNo+".png";
        Bitmap bitmap= BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);

        canvasView.setDrawingCacheEnabled(true);
        canvasView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        canvasView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

canvasPngs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        save();
        canvasView.paths.clear();
        canvasSelno=position;
        canvaspngselck=true;
        canvasView.createPng();
        canvasView.invalidate();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});
options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                canvasView.onClickPen();
                break;
            case 1:
                canvasView.onClickEraser();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});

colour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                CanvasView.colour = Color.BLACK;
                break;
            case 1:
                CanvasView.colour = Color.RED;
                break;
            case 2:
                CanvasView.colour = Color.GREEN;
                break;
            case 3:
                CanvasView.colour = Color.BLUE;
                break;
            default:
                CanvasView.colour = Color.BLACK;
                break;
        }


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});
        newCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                canvasView.paths.clear();
                directoryck();
                //adapter2.notifyDataSetChanged();
                Canvasno=canvasNameArray.size();
                Canvasno++;
                canvasSelno=Canvasno;
                canvasView.createPng();
                canvasView.invalidate();
                adapter2.notifyDataSetChanged();

                            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.onClickUndo();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.onClickRedo();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                PdfFileRenderer.check=true;
                finish();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.clearCanves();
            }
        });
    }


    public void save(){
        Bitmap temp=canvasView.getDrawingCache();
        try {
            // String dirPath=getFilesDir().getAbsolutePath()+File.separator+fileName+File.separator+"canvas";
            String dirPath=canvasView.dirPath;
            File file=new File(dirPath);
            if(!file.exists())
                file.mkdir();
            if (file.exists()){
                // OutputStream stream=new FileOutputStream(dirPath+File.separator+pageNo+".png");
                OutputStream stream=new FileOutputStream(canvasView.dirFile);
                temp.compress(Bitmap.CompressFormat.PNG, 80, stream);
                stream.flush();
                stream.close();
                Toast.makeText(getApplicationContext(), "image saved", Toast.LENGTH_LONG).show();
                //canvasView.clearCanves();
                canvasView.destroyDrawingCache();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
    public static String[] getData(){

        return data1;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        if (linearLayout.getVisibility() == View.VISIBLE)
            linearLayout.setVisibility(LinearLayout.GONE);
        else
            linearLayout.setVisibility(LinearLayout.VISIBLE);
        return false;
    }

    private void directoryck(){
        String path=canvasView.dirPath;
        Log.d("Files", path);
        File f=new File(path);
        File file[]=f.listFiles();
        //canvasname=new String[file.length];

        for (int i=0; i < file.length; i++)
        {
           // canvasname[i]=file[i].getName();

            canvasNameArray.add(file[i].getName().toString());
            Log.d("Files", "FileName:" + file[i].getName());
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
