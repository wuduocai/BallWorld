package com.ballworld.util;

/*
 * Created by duocai at 22:55 on 2015/11/11.
 * 本类根据传感器传来的手机倾斜角数据
 * 计算出手机屏幕上物体在X轴和Z轴上的加速度
 */
public class RotateUtil {

	/**
	 * 处理x轴倾斜角
	 * @param angle
	 * @param gVector
	 * @return
	 */
	public static double[] pitchRotate(double angle,double[] gVector) {
		double[][] matrix=
		{
		   {1,0,0,0},
		   {0,Math.cos(angle),Math.sin(angle),0},		   
		   {0,-Math.sin(angle),Math.cos(angle),0},		  
		   {0,0,0,1}	
		};
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;
	}

	/**
	 * 处理y轴滚动角
	 * @param angle
	 * @param gVector
	 * @return
	 */
	public static double[] rollRotate(double angle,double[] gVector) {
		double[][] matrix=
		{
		   {Math.cos(angle),0,-Math.sin(angle),0},
		   {0,1,0,0},
		   {Math.sin(angle),0,Math.cos(angle),0},
		   {0,0,0,1}	
		};
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;
	}

	/**
	 * 处理Z轴旋转角
	 * @param angle
	 * @param gVector
	 * @return
	 */
	public static double[] yawRotate(double angle,double[] gVector) {
		double[][] matrix=
		{
		   {Math.cos(angle),Math.sin(angle),0,0},		   
		   {-Math.sin(angle),Math.cos(angle),0,0},
		   {0,0,1,0},
		   {0,0,0,1}	
		};
		double[] tempDot={gVector[0],gVector[1],gVector[2],gVector[3]};
		for(int j=0;j<4;j++)
		{
			gVector[j]=(tempDot[0]*matrix[0][j]+tempDot[1]*matrix[1][j]+
			             tempDot[2]*matrix[2][j]+tempDot[3]*matrix[3][j]);    
		}
		return gVector;
	}

	/**
	 * 根据倾斜角得到加速度
	 * @param values
	 * @return
	 */
	public static int[] getDirectionDot(double[] values) {
		//转化为弧度
		double yawAngle=-Math.toRadians(values[0]);
		double pitchAngle=-Math.toRadians(values[1]);
		double rollAngle=-Math.toRadians(values[2]);
		
		double[] gVector={0,0,-10,1};//初始加速度

		//通过手机倾斜角计算各个方向加速度
		gVector=RotateUtil.yawRotate(yawAngle,gVector);
		gVector=RotateUtil.pitchRotate(pitchAngle,gVector);	
		gVector=RotateUtil.rollRotate(rollAngle,gVector);

		//获得x轴和z轴方向的加速度
		double mapX=gVector[0];
		double mapY=gVector[1];
		int[] result={(int)mapX,(int)mapY};
		return result;
	}	
}