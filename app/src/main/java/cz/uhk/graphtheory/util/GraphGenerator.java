package cz.uhk.graphtheory.util;

import android.util.Log;
import android.util.SparseArray;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Objects;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Graph;

public class GraphGenerator {

    private static final int DISTANCE_BETWEEN_NEAREST_NODE = 300;

    /**
     * generate random graph
     *
     * @param height dimension x
     * @param width  dimension y
     * @return generated graph
     */
    public static Graph generateGraph(int height, int width, int BRUSH_SIZE, int amountOfNodes) {
        ArrayList<Coordinate> nodes = generateNodes(height - BRUSH_SIZE, width - BRUSH_SIZE, BRUSH_SIZE, amountOfNodes);
        ArrayList<Edge> edges;
        Graph graph;
        do {
            edges = generateRandomEdges(nodes);
            graph = new Graph(edges, nodes);
        }while (checkIfGraphIsSplitInTwo(graph));
        return graph;
    }

    /**
     * generate random amount of edges based on minimum amount of edges
     *
     * @param nodesPoints nodes which should be connected
     * @return list of lines (connecting nodes)
     */
    public static ArrayList<Edge> generateRandomEdges(ArrayList<Coordinate> nodesPoints) {
        int amountOfNodes = nodesPoints.size();
        int maximumOfEdges = (amountOfNodes * (amountOfNodes - 1)) / 2; //viz. definice uplneho grafu
        if (amountOfNodes > 3)
            maximumOfEdges = maximumOfEdges - 1; //maximum je třeba zmenšit o jedna, aby byly vidět alg., které se na graf aplikují
        int amountOfEdges = (int) Math.round(Math.random() * maximumOfEdges); //nahodny pocet hran
        if (amountOfEdges < maximumOfEdges) amountOfEdges++;

        //vezmeme nahodny uzel na indexu a mrkneme na seznam, se kterymi dalsimi prvky je spojen
        //pokud neni jeste spojen s nahodnym uzlem, je dany uzel pridan do seznamu
        SparseArray<ArrayList<Integer>> connectedNodes = new SparseArray<>(nodesPoints.size());
        int createdEdges = 0;
        int run = 0;
        int RUN_THRESHOLD = 500;
        do {
            run++;
            int randomIndex = (int) Math.round(Math.random() * (nodesPoints.size() - 1));
            int randomIndex2 = (int) Math.round(Math.random() * (nodesPoints.size() - 1));
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
        for (int i = 0; i < nodesPoints.size(); i++) { //vezmeme prvni node
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
                    } while (!isIndexNotEmpty && next < nodesPoints.size() && run < RUN_THRESHOLD);

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

                    if (nodesPoints.size() < 2) {
                        switch (i) {
                            case 0:
                                index = 1;
                            case 1:
                                index = 0;
                        }
                    } else {
                        do {
                            index = (int) Math.round(Math.random() * (nodesPoints.size() - 1));
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

        ArrayList<Edge> edges = new ArrayList<>();

        //projdeme vsechny uzly a projdeme jejich seznamy, se kterymi uzly sousedi jsou spojeni a vytvorime Customeline pro vytvoreni Liny ve View
        for (int i = 0; i < connectedNodes.size(); i++) {
            //dostat další hodnotu ve sparseArray (která může být na jiném indexu)
            int key = connectedNodes.keyAt(i);
            // get the object by the key.
            ArrayList<Integer> indexesOfNodes = connectedNodes.get(key);
            if (indexesOfNodes != null) {
                for (Integer integer : indexesOfNodes) {
                    Edge edge = new Edge(nodesPoints.get(key), nodesPoints.get(integer));
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    public static ArrayList<Coordinate> generateNodes(int height, int width, int BRUSH_SIZE, int maximumAmountOfNodes) {
        ArrayList<Coordinate> coordinateArrayList = new ArrayList<>();
        int foundedNodes = 0;
        int run = 0;


        //Tady se generuji body, ktere nelezi na sobe
        boolean shouldRun = true;
        do {
            run++;
            //ohraniceni, kvuli vyjizdecimu menu a spodku
            float xBorder = 210; //o kolik se snizi viewport - musi byt vetsi číslo, než xcoodrinate, jinak vyleti prvek z obrazu)
            float yBorder = 210;
            float xCoordinate = (float) (Math.random() * (width - xBorder));
            xCoordinate += 150;
            float yCoordinate = (float) (Math.random() * (height - yBorder));
            yCoordinate += 60;

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
            if (foundedNodes == maximumAmountOfNodes) shouldRun = false;
            if (run > (maximumAmountOfNodes * maximumAmountOfNodes * 500)) shouldRun = false;

        } while (shouldRun);
        return coordinateArrayList;
    }

    /**
     * Metoda využívá alg. pro ověřování mostu - vezme přímku a podívá se kam všude se může ze sousedních bodů dostat
     * Pokud dají obě strany počet uzlů n - 2, kde n je počet všech uzlů, tak víme, že se jedná o spojitý graf, jinak nikoliv
     * @param graph mapa, která se má prozkoumat
     * @return true pokud je graf nespojitý
     */
    private static boolean checkIfGraphIsSplitInTwo(Graph graph){
        ArrayList<Edge> edges = graph.getEdges();
        ArrayList<Coordinate> nodes = graph.getNodes();

        Coordinate oneEndOfCustomLine = edges.get(0).getFrom();
        Coordinate secondEndOfCustomLine = edges.get(0).getTo();

        //myslenka - projdu vsechny sousedy od prvniho bodu a budu si pamatovat, ktery jsem prosel
        //v dalsim kole budu prochazet sousedy sousedů, ktere jsem jeste nenavstivil, takhle postupne projdu vsechny z teho kategorie
        //na konci by mi meli chybet v seznamu nejake uzly - ty z druhe strany, kterou artikulace spojovala
        ArrayList<Coordinate> alreadyVisitedNodesFirstEndOfBridge = new ArrayList<>(); //seznam pro jeden konec přímky
        ArrayList<Coordinate> nodesToExploreFirstEndOfBridge = new ArrayList<>();
        ArrayList<Coordinate> alreadyVisitedNodesSecondEndOfBridge = new ArrayList<>(); //seznam pro druhý konec přímky
        ArrayList<Coordinate> nodesToExploreSecondEndOfBridge = new ArrayList<>();


        //přihodím do prohledávání všechny uzly, které jsou propojeny s jedním koncem přímky
        //algoritmus totiž prochází všechny uzly z nodesToExplore, ale neleze tam, kde se to dotýká konce čáry, čímž může v některých situacích vyhodnotit špatně propojení
        for (Edge edge : edges) {
            if (edge.getFrom().equal(oneEndOfCustomLine) && !edge.getTo().equal(secondEndOfCustomLine)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                    nodesToExploreFirstEndOfBridge.add(edge.getTo());
                    alreadyVisitedNodesFirstEndOfBridge.add(edge.getTo());
                }
            } else if (edge.getTo().equal(oneEndOfCustomLine) && !edge.getFrom().equal(secondEndOfCustomLine)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                    nodesToExploreFirstEndOfBridge.add(edge.getFrom());
                    alreadyVisitedNodesFirstEndOfBridge.add(edge.getFrom());
                }
            }else if (edge.getTo().equal(secondEndOfCustomLine) && !edge.getFrom().equal(oneEndOfCustomLine)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                    alreadyVisitedNodesSecondEndOfBridge.add(edge.getFrom());
                    nodesToExploreSecondEndOfBridge.add(edge.getFrom());
                }
            }else if (edge.getFrom().equal(secondEndOfCustomLine) && !edge.getTo().equal(oneEndOfCustomLine)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                    alreadyVisitedNodesSecondEndOfBridge.add(edge.getTo());
                    nodesToExploreSecondEndOfBridge.add(edge.getTo());
                }
            }
        }

        //teď projdu všechny sousedy nalezených uzlů pro jeden konec grafu
        for (int i = 0; i < nodesToExploreFirstEndOfBridge.size(); i++) {
            Coordinate coordinateToExplore = nodesToExploreFirstEndOfBridge.get(i);
            for (Edge edge : edges) {
                //nejdriv kontrola, ze se nepresuneme pres konec čáry do druhe půlky
                if (!edge.isPointInStartOrEndOfLine(oneEndOfCustomLine) && !edge.isPointInStartOrEndOfLine(secondEndOfCustomLine)) {
                    if (edge.getFrom().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                            alreadyVisitedNodesFirstEndOfBridge.add(edge.getTo());
                            nodesToExploreFirstEndOfBridge.add(edge.getTo());
                        }
                    } else if (edge.getTo().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                            alreadyVisitedNodesFirstEndOfBridge.add(edge.getFrom());
                            nodesToExploreFirstEndOfBridge.add(edge.getFrom());
                        }
                    }
                }
            }
            nodesToExploreFirstEndOfBridge.remove(i);
            i--;
        }

        //a všechny sousedy druhého konce grafu
        for (int i = 0; i < nodesToExploreSecondEndOfBridge.size(); i++) {
            Coordinate coordinateToExplore = nodesToExploreSecondEndOfBridge.get(i);
            for (Edge edge : edges) {
                //nejdriv kontrola, ze se nepresuneme pres konec čáry do druhe půlky
                if (!edge.isPointInStartOrEndOfLine(oneEndOfCustomLine) && !edge.isPointInStartOrEndOfLine(secondEndOfCustomLine)) {
                    if (edge.getFrom().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesSecondEndOfBridge.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                            alreadyVisitedNodesSecondEndOfBridge.add(edge.getTo());
                            nodesToExploreSecondEndOfBridge.add(edge.getTo());
                        }
                    } else if (edge.getTo().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesSecondEndOfBridge.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                            alreadyVisitedNodesSecondEndOfBridge.add(edge.getFrom());
                            nodesToExploreSecondEndOfBridge.add(edge.getFrom());
                        }
                    }
                }
            }
            nodesToExploreSecondEndOfBridge.remove(i);
            i--;
        }

        //pokud ani jeden seznam nemá n-2 uzlů, nejedná se o rozdělený graf
        return alreadyVisitedNodesFirstEndOfBridge.size() != nodes.size() - 2 && alreadyVisitedNodesSecondEndOfBridge.size() != nodes.size() - 2;
    }
}
