package com.xiaopeng.captureanim;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CaptureAnimView mView;
    private SeekBar mSeekBar;
    private Button goBtn;
    Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (CaptureAnimView) findViewById(R.id.mCap);
        mView.setCapAnimListener(new CaptureAnimView.CaptureAnimListener() {
            @Override
            public void onCaptureAnimEnd() {
                Toast.makeText(getApplication(),"finish ok",Toast.LENGTH_SHORT).show();
            }
        });
        mSeekBar = (SeekBar) findViewById(R.id.mSeekBar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setMax(1000);
        findViewById(R.id.goBtn).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBitmap==null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my_bitmap);
        }
        mView.setBitmap(mBitmap);
    }

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mView!=null){
                float posi = ((float) progress/1000f);
                mView.setPosi(posi);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBtn:
                mView.setVisibility(View.VISIBLE);
                mView.beginAnim();
                break;
        }
    }
}
