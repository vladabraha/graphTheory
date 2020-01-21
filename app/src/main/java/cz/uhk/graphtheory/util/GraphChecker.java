package cz.uhk.graphtheory.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import cz.uhk.graphtheory.model.Coordinate;
import cz.uhk.graphtheory.model.Edge;
import cz.uhk.graphtheory.model.Map;

public class GraphChecker {

    public static boolean checkIfGraphContainsPath(Map map) {
        if (map != null) {

            //mrknu, že každý bod je v seznamu maximálně 2x
            ArrayList<Edge> path = map.getRedEdgesList();
            ArrayList<Edge> edges = map.getEdges();
            if (path.size() < 1) return false;
            ArrayList<Coordinate> coordinateArrayList = new ArrayList<>();

            for (int i = 0; i < path.size(); i++) {
                //nejdřív kontrola, že červená čára je i ve hranách
                int finalI = i;
                if (edges.stream().noneMatch(n -> n.isEdgeSame(path.get(finalI)))) {
                    return false;
                }
                coordinateArrayList.add(path.get(i).getFrom());
                coordinateArrayList.add(path.get(i).getTo());
            }
            for (Coordinate coordinate : coordinateArrayList) {
                int numberOfOccurance = 0;
                for (Coordinate coordinate2 : coordinateArrayList) {
                    if (coordinate.equal(coordinate2)) numberOfOccurance++;
                }
                if (numberOfOccurance > 2) return false;
            }
        } else {
            return Boolean.parseBoolean(null);
        }
        return true;
    }

    public static boolean checkIfGraphContainsTah(Map map) {
        ArrayList<Edge> redLineList = map.getRedEdgesList();
        if (redLineList.size() == 0) return false;
        ArrayList<Edge> alreadyChecked = new ArrayList<>();

        for (Edge edge : redLineList) {
            for (Edge edgeAlreadyChecked : alreadyChecked) {
                if (edge.isEdgeSame(edgeAlreadyChecked)) {
                    return false;
                }
            }
            alreadyChecked.add(edge);
        }
        return true;
    }

    public static boolean checkIfGraphContainsCycle(Map map) {
        ArrayList<Edge> redLineList = map.getRedEdgesList();
        if (redLineList.size() < 2) return false;
        Edge startingLine = redLineList.get(0);
        Edge lastLine = redLineList.get(redLineList.size() - 1);
        if (startingLine.getFrom().equal(lastLine.getTo())) {
            return true;
        } else if (startingLine.getFrom().equal(lastLine.getFrom())) {
            return true;
        } else if (startingLine.getTo().equal(lastLine.getTo())) {
            return true;
        } else return startingLine.getTo().equal(lastLine.getFrom());
    }

