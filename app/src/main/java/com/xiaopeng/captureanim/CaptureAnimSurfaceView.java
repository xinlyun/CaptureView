package com.xiaopeng.captureanim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by linzx on 17-7-27.
 */

public class CaptureAnimSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceTexture texture;
    private Context mContext;
    private CaptureAnimListener mCapAnimListener;
    private static final String TAG = "CaptureAnimView";
    private float width,height;
    private AccelerateDecelerateInterpolator acInterpolator = new AccelerateDecelerateInterpolator();

    private ValueAnimator valueAnimator;
    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private MeshHelper mMeshHelper;
    private float posi;
    SurfaceHolder surfaceHolder;
    public void setCapAnimListener(CaptureAnimListener listener){
        this.mCapAnimListener = listener;
    }

    public CaptureAnimSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CaptureAnimSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CaptureAnimSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
//        width = getScreenWidth(context);
//        height = getScreenHeight(context);
        surfaceHolder = getHolder();
        mPaint.setAntiAlias(true);
        mMeshHelper = new MeshHelper();
        valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(800);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float posi = (float) animation.getAnimatedValue();
                setPosi(posi);
                if (posi==1f && mCapAnimListener!=null){
                    mCapAnimListener.onCaptureAnimEnd();
                }
            }
        });
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceHolder = holder;
    }

    public interface CaptureAnimListener{
        void onCaptureAnimEnd();
    }



    public void setPosi(float posi){
        this.posi = posi;
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas!=null) {
            onDrawThis(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        mMeshHelper.setBitmapDet(bitmap.getWidth(),bitmap.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measureHanlder(1080,widthMeasureSpec);
        height = measureHanlder(1920,heightMeasureSpec);
        Log.d(TAG,"onMeasure width:"+width +"  height:"+height);
        mMeshHelper.init((int)width,(int)(height*0.95f));
        if (mBitmap!=null){
            mMeshHelper.setBitmapDet(mBitmap.getWidth(),mBitmap.getHeight());
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    private int measureHanlder(int defaultSize,int measureSpec){
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    private void onDrawThis(Canvas canvas){
        if (mBitmap==null || mMeshHelper==null)return;
        // 设置画布颜色
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        float[] mesh = mMeshHelper.setPosi(posi);
//        Log.d(TAG,"onDraw: mesh size:"+mesh.length);
        canvas.drawBitmapMesh(mBitmap,mMeshHelper.getVetWidth(),mMeshHelper.getVetHeight()
                ,mesh,0,null,0,mPaint);
    }

    public void beginAnim(){
        if (valueAnimator!=null){
            valueAnimator.start();
        }
    }


}
