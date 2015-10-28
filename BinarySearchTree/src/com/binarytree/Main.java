package com.binarytree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Created by Max on 13.10.2015.
 */
public class Main {
    static class PrintVisitor<T> implements BinarySearchTree.IVisitor<T> {
        @Override
        public void visit(T value) {
            System.out.print(value + " ");
        }
    }

    static class SumIntegerVisitor<T extends Integer> implements BinarySearchTree.IVisitor<T> {
        Integer sum = 0;

        @Override
        public void visit(T value) {
            sum = sum + (Integer)value;
        }

        public void clear() {
            sum = 0;
        }

        public Integer result() {
            return sum;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        Scanner scanner = null;

        try {
            scanner = new Scanner(new File("bst.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringJoiner sj = new StringJoiner(" ");

        String toInsert = scanner.nextLine();
        String toDelete = scanner.nextLine();
        String toFind = scanner.nextLine();

        for (String number: toInsert.split(" ")) {
            // insert
            bst.insert(Integer.parseInt(number));
        }

        for (String number: toDelete.split(" ")) {
            // delete
            bst.delete(Integer.parseInt(number));
        }

        for (String number: toFind.split(" ")) {
            // find
            BinarySearchTree<Integer>.Node<Integer> node = bst.find(Integer.parseInt(number));
            BinarySearchTree<Integer>.Node<Integer> right = node == null ? null : node.getRight();

            if (right == null) {
                sj.add("null");
            } else {
                sj.add(String.valueOf(right.getValue()));
            }
        }

        try {
            File file = new File("bst.out");
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(sj.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
