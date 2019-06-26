package cz.uhk.graphstheory.first;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;
import cz.uhk.graphstheory.util.GraphGenerator;

public class GraphGeneratedView extends View {

    private static final int MAXIMUM_AMOUNT_OF_NODES = 10;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private ArrayList<Coordinate> circleCoordinates = new ArrayList<>();
    private ArrayList<Coordinate> allLineList = new ArrayList<>(); //seznam všech vytvořených line, ktere propojuji kruhy

    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    public static int BRUSH_SIZE = 15;

    private Paint mPaint;



    public GraphGeneratedView(Context context, AttributeSet attrs) {
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
    }

    public GraphGeneratedView(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
    }

    public void init(DisplayMetrics metrics) {
        //dostaneme výšku a šírku okna do ktereho budetem kreslit
        int displayHeight = metrics.heightPixels;
        int displayWidth = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);//vytvoření bitmapy pro kresleni
        mCanvas = new Canvas(mBitmap);  //předá se to cavasu

//        currentColor = DEFAULT_COLOR;
//        strokeWidth = BRUSH_SIZE;

    }

    public void setDimensionsForMapGeneratorAndGenerateRandomMap(int height, int width){
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges == 0 || amountOfEdges == 1) amountOfEdges += 2;
        setMap(GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges));
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
