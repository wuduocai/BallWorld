package com.ballworld.mapEntity;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by duocai at 22:34 on 2015/11/12.
 */
public class Numbers {
    Number[] numbers = new Number[10];

    public Numbers() {
        //生成0-9十个数字的纹理矩形
        for (int i = 0; i < 10; i++) {
            numbers[i] = new Number
                    (
                            i,
                            1,
                            1
                    );
        }
    }

    public void drawSelf(GL10 gl, int number, int texId) {
        numbers[number].drawSelf(gl,texId);
    }
}
