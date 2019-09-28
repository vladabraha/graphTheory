package cz.uhk.graphstheory.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Util {

    //myšlenka - vyzmu pole, naplním ho hodnotama menší než je velikost pole (definice)
    //HAVEL HAKIMI algorithm
    //zkontroluju, že to může být skore - seradim pole, vezmu prvni hodnotu (největší), smažu a podle jeji hodnoty odečtu danemu počtu jedničku od dalších prvku
    //vždycky zkontroluju, že nejsem už v zápornejch číslech a na konci odečtení znovu seřadím pole
    public static ArrayList<Integer> generateGraphScore(int amountOfNodes) {
        ArrayList<Integer> graphScore, testingArray;
        boolean found = false;
        boolean wrongScoreGenerated;
        do {
            wrongScoreGenerated = false;
            graphScore = new ArrayList<>();
            testingArray = new ArrayList<>();
            for (int i = 0; i < amountOfNodes; i++) {
                Random random = new Random();
                int number = random.nextInt(amountOfNodes);
                graphScore.add(number); //tohle je vygenerovany
                testingArray.add(number); //v tomhle jsou stejny hodnoty a budeme na nem testovat alg.
            }
            for (int i = 0; i < amountOfNodes; i++) {
                Collections.sort(testingArray, Collections.reverseOrder());
                Collections.sort(graphScore, Collections.reverseOrder());
                int firstNumber = testingArray.get(0);
                if (firstNumber > (amountOfNodes - 1)) {
                    wrongScoreGenerated = true;
                    break;
                }

                testingArray.remove(0); //odstranime prvni boc
                //odecteme jednicku od dalsich hodnot (dle hodnoty prvniho elementu)
                for (int j = 0; j < firstNumber; j++) {
                    int value = testingArray.get(j);
                    value--;
                    if (value < 0) {
                        wrongScoreGenerated = true;
                        break;
                    }
                    testingArray.set(j, value);
                }

                if (wrongScoreGenerated) {
                    break;
                }
            }
            if (!wrongScoreGenerated) found = true;

        } while (!found);

        return graphScore;
    }
}
