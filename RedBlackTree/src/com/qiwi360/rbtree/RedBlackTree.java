package com.qiwi360.rbtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Created by Max on 01.11.2015.
 */

public class RedBlackTree<T extends Comparable> {
    private class Node {
        private static final boolean Red = true;
        private static final boolean Black = false;
        private Node parent;
        private Node left = null;
        private Node right = null;

        private T value;
        private boolean color;

        public Node(T value, Node parent) {
            // create red node by default
            this(value, parent, Red);
        }

        public Node(T value, Node parent, boolean color) {
            this.value = value;
            this.parent = parent;
            this.color = color;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;

            if (left != null) {
                left.setParent(this);
            }
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;

            if (right != null) {
                right.setParent(this);
            }
        }

        public boolean hasLeft() {
            return left != null;
        }

        public boolean hasRight() {
            return right != null;
        }

        public boolean isBlack() {
            return color == Black;
        }

        public boolean isRed() {
            return color == Red;
        }

        public void setRed() {
            color = Red;
        }

        public void setBlack() {
            color = Black;
        }

        public Node findGrandParent() {
            Node parent = getParent();

            if (parent == null) {
                return null;
            }

            return parent.getParent();
        }

        public Node findUncle() {
            Node grandParent = findGrandParent();

            if (grandParent == null) {
                return null;
            }

            if (getParent() == grandParent.left) {
                return grandParent.right;
            } else {
                return grandParent.left;
            }
        }
    }

    private Node root = null;

    private Node find(Node node, T value) {
        int compare = value.compareTo(node.getValue());

        if (compare < 0) {
            if (node.hasLeft()) {
                return find(node.getLeft(), value);
            }

            return null;
        } else if (compare > 0) {
            if (node.hasRight()) {
                return find(node.getRight(), value);
            }
            return null;
        }

        return node;
    }

    public Node find(T value) {
        return find(root, value);
    }

    public void balance(Node node) {
        if (node.getParent() == null) {
            // root node
            node.setBlack();
            return;
        }

        if (node.getParent().isBlack()) {
            // no balancing needed
            return;
        }

        if (node.getParent().isRed()) {
            // probably, double red problem
            Node uncle = node.findUncle();

            if (uncle == null || uncle.isBlack()) {
                // first case: uncle is black
                Node grandParent = node.findGrandParent();

                if (grandParent.getLeft() == node.getParent() && node.getParent().getRight() == node) {
                    //   /
                    //   \
                    rotateLeft(node.getParent());
                    node = node.getLeft();
                } else
                if (grandParent.getRight() == node.getParent() && node.getParent().getLeft() == node) {
                    //   \
                    //   /
                    rotateRight(node.getParent());
                    node = node.getRight();
                }

                grandParent = node.findGrandParent(); // reinitialize(rotations might have happen)
                node.getParent().setBlack();
                grandParent.setRed();

                if (node == node.getParent().getLeft()) {
                    rotateRight(grandParent);
                } else {
                    rotateLeft(grandParent);
                }
            } else
            if (uncle.isRed()) {
                // second case: uncle is red
                Node grandParent = node.findGrandParent();

                node.getParent().setBlack();
                grandParent.setRed();
                uncle.setBlack();

                balance(grandParent);
            }
        }
    }

    public boolean insert(T value) {
        if (root == null) {
            root = new Node(value, null);
            root.setBlack();
            return true;
        }

        Node insertedNode = insert(root, value);

        if (insertedNode != null) {
            // rebalance the tree from the node inserted
            balance(insertedNode);
            return true;
        } else {
            return false;
        }
    }

