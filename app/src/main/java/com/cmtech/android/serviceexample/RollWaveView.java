/*
 * * SignalScanView: ��ɨ��ķ�ʽ������ʾ��������ʾ�ź�
 * Ŀǰû�м����κ��˲�����
 * Wrote by chenm, BME, GDMC
 * 2013.09.25
 */
package com.cmtech.android.serviceexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.vise.log.ViseLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RollWaveView: 卷轴滚动式的波形显示视图
 * Created by bme on 2018/12/06.
 */

public class RollWaveView extends View {
    private static final int DEFAULT_SIZE = 100; // 缺省View的大小
    private static final int DEFAULT_XRES = 2; // 缺省的X方向的分辨率
    private static final float DEFAULT_YRES = 1.0f; // 缺省的Y方向的分辨率
    public static final float DEFAULT_ZERO_LOCATION = 0.5f; // 缺省的零线位置在Y方向的高度的比例
    private static final int DEFAULT_GRID_WIDTH = 10; // 缺省的每个栅格的像素宽度
    private static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK; // 缺省的背景颜色
    private static final int DEFAULT_GRID_COLOR = Color.RED; // 缺省的栅格线颜色
    protected static final int DEFAULT_WAVE_COLOR = Color.YELLOW; // 缺省的波形颜色

    private int viewWidth = DEFAULT_SIZE; //视图宽度
    private int viewHeight = DEFAULT_SIZE;  //视图高度
    protected int initX, initY;	 //画图起始位置
    protected int preX, preY; //画线的前一个点坐标
    protected final Paint wavePaint = new Paint(); // 波形画笔
    protected Bitmap backBitmap; //背景bitmap
    private Bitmap foreBitmap; //前景bitmap
    protected Canvas foreCanvas; //前景canvas
    //private final LinkedBlockingQueue<Integer> viewData = new LinkedBlockingQueue<Integer>();	//要显示的信号数据对象的引用
    protected List<Integer> viewData = new ArrayList<>(); //要显示的信号数据对象的引用
    // View初始化主要需要设置下面4个参数
    private int gridWidth = DEFAULT_GRID_WIDTH; // 一个栅格的像素宽度
    protected int xRes = DEFAULT_XRES;	 //X方向分辨率，表示屏幕X方向每个数据点占多少个像素，pixel/data
    protected float yRes = DEFAULT_YRES; //Y方向分辨率，表示屏幕Y方向每个像素代表的信号值的变化，DeltaSignal/pixel
    private double zeroLocation = DEFAULT_ZERO_LOCATION; //表示零值位置占视图高度的百分比
    protected int dataNumXDirection; // X方向上一屏包含的数据点数
    private final int backgroundColor; // 背景颜色
    private final int gridColor; // 栅格线颜色
    private final int waveColor; // 波形颜色
    private final boolean showGridLine; // 是否显示栅格线

    public RollWaveView(Context context) {
        super(context);

        backgroundColor = DEFAULT_BACKGROUND_COLOR;
        gridColor = DEFAULT_GRID_COLOR;
        waveColor = DEFAULT_WAVE_COLOR;
        showGridLine = true;

        ViseLog.e("RollWaveView(Context context)");
    }

    public RollWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        backgroundColor = DEFAULT_BACKGROUND_COLOR;
        gridColor = DEFAULT_GRID_COLOR;
        waveColor = DEFAULT_WAVE_COLOR;
        showGridLine = true;

        //setDataNumXDirection(viewWidth, xRes);
        //initWavePaint();

        ViseLog.e("RollWaveView(Context context, AttributeSet attrs)");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViseLog.e("onMeasure");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        ViseLog.e("onLayout " + changed + " " + left + " " + top + " " + right + " " + bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ViseLog.e("onSizeChanged w:" + w + " h:" + h + " oldw:" + oldw + " oldh:" + oldh);

        viewWidth = getWidth();
        viewHeight = getHeight();
        setDataNumXDirection(viewWidth, xRes);
        initWavePaint();

        reset();
        drawDataOnForeCanvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //setDataNumXDirection(viewWidth, xRes);
        //initWavePaint();
        //canvas.drawColor(backgroundColor);
        if(foreBitmap != null)
            canvas.drawBitmap(foreBitmap, 0, 0, null);

        ViseLog.e("onDraw " + "canvas: " + canvas);
    }

    private void initWavePaint() {
        wavePaint.setAlpha(255);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeWidth(2);
        wavePaint.setColor(waveColor);
    }

