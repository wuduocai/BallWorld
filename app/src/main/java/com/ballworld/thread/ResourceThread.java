package com.ballworld.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.ballworld.entity.Player;

/**
 *
 * Created by mac on 2015/11/26.
 */
public class ResourceThread extends Thread {
    Player player;
    Handler handler;

    public ResourceThread(Player player,Handler handler){
        this.player=player;
        this.handler=handler;
    }

    public void run(){
        while(true){
            player.setFood(player.getFood() + 1);
            player.setWood(player.getWood() + 1);
            player.setMine(player.getMine() + 1);
            Bundle bundle=new Bundle();
            Message msg = new Message();//创建消息类
            bundle.putInt("food",player.getFood());
            bundle.putInt("wood",player.getWood());
            bundle.putInt("mine",player.getMine());
            msg.setData(bundle);
            handler.sendMessage(msg);//通过handler对象发送消息
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
