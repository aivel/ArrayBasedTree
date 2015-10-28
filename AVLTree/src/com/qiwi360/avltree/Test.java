package com.qiwi360.avltree;

/**
 *
 * @author Max
 */
public class Test {
    static class PrintVisitor<T> implements AVLTree.IVisitor<T> {
        @Override
        public void visit(T value) {
            System.out.print(value.toString() + " ");
        }
    }

    public static void main(String[] args) throws Exception {
        AVLTree<String> at = new AVLTree<>();

//        String[] months = {"March", "May", "November", "August", "April", "January", "December", "July",
//                "February", "June", "October", "September"};
//
//        for(String month: months) {
//            at.add(month);
//        }
//
//        System.out.println("pre: ");
//        at.traversePreorder(new PrintVisitor<>());
//        System.out.println("\nin: ");
//        at.traverseInorder(new PrintVisitor<>());
//        System.out.println("\npost: ");
//        at.traversePostorder(new PrintVisitor<>());
//
//        System.out.println("-----");
//
//        List<List<String>> levels = at.splitToLevels();
//
//        for (List<String> level: levels) {
//            for (String str: level) {
//                System.out.printf("%s ", str);
//            }
//
//            System.out.printf("\n");
//        }
    }
    
}
