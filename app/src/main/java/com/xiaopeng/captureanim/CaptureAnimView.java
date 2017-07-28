package com.xiaopeng.captureanim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by linzx on 17-7-27.
 * for the Capture catch and exit anim
 */

public class CaptureAnimView extends View {
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

    public void setCapAnimListener(CaptureAnimListener listener){
        this.mCapAnimListener = listener;
    }
    public CaptureAnimView(Context context) {
        super(context);
        init(context);
    }

    public CaptureAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CaptureAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint.setAntiAlias(true);
        mMeshHelper = new MeshHelper();
        valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(acInterpolator);
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

    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
        mMeshHelper.setBitmapDet(bitmap.getWidth(),bitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap==null || mMeshHelper==null)return;
        float[] mesh = mMeshHelper.setPosi(posi);
//        Log.d(TAG,"onDraw: mesh size:"+mesh.length);
        canvas.drawBitmapMesh(mBitmap,mMeshHelper.getVetWidth(),mMeshHelper.getVetHeight()
                ,mesh,0,null,0,mPaint);
    }

    /**
     *  应用需求,需要根据在xml描述的宽高自适应故这么写,
     *
     */
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

    public void setPosi(float posi){
        this.posi = posi;
        invalidate();
    }

    public interface CaptureAnimListener{
        void onCaptureAnimEnd();
    }

    public void beginAnim(){
        if (valueAnimator!=null){
            valueAnimator.start();
        }
    }
}