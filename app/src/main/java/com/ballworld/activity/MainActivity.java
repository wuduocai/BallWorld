package com.ballworld.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ballworld.entity.Player;
import com.ballworld.thread.GuideThread;
import com.ballworld.thread.ResourceThread;
import com.ballworld.util.MyTTSListener;
import com.ballworld.util.RotateUtil;
import com.ballworld.util.SQLiteUtil;
import com.ballworld.view.CoverFlowGallery;
import com.ballworld.view.GameView;
import com.ballworld.view.WelcomeView;
import com.turing.androidsdk.constant.Constant;
import com.turing.androidsdk.tts.TTSManager;

import java.util.HashMap;

import cn.sharerec.recorder.impl.GLRecorder;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.ballworld.util.Constant.SCREEN_HEIGHT;
import static com.ballworld.util.Constant.SCREEN_WIDTH;
import static com.ballworld.view.GameView.OnTouchListener;
import static com.ballworld.view.GameView.ballGX;
import static com.ballworld.view.GameView.ballGZ;

enum WhichView {
    WELCOME_VIEW, MAIN_MENU, SETTING_VIEW,
    HELP_VIEW, CASUAL_GAME_VIEW, STORY_GAME_VIEW, CASUAL_MODE_VIEW, TOWN_VIEW,
    GUIDE
}

/**
 * Created by duocai at 20:24 on 2015/10/31.
 */
