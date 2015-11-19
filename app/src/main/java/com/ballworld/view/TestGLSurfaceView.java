package com.ballworld.view;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
public class TestGLSurfaceView extends GLSurfaceView{
	MyRenderer myRenderer;//�Զ������Ⱦ��
	private IntBuffer   mVertexBuffer;//�����������ݻ���
    private IntBuffer   mColorBuffer;//������ɫ���ݻ���
    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
	private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����
    float yAngle=0;//��y����ת�ĽǶ�
    float zAngle=0;//��z����ת�ĽǶ�
    int vertexCount;//����ĸ���
	public TestGLSurfaceView(Context context){//������
		super(context);
		myRenderer = new MyRenderer();//������Ⱦ��
		this.setRenderer(myRenderer);//������Ⱦ��
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������Ⱦģʽ
		this.initVertexBuffer();//��ʼ��������������
		this.initColorBuffer();//��ʼ����ɫ����
	}
    @Override 
    public boolean onTouchEvent(MotionEvent e){//�����¼��Ļص�����
    	float x = e.getX();//�õ�x����
        float y = e.getY();//�õ�y����
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE://���ر��ƶ�
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            zAngle += dy * TOUCH_SCALE_FACTOR;//������z����ת�Ƕ�
            requestRender();//�ػ滭��
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;//����true
    }
	private void initVertexBuffer(){//��ʼ��������������
		vertexCount=15;//����ĸ���
        final int UNIT_SIZE=10000;//�������ص�λ
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
        //���������������ݻ���
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asIntBuffer();//ת��Ϊint�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
	}
	private void initColorBuffer(){//��ʼ����ɫ����
        final int one = 65535;
        int colors[]=new int[]{//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
        		one,one,one,0,0,0,one,0,
        		0,0,one,0,one,one,one,0,
        		0,0,one,0,0,0,one,0,
        		one,one,one,0,0,0,one,0,
        		0,0,one,0,one,one,one,0,
        		0,0,one,0,0,0,one,0,
        		one,one,one,0,0,0,one,0,
        		0,0,one,0,
        };
        //����������ɫ���ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mColorBuffer = cbb.asIntBuffer();//ת��Ϊint�ͻ���
        mColorBuffer.put(colors);//�򻺳����з��붥����ɫ����
        mColorBuffer.position(0);//���û�������ʼλ��
	}
	//�Զ������Ⱦ��
	private class MyRenderer implements Renderer{
		@Override
		public void onDrawFrame(GL10 gl) {//���Ʒ���
        	//�����ɫ��������Ȼ���
        	gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);//���õ�ǰ����Ϊģʽ����
            gl.glLoadIdentity(); //���õ�ǰ����Ϊ��λ����
            gl.glTranslatef(0, 0f, -3f);
            gl.glRotatef(yAngle, 0, 1, 0);//��Y����ת
            gl.glRotatef(zAngle, 0, 0, 1);//��X����ת
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//���ö�����������
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);//���ö�����ɫ����
            gl.glVertexPointer(//Ϊ����ָ��������������
            		3,//ÿ���������������Ϊ3  xyz 
            		GL10.GL_FIXED,//��������ֵ������Ϊ GL_FIXED
            		0, mVertexBuffer//������������
            );
            gl.glColorPointer(//Ϊ����ָ��������ɫ����
            		4, //������ɫ����ɳɷ֣�����Ϊ4��RGBA
            		GL10.GL_FIXED,//������ɫֵ������Ϊ GL_FIXED
            		0, mColorBuffer//������ɫ����
            );
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertexCount); //����ͼ��
		}
		@Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	gl.glViewport(0, 0, width, height);//�����Ӵ���С��λ�� 
            gl.glMatrixMode(GL10.GL_PROJECTION);//���õ�ǰ����ΪͶӰ����
            gl.glLoadIdentity();//���õ�ǰ����Ϊ��λ����
            float ratio = (float)width/height;//����͸��ͶӰ�ı���
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);//���ô˷����������͸��ͶӰ����
        }
		@Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {//����ʱ������
        	gl.glDisable(GL10.GL_DITHER);//�رտ�����           
            gl.glEnable(GL10.GL_DEPTH_TEST);//������Ȳ���
        }
	}
}