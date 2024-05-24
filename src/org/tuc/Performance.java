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

		System.out.println("Insert Time	AVL     BST    BTree1001	BTree600	Linear40    Linear1000");
		for (int[] keys : keySets) {
			AVLTree avlTree = new AVLTree();
			BSTree bsTree = new BSTree();
			BTree bTree1001 = new BTree(500);
			BTree bTree600 = new BTree(300);
			LinearHashing linearHashing40 = new LinearHashing(40, 500);
			LinearHashing linearHashing1000 = new LinearHashing(1000, 500);
			// Insert keys
			insertKeys(keys, avlTree);
			insertKeys(keys, bsTree);
			insertKeys(keys, bTree1001);
			insertKeys(keys, bTree600);
			insertKeys(keys, linearHashing40);
			insertKeys(keys, linearHashing1000);

			// Insert Time Measurement

			System.out.print(keys.length + "	");
			measureInsertPerformance(keys, avlTree);
			measureInsertPerformance(keys, bsTree);
			measureInsertPerformance(keys, bTree1001);
			measureInsertPerformance(keys, bTree600);
			measureInsertPerformance(keys, linearHashing40);
			measureInsertPerformance(keys, linearHashing1000);
			System.out.println();


			// Search Time Measurement
			//System.out.println("Search Time	AVL     BST    BTree1001	BTree600	Linear40    Linear1000");

			System.out.print(keys.length + "	");
			measureSearchPerformance(keys, avlTree);
			measureSearchPerformance(keys, bsTree);
			measureSearchPerformance(keys, bTree1001);
			measureSearchPerformance(keys, bTree600);
			measureSearchPerformance(keys, linearHashing40);
			measureSearchPerformance(keys, linearHashing1000);
			System.out.println();


			// Search Levels Measurement
			//System.out.println("Search Levels	AVL     BST    BTree1001	BTree600	Linear40    Linear1000");

			System.out.print(keys.length + "	");
			measureSearchLevelsPerformance(keys, avlTree);
			measureSearchLevelsPerformance(keys, bsTree);
			measureSearchLevelsPerformance(keys, bTree1001);
			measureSearchLevelsPerformance(keys, bTree600);
			measureSearchLevelsPerformance(keys, linearHashing40);
			measureSearchLevelsPerformance(keys, linearHashing1000);
			System.out.println();
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

		double averageTime = (double) totalTime / K;
		System.out.print("	|" + averageTime);
	}

	private static void measureSearchPerformance(int[] keys, SearchInsert dataStructure) {
		long totalTime = 0;
		int K = determineK(keys.length);
		int key;
		long startTime, endTime;
		boolean found;
		for (int i = 0; i < K; i++) {
			key = generateRandomKey(keys.length);
			startTime = System.nanoTime();
			found = dataStructure.searchKey(key);
			// endTime = System.nanoTime();
			totalTime += (System.nanoTime() - startTime);
		}

		double averageTime = (double) totalTime / K;
		System.out.print("	|" + averageTime);
	}

	private static void measureSearchLevelsPerformance(int[] keys, SearchInsert dataStructure) {
		int totalLevels = 0;
		int K = determineK(keys.length);
		int key;
		for (int i = 0; i < K; i++) {
			key = generateRandomKey(keys.length);
			totalLevels += getSearchLevels(dataStructure, key);
		}
		double averageLevels = totalLevels / K;
		System.out.print("	|" + averageLevels);
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

	private static double getSearchLevels(SearchInsert dataStructure, int key) {
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
