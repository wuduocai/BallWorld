
package com.ballworld.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import com.ballworld.activity.MainActivity;
import com.ballworld.activity.R;
import com.ballworld.mapEntity.Ball;
import com.ballworld.mapEntity.Bomb;
import com.ballworld.mapEntity.CoverBlock;
import com.ballworld.mapEntity.Number;
import com.ballworld.mapEntity.Road;
import com.ballworld.mapEntity.Walls;
import com.ballworld.thread.BallMoveThread;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import static com.ballworld.util.Constant.ALL_INIT_LOCATION;
import static com.ballworld.util.Constant.ALL_MAP;
import static com.ballworld.util.Constant.ALL_MAP_BOMB;
import static com.ballworld.util.Constant.ALL_TARGET_LOCATION;
import static com.ballworld.util.Constant.DISTANCE;
import static com.ballworld.util.Constant.UNIT_SIZE;
import static com.ballworld.util.Constant.V_TENUATION;
import static com.ballworld.util.Constant.ballR;

/**
 * Created by duocai at 18:57 on 2015/10/31.
 */
public class GameView extends GLSurfaceView {
    public static float ballGX;
    public static float ballGZ;
    //实现3d旋转
    public static float yAngle = 0f;//方位角
    public static float xAngle = 90f;//仰角
    public static float cx;//摄像机x坐标
    public static float cy;//摄像机y坐标
    public static float cz;//摄像机z坐标
    public static float tx = 0;//观察目标点x坐标
    public static float ty = 0;//观察目标点y坐标
    public static float tz = 0f;//观察目标点z坐标
    //地图上的对象
    public static int[][] map;//对应关卡的地图数组
    public MainActivity activity;//调用该view的activity
    //关卡信息
    public int levelId;//关卡id

    //路面类型
    public int[][] mapBomb;//对应关卡的洞数组
    public int[][] mapBombNumber;//提示数字
    public int[][] coverBlocks;//覆盖方块
    public Road road;
    public Walls walls;//墙
    public Ball ball;//球
    public Bomb bomb;//炸弹
    public CoverBlock coverBlock;//覆盖方块
    private BallMoveThread ballMoveThread;//小球移动线程
    private MyRenderer myRenderer;//渲染器
    //图片纹理Id，渲染画面使用
    private int roadId;//路
    private int wallId;//墙
    private int bombId;//炸弹
    private int coverBlockId;//覆盖方块
    private int ballId;//球
    private int targetId;//终点目标
    private int numberId;//数字

