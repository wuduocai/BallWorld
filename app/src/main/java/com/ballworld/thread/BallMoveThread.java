package com.ballworld.thread;

import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.ballworld.entity.Player;
import com.ballworld.util.MyTTSListener;
import com.ballworld.view.GameView;
import com.turing.androidsdk.constant.Constant;
import com.turing.androidsdk.tts.TTSManager;

import static com.ballworld.util.Constant.MAX_DAMAGE;
import static com.ballworld.util.Constant.TREASURE;
import static com.ballworld.util.Constant.UNIT_SIZE;
import static com.ballworld.util.Constant.VZ_TENUATION;
import static com.ballworld.util.Constant.V_TENUATION;
import static com.ballworld.util.Constant.ballR;

/**
 * Created by duocai at 22:55 on 2015/11/10.
 */
public class BallMoveThread extends Thread {
    public static float t = 0.1f;//每次走的时间，认为规定的每次改变位置设为移动了该时间
    public Boolean flag = true;//线程标志位
    public float ballGX;
    public float ballGZ;//每次拷贝加速度
    GameView gameView;//引用gameView
    Player player;//玩家

    TTSManager ttsManager;//语音指导

    public BallMoveThread(GameView gameView, Player player) {
        this.gameView = gameView;
        this.player = player;
        ttsManager = new TTSManager(gameView.activity, new MyTTSListener());
    }