public class MainActivity extends Activity {
    //声明变量
    public WhichView currentView;
    //关数
    public int levelId = 0;
    public GLRecorder recorder;//录屏工具
    //view
    WelcomeView welcomeView;
    GameView gameView;
    //thread
    ResourceThread resource;
    GuideThread guideThread;
    //声明player
    Player player;
    //指导页面变量
    public String[] guide;//指导框对话
    String[] me;
    TTSManager ttsManager;
    TextView guideView;
    TextView meView;
    ImageView skipButton;
    public int curText;
    //数据库
    SQLiteUtil slu;
    //功能引用
    Vibrator myVibrator;//声明振动器
    boolean shakeflag = true;//是否震动
    SoundPool soundPool;//声音池
    HashMap<Integer, Integer> soundPoolMap; //记录声音池返回的资源id
    boolean backgroundSoundFlag = true;//是否播放背景音乐
    boolean knockWallSoundFlag = true;//撞壁音效
    //判断控制资源增长的线程是否已经开启
    //    界面转换控制
    public Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://切换主菜单界面
                    goToMenuView();
                    break;
                case 1://切换城镇的界面
                    goToTownView();
                    break;
                case 2://切换到建造房屋的界面
                    goToBuildHouseView();
                    break;
                case 3://切换到建造武器的界面
                    goToMakeWeaponView();
                    break;
                case 4://切换到玩家信息界面
                    goToPlayerInformationView();
                    break;
                case 5://切换到游戏界面
                    goToGameView();
                    break;
                case 6://切换到关于游戏界面
                    goToAboutGameView();
                    break;
                case 7://切换到游戏帮助界面
                    goToGameHelpView();
                    break;
                case 8://切换到游戏设置界面
                    goToSettingView();
                    break;
                case 9://回到欢迎界面
                    goToWelcomeView();
                    break;
                case 10://休闲模式选择
                    goToCasualModeView();
                    break;
            }
        }
    };
    boolean recordGameFlag = true;//是否录屏
    SensorManager mySensorManager;    //SensorManager对象引用，后注册手机方向传感器
    //监听传感器
    private SensorListener mySensorListener = new SensorListener() {
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(int sensor, float[] values) {
            if (sensor == SensorManager.SENSOR_ORIENTATION) {//判断是否为加速度传感器变化产生的数据

                //通过倾角算出X轴和Z轴方向的加速度
                int directionDotXY[] = RotateUtil.getDirectionDot(
                        new double[]{values[0], values[1], values[2]}
                );
                //改变小球加速度
                ballGX = -directionDotXY[0] * 3.2f;//得到X和Z方向上的加速度
                ballGZ = directionDotXY[1] * 3.2f;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉tittle
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //强制为横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //初始化变量
        //获取屏幕长宽,变量在Constant类声明
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_WIDTH = dm.widthPixels;
        //其他变量
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获得SensorManager对象
        myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);//获得震动服务
        initSound();
        initDataBase();
        initPlayer();
        //进入欢迎界面
        goToWelcomeView();
    }

    /**
     * 初始化玩家
     */
    public void initPlayer() {
        player = slu.queryPlayer(1);
    }

    /**
     * 初始化数据库
     */
    public void initDataBase() {
        slu = new SQLiteUtil(this);
    }

    /**
     * 初始化音乐
     */
    private void initSound() {
        //声音池
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPoolMap = new HashMap<Integer, Integer>();
        //撞墙音
        soundPoolMap.put(1, soundPool.load(this, R.raw.dong, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.bomb, 1));
    }

    /**
     * 播放声音
     *
     * @param sound - 声音id
     * @param loop  - loop —— 循环播放的次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次（例如，3为一共播放4次）.
     */
    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);//音量
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        switch (sound) {
            case 1:
                if (knockWallSoundFlag)//是否关闭撞壁音效
                    soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
                break;
            case 2:
                soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
                break;
        }

    }

    /**
     * 包装震动
     *
     * @param mode - 震动模式，0：一次100ms短震动
     */
    public void shake(int mode) {
        if (shakeflag) {//判断用户是否关闭震动
            switch (mode) {
                case 0:
                    myVibrator.vibrate(100);
            }
        }
    }

    /**
     * 进入欢迎界面
     */
    private void goToWelcomeView() {
        if (welcomeView == null)
            welcomeView = new WelcomeView(this);
        this.setContentView(welcomeView);
    }

    /**
     * 进入主菜单界面
     * 并为主菜单界面的控件添加listener
     */
    private void goToMenuView() {
        this.setContentView(R.layout.menu);

//        if (currentView == WhichView.CASUAL_MODE_VIEW) {
////            recorder.stopRecorder();
////            recorder.setText("我的小球世界");
////            recorder.showShare();
//        }

        currentView = WhichView.MAIN_MENU;

        //实例化player
        if (player==null)
            player = new Player(0, 0, 0);
        if (resource == null)
            resource = new ResourceThread(player, null);

        //get button
        ImageButton storyMode = (ImageButton) this.findViewById(R.id.storyModeButton),
                casualMode = (ImageButton) this.findViewById(R.id.casualModeButton),
                gameSetting = (ImageButton) this.findViewById(R.id.gameSettingButton),
                gameHelp = (ImageButton) this.findViewById(R.id.gameHelpButton);

        //set listener
        storyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hd.sendEmptyMessage(1);//城镇界面
                //指导demo
                showGuide(1,
                        new String[]{"欢迎玩小球世界", "你一定会觉得它很棒的"},
                        new String[]{"en", "好期待啊"});
            }
        });
        casualMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(10);//游戏界面
            }
        });
        gameSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(8);//设置界面
            }
        });
        gameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(7);//帮助界面
            }
        });

    }

    /**
     * 进入城镇界面
     * 初始化变量
     * add listener
     */
    private void goToTownView() {
        setContentView(R.layout.main_town);
        currentView = WhichView.TOWN_VIEW;
        //头像点击，即进入个人信息界面
        final ImageView person = (ImageView) findViewById(R.id.head);
        //医院
        final ImageView hospital = (ImageView) findViewById(R.id.hospital);
        //农场
        final ImageView food = (ImageView) findViewById(R.id.food);
        //铁匠铺
        final ImageView fabricate = (ImageView) findViewById(R.id.fabricate);
        //伐木场
        final ImageView wood = (ImageView) findViewById(R.id.wood);
        //矿场
        final ImageView mine = (ImageView) findViewById(R.id.mine);
        //住房
        final ImageView house = (ImageView) findViewById(R.id.house);
        //go按钮，即出征
        final ImageView arraw1 = (ImageView) findViewById(R.id.arraw1);
        //build按钮，即建造房屋
        final ImageView arraw2 = (ImageView) findViewById(R.id.arraw2);
        //资源显示的textview
        final TextView foodstorage = (TextView) findViewById(R.id.foodstorage);
        final TextView woodstorage = (TextView) findViewById(R.id.woodstorage);
        final TextView minestorage = (TextView) findViewById(R.id.minestorage);

        //每次进入该界面时实时调整资源的数量
        foodstorage.setText("" + player.getFood());
        woodstorage.setText("" + player.getWood());
        minestorage.setText("" + player.getMine());

        //将未建造的建筑的透明度设为0
        changeAlpha(new ImageView[]{house, food, wood, mine, fabricate, hospital}, player);
        //用于更新textview的handler
        resource.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                foodstorage.setText("" + bundle.getInt("food"));
                woodstorage.setText("" + bundle.getInt("wood"));
                minestorage.setText("" + bundle.getInt("mine"));
            }
        });
        if (!resource.getFlag()) {
            resource.start();
            resource.setFlag(true);
        }
        //对头像添加监听器
        imageClick(person, R.drawable.head, R.drawable.head, 4);
        //对医院添加监听器
        if (player.getBuilding()[5].getLevel() != 0) {
            imageClick(hospital, R.drawable.hospitalpressed, R.drawable.hospital, 0);
        }
        //对住房添加监听器
        if (player.getBuilding()[0].getLevel() != 0) {
            imageClick(house, R.drawable.housepressed, R.drawable.house, 0);
        }
        //对伐木场添加监听器
        if (player.getBuilding()[2].getLevel() != 0) {
            imageClick(wood, R.drawable.woodpressed, R.drawable.wood, 0);
        }
        //对农场添加监听器
        if (player.getBuilding()[1].getLevel() != 0) {
            imageClick(food, R.drawable.foodpressed, R.drawable.food, 0);
        }
        //对矿场添加监听器
        if (player.getBuilding()[3].getLevel() != 0) {
            imageClick(mine, R.drawable.minepressed, R.drawable.mine, 0);
        }
        //对铁匠铺添加监听器，前往开发装备界面
        if (player.getBuilding()[4].getLevel() != 0) {
            imageClick(fabricate, R.drawable.fabricatepressed, R.drawable.fabricate, 3);
        }
        //对arraw1添加监听器，即go按钮
        imageClick(arraw1, R.drawable.arraw1pressed, R.drawable.arraw1, 5);
        //对arraw2添加监听器，即build按钮，前往建造房屋界面
        imageClick(arraw2, R.drawable.arraw2pressed, R.drawable.arraw2, 2);
        //add listener
    }

    /**
     * 进入建造房屋的界面
     */
    private void goToBuildHouseView() {
        setContentView(R.layout.build_house);
        //返回按钮
        final ImageView arraw3 = (ImageView) findViewById(R.id.back);
        //房屋的建造按钮
        final ImageButton bulidHouse = (ImageButton) findViewById(R.id.buildhouse);
        //农场的建造按钮
        final ImageButton bulidFood = (ImageButton) findViewById(R.id.buildfood);
        //伐木场的建造按钮
        final ImageButton bulidWood = (ImageButton) findViewById(R.id.buildwood);
        //矿场的建造按钮
        final ImageButton bulidMine = (ImageButton) findViewById(R.id.buildmine);
        //铁匠铺的建造按钮
        final ImageButton bulidFabricate = (ImageButton) findViewById(R.id.buildfabricate);
        //医院的建造按钮
        final ImageButton bulidHospital = (ImageButton) findViewById(R.id.buildhospital);
        //为ImageButton添加监听器
        buildButtonClick(bulidHouse, 0);
        buildButtonClick(bulidFood, 1);
        buildButtonClick(bulidWood, 2);
        buildButtonClick(bulidMine, 3);
        buildButtonClick(bulidFabricate, 4);
        buildButtonClick(bulidHospital, 5);
        //为返回按钮添加监听器
        imageClick(arraw3, R.drawable.arraw3pressed, R.drawable.arraw3, 1);
    }

    /**
     * 进入制造武器的界面
     */
    private void goToMakeWeaponView() {
        setContentView(R.layout.make_weapon);
        //返回按钮
        final ImageView arraw3 = (ImageView) findViewById(R.id.back);
        imageClick(arraw3, R.drawable.arraw3pressed, R.drawable.arraw3, 1);
    }

    /**
     * 进入玩家信息界面
     * 要实现穿戴装备功能
     */
    private void goToPlayerInformationView() {
        setContentView(R.layout.player_information);
        //返回按钮
        final ImageView arraw3 = (ImageView) findViewById(R.id.back);
        imageClick(arraw3, R.drawable.arraw3pressed, R.drawable.arraw3, 1);
    }

    /**
     * 进入游戏界面
     */
    private void goToGameView() {
        if (currentView == WhichView.CASUAL_MODE_VIEW) {
            gameView = new GameView(this, levelId, Player.NIL);//模拟第0（1）关
            currentView = WhichView.CASUAL_GAME_VIEW;//休闲模式
//            recorder = new SrecGLSurfaceView(this) {
//                @Override
//                protected String getShareRecAppkey() {
//                    return "cfbc621ddad0";
//                }
//            }.getRecorder();
//            if (recorder.isAvailable()){
//                Toast.makeText(MainActivity.this, "recorder isAvailable", Toast.LENGTH_SHORT).show();
//                // 启动录制
//               recorder.startRecorder();
//            }
        } else {
            gameView = new GameView(this, player.getLevelId(), player);//模拟第0（1）关
            currentView = WhichView.STORY_GAME_VIEW;//故事模式
        }

        gameView.requestFocus();//获得焦点
        gameView.setFocusableInTouchMode(true);//可触控
        this.setContentView(gameView);
    }

    /**
     * 进入休闲模式界面
     */
    private void goToCasualModeView() {
        setContentView(R.layout.casual_mode_gallery);
        currentView = WhichView.CASUAL_MODE_VIEW;
        final CoverFlowGallery cfg = (CoverFlowGallery) findViewById(R.id.gallery);//使用画廊
        CoverFlowGallery.ImageAdapter imageAdapter = cfg.new ImageAdapter(this);
        cfg.setAdapter(imageAdapter);//自定义图片的填充方式
        //添加监听器
        cfg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 4 && cfg.galleryCenterPoint == cfg.getViewCenterPoint(view)) {
                    levelId = position;
                    //hd.sendEmptyMessage(5);//游戏界面
                    //显示指导demo，双线程比较复杂，就这样用着
                    showGuide(5,
                            new String[]{"欢迎玩小球世界", "你一定会觉得它很棒的"},
                            new String[]{"en", "好期待啊"});
                }
            }
        });

    }

    /**
     * 进入游戏信息界面
     * 放在后面实现
     */
    private void goToAboutGameView() {
        setContentView(R.layout.about_game);
    }

    /**
     * 进入游戏帮助界面
     * 添加至少一个返回button listener
     * 放在后面实现
     */
    private void goToGameHelpView() {
        setContentView(R.layout.game_help);
        currentView = WhichView.HELP_VIEW;

        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("小球世界");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }

    /**
     * 进入游戏设置界面
     * 初始化变量
     * set listener
     * 放在后面实现
     */
    private void goToSettingView() {
        setContentView(R.layout.setting);
        currentView = WhichView.SETTING_VIEW;

        //返回菜单
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(0);
            }
        });

        //音效
        final CheckBox sound = (CheckBox) findViewById(R.id.sound);
        if (knockWallSoundFlag)
            sound.setChecked(true);
        else
            sound.setChecked(false);
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sound.isChecked())
                    knockWallSoundFlag = true;
                else
                    knockWallSoundFlag = false;
            }
        });

        //震动
        final CheckBox shake = (CheckBox) findViewById(R.id.shake);
        if (shakeflag)
            shake.setChecked(true);
        else
            shake.setChecked(false);
        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shake.isChecked())
                    shakeflag = true;
                else
                    shakeflag = false;
            }
        });

        //实现问题反馈
        final CheckBox chat = (CheckBox) findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat.isChecked()) {
                    Intent intent = new Intent(MainActivity.this, SmartChatActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 未建造的房屋变透明
     */
    public void changeAlpha(final ImageView[] image, Player player) {
        for (int i = 0; i < image.length; i++) {
            if (player.getBuilding()[i].getLevel() == 0) {
                image[i].setAlpha(0);
            }
        }
    }

    /**
     * 无自动线程的guideview，可用
     * @param destView
     * @param guide
     * @param me
     */
    public void showGuide(final int destView, final String[] guide,final String[] me) {
        setContentView(R.layout.guidance);
        currentView = WhichView.GUIDE;

        //初始化控件
        final TTSManager ttsManager = new TTSManager(this,new MyTTSListener());
        final TextView guideView = (TextView)findViewById(R.id.guide);
        final TextView meView = (TextView)findViewById(R.id.me);
        ImageView skipButton = (ImageView)findViewById(R.id.skip);
        curText=0;

        //显示对话
        ttsManager.startTTS(guide[curText], Constant.BaiDu);//语音模拟
        guideView.setText(guide[curText]);
        meView.setText(me[curText]);

        //更换动画
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curText++;
                if (curText < guide.length) {
                    ttsManager.startTTS(guide[curText], Constant.BaiDu);//语音模拟
                    guideView.setText(guide[curText]);
                    meView.setText(me[curText]);
                } else {//语音放完切换到目标页面
                    hd.sendEmptyMessage(destView);
                }
            }
        });
    }

    /*
    *为ImageButton添加监听器
    * 在建造页面按下建造按钮之后，扣除相应的费用，并且建造相应的房屋
    * */
    public void buildButtonClick(final ImageButton button, final int type) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否已经满级
                if (player.getBuilding()[type].getLevel() != 3) {
                    //若未满级，判断资源是否足够
                    int[] res = player.getBuilding()[type].cost();
                    if (player.getFood() >= res[0] && player.getWood() >= res[1] && player.getMine() >= res[2]) {
                        player.getBuilding()[type].addLevel();
                        player.setFood(player.getFood() - res[0]);
                        player.setWood(player.getWood() - res[1]);
                        player.setMine(player.getMine() - res[2]);
                        Toast.makeText(getApplicationContext(), "建造成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "资源不足", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "该建筑已经满级", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    * 使imageview被点击时模拟出button的效果
    * 传入的参数分别为该imageview，两张图片，即将进入的界面
    * */
    public void imageClick(final ImageView image, final int pic1, final int pic2, final int des) {
        image.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    image.setImageResource(pic1);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x - previousX;
                    float dy = y - previousY;
                    if ((dx > 2 || dy > 2) && previousX != 0 && previousY != 0)
                        click = false;
                    previousX = x;
                    previousY = y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    image.setImageResource(pic2);
                    if (click)
                        hd.sendEmptyMessage(des);
                    click = true;
                }
                return true;
            }
        });
    }

    /**
     * 初始化guideview控件
     * @param destView
     */
    public void initGuideView(final int destView) {
        ttsManager = new TTSManager(this, new MyTTSListener());
        guideView = (TextView) findViewById(R.id.guide);
        meView = (TextView) findViewById(R.id.me);
        skipButton = (ImageView) findViewById(R.id.skip);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideThread.guideFlag=false;//结束线程
                hd.sendEmptyMessage(destView);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);//调音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentView == WhichView.SETTING_VIEW ||
                    currentView == WhichView.HELP_VIEW ||
                    currentView == WhichView.CASUAL_MODE_VIEW) {
                hd.sendEmptyMessage(0);
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            mAudioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mAudioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
        }
        return true;//父类不再操作
    }

    @Override
    protected void onResume() {//重写onResume方法
        super.onResume();
        mySensorManager.registerListener
                (            //注册监听器
                        mySensorListener,                    //监听器对象
                        SensorManager.SENSOR_ORIENTATION,    //传感器类型,倾角
                        SensorManager.SENSOR_DELAY_UI        //传感器事件传递的频度
                );
    }

    @Override
    protected void onPause() //重写onPause方法
    {
        super.onPause();
        if (player!=null)
            slu.updatePlayer(player,1);
        mySensorManager.unregisterListener(mySensorListener);    //取消注册监听器
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
