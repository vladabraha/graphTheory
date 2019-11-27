package cz.uhk.graphstheory.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphChecker {

    public static boolean checkIfGraphContainsPath(Map map) {
        if (map != null) {

            //mrknu, že každý bod je v seznamu maximálně 2x
            ArrayList<CustomLine> path = map.getRedLineList();
            ArrayList<CustomLine> customLines = map.getCustomLines();
            if (path.size() < 1) return false;
            ArrayList<Coordinate> coordinateArrayList = new ArrayList<>();

            for (int i = 0; i < path.size(); i++) {
                //nejdřív kontrola, že červená čára je i ve hranách
                int finalI = i;
                if (customLines.stream().noneMatch(n -> n.isLineSame(path.get(finalI)))) {
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
        ArrayList<CustomLine> redLineList = map.getRedLineList();
        if (redLineList.size() == 0) return false;
        ArrayList<CustomLine> alreadyChecked = new ArrayList<>();

        for (CustomLine customLine : redLineList) {
            for (CustomLine customLineAlreadyChecked : alreadyChecked) {
                if (customLine.isLineSame(customLineAlreadyChecked)) {
                    return false;
                }
            }
            alreadyChecked.add(customLine);
        }
        return true;
    }

    public static boolean checkIfGraphContainsCycle(Map map) {
        ArrayList<CustomLine> redLineList = map.getRedLineList();
        if (redLineList.size() < 2) return false;
        CustomLine startingLine = redLineList.get(0);
        CustomLine lastLine = redLineList.get(redLineList.size() - 1);
        if (startingLine.getFrom().equal(lastLine.getTo())) {
            return true;
        } else if (startingLine.getFrom().equal(lastLine.getFrom())) {
            return true;
        } else if (startingLine.getTo().equal(lastLine.getTo())) {
            return true;
        } else return startingLine.getTo().equal(lastLine.getFrom());
    }

    public static boolean checkIfGraphIsBipartite(Map map) {
        ArrayList<CustomLine> customLines = map.getCustomLines();
        ArrayList<Coordinate> circles = map.getCircles();
        ArrayList<Coordinate> circlesConnectedTogether = new ArrayList<>();
        ArrayList<Coordinate> circlesInFirstPartOfBipartite = new ArrayList<>();
        ArrayList<Coordinate> circlesInSecondPartOfBipartite = new ArrayList<>();

        //myšlenka, hledám všechny uzly, se kterými je jeden uzel propojen a ty se kterými není si dám s ním do jedné skupiny
        //protoze u bipartitniho grafu plati, ze je jeden uzel spojen se vsemi prvky druhe skupiny, staci nam pro urceni skupiny jeden uzel
        // pro nej zjistime se kterymi uzly neni propojen a ty by meli byt propojeny se vsemi uzly druhe skupiny

        if (circles.size() < 2 || customLines.size() < 3) return false;
        Coordinate coordinate = circles.get(0);

        for (CustomLine customLine : customLines) {
            if (customLine.getFrom().equal(coordinate)) {
                circlesConnectedTogether.add(customLine.getTo());
            } else if (customLine.getTo().equal(coordinate)) {
                circlesConnectedTogether.add(customLine.getFrom());
            }
        }

        //ted projdu vsechny uzly a podivam se, se kterymi neni propojen a podle toho je rozradim do skupin
        for (Coordinate circle : circles) {
            boolean istheSecondPart = false;
            for (Coordinate coordinateTogether : circlesConnectedTogether) {
                if (coordinateTogether.equal(circle)) {
                    istheSecondPart = true;
                }
            }
            if (istheSecondPart) {
                circlesInSecondPartOfBipartite.add(circle);
            } else {
                circlesInFirstPartOfBipartite.add(circle);
            }
        }

        //a ted kontrola, zdali jsou uzly propojeny mezi sebou
        //projdeme vsechny cary a mrkneme, zdali ukazuji z jednoho bodu na vsechny uzly z druhe skupiny
        //pri kazdem nalezeni, odstranime bod ze seznamu druhe casti bipartitniho grafu a na konci by měl být prázdný
        for (Coordinate coordinateFirstPart : circlesInFirstPartOfBipartite) {
            ArrayList<Coordinate> circlesInSecondPartOfBipartiteCloned = new ArrayList<>();
            for (Coordinate coor : circlesInSecondPartOfBipartite){
                circlesInSecondPartOfBipartiteCloned.add(new Coordinate(coor.x, coor.y));
            }

            Iterator<Coordinate> iter = circlesInSecondPartOfBipartiteCloned.iterator();

            while (iter.hasNext()) {
                Coordinate coordinateSecondPart = iter.next();
                for (CustomLine customLine : customLines) {
                    if (customLine.getTo().equal(coordinateFirstPart) && customLine.getFrom().equal(coordinateSecondPart)) {
                        iter.remove();
                    } else if (customLine.getTo().equal(coordinateSecondPart) && customLine.getFrom().equal(coordinateFirstPart)) {
                        iter.remove();
                    }
                }
            }
            if (!circlesInSecondPartOfBipartiteCloned.isEmpty()) return false;
        }

        //a ještě kontrola, že uzly v jedné skupině bipartitního grafu nejsou spolu propojeny
        for (Coordinate coordinateFirstPart : circlesInFirstPartOfBipartite){
            for (Coordinate coordinateFirstPart2 : circlesInFirstPartOfBipartite){
                if (customLines.stream().anyMatch(customLine -> (
                    customLine.isPointInStartOrEndOfLine(coordinateFirstPart) && customLine.isPointInStartOrEndOfLine(coordinateFirstPart2) && !coordinateFirstPart.equal(coordinateFirstPart2)
                ))){
                    return false;
                }
            }
        }
        for (Coordinate coordinateSecondPart : circlesInSecondPartOfBipartite){
            for (Coordinate coordinateSecondPart2 : circlesInSecondPartOfBipartite){
                if (customLines.stream().anyMatch(customLine -> (
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
        ArrayList<CustomLine> customLines = map.getCustomLines();
        ArrayList<Coordinate> circles = map.getCircles();
        ArrayList<Coordinate> redCircles = map.getRedCircles();

        if (customLines.size() < 2 || circles.size() < 2) return "false";
        if (redCircles.size() != 1) return "chyba v poctu cervenych bodu";

        //aritkulace bude ohranicena 2 cervenymi carami
        Coordinate articulation = redCircles.get(0);

        //hledani prvniho bodu mimo artikulaci
        Coordinate firstCoordinate = null;
        for (CustomLine customLine : customLines) {
            if (customLine.isPointInStartOrEndOfLine(articulation)) {
                if (customLine.getFrom().equal(articulation)) {
                    firstCoordinate = customLine.getTo();
                } else {
                    firstCoordinate = customLine.getFrom();
                }
            }
        }

        //myslenka - mám artikulaci - vezmu jeden bod z cesty z artikulace
        //vezmu prvni caru z vrcholu a nalezeny bod hodim do seznamu a odmazu cestu ze zkopirovaneho zasobniku vsech cest
        //takhle budu pokracovat (budu si ještě kontrolovat, že nejdu zase přes artikulaci na druhou stranu
        // dokud nedojdu na konec - pokud uz dal nemuzu, vyhodim uzel ze seznamu a vezmu predposledni a zkusim pro nej najit další cesty, dokud nevyprazdnim seznma
        //na konci bych měl mít seznam všech prozkoumaných vrcholů (ty si musím evidovat ve speciálním seznamu) menší o 2 než seznam všech vrcholů, aby to byla artikulace

        ArrayList<CustomLine> customLinesCopy = new ArrayList<>();
        for (CustomLine customLine : customLines) {
            customLinesCopy.add(new CustomLine(customLine));
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
                CustomLine customLine = customLinesCopy.get(i);
                if (customLine.isPointInStartOrEndOfLine(coordinateOnStack)) {
                    if (customLine.getFrom().equal(coordinateOnStack) && !customLine.getTo().equal(articulation)) {
                        Coordinate foundCoordinate = customLine.getTo();
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
                    } else if (customLine.getTo().equal(coordinateOnStack) && !customLine.getFrom().equal(articulation)) {
                        Coordinate foundCoordinate = customLine.getFrom();
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

        if (visitedNodes.size() > circles.size() - 1) {
            return "false";
        } else {
            return "true";
        }

    }

    //MYŠLENKA - od uzivatele přijde červenou čarou označený most - vezmu jeden jeho konec a podívám se na všechny sousedy toho konce a ty si uložím zvlášť do seznamu a to samé udělám pro druhý konec
    //Jakmile mám seznam sousedů jednoho konce mostu, podívám se na všechny sousedy těchto uzlů, dokud nedojdu na konec seznamu - pokud takhle najdu n-2 uzlů, tak se nejedná o most
    //tento postup budu opakovat u druhého konce mostu a jeho seznamu sousedů
    public static String checkIfGraphContainsBridge(Map userGraph) {
        ArrayList<CustomLine> customLines = userGraph.getCustomLines();
        ArrayList<CustomLine> redLines = userGraph.getRedLineList();
        ArrayList<Coordinate> circles = userGraph.getCircles();
        if (redLines.isEmpty()) return "chybi ohraniceni cervenou carou";
        if (circles.size() < 3 || redLines.size() > 1) return "false";

        //most bude označen cervenou carou
        //hledani prvniho bodu mimo most

        Coordinate oneEndOfBridge = redLines.get(0).getFrom();
        Coordinate secondEndOfBridge = redLines.get(0).getTo();
        Coordinate borderWhereBridgeBeggins = null; //tohle je kvuli algoritmu, ktery je stejny jako u aritkulace a potřebuje hraniční bod, přes který by neměl přejít
        for (CustomLine customLine : customLines) {
            if (customLine.getFrom().equal(oneEndOfBridge) && !customLine.getTo().equal(secondEndOfBridge)) {
                borderWhereBridgeBeggins = customLine.getFrom();
            } else if (customLine.getTo().equal(oneEndOfBridge) && !customLine.getFrom().equal(secondEndOfBridge)) {
                borderWhereBridgeBeggins = customLine.getTo();
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
        for (CustomLine customLine : customLines) {
            if (customLine.getFrom().equal(oneEndOfBridge) && !customLine.getTo().equal(secondEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getTo()))) {
                    nodesToExploreFirstEndOfBridge.add(customLine.getTo());
                    alreadyVisitedNodesFirstEndOfBridge.add(customLine.getTo());
                }
            } else if (customLine.getTo().equal(oneEndOfBridge) && !customLine.getFrom().equal(secondEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getFrom()))) {
                    nodesToExploreFirstEndOfBridge.add(customLine.getFrom());
                    alreadyVisitedNodesFirstEndOfBridge.add(customLine.getFrom());
                }
            }else if (customLine.getTo().equal(secondEndOfBridge) && !customLine.getFrom().equal(oneEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getFrom()))) {
                    alreadyVisitedNodesSecondEndOfBridge.add(customLine.getFrom());
                    nodesToExploreSecondEndOfBridge.add(customLine.getFrom());
                }
            }else if (customLine.getFrom().equal(secondEndOfBridge) && !customLine.getTo().equal(oneEndOfBridge)) {
                if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getTo()))) {
                    alreadyVisitedNodesSecondEndOfBridge.add(customLine.getTo());
                    nodesToExploreSecondEndOfBridge.add(customLine.getTo());
                }
            }
        }

        //teď projdu všechny sousedy nalezených uzlů pro jeden konec grafu
        for (int i = 0; i < nodesToExploreFirstEndOfBridge.size(); i++) {
            Coordinate coordinateToExplore = nodesToExploreFirstEndOfBridge.get(i);
            for (CustomLine customLine : customLines) {
                //nejdriv kontrola, ze se nepresuneme pres artikulaci do druhe půlky
                if (!customLine.isPointInStartOrEndOfLine(borderWhereBridgeBeggins) && !customLine.isPointInStartOrEndOfLine(oneEndOfBridge) && !customLine.isPointInStartOrEndOfLine(secondEndOfBridge)) {
                    if (customLine.getFrom().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getTo()))) {
                            alreadyVisitedNodesFirstEndOfBridge.add(customLine.getTo());
                            nodesToExploreFirstEndOfBridge.add(customLine.getTo());
                        }
                    } else if (customLine.getTo().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesFirstEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getFrom()))) {
                            alreadyVisitedNodesFirstEndOfBridge.add(customLine.getFrom());
                            nodesToExploreFirstEndOfBridge.add(customLine.getFrom());
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
            for (CustomLine customLine : customLines) {
                //nejdriv kontrola, ze se nepresuneme pres artikulaci do druhe půlky
                if (!customLine.isPointInStartOrEndOfLine(borderWhereBridgeBeggins) && !customLine.isPointInStartOrEndOfLine(oneEndOfBridge) && !customLine.isPointInStartOrEndOfLine(secondEndOfBridge)) {
                    if (customLine.getFrom().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesSecondEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getTo()))) {
                            alreadyVisitedNodesSecondEndOfBridge.add(customLine.getTo());
                            nodesToExploreSecondEndOfBridge.add(customLine.getTo());
                        }
                    } else if (customLine.getTo().equal(coordinateToExplore)) {
                        if (alreadyVisitedNodesSecondEndOfBridge.stream().noneMatch(m -> m.equal(customLine.getFrom()))) {
                            alreadyVisitedNodesSecondEndOfBridge.add(customLine.getFrom());
                            nodesToExploreSecondEndOfBridge.add(customLine.getFrom());
                        }
                    }
                }
            }
            nodesToExploreSecondEndOfBridge.remove(i);
            i--;
        }

        //pokud ani jeden seznam nemá n-2 uzlů, nejednalo se o most
        if (alreadyVisitedNodesFirstEndOfBridge.size() == circles.size() - 2 || alreadyVisitedNodesSecondEndOfBridge.size() == circles.size() - 2) {
            return "false";
        } else {
            return "true";
        }
    }

    //myšlenka - projdu postupně všechny redlines a budu si paamatovat kde jsem byl
    //na konci by měl bejt seznam 2x tak velkej než circles (každej bod by tam měl bejt max.2 a zároveň by tam neměl chybět
    //a taky se musi jeste vratit kruznice do prvniho bodu kde zacala
    public static String checkIfGraphContainsHamiltonCircle(Map userGraph) {
        ArrayList<CustomLine> redLines = userGraph.getRedLineList();
        ArrayList<CustomLine> lines = userGraph.getCustomLines();
        ArrayList<Coordinate> circles = userGraph.getCircles();

        if (redLines.isEmpty()) return "chybi ohraniceni cervenou carou";

        ArrayList<Coordinate> alreadyVisitedNodes = new ArrayList<>();

        for (CustomLine redLine : redLines) {
            alreadyVisitedNodes.add(redLine.getFrom());
            alreadyVisitedNodes.add(redLine.getTo());
        }
        for (Coordinate circle : circles) {
            if (alreadyVisitedNodes.stream().noneMatch(node -> node.equal(circle))) {
                return "false";
            }
        }
        if (alreadyVisitedNodes.size() != (circles.size() * 2)) return "false";


        //kontrola, zdali cara neprochazi mistem, kde nebyla puvodne hrana
        for (CustomLine redLine : redLines) {
            if (lines.stream().noneMatch(line -> line.isLineSame(redLine))) {
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

        CustomLine firstRedLine = redLines.get(0);
        CustomLine lastRedLine = redLines.get(redLines.size() - 1);
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
        ArrayList<CustomLine> redLines = map.getRedLineList();
        ArrayList<CustomLine> lines = map.getCustomLines();
        ArrayList<Coordinate> circles = map.getCircles();

        //spocitame pocet vrcholu se lichým pocetem sousedu (ty můžou být max. 2, nebo musí být všechny vrcholy sudé)
        ArrayList<Coordinate> nodesWithMoreThanTwoEvenNeighbours = new ArrayList<>();
        for (Coordinate coordinate : circles) {
            int numberOfLinesConnectedToNode = (int) lines.stream().filter(m -> m.isPointInStartOrEndOfLine(coordinate)).count();
            if (numberOfLinesConnectedToNode % 2 == 1) {
                nodesWithMoreThanTwoEvenNeighbours.add(coordinate);
            }
        }

        if (nodesWithMoreThanTwoEvenNeighbours.size() > 2 || nodesWithMoreThanTwoEvenNeighbours.isEmpty())
            return "false";

        if (redLines.size() != lines.size()) return "false";

        //kontrola, ze redlines maj vsechny normalni cary a zadnou novou navic
        for (CustomLine line : lines) {
            boolean found = false;
            for (CustomLine redline : redLines) {
                if (redline.isLineSame(line)) found = true;
            }
            if (!found) return "false";
        }

        for (CustomLine redline : redLines) {
            boolean found = false;
            for (CustomLine line : lines) {
                if (line.isLineSame(redline)) found = true;
            }
            if (!found) return "false";
        }

        //kontrola, ze redlines na sebe navazuji
        //tzn, ze kazdy 2 usecky musi mit spolecny prave jeden bod
        for (int i = 0; i < redLines.size(); i++) {
            if (i > 1) {

                CustomLine firstLine = redLines.get(i - 1);
                Coordinate first = firstLine.getFrom();
                Coordinate second = firstLine.getTo();

                CustomLine secondLine = redLines.get(i);
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
        ArrayList<CustomLine> lines = map.getCustomLines();
        ArrayList<Coordinate> circles = map.getCircles();

        if (degreeList.size() != circles.size()) return false;

        //spocitam stupne vrcholu jednotlivych uzlu a vlozim si je do hashmapy
        HashMap<Coordinate, Integer> degreesMap = new HashMap<>();
        for (Coordinate node : circles) {
            int degree = 0;
            for (CustomLine customLine : lines) {
                if (customLine.isPointInStartOrEndOfLine(node)) degree++;
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
        return userGraph.getRedLineList().size() == length;
    }

    //myšlenka - budu prochazet vsechny usecky, ktery maj danej bod v from nebo to a druhej konec hodim do seznamu
    //na konci prohledani odstranim bod ze zasobniku a pokracuju dalsim bodem, dokud neprojdu takto vsechny vrcholy na ktere dosahnu
    //pokud mi zbydou jeste nějaké vrcholy k prozkoumání, mám druhou komponentu, vyberu tedy libovolny zbyvajici bod a provedu na nem opět prohledávání
    //toto opakuji tolikrat, kolik ma byt kompo
    public static boolean checkIfGraphHasCertainAmountOfComponent(Map userGraph, int amountOfComponent) {
        if (!checkIfGraphDoesNotContainsCycle(userGraph)) return false;
        ArrayList<CustomLine> customLines = userGraph.getCustomLines();
        ArrayList<Coordinate> nodes = userGraph.getCircles();


        ArrayList<CustomLine> customLinesCopied = new ArrayList<>();
        ArrayList<Coordinate> nodesCopied = new ArrayList<>();

        for (Coordinate coordinate : nodes) {
            nodesCopied.add(new Coordinate(coordinate.x, coordinate.y));
        }

        for (CustomLine customLine : customLines) {
            customLinesCopied.add(new CustomLine(customLine));
        }

        ArrayList<Coordinate> coordinatesToExplore = new ArrayList<>();
        coordinatesToExplore.add(nodesCopied.get(0));

        int foundComponents = 0;
        boolean found = false;
        do {
            for (int i = 0; i < coordinatesToExplore.size(); i++) {
                Coordinate coordinateToExplore = coordinatesToExplore.get(i);
                for (int j = 0; j < customLinesCopied.size(); j++) {
                    CustomLine customLine = customLinesCopied.get(j);
                    if (customLine.isPointInStartOrEndOfLine(coordinateToExplore)) {
                        if (customLine.getFrom().equal(coordinateToExplore)) {
                            coordinatesToExplore.add(customLine.getTo());
                            for (Coordinate node : nodesCopied) {
                                if (node.equal(customLine.getTo())) {
                                    nodesCopied.remove(node);
                                    break;
                                }
                            }
                        } else {
                            coordinatesToExplore.add(customLine.getFrom());
                            for (Coordinate node : nodesCopied) {
                                if (node.equal(customLine.getFrom())) {
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
        ArrayList<CustomLine> customLines = userGraph.getCustomLines();

        ArrayList<Coordinate> nodesWithMultipleLinesFirst = new ArrayList<>();
        ArrayList<Coordinate> nodesWithMultipleLinesSecond = new ArrayList<>();
        ArrayList<Coordinate> nodesToCheck = new ArrayList<>();

        //v teto casti najdeme uzly s více než 2 hranami
        for (CustomLine customLine : customLines) {
            if (nodesWithMultipleLinesFirst.stream().noneMatch(m -> m.equal(customLine.getTo()))) {
                nodesWithMultipleLinesFirst.add(customLine.getTo());
            } else if (nodesWithMultipleLinesSecond.stream().noneMatch(m -> m.equal(customLine.getTo()))) {
                nodesWithMultipleLinesSecond.add(customLine.getTo());
            } else {
                nodesToCheck.add(customLine.getTo());
            }
            if (nodesWithMultipleLinesFirst.stream().noneMatch(m -> m.equal(customLine.getFrom()))) {
                nodesWithMultipleLinesFirst.add(customLine.getFrom());
            } else if (nodesWithMultipleLinesSecond.stream().noneMatch(m -> m.equal(customLine.getFrom()))) {
                nodesWithMultipleLinesSecond.add(customLine.getFrom());
            } else {
                nodesToCheck.add(customLine.getFrom());
            }
        }

        //nalezeni prvniho bodu, kterej se spojuje (ten může mít jenom 2 hrany a presto tvořit kružnici
        Coordinate firstNode;
        Coordinate firstNodeCandidate = customLines.get(0).getFrom();
        Coordinate secondNodeCandidate = customLines.get(0).getTo();
        if (customLines.get(1).getFrom().equal(firstNodeCandidate) || customLines.get(1).getTo().equal(firstNodeCandidate)) {
            firstNode = secondNodeCandidate;
        } else {
            firstNode = firstNodeCandidate;
        }

        //projed vsechny vrcholy, ktery maj vic jak 2 cary k sobe
        do {
            //tady akorat kopie
            ArrayList<CustomLine> customLinesCopied = new ArrayList<>();
            for (CustomLine customLine : customLines) {
                customLinesCopied.add(new CustomLine(customLine.getFrom(), customLine.getTo()));
            }
            ArrayList<Coordinate> nodesCopied = new ArrayList<>();
            for (Coordinate coordinate : nodesCopied) {
                nodesCopied.add(new Coordinate(coordinate.x, coordinate.y));
            }

            //pokud nemá žádnej vrchol více jak 2 hrany, tak to proji celý, jeslti tam nic nenajdeš od začátku
            if (nodesToCheck.isEmpty()) {
                ArrayList<Coordinate> nodesOnStack = new ArrayList<>();
                for (CustomLine customLine : customLinesCopied) {
                    if (customLine.getFrom().equal(firstNode)) {
                        nodesOnStack.add(customLine.getTo());
                        customLinesCopied.remove(customLine);
                        break;
                    } else if (customLine.getTo().equal(firstNode)) {
                        nodesOnStack.add(customLine.getFrom());
                        customLinesCopied.remove(customLine);
                        break;
                    }
                }

                do {
                    Coordinate coordinate = nodesOnStack.get(0);
                    Iterator<CustomLine> iterator = customLinesCopied.iterator();
                    while (iterator.hasNext()) {
                        CustomLine customLine = iterator.next();
                        if (customLine.getFrom().equal(coordinate)) {
                            if (customLine.getTo().equal(firstNode)) return false;
                            nodesOnStack.add(customLine.getTo());
                            iterator.remove();
                        } else if (customLine.getTo().equal(coordinate)) {
                            if (customLine.getFrom().equal(firstNode)) return false;
                            nodesOnStack.add(customLine.getFrom());
                            iterator.remove();
                        }
                    }
                    nodesOnStack.remove(0);
                } while (!nodesOnStack.isEmpty());

                return true; //pokud nema zadnej uzel vic jak 2 hrany k jinymu uzlu, je to v pořádku
            }
            Coordinate coordinate = nodesToCheck.get(0); //vezmu prvni bod k prozkoumani
            ArrayList<Coordinate> nodesOnStack = new ArrayList<>();
            ArrayList<CustomLine> customLinesAlreadyUsed = new ArrayList<>(); //ukladam si, ktery hrany jsem uz zkousel, abych se netočil v dalších nodech od toho nodu, ktery ma vic jak 2 hrany
            boolean stop = false;
            //hledam dalsi bod, kterej je po mem bodu, co ma vic jak 2 hrany
            do {
                for (CustomLine customLine : customLinesCopied) {
                    if (customLinesAlreadyUsed.stream().noneMatch(m -> m.isLineSame(customLine))) {
                        if (customLine.getFrom().equal(coordinate)) {
                            nodesOnStack.add(customLine.getTo());
                            customLinesAlreadyUsed.add(customLine);
                            break;
                        } else if (customLine.getTo().equal(coordinate)) {
                            nodesOnStack.add(customLine.getFrom());
                            customLinesAlreadyUsed.add(customLine);
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
                        Iterator<CustomLine> iterator = customLinesCopied.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                            CustomLine customLine = iterator.next();
                            //pokud najdu usecku, tkera muj bod spojuje s nejakym dalsim, hodim si ten dalsi bod do seznamu a odstranim usecku ze seznamu
                            if (customLinesAlreadyUsed.stream().noneMatch(m -> m.isLineSame(customLine))) { //tady ještě kontrola, že neprocházím tu úsečku,k která mě už dostala k tomuto bodu
                                if (customLine.getFrom().equal(coordinateToExplore)) {
                                    if (customLine.getTo().equal(coordinate))
                                        return false; //pokud jsem se dostal k bodu, co ma vic jak ty 2 hrany, tak jsem objevil kruznici a vracim false
                                    nodesOnStack.add(customLine.getTo());
                                    iterator.remove();
                                    found = true;
                                } else if (customLine.getTo().equal(coordinateToExplore)) {

                                    if (customLine.getFrom().equal(coordinate))
                                        return false; //pokud jsem se dostal k bodu, co ma vic jak ty 2 hrany, tak jsem objevil kruznici a vracim false
                                    nodesOnStack.add(customLine.getFrom());
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
    //akorát zkontrolujeme, že se jedná o stejně početný graf na nody, jaký byl vygenerován a přehodíme pro checker redlines na běžné customlines (edges)
    public static String checkIfGraphIsSpanningTree(Map userGraph, Map generatedMap) {

        //pokud je jiný počet nodů, než byl vygenerovaný, nebo neobsahuje o jedna menší počet červných linek než uzlů, vrať false

        //kontrola, zdali kostra grafu prochází přes již existující čáry
        ArrayList<CustomLine> redLineList = userGraph.getRedLineList(); //uživatelova kostra
        ArrayList<CustomLine> customLines = userGraph.getCustomLines(); //uživatelovy čáry
        for (CustomLine redLine : redLineList) {
            if (customLines.stream().noneMatch(m -> m.isLineSame(redLine))) return "cesta";
        }

       if (userGraph.getRedLineList().size() != userGraph.getCircles().size() - 1) {
            return "false";
        } else {
            Map mapForChecker = new Map(userGraph);
            mapForChecker.setCustomLines(mapForChecker.getRedLineList());
            mapForChecker.setRedCircles(new ArrayList<>());
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
        ArrayList<CustomLine> redLines = mapCreatedByUser.getRedLineList();
        ArrayList<CustomLine> redLinesToCheck = mapToCheck.getRedLineList();

        if (redLinesToCheck.size() == 0 && redLines.size() > 0) return false;

        for (CustomLine customLine : redLinesToCheck) {
            boolean found = false;
            for (CustomLine redLine : redLines) {
                if (redLine.isLineSame(customLine)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
