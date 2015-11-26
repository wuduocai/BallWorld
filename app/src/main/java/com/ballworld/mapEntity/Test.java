package com.ballworld.mapEntity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by duocai at 16:58 on 2015/11/8.
 */
public class Test {
    private IntBuffer mVertexBuffer;//顶点坐标数据缓冲
    private IntBuffer   mColorBuffer;//顶点着色数据缓冲
    int vertexCount;//顶点的个数
    public Test() {
        vertexCount=15;//顶点的个数
        final int UNIT_SIZE=10000;//创建像素单位
        int vertices[]=new int[]{
                0,0,0,-8*UNIT_SIZE,-8*UNIT_SIZE,0,
                -8*UNIT_SIZE,8*UNIT_SIZE,0,
                0,0,0,-8*UNIT_SIZE,8*UNIT_SIZE,0,
                0,14*UNIT_SIZE,0,
                0,0,0,0,14*UNIT_SIZE,0,
                8*UNIT_SIZE,8*UNIT_SIZE,0,
                0,0,0,8*UNIT_SIZE,8*UNIT_SIZE,0,
                8*UNIT_SIZE,-8*UNIT_SIZE,0,
                0,0,0,8*UNIT_SIZE,-8*UNIT_SIZE,0,
                -8*UNIT_SIZE,-8*UNIT_SIZE,0
        };
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asIntBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        final int one = 65535;
        int colors[]=new int[]{//顶点颜色值数组，每个顶点4个色彩值RGBA
                one,one,one,0,0,0,one,0,
                0,0,one,0,one,one,one,0,
                0,0,one,0,0,0,one,0,
                one,one,one,0,0,0,one,0,
                0,0,one,0,one,one,one,0,
                0,0,one,0,0,0,one,0,
                one,one,one,0,0,0,one,0,
                0,0,one,0,
        };
        //创建顶点着色数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asIntBuffer();//转换为int型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    public void drawSelf(GL10 gl) {
        gl.glTranslatef(0, 0f, -3f);
        gl.glRotatef(2, 0, 1, 0);//沿Y轴旋转
        gl.glRotatef(2, 0, 0, 1);//沿X轴旋转
        gl.glVertexPointer(//为画笔指定顶点坐标数据
                3,//每个顶点的坐标数量为3  xyz
                GL10.GL_FIXED,//顶点坐标值的类型为 GL_FIXED
                0, mVertexBuffer//顶点坐标数据
        );
        gl.glColorPointer(//为画笔指定顶点着色数据
                4, //设置颜色的组成成分，必须为4—RGBA
                GL10.GL_FIXED,//顶点颜色值的类型为 GL_FIXED
                0, mColorBuffer//顶点着色数据
        );
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertexCount); //绘制图形
    }
}