    public void setPixelPerGrid(int gridWidth) {
        this.gridWidth = gridWidth;
    }
    public int getXRes()
    {
        return xRes;
    }
    public float getYRes()
    {
        return yRes;
    }
    public void setResolution(int xRes, float yRes)
    {
        if((xRes < 1) || (yRes < 0)) return;
        this.xRes = xRes;
        this.yRes = yRes;
        setDataNumXDirection(viewWidth, xRes);
    }
    public void setZeroLocation(double zeroLocation)
    {
        this.zeroLocation = zeroLocation;
        initY = (int)(viewHeight * this.zeroLocation);
    }
    public int getDataNumXDirection() {
        return dataNumXDirection;
    }
    public void setDataNumXDirection(int viewWidth, int xRes) {
        dataNumXDirection = viewWidth/xRes+1;
    }

    private int calculateMeasure(int measureSpec)
    {
        int size = (int)(DEFAULT_SIZE * getResources().getDisplayMetrics().density);
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY)
        {
            size = specSize;
        }
        else if(specMode == MeasureSpec.AT_MOST)
        {
            size = Math.min(size, specSize);
        }
        return size;
    }

    public void reset()
    {
        // 清除缓存区
        //viewData.design();

        //创建背景Bitmap
        createBackBitmap();

        //将背景bitmap复制到前景bitmap
        foreBitmap = backBitmap.copy(Config.ARGB_8888,true);
        foreCanvas = new Canvas(foreBitmap);

        // 初始化画图起始位置
        preX = initX;
        preY = initY;

        postInvalidate();
    }

    public void clearData() {
        viewData.clear();
    }

    public void addData(Integer data) {
        viewData.add(data);
    }

    public synchronized void showData(List<Integer> data) {
        viewData.addAll(data);
        drawDataOnForeCanvas();
        invalidate();
    }

    protected boolean drawDataOnForeCanvas()
    {
        foreCanvas.drawBitmap(backBitmap, 0, 0, null);

        Integer[] data = viewData.toArray(new Integer[0]);
        int dataNum = data.length;
        if(dataNum <= 1) {
            return true;
        }

        int begin = dataNum - dataNumXDirection;
        if(begin < 0) {
            begin = 0;
        }

        clearData();
        addData(data[begin]);
        preX = initX;
        preY = initY - Math.round(data[begin]/yRes);
        Path path = new Path();
        path.moveTo(preX, preY);
        for(int i = begin+1; i < dataNum; i++) {
            addData(data[i]);
            preX += xRes;
            preY = initY - Math.round(data[i]/yRes);
            path.lineTo(preX, preY);
        }

        foreCanvas.drawPath(path, wavePaint);
        return true;
    }

    // 创建背景Bitmap
    private void createBackBitmap()
    {
        initX = 0;
        initY = (int)(viewHeight * zeroLocation);

        //创建背景Bitmap
        backBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);

        if(!showGridLine) return;

        Canvas backCanvas = new Canvas(backBitmap);
        backCanvas.drawColor(backgroundColor);

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(4);
        backCanvas.drawText(""+new Date().getTime(), 100, 100, paint);

        // 画零位线
        paint.setColor(gridColor);
        paint.setStrokeWidth(4);
        backCanvas.drawLine(initX, initY, initX + viewWidth, initY, paint);

        paint.setStrokeWidth(1);

        // 画水平线
        int vCoordinate = initY - gridWidth;
        int i = 1;
        while(vCoordinate > 0) {
            backCanvas.drawLine(initX, vCoordinate, initX + viewWidth, vCoordinate, paint);
            vCoordinate -= gridWidth;
            if(++i == 5) {
                paint.setStrokeWidth(2);
                i = 0;
            }
            else
                paint.setStrokeWidth(1);
        }
        paint.setStrokeWidth(1);
        vCoordinate = initY + gridWidth;
        i = 1;
        while(vCoordinate < viewHeight) {
            backCanvas.drawLine(initX, vCoordinate, initX + viewWidth, vCoordinate, paint);
            vCoordinate += gridWidth;
            if(++i == 5) {
                paint.setStrokeWidth(2);
                i = 0;
            }
            else
                paint.setStrokeWidth(1);
        }

        // 画垂直线
        paint.setStrokeWidth(1);
        int hCoordinate = initX + gridWidth;
        i = 1;
        while(hCoordinate < viewWidth) {
            backCanvas.drawLine(hCoordinate, 0, hCoordinate, viewHeight, paint);
            hCoordinate += gridWidth;
            if(++i == 5) {
                paint.setStrokeWidth(2);
                i = 0;
            }
            else
                paint.setStrokeWidth(1);
        }
    }
}
