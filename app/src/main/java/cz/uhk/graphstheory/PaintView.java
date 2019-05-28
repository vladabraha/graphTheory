package cz.uhk.graphstheory;

import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {

    public static int BRUSH_SIZE = 15;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float previousXCoordinate, previousYCoordinate;
    //    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> fingerPaths = new ArrayList<>();
    //    private int currentColor;
//    private int strokeWidth;
//    private boolean emboss;
//    private boolean blur;
    //    private MaskFilter mEmboss;
//    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private DisplayMetrics displayMetrics;

    private ArrayList<Coordinate> lineCoordinates = new ArrayList<>(); //prvni hodnota tam, kde začal tah, druhá tam kde končí tah (liny)
    private ArrayList<Coordinate> lineCoordinatesFinal = new ArrayList<>();
    private ArrayList<Coordinate> circleCoordinates = new ArrayList<>();
    private boolean circle = true;
    private boolean line = false;

    private Coordinate firstCoordinate;

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

//        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
//        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        //dostaneme výšku a šírku okna do ktereho budetem kreslit
        displayMetrics = metrics;
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//vytvoření bitmapy pro kresleni
        mCanvas = new Canvas(mBitmap);  //předá se to cavasu

//        currentColor = DEFAULT_COLOR;
//        strokeWidth = BRUSH_SIZE;
    }

    public void clear() {
        fingerPaths.clear();
        lineCoordinates.clear();
        circleCoordinates.clear();
        lineCoordinatesFinal.clear();
        circle();
        invalidate();
    }

    public void circle() {
        circle = true;
        line = false;
    }

    public void line() {
        circle = false;
        line = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas = canvas;
       mCanvas.drawColor(DEFAULT_BG_COLOR); //vybarví celou plochu bílou barvou

        //kresli z arraylistu jednotlive čáry
//        for (FingerPath fingerPath : fingerPaths) {
//            mPaint.setColor(fingerPath.color);
//            mPaint.setStrokeWidth(fingerPath.strokeWidth);
//
//            mCanvas.drawPath(fingerPath.getPath(), mPaint);
//
//        }


        for (int i = 0; i < lineCoordinates.size(); i++) {
            if (i != 0) {
                mPaint.setColor(DEFAULT_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);

                mCanvas.drawLine(lineCoordinates.get(i).x, lineCoordinates.get(i).y, lineCoordinates.get(i - 1).x, lineCoordinates.get(i - 1).y, mPaint);
            }
        }

        if (circleCoordinates.size() > 0) {
            for (Coordinate coordinate : circleCoordinates) {
                mPaint.setColor(DEFAULT_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);
                mPaint.setStyle(Paint.Style.FILL);

                Log.d("hoo", String.valueOf(displayMetrics.density));
                mCanvas.drawCircle(coordinate.x, coordinate.y, BRUSH_SIZE + 30, mPaint);
            }
        }

        for (int i = 0; i < lineCoordinatesFinal.size(); i++) {
            if (i % 2 != 0) {
                mPaint.setColor(DEFAULT_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);

                mCanvas.drawLine(lineCoordinatesFinal.get(i).x, lineCoordinatesFinal.get(i).y, lineCoordinatesFinal.get(i - 1).x, lineCoordinatesFinal.get(i - 1).y, mPaint);
            }
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();

    }

    private void touchStart(float x, float y) {
//        mPath = new Path(); //sem se kresli jedna cesta do zdvihnuti
//        FingerPath fp = new FingerPath(currentColor, strokeWidth, mPath);
//        fingerPaths.add(fp);
//
//        //nastaveni prvni souradnice do pathu
//        mPath.reset();
//        mPath.moveTo(x, y); //posune prvni texku

        firstCoordinate = new Coordinate(x, y);
        //nasetuje prvni hodnotu do lineCoordinates (pro vykresleni čáry, která se právě kreslí
        lineCoordinates.add(new Coordinate(x, y));


        //nastaveni počátečních hodnot pro toleranci
        previousXCoordinate = x;
        previousYCoordinate = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - previousXCoordinate);
        float dy = Math.abs(y - previousYCoordinate);

        //zkontroluje, zdali se nejedna o chybu, napr. drzenim prstu prilis dlouho
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {


            //pokud je coordinate první, přidá, jinak nasetuje druhej v arraylistu
            if (lineCoordinates.size() == 1) {
                lineCoordinates.add(new Coordinate(x, y));
            } else {
                lineCoordinates.set(1, new Coordinate(x, y));
            }

            previousXCoordinate = x;
            previousYCoordinate = y;
        }
    }

    private void touchUp(float x, float y) {
//        mPath.lineTo(previousXCoordinate, previousYCoordinate);
        lineCoordinates.clear();

        if (line) {
            Coordinate firstLineCoordinate = null;
            Coordinate secondLineCoordinate;
            for (Coordinate circleCoordinate : circleCoordinates) {
                //otestuje, zdali konec, nebo začátek čáry leží v daném kruhu
                if (checkIsInCircle(circleCoordinate.x, circleCoordinate.y, x, y) || checkIsInCircle(circleCoordinate.x, circleCoordinate.y, firstCoordinate.x, firstCoordinate.y)) {
                    //pokud je coordinate v kruhu, nastaví se první souřadnice přímky
                    if (firstLineCoordinate == null) {
                        firstLineCoordinate = new Coordinate(circleCoordinate.x, circleCoordinate.y);
                        //pokud je coordinate v kruhu a už máme první souřadnici přímky nastaví se druhá souřadnice přímky
                    } else {
                        secondLineCoordinate = new Coordinate(circleCoordinate.x, circleCoordinate.y);
                        lineCoordinatesFinal.add(firstLineCoordinate);
                        lineCoordinatesFinal.add(secondLineCoordinate);
                    }
                }
            }
        }

        if (circle) circleCoordinates.add(new Coordinate(x, y));


    }

    private boolean checkIsInCircle(float circle_x, float circle_y, float point_x, float point_y) {
        double D = Math.pow(point_x - circle_x, 2) + Math.pow(point_y - circle_y, 2);
        return D <= Math.pow(BRUSH_SIZE + 30, 2);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lineCoordinates.clear();

                if (line) touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (line) touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                if (line) touchMove(x, y);
                invalidate();
                break;
        }

        return true;
    }
}
