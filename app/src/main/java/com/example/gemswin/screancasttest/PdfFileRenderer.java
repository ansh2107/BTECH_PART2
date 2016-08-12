package com.example.gemswin.screancasttest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.pdf.PdfRenderer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.gemswin.screancasttest.ScreenCastLib.DecoderAsyncTask;
import com.example.gemswin.screancasttest.ScreenCastLib.MediaCodecFactory;
import com.example.gemswin.screancasttest.ScreenCastLib.VideoChunk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


public class PdfFileRenderer extends Activity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,EncoderAsyncTask.MediaCodecListener,TextureView.SurfaceTextureListener {

    private static final short GET_MEDIA_PROJECTION_CODE = 986;
    private MediaCodecFactory mMediaCodecFactory;
    private TextureView mTextureView;
    private EncoderAsyncTask mEncoderAsyncTask;
    private DecoderAsyncTask mDecoderAsyncTask;
    private SenderAsyncTask mSenderAsyncTask;
  // private MyService mSenderAsyncTask;
    public LinearLayout linearLayout, closeLayout;

    ViewFlipper viewFlipper;
    Animation slide_in_left, slide_out_right;
    Animation slide_in_right, slide_out_left;
    private ImageView imageView;
    public int currentpage = 0, imageNo = -1, ckpg = 0, cknt = 0, tPage;
    private Button next, previous, canvas;
    private ImageButton close;
    private GestureDetectorCompat detector;
    private CanvasMain canvasxml;
    public Bitmap bitmap;
    String pdfpath;
    public int REQ_WIDTH, REQ_HEIGHT;
    public String dirPath, name, imgDir, imgFile;
    public File file;
    public static boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int height = metrics.heightPixels / 3;
        final int width = metrics.widthPixels / 3;
        mMediaCodecFactory = new MediaCodecFactory(width, height);
        setContentView(R.layout.activity_pdf_file_renderer);
        // setContentView(R.layout.activity_canvas_main);
        // canvasxml =new CanvasMain(this);
        //setContentView(canvasxml);
        //setContentView(new CanvasMain(this,null)); //too run canvas
        //canvasxml = (Canvas_Main) findViewById(R.id.canvasxml);
        //customCanvas= (Canvas_Main) findViewById(R.id.canvasXml);
        // this.canvasxml= (Canvas_Main) this.findViewById(R.id.canvasxml);
        linearLayout = (LinearLayout) findViewById(R.id.pdfbttn);
        closeLayout = (LinearLayout) findViewById(R.id.closelayout);
        detector = new GestureDetectorCompat(this, this);
        detector.setOnDoubleTapListener(this);

        next = (Button) findViewById(R.id.next);
        previous = (Button) findViewById(R.id.previous);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        canvas = (Button) findViewById(R.id.canvas);
        close = (ImageButton) findViewById(R.id.close);
        imageView = (ImageView) findViewById(R.id.image);

        if (getIntent().hasExtra("pdfpath")) {
            pdfpath = getIntent().getStringExtra("pdfpath");

            file = new File(pdfpath);
            name = file.getName();
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name;
            //dirPath=getFilesDir().getAbsolutePath()+File.separator+name;
            imgDir = dirPath + File.separator + name + "_" + currentpage + ".png";
            imgFile = dirPath + File.separator + name + "_" + imageNo + ".png";
            render();
            //next.performClick();
        }

        @SuppressWarnings("ResourceType") MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), GET_MEDIA_PROJECTION_CODE);


