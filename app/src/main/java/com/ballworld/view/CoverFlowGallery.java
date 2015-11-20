package com.ballworld.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.ballworld.activity.R;
import com.ballworld.util.ImageUtil;

/**
 * Created by duocai at 20:24 on 2015/11/20.
 * 自己定义的Gallery
 * 参考：http://www.cnblogs.com/tankaixiong/
 * http://www.tuicool.com/articles/am2eYnB
 */
public class CoverFlowGallery extends Gallery {
    /**
     * Gallery的中心点
     */
    public int galleryCenterPoint = 0;
    /**
     * 摄像机对象
     */
    private Camera camera;
    // 图片数组
    private int[] resIds = new int[]{R.drawable.gallery_snow, R.drawable.gallery_desert,
            R.drawable.gallery_grass,
            R.drawable.gallery_water, R.drawable.gallery_photo_1,
            R.drawable.gallery_photo_2, R.drawable.gallery_photo_3,
            R.drawable.gallery_photo_4, R.drawable.gallery_photo_5,
            R.drawable.gallery_photo_6, R.drawable.gallery_photo_7,
            R.drawable.gallery_photo_8};

    public CoverFlowGallery(Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
    }

    public CoverFlowGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 启动getChildStaticTransformation
        setStaticTransformationsEnabled(true);
        camera = new Camera();
    }

    public CoverFlowGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setStaticTransformationsEnabled(true);
    }

    /**
     * 当Gallery的宽和高改变时回调此方法，第一次计算gallery的宽和高时，也会调用此方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        galleryCenterPoint = getGalleryCenterPoint();

    }

    /**
     * 返回gallery的item的子图形的变换效果
     *
     * @param t 指定当前item的变换效果
     */
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        int viewCenterPoint = getViewCenterPoint(child); // view的中心点
        int rotateAngle = 0; // 旋转角度，默认为0

        if (Build.VERSION.SDK_INT >= 16) {
            //当版本大于16时，需刷新
            child.invalidate();
        }

        // 如果view的中心点不等于gallery中心，两边图片需要计算旋转的角度
        if (viewCenterPoint != galleryCenterPoint) {
            // gallery中心点 - view中心点 = 差值
            int diff = galleryCenterPoint - viewCenterPoint;
            // 差值 / 图片的宽度 = 比值
            float scale = (float) diff / (float) child.getWidth();
            // 比值 * 最大旋转角度 = 最终view的旋转角度(最大旋转角度定为40度)
            rotateAngle = (int) (scale * 40);

            if (Math.abs(rotateAngle) > 40) {// 当最终旋转角度>最大旋转角度，要改成40或-40
                rotateAngle = rotateAngle > 0 ? 40 : -40;
            }
        }

        // 设置变换效果前，需要把Transformation中的上一个item的变换效果清除
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX); // 设置变换效果的类型为矩阵类型
        startTransformationItem((ImageView) child, rotateAngle, t);
        return true;
    }

    /**
     * 设置变换的效果
     *
     * @param iv          gallery的item
     * @param rotateAngle 旋转的角度
     * @param t           变换的对象
     */
    private void startTransformationItem(ImageView iv, int rotateAngle, Transformation t) {
        camera.save(); // 保存状态
        int absRotateAngle = Math.abs(rotateAngle);

        // 1.放大效果（中间的图片要比两边的图片大）
        camera.translate(0, 0, 90f); // 给摄像机定位,用远近来调控大小
        int zoom = -250 + (absRotateAngle * 4);
        camera.translate(0, 0, zoom);

        // 2.透明度（中间的图片完全显示，两边有一定的透明度）
        int alpha = (int) (255 - (absRotateAngle * 3));
        iv.setAlpha(alpha);

        // 3.旋转（中间的图片没有旋转角度，只要不在中间的图片都有旋转角度）
        camera.rotateY(rotateAngle);

        Matrix matrix = t.getMatrix(); // 变换的矩阵，将变换效果添加到矩阵中
        camera.getMatrix(matrix); // 把matrix矩阵给camera对象，camera对象会把上面添加的效果转换成矩阵添加到matrix对象中
        matrix.preTranslate(-iv.getWidth() / 2, -iv.getHeight() / 2); // 矩阵前乘
        matrix.postTranslate(iv.getWidth() / 2, iv.getHeight() / 2); // 矩阵后乘

        camera.restore(); // 恢复之前保存的状态
    }

    /**
     * 获取Gallery的中心点
     *
     * @return
     */
    private int getGalleryCenterPoint() {
        return this.getWidth() / 2;
    }

    /**
     * 获取item上view的中心点
     *
     * @param v
     * @return
     */
    public int getViewCenterPoint(View v) {
        return v.getWidth() / 2 + v.getLeft(); // 图片宽度的一半+图片距离屏幕左边距
    }

    /**
     * 自定义图片的填充方式
     */
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return resIds.length;
        }

        @Override
        public Object getItem(int position) {
            return resIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView != null) {
                imageView = (ImageView) convertView;
            } else {
                imageView = new ImageView(mContext);
            }
            Bitmap bitmap = ImageUtil.getImageBitmap(getResources(), resIds[position]);//生成图片
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            drawable.setAntiAlias(true); // 消除锯齿
            imageView.setImageDrawable(drawable);
            LayoutParams params = new LayoutParams(480, 320);
            imageView.setLayoutParams(params);
            return imageView;
        }
    }
}
