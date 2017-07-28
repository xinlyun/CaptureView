package com.xiaopeng.captureanim;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by linzx on 17-7-27.
 * 供给CaptureAnimView的支持类,核心数据计算均在此处
 */

public class MeshHelper {
    private float width,height;
    private float mapWidth,mapHeight;
    private int WIDTH_DET = 40,HEIGHT_DET = 40;
    //核心移动曲线
    private AccelerateDecelerateInterpolator mInterpolator = new AccelerateDecelerateInterpolator();



    public void init(int width,int height){
        this.width = width;
        this.height = height;
    }

    public void setBitmapDet(int mapWidth,int mapHeight){
        this.mapWidth = width;
        this.mapHeight = width/mapWidth * mapHeight;
    }

    /**
     | ------------------------------
     | |                          ||
     | \                         / |
     |  |                       /  |
     |  |                      /   |
     |  | leftLine            /    |
     |  \                    /     |
     |   |                  /      |
     |   |                 /       |
     |   |                /        |
     |   |               /         |
     |   \              /          |
     |    |            /rightLine  |
     |    |           /            |
     |    |          /
     | 0.1f        0.3f
     |
     * @param posi
     * @return
     */
    public float[] setPosi(float posi){
        //左右边线运动轨迹
        LinePosi leftLine,rightLine;

        //在0~0.3f的部分,左右轨迹要逐渐向中心靠拢
        if (posi<=0.3f){
            leftLine = new LinePosi(0,width*0.1f* (posi/0.3f),0,height);
            rightLine = new LinePosi(width,width*0.2f+width*0.8f*(0.3f-posi)/0.3f,0,height);
        }else {
            //在0.3f~1f,左右轨迹保持不变,图像按照此轨迹作为边界进行运动
            leftLine = new LinePosi(0,width*0.1f,0,height);
            rightLine = new LinePosi(width,width*0.2f,0,height);
        }
        float destY = height * posi;


        float[] newFloat = new float[(WIDTH_DET+1)*(HEIGHT_DET+1)*2];

        int num = 0;

        for (int i = 0;i<=HEIGHT_DET;i++){
            for (int j=0;j<=WIDTH_DET;j++){

                //Y轴点位根据实际bitmap高度进行平均
                float posiY = destY + mapHeight * i / HEIGHT_DET;

                float leftPosiX = leftLine.inputY(posiY);
                float rightPosiX = rightLine.inputY(posiY);
                float disPosiX = rightPosiX - leftPosiX;

                //X点位根据两个line在Y值时的差值进行平均分布
                float posiX = disPosiX * j /WIDTH_DET + leftPosiX;

                //先X后Y输出
                newFloat[num] = posiX;
                num++;
                newFloat[num] = posiY;
                num++;
            }
        }

        return newFloat;
    }

    public int getVetWidth() {
        return WIDTH_DET;
    }

    public int getVetHeight() {
        return HEIGHT_DET;
    }

    /**
     * 描述运动轨迹(起点XY和终点XY以及变函数Interpolator),根据输入量,可输出相对应的值
     */
    private class LinePosi{
        private float fromX,toX,fromY,toY;
        LinePosi(float fromX,float toX,float fromY,float toY){
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public float inputY(float yP){
            float disLength = toY - fromY;
            float disXLength = toX - fromX;
            float disFloat = yP / disLength;
            float disXFloat = mInterpolator.getInterpolation(disFloat);
            float merage = disXLength * disXFloat;
            return fromX + merage;
        }
    }
}