//final ViewTreeObserver vto=imageView.getViewTreeObserver();
//            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//
//                public boolean onPreDraw() {
//                    REQ_WIDTH = imageView.getMeasuredWidth();
//                    REQ_HEIGHT = imageView.getMeasuredHeight();
//                    return true;
//                }
//            });


        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        slide_in_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        //render();
        canvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                canvas();

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (check==0){
//                    render();
//                    check++;
//                }
                if (check) {
                    check = false;
                    return;
                }

                if (imageNo <= tPage) {
                    imageNo++;
                }

                File image = new File(dirPath + File.separator + name + "_" + imageNo + ".png");
                if (image.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(dirPath + File.separator + name + "_" + imageNo + ".png");
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                }
                //render();
                viewFlipper.setInAnimation(slide_in_right);
                viewFlipper.setOutAnimation(slide_out_left);
                viewFlipper.showPrevious();
                Log.d("gems", "swipe next");


            }

        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageNo >= 1) {
                    imageNo--;
                    File image = new File(dirPath + File.separator + name + "_" + imageNo + ".png");
                    if (image.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(dirPath + File.separator + name + "_" + imageNo + ".png");
                        imageView.setImageBitmap(bitmap);
                        imageView.invalidate();
                    }
                    // render();
                    viewFlipper.setInAnimation(slide_in_left);
                    viewFlipper.setOutAnimation(slide_out_right);
                    viewFlipper.showNext();
                    Log.d("gems", "swipe previous");

                }


            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        next.performClick();
    }

    public void canvas() {


        Intent intent = new Intent(this, CanvasMain.class);

        intent.putExtra("fileName", name);
        intent.putExtra("pageNo", (int) imageNo);

        startActivity(intent);
    }


    private void render() {
        try {


            Log.d("pdf", "width=" + REQ_WIDTH);
            Log.d("pdf", "height=" + REQ_HEIGHT);
            Log.d("pdf", "currentpge=" + currentpage);


            //File file=new File("/sdcard/Download/Redis_Cluster.pdf");
            //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Redis_Cluster.pdf");

            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

//            if (currentpage < 0) {
//                currentpage = 0;
//
//            } else if (currentpage > renderer.getPageCount()) {
//                currentpage = renderer.getPageCount() - 1;
//
//            }
            tPage = renderer.getPageCount();
            for (; currentpage <= tPage; currentpage++) {
                PdfRenderer.Page page = renderer.openPage(currentpage);
                REQ_HEIGHT = page.getHeight();
                REQ_WIDTH = page.getWidth();
                bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_4444);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                try {

                    File test = new File(dirPath);
                    if (!test.exists())
                        test.mkdir();
                    if (test.exists()) {
                        int x = test.listFiles().length;
                        Log.d("pdf", "number of files=" + x);
                        if (x == tPage + 1) {
                            Log.d("pdf", "Renderer already renders");
                            break;

                        } else {
                            OutputStream stream = new FileOutputStream(dirPath + File.separator + name + "_" + currentpage + ".png");
                            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                            stream.flush();
                            stream.close();
                            // Toast.makeText(getApplicationContext(), "image saved", Toast.LENGTH_LONG).show();
                            page.close();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), "render compleated", Toast.LENGTH_LONG).show();

//            imageView.setImageBitmap(bitmap);
//            imageView.invalidate();
//            renderer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(LinearLayout.GONE);
            closeLayout.setVisibility(LinearLayout.GONE);
        } else {
            linearLayout.setVisibility(LinearLayout.VISIBLE);
            closeLayout.setVisibility(LinearLayout.VISIBLE);
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        if (mEncoderAsyncTask != null) {
            mEncoderAsyncTask.cancel(true);
            mEncoderAsyncTask = null;
        }
      if(mDecoderAsyncTask!=null){
            mDecoderAsyncTask.cancel(true);
            mDecoderAsyncTask = null;
       }
        if (mSenderAsyncTask != null) {
            mSenderAsyncTask.cancel(true);
            mSenderAsyncTask = null;
        }
        super.onDestroy();
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

    @Override
    public void onData(VideoChunk chunk) {
        if (mSenderAsyncTask != null) {
            mSenderAsyncTask.addChunk(chunk);
        }

    }

    @Override
    public void onEnd() {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> ip = MainActivity.ipArray;
//        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, 0);
//        final SharedPreferences.Editor edit = sharedPreferences.edit();
//        edit.putString(RECEIVER_IP_KEY,ip);
//        edit.commit();

        if (resultCode == RESULT_OK && requestCode == GET_MEDIA_PROJECTION_CODE) {
            try {
                @SuppressWarnings("ResourceType") MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                final MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                mEncoderAsyncTask = new EncoderAsyncTask(this, mediaProjection, mMediaCodecFactory);
                mEncoderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

               // mSenderAsyncTask = new MyService();

                mSenderAsyncTask = new SenderAsyncTask(getApplicationContext());
                mSenderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

              /*  int i =1;
            Intent intent = new Intent(PdfFileRenderer.this, MyBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getBaseContext(), 234324243, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);

*/


            } catch (IOException e) {
//                mStartButton.setEnabled(false);
//                mStartButton.setText(getString(R.string.mediacodec_error));
                e.printStackTrace();
            }

        }
    }
}
