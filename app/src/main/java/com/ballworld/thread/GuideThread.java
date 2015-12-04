package com.ballworld.thread;

import android.content.Context;

import com.ballworld.activity.MainActivity;

/**
 * Created by duocai at 23:48 on 2015/12/3.
 */
public class GuideThread extends Thread {
    public boolean guideFlag = true;
    MainActivity activity;
    int destView;
    int limit;

    public GuideThread(Context context, int destView, int limit) {
        this.activity = (MainActivity) context;
        this.destView = destView;
        this.limit = limit;
    }

    @Override
    public void run() {
        int i = 0;
        while (guideFlag) {
            if (i < limit) {//显示指导
                activity.curText=i;
            }

            try {
                sleep(activity.guide[i].length()*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;
            if (i >= limit) {//结束进程
                guideFlag = false;
                activity.hd.sendEmptyMessage(destView);
            }
        }
    }
}
