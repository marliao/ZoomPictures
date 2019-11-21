package com.marliao.zoompictures;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 这个demo对图片的控制仅仅在ImageView中，也就是说在ImageView中的图片可以进行
 * 移动，放大缩小操作，无法对整个ImageView操作
 */
public class MainActivity extends AppCompatActivity {

    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int NONE = 0;

    private Matrix matrix = new Matrix();
    private Matrix saveMatrix = new Matrix();
    //第一个按下手指的点
    private PointF startPointf = new PointF();
    //两个按下手机的触摸点的中心点
    private PointF midPointf = new PointF();
    //初始的两个手指按下的触摸点的距离
    private float oriDis = 1f;

    private ImageView ivPicture;
    private LinearLayout llRoot;

    private int mode = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        //重写长安点击事件，避免影响其他点击事件
        ivPicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        //设置触摸监听
        ivPicture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return seTouchPicture((ImageView) v, event);
            }
        });
    }

    private boolean seTouchPicture(ImageView v, MotionEvent event) {
        ImageView imageView = v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //单点触控
                matrix.set(imageView.getImageMatrix());
                saveMatrix.set(matrix);
                startPointf.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //多点触控
                oriDis = distance(event);
                if (oriDis > 10f) {
                    saveMatrix.set(matrix);
                    midPointf = midPointf(event);
                    mode = ZOOM;
                }
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(saveMatrix);
                    matrix.postTranslate(event.getX() - startPointf.x, event.getY() - startPointf.y);
                } else if (mode == ZOOM) {
                    float newDis = distance(event);
                    if (newDis > 10f) {
                        matrix.set(saveMatrix);
                        float scale = newDis / oriDis;
                        matrix.postScale(scale, scale, midPointf.x, midPointf.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        imageView.setImageMatrix(matrix);
        return true;
    }

    /**
     * 计算两手指之间中心点的位置
     * x=(x1+x2)/2
     * y=(y1+y2)/2
     *
     * @param event 触摸事件
     * @return 返回中心点坐标
     */
    private PointF  midPointf(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    /**
     * 计算两指之间的距离
     *
     * @param event 触摸事件
     * @return 返回两个手指之间的距离
     */
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float sqrt = (float) Math.sqrt(x * x + y * y);
        return sqrt;
    }

}
