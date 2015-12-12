package com.ballworld.thread;

import android.content.Context;

import com.ballworld.activity.MainActivity;

/**
 * Created by duocai at 0:06 on 2015/12/13.
 */
public class KeyBackThread extends Thread {
    public static boolean keyBackFlag = true;
    private MainActivity activity;

    public KeyBackThread(Context context) {
        this.activity = (MainActivity)context;
    }

    @Override
    public void run() {
        while (keyBackFlag) {
            activity.keyBack=false;
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
