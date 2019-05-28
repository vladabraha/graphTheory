package com.example.graphstheory;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView =  findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

//        setContentView(new MyView(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.circle:
                paintView.circle();
                return true;
            case R.id.line:
                paintView.line();
                return true;

            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public class MyView extends View
//    {
//        Paint paint = null;
//        public MyView(Context context)
//        {
//            super(context);
//            paint = new Paint();
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas)
//        {
//            super.onDraw(canvas);
//            int x = getWidth();
//            int y = getHeight();
//            int radius;
//            radius = 100;
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.WHITE);
//            canvas.drawPaint(paint);
//            // Use Color.parseColor to define HTML colors
//            paint.setColor(Color.parseColor("#CD5C5C"));
//            canvas.drawCircle(x / 2, y / 2, radius, paint);
//        }
//    }


}
