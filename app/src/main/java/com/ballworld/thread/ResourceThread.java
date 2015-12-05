package com.ballworld.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ballworld.entity.Player;

/**
 *
 * Created by mac on 2015/11/26.
 */
public class ResourceThread extends Thread {
    Player player;
    Handler handler;
    boolean flag;

    public ResourceThread(Player player,Handler handler){
        this.player=player;
        this.handler=handler;
        this.flag=false;
    }

    public void run(){
        while(true){
            player.setFood(player.getFood() + 2);
            player.setWood(player.getWood() + 2);
            player.setMine(player.getMine() + 1);
            Bundle bundle=new Bundle();
            Message msg = new Message();//创建消息类
            bundle.putInt("food",player.getFood());
            bundle.putInt("wood",player.getWood());
            bundle.putInt("mine",player.getMine());
            msg.setData(bundle);
            handler.sendMessage(msg);//通过handler对象发送消息
            try {
                Thread.sleep(2000);
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