    public GameView(MainActivity activity, int levelId) {
        super(activity);
        //初始化变量
        this.activity = activity;
        this.levelId = levelId;
        //摄像机
        tx = 0;//摄像机目标位置
        ty = 0;
        tz = 0;
        //设置摄像机的位置
        cx = (float) (tx + Math.cos(Math.toRadians(xAngle)) * Math.sin(Math.toRadians(yAngle)) * DISTANCE);//摄像机x坐标
        cz = (float) (tz + Math.cos(Math.toRadians(xAngle)) * Math.cos(Math.toRadians(yAngle)) * DISTANCE);//摄像机z坐标
        cy = (float) (ty + Math.sin(Math.toRadians(xAngle)) * DISTANCE);//摄像机y坐标

        //关卡速度,及其它特色
        switch (levelId) {
            case 0:
                V_TENUATION=0.95f;
                break;
            case 1:
                V_TENUATION=0.4f;
                break;
            case 2:
                V_TENUATION=0.7f;
                break;
            case 3:
                V_TENUATION=0.7f;
        }
        //初始化地图上的对象
        map = ALL_MAP[levelId];//地图
        mapBomb = new int[map.length][map[0].length];//炸弹
        for (int i = 0; i < map.length; i++) {//init，炸弹，要拷贝，不能直接相等
            for (int j = 0; j < map[0].length; j++) {
                mapBomb[i][j] = ALL_MAP_BOMB[levelId][i][j];
            }
        }
        mapBombNumber = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {//init，提示数
            for (int j = 0; j < map[0].length; j++) {
                int startZ = (i - 1 > 0) ? i - 1 : 0;
                int endZ = (i + 2 <= map.length) ? i + 2 : map.length;
                int startX = (j - 1 > 0) ? j - 1 : 0;
                int endX = (j + 2 <= map[0].length) ? j + 2 : map[0].length;
                int totalNumber = 0;
                for (int m = startZ; m < endZ; m++) {
                    for (int n = startX; n < endX; n++) {
                        if (mapBomb[m][n] == 1)
                            totalNumber++;
                    }
                }
                mapBombNumber[i][j] = totalNumber;
            }
        }
        coverBlocks = new int[map.length][map[0].length];
        for (int i = 0; i < coverBlocks.length; i++) {//init，全覆盖
            for (int j = 0; j < coverBlocks[0].length; j++) {
                coverBlocks[i][j] = 1;
            }
        }
        road = new Road(map[0].length, map.length);//地板
        ball = new Ball(ballR, 15);//小球
        ball.ballGX = ball.ballGZ = ball.ballVX = ball.ballVZ = 0f;
        ball.ballCsX = ALL_INIT_LOCATION[levelId][0];//读取球初始格子
        ball.ballCsZ = ALL_INIT_LOCATION[levelId][1];
        ball.ballX = ball.ballCsX * UNIT_SIZE - map[0].length * UNIT_SIZE / 2 + UNIT_SIZE / 2;//初始化球位置
        ball.ballZ = ball.ballCsZ * UNIT_SIZE - map.length * UNIT_SIZE / 2 + UNIT_SIZE / 2;
        ball.ballMbX = ALL_TARGET_LOCATION[levelId][0];
        ball.ballMbZ = ALL_TARGET_LOCATION[levelId][1];
        walls = new Walls();
        bomb = new Bomb(1, 1);//传参为长宽占几个单位
        coverBlock = new CoverBlock(1, 1);

        //设置渲染器
        myRenderer = new MyRenderer();
        this.setRenderer(myRenderer);
        //设置渲染模式为自动渲染，
        //系统在调用surfaceCreated之后，
        //系统会自动每隔一段时间调用onDrawFrame
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        //开启小球移动线程
        ballMoveThread = new BallMoveThread(this);
        ballMoveThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return true;
    }

