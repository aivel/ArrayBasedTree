package com.binarytree;

/**
 * Created by Max on 13.10.2015.
 */
public class Main {
    static class PrintVisitor<T> implements BinarySearchTree.IVisitor<T> {
        @Override
        public void visit(T value) {
            System.out.print(value);
        }
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();

        for (int i = 0; i < 10; i++) {
            bst.add(i);
        }

//        bst.delete(Integer.valueOf(5));

        bst.traverse(new PrintVisitor<Integer>());
        System.out.printf("\nSize: %s", bst.size());
    }
}
