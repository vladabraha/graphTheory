package cz.uhk.graphstheory.util;

import android.util.Log;
import android.util.SparseArray;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphGenerator {

    private static final int DISTANCE_BETWEEN_NEAREST_NODE = 200;

    /**
     * generate random map
     *
     * @param height dimension x
     * @param width  dimension y
     * @return generated map
     */
    public static Map generateMap(int height, int width, int BRUSH_SIZE, int amountOfNodes) {
        ArrayList<Coordinate> circles = generateNodes(height - BRUSH_SIZE, width - BRUSH_SIZE, BRUSH_SIZE, amountOfNodes);
        ArrayList<CustomLine> customLines = generateRandomEdges(circles);
        return new Map(customLines, circles);
    }

    /**
     * generate random amount of edges based on minimum amount of edges
     *
     * @param circlesPoints nodes which should be connected
     * @return list of lines (connecting nodes)
     */
    public static ArrayList<CustomLine> generateRandomEdges(ArrayList<Coordinate> circlesPoints) {
        int amountOfNodes = circlesPoints.size();
        int maximumOfEdges = (amountOfNodes * (amountOfNodes - 1)) / 2; //viz. definice uplneho grafu
        if (amountOfNodes > 3) maximumOfEdges = maximumOfEdges - 1; //maximum je třeba zmenšit o jedna, aby byly vidět alg., které se na graf aplikují
        int amountOfEdges = (int) Math.round(Math.random() * maximumOfEdges); //nahodny pocet hran
        if (amountOfEdges < maximumOfEdges) amountOfEdges++;

        //vezmeme nahodny uzel na indexu a mrkneme na seznam, se kterymi dalsimi prvky je spojen
        //pokud neni jeste spojen s nahodnym uzlem, je dany uzel pridan do seznamu
        SparseArray<ArrayList<Integer>> connectedNodes = new SparseArray<>(circlesPoints.size());
        int createdEdges = 0;
        int run = 0;
        int RUN_THRESHOLD = 500;
        do {
            run++;
            int randomIndex = (int) Math.round(Math.random() * (circlesPoints.size() - 1));
            int randomIndex2 = (int) Math.round(Math.random() * (circlesPoints.size() - 1));
            //pro velmi male grafy, kde jsou napr. pouze 2 nody jede algoritmus hrozne dlouho, optimalizace pro tyto pripady
            if (randomIndex2 == 0 && randomIndex2 == randomIndex) randomIndex2++;

            ArrayList<Integer> listOfConnectedNodes = connectedNodes.get(randomIndex); //nahodny uzel

            boolean isThisNumberInList = false;

            if (listOfConnectedNodes == null || listOfConnectedNodes.isEmpty()) {
                listOfConnectedNodes = new ArrayList<>(); //pokud jeste nema seznam propojenych vrcholu, tak je vytvoren
            } else {
                //koukneme, abychome nepridavali 2 stejne uzly
                for (Integer integer : listOfConnectedNodes) {
                    if (integer.equals(randomIndex2)) {
                        isThisNumberInList = true;
                    }
                }
            }
            //je potřeba ještě mrknout, zdali už nejsou body propojeny přesně naopak
            if (!isThisNumberInList) {

                ArrayList<Integer> listOfConnectedNodes2 = connectedNodes.get(randomIndex2); //nahodny uzel
                if (listOfConnectedNodes2 != null) {
                    //koukneme, abychome nepridavali 2 stejne uzly
                    for (Integer integer : listOfConnectedNodes2) {
                        if (integer.equals(randomIndex)) {
                            isThisNumberInList = true;
                        }
                    }
                }
            }

            //a pokud tam jeste zadny takovy neni a odkaz neni na sama sebe, tak ho pridame
            if (!isThisNumberInList && randomIndex2 != randomIndex) {
                listOfConnectedNodes.add(randomIndex2);
                connectedNodes.put(randomIndex, listOfConnectedNodes);
                createdEdges++;
            }

        } while (createdEdges < amountOfEdges && run < RUN_THRESHOLD);

        //kontrola, zdali neni nejaky uzel osamocen (graf pak vypada divne)
        for (int i = 0; i < circlesPoints.size(); i++) { //vezmeme prvni node
            //sparse array může mít zaplenene jenom nektere indexy (např. když má size 2, tak může mít zaplneny jenom index 1 a 5 a mezitím nic -> velikost 2)
            boolean isNodeConnectedToAnotherNode = false;
            run = 0;
            //kontrola, zdali v indexu daneho uzlu už nejsou nějaké hodnoty se kterými je uzel propojen (když je na indexu 0 napr. 1, tak nemusi byt na indexu 1 hodnota 0)
            if (connectedNodes.get(i) == null || connectedNodes.get(i).isEmpty()) {


                for (int j = 0; j < connectedNodes.size(); j++) { //prohledame vsechny seznamy, zdali je v nějakem -> tzn. je pripojen k jinemu
                    //hledani indexu s nenulovym seznamem
                    boolean isIndexNotEmpty = false;

                    ArrayList<Integer> arrayList;
                    int next = 0;
                    do {
                        run++;
                        arrayList = connectedNodes.get(j + next);
                        if (arrayList != null && !arrayList.isEmpty()) {
                            isIndexNotEmpty = true; //mame index, hura
                        } else {
                            next++;
                        }
                    } while (!isIndexNotEmpty && next < circlesPoints.size() && run < RUN_THRESHOLD);

                    if (Objects.requireNonNull(arrayList).contains(i)) { //mrkneme, zdali dany seznam obsahuje naš node
                        isNodeConnectedToAnotherNode = true; //pokud ano, hura, tenhle node je propojen jdeme na dalsi
                        break;
                    }
                }

                if (!isNodeConnectedToAnotherNode) { //pokud jsme node nikde v seznamu nenasli, pridameho do nejakeho
                    //pokud neni node s ničím spojen, vezmeme náhodný index, který není stejný
                    //pokud tam jeste neni seznam, tak ho vytvorime
                    //na daném indexu pridame node do seznamu
                    int index = 0;
                    boolean found = false;

                    if (circlesPoints.size() < 2) {
                        switch (i) {
                            case 0:
                                index = 1;
                            case 1:
                                index = 0;
                        }
                    } else {
                        do {
                            index = (int) Math.round(Math.random() * (circlesPoints.size() - 1));
                            if (i != index) found = true;
                        } while (!found);
                    }
                    ArrayList<Integer> arrayList = connectedNodes.get(index);
                    if (arrayList != null) {
                        arrayList.add(i);
                        connectedNodes.put(index, arrayList);
                    } else {
                        arrayList = new ArrayList<>();
                        arrayList.add(i);
                        connectedNodes.put(index, arrayList);
                    }
                }
            }
        }

        ArrayList<CustomLine> edges = new ArrayList<>();

        //projdeme vsechny uzly a projdeme jejich seznamy, se kterymi uzly sousedi jsou spojeni a vytvorime Customeline pro vytvoreni Liny ve View
        for (int i = 0; i < connectedNodes.size(); i++) {
            //dostat další hodnotu ve sparseArray (která může být na jiném indexu)
            int key = connectedNodes.keyAt(i);
            // get the object by the key.
            ArrayList<Integer> indexesOfNodes = connectedNodes.get(key);
            if (indexesOfNodes != null) {
                for (Integer integer : indexesOfNodes) {
                    CustomLine customLine = new CustomLine(circlesPoints.get(key), circlesPoints.get(integer));
                    edges.add(customLine);
                }
            }
        }
        return edges;
    }

    public static ArrayList<Coordinate> generateNodes(int height, int width, int BRUSH_SIZE, int amountOfNodes) {
        ArrayList<Coordinate> coordinateArrayList = new ArrayList<>();
        int foundedNodes = 0;
        int run = 0;


        //Tady se generuji body, ktere nelezi na sobe
        boolean shouldRun = true;
        do {
            run++;
            //ohraniceni, kvuli vyjizdecimu menu a spodku
            float xBorder = 30;
            float yBorder = 150;
            float xCoordinate = (float) (Math.random() * (width - xBorder));
            xCoordinate+= 20;
            float yCoordinate = (float) (Math.random() * (height - yBorder));
            yCoordinate+= 60;

            //pokud vyjde male cislo, pripocitame souradnice, aby nelezely na okraji obrazovky
            if (xCoordinate < BRUSH_SIZE) xCoordinate += BRUSH_SIZE;
            if (yCoordinate < BRUSH_SIZE) yCoordinate += BRUSH_SIZE;


            boolean isInOtherCircle = false;
            for (Coordinate coordinate : coordinateArrayList) {
                //zkontroluje, zdali neni kolize (common maths na tom nejede)
                double D = Math.pow(xCoordinate - coordinate.x, 2) + Math.pow(yCoordinate - coordinate.y, 2);
                if (D <= Math.pow(BRUSH_SIZE, 2)) {
                    isInOtherCircle = true;
                }
            }

            //kontrola jeste na vzdalenost
            if (!isInOtherCircle) {
                Vector2D newCoordinate = new Vector2D(xCoordinate, yCoordinate);
                for (Coordinate oldCoordinate : coordinateArrayList) {
                    Vector2D coordinate = new Vector2D(oldCoordinate.x, oldCoordinate.y);
                    Log.d("distance", oldCoordinate.x + " " + oldCoordinate.y);
                    if (coordinate.distance(newCoordinate) < DISTANCE_BETWEEN_NEAREST_NODE) {
                        isInOtherCircle = true;
                    }
                }
            }
            if (!isInOtherCircle) {
                Coordinate newCoordinate = new Coordinate(xCoordinate, yCoordinate);
                coordinateArrayList.add(newCoordinate);
                foundedNodes++;
            }
            if (foundedNodes == amountOfNodes) shouldRun = false;
            if (run > (amountOfNodes * amountOfNodes * 500)) shouldRun = false;

        } while (shouldRun);
        return coordinateArrayList;
    }
}
