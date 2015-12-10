package com.ballworld.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ballworld.entity.Player;

/**
 * Created by mac on 2015/12/9.
 */
//用于控制装备页面现有资源的线程
public class EquitThread extends Thread{
    Player player;
    Handler handler;
    boolean flag;

    public EquitThread(Player player,Handler handler){
        this.player=player;
        this.handler=handler;
        this.flag=false;
    }

    public void run(){
        while(flag){
            Bundle bundle=new Bundle();
            Message msg = new Message();//创建消息类
            bundle.putInt("mine",player.getMine());
            msg.setData(bundle);
            handler.sendMessage(msg);//通过handler对象发送消息
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return this.flag;
    }

}