    /**
     * 初始化图片纹理
     * 需重复绘制的
     */
    public int initRepeatTexture(GL10 gl, int drawableId) {
        //生成纹理ID
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);//获得材质纹理id
        int currTextureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);//向gL库添加该纹理

        //在MIN_FILTER MAG_FILTER中使用MIPMAP纹理
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
        // 生成Mipmap纹理
        ((GL11) gl).glTexParameterf(GL10.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL10.GL_TRUE);
        //重复绘制至充满
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        //获取图片资源
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //将图片资源与texture结合
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return currTextureId;
    }

    /**
     * 初始化纹理
     * 不需重复绘制的
     */
    public int initNoRepeatTexture(GL10 gl, int drawableId) {
        //生成纹理ID
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int currTextureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        //靠边线绘制一次
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        //获取图片资源
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //将图片资源与texture结合起来
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();//释放图片
        return currTextureId;
    }

    private class MyRenderer implements GLSurfaceView.Renderer {

        /**
         * Called when the surface is created or recreated.
         * <p/>
         * Called when the rendering thread
         * starts and whenever the EGL context is lost. The EGL context will typically
         * be lost when the Android device awakes after going to sleep.
         * <p/>
         * Since this method is called at the beginning of rendering, as well as
         * every time the EGL context is lost, this method is a convenient place to put
         * code to create resources that need to be created when the rendering
         * starts, and that need to be recreated when the EGL context is lost.
         * Textures are an example of a resource that you might want to create
         * here.
         * <p/>
         * Note that when the EGL context is lost, all OpenGL resources associated
         * with that context will be automatically deleted. You do not need to call
         * the corresponding "glDelete" methods such as glDeleteTextures to
         * manually delete these lost resources.
         * <p/>
         *
         * @param gl     the GL interface. Use <code>instanceof</code> to
         *               test if the interface supports GL11 or higher interfaces.
         * @param config the EGLConfig of the created surface. Can be used
         */
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //关闭抗抖动
            gl.glDisable(GL10.GL_DITHER);
            //设置特定Hint项目的模式，这里为设置为使用快速模式
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
            //设置为打开背面剪裁
            gl.glEnable(GL10.GL_CULL_FACE);
            //设置着色模型为平滑着色
            gl.glShadeModel(GL10.GL_SMOOTH);
            //开启混合
            //设置屏幕背景色黑色RGBA
            gl.glClearColor(0, 0, 0, 0);
            //启用深度测试
            gl.glEnable(GL10.GL_DEPTH_TEST);

            //初始化纹理
            switch (levelId) {//路面类型
                case 0:
                    roadId = initNoRepeatTexture(gl, R.drawable.snow);//路
                    wallId = initRepeatTexture(gl, R.drawable.snowwall);//墙
                    coverBlockId = initNoRepeatTexture(gl, R.drawable.snowcover);//覆盖方块
                    break;
                case 1:
                    roadId = initNoRepeatTexture(gl, R.drawable.desert);//路
                    wallId = initRepeatTexture(gl, R.drawable.desertwall);//墙
                    coverBlockId = initNoRepeatTexture(gl, R.drawable.desertcover);//覆盖方块
                    break;
                case 2:
                    roadId = initNoRepeatTexture(gl, R.drawable.grass);//路
                    wallId = initRepeatTexture(gl, R.drawable.grasswall);//墙
                    coverBlockId = initNoRepeatTexture(gl, R.drawable.grasscover);//覆盖方块
                    break;
                case 3:
                    roadId = initNoRepeatTexture(gl, R.drawable.water);//路
                    wallId = initRepeatTexture(gl, R.drawable.waterwall);//墙
                    coverBlockId = initNoRepeatTexture(gl, R.drawable.watercover);//覆盖方块
                    break;
            }
            bombId = initNoRepeatTexture(gl, R.drawable.bomb);//炸弹
            ballId = initNoRepeatTexture(gl, R.drawable.ball);//球
            //targetId = initNoRepeatTexture(gl, R.drawable.target);//终点目标
            numberId = initNoRepeatTexture(gl, R.drawable.number);//数字

            initLight(gl);//初始化灯光
            float[] positionParamsGreen = {-4, 4, 4, 0};//最后的0表示是定向光
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, positionParamsGreen, 0);
        }

        /**
         * Called when the surface changed size.
         * <p/>
         * Called after the surface is created and whenever
         * the OpenGL ES surface size changes.
         * <p/>
         * Typically you will set your viewport here. If your camera
         * is fixed then you could also set your projection matrix here:
         * <pre class="prettyprint">
         * void onSurfaceChanged(GL10 gl, int width, int height) {
         * gl.glViewport(0, 0, width, height);
         * // for a fixed camera, set the projection too
         * float ratio = (float) width / height;
         * gl.glMatrixMode(GL10.GL_PROJECTION);
         * gl.glLoadIdentity();
         * gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
         * }
         * </pre>
         *
         * @param gl     the GL interface. Use <code>instanceof</code> to
         *               test if the interface supports GL11 or higher interfaces.
         * @param width
         * @param height
         */
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            gl.glViewport(0, 0, width, height);
            //设置当前矩阵为投影矩阵
            gl.glMatrixMode(GL10.GL_PROJECTION);
            //设置当前矩阵为单位矩阵
            gl.glLoadIdentity();
            //计算透视投影的比例
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            gl.glFrustumf(-ratio, ratio, -1, 1, 3, 1000);
        }

        /**
         * Called to draw the current frame.
         * <p/>
         * This method is responsible for drawing the current frame.
         * <p/>
         * The implementation of this method typically looks like this:
         * <pre class="prettyprint">
         * void onDrawFrame(GL10 gl) {
         * gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
         * //... other gl calls to render the scene ...
         * }
         * </pre>
         *
         * @param gl the GL interface. Use <code>instanceof</code> to
         *           test if the interface supports GL11 or higher interfaces.
         */
        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glShadeModel(GL10.GL_SMOOTH);//采用平滑着色
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); //清除颜色缓存于深度缓存
            gl.glMatrixMode(GL10.GL_MODELVIEW);//设置当前矩阵为模式矩阵
            gl.glLoadIdentity();//设置当前矩阵为单位矩阵，原点定为屏幕中心
            GLU.gluLookAt(gl, cx, cy, cz, tx, ty, tz, 0, 1, 0); //设置摄像机，实现3D旋转，封装了glTranslatef和glRotatef
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//启用顶点数组
            gl.glEnable(GL10.GL_TEXTURE_2D);//启用纹理
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);//启用 UV array
            gl.glEnable(GL10.GL_LIGHTING);//允许光照,小球影子
            gl.glEnable(GL10.GL_LIGHT0);//开0号灯
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);//允许使用法向量数组

            //画物体
            //绘制地面
            road.drawSelf(gl, roadId);
            //绘制提示数字
            gl.glEnable(GL10.GL_BLEND);//开启混合
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);//设置混合因子
            gl.glPushMatrix();
            gl.glTranslatef(-map[0].length * UNIT_SIZE / 2 + UNIT_SIZE / 2, 0.01f, -map.length * UNIT_SIZE / 2 + UNIT_SIZE / 2);
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (mapBombNumber[i][j] != 0) {
                        Number number = new Number(mapBombNumber[i][j], 1, 1);
                        //移动到相应位置
                        number.x = (j) * UNIT_SIZE;
                        number.y = 0.001f;//略微浮起
                        number.z = (i) * UNIT_SIZE;

                        //小球在当前隔
                        float ballXTemp = map[0].length * UNIT_SIZE / 2 + ball.ballX;
                        float ballZTemp = map.length * UNIT_SIZE / 2 + ball.ballZ;
                        int iX = (int) (ballXTemp / UNIT_SIZE);
                        int iZ = (int) (ballZTemp / UNIT_SIZE);
                        //如果没有覆盖将数字提升到小球上方
                        if(coverBlocks[i][j]==0&&iZ==i&&iX==j){
                            number.y=1.5f;
                            if (j<11)
                                number.x+=UNIT_SIZE/3;
                            else if (j>22)
                                number.x-=UNIT_SIZE/3;
                            if (i<6)
                                number.z+=0.2f;
                            else if (i>12)
                                number.z-=0.2f;
                        }
                        number.drawSelf(gl, numberId);//绘制
                    }
                }
            }
            gl.glPopMatrix();
            gl.glDisable(GL10.GL_BLEND);//关闭混合
            //绘制炸弹
            gl.glEnable(GL10.GL_BLEND);//开启混合
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);//设置混合因子
            gl.glPushMatrix();
            gl.glTranslatef(-map[0].length * UNIT_SIZE / 2 + UNIT_SIZE / 2, 0.01f, -map.length * UNIT_SIZE / 2 + UNIT_SIZE / 2);
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (mapBomb[i][j] == 1 || mapBomb[i][j] == 2) {
                        //移动到相应位置
                        bomb.x = (j) * UNIT_SIZE;
                        bomb.y = 0.005f;//略微浮起
                        bomb.z = (i) * UNIT_SIZE;
                        bomb.drawSelf(gl, bombId);//绘制
                    }
                }
            }
            gl.glPopMatrix();
            gl.glDisable(GL10.GL_BLEND);//关闭混合
            //绘制覆盖方块
            gl.glPushMatrix();
            gl.glTranslatef(-map[0].length * UNIT_SIZE / 2 + UNIT_SIZE / 2, 0.01f, -map.length * UNIT_SIZE / 2 + UNIT_SIZE / 2);
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[0].length; j++) {
                    if (coverBlocks[i][j] == 1) {
                        //移动到相应位置
                        coverBlock.x = (j) * UNIT_SIZE;
                        coverBlock.y = 0.01f;//略微浮起
                        coverBlock.z = (i) * UNIT_SIZE;
                        coverBlock.drawSelf(gl, coverBlockId);//绘制
                    }
                }
            }
            gl.glPopMatrix();

            //画墙
            walls.drawSelf(gl, wallId);   //绘制
            //画小球
            ball.drawSelf(gl, ballId);   //绘制

            //清空buffer
            gl.glDisable(GL10.GL_LIGHTING);//关闭光照
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);//关闭法向量数组
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);   //关闭顶点数组
            gl.glDisable(GL10.GL_TEXTURE_2D);//关闭纹理
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);//关闭 UV array
        }

        private void initLight(GL10 gl) {
            //白色灯光
            gl.glEnable(GL10.GL_LIGHT0);//打开0号灯
            //环境光设置
            float[] ambientParams = {1f, 1f, 1f, 1.0f};//光参数 RGBA
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientParams, 0);
            //散射光设置
            float[] diffuseParams = {1f, 1f, 1f, 1.0f};//光参数 RGBA
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseParams, 0);
            //反射光设置
            float[] specularParams = {1f, 1f, 1f, 1.0f};//光参数 RGBA
            gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularParams, 0);
        }
    }
}
