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
		long start = System.nanoTime();
		List<int[]> keySets = readAllKeyFiles();

		// Data Structures Instances

		System.out.println("				|AVL(insert)     	|BST(insert)    	|BTree100(insert)	|BTree600(insert)	|Linear40(insert)       |Linear1000(insert)	|AVL(search)     	|BST(search)    	|BTree100(search)	|BTree600(search)	|Linear40(search)       |Linear1000(search)	|AVL(levels)	     	|BST(levels)    	|BTree100(levels)	|BTree600(levels)	|Linear40(levels)       |Linear1000(levels)	");
		for (int[] keys : keySets) {
			AVLTree avlTree = new AVLTree();
			BSTree bsTree = new BSTree();
			BTree bTree100 = new BTree(50);
			BTree bTree600 = new BTree(300);
			LinearHashing linearHashing40 = new LinearHashing(40, 500);
			LinearHashing linearHashing1000 = new LinearHashing(1000, 500);
			// Insert keys
			insertKeys(keys, avlTree);
			insertKeys(keys, bsTree);
			insertKeys(keys, bTree100);
			insertKeys(keys, bTree600);
			insertKeys(keys, linearHashing40);
			insertKeys(keys, linearHashing1000);

			// Insert Time Measurement
			System.out.print(keys.length + "	");
			measureInsertPerformance(keys, avlTree);
			measureInsertPerformance(keys, bsTree);
			measureInsertPerformance(keys, bTree100);
			measureInsertPerformance(keys, bTree600);
			measureInsertPerformance(keys, linearHashing40);
			measureInsertPerformance(keys, linearHashing1000);


			// Search Time and Levels Measurement
			double avl = measureSearchPerformance(keys, avlTree);
			double bst = measureSearchPerformance(keys, bsTree);
			double bt1001 = measureSearchPerformance(keys, bTree100);
			double bt600 = measureSearchPerformance(keys, bTree600);
			double lh40 = measureSearchPerformance(keys, linearHashing40);
			double lh1000 = measureSearchPerformance(keys, linearHashing1000);

			System.out.print("			|" + avl);
			System.out.print("			|" + bst);
			System.out.print("			|" + bt1001);
			System.out.print("			|" + bt600);
			System.out.print("			|" + lh40);
			System.out.print("			|" + lh1000);
			System.out.println();
		}
		long end = System.nanoTime();
		System.out.println("Total time:" + (end - start) / 1000000000);
	}

	private static List<int[]> readAllKeyFiles() throws IOException {
		List<int[]> keySets = new ArrayList<>();
		String basePath = "Keys/";
		int[] Ns = { 20, 50, 100, 200, 1000, 2500, 5000, 10000, 20000, 40000, 60000, 80000, 100000, 200000,
				1000000, 1500000, 2000000, 2500000, 3000000};

		for (int N : Ns) {
			String fileName = "numbers-" + N + ".bin";
			keySets.add(RandomNumbersToFiles.readInts(fileName));
		}

		return keySets;
	}

	private static void insertKeys(int[] keys, SearchInsert dataStructure) {
		for (int key : keys) {
			dataStructure.insert(key);
		}
	}

	private static void measureInsertPerformance(int[] keys, SearchInsert dataStructure) {
		long totalTime = 0;
		int K = determineK(keys.length);
		int key;
		long startTime, endTime;
		for (int i = 0; i < K; i++) {
			key = generateRandomKey(keys.length);
			startTime = System.nanoTime();
			dataStructure.insert(key);
			endTime = System.nanoTime();
			totalTime += (endTime - startTime);
		}

		double averageTime = (double) totalTime / (double) K;
		System.out.print("			|" + averageTime);
	}

	private static double measureSearchPerformance(int[] keys, SearchInsert dataStructure) {
		long totalTime = 0;
		float totalLevels = 0;
		int K = determineK(keys.length);
		int key;
		long startTime, endTime;
		boolean found;
		for (int i = 1; i < 5; i++) {
			MultiCounter.resetCounter(i);
		}
		for (int i = 0; i < K; i++) {
			key = generateRandomKey(keys.length);
			startTime = System.nanoTime();
			found = dataStructure.searchKey(key);
			endTime = System.nanoTime();
			totalTime += (endTime - startTime);
		}
		if (dataStructure.getClass().getSimpleName().contentEquals("AVLTree")) {
			totalLevels = MultiCounter.getCount(1);
		} else if (dataStructure.getClass().getSimpleName().contentEquals("BSTree")) {
			totalLevels = MultiCounter.getCount(2);
		}	else if (dataStructure.getClass().getSimpleName().contentEquals("BTree")) {
			totalLevels = MultiCounter.getCount(3);
		} else {
			totalLevels = MultiCounter.getCount(4);
		}
		
		double averageTime = (double) totalTime / (double) K;
		System.out.print("			|" + averageTime);
		double averageLevels = (double) totalLevels / (double) K;
		return averageLevels;
	}

	private static int determineK(int N) {
		if (N < 201)
			return 10;
		if (N < 1001)
			return 50;
		return 100;
	}

	private static int generateRandomKey(int N) {
		return new Random().nextInt(3 * N) + 1;
	}

}
