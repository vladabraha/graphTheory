package cz.uhk.graphtheory.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.FingerPath;
import cz.uhk.graphtheory.model.Map;

/**
 * obecna trida pro kresleni - bude pouzita ve vsech kreslicich prvcich
 */
public class PaintView extends View {

    public static int BRUSH_SIZE = 15;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int RED_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE_FINGER_MOVE = 5; //tolerance posunutí prstu při změně
    private static final float TOUCH_TOLERANCE_FINGER_TAPPED = 15; //tolerance posunutí prstu při změně
    private float previousXCoordinate, previousYCoordinate;
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
    private ArrayList<Coordinate> redLineList = new ArrayList<>(); //seznam vytvořené cesty
    private ArrayList<Coordinate> redNodesCoordinates = new ArrayList<>(); //seznam cervenych vrcholu, napr. pro artikulaci
    private boolean circle, line, move, remove, path = false;
    private boolean isCircleDragged = false;
    PaintView.CommunicationInterface mListener;

    HashMap<Coordinate, String> charsForNodes;

    int charValue = "A".charAt(0); //aktuální písmeno, které se zobrazuje v uzlu

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
        mPaint.setAlpha(0xff);

//        mPaint.setXfermode(null);
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
    }

    public void clear() {
        fingerPaths.clear();
        lineCoordinates.clear();
        circleCoordinates.clear();
        redLineList.clear();
        allLineList.clear();
        circle();
        invalidate();
    }

    public void circle() {
        circle = true;
        line = false;
        remove = false;
        path = false;
        move = false;
    }

    public void circleMove() {
        circle = false;
        move = true;
        line = false;
        remove = false;
        path = false;
    }

    public void line() {
        circle = false;
        line = true;
        remove = false;
        path = false;
        move = false;
    }

    public void remove() {
        circle = false;
        line = false;
        remove = true;
        path = false;
        move = false;
    }

    public void path() {
        circle = false;
        line = false;
        remove = false;
        path = true;
        move = false;
    }

    public void disableAllActions() {
        circle = false;
        line = false;
        remove = false;
        path = false;
        move = false;
    }

    /**
     * Metoda je provedena každým zavoláním invalidate
     *
     * @param canvas override parametr, je do něho zakreslováno
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save(); //uloží to, co bylo do teď vykresleno, dále mohou probíhat modifikace, např. otočení, zvětšení obrazovky atd.
        mCanvas = canvas;
        mCanvas.drawColor(DEFAULT_BG_COLOR); //vybarví celou plochu bílou barvou

        //kresli z arraylistu jednotlive čáry
//        for (FingerPath fingerPath : fingerPaths) {
//            mPaint.setColor(fingerPath.color);
//            mPaint.setStrokeWidth(fingerPath.strokeWidth);
//            mCanvas.drawPath(fingerPath.getPath(), mPaint);
//        }

        for (int i = 0; i < lineCoordinates.size(); i++) {
            if (i != 0) {
                if (line) {
                    mPaint.setColor(DEFAULT_COLOR);
                } else if (path) {
                    mPaint.setColor(RED_COLOR);
                }
                mPaint.setStrokeWidth(BRUSH_SIZE);

                mCanvas.drawLine(lineCoordinates.get(i).x, lineCoordinates.get(i).y, lineCoordinates.get(i - 1).x, lineCoordinates.get(i - 1).y, mPaint);
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

        for (int i = 0; i < redLineList.size(); i++) {
            if (i % 2 != 0) {
                mPaint.setColor(RED_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);

                if (!redLineList.isEmpty())
                    mCanvas.drawLine(redLineList.get(i).x, redLineList.get(i).y, redLineList.get(i - 1).x, redLineList.get(i - 1).y, mPaint);
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

        if (redNodesCoordinates != null && redNodesCoordinates.size() > 0) {
            for (Coordinate coordinate : redNodesCoordinates) {
                mPaint.setColor(RED_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawCircle(coordinate.x, coordinate.y, BRUSH_SIZE + 30, mPaint);
            }
        }
        createCharsInNodes();

        if (redNodesCoordinates != null && redNodesCoordinates.size() > 0) {
            for (Coordinate coordinate : redNodesCoordinates) {
                mPaint.setColor(RED_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawCircle(coordinate.x, coordinate.y, BRUSH_SIZE + 30, mPaint);

                mPaint.setColor(Color.BLACK);
                mPaint.setTextSize(80);
                mCanvas.drawText("A", coordinate.x - (BRUSH_SIZE * 2), coordinate.y + (BRUSH_SIZE * 2), mPaint);
            }
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore(); //v případě, že jsme nějak otočili, zvětšili, apod. canvas, tak ho to navrátí do "výchozí" podoby

    }

    /**
     * Vytvoří HashMapu pro všechny uzly
     * Při změně se aktualizuje jenom příslušný záznam (pro update se smaže starý a vytvoří nový se stejným písmenem)
     */
    private void createCharsInNodes() {
        //pokud je hashmapa prázdná, vytvoř všechny záznamy
        if (charsForNodes == null) {
            charsForNodes = new HashMap<>();
            if (circleCoordinates.size() > 0) {
                for (Coordinate coordinate : circleCoordinates) {
                    charValue++;
                    if(charValue > 90) charValue = "A".charAt(0); //pokud jsme překročili Z, vrať se na A
                    String nextChar = String.valueOf((char) charValue);
                    charsForNodes.put(coordinate, nextChar);
                }
            }
        } else {
            if (charsForNodes.size() == circleCoordinates.size()) {
                //pokud pro daný záznam nenajdeme circle coordinate, víme, že tento záznam se už změnil a musí se aktualizovat
                for (java.util.Map.Entry<Coordinate, String> charEntry : charsForNodes.entrySet()) {
                    boolean shouldBreak = false;
                    boolean found = circleCoordinates.stream().anyMatch(c -> c.equal(charEntry.getKey()));
                    //podíváme se do hashh mapy, pokud pro dany coordinate nemá žádný záznam je to náš nový coordinate
                    if (!found) {
                        for (Coordinate coordinate : circleCoordinates) {
                            boolean found2 = false;
                            for (java.util.Map.Entry<Coordinate, String> charEntry2 : charsForNodes.entrySet()) {
                                if (charEntry2.getKey().equal(coordinate)) {
                                    found2 = true;
                                }
                            }
                            //nalezli jsme záznam, který není v hash mape, takže si vezmeme písmeno, smažeme záznam a vytvoříme si nový se stejným písmenem
                            if (!found2) {
                                String value = charEntry.getValue();
                                charsForNodes.remove(charEntry.getKey());
                                charsForNodes.put(coordinate, value);
                                shouldBreak = true;
                                break;
                            }
                        }
                    }
                    if (shouldBreak) break;
                }
            } else if (charsForNodes.size() > circleCoordinates.size()) {
                //pokud pro dany zaznam nenalezneme hodnotu v nodes, tak víme, že je se ma tento záznam smazat
                for (java.util.Map.Entry<Coordinate, String> charEntry : charsForNodes.entrySet()) {
                    if (circleCoordinates.stream().noneMatch(c -> c.equal(charEntry.getKey()))) {
                        charsForNodes.remove(charEntry.getKey(), charEntry.getValue());
                        break;
                    }
                }
            } else {
                //pokud pro daný uzel není záznam v hashmape, tak se přidá s dalším textem
                for (Coordinate coordinate : circleCoordinates) {
                    boolean found = false;
                    for (java.util.Map.Entry<Coordinate, String> charEntry : charsForNodes.entrySet()) {
                        if (charEntry.getKey().equal(coordinate)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        charValue++;
                        if(charValue > 90) charValue = "A".charAt(0); //pokud jsme překročili Z, vrať se na A
                        charsForNodes.put(coordinate, String.valueOf((char) charValue));
                    }
                }
            }
        }
        if (circleCoordinates.size() > 0 && charsForNodes != null) {
            for (Coordinate coordinate : circleCoordinates) {
                mPaint.setColor(DEFAULT_COLOR);
                mPaint.setStrokeWidth(BRUSH_SIZE);
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawCircle(coordinate.x, coordinate.y, BRUSH_SIZE + 30, mPaint);

                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(80);

                //todo ošetřit písmenka
//                if(charsForNodes.get(coordinate) != null){
                    mCanvas.drawText(Objects.requireNonNull(charsForNodes.get(coordinate), "hashmapa nemá pro tento coordinate hodnotu"), coordinate.x - (BRUSH_SIZE * 2), coordinate.y + (BRUSH_SIZE * 2), mPaint);
//                }
            }
        }
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
                //když je zařízení pomalejší a vykreslí se fragment rychleji než view, tak se neprovede některá část a spadne to
            } else if (lineCoordinates.size() > 1) {
                lineCoordinates.set(1, new Coordinate(x, y));
            }
            previousXCoordinate = x;
            previousYCoordinate = y;
        }
    }

    private void touchStartCircle(float x, float y) {
        //pokud je režim posouvani, mrkni na vsechny uzly, jestli zrovna neklikas na nejaky z nich
        if (move) {
            for (Coordinate coordinate : circleCoordinates) {
                if (checkIsInCircle(coordinate.x, coordinate.y, x, y)) {
                    isCircleDragged = true;
                    firstCoordinate = coordinate;
                    break;
                }
            }
            for (Coordinate coordinate : redNodesCoordinates) {
                if (checkIsInCircle(coordinate.x, coordinate.y, x, y)) {
                    isCircleDragged = true;
                    firstCoordinate = coordinate;
                    break;
                }
            }
        }
        if (circle) {
            //pokud neni klepnuto na žádný kruh, vytvoří se nový
            if (!isCircleDragged) {
                circleCoordinates.add(new Coordinate(x, y));
            }
        }
    }


    private void circleDragged(float x, float y) {
        //pokud posouvame uzly, tak se podivej, jestli posouvame cervenym, nebo normalnim uzlem a podle toho vymaz prislusnou pozici starsiho uzlu
        boolean movingWithRedCircle = false;
        if (circleCoordinates.stream().anyMatch(c -> c.equal(firstCoordinate))) {
            circleCoordinates.remove(firstCoordinate);
        } else {
            movingWithRedCircle = true;
            redNodesCoordinates.remove(firstCoordinate);
        }
        for (Coordinate coordinate : allLineList) {
            //při tažení se přehodí i souřadnice přímky
            if (coordinate.x == firstCoordinate.x && coordinate.y == firstCoordinate.y) {
                coordinate.x = x;
                coordinate.y = y;
            }
        }
        for (Coordinate coordinate : redLineList) {
            //při tažení se přehodí i souřadnice přímky
            if (coordinate.x == firstCoordinate.x && coordinate.y == firstCoordinate.y) {
                coordinate.x = x;
                coordinate.y = y;
            }
        }
        //podle toho jeslti jsme posouvali cervenym uzlem, nebo normalnim uzlem, tak pridej novou pozici uzlu do seznamu
        firstCoordinate = new Coordinate(x, y);
        if (!movingWithRedCircle) {
            circleCoordinates.add(firstCoordinate);
        } else {
            redNodesCoordinates.add(firstCoordinate);
        }
    }

    private void removeObject(float x, float y) {
        for (Coordinate coordinate : circleCoordinates) {
            if (checkIsInCircle(coordinate.x, coordinate.y, x, y)) {
                circleCoordinates.remove(coordinate);
                //projde všechny vrcholy a pokud maji stejnou souřadnici, jako střed kruhu, tak je smaže včetně párového (je to přímka, takže druhá souřadnice)
                for (int i = 0; i < allLineList.size(); i++) {
                    if (allLineList.get(i).x == coordinate.x && allLineList.get(i).y == coordinate.y) {
                        if (i % 2 == 0) {
                            allLineList.remove(i);
                            allLineList.remove(i);
                            i = i - 1; //abychom nepřeskočili žádnou hranu
                        } else {
                            allLineList.remove(i - 1);
                            allLineList.remove(i - 1);
                            i = i - 2; //abychom nepřeskočili žádnou hranu
                        }
                    }
                }
                for (int i = 0; i < redLineList.size(); i++) {
                    if (redLineList.get(i).x == coordinate.x && redLineList.get(i).y == coordinate.y) {
                        if (i % 2 == 0) {
                            redLineList.remove(i);
                            redLineList.remove(i);
                            i = i - 1; //abychom nepřeskočili žádnou hranu
                        } else {
                            redLineList.remove(i - 1);
                            redLineList.remove(i - 1);
                            i = i - 2; //abychom nepřeskočili žádnou hranu
                        }
                    }
                }
                break;
            }
        }

        //převedeni allLine do CommonMathsLines a zjištění, zdali souřadnice uživatelova klepnutí neleží v blízkosti někteřé přímky
        for (int i = 0; i < allLineList.size(); i++) {
            if (i % 2 != 0) {
                Line line = new Line(new Vector2D(allLineList.get(i - 1).x, allLineList.get(i - 1).y), new Vector2D(allLineList.get(i).x, allLineList.get(i).y), 1); //přímka
                Segment segment = new Segment(new Vector2D(allLineList.get(i - 1).x, allLineList.get(i - 1).y), new Vector2D(allLineList.get(i).x, allLineList.get(i).y), line); //úsečka
                if (segment.distance(new Vector2D(x, y)) < TOUCH_TOLERANCE_FINGER_TAPPED) {
                    allLineList.remove(i);
                    allLineList.remove(i - 1);
                    break;
                }
            }
        }

        for (int i = 0; i < redLineList.size(); i++) {
            if (i % 2 != 0) {
                Line line = new Line(new Vector2D(redLineList.get(i - 1).x, redLineList.get(i - 1).y), new Vector2D(redLineList.get(i).x, redLineList.get(i).y), 1); //přímka
                Segment segment = new Segment(new Vector2D(redLineList.get(i - 1).x, redLineList.get(i - 1).y), new Vector2D(redLineList.get(i).x, redLineList.get(i).y), line); //úsečka
                if (segment.distance(new Vector2D(x, y)) < TOUCH_TOLERANCE_FINGER_TAPPED) {
                    redLineList.remove(i);
                    redLineList.remove(i - 1);
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
                    if (line) {
                        allLineList.add(firstLineCoordinate);
                        allLineList.add(secondLineCoordinate);
                    } else if (path) {
                        redLineList.add(firstLineCoordinate);
                        redLineList.add(secondLineCoordinate);
                    }
                }
            }
        }
    }

    private boolean checkIsInCircle(float circle_x, float circle_y, float point_x,
                                    float point_y) {
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

                if (line || path) touchStart(x, y);
                if (circle || move) touchStartCircle(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (line || path) touchMove(x, y);
                if (isCircleDragged) circleDragged(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (line || path) {
                    touchUp(x, y);
                    touchMove(x, y);
                }
                if (isCircleDragged)
                    isCircleDragged = false; //aby to neposouvalo v dalším tahu kruhy
                if (remove) removeObject(x, y);
                invalidate();
                if (mListener != null) mListener.sentTouchUpCoordinates(new Coordinate(x, y));
                break;
        }
        return true;
    }

    /**
     * get map for DrawMapViewModel
     */
    public Map getMap() {

        ArrayList<Edge> lines = new ArrayList<>();
        for (int x = 0; x < allLineList.size(); x++) {
            if (x % 2 != 0) {
                Edge line = new Edge(allLineList.get(x - 1), allLineList.get(x));
                lines.add(line);
            }
        }

        ArrayList<Edge> path = new ArrayList<>();
        for (int x = 0; x < redLineList.size(); x++) {
            if (x % 2 != 0) {
                Edge line = new Edge(redLineList.get(x - 1), redLineList.get(x));
                path.add(line);
            }
        }
        return new Map(lines, circleCoordinates, path, redNodesCoordinates);
    }

    public void setMap(Map map) {
        ArrayList<Edge> lines = map.getEdges();
        ArrayList<Edge> path = map.getRedEdgesList();
        charsForNodes = null;

        allLineList.clear();
        redLineList.clear();

        if (map.getNodes().isEmpty()) circleCoordinates.clear();
        if (map.getNodes().isEmpty()) redNodesCoordinates.clear();
        circleCoordinates = map.getNodes();
        redNodesCoordinates = map.getRedNodes();
        if (!circleCoordinates.isEmpty() || !allLineList.isEmpty() || !redNodesCoordinates.isEmpty()) {
            invalidate();
        }

        for (int i = 0; i < lines.size(); i++) {
            allLineList.add(new Coordinate(lines.get(i).getFrom().x, lines.get(i).getFrom().y));
            allLineList.add(new Coordinate(lines.get(i).getTo().x, lines.get(i).getTo().y));
            invalidate();
        }

        for (int i = 0; i < path.size(); i++) {
            redLineList.add(new Coordinate(path.get(i).getFrom().x, path.get(i).getFrom().y));
            redLineList.add(new Coordinate(path.get(i).getTo().x, path.get(i).getTo().y));
            invalidate();
        }
    }

    public static int getBrushSize() {
        return BRUSH_SIZE;
    }

    public void setmListener(CommunicationInterface mListener) {
        this.mListener = mListener;
    }

    public interface CommunicationInterface {
        public void sentTouchUpCoordinates(Coordinate coordinate);
    }
}
