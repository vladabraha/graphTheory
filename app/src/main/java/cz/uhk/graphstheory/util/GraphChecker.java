package cz.uhk.graphstheory.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import cz.uhk.graphstheory.model.Coordinate;
import cz.uhk.graphstheory.model.CustomLine;
import cz.uhk.graphstheory.model.Map;

public class GraphChecker {

    public static boolean checkIfGraphContainsCesta(Map map) {
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
            for (Coordinate coordinate : coordinateArrayList){
                int numberOfOccurance = 0;
                for (Coordinate coordinate2 : coordinateArrayList){
                    if (coordinate.equal(coordinate2)) numberOfOccurance++;
                }
                if (numberOfOccurance > 2) return false;
            }
        }else {
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
        } else return startingLine.getFrom().equal(lastLine.getFrom());
    }

    //todo rozpozna, jestli jsou 2 grafy od sebe rozdeleny, pripadne smazat
//    public static boolean checkIfGraphIsBipartite(Map map){
//        ArrayList<CustomLine> customLines = map.getCustomLines();
//        ArrayList<CustomLine> customLinesCloned = (ArrayList<CustomLine>) map.getCustomLines().clone();
//        ArrayList<Coordinate> alreadyFound = new ArrayList<>();
//        ArrayList<Coordinate> circles = map.getCircles();
//
//        if (customLines.size() < 2) return false;
//
//        //vezmi prvni primku, hod si z ni body do seznamu
//        //projdi vsechny primky a koukni se, jestli maji jeden bod koncici v nekterem z techto nodu
//        //pokud ano, odstran tutu primku z dalsiho hledani a nove propojujici bod pridej do seznamu (pouze pokud tam jeste neni)
//        //na konci porovnej, zdali je seznam s body stejne velky jako puvodni, pokud ano, neni tam zadny bipartnitni, jinak je
//        alreadyFound.add(customLines.get(0).getFrom());
//        alreadyFound.add(customLines.get(0).getTo());
//        customLinesCloned.remove(0);
//
//        boolean shouldRun;
//        boolean shouldBreak; //this is for currentModificationException
//        do {
//            shouldBreak = false;
//            for (CustomLine customLine : customLinesCloned){
//                for (Coordinate coordinate : alreadyFound){
//                    if (customLine.getFrom().equal(coordinate)){ //pokud je bod uz v nasem prvotnim seznamu
//                           //kontrola, ze bod jeste nemame v seznamu
//                        boolean isItNewNode = true;
//                        for (Coordinate coordinateAlreadyAdded : alreadyFound){
//                            if (coordinateAlreadyAdded.equal(customLine.getTo())){
//                                isItNewNode = false;
//                            }
//                        }
//                        customLinesCloned.remove(customLine); //smazani, abychom to nehledali do nekonecna, hledame od prazdneho seznamu
//                        shouldBreak = true;
//                        if (isItNewNode){ //pokud nemame, tak ho pridej do seznamu
//                            alreadyFound.add(customLine.getTo());
//                            break;
//                        }
//                    }else if(customLine.getTo().equal(coordinate)){
//                        boolean isItNewNode = true;
//                        for (Coordinate coordinateAlreadyAdded : alreadyFound){
//                            if (coordinateAlreadyAdded.equal(customLine.getFrom())){
//                                isItNewNode = false;
//                            }
//                        }
//                        if (isItNewNode){
//                            alreadyFound.add(customLine.getFrom());
//                            break;
//                        }
//                        customLinesCloned.remove(customLine); //smazani, abychom to nehledali do nekonecna, hledame od prazdneho seznamu
//                        shouldBreak = true;
//                    }
//                }
//                if (shouldBreak) break;
//            }
//            //check if list of lines contains any point heading to our list of nodes
//            //if not, algorithm stops
//            shouldRun = false;
//            for (CustomLine customLine : customLinesCloned){
//                for (Coordinate coordinate : alreadyFound){
//                    if (customLine.getTo().equal(coordinate) || customLine.getFrom().equal(coordinate)){
//                        shouldRun = true;
//                        break;
//                    }
//                }
//                if (shouldRun) break;
//            }
//        }while (shouldRun);
//
//        return alreadyFound.size() != circles.size();
//    }

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
            //todo vytvorit novej objekt
            ArrayList<Coordinate> circlesInSecondPartOfBipartiteCloned = (ArrayList<Coordinate>) circlesInSecondPartOfBipartite.clone();
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
        return true;
    }

    /**
     * bod aritkulace musí být ohraničen 2 přímkami z redline listu
     *
     * @param map uzivatelova mapa
     * @return -
     */
    public static String checkIfGraphContainsArticulation(Map map) {
        ArrayList<CustomLine> customLines = map.getCustomLines();
        ArrayList<CustomLine> redLines = map.getRedLineList();
        ArrayList<Coordinate> circles = map.getCircles();

        if (customLines.size() < 2 || circles.size() < 2 || redLines.size() < 2) return "false";

        //aritkulace bude ohranicena 2 cervenymi carami
        Coordinate articulation;
        if (redLines.get(0).getFrom().equal(redLines.get(1).getFrom()) || redLines.get(0).getFrom().equal(redLines.get(1).getTo())) {
            articulation = redLines.get(0).getFrom();
        } else if (redLines.get(0).getTo().equal(redLines.get(1).getFrom()) || redLines.get(0).getTo().equal(redLines.get(1).getTo())) {
            articulation = redLines.get(0).getTo();
        } else {
            return "chybi ohraniceni cervenou carou";
        }

        //hledani prvniho bodu mimo artikulaci
        Coordinate firstCoordinate = null;
        if (redLines.get(0).getFrom().equal(articulation)) {
            firstCoordinate = redLines.get(0).getTo();
        } else if (redLines.get(0).getTo().equal(articulation)) {
            firstCoordinate = redLines.get(0).getFrom();
        }

        //myslenka - projdu vsechny sousedy od prvniho bodu a budu si pamatovat, ktery jsem prosel
        //v dalsim kole budu prochazet sousedy sousedů, ktere jsem jeste nenavstivil, takhle postupne projdu vsechny z teho kategorie
        //na konci by mi meli chybet v seznamu nejake uzly - ty z druhe strany, kterou artikulace spojovala
        ArrayList<Coordinate> alreadyVisitedNodes = new ArrayList<>();
        ArrayList<Coordinate> nodesToExplore = new ArrayList<>();

        //pro prvni uzel najdeme vsechny nody se kterymi je spojen a jeste jsme v nich nebyly
        for (CustomLine customLine : customLines) {
            if (customLine.getFrom().equal(Objects.requireNonNull(firstCoordinate))) {
                boolean isVisited = false;
                for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                    if (alreadyVisitedCoordinate.equal(customLine.getTo())) {
                        isVisited = true;
                    }
                }
                if (!isVisited) {
                    alreadyVisitedNodes.add(customLine.getTo());
                    nodesToExplore.add(customLine.getTo());
                }
            } else if (customLine.getTo().equal(Objects.requireNonNull(firstCoordinate))) {
                boolean isVisited = false;
                for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                    if (alreadyVisitedCoordinate.equal(customLine.getFrom())) {
                        isVisited = true;
                    }
                }
                if (!isVisited) {
                    alreadyVisitedNodes.add(customLine.getFrom());
                    nodesToExplore.add(customLine.getFrom());
                }
            }
        }

        for (int i = 0; i < nodesToExplore.size(); i++) {
            Coordinate coordinateToExplore = nodesToExplore.get(i);
            for (CustomLine customLine : customLines) {
                //nejdriv kontrola, ze se nepresuneme pres artikulaci do druhe půlky
                if (!customLine.getFrom().equal(articulation) && !customLine.getTo().equal(articulation)) {
                    if (customLine.getFrom().equal(coordinateToExplore)) {
                        boolean isVisited = false;
                        for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                            if (alreadyVisitedCoordinate.equal(customLine.getTo())) {
                                isVisited = true;
                            }
                        }
                        if (!isVisited) {
                            alreadyVisitedNodes.add(customLine.getTo());
                            nodesToExplore.add(customLine.getTo());
                        }
                    } else if (customLine.getTo().equal(coordinateToExplore)) {
                        boolean isVisited = false;
                        for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                            if (alreadyVisitedCoordinate.equal(customLine.getFrom())) {
                                isVisited = true;
                            }
                        }
                        if (!isVisited) {
                            alreadyVisitedNodes.add(customLine.getFrom());
                            nodesToExplore.add(customLine.getFrom());
                        }
                    }
                }
            }
            nodesToExplore.remove(i);
            i--;
        }

        if (alreadyVisitedNodes.size() == circles.size()) {
            return "false";
        } else {
            return "true";
        }

    }

    public static String checkIfGraphContainsBridge(Map userGraph) {
        ArrayList<CustomLine> customLines = userGraph.getCustomLines();
        ArrayList<CustomLine> redLines = userGraph.getRedLineList();
        ArrayList<Coordinate> circles = userGraph.getCircles();
        if (redLines.isEmpty() || circles.size() < 3 || redLines.size() > 1) return "false";

        //most bude označen cervenou carou
        //hledani prvniho bodu mimo most

        Coordinate oneEndOfBridge = redLines.get(0).getFrom();
        Coordinate secondEndOfBridge = redLines.get(0).getTo();
        Coordinate firstCoordinate = null;
        Coordinate borderWhereBridgeBeggins = null; //tohle je kvuli algoritmu, ktery je stejny jako u aritkulace a potřebuje hraniční bod, přes který by neměl přejít
        for (CustomLine customLine : customLines) {
            if (customLine.getFrom().equal(oneEndOfBridge) && !customLine.getTo().equal(secondEndOfBridge)) {
                firstCoordinate = customLine.getTo();
                borderWhereBridgeBeggins = customLine.getFrom();
            } else if (customLine.getTo().equal(oneEndOfBridge) && !customLine.getFrom().equal(secondEndOfBridge)) {
                firstCoordinate = customLine.getFrom();
                borderWhereBridgeBeggins = customLine.getTo();
            }
        }
        if (firstCoordinate == null) return "chybi ohraniceni cervenou carou";


        //myslenka - projdu vsechny sousedy od prvniho bodu a budu si pamatovat, ktery jsem prosel
        //v dalsim kole budu prochazet sousedy sousedů, ktere jsem jeste nenavstivil, takhle postupne projdu vsechny z teho kategorie
        //na konci by mi meli chybet v seznamu nejake uzly - ty z druhe strany, kterou artikulace spojovala
        ArrayList<Coordinate> alreadyVisitedNodes = new ArrayList<>();
        ArrayList<Coordinate> nodesToExplore = new ArrayList<>();

        //pro prvni uzel najdeme vsechny nody se kterymi je spojen a jeste jsme v nich nebyly
        for (CustomLine customLine : customLines) {
            if (customLine.getFrom().equal(Objects.requireNonNull(firstCoordinate))) {
                boolean isVisited = false;
                for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                    if (alreadyVisitedCoordinate.equal(customLine.getTo())) {
                        isVisited = true;
                    }
                }
                if (!isVisited) {
                    alreadyVisitedNodes.add(customLine.getTo());
                    nodesToExplore.add(customLine.getTo());
                }
            } else if (customLine.getTo().equal(Objects.requireNonNull(firstCoordinate))) {
                boolean isVisited = false;
                for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                    if (alreadyVisitedCoordinate.equal(customLine.getFrom())) {
                        isVisited = true;
                    }
                }
                if (!isVisited) {
                    alreadyVisitedNodes.add(customLine.getFrom());
                    nodesToExplore.add(customLine.getFrom());
                }
            }
        }

        for (int i = 0; i < nodesToExplore.size(); i++) {
            Coordinate coordinateToExplore = nodesToExplore.get(i);
            for (CustomLine customLine : customLines) {
                //nejdriv kontrola, ze se nepresuneme pres artikulaci do druhe půlky
                if (!customLine.getFrom().equal(borderWhereBridgeBeggins) && !customLine.getTo().equal(borderWhereBridgeBeggins)) {
                    if (customLine.getFrom().equal(coordinateToExplore)) {
                        boolean isVisited = false;
                        for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                            if (alreadyVisitedCoordinate.equal(customLine.getTo())) {
                                isVisited = true;
                            }
                        }
                        if (!isVisited) {
                            alreadyVisitedNodes.add(customLine.getTo());
                            nodesToExplore.add(customLine.getTo());
                        }
                    } else if (customLine.getTo().equal(coordinateToExplore)) {
                        boolean isVisited = false;
                        for (Coordinate alreadyVisitedCoordinate : alreadyVisitedNodes) {
                            if (alreadyVisitedCoordinate.equal(customLine.getFrom())) {
                                isVisited = true;
                            }
                        }
                        if (!isVisited) {
                            alreadyVisitedNodes.add(customLine.getFrom());
                            nodesToExplore.add(customLine.getFrom());
                        }
                    }
                }
            }
            nodesToExplore.remove(i);
            i--;
        }

        if (alreadyVisitedNodes.size() == circles.size()) {
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

        if (redLines.size() < 3) return "chybi ohraniceni cervenou carou";

        ArrayList<Coordinate> alreadyVisitedNodes = new ArrayList<>();

        for (CustomLine redLine : redLines){
            alreadyVisitedNodes.add(redLine.getFrom());
            alreadyVisitedNodes.add(redLine.getTo());
        }
        for (Coordinate circle : circles){
            if (alreadyVisitedNodes.stream().noneMatch(node -> node.equal(circle))){
                return "false";
            }
        }
        if (alreadyVisitedNodes.size() != (circles.size() * 2)) return "false";


        //kontrola, zdali cara neprochazi mistem, kde nebyla puvodne hrana
        for (CustomLine redLine: redLines){
            if (lines.stream().noneMatch(line -> line.isLineSame(redLine))){
                return "false";
            }
        }

        //každy bod max. 2x
        for(Coordinate coordinate : alreadyVisitedNodes){
            AtomicInteger numberOfOccurrence = new AtomicInteger();
            alreadyVisitedNodes.forEach(node -> {
                if (node.equal(coordinate)){
                    numberOfOccurrence.getAndIncrement();
                }
            });
            if (numberOfOccurrence.get() > 2) return "false";
        }

        CustomLine firstRedLine = redLines.get(0);
        CustomLine lastRedLine = redLines.get(redLines.size() - 1);
        if (!firstRedLine.isPointInStartOrEndOfLine(lastRedLine.getFrom()) && !firstRedLine.isPointInStartOrEndOfLine(lastRedLine.getTo()) ){
            return "false";
        }
        return "true";
    }

    //TEORIE (reseno nakonec jinak)
    //vezmu bod se sudym počtem sousedů (těch může být max. 2ks)
    //Přidám bod do zásobníku a jdu na jeho souseda
    //Odstraním hranu ze zásobníku hran (tu mezi prvním bodem a sousedem)
    //pokračuju dál, pokud už nemám souseda, přádám ho do eulerovy cesty a odstraním vrchol ze zásobníku
    public static String checkIfGraphHasEulerPath(Map map){
        ArrayList<CustomLine> redLines = map.getRedLineList();
        ArrayList<CustomLine> lines = map.getCustomLines();
        ArrayList<Coordinate> circles = map.getCircles();

        //spocitame pocet vrcholu se sudym pocetem sousedu
        ArrayList<Coordinate> nodesWithMoreThanTwoNeighbours = new ArrayList<>();
        for (Coordinate coordinate : circles){
            int numberOfLinesConnectedToNode = (int) lines.stream().filter(m -> m.isPointInStartOrEndOfLine(coordinate)).count();
            if (numberOfLinesConnectedToNode % 2 == 0 ){
                nodesWithMoreThanTwoNeighbours.add(coordinate);
            }
        }
        if (nodesWithMoreThanTwoNeighbours.size() > 2 || nodesWithMoreThanTwoNeighbours.isEmpty()) return "false";

        //kontrola, ze redlines maj vsechny normalni cary a zadnou novou navic
        for (CustomLine line : lines){
            boolean found = false;
            for (CustomLine redline : redLines){
                if (redline.isLineSame(line)) found = true;
            }
            if (!found) return "false";
        }

        for (CustomLine redline : redLines){
            boolean found = false;
            for (CustomLine line : lines){
                if (line.isLineSame(redline)) found = true;
            }
            if (!found) return "false";
        }

        //kontrola, ze redlines na sebe navazuji
        //tzn, ze kazdy 2 usecky musi mit spolecny prave jeden bod
        for (int i = 0; i < redLines.size(); i++){
            if (i > 1){
                ArrayList<Coordinate> coordinates = new ArrayList<>();

                CustomLine firstLine = redLines.get(i - 1);
                Coordinate first = firstLine.getFrom();
                Coordinate second = firstLine.getTo();

                CustomLine secondLine = redLines.get(i);
                Coordinate third = secondLine.getFrom();
                Coordinate fourth = secondLine.getTo();

                if (!first.equal(third) && !first.equal(fourth) && !second.equal(third) && !second.equal(fourth)){
                    return "false";
                }
            }
        }
        return "true";
    }

    public static boolean checkIfGraphIsCorrect(Map map, ArrayList<Integer> degreeList){
        ArrayList<CustomLine> lines = map.getCustomLines();
        ArrayList<Coordinate> circles = map.getCircles();

        if (degreeList.size() != circles.size()) return false;

        //spocitam stupne vrcholu jednotlivych uzlu
        HashMap<Coordinate, Integer> degreesMap = new HashMap<>();
        for (Coordinate node : circles){
            int degree = 0;
            for (CustomLine customLine : lines){
                if (customLine.isPointInStartOrEndOfLine(node)) degree++;
            }
            degreesMap.put(node, degree);
        }

        //pro kazdej stupen vrcholu smazu z Hasmapy stejnou value a nemělo by mi na konci nic chybět -> tzn. projdu bez problemu a muzu rict, je to to spravne
        for (Integer degree: degreeList){
            Coordinate coordinateToDelete = null;
            for(HashMap.Entry<Coordinate, Integer> entry : degreesMap.entrySet()) {
                if (entry.getValue().equals(degree)){
                   coordinateToDelete = entry.getKey();
                   break;
                }
            }
            if (coordinateToDelete != null){
                degreesMap.remove(coordinateToDelete);
            }else{
                return false;
            }
        }
        return true;
    }
}
