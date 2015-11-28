package com.qiwi360.heap;

import java.util.ArrayList;

/**
 * Created by Max on 10.11.2015.
 */
public class Heap<T> {
    private class HeapNode {
        private int key;
        private T value;

        public HeapNode(int key, T value) {
            this.key = key;
            this.value = value;
        }
    }

    private ArrayList<HeapNode> nodes = new ArrayList<>();

    private int getLeftChildIndex(int i) {
        return 2 * i + 1;
    }

    private HeapNode getLeftChild(int i) {
        int leftChildIndex = getLeftChildIndex(i);

        if (leftChildIndex < nodes.size()) {
            HeapNode leftChild = nodes.get(leftChildIndex);

            if (leftChild != null) {
                return leftChild;
            }
        }

        return null;
    }

    private int getRightChildIndex(int i) {
        return 2 * i + 2;
    }

    private HeapNode getRightChild(int i) {
        int rightChildIndex = getRightChildIndex(i);

        if (rightChildIndex < nodes.size()) {
            HeapNode rightChild = nodes.get(rightChildIndex);

            if (rightChild != null) {
                return rightChild;
            }
        }

        return null;
    }

    private HeapNode getMaxChild(int index) {
        if (index < 0 || index >= nodes.size()) {
            return null;
        }

        HeapNode leftChild = getLeftChild(index);
        HeapNode rightChild = getRightChild(index);

        if (leftChild == null && rightChild == null) {
            // Node has no children
            return null;
        }

        // One of children is null

        if (leftChild == null) {
            return rightChild;
        }

        if (rightChild == null) {
            return leftChild;
        }

        return rightChild.key >= leftChild.key ? rightChild : leftChild;
    }

    private int getParent(int i) {
        return i < nodes.size() && i > 0 ? i / 2 : -1;
    }

    public void put(int key, T value) {
        nodes.add(new HeapNode(key, value));

        upHeap(nodes.size() - 1);
    }

    private void upHeap(int index) {
        while (index >= 0) {
            int parent = getParent(index);

            if (parent < 0) {
                return;
            }

            if (nodes.get(index).key > nodes.get(parent).key) {
                swap(index, parent);
            }

            index = parent;
        }
    }

    private void downHeap(int index) {
        int biggestChild;
        HeapNode start = nodes.get(index);

        while (index < nodes.size() / 2) {
            int leftChildIndex = getLeftChildIndex(index);
            int rightChildIndex = getRightChildIndex(index);

            if(rightChildIndex < nodes.size() && nodes.get(leftChildIndex).key < nodes.get(rightChildIndex).key) {
                biggestChild = rightChildIndex;
            }
            else {
                biggestChild = leftChildIndex;
            }

            if(start.key >= nodes.get(biggestChild).key) {
                break;
            }
            else {
                nodes.set(index, nodes.get(biggestChild));
                index = biggestChild;
            }
        }

        nodes.set(index, start);
    }

    private void swap(int currentKey, int parentKey) {
        HeapNode tmpNode = nodes.get(currentKey);
        nodes.set(currentKey, nodes.get(parentKey));
        nodes.set(parentKey, tmpNode);
    }

    public T removeMax() {
        if (nodes.isEmpty()) {
            return null;
        }

        T val = nodes.get(0).value;
        swap(0, nodes.size() - 1);
        nodes.remove(nodes.size() - 1);
        downHeap(0);

        return val;
    }

    static class PriorityQueue<T> extends Heap<T> {
        public T peek() {
            return (T) removeMax();
        }
    }

    public static void heapSort(int[] arr) {
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            shiftDown(arr, i, arr.length);
        }

        for (int i = arr.length - 1; i >= 1; i--) {
            swap(arr, 0, i);
            shiftDown(arr, 0, i);
        }
    }

    private static void swap(int[] arr, int currentKey, int parentKey) {
        int tmpNode = arr[currentKey];
        arr[currentKey] = arr[parentKey];
        arr[parentKey] = tmpNode;
    }

    static void shiftDown(int[] arr, int i, int j) {
        boolean done = false;
        int maximumChild;

        while ((i * 2 + 1 < j) && (!done)) {
            if (i * 2 + 1 == j - 1)
                maximumChild = i * 2 + 1;
            else if (arr[i * 2 + 1] > arr[i * 2 + 2])
                maximumChild = i * 2 + 1;
            else
                maximumChild = i * 2 + 2;

            if (arr[i] < arr[maximumChild]) {
                swap(arr, i, maximumChild);
                i = maximumChild;
            }
            else {
                done = true;
            }
        }
    }

    private static int[] mergeSort(int[] arr) {
        if (arr.length < 2) {
            return arr;
        }

        int mid = arr.length / 2;
        int[] left = new int[mid];

        for (int i = 0; i < mid; i++) {
            left[i] = arr[i];
        }

        int rightSize = arr.length - mid;
        int[] right = new int[rightSize];

        for (int i = mid, j = 0; i < mid + rightSize; i++, j++) {
            right[j] = arr[i];
        }

        left = mergeSort(left);
        right = mergeSort(right);

        return merge(left, right);
    }

    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];

        for (int l = 0, r = 0, i = 0; l < left.length || r < right.length;) {

            if (l < left.length) {
                if (r < right.length) {
                    if (left[l] < right[r]) {
                        result[i++] = left[l++];
                    } else {
                        result[i++] = right[r++];
                    }
                } else {
                    result[i++] = left[l++];
                }
            } else {
                result[i++] = right[r++];
            }
        }

        return result;
    }

    private static void testPQ() {
        PriorityQueue<String> heap = new PriorityQueue<>();
        int SIZE = 14;

        for (int i = 0; i < SIZE; i++) {
            heap.put(i - SIZE, "sdf: " + (i - SIZE));
        }

        System.out.println(heap.peek());

        System.out.println("ok");
    }

    private static void testHeapSort() {
        int[] arr = {3, 56, 565, 2, 12, 53, 33, 14, 4, 57};

        heapSort(arr);
        System.out.println("ok");
    }

    private static void testMergeSort() {
        int[] arr = {3, 56, 565, 2, 12, 53, 33, 14, 4, 57};

        arr = mergeSort(arr);
        System.out.println("ok");
    }


    public static void main(String[] args) {
//        testPQ();
//        testHeapSort();
        testMergeSort();
    }
}