    @Override
    public void run() {
        while (this.flag) {
            //清楚脚下覆盖方块，并判断是不是炸弹
            clearCoverBlock(gameView.ball.ballX, gameView.ball.ballZ);

            //确定加速度//拷贝加速度,作为此时加速度处理
            gameView.ball.ballGX = this.ballGX = GameView.ballGX;
            gameView.ball.ballGZ = this.ballGZ = GameView.ballGZ;

            //判断是否撞墙
            knockWall(gameView.ball.ballX, gameView.ball.ballZ, gameView.ball.ballVX * t + this.ballGX * t * t * 0.5, gameView.ball.ballVZ * t + this.ballGZ * t * t * 0.5);

            //确定速度
            gameView.ball.ballVX += this.ballGX * t;
            gameView.ball.ballVZ += this.ballGZ * t;//最终速度

            //确定位置和旋转角
            if (gameView.ball.ballY > 0)
                gameView.ball.ballY -= 0.1f;
            if (gameView.ball.ballY < 0)
                gameView.ball.ballY = 0f;
            gameView.ball.ballX = gameView.ball.ballX + gameView.ball.ballVX * t + this.ballGX * t * t / 2;//V*T+1/2A*T*T
            gameView.ball.ballZ = gameView.ball.ballZ + gameView.ball.ballVZ * t + this.ballGZ * t * t / 2;//最终位置

            //旋转的角度
            gameView.ball.mAngleX += (float) Math.toDegrees(((gameView.ball.ballVZ * t + this.ballGZ * t * t / 2)) / ballR);
            gameView.ball.mAngleZ -= (float) Math.toDegrees((gameView.ball.ballVX * t + this.ballGX * t * t / 2) / ballR);
            //如果当前前进值小于调整值，则相应的转动方向角归零
            if (Math.abs((gameView.ball.ballVZ * t + this.ballGZ * t * t / 2)) < 0.005f) {
                gameView.ball.mAngleX = 0;
            }
            if (Math.abs(gameView.ball.ballVX * t + this.ballGX * t * t / 2) < 0.005f) {
                gameView.ball.mAngleZ = 0;
            }

            //速度衰减
            gameView.ball.ballVX *= V_TENUATION;
            gameView.ball.ballVZ *= V_TENUATION;//衰减

            //当速度小于某个调整值时,归0
            if (Math.abs(gameView.ball.ballVX) < 0.04) {
                gameView.ball.ballVX = 0;//速度归零
                gameView.ball.mAngleZ = 0;//将绕轴选择的值置为零
            }
            if (Math.abs(gameView.ball.ballVZ) < 0.04) {
                gameView.ball.ballVZ = 0;
                gameView.ball.mAngleX = 0;
            }

            //停顿后进入下一循环
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是将要撞到墙
     *
     * @param ballX
     * @param ballZ
     * @param xForward
     * @param zForward
     * @return
     */
    public Boolean knockWall(float ballX, float ballZ, double xForward, double zForward) {
        Boolean flag = false;
        //将地图移到XZ都大于零的象限,以匹配数组
        ballX = gameView.map[0].length * UNIT_SIZE / 2 + ballX;
        ballZ = gameView.map.length * UNIT_SIZE / 2 + ballZ;

        if (zForward > 0) {//如果向Z轴正方向运动
            //循环，假如它一下穿过几个格子，那么从第一个格子开始判断
            for (int i = (int) ((ballZ + ballR) / UNIT_SIZE); i < gameView.map.length && i <= (int) ((ballZ + ballR + zForward) / UNIT_SIZE); i++) {
                //判断是否碰墙壁了,Z向正碰
                if (gameView.map[i][(int) (ballX / UNIT_SIZE)] == 1 && gameView.map[i - 1][(int) (ballX / UNIT_SIZE)] == 0) {
                    gameView.ball.ballVZ = -gameView.ball.ballVZ * VZ_TENUATION;//将速度置反，并调整

                    //如果速度调反后还是会穿墙，那么将加速度归零，并将球画在和墙壁紧挨着的地方
                    if ((gameView.ball.ballZ + gameView.ball.ballVZ * t + this.ballGZ * t * t / 2) >= (i * UNIT_SIZE - ballR - gameView.map.length * UNIT_SIZE / 2)) {
                        gameView.ball.ballZ = (i * UNIT_SIZE - ballR - gameView.map.length * UNIT_SIZE / 2);
                        gameView.ball.ballVZ = 0;
                        this.ballGZ = 0;
                    } else
                        gameView.activity.playSound(1, 0);//撞壁音效
                    flag = true;//标志位置为true
                }
            }
        }

        //如果向X轴正方向走
        if (xForward > 0) {
            for (int i = (int) ((ballX + ballR) / UNIT_SIZE); i < gameView.map[0].length && i <= (int) ((ballX + ballR + xForward) / UNIT_SIZE); i++) {//循环，假如它一下穿过几个格子，那么从第一个格子开始判断
                if (gameView.map[(int) (ballZ / UNIT_SIZE)][i] == 1 && gameView.map[(int) (ballZ / UNIT_SIZE)][i - 1] == 0) {//如果碰壁了
                    gameView.ball.ballVX = -gameView.ball.ballVX * VZ_TENUATION;//速度置反，并调整

                    //如果速度调反后还是会穿墙，那么将加速度归零，并将球画在和墙壁紧挨着的地方
                    if ((gameView.ball.ballX + gameView.ball.ballVX * t + this.ballGX * t * t / 2) > ((i) * UNIT_SIZE - ballR - gameView.map[0].length * UNIT_SIZE / 2)) {
                        gameView.ball.ballX = (i) * UNIT_SIZE - ballR - gameView.map[0].length * UNIT_SIZE / 2;
                        this.ballGX = 0;//加速度和速度设置为零
                        gameView.ball.ballVX = 0;
                    } else
                        gameView.activity.playSound(1, 0);//撞壁音效
                    flag = true;
                }
            }
        }

        //x轴负方向
        if (xForward < 0) {
            //循环判断是否碰壁
            for (int i = (int) ((ballX - ballR) / UNIT_SIZE); i >= (int) ((ballX - ballR + xForward) / UNIT_SIZE); i--) {
                if (gameView.map[(int) (ballZ / UNIT_SIZE)][i] == 1 && gameView.map[(int) (ballZ / UNIT_SIZE)][i + 1] == 0) {//如果碰壁
                    gameView.ball.ballVX = -gameView.ball.ballVX * VZ_TENUATION;//速度置反并调整，
                    //任然会撞墙
                    if ((gameView.ball.ballX + gameView.ball.ballVX * t + this.ballGX * t * t / 2) < ((1 + i) * UNIT_SIZE + ballR - gameView.map[0].length * UNIT_SIZE / 2)) {
                        gameView.ball.ballX = (1 + i) * UNIT_SIZE + ballR - gameView.map[0].length * UNIT_SIZE / 2;
                        this.ballGX = 0;//加速度和速度设置为零
                        gameView.ball.ballVX = 0;
                    } else
                        gameView.activity.playSound(1, 0);//撞壁音效
                    flag = true;
                }
            }
        }

        //向Z轴负方向上运动时
        if (zForward < 0) {
            //循环看是否碰壁了
            for (int i = (int) ((ballZ - ballR) / UNIT_SIZE); i >= (int) ((ballZ - ballR + zForward) / UNIT_SIZE); i--) {
                if (gameView.map[i][(int) (ballX / UNIT_SIZE)] == 1 && gameView.map[i + 1][(int) (ballX / UNIT_SIZE)] == 0) {
                    gameView.ball.ballVZ = -gameView.ball.ballVZ * VZ_TENUATION;//将速度置反，并调整

                    //看调整后的速度下，是否会穿墙
                    if ((gameView.ball.ballZ + gameView.ball.ballVZ * t + this.ballGZ * t * t / 2) <= ((1 + i) * UNIT_SIZE + ballR - gameView.map.length * UNIT_SIZE / 2)) {
                        gameView.ball.ballZ = (1 + i) * UNIT_SIZE + ballR - gameView.map.length * UNIT_SIZE / 2;
                        gameView.ball.ballVZ = 0;
                        this.ballGZ = 0;
                    } else
                        gameView.activity.playSound(1, 0);//撞壁音效
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 清除覆盖方块
     *
     * @param ballX
     * @param ballZ
     */
    public void clearCoverBlock(float ballX, float ballZ) {
        //将地图移到XZ都大于零的象限,以匹配数组
        ballX = gameView.map[0].length * UNIT_SIZE / 2 + ballX;
        ballZ = gameView.map.length * UNIT_SIZE / 2 + ballZ;
        gameView.coverBlocks[(int) (ballZ / UNIT_SIZE)][(int) (ballX / UNIT_SIZE)] = 0;
        //碰到炸弹
        if (gameView.mapBomb[(int) (ballZ / UNIT_SIZE)][(int) (ballX / UNIT_SIZE)] == 1) {
            gameView.mapBomb[(int) (ballZ / UNIT_SIZE)][(int) (ballX / UNIT_SIZE)] = 2;
            gameView.ball.ballVX = gameView.ball.ballVZ = 0f;
            gameView.activity.playSound(2, 0);
            gameView.activity.shake(0);//100ms短震动
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //回到剧情界面
            if (player == Player.NIL) {
                this.flag = false;//停止线程
                ttsManager.startTTS("Sorry,you died",Constant.XunFei);//语音播报
                //文字提醒
                Message m = Message.obtain();
                m.obj = "你被炸死了";
                gameView.activity.gameHandler.sendMessage(m);
                gameView.activity.hd.sendEmptyMessage(0);
            } else {
                player.setHp(player.getHp() - (MAX_DAMAGE-player.getDefense()));
                if (player.getHp() <= 0) {
                    this.flag = false;//停止线程
                    player.setHp(1);//暂时模拟死亡效果
                    ttsManager.startTTS("Sorry,you died", Constant.XunFei);
                    //文字提醒
                    Message m = Message.obtain();
                    m.obj = "你被炸死了";
                    gameView.activity.gameHandler.sendMessage(m);
                    gameView.activity.hd.sendEmptyMessage(1);
                } else {
                    ttsManager.startTTS("要小心", Constant.XunFei);
                    //文字提醒
                    Message m = Message.obtain();
                    m.obj = "你损失了"+(MAX_DAMAGE-player.getDefense())+"滴血";
                    gameView.activity.gameHandler.sendMessage(m);
                }
            }
        }

        //到达目标
        if ((int) (ballZ / UNIT_SIZE) == gameView.ball.ballMbZ && (int) (ballX / UNIT_SIZE) == gameView.ball.ballMbX) {
            gameView.ball.ballVX = gameView.ball.ballVZ = 0f;
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.flag = false;//停止线程
            //声效

            //回到剧情界面
            if (player == Player.NIL) {
                ttsManager.startTTS("congratulations, you win!!!", Constant.XunFei);
                //文字提醒
                Message m = Message.obtain();
                m.obj = "你过关了";
                gameView.activity.gameHandler.sendMessage(m);
                gameView.activity.hd.sendEmptyMessage(0);
            } else {
                ttsManager.startTTS("congratulations!!", Constant.XunFei);
                //文字提醒
                Message m = Message.obtain();
                m.obj = "通过关卡，获得"+TREASURE[player.getLevelId()%4];
                gameView.activity.gameHandler.sendMessage(m);
                player.setLevelId(player.getLevelId() + 1);
                gameView.activity.hd.sendEmptyMessage(1);
            }
        }
    }

}