    private Node insert(Node node, T value) {
        int compare = value.compareTo(node.getValue());

        if (compare < 0) {
            // go left
            if (node.hasLeft()) {
                return insert(node.getLeft(), value);
            } else {
                Node newNode = new Node(value, node);
                node.setLeft(newNode);
                return newNode;
            }
        } else if (compare > 0) {
            // go right
            if (node.hasRight()) {
                return insert(node.getRight(), value);
            } else {
                Node newNode = new Node(value, node);
                node.setRight(newNode);
                return newNode;
            }
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

    public Node findMin() {
        return findMin(root);
    }

    public Node findMax() {
        return findMax(root);
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

    private boolean delete(Node node, T value) {
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
            Node parent = node.parent;

            // node has no children

            if (node.right == null && node.left == null) {
                if (parent == null) {
                    if (node == root) {
                        // trying to remove the root element
                        // root that has no children
                        root = null;
                        return true;
                    }

                    return false;
                }

                if (node.isRed()) {
                    // if the element to remove is red - just delete it
                    if (parent.left != null && parent.left == node) {
                        parent.setLeft(null);
                        return true;
                    }

                    if (parent.right != null && parent.right == node) {
                        parent.setRight(null);
                        return true;
                    }
                } else {
                    // TODO: REMOVE BLACK LEAF; REBALANCE NEEDED
                }

                return false;
            }

            // Node has one child

            if (node.right != null && node.left == null) {
                // right child
                // TODO: is the root removal correct in this case?
                if (parent == null) {
                    if (node == root) {
                        root = node.right;

                        return true;
                    }

                    return false;
                }


                if (parent.left != null && parent.left == node) {
                    parent.setLeft(node.right);

                    if (node.isBlack()) {
                        parent.left.setBlack();
                    } // else - black depth property is not violated

                    return true;
                }

                if (parent.right != null && parent.right == node) {
                    parent.setRight(node.right);

                    if (node.isBlack()) {
                        parent.right.setBlack();
                    } // else - black depth property is not violated

                    return true;
                }

                return false;
            }

            if (node.left != null && node.right == null) {
                // left child
                // TODO: is it okay?
                if (parent == null) {
                    if (node == root) {
                        root = node.left;

                        return true;
                    }

                    return false;
                }

                if (parent.left != null && parent.left.equals(node)) {
                    parent.setLeft(node.left);

                    if (node.isBlack()) {
                        parent.left.setBlack();
                    } // else - black depth property is not violated

                    return true;
                }

                if (parent.right != null && parent.right.equals(node)) {
                    parent.setRight(node.left);

                    if (node.isBlack()) {
                        parent.right.setBlack();
                    } // else - black depth property is not violated

                    return true;
                }

                return false;
            }

            // node has two children

            // take predecessor
            Node nodeToReplaceWith = findMax(node.left);

            if (nodeToReplaceWith == null) {
                nodeToReplaceWith = findMin(node.right);

                if (nodeToReplaceWith == null) {
                    return false;
                }
            }

            delete(nodeToReplaceWith.value);

            node.setValue(nodeToReplaceWith.value);

            return true;
        }

        return false;
    }

    public boolean delete(T value) {
        return delete(root, value);
    }

    private void rotateLeft(Node node) {
        Node grandParent = node.getParent();
        Node rightChild = node.getRight();
        Node rightChildLeft = rightChild.getLeft();

        if (grandParent != null) {
            if (grandParent.getLeft() == node)
                grandParent.setLeft(rightChild);
            else {
                grandParent.setRight(rightChild);
            }
        } else {
            root = rightChild;
            root.setParent(null);
        }

        rightChild.setLeft(node);
        node.setRight(rightChildLeft);
    }

    private void rotateRight(Node node) {
        Node grandParent = node.getParent();
        Node leftChild = node.getLeft();
        Node leftChildRight = leftChild.getRight();

        if (grandParent != null) {
            if (grandParent.getLeft() == node) {
                grandParent.setLeft(leftChild);
            } else {
                grandParent.setRight(leftChild);
            }
        } else {
            root = leftChild;
            root.setParent(null);
        }

        leftChild.setRight(node);
        node.setLeft(leftChildRight);
    }


    public static double distance(int x, int y) {
        return Math.round(Math.sqrt(x * x + y * y));
    }

    public static void main(String[] args) {
        RedBlackTree<Double> rbt = new RedBlackTree<>();
        final int pushFrequency = 60_000;

        Scanner scanner = null;

        try {
            scanner = new Scanner(new File("data.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringJoiner sj = new StringJoiner(" ");

        String toInsert = scanner.nextLine();

        if (!toInsert.isEmpty()) {
            String[] splittedString = toInsert.split(" ");

            for (int i = 0; i < splittedString.length;) {
                int x = Integer.parseInt(splittedString[i++]);
                int y = Integer.parseInt(splittedString[i++]);
                double distance = distance(x, y);

                rbt.insert(distance);

                if (i != 0 && ((i / 2) % pushFrequency == 0) || (i >= splittedString.length - 1)) {
                    RedBlackTree<Double>.Node nodeMin = rbt.findMin();
                    RedBlackTree<Double>.Node nodeMax = rbt.findMax();

                    sj.add(String.valueOf((int)Math.ceil(nodeMin.getValue())));
                    sj.add(String.valueOf((int)Math.ceil(nodeMax.getValue())));

                    if (i >= splittedString.length - 1) {
                        RedBlackTree<Double>.Node lastPoint = rbt.find(distance);
                        RedBlackTree<Double>.Node lastPointParent = lastPoint.parent;
                        RedBlackTree<Double>.Node lastPointRight = lastPoint.right;
                        RedBlackTree<Double>.Node lastPointLeft = lastPoint.left;

                        double closest = Integer.MAX_VALUE;

                        if (lastPointParent != null && lastPointParent.value < closest) {
                            closest = lastPointParent.value;
                        }

                        if (lastPointLeft != null && lastPointLeft.value < closest) {
                            closest = lastPointLeft.value;
                        }

                        if (lastPointRight != null && lastPointRight.value < closest) {
                            closest = lastPointRight.value;
                        }

                        sj.add(String.valueOf(((int)Math.ceil(closest))));
                    }
                }
            }
        }

        try {
            File file = new File("data.out");
            file.delete();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(sj.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