    public static boolean checkIfGraphIsBipartite(Map map) {
        ArrayList<Edge> edges = map.getEdges();
        ArrayList<Coordinate> nodes = map.getNodes();
        ArrayList<Coordinate> nodesConnectedTogether = new ArrayList<>();
        ArrayList<Coordinate> nodesInFirstPartOfBipartite = new ArrayList<>();
        ArrayList<Coordinate> nodesInSecondPartOfBipartite = new ArrayList<>();

        //myšlenka, hledám všechny uzly, se kterými je jeden uzel propojen a ty se kterými není si dám s ním do jedné skupiny
        //protoze u bipartitniho grafu plati, ze je jeden uzel spojen se vsemi prvky druhe skupiny, staci nam pro urceni skupiny jeden uzel
        // pro nej zjistime se kterymi uzly neni propojen a ty by meli byt propojeny se vsemi uzly druhe skupiny

        if (nodes.size() < 2 || edges.size() < 3) return false;
        Coordinate coordinate = nodes.get(0);

        for (Edge edge : edges) {
            if (edge.getFrom().equal(coordinate)) {
                nodesConnectedTogether.add(edge.getTo());
            } else if (edge.getTo().equal(coordinate)) {
                nodesConnectedTogether.add(edge.getFrom());
            }
        }

        //ted projdu vsechny uzly a podivam se, se kterymi neni propojen a podle toho je rozradim do skupin
        for (Coordinate circle : nodes) {
            boolean istheSecondPart = false;
            for (Coordinate coordinateTogether : nodesConnectedTogether) {
                if (coordinateTogether.equal(circle)) {
                    istheSecondPart = true;
                }
            }
            if (istheSecondPart) {
                nodesInSecondPartOfBipartite.add(circle);
            } else {
                nodesInFirstPartOfBipartite.add(circle);
            }
        }

        //a ted kontrola, zdali jsou uzly propojeny mezi sebou
        //projdeme vsechny cary a mrkneme, zdali ukazuji z jednoho bodu na vsechny uzly z druhe skupiny
        //pri kazdem nalezeni, odstranime bod ze seznamu druhe casti bipartitniho grafu a na konci by měl být prázdný
        for (Coordinate coordinateFirstPart : nodesInFirstPartOfBipartite) {
            ArrayList<Coordinate> nodesInSecondPartOfBipartiteCloned = new ArrayList<>();
            for (Coordinate coor : nodesInSecondPartOfBipartite){
                nodesInSecondPartOfBipartiteCloned.add(new Coordinate(coor.x, coor.y));
            }

            Iterator<Coordinate> iter = nodesInSecondPartOfBipartiteCloned.iterator();

            while (iter.hasNext()) {
                Coordinate coordinateSecondPart = iter.next();
                for (Edge edge : edges) {
                    if (edge.getTo().equal(coordinateFirstPart) && edge.getFrom().equal(coordinateSecondPart)) {
                        iter.remove();
                    } else if (edge.getTo().equal(coordinateSecondPart) && edge.getFrom().equal(coordinateFirstPart)) {
                        iter.remove();
                    }
                }
            }
            if (!nodesInSecondPartOfBipartiteCloned.isEmpty()) return false;
        }

        //a ještě kontrola, že uzly v jedné skupině bipartitního grafu nejsou spolu propojeny
        for (Coordinate coordinateFirstPart : nodesInFirstPartOfBipartite){
            for (Coordinate coordinateFirstPart2 : nodesInFirstPartOfBipartite){
                if (edges.stream().anyMatch(customLine -> (
                    customLine.isPointInStartOrEndOfLine(coordinateFirstPart) && customLine.isPointInStartOrEndOfLine(coordinateFirstPart2) && !coordinateFirstPart.equal(coordinateFirstPart2)
                ))){
                    return false;
                }
            }
        }
        for (Coordinate coordinateSecondPart : nodesInSecondPartOfBipartite){
            for (Coordinate coordinateSecondPart2 : nodesInSecondPartOfBipartite){
                if (edges.stream().anyMatch(customLine -> (
                        customLine.isPointInStartOrEndOfLine(coordinateSecondPart) && customLine.isPointInStartOrEndOfLine(coordinateSecondPart2) && !coordinateSecondPart.equal(coordinateSecondPart2)
                ))){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * projde všechny spoje na jednu stranu od označené artikulace a pokud má na konci nalezených více jak n-1 nodu vrati false
     *
     * @param map uzivatelova mapa
     * @return -
     */
    public static String checkIfGraphContainsArticulation(Map map) {
        ArrayList<Edge> edges = map.getEdges();
        ArrayList<Coordinate> nodes = map.getNodes();
        ArrayList<Coordinate> redNodes = map.getRedNodes();

        if (edges.size() < 2 || nodes.size() < 2) return "false";
        if (redNodes.size() != 1) return "chyba v poctu cervenych bodu";

        //aritkulace bude ohranicena 2 cervenymi carami
        Coordinate articulation = redNodes.get(0);

        //hledani prvniho bodu mimo artikulaci
        Coordinate firstCoordinate = null;
        for (Edge edge : edges) {
            if (edge.isPointInStartOrEndOfLine(articulation)) {
                if (edge.getFrom().equal(articulation)) {
                    firstCoordinate = edge.getTo();
                } else {
                    firstCoordinate = edge.getFrom();
                }
            }
        }

        //myslenka - mám artikulaci - vezmu jeden bod z cesty z artikulace
        //vezmu prvni caru z vrcholu a nalezeny bod hodim do seznamu a odmazu cestu ze zkopirovaneho zasobniku vsech cest
        //takhle budu pokracovat (budu si ještě kontrolovat, že nejdu zase přes artikulaci na druhou stranu
        // dokud nedojdu na konec - pokud uz dal nemuzu, vyhodim uzel ze seznamu a vezmu predposledni a zkusim pro nej najit další cesty, dokud nevyprazdnim seznma
        //na konci bych měl mít seznam všech prozkoumaných vrcholů (ty si musím evidovat ve speciálním seznamu) menší o 2 než seznam všech vrcholů, aby to byla artikulace

        ArrayList<Edge> customLinesCopy = new ArrayList<>();
        for (Edge edge : edges) {
            customLinesCopy.add(new Edge(edge));
        }
        ArrayList<Coordinate> nodesOnStack = new ArrayList<>();
        ArrayList<Coordinate> visitedNodes = new ArrayList<>();
        nodesOnStack.add(firstCoordinate);
        visitedNodes.add(firstCoordinate);
        assert firstCoordinate != null;

        do {
            Coordinate coordinateOnStack = nodesOnStack.get(nodesOnStack.size() - 1);
            boolean found = false;
            int maxRun = customLinesCopy.size();
            for (int i = 0; i < maxRun; i++) {
                Edge edge = customLinesCopy.get(i);
                if (edge.isPointInStartOrEndOfLine(coordinateOnStack)) {
                    if (edge.getFrom().equal(coordinateOnStack) && !edge.getTo().equal(articulation)) {
                        Coordinate foundCoordinate = edge.getTo();
                        if (visitedNodes.stream().noneMatch(m -> m.equal(foundCoordinate)) && !foundCoordinate.equal(articulation)) {
                            nodesOnStack.add(foundCoordinate);
                            visitedNodes.add(foundCoordinate);
                            found = true;
                            customLinesCopy.remove(i);
                            break;
                        } else {
                            customLinesCopy.remove(i);
                            i--;
                            maxRun--;
                        }
                    } else if (edge.getTo().equal(coordinateOnStack) && !edge.getFrom().equal(articulation)) {
                        Coordinate foundCoordinate = edge.getFrom();
                        if (visitedNodes.stream().noneMatch(m -> m.equal(foundCoordinate)) && !foundCoordinate.equal(articulation)) {
                            nodesOnStack.add(foundCoordinate);
                            visitedNodes.add(foundCoordinate);
                            found = true;
                            customLinesCopy.remove(i);
                            break;
                        } else {
                            customLinesCopy.remove(i);
                            i--;
                            maxRun--;
                        }
                    }
                }
            }
            if (!found && !nodesOnStack.isEmpty()) {
                nodesOnStack.remove(nodesOnStack.size() - 1);
            }
        } while (!nodesOnStack.isEmpty());

        if (visitedNodes.size() > nodes.size() - 1) {
            return "false";
        } else {
            return "true";
        }

    }

    //MYŠLENKA - od uzivatele přijde červenou čarou označený most - vezmu jeden jeho konec a podívám se na všechny sousedy toho konce a ty si uložím zvlášť do seznamu a to samé udělám pro druhý konec
    //Jakmile mám seznam sousedů jednoho konce mostu, podívám se na všechny sousedy těchto uzlů, dokud nedojdu na konec seznamu - pokud takhle najdu n-1 uzlů, tak se nejedná o most
    //tento postup budu opakovat u druhého konce mostu a jeho seznamu sousedů
    public static String checkIfGraphContainsBridge(Map userGraph) {
        ArrayList<Edge> edges = userGraph.getEdges();
        ArrayList<Edge> redEdges = userGraph.getRedEdgesList();
        ArrayList<Coordinate> nodes = userGraph.getNodes();
        if (redEdges.isEmpty()) return "chybi ohraniceni cervenou carou";
        if (nodes.size() < 3 || redEdges.size() > 1) return "false";

        //most bude označen cervenou carou
        //hledani prvniho bodu mimo most

        Coordinate oneEndOfBridge = redEdges.get(0).getFrom();
        Coordinate secondEndOfBridge = redEdges.get(0).getTo();
        Coordinate borderWhereBridgeBeggins = null; //tohle je kvuli algoritmu, ktery je stejny jako u aritkulace a potřebuje hraniční bod, přes který by neměl přejít
        for (Edge edge : edges) {
            if (edge.getFrom().equal(oneEndOfBridge) && !edge.getTo().equal(secondEndOfBridge)) {
                borderWhereBridgeBeggins = edge.getFrom();
            } else if (edge.getTo().equal(oneEndOfBridge) && !edge.getFrom().equal(secondEndOfBridge)) {
                borderWhereBridgeBeggins = edge.getTo();
            }
        }
        if (borderWhereBridgeBeggins == null) return "chybi ohraniceni cervenou carou";

        //myslenka - projdu vsechny sousedy od prvniho bodu a budu si pamatovat, ktery jsem prosel
        //v dalsim kole budu prochazet sousedy sousedů, ktere jsem jeste nenavstivil, takhle postupne projdu vsechny z teho kategorie
        //na konci by mi meli chybet v seznamu nejake uzly - ty z druhe strany, kterou artikulace spojovala
        ArrayList<Coordinate> alreadyVisitedNodesFirstEndOfBridge = new ArrayList<>(); //seznam pro jeden konec mostu
        ArrayList<Coordinate> nodesToExploreFirstEndOfBridge = new ArrayList<>();
        ArrayList<Coordinate> alreadyVisitedNodesSecondEndOfBridge = new ArrayList<>(); //seznam pro druhý konec mostu
        ArrayList<Coordinate> nodesToExploreSecondEndOfBridge = new ArrayList<>();


        //přihodím do prohledávání všechny uzly, které jsou propojeny s jedním koncem mostu
        //algoritmus totiž prochází všechny uzly z nodesToExplore, ale neleze tam, kde se to dotýká červené čáry, čímž může v některých situacích vyhodnotit špatně most
        for (Edge edge : edges) {
            if (edge.getFrom().equal(oneEndOfBridge) && !edge.getTo().equal(secondEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                    nodesToExploreFirstEndOfBridge.add(edge.getTo());
                    alreadyVisitedNodesFirstEndOfBridge.add(edge.getTo());
                }
            } else if (edge.getTo().equal(oneEndOfBridge) && !edge.getFrom().equal(secondEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                    nodesToExploreFirstEndOfBridge.add(edge.getFrom());
                    alreadyVisitedNodesFirstEndOfBridge.add(edge.getFrom());
                }
            }else if (edge.getTo().equal(secondEndOfBridge) && !edge.getFrom().equal(oneEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                    alreadyVisitedNodesSecondEndOfBridge.add(edge.getFrom());
                    nodesToExploreSecondEndOfBridge.add(edge.getFrom());
                }
            }else if (edge.getFrom().equal(secondEndOfBridge) && !edge.getTo().equal(oneEndOfBridge)) {
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
                //nejdriv kontrola, ze se nepresuneme pres artikulaci do druhe půlky
                if (!edge.isPointInStartOrEndOfLine(borderWhereBridgeBeggins) && !edge.isPointInStartOrEndOfLine(oneEndOfBridge) && !edge.isPointInStartOrEndOfLine(secondEndOfBridge)) {
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
                //nejdriv kontrola, ze se nepresuneme pres artikulaci do druhe půlky
                if (!edge.isPointInStartOrEndOfLine(borderWhereBridgeBeggins) && !edge.isPointInStartOrEndOfLine(oneEndOfBridge) && !edge.isPointInStartOrEndOfLine(secondEndOfBridge)) {
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

        //pokud ani jeden seznam nemá n-1 uzlů, nejednalo se o most
        if (alreadyVisitedNodesFirstEndOfBridge.size() == nodes.size() - 1 || alreadyVisitedNodesSecondEndOfBridge.size() == nodes.size() - 1) {
            return "false";
        } else {
            return "true";
        }
    }

    //myšlenka - projdu postupně všechny redEdges a budu si paamatovat kde jsem byl
    //na konci by měl bejt seznam 2x tak velkej než nodes (každej bod by tam měl bejt max.2 a zároveň by tam neměl chybět
    //a taky se musi jeste vratit kruznice do prvniho bodu kde zacala
    public static String checkIfGraphContainsHamiltonCircle(Map userGraph) {
        ArrayList<Edge> redEdges = userGraph.getRedEdgesList();
        ArrayList<Edge> lines = userGraph.getEdges();
        ArrayList<Coordinate> nodes = userGraph.getNodes();

        if (redEdges.isEmpty()) return "chybi ohraniceni cervenou carou";

        ArrayList<Coordinate> alreadyVisitedNodes = new ArrayList<>();

        for (Edge redLine : redEdges) {
            alreadyVisitedNodes.add(redLine.getFrom());
            alreadyVisitedNodes.add(redLine.getTo());
        }
        for (Coordinate circle : nodes) {
            if (alreadyVisitedNodes.stream().noneMatch(node -> node.equal(circle))) {
                return "false";
            }
        }
        if (alreadyVisitedNodes.size() != (nodes.size() * 2)) return "false";


        //kontrola, zdali cara neprochazi mistem, kde nebyla puvodne hrana
        for (Edge redLine : redEdges) {
            if (lines.stream().noneMatch(line -> line.isEdgeSame(redLine))) {
                return "false";
            }
        }

        //každy bod max. 2x
        for (Coordinate coordinate : alreadyVisitedNodes) {
            AtomicInteger numberOfOccurrence = new AtomicInteger();
            alreadyVisitedNodes.forEach(node -> {
                if (node.equal(coordinate)) {
                    numberOfOccurrence.getAndIncrement();
                }
            });
            if (numberOfOccurrence.get() > 2) return "false";
        }

        Edge firstRedLine = redEdges.get(0);
        Edge lastRedLine = redEdges.get(redEdges.size() - 1);
        if (!firstRedLine.isPointInStartOrEndOfLine(lastRedLine.getFrom()) && !firstRedLine.isPointInStartOrEndOfLine(lastRedLine.getTo())) {
            return "false";
        }
        return "true";
    }

    //TEORIE (reseno nakonec jinak)
    //vezmu bod se sudym počtem sousedů
    //Přidám bod do zásobníku a jdu na jeho souseda
    //Odstraním hranu ze zásobníku hran (tu mezi prvním bodem a sousedem)
    //pokračuju dál, pokud už nemám souseda, přádám ho do eulerovy cesty a odstraním vrchol ze zásobníku
    public static String checkIfGraphHasEulerPath(Map map) {
        ArrayList<Edge> redEdges = map.getRedEdgesList();
        ArrayList<Edge> lines = map.getEdges();
        ArrayList<Coordinate> nodes = map.getNodes();

        //spocitame pocet vrcholu se lichým pocetem sousedu (ty můžou být max. 2, nebo musí být všechny vrcholy sudé)
        ArrayList<Coordinate> nodesWithMoreThanTwoEvenNeighbours = new ArrayList<>();
        for (Coordinate coordinate : nodes) {
            int numberOfLinesConnectedToNode = (int) lines.stream().filter(m -> m.isPointInStartOrEndOfLine(coordinate)).count();
            if (numberOfLinesConnectedToNode % 2 == 1) {
                nodesWithMoreThanTwoEvenNeighbours.add(coordinate);
            }
        }

        if (nodesWithMoreThanTwoEvenNeighbours.size() > 2 || nodesWithMoreThanTwoEvenNeighbours.isEmpty())
            return "false";

        if (redEdges.size() != lines.size()) return "false";

        //kontrola, ze redEdges maj vsechny normalni cary a zadnou novou navic
        for (Edge line : lines) {
            boolean found = false;
            for (Edge redline : redEdges) {
                if (redline.isEdgeSame(line)) found = true;
            }
            if (!found) return "false";
        }

        for (Edge redline : redEdges) {
            boolean found = false;
            for (Edge line : lines) {
                if (line.isEdgeSame(redline)) found = true;
            }
            if (!found) return "false";
        }

        //kontrola, ze redEdges na sebe navazuji
        //tzn, ze kazdy 2 usecky musi mit spolecny prave jeden bod
        for (int i = 0; i < redEdges.size(); i++) {
            if (i > 1) {

                Edge firstLine = redEdges.get(i - 1);
                Coordinate first = firstLine.getFrom();
                Coordinate second = firstLine.getTo();

                Edge secondLine = redEdges.get(i);
                Coordinate third = secondLine.getFrom();
                Coordinate fourth = secondLine.getTo();

                if (!first.equal(third) && !first.equal(fourth) && !second.equal(third) && !second.equal(fourth)) {
                    return "false";
                }
            }
        }
        return "true";
    }

    public static boolean checkIfGraphHasCorrectScore(Map map, ArrayList<Integer> degreeList) {
        ArrayList<Edge> lines = map.getEdges();
        ArrayList<Coordinate> nodes = map.getNodes();

        if (degreeList.size() != nodes.size()) return false;

        //spocitam stupne vrcholu jednotlivych uzlu a vlozim si je do hashmapy
        HashMap<Coordinate, Integer> degreesMap = new HashMap<>();
        for (Coordinate node : nodes) {
            int degree = 0;
            for (Edge edge : lines) {
                if (edge.isPointInStartOrEndOfLine(node)) degree++;
            }
            degreesMap.put(node, degree);
        }

        //pro kazdej stupen vrcholu smazu z Hasmapy stejnou value a nemělo by mi na konci nic chybět -> tzn. projdu bez problemu a muzu rict, je to to spravne
        for (Integer degree : degreeList) {
            Coordinate coordinateToDelete = null;
            for (HashMap.Entry<Coordinate, Integer> entry : degreesMap.entrySet()) {
                if (entry.getValue().equals(degree)) {
                    coordinateToDelete = entry.getKey();
                    break;
                }
            }
            if (coordinateToDelete != null) {
                degreesMap.remove(coordinateToDelete);
            } else {
                return false;
            }
        }
        return true;
    }

    //sled
    public static boolean checkIfGraphContainsWalk(Map userGraph, int length) {
        return userGraph.getRedEdgesList().size() == length;
    }

    //myšlenka - budu prochazet vsechny usecky, ktery maj danej bod v from nebo to a druhej konec hodim do seznamu
    //na konci prohledani odstranim bod ze zasobniku a pokracuju dalsim bodem, dokud neprojdu takto vsechny vrcholy na ktere dosahnu
    //pokud mi zbydou jeste nějaké vrcholy k prozkoumání, mám druhou komponentu, vyberu tedy libovolny zbyvajici bod a provedu na nem opět prohledávání
    //toto opakuji tolikrat, kolik ma byt kompo
    public static boolean checkIfGraphHasCertainAmountOfComponent(Map userGraph, int amountOfComponent) {
        if (!checkIfGraphDoesNotContainsCycle(userGraph)) return false;
        ArrayList<Edge> edges = userGraph.getEdges();
        ArrayList<Coordinate> nodes = userGraph.getNodes();


        ArrayList<Edge> customLinesCopied = new ArrayList<>();
        ArrayList<Coordinate> nodesCopied = new ArrayList<>();

        for (Coordinate coordinate : nodes) {
            nodesCopied.add(new Coordinate(coordinate.x, coordinate.y));
        }

        for (Edge edge : edges) {
            customLinesCopied.add(new Edge(edge));
        }

        ArrayList<Coordinate> coordinatesToExplore = new ArrayList<>();
        coordinatesToExplore.add(nodesCopied.get(0));

        int foundComponents = 0;
        boolean found = false;
        do {
            for (int i = 0; i < coordinatesToExplore.size(); i++) {
                Coordinate coordinateToExplore = coordinatesToExplore.get(i);
                for (int j = 0; j < customLinesCopied.size(); j++) {
                    Edge edge = customLinesCopied.get(j);
                    if (edge.isPointInStartOrEndOfLine(coordinateToExplore)) {
                        if (edge.getFrom().equal(coordinateToExplore)) {
                            coordinatesToExplore.add(edge.getTo());
                            for (Coordinate node : nodesCopied) {
                                if (node.equal(edge.getTo())) {
                                    nodesCopied.remove(node);
                                    break;
                                }
                            }
                        } else {
                            coordinatesToExplore.add(edge.getFrom());
                            for (Coordinate node : nodesCopied) {
                                if (node.equal(edge.getFrom())) {
                                    nodesCopied.remove(node);
                                    break;
                                }
                            }
                        }
                        customLinesCopied.remove(j);
                        j--;
                    }
                }
                coordinatesToExplore.remove(i);
                i--;
            }
            if (coordinatesToExplore.isEmpty() && !nodesCopied.isEmpty()) {
                coordinatesToExplore.add(nodesCopied.get(0));
                nodesCopied.remove(0);
                foundComponents++;
            } else if (coordinatesToExplore.isEmpty()) {
                found = true;
            }
        } while (!found);
        return foundComponents == amountOfComponent;
    }

    //Myšlenka - projdu všechny uzly a ty který maj víc jak 2 liny si dám do seznamu, ten projdu a pro každej uzel z toho zkusím jestli neobsahuje kružnici
    //tzn. když budu procházet jednotlivý usečky, tak zdali se nevrátím přes některou zpátky do stejného bodu
    //alg. vezmu prvni bod spojenej s naším bodem a od něj už zkousím hledat všechny čáry, který jsou s nim spojeny - ty si dávám na stack
    //jakmile projedu všechny takové úsečky, odstraním si z nich ty, které jsem už použil pro hledání dalších vrcholů, odstraním bod ze stacku a pokračuju stejnou metodou pro další uzly
    //
    //speciální případ, pokud graf nemá žádný vrchol s více jak 2 hranami, vezmu první bod, který uživatel vybral a zkusím projet všechny navazující úsečky, zdali neobsahují kružnici
    //tedy obdobně jako v předchozím případě vezmu první spojenej bod s prvním bodem a prohledávám všechny liny, zdali některý neobsahuje můj bod, pokud jo, odstraním ho stacku, vezmu další a pokračuju dál
    public static boolean checkIfGraphDoesNotContainsCycle(Map userGraph) {
        ArrayList<Edge> edges = userGraph.getEdges();

        ArrayList<Coordinate> nodesWithMultipleLinesFirst = new ArrayList<>();
        ArrayList<Coordinate> nodesWithMultipleLinesSecond = new ArrayList<>();
        ArrayList<Coordinate> nodesToCheck = new ArrayList<>();

        //v teto casti najdeme uzly s více než 2 hranami
        for (Edge edge : edges) {
            if (nodesWithMultipleLinesFirst.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                nodesWithMultipleLinesFirst.add(edge.getTo());
            } else if (nodesWithMultipleLinesSecond.stream().noneMatch(m -> m.equal(edge.getTo()))) {
                nodesWithMultipleLinesSecond.add(edge.getTo());
            } else {
                nodesToCheck.add(edge.getTo());
            }
            if (nodesWithMultipleLinesFirst.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                nodesWithMultipleLinesFirst.add(edge.getFrom());
            } else if (nodesWithMultipleLinesSecond.stream().noneMatch(m -> m.equal(edge.getFrom()))) {
                nodesWithMultipleLinesSecond.add(edge.getFrom());
            } else {
                nodesToCheck.add(edge.getFrom());
            }
        }

        //nalezeni prvniho bodu, kterej se spojuje (ten může mít jenom 2 hrany a presto tvořit kružnici
        Coordinate firstNode;
        Coordinate firstNodeCandidate = edges.get(0).getFrom();
        Coordinate secondNodeCandidate = edges.get(0).getTo();
        if (edges.get(1).getFrom().equal(firstNodeCandidate) || edges.get(1).getTo().equal(firstNodeCandidate)) {
            firstNode = secondNodeCandidate;
        } else {
            firstNode = firstNodeCandidate;
        }

        //projed vsechny vrcholy, ktery maj vic jak 2 cary k sobe
        do {
            //tady akorat kopie
            ArrayList<Edge> customLinesCopied = new ArrayList<>();
            for (Edge edge : edges) {
                customLinesCopied.add(new Edge(edge.getFrom(), edge.getTo()));
            }
            ArrayList<Coordinate> nodesCopied = new ArrayList<>();
            for (Coordinate coordinate : nodesCopied) {
                nodesCopied.add(new Coordinate(coordinate.x, coordinate.y));
            }

            //pokud nemá žádnej vrchol více jak 2 hrany, tak to proji celý, jeslti tam nic nenajdeš od začátku
            if (nodesToCheck.isEmpty()) {
                ArrayList<Coordinate> nodesOnStack = new ArrayList<>();
                for (Edge edge : customLinesCopied) {
                    if (edge.getFrom().equal(firstNode)) {
                        nodesOnStack.add(edge.getTo());
                        customLinesCopied.remove(edge);
                        break;
                    } else if (edge.getTo().equal(firstNode)) {
                        nodesOnStack.add(edge.getFrom());
                        customLinesCopied.remove(edge);
                        break;
                    }
                }

                do {
                    Coordinate coordinate = nodesOnStack.get(0);
                    Iterator<Edge> iterator = customLinesCopied.iterator();
                    while (iterator.hasNext()) {
                        Edge edge = iterator.next();
                        if (edge.getFrom().equal(coordinate)) {
                            if (edge.getTo().equal(firstNode)) return false;
                            nodesOnStack.add(edge.getTo());
                            iterator.remove();
                        } else if (edge.getTo().equal(coordinate)) {
                            if (edge.getFrom().equal(firstNode)) return false;
                            nodesOnStack.add(edge.getFrom());
                            iterator.remove();
                        }
                    }
                    nodesOnStack.remove(0);
                } while (!nodesOnStack.isEmpty());

                return true; //pokud nema zadnej uzel vic jak 2 hrany k jinymu uzlu, je to v pořádku
            }
            Coordinate coordinate = nodesToCheck.get(0); //vezmu prvni bod k prozkoumani
            ArrayList<Coordinate> nodesOnStack = new ArrayList<>();
            ArrayList<Edge> customLinesAlreadyUsed = new ArrayList<>(); //ukladam si, ktery hrany jsem uz zkousel, abych se netočil v dalších nodech od toho nodu, ktery ma vic jak 2 hrany
            boolean stop = false;
            //hledam dalsi bod, kterej je po mem bodu, co ma vic jak 2 hrany
            do {
                for (Edge edge : customLinesCopied) {
                    if (customLinesAlreadyUsed.stream().noneMatch(m -> m.isEdgeSame(edge))) {
                        if (edge.getFrom().equal(coordinate)) {
                            nodesOnStack.add(edge.getTo());
                            customLinesAlreadyUsed.add(edge);
                            break;
                        } else if (edge.getTo().equal(coordinate)) {
                            nodesOnStack.add(edge.getFrom());
                            customLinesAlreadyUsed.add(edge);
                            break;
                        }
                    }
                }
                //pokud jsem zadnej bod nenašel (nebo už mi všechny ostatni došly) vyhodim tento bod z hledani (tam kružnice nebude)
                if (nodesOnStack.isEmpty()) {
                    stop = true;
                } else {
                    do {
                        //vezmu si posledni bod na zasobniku (na zacatku tam je akorta ten prvni bod za tim bodem co ma vic jak 2 hrany
                        Coordinate coordinateToExplore = nodesOnStack.get(nodesOnStack.size() - 1);
                        Iterator<Edge> iterator = customLinesCopied.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                            Edge edge = iterator.next();
                            //pokud najdu usecku, tkera muj bod spojuje s nejakym dalsim, hodim si ten dalsi bod do seznamu a odstranim usecku ze seznamu
                            if (customLinesAlreadyUsed.stream().noneMatch(m -> m.isEdgeSame(edge))) { //tady ještě kontrola, že neprocházím tu úsečku,k která mě už dostala k tomuto bodu
                                if (edge.getFrom().equal(coordinateToExplore)) {
                                    if (edge.getTo().equal(coordinate))
                                        return false; //pokud jsem se dostal k bodu, co ma vic jak ty 2 hrany, tak jsem objevil kruznici a vracim false
                                    nodesOnStack.add(edge.getTo());
                                    iterator.remove();
                                    found = true;
                                } else if (edge.getTo().equal(coordinateToExplore)) {

                                    if (edge.getFrom().equal(coordinate))
                                        return false; //pokud jsem se dostal k bodu, co ma vic jak ty 2 hrany, tak jsem objevil kruznici a vracim false
                                    nodesOnStack.add(edge.getFrom());
                                    iterator.remove();
                                    found = true;
                                }
                            } else {
                                iterator.remove();
                            }
                        }
                        if (!found) {
                            nodesOnStack.remove(nodesOnStack.size() - 1);
                        }
                    } while (!nodesOnStack.isEmpty());
                }
            } while (!stop);
            nodesToCheck.remove(0); //smazani zkouseneho bodu (toho co ma vic jak 2 hrany)

        } while (!nodesToCheck.isEmpty());
        return true;
    }


    /**
     * zjistí, zdali graf obsahuje kostru grafu
     * @param userGraph    uživatelem nakreselný graf
     * @param generatedMap graf, který byl pro uživatele vygenerován
     * @return graf - jinej graf, než byl vygenerován, false, červenou čarou není zvýrazněná kostra, true, červenou čarou je zvýrazněná kostra
     */
    //vzhledem k tomu, že definicí kostry grafu je to same co  stromu, tedy že neobsahuje kružnici a je spojitý, tedy má jednu komponentu, využijeme checker na strom
    //akorát zkontrolujeme, že se jedná o stejně početný graf na nody, jaký byl vygenerován a přehodíme pro checker redEdges na běžné customlines (edges)
    public static String checkIfGraphIsSpanningTree(Map userGraph, Map generatedMap) {

        //pokud je jiný počet nodů, než byl vygenerovaný, nebo neobsahuje o jedna menší počet červných linek než uzlů, vrať false

        //kontrola, zdali kostra grafu prochází přes již existující čáry
        ArrayList<Edge> redLineList = userGraph.getRedEdgesList(); //uživatelova kostra
        ArrayList<Edge> edges = userGraph.getEdges(); //uživatelovy čáry
        for (Edge redLine : redLineList) {
            if (edges.stream().noneMatch(m -> m.isEdgeSame(redLine))) return "cesta";
        }

       if (userGraph.getRedEdgesList().size() != userGraph.getNodes().size() - 1) {
            return "false";
        } else {
            Map mapForChecker = new Map(userGraph);
            mapForChecker.setEdges(mapForChecker.getRedEdgesList());
            mapForChecker.setRedNodes(new ArrayList<>());
            if (checkIfGraphHasCertainAmountOfComponent(mapForChecker, 1)) {
                return "true";
            }
        }
        return "false";
    }

    //myšlenka - pokud vezmu ten prvni graf a připočítám k němu stejnej rozdíl, dostanu stejnej graf
    //na něm porovnám, zdali má červené čáry (doplněk) stejný souřadnice jako když by to generoval algoritmus
    //podmínkou je lock na posunování uzlů
    public static boolean checkIfGraphIsComplementGraph(Map mapCreatedByUser, Map mapToCheck) {
        ArrayList<Edge> redEdges = mapCreatedByUser.getRedEdgesList();
        ArrayList<Edge> redEdgesToCheck = mapToCheck.getRedEdgesList();

        if (redEdgesToCheck.size() == 0 && redEdges.size() > 0) return false;

        for (Edge edge : redEdgesToCheck) {
            boolean found = false;
            for (Edge redLine : redEdges) {
                if (redLine.isEdgeSame(edge)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
