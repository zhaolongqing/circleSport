package com.zhaolongqing.zlqpc.circlesport;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;


public class CircleSport extends ConstraintLayout {

    private static final String TAG = "CircleSport";
    private final Context context;
    private ArrayList<Circle> circles;
    private int circleCount;
    private int circleSpeed;
    private int circleColor;
    private float radius;
    private static final int START_VIEW = 123;
    private static final int END_VIEW = 124;
    private ArrayList<ObjectAnimator> animators = new ArrayList<>();
    private ViewHandler viewHandler;

    public CircleSport(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getTypeArray(attrs);
        initView();
    }

    private void getTypeArray(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleSport);
        circleCount = typedArray.getInteger(R.styleable.CircleSport_circle_count, 1);
        circleSpeed = typedArray.getInteger(R.styleable.CircleSport_sport_speed, 2) * 1000;
        circleColor = typedArray.getColor(R.styleable.CircleSport_circle_color, Color.WHITE);
        radius = typedArray.getDimension(R.styleable.CircleSport_circle_radius, 20);
        typedArray.recycle();
    }

    private void initView() {
        circles = new ArrayList<>();
        for (int i = 0; i < circleCount; i++) {
            Circle circle = new Circle(context, circleColor, radius);
            LayoutParams layoutParams = new LayoutParams((int) radius * 2, (int) radius * 2);
            layoutParams.endToEnd = LayoutParams.PARENT_ID;
            layoutParams.startToStart = LayoutParams.PARENT_ID;
            layoutParams.topToTop = LayoutParams.PARENT_ID;
            layoutParams.verticalBias = 0.5f;
            circle.setLayoutParams(layoutParams);
            circle.setAlpha(0);
            addView(circle);
            circles.add(circle);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHandler = new ViewHandler();
        if (circles != null) {
            for (int i = 0; i < circles.size(); i++) {
                setAnimation(circles.get(i), h, i, circles.size());
            }
            animators.get(0).start();
        }
    }

    private void setAnimation(Circle circle, int h, int i, int last) {

        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
        PropertyValuesHolder holder3 = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        PropertyValuesHolder holder4 = PropertyValuesHolder.ofFloat("translationY", 0, h);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(circle, holder1, holder2, holder3, holder4);
        animators.add(animator);
        animator.setDuration(circleSpeed);
        animator.setRepeatCount(0);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            invalidate();
            post(() -> {
                Message message = new Message();
                if (animation.getAnimatedFraction() == 1) {
                    message.obj = animation;
                    message.what = END_VIEW;
                    viewHandler.sendMessage(message);
                } else if (i < last - 1 && !animators.get(i + 1).isStarted() && animation.getAnimatedFraction() >= (1.0 / (circleCount + 2))) {
                    message.obj = animators.get(i + 1);
                    message.what = START_VIEW;
                    viewHandler.sendMessage(message);
                } else if (i == last - 1) {
                    message.obj = animators.get(0);
                    message.what = START_VIEW;
                    viewHandler.sendMessage(message);
                }
            });
        });
    }

    private static class ViewHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            ObjectAnimator objectAnimator = (ObjectAnimator) msg.obj;
            switch ((int) msg.what) {
                case START_VIEW:
                    objectAnimator.start();
                    break;
                case END_VIEW:
                    objectAnimator.pause();
                    break;
            }
        }
    }


}