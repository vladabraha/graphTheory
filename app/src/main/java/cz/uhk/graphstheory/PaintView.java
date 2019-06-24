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

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;

import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;


public class PaintView extends View {

    public static int BRUSH_SIZE = 15;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE_FINGER_MOVE = 5; //tolerance posunutí prstu při změně
    private static final float TOUCH_TOLERANCE_FINGER_TAPPED = 15; //tolerance posunutí prstu při změně
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

    private ArrayList<Coordinate> lineCoordinates = new ArrayList<>(); //prvni hodnota tam, kde začal tah, druhá tam kde končí tah (liny) při vytváření nové čáry uživatelem

    private ArrayList<Coordinate> circleCoordinates = new ArrayList<>();
    private ArrayList<Coordinate> allLineList = new ArrayList<>(); //seznam všech vytvořených line, ktere propojuji kruhy
    private boolean circle = true;
    private boolean line = false;
    private boolean remove = false;
    private boolean isCircleDragged = false;

    private Coordinate firstCoordinate; //označuje souřadnici, kam uživatel klepnul poprvé během posledního klepnutí

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
        allLineList.clear();
        circle();
        invalidate();
    }

    public void circle() {
        circle = true;
        line = false;
        remove = false;
    }

    public void line() {
        circle = false;
        line = true;
        remove = false;
    }

    public void remove() {
        circle = false;
        line = false;
        remove = true;
    }

    /**
     * Metoda je provedena každým zavoláním invalidate
     *
     * @param canvas override parametr, je do něho zakreslováno
     */
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
                mCanvas.drawCircle(coordinate.x, coordinate.y, BRUSH_SIZE + 30, mPaint);
            }
        }

        for (int i = 0; i < allLineList.size(); i++) {
            if (i % 2 != 0) {
                mPaint.setColor(DEFAULT_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);

                if (!allLineList.isEmpty())
                    mCanvas.drawLine(allLineList.get(i).x, allLineList.get(i).y, allLineList.get(i - 1).x, allLineList.get(i - 1).y, mPaint);
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

        if (dx >= TOUCH_TOLERANCE_FINGER_MOVE || dy >= TOUCH_TOLERANCE_FINGER_MOVE) {

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

    private void touchStartCircle(float x, float y) {
        Log.d("hoo", x + " " + y);
        for (Coordinate coordinate : circleCoordinates) {
            if (checkIsInCircle(coordinate.x, coordinate.y, x, y)) {
                Log.d("hoo", (x + " " + y + "coordinates of circle " + coordinate.x + " " + coordinate.y ));
                isCircleDragged = true;
                firstCoordinate = coordinate;
                break;
            }
        }
        //pokud neni klepnuto na žádný kruh, vytvoří se nový
        if (!isCircleDragged){
           circleCoordinates.add(new Coordinate(x, y));
        }
    }


    private void circleDragged(float x, float y) {
        circleCoordinates.remove(firstCoordinate);
        for (Coordinate coordinate : allLineList){
            //při tažení se přehodí i souřadnice přímky
            if (coordinate.x == firstCoordinate.x && coordinate.y == firstCoordinate.y){
                coordinate.x = x;
                coordinate.y = y;
            }
        }
        firstCoordinate = new Coordinate(x, y);
        circleCoordinates.add(firstCoordinate);
    }

    private void removeObject(float x, float y){
        for (Coordinate coordinate : circleCoordinates){
            if (checkIsInCircle(coordinate.x, coordinate.y, x, y)){
                circleCoordinates.remove(coordinate);
                //projde všechny vrcholy a pokud maji stejnou souřadnici, jako střed kruhu, tak je smaže včetně párového (je to přímka, takže druhá souřadnice)
                for (int i = 0; i < allLineList.size(); i++ ){
                    if (allLineList.get(i).x == coordinate.x && allLineList.get(i).y == coordinate.y){
                        if (i % 2 == 0){
                            allLineList.remove(i);
                            allLineList.remove(i);
                            i = i - 1; //abychom nepřeskočili žádnou hranu
                        }else {
                            allLineList.remove(i-1);
                            allLineList.remove(i-1);
                            i = i - 2; //abychom nepřeskočili žádnou hranu
                        }
                    }
                }
                break;
            }
        }

        //převedeni allLine do CommonMathsLines a zjištění, zdali souřadnice uživatelova klepnutí neleží v blízkosti někteřé přímky
        for (int i = 0; i < allLineList.size(); i++){
            if (i % 2 != 0){
                Line line = new Line(new Vector2D(allLineList.get(i-1).x, allLineList.get(i-1).y), new Vector2D(allLineList.get(i).x, allLineList.get(i).y),1); //přímka
                Segment segment = new Segment(new Vector2D(allLineList.get(i-1).x, allLineList.get(i-1).y), new Vector2D(allLineList.get(i).x, allLineList.get(i).y), line); //úsečka
                Log.d("hoo", ("x je " + x + " y je " + y + " vzdalenost je " + segment.distance(new Vector2D(x,y))));
                if (segment.distance(new Vector2D(x,y)) < TOUCH_TOLERANCE_FINGER_TAPPED){
                    allLineList.remove(i);
                    allLineList.remove(i-1);
                    break;
                }
            }
        }
    }


    private void touchUp(float x, float y) {
//        mPath.lineTo(previousXCoordinate, previousYCoordinate);
        lineCoordinates.clear();

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
                    allLineList.add(firstLineCoordinate);
                    allLineList.add(secondLineCoordinate);
                }
            }
        }
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
                if (circle) touchStartCircle(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (line) touchMove(x, y);
                if (isCircleDragged) circleDragged(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (line) {
                    touchUp(x, y);
                    touchMove(x, y);
                }
                if (isCircleDragged) isCircleDragged = false; //aby to neposouvalo v dalším tahu kruhy
                if (remove) removeObject(x, y);
                invalidate();
                break;
        }
        return true;
    }

    /**
     * get map for MapViewModel
     */
    public Map getMap() {

        ArrayList<CustomLine> lines = new ArrayList<>();
        for (int x = 0; x < allLineList.size(); x++) {
            if (x % 2 != 0) {
                CustomLine line = new CustomLine(allLineList.get(x - 1), allLineList.get(x));
                lines.add(line);
            }
        }
        return new Map(lines, circleCoordinates);
    }

    public void setMap(Map map) {
        ArrayList<CustomLine> lines = map.getCustomLines();

        circleCoordinates = map.getCircles();
        if (!circleCoordinates.isEmpty() || !allLineList.isEmpty()) {
            invalidate();
        }

        for (int i = 0; i < lines.size(); i++) {
            allLineList.add(new Coordinate(lines.get(i).getFrom().x, lines.get(i).getFrom().y));
            allLineList.add(new Coordinate(lines.get(i).getTo().x, lines.get(i).getTo().y));
            invalidate();
        }
    }
}
