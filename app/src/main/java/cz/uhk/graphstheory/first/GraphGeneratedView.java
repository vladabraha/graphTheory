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
import cz.uhk.graphstheory.util.PathGenerator;

public class GraphGeneratedView extends View {

    private static final int MAXIMUM_AMOUNT_OF_NODES = 10;
    private static final int MINIMUM_AMOUNT_OF_NODES = 3;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private ArrayList<Coordinate> circleCoordinates = new ArrayList<>();
    private ArrayList<Coordinate> allLineList = new ArrayList<>(); //seznam všech vytvořených line, ktere propojuji kruhy
    private ArrayList<Coordinate> pathLineList = new ArrayList<>();

    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int LINE_COLOR = Color.RED;
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
        mPaint.setAlpha(0xff);
    }

    public void init(DisplayMetrics metrics) {
        //dostaneme výšku a šírku okna do ktereho budetem kreslit
        int displayHeight = metrics.heightPixels;
        int displayWidth = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);//vytvoření bitmapy pro kresleni
        mCanvas = new Canvas(mBitmap);  //předá se to cavasu
    }

    public void setDimensionsForMapGeneratorAndGenerateRandomMap(int height, int width){
        int amountOfEdges = (int) (Math.random() * MAXIMUM_AMOUNT_OF_NODES);
        if (amountOfEdges < MINIMUM_AMOUNT_OF_NODES) amountOfEdges = MINIMUM_AMOUNT_OF_NODES;
        setMap(GraphGenerator.generateMap(height, width, BRUSH_SIZE, amountOfEdges));
        pathLineList = PathGenerator.generateCesta(getMap());
    }

    public void changePathGenerator(String method){
        switch (method){
            //todo dodelat volani generatoru pro dalsi moznosti (cesta, sled, kruznice...)
            case "cesta":
                pathLineList = PathGenerator.generateCesta(getMap());
                break;
        }
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

        for (int i = 0; i < allLineList.size(); i++) {
            if (i % 2 != 0) {
                mPaint.setColor(DEFAULT_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);

                if (!allLineList.isEmpty())
                    mCanvas.drawLine(allLineList.get(i).x, allLineList.get(i).y, allLineList.get(i - 1).x, allLineList.get(i - 1).y, mPaint);
            }
        }

        for (int i = 0; i < pathLineList.size(); i++) {
            if (i % 2 != 0) {
                mPaint.setColor(LINE_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);

                if (!allLineList.isEmpty())
                    mCanvas.drawLine(pathLineList.get(i).x, pathLineList.get(i).y, pathLineList.get(i - 1).x, pathLineList.get(i - 1).y, mPaint);
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

        ArrayList<CustomLine> path = new ArrayList<>();
        for (int x = 0; x < pathLineList.size(); x++){
            if (x % 2 != 0) {
                CustomLine line = new CustomLine(pathLineList.get(x - 1), pathLineList.get(x));
                path.add(line);
            }
        }

        return new Map(lines, circleCoordinates, path);
    }

    public void setMap(Map map) {
        ArrayList<CustomLine> lines = map.getCustomLines();
        ArrayList<CustomLine> path = map.getPathLineList();

        circleCoordinates = map.getCircles();
        if (!circleCoordinates.isEmpty() || !allLineList.isEmpty()) {
            invalidate();
        }

        for (int i = 0; i < lines.size(); i++) {
            allLineList.add(new Coordinate(lines.get(i).getFrom().x, lines.get(i).getFrom().y));
            allLineList.add(new Coordinate(lines.get(i).getTo().x, lines.get(i).getTo().y));
            invalidate();
        }

        for (int i = 0; i < path.size(); i++) {
            pathLineList.add(new Coordinate(path.get(i).getFrom().x, path.get(i).getFrom().y));
            pathLineList.add(new Coordinate(path.get(i).getTo().x, path.get(i).getTo().y));
            invalidate();
        }
    }
}
