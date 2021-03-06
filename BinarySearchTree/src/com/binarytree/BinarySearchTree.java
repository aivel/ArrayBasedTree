package com.binarytree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Max on 13.10.2015.
 * Binary Search Tree implementation
 */
public class BinarySearchTree<T extends Comparable> {
    public final class Node<T extends Comparable> implements Comparable<T> {
        // Tree node
        private T value;
        private Node<T> left;
        private Node<T> right;

        public Node(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public Node<T> getLeft() {
            return left;
        }

        public void setLeft(Node<T> left) {
            this.left = left;
        }

        public Node<T> getRight() {
            return right;
        }

        public void setRight(Node<T> right) {
            this.right = right;
        }

        @Override
        public int compareTo(T o) {
            return value.compareTo(o);
        }
    }

    public interface IVisitor<T> {
        // Visitor interface for traversing the tree
        void visit(T value);
    }

    private Node<T> root;
    private int size; // size of a tree(number of nodes)

    public BinarySearchTree() {
        size = 0;
    }

    private Node<T> find(Node<T> node, T value) {
        if (node == null) {
            return null;
        }

        if (value.equals(node.value)) {
            return node;
        }

        if (value.compareTo(node.value) > 0) {
            return find(node.right, value);
        } else {
            return find(node.left, value);
        }
    }

    public Node<T> find(T value) {
        return find(root, value);
    }

    private boolean insert(Node<T> node, T value) {
        if (node == null) {
            return false;
        }

        if (value.compareTo(node.value) > 0) {
            if (node.right == null) {
                node.setRight(new Node<>(value));
                size++;
                return true;
            }

            return insert(node.right, value);
        }

        if (value.compareTo(node.value) < 0) {
            if (node.left == null) {
                node.setLeft(new Node<>(value));
                size++;
                return true;
            }

            return insert(node.left, value);
        }

        return false;
    }

    public boolean insert(T value) {
        if (root == null) {
            root = new Node<>(value);
            size++;
            return true;
        }

        return insert(root, value);
    }

    private Node<T> findParentNode(Node<T> node, T value) {
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

    private Node<T> findMin(Node<T> node) {
        if (node == null) {
            return null;
        }

        if (node.left == null) {
            return node;
        }

        return findMin(node.left);
    }

    private Node<T> findMax(Node<T> node) {
        if (node == null) {
            return null;
        }

        if (node.right == null) {
            return node;
        }

        return findMax(node.right);
    }

    private boolean delete(Node<T> node, T value) {
        if (node == null) {
            return false;
        }

        if (value.compareTo(node.value) > 0) {
            return delete(node.right, value);
        }

        if (value.compareTo(node.value) < 0) {
            return delete(node.left, value);
        }

        if (value.equals(node.value)) {
            Node<T> parent = findParentNode(root, value);

            // node has no children

            if (node.right == null && node.left == null) {
                if (parent == null) {
                    if (node == root) {
                        // trying to remove the root element
                        // root that has no children
                        size = 0;
                        root = null;
                        return true;
                    }

                    return false;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(null);
                    size--;
                    return true;
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(null);
                    size--;
                    return true;
                }

                return false;
            }

            // Node has one child

            if (node.right != null && node.left == null) {
                // right child
                if (parent == null) {
                    if (node == root) {
                        root = node.right;
                        size--;

                        return true;
                    }

                    return false;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(node.right);
                    size--;
                    return true;
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(node.right);
                    size--;
                    return true;
                }

                return false;
            }

            if (node.left != null && node.right == null) {
                // left child
                if (parent == null) {
                    if (node == root) {
                        root = node.left;
                        size--;

                        return true;
                    }

                    return false;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(node.left);
                    size--;
                    return true;
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(node.left);
                    size--;
                    return true;
                }

                return false;
            }

            // node has two children

            // take predecessor
            Node<T> nodeToReplaceWith = findMax(node.left);

            if (nodeToReplaceWith == null) {
                nodeToReplaceWith = findMin(node.right);

                if (nodeToReplaceWith == null) {
                    return false;
                }
            }

            if (delete(nodeToReplaceWith.value)) {
                size++; // fix size
            }

            node.setValue(nodeToReplaceWith.value);

            size--;

            return true;
        }

        return false;
    }

    public boolean delete(T value) {
        return delete(root, value);
    }

    public int size() {
        return size;
    }

    private int height(Node<T> node) {
        if (node == null) {
            return 0;
        }

        return Math.max(height(node.left), height(node.right)) + 1;
    }

    public int height() {
        return height(root);
    }

    private void traversePreorder(Node<T> node, IVisitor visitor) {
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

    private void traverseInorder(Node<T> node, IVisitor visitor) {
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

    private void traversePostorder(Node<T> node, IVisitor visitor) {
        if (node == null) {
            return;
        }

        traversePostorder(node.left, visitor);
        traversePostorder(node.right, visitor);
        visitor.visit(node.value);
    }

    public List<Node<T>> flatten() {
        Stack<Node<T>> stack = new Stack<>();
        List<Node<T>> result = new LinkedList<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();

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

    private void splitToLevels(Node<T> node, List<List<T>> levels, int level) {
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
