package com.zhaolongqing.zlqpc.circlesport;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 */
public class Circle extends View {

    private final Context context;
    private int color;
    private float radius;

    public Circle(Context context,int color,float radius) {
        super(context);
        this.context = context;
        this.color = color;
        this.radius = radius;
    }

    public Circle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getAttr(attrs);
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Circle);
        color = typedArray.getColor(R.styleable.Circle_color, 0xffffffff);
        radius = typedArray.getDimension(R.styleable.Circle_radius,20 );
        typedArray.recycle();
    }

    public void initPaint(Canvas canvas){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        paint.setStrokeWidth(1);//设置画笔粗细
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initPaint(canvas);
        super.onDraw(canvas);
    }

}
