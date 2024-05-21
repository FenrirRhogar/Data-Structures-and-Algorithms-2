package org.tuc;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.tuc.avl.AVLTree;
import org.tuc.bst.BSTree;
import org.tuc.btree.BTree;
import org.tuc.interfaces.SearchInsert;
import org.tuc.linearhashing.LinearHashing;

public class Performance {

    public static void main(String[] args) throws IOException {
        // Βήμα 1: Ανάγνωση κλειδιών
        List<int[]> keySets = readAllKeyFiles();

        // Βήμα 2: Προετοιμασία
        for (int[] keys : keySets) {
            runExperiment(keys);
        }
    }

    // Βήμα 1: Ανάγνωση κλειδιών από αρχεία
    private static List<int[]> readAllKeyFiles() throws IOException {
        List<int[]> keySets = new ArrayList<>();
        String basePath = "Keys/";
        int[] Ns = {20, 50, 100, 200, 1000, 2500, 5000, 10000, 20000, 40000, 60000, 80000, 100000, 200000, 1000000, 1500000, 2000000, 2500000, 3000000};

        for (int N : Ns) {
            String fileName = "numbers-" + N + ".bin";
            //keySets.add(readKeysFromFile(fileName, N));
            keySets.add(RandomNumbersToFiles.readInts(fileName));
        }

        return keySets;
    }

    private static int[] readKeysFromFile(String fileName, int N) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        int[] keys = new int[N];
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < N; i++) {
            keys[i] = buffer.getInt();
        }

        return keys;
    }

    // Βήμα 2: Προετοιμασία και Βήμα 3: Μετρήσεις
    private static void runExperiment(int[] keys) {
        // Δημιουργία 6 στιγμιότυπων δομών δεδομένων
        AVLTree avlTree = new AVLTree();
        BSTree bsTree = new BSTree();
        //BTree bTree1001 = new BTree(1001);
        //BTree bTree600 = new BTree(600);
        LinearHashing linearHashing40 = new LinearHashing(40, 500);
        LinearHashing linearHashing1000 = new LinearHashing(1000, 500);

        // Εισαγωγή κλειδιών
        insertKeys(keys, avlTree);
        insertKeys(keys, bsTree);
        //insertKeys(keys, bTree1001);
        //insertKeys(keys, bTree600);
        insertKeys(keys, linearHashing40);
        insertKeys(keys, linearHashing1000);

        // Μετρήσεις εισαγωγής
        measureInsertPerformance(keys, avlTree);
        measureInsertPerformance(keys, bsTree);
        //measureInsertPerformance(keys, bTree1001);
        //measureInsertPerformance(keys, bTree600);
        measureInsertPerformance(keys, linearHashing40);
        measureInsertPerformance(keys, linearHashing1000);

        // Μετρήσεις αναζήτησης
        measureSearchPerformance(keys, avlTree);
        measureSearchPerformance(keys, bsTree);
        //measureSearchPerformance(keys, bTree1001);
        //measureSearchPerformance(keys, bTree600);
        measureSearchPerformance(keys, linearHashing40);
        measureSearchPerformance(keys, linearHashing1000);
    }

    private static void insertKeys(int[] keys, SearchInsert dataStructure) {
        for (int key : keys) {
            dataStructure.insert(key);
        }
    }

    private static void measureInsertPerformance(int[] keys, SearchInsert dataStructure) {
        long totalTime = 0;
        int K = determineK(keys.length);

        for (int i = 0; i < K; i++) {
            int key = generateRandomKey(keys.length);
            long startTime = System.nanoTime();
            dataStructure.insert(key);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }

        double averageTime = (double) totalTime / K;
        System.out.printf("Average insert time for %s: %.2f ns\n", dataStructure.getClass().getSimpleName(), averageTime);
    }

    private static void measureSearchPerformance(int[] keys, SearchInsert dataStructure) {
        long totalTime = 0;
        int totalLevels = 0;
        int K = determineK(keys.length);

        for (int i = 0; i < K; i++) {
            int key = generateRandomKey(keys.length);
            long startTime = System.nanoTime();
            boolean found = dataStructure.searchKey(key);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
            totalLevels += getSearchLevels(dataStructure, key); // Υποθέτουμε ότι κάθε δομή έχει μια μέθοδο για τα επίπεδα
        }

        double averageTime = (double) totalTime / K;
        double averageLevels = (double) totalLevels / K;
        System.out.printf("Average search time for %s: %.2f ns, Average levels: %.2f\n", dataStructure.getClass().getSimpleName(), averageTime, averageLevels);
    }

    private static int determineK(int N) {
        if (N < 201) return 10;
        if (N < 1001) return 50;
        return 100;
    }

    private static int generateRandomKey(int N) {
        return new Random().nextInt(3 * N) + 1;
    }

    private static int getSearchLevels(SearchInsert dataStructure, int key) {
    	if (dataStructure instanceof AVLTree) {
            return ((AVLTree) dataStructure).searchKeyLevels(key);
        } else if (dataStructure instanceof BSTree) {
            return ((BSTree) dataStructure).searchKeyLevelsBSTree(key);
        } else if (dataStructure instanceof BTree) {
            return ((BTree) dataStructure).searchKeyLevels(key);
        } else if (dataStructure instanceof LinearHashing) {
            return ((LinearHashing) dataStructure).getSearchLevels(key);
        }
        return 0;
    }
    
}
