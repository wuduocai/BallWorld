package com.ballworld.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ballworld.entity.Buildings;
import com.ballworld.entity.Player;

/**
 * Created by mac on 2015/12/12.
 */
public class BuildThread extends Thread {
    private int type;
    private Player player;
    private boolean flag;
    private int actualtime;
    private Handler handler;
    private boolean start;

    //type表示是哪一种建筑的建造线程
    //0住房，1农场，2伐木场，3矿场，4铁匠铺，5医院
    public BuildThread(int type,Player player,Handler handler){
        this.type=type;
        this.player=player;
        flag=false;
        this.handler=handler;
        this.start=false;
    }

    public void run(){
        this.flag=true;
        Buildings building=player.getBuilding()[type];
        actualtime=building.buildingTime();
        actualtime=actualtime*60;
        while(player.getBuilding()[type].isUnderBuild()&&actualtime>0){
            actualtime--;
            Bundle bundle=new Bundle();
            Message msg = new Message();//创建消息类
            bundle.putInt("minute",actualtime/60);
            bundle.putInt("second",actualtime%60);
            msg.setData(bundle);
            handler.sendMessage(msg);//通过handler对象发送消息
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getActualtime() {
        return this.actualtime;
    }

    public boolean isStart() {
        return this.start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
