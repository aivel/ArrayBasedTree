package com.qiwi360.avltree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Max on 13.10.2015.
 */
public class AVLTree<T extends Comparable> {
    private final class Node implements Comparable<T> {
        private T value;
        private int height;
        private Node left;
        private Node right;

        public Node(T value) {
            this.value = value;
            height = 1;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        @Override
        public int compareTo(T o) {
            return value.compareTo(o);
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public interface IVisitor<T> {
        void visit(T value);
    }

    private Node root;

    private Node find(Node node, T value) {
        if (node == null) {
            return null;
        }

        if (value.equals(node.value)) {
            return node;
        }

        if (value.compareTo(node.value) > 0) {
            return find(node.right, value);
        }

        if (value.compareTo(node.value) < 0) {
            return find(node.left, value);
        }

        return null;
    }

    public Node find(T value) {
        return find(root, value);
    }

    private Node insert(Node p, T value) {
        if (p == null) {
            return new Node(value);
        }

        if (value.compareTo(p.value) < 0) {
            p.setLeft(insert(p.left, value));
            return balance(p);
        }

        if (value.compareTo(p.value) > 0) {
            p.setRight(insert(p.right, value));
            return balance(p);
        }

        return null;
    }

    public void insert(T value) {
        root = insert(root, value);
    }

    private Node findParentNode(Node node, T value) {
        if (root == null) {
            return null;
        }

        if (node.left != null) {
            if (value.equals(node.left.value)) {
                return node;
            }
        }

        if (node.right != null) {
            if (value.equals(node.right.value)) {
                return node;
            }
        }

        if (value.compareTo(node.value) > 0) {
            return findParentNode(node.right, value);
        }

        if (value.compareTo(node.value) < 0) {
            return findParentNode(node.left, value);
        }

        return null;
    }

    private Node findMin(Node node) {
        if (node == null) {
            return null;
        }

        if (node.left == null) {
            return node;
        }

        return findMin(node.left);
    }

    private Node findMax(Node node) {
        if (node == null) {
            return null;
        }

        if (node.right == null) {
            return node;
        }

        return findMax(node.right);
    }

    private Node removeMin(Node p) {
        if (p.left == null) {
            return p.right;
        }

        p.setLeft(removeMin(p.left));

        return balance(p);
    }

    private Node delete(Node node, T value) {
        if (node == null) {
            return null;
        }

        if (value.compareTo(node.value) > 0) {
            return balance(delete(node.right, value));
        }

        if (value.compareTo(node.value) < 0) {
            return balance(delete(node.left, value));
        }

        if (value.equals(node.value)) {
            Node parent = findParentNode(root, value);

            // node has no children

            if (node.right == null && node.left == null) {
                if (parent == null) {
                    if (node == root) {
                        // trying to remove the root element
                        // root that has no children
                        root = null;
                        return null;
                    }

                    return null;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(null);
                    return balance(parent);
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(null);
                    return balance(parent);
                }

                return null;
            }

            // Node has one child

            if (node.right != null && node.left == null) {
                // right child
                if (parent == null) {
                    if (node == root) {
                        root = node.right;

                        return balance(root);
                    }

                    return null;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(node.right);
                    return balance(parent);
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(node.right);
                    return balance(parent);
                }

                return null;
            }

            if (node.left != null && node.right == null) {
                // left child
                if (parent == null) {
                    if (node == root) {
                        root = node.left;

                        return balance(root);
                    }

                    return null;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(node.left);
                    return balance(parent);
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(node.left);
                    return balance(parent);
                }

                return null;
            }

            // node has two children

            // take predecessor
            Node nodeToReplaceWith = findMax(node.left);

            if (nodeToReplaceWith == null) {
                nodeToReplaceWith = findMin(node.right);

                if (nodeToReplaceWith == null) {
                    return null;
                }
            }

            delete(nodeToReplaceWith.value);

            node.setValue(nodeToReplaceWith.value);

            return balance(node);
        }

        return null;
    }

    public void delete(T value) {
        root = balance(delete(root, value));
    }

    private int balanceFactor(Node node) {
        return (node.right != null ? node.right.height: 0) - (node.left != null ? node.left.height : 0);
    }

    private void fixHeight(Node node) {
        node.setHeight(Math.max(node.left != null ? node.left.height : 0,
                node.right != null ? node.right.height : 0) + 1);
    }

    private Node rotateRight(Node p) {
        Node q = p.left;

        p.setLeft(q.right);
        q.setRight(p);
        fixHeight(p);
        fixHeight(q);

        return q;
    }

    private Node rotateLeft(Node q) {
        Node p = q.right;

        q.setRight(p.left);
        p.setLeft(q);
        fixHeight(q);
        fixHeight(p);

        return p;
    }

    private Node balance(Node p) {
        fixHeight(p);
        int pBalanceFactor = balanceFactor(p);

        if (pBalanceFactor == 2) {
            if (balanceFactor(p.right) < 0) {
                p.setRight(rotateRight(p.right));
            }

            return rotateLeft(p);
        }

        if (pBalanceFactor == -2) {
            if (balanceFactor(p.left) > 0) {
                p.setLeft(rotateLeft(p.left));
            }

            return rotateRight(p);
        }

        return p;
    }

    private void traversePreorder(Node node, IVisitor visitor) {
        if (node == null) {
            return;
        }

        visitor.visit(node.value);
        traversePreorder(node.left, visitor);
        traversePreorder(node.right, visitor);
    }

    public void traversePreorder(IVisitor visitor) {
        traversePreorder(root, visitor);
    }

    private void traverseInorder(Node node, IVisitor visitor) {
        if (node == null) {
            return;
        }

        traverseInorder(node.left, visitor);
        visitor.visit(node.value);
        traverseInorder(node.right, visitor);
    }

    public void traverseInorder(IVisitor visitor) {
        traverseInorder(root, visitor);
    }

    private void traversePostorder(Node node, IVisitor visitor) {
        if (node == null) {
            return;
        }

        traversePostorder(node.left, visitor);
        traversePostorder(node.right, visitor);
        visitor.visit(node.value);
    }

    public List<Node> flatten() {
        Stack<Node> stack = new Stack<>();
        List<Node> result = new LinkedList<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();

            result.add(node);

            if (node.right != null) {
                stack.push(node.right);
            }

            if (node.left != null) {
                stack.push(node.left);
            }
        }

        return result;
    }

    private void splitToLevels(Node node, List<List<T>> levels, int level) {
        if (node == null) {
            return;
        }

        if (levels == null) {
            return;
        }

        List<T> currentLevel;

        if (levels.size() - 1 == level) {
            currentLevel = levels.get(level);
        } else {
            currentLevel = new LinkedList<>();
            levels.add(currentLevel);
        }

        currentLevel.add(node.value);

        splitToLevels(node.left, levels, level + 1);
        splitToLevels(node.right, levels, level + 1);
    }

    public List<List<T>> splitToLevels() {
        List<List<T>> levels = new LinkedList<>();
        splitToLevels(root, levels, 0);
        return levels;
    }

    public void traversePostorder(IVisitor visitor) {
        traversePostorder(root, visitor);
    }

    public static void main(String[] args) {
        AVLTree<Integer> avl = new AVLTree<>();

        Scanner scanner = null;

        try {
            scanner = new Scanner(new File("avl.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringJoiner sj = new StringJoiner(" ");

        String toInsert = scanner.nextLine();
        String toDelete = scanner.nextLine();
        String toFind = scanner.nextLine();

        if (!toInsert.isEmpty()) {
            for (String number : toInsert.split(" ")) {
                // insert
                avl.insert(Integer.parseInt(number));
            }
        }

        if (!toDelete.isEmpty()) {
            for (String number: toDelete.split(" ")) {
                // delete
                avl.delete(Integer.parseInt(number));
            }
        }

        if (!toFind.isEmpty()) {
            for (String number : toFind.split(" ")) {
                // find
                AVLTree<Integer>.Node node = avl.find(Integer.parseInt(number));
                AVLTree<Integer>.Node right = node == null ? null : node.getRight();

                if (right == null) {
                    sj.add("null");
                } else {
                    sj.add(String.valueOf(right.getValue()));
                }
            }
        }

        try {
            File file = new File("avl.out");
            file.delete();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(sj.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
