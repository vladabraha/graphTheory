package cz.uhk.graphstheory.util;

import android.util.Log;
import android.util.SparseArray;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphGenerator {

    private static final int DISTANCE_BETWEEN_NEAREST_NODE = 100;

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
    private static ArrayList<CustomLine> generateRandomEdges(ArrayList<Coordinate> circlesPoints) {

        int amountOfEdges = (int) (Math.random() * circlesPoints.size()); //nahodny pocet hran
        if (amountOfEdges < 6) amountOfEdges++;

        //vezmeme nahodny uzel na indexu a mrkneme na seznam, se kterymi dalsimi prvky je spojen
        //pokud neni jeste spojen s nahodnym uzlem, je dany uzel pridan do seznamu
        SparseArray<ArrayList<Integer>> connectedNodes = new SparseArray<>(circlesPoints.size());
        int createdEdges = 0;
        do {
            int randomIndex = (int) (Math.random() * (circlesPoints.size() - 1));
            ArrayList<Integer> listOfConnectedNodes = connectedNodes.valueAt(randomIndex); //nahodny uzel

            boolean isThisNumberInList = false;

            if (listOfConnectedNodes == null) {
                listOfConnectedNodes = new ArrayList<>(); //pokud jeste nema seznam propojenych vrcholu, tak je vytvoren
            } else {
                //koukneme, abychome nepridavali 2 stejne uzly
                for (Integer integer : listOfConnectedNodes) {
                    if (integer.equals(createdEdges)) {
                        isThisNumberInList = true;
                    }
                }
            }

            //a pokud tam jeste zadny takovy neni a odkaz neni na sama sebe, tak ho pridame
            if (!isThisNumberInList && createdEdges != randomIndex) {
                listOfConnectedNodes.add(createdEdges);
                connectedNodes.put(randomIndex, listOfConnectedNodes);
                createdEdges++;
            }

        } while (createdEdges < amountOfEdges);

        //kontrola, zdali neni nejaky uzel osamocen (graf pak vypada divne)
        for (int i = 0; i < circlesPoints.size(); i++) { //vezmeme prvni node
            //sparse array může mít zaplenene jenom nektere indexy (např. když má size 2, tak může mít zaplneny jenom index 1 a 5 a mezitím nic -> velikost 2)
            boolean isNodeConnectedToAnotherNode = false;

            for (int j = 0; j < connectedNodes.size(); j++) { //prohledame vsechny seznamy, zdali je v nějakem -> tzn. je pripojen k jinemu
                //hledani indexu s nenulovym seznamem
                boolean isIndexNotEmpty = false;
                ArrayList<Integer> arrayList;
                int next = 0;
                do {
                    arrayList = connectedNodes.get(j + next);
                    if (arrayList != null) {
                        isIndexNotEmpty = true; //mame index, hura
                    } else {
                        next++;
                    }
                } while (!isIndexNotEmpty);

                if (arrayList.contains(i)) { //mrkneme, zdali dany seznam obsahuje naš node
                    isNodeConnectedToAnotherNode = true; //pokud ano, hura, tenhle node je propojen jdeme na dalsi
                    break;
                }
            }

            if (!isNodeConnectedToAnotherNode) { //pokud jsme node nikde v seznamu nenasli, pridameho do nejakeho
                //hledame dostupny index (nektere mohou byt null)
                for (int v = 0; v < connectedNodes.size(); v++) {
                    ArrayList<Integer> arrayList = connectedNodes.get(v);
                    if (arrayList != null && i != v) {
                        arrayList.add(i);
                        connectedNodes.put(v, arrayList);
                        break;
                    }
                }
            }
        }


        ArrayList<CustomLine> edges = new ArrayList<>();

        //projdeme vsechny uzly a projdeme jejich seznamy, se kterymi uzly sousedi jsou spojeni a vytvorime Customeline pro vytvoreni Liny ve View
        for (int i = 0; i < connectedNodes.size(); i++) {
            ArrayList<Integer> indexesOfNodes = connectedNodes.valueAt(i);
            if (indexesOfNodes != null && !indexesOfNodes.isEmpty())
                for (Integer integer : indexesOfNodes) {
                    CustomLine customLine = new CustomLine(circlesPoints.get(i), circlesPoints.get(integer));
                    edges.add(customLine);
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
            float xCoordinate = (float) (Math.random() * width);
            float yCoordinate = (float) (Math.random() * height);

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
