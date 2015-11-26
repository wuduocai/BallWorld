package com.ballworld.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ballworld.activity.MainActivity;
import com.ballworld.activity.R;

import static com.ballworld.util.Constant.*;

/**
 * Created by duocai at 20:30 on 2015/10/31.
 */
public class WelcomeView extends SurfaceView implements SurfaceHolder.Callback {//实现生命周期回调接口
    //声明变量
    MainActivity activity;//activity的引用
    Paint paint;      //画笔
    int currentAlpha = 0;  //当前的不透明值
    int sleepSpan = 50;      //动画的时延ms
    Bitmap[] logos = new Bitmap[2];//logo图片数组
    Bitmap currentLogo;  //当前logo图片引用
    int currentX;//图片所画位置
    int currentY;

    public WelcomeView(MainActivity activity) {
        super(activity);
        this.activity = activity;
        this.getHolder().addCallback(this);  //设置生命周期回调接口的实现者
        paint = new Paint();  //创建画笔
        paint.setAntiAlias(true);  //打开抗锯齿
        //加载图片
        logos[0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.welcome);
        logos[1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.logo);
    }

    /**
     * 重写的屏幕监听器
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 屏幕被按下
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            activity.hd.sendEmptyMessage(0);
        }
        return super.onTouchEvent(event);
    }


    /**
     * 绘制画面
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        //绘制黑填充矩形清背景
        paint.setColor(Color.BLACK);//设置画笔颜色
        paint.setAlpha(255);//设置不透明度为255
        canvas.drawRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, paint);

        if (currentLogo == null)
            return;
        paint.setAlpha(currentAlpha);//设置透明度，该透明度不断改变
        canvas.drawBitmap(currentLogo, currentX, currentY, paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//创建时被调用
        new Thread() {
            public void run() {
                for (Bitmap bm : logos) {
                    currentLogo = bm;//当前图片的引用
                    currentX = SCREEN_WIDTH / 2 - bm.getWidth() / 2;//图片位置，居中
                    currentY = SCREEN_HEIGHT / 2 - bm.getHeight() / 2;

                    //动态更改图片的透明度值并不断重绘
                    for (int i = 255; i > -10; i = i - 10) {
                        currentAlpha = i;
                        if (currentAlpha < 0) {//如果当前不透明度小于零
                            currentAlpha = 0;//将不透明度置为零
                        }

                        //获取画布
                        SurfaceHolder myholder = WelcomeView.this.getHolder();//获取回调接口
                        Canvas canvas = myholder.lockCanvas();//获取画布
                        try {
                            synchronized (myholder) {//同步
                                onDraw(canvas);//进行绘制
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (canvas != null) {//如果当前画布不为空
                                myholder.unlockCanvasAndPost(canvas);//解锁画布
                            }
                        }

                        //暂停线程，来实现动画效果
                        try {
                            if (i == 255) {//若是新图片，多等待一会
                                Thread.sleep(100);
                            }
                            Thread.sleep(sleepSpan);
                        } catch (Exception e) {//抛出异常
                            e.printStackTrace();
                        }
                    }
                }

                //开机动画结束
                activity.hd.sendEmptyMessage(0);//发送消息，进入到主菜单界面
            }
        }.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
}
