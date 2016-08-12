package com.example.gemswin.screancasttest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pc on 05-07-2015.
 */
public class CanvasView extends View {
    public int width, height;
    public Bitmap bitmap;
    private Canvas mcanvas;
    private Path path;
    Context mcontext;
    private static Paint paint;
    private float x, y;
    private static final float tolerance = 5;
    private int i=1;
    public String dirPath,dirFile;
    public File pngFolder,pngFile;
    private Bitmap temp;
    public String name;
    public String pageNo;
    public  String[] data=new String[1];


    ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();
    ArrayList<Pair<Path, Paint>> undopaths = new ArrayList<Pair<Path, Paint>>();
    ArrayList<Pair<Path, Paint>> clear;
    boolean check=false;
    boolean isEraser=false;


    public static int colour;
    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mcontext = context;
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

        createPng();


    }

    public void createPng(){
        if (CanvasMain.canvasck){
            if (CanvasMain.canvaspngselck){
                dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Canvas";
                //dirPath=getContext().getFilesDir().getAbsolutePath()+File.separator+"Canvas";
                File file=new File(dirPath);
                if(!file.exists())
                    file.mkdir();
                Log.d("dir", "dir=" + dirPath);
                //pngFolder = new File(dirPath);
                dirFile=dirPath +File.separator+CanvasMain.canvasSelno+".png";
                pngFile=new File(dirFile);
                if (pngFile.exists()) {
                    temp = BitmapFactory.decodeFile(dirFile);
                }
                CanvasMain.canvaspngselck=false;
            }else {
                dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Canvas";
                //dirPath=getContext().getFilesDir().getAbsolutePath()+File.separator+"Canvas";
                File file=new File(dirPath);
                if(!file.exists())
                    file.mkdir();
                Log.d("dir", "dir=" + dirPath);
                //pngFolder = new File(dirPath);
                dirFile=dirPath +File.separator+CanvasMain.canvasSelno+".png";
                pngFile=new File(dirFile);
                if (pngFile.exists()) {
                    temp = BitmapFactory.decodeFile(dirFile);
                }
            }


        }else{
            String[] data=new String[2];
            data=CanvasMain.data1;
            name=data[0];
            pageNo=data[1];
            dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+name+File.separator+"canvas";
            //dirPath=getContext().getFilesDir().getAbsolutePath()+File.separator+name+File.separator+"canvas";
            File file=new File(dirPath);
            if(!file.exists())
                file.mkdir();
            Log.d("dir", "dir=" + dirPath);
            //pngFolder = new File(dirPath);
            dirFile=dirPath +File.separator+pageNo+".png";
            Log.d("canvas", "pgno=" + pageNo);
            Log.d("canvas", "name=" + name);
            pngFile=new File(dirFile);
            if (pngFile.exists()) {
                Log.d("canvas", "name=" + name);
                Log.d("canvas", "pgno=" + pageNo);
                temp = BitmapFactory.decodeFile(dirFile);
                CanvasMain.canvasck=true;
            }


        }

    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            mcanvas = new Canvas(bitmap);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       paint.setColor(colour);
       canvas.drawPath(path, paint);
        if (pngFile.exists())
         canvas.drawBitmap(temp, 0, 0, null);
        Draw(canvas);



        }

    private void startTouch(float mx, float my) {
        //path.reset();
        //path=new Path();
        path.moveTo(mx, my);
        x = mx;
        y = my;
    }

    private void moveTouch(float mx, float my) {
        float dx = Math.abs(mx - x);
        float dy = Math.abs(my - y);
        if (dx >= tolerance || dy >= tolerance) {
            path.quadTo(x, y, (mx + x) / 2, (my + y) / 2);
            x = mx;
            y = my;
        }
    }

    public void clearCanves() {
        clear = new ArrayList<Pair<Path, Paint>>(paths);
        paths.clear();
        check=true;
        invalidate();

    }


    private void upTouch() {
        path.lineTo(x, y);
       // path=new Path();
        Paint newPaint = new Paint(paint); // Clones the mPaint object
        paths.add(new Pair<Path, Paint>(path, newPaint));
        path=new Path();
    }

    public  void onClickUndo ()
    {

        if(paths.size()>0)
        {
            undopaths.add(paths.remove(paths.size()-1));
            invalidate();
        }

    }

    public  void onClickRedo ()
    {
        if (check){
            paths=new ArrayList<Pair<Path, Paint>>(clear);
            clear.clear();
            check=false;
            invalidate();
        }

        else if (undopaths.size()>0)
        {
            paths.add(undopaths.remove(undopaths.size()-1));
            invalidate();
        }
    }


    public void onClickEraser()
    {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStrokeWidth(7f);
    }
    public void onClickPen() {
        paint.setXfermode(null);
        paint.setStrokeWidth(4f);

    }

    public void Draw(Canvas canvas){
        for (Pair<Path, Paint> p : paths)
        {
            canvas.drawPath(p.first, p.second);
        }
        Log.d("paths:", String.valueOf(paths.size()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }



}
