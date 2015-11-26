package com.ballworld.mapEntity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import static com.ballworld.util.Constant.*;
import static com.ballworld.view.GameView.*;

/**
 * Created by duocai at 16:39 on 2015/11/12.
 */
public class Walls {
    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;//顶点纹理数据缓冲
    private FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    int vCount;//顶点数量
    private int[][] indexFlag;//用于记录当前点是否扫描过   1 表示此点不需要在扫描   0   表示此点需要扫描

    public Walls() {
        //顶点坐标数据的初始化================begin============================
        int rows = map.length;
        int cols = map[0].length;
        indexFlag = new int[rows][cols];

        ArrayList<Float> alVertex = new ArrayList<Float>();
        ArrayList<Float> alNormal = new ArrayList<Float>();
        ArrayList<Float> alTexture = new ArrayList<Float>();

        for (int i = 0; i < rows; i++)//行扫描
        {
            for (int j = 0; j < cols; j++)//列扫描
            {//对地图中的每一块进行处理
                if (map[i][j] == 1)//当前点为墙
                {
                    int[][] area = returnMaxBlock(i, j);// area[0]表示起始点    行 列  area[1]表示宽度和高度
                    for (int k = area[0][0]; k < area[0][0] + area[1][1]; k++)//对区域内的每个 点    建造围墙
                    {
                        for (int t = area[0][1]; t < area[0][1] + area[1][0]; t++) {
                            //建造顶层墙
                            float xx1 = t * UNIT_SIZE;       //    1
                            float y = FLOOR_Y + WALL_HEIGHT;
                            float zz1 = k * UNIT_SIZE;

                            float xx2 = t * UNIT_SIZE;       //     2
                            float zz2 = (k + 1) * UNIT_SIZE;

                            float xx3 = (t + 1) * UNIT_SIZE;    //   3
                            float zz3 = (k + 1) * UNIT_SIZE;

                            float xx4 = (t + 1) * UNIT_SIZE;       //    4
                            float zz4 = k * UNIT_SIZE;
                            //构造三角形
                            alVertex.add(xx1);
                            alVertex.add(y);
                            alVertex.add(zz1);
                            alVertex.add(xx2);
                            alVertex.add(y);
                            alVertex.add(zz2);
                            alVertex.add(xx3);
                            alVertex.add(y);
                            alVertex.add(zz3);

                            alVertex.add(xx3);
                            alVertex.add(y);
                            alVertex.add(zz3);
                            alVertex.add(xx4);
                            alVertex.add(y);
                            alVertex.add(zz4);
                            alVertex.add(xx1);
                            alVertex.add(y);
                            alVertex.add(zz1);

                            //添加纹理   整块平铺
                            alTexture.add((float) ((float) t / cols));
                            alTexture.add((float) k / rows);
                            alTexture.add((float) ((float) t / cols));
                            alTexture.add((float) ((float) (k + 1) / rows));
                            alTexture.add((float) ((float) (t + 1) / cols));
                            alTexture.add((float) ((float) (k + 1) / rows));

                            alTexture.add((float) ((float) (t + 1) / cols));
                            alTexture.add((float) ((float) (k + 1) / rows));
                            alTexture.add((float) ((float) (t + 1) / cols));
                            alTexture.add((float) k / rows);
                            alTexture.add((float) ((float) t / cols));
                            alTexture.add((float) k / rows);


                            //建造向量
                            alNormal.add(0f);
                            alNormal.add(1f);
                            alNormal.add(0f);
                            alNormal.add(0f);
                            alNormal.add(1f);
                            alNormal.add(0f);
                            alNormal.add(0f);
                            alNormal.add(1f);
                            alNormal.add(0f);

                            alNormal.add(0f);
                            alNormal.add(1f);
                            alNormal.add(0f);
                            alNormal.add(0f);
                            alNormal.add(1f);
                            alNormal.add(0f);
                            alNormal.add(0f);
                            alNormal.add(1f);
                            alNormal.add(0f);

                            //建造墙的上面
                            if (k == 0 || map[k - 1][t] == 0) {
                                float x1 = t * UNIT_SIZE;
                                float y1 = FLOOR_Y;
                                float z1 = k * UNIT_SIZE;   //  1

                                float x2 = t * UNIT_SIZE;
                                float y2 = FLOOR_Y + WALL_HEIGHT;
                                float z2 = k * UNIT_SIZE;    // 2

                                float x3 = (t + 1) * UNIT_SIZE;
                                float y3 = FLOOR_Y + WALL_HEIGHT;
                                float z3 = k * UNIT_SIZE;    //  3

                                float x4 = (t + 1) * UNIT_SIZE;
                                float y4 = FLOOR_Y;
                                float z4 = k * UNIT_SIZE;    //  4
                                //建造三角形
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                alVertex.add(x2);
                                alVertex.add(y2);
                                alVertex.add(z2);
                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);

                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);
                                alVertex.add(x4);
                                alVertex.add(y4);
                                alVertex.add(z4);
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                //建造纹理
                                alTexture.add((float) ((float) (t - area[0][1]) / cols));
                                alTexture.add(0f);
                                alTexture.add((float) ((float) (t - area[0][1]) / cols));
                                alTexture.add((float) ((float) 1 / rows));
                                alTexture.add((float) ((float) (t + 1 - area[0][1]) / cols));
                                alTexture.add((float) ((float) 1 / rows));

                                alTexture.add((float) ((float) (t + 1 - area[0][1]) / cols));
                                alTexture.add((float) ((float) 1 / rows));
                                alTexture.add((float) ((float) (t + 1 - area[0][1]) / cols));
                                alTexture.add(0f);
                                alTexture.add((float) ((float) (t - area[0][1]) / cols));
                                alTexture.add((float) 1 / rows);
                                //建造向量
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);

                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                            }
                            //建造墙的下面
                            if (k == rows - 1 || map[k + 1][t] == 0) {
                                float x2 = t * UNIT_SIZE;
                                float y2 = FLOOR_Y;
                                float z2 = (k + 1) * UNIT_SIZE;

                                float x1 = t * UNIT_SIZE;
                                float y1 = FLOOR_Y + WALL_HEIGHT;
                                float z1 = (k + 1) * UNIT_SIZE;

                                float x4 = (t + 1) * UNIT_SIZE;
                                float y4 = FLOOR_Y + WALL_HEIGHT;
                                float z4 = (k + 1) * UNIT_SIZE;

                                float x3 = (t + 1) * UNIT_SIZE;
                                float y3 = FLOOR_Y;
                                float z3 = (k + 1) * UNIT_SIZE;
                                //建造三角形
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                alVertex.add(x2);
                                alVertex.add(y2);
                                alVertex.add(z2);
                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);

                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);
                                alVertex.add(x4);
                                alVertex.add(y4);
                                alVertex.add(z4);
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                //建造纹理
                                alTexture.add((float) ((float) (t - area[0][1]) / cols));
                                alTexture.add(0f);
                                alTexture.add((float) ((float) (t - area[0][1]) / cols));
                                alTexture.add((float) ((float) 1 / rows));
                                alTexture.add((float) ((float) (t + 1 - area[0][1]) / cols));
                                alTexture.add((float) ((float) 1 / rows));

                                alTexture.add((float) ((float) (t + 1 - area[0][1]) / cols));
                                alTexture.add((float) ((float) 1 / rows));
                                alTexture.add((float) ((float) (t + 1 - area[0][1]) / cols));
                                alTexture.add(0f);
                                alTexture.add((float) ((float) (t - area[0][1]) / cols));
                                alTexture.add((float) 1 / rows);
                                //建造向量
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);

                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                            }
                            //建造墙的左面
                            if (t == 0 || map[k][t - 1] == 0) {
                                float x2 = t * UNIT_SIZE;
                                float y2 = FLOOR_Y;
                                float z2 = (k + 1) * UNIT_SIZE;

                                float x3 = t * UNIT_SIZE;
                                float y3 = FLOOR_Y + WALL_HEIGHT;
                                float z3 = (k + 1) * UNIT_SIZE;

                                float x4 = t * UNIT_SIZE;
                                float y4 = FLOOR_Y + WALL_HEIGHT;
                                float z4 = k * UNIT_SIZE;

                                float x1 = t * UNIT_SIZE;
                                float y1 = FLOOR_Y;
                                float z1 = k * UNIT_SIZE;
                                //建造三角形
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                alVertex.add(x2);
                                alVertex.add(y2);
                                alVertex.add(z2);
                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);

                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);
                                alVertex.add(x4);
                                alVertex.add(y4);
                                alVertex.add(z4);
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                //建造纹理
                                alTexture.add(0f);
                                alTexture.add((float) (k - area[0][0]) / rows);
                                alTexture.add(0f);
                                alTexture.add((float) ((float) (k + 1 - area[0][0]) / rows));
                                alTexture.add((float) ((float) 1 / cols));
                                alTexture.add((float) ((float) (k + 1 - area[0][0]) / rows));

                                alTexture.add((float) ((float) 1 / cols));
                                alTexture.add((float) ((float) (k + 1 - area[0][0]) / rows));
                                alTexture.add((float) ((float) 1 / cols));
                                alTexture.add((float) (k - area[0][0]) / rows);
                                alTexture.add(0f);
                                alTexture.add((float) (k - area[0][0]) / rows);
                                //建造向量
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);

                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(-1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                            }
                            //建造墙的右面
                            if (t == cols - 1 || map[k][t + 1] == 0) {
                                float x3 = (t + 1) * UNIT_SIZE;
                                float y3 = FLOOR_Y;
                                float z3 = (k + 1) * UNIT_SIZE;

                                float x2 = (t + 1) * UNIT_SIZE;
                                float y2 = FLOOR_Y + WALL_HEIGHT;
                                float z2 = (k + 1) * UNIT_SIZE;

                                float x1 = (t + 1) * UNIT_SIZE;
                                float y1 = FLOOR_Y + WALL_HEIGHT;
                                float z1 = k * UNIT_SIZE;

                                float x4 = (t + 1) * UNIT_SIZE;
                                float y4 = FLOOR_Y;
                                float z4 = k * UNIT_SIZE;
                                //建造三角形
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                alVertex.add(x2);
                                alVertex.add(y2);
                                alVertex.add(z2);
                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);

                                alVertex.add(x3);
                                alVertex.add(y3);
                                alVertex.add(z3);
                                alVertex.add(x4);
                                alVertex.add(y4);
                                alVertex.add(z4);
                                alVertex.add(x1);
                                alVertex.add(y1);
                                alVertex.add(z1);
                                //建造纹理
                                alTexture.add(0f);
                                alTexture.add((float) (k - area[0][0]) / rows);
                                alTexture.add(0f);
                                alTexture.add((float) ((float) (k + 1 - area[0][0]) / rows));
                                alTexture.add((float) ((float) 1 / cols));
                                alTexture.add((float) ((float) (k + 1 - area[0][0]) / rows));

                                alTexture.add((float) ((float) 1 / cols));
                                alTexture.add((float) ((float) (k + 1 - area[0][0]) / rows));
                                alTexture.add((float) ((float) 1 / cols));
                                alTexture.add((float) (k - area[0][0]) / rows);
                                alTexture.add(0f);
                                alTexture.add((float) (k - area[0][0]) / rows);
                                //建造向量
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);

                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                                alNormal.add(1f);
                                alNormal.add(0f);
                                alNormal.add(0f);
                            }
                        }
                    }
                }
            }
        }
        vCount = alVertex.size() / 3;
        float vertices[] = new float[alVertex.size()];
        for (int i = 0; i < alVertex.size(); i++) {
            vertices[i] = alVertex.get(i);
        }

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================

        //顶点法向量数据初始化================begin============================
        float normals[] = new float[vCount * 3];
        for (int i = 0; i < vCount * 3; i++) {
            normals[i] = alNormal.get(i);
        }

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为int型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点着色数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //顶点法向量数据初始化================end============================ 

        //顶点纹理数据的初始化================begin============================
        float textures[] = new float[alTexture.size()];
        for (int i = 0; i < alTexture.size(); i++) {
            textures[i] = alTexture.get(i);
        }
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================
    }

    public void drawSelf(GL10 gl, int texId) {
        gl.glPushMatrix();//保护矩阵
        gl.glTranslatef(-map[0].length / 2 * UNIT_SIZE, 0, (-map.length / 2) * UNIT_SIZE );
        //为画笔指定顶点坐标数据
        gl.glVertexPointer
                (
                        3,                //每个顶点的坐标数量为3  xyz
                        GL10.GL_FLOAT,    //顶点坐标值的类型为 GL_FIXED
                        0,                //连续顶点坐标数据之间的间隔
                        mVertexBuffer    //顶点坐标数据
                );

        //为画笔指定顶点法向量数据
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
        //为画笔指定纹理ST坐标缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        //绑定当前纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);

        //绘制图形
        gl.glDrawArrays
                (
                        GL10.GL_TRIANGLES,        //以三角形方式填充
                        0,
                        vCount
                );
        gl.glPopMatrix();
    }

    //根据当前点  当前点必须为墙   判断出此点周围最大的面积块数
    public int[][] returnMaxBlock(int rowIndex, int colIndex) {
        int rowindex = rowIndex;
        int colindex = colIndex;
        int rowsize;//用于记录行的大小
        int colsize;//用于记录列的大小

        int[][] area = new int[2][2];//用于记录位置 area[0]表示起始点索引  area[1]表示宽度和高度
        area[0][0] = rowIndex;
        area[0][1] = colIndex;
        //横向 高度为1
        int tempRowSize = 1;//高度
        int tempColSize = 1;//宽度
        while (colindex + 1 < map[0].length && map[rowindex][colindex + 1] == 1 && indexFlag[rowindex][colindex + 1] == 0) {
            tempColSize++;//宽度加一
            colindex++;//列索引加一
        }
        rowsize = tempRowSize;
        colsize = tempColSize;
        area[1][0] = colsize;
        area[1][1] = rowsize;//返回当前得面积位置

        //横向宽度为2
        tempRowSize = 0;
        tempColSize = 0;
        rowindex = rowIndex;
        colindex = colIndex;//数据初始化
        while (colindex + 1 < map[0].length && rowindex + 1 < map.length && map[rowindex][colindex] == 1 &&
                indexFlag[rowindex][colindex] == 0 && map[rowindex + 1][colindex] == 1 && indexFlag[rowindex + 1][colindex] == 0) {
            colindex++;
            tempRowSize = 2;
            tempColSize++;//列数加一
        }
        if (tempColSize * tempRowSize > colsize) {
            rowsize = tempRowSize;
            colsize = tempColSize;
            area[1][0] = colsize;
            area[1][1] = rowsize;//返回当前得面积位置
        }
//    	纵向  宽度为1
        tempRowSize = 1;
        tempColSize = 1;
        rowindex = rowIndex;
        colindex = colIndex;//数据初始化
        while (rowindex + 1 < map.length && map[rowindex + 1][colindex] == 1 && indexFlag[rowindex + 1][colindex] == 0) {
            rowindex++;
            tempRowSize++;
        }
        if (tempRowSize * tempColSize > rowsize * colsize) {
            rowsize = tempRowSize;
            colsize = tempColSize;
            area[1][0] = 1;
            area[1][1] = rowsize;//返回当前得面积位置
        }
        //纵向宽度为2
        tempRowSize = 0;
        tempColSize = 0;
        rowindex = rowIndex;
        colindex = colIndex;//数据初始化
        while (colindex + 1 < map[0].length && rowindex + 1 < map.length && map[rowindex][colindex] == 1 &&
                indexFlag[rowindex][colindex] == 0 && map[rowindex][colindex + 1] == 1 && indexFlag[rowindex][colindex + 1] == 0) {
            rowindex++;
            tempColSize = 2;
            tempRowSize++;//高度加1
        }
        if (tempColSize * tempRowSize > colsize * rowsize) {
            rowsize = tempRowSize;
            colsize = tempColSize;
            area[1][0] = colsize;
            area[1][1] = rowsize;//返回当前得面积位置
        }
        //将indexFlag扫描过的格子置为1
        for (int i = area[0][0]; i < area[0][0] + area[1][1]; i++) {
            for (int j = area[0][1]; j < area[0][1] + area[1][0]; j++) {
                indexFlag[i][j] = 1;//将其值设置为    1     表示不用在扫描
            }
        }
        return area;
    }
}

