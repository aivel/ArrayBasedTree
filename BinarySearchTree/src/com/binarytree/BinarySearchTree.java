package com.binarytree;

/**
 * Created by Max on 13.10.2015.
 */
public class BinarySearchTree<T extends Comparable> {
    private final class Node<T> {
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
    }

    public interface IVisitor<T> {
        void visit(T value);
    }

    private Node<T> root;
    private int size;

    public BinarySearchTree() {
        size = 0;
    }

    public Node<T> find(T value) {
        if (value == null || root == null) {
            return null;
        }

        Node<T> current = root;

        while (current != null) {
            int comparison = value.compareTo(current.getValue());

            switch (comparison) {
                case -1:
                    current = current.getLeft();
                    break;
                case 0:
                    return current;
                case 1:
                    current = current.getRight();
                    break;
            }
        }

        return null;
    }

    private Node<T> findParent(T value, Node<T> fromNode) {
        if (fromNode == null) {
            return null;
        }

        Node<T> left = fromNode.getLeft();
        Node<T> right = fromNode.getRight();

        if (left != null && value.equals(left.getValue())) {
            return left;
        }

        if (right != null && value.equals(right.getValue())) {
            return right;
        }

        Node<T> parent = null;

        if (left != null && value.compareTo(left.getValue()) > 0) {
            parent = findParent(value, right);
        }

        if (right != null && value.compareTo(right.getValue()) > 0) {
            parent = findParent(value, left);
        }

        return parent;
    }

    public Node<T> findParent(T value) {
        return findParent(value, root);
    }

    public boolean add(T value) {
        if (value == null) {
            return false;
        }

        Node<T> potentialParent = findParent(value);

        if (potentialParent == null) {
            root = new Node<>(value);
            size++;
            return true;
        }

        int comparison = value.compareTo(potentialParent.getValue());

        if (comparison == 0) {
            // already present in the tree
            return false;
        } else
        if (comparison < 0) {
            potentialParent.setLeft(new Node<>(value));
            size++;
            return true;
        } else {
            potentialParent.setRight(new Node<>(value));
            size++;
            return true;
        }
    }

    public boolean delete(T value) {
        Node<T> potentialParent = findParent(value);

        if (potentialParent == null) {
            if (root == null) {
                return false;
            } else {
                if (value.equals(root.getValue())) {
                    // delete root
                    // ..
                    // ..
                    return true;
                }
                return false;
            }
        }

        Node<T> left = potentialParent.getLeft();
        Node<T> right = potentialParent.getRight();

        if (left == null && right == null) {
            return false;
        }

        if (left != null) {
            if (value.equals(left.getValue())) {
                Node<T> leftLeft = left.getLeft();
                Node<T> leftRight = left.getRight();

                if (leftRight != null && leftLeft == null ||
                        leftRight == null && leftLeft != null) {
                    // One child, just remove & relink
                    potentialParent.setLeft(leftLeft == null ? leftRight : leftLeft);
                    return true;
                }
            }
        }

        if (right != null) {
            if (value.equals(right.getValue())) {
                Node<T> rightLeft = right.getLeft();
                Node<T> rightRight = right.getRight();

                if (rightRight != null && rightLeft == null ||
                        rightRight == null && rightLeft != null) {
                    // One child, just remove & relink
                    potentialParent.setLeft(rightLeft == null ? rightRight : rightLeft);
                    return true;
                }
            }
        }

        return false;
    }

    public int size() {
        return size;
    }

    public void traverse(IVisitor visitor) {
        traverse(visitor, root);
    }

    public void traverse(IVisitor visitor, Node<T> fromNode) {
        if (fromNode == null) {
            return;
        }

        traverse(visitor, fromNode.getLeft());
        visitor.visit(fromNode.getValue());
        traverse(visitor, fromNode.getRight());
    }
}
