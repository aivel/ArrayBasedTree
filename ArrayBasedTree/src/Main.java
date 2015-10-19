import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by Max on 10.10.2015.
 */
public class Main {
    interface IVisitor<T> {
        public void visit(T node);
    }

    static class PrintVisitor<T> implements IVisitor<T> {
        @Override
        public void visit(T node) {
            System.out.print(node.toString());
        }
    }

    static class CalculatorVisitor implements IVisitor<String> {
        private String pendingOperation;
        private Integer currentValue;

        public CalculatorVisitor() {
            pendingOperation = null;
            currentValue = null;
        }

        @Override
        public void visit(String node) {
            if (node.equals("+")) {
                pendingOperation = "+";
            } else
            if (node.equals("*")) {
                pendingOperation = "*";
            } else
            if (node.equals("-")) {
                pendingOperation = "-";
            } else
            if (node.equals("/")) {
                pendingOperation = "/";
            } else {
                int val = Integer.parseInt(node);

                if (currentValue == null || pendingOperation == null) {
                    currentValue = val;
                } else {
                    switch (pendingOperation) {
                        case "+":
                            currentValue += val;
                            break;
                        case "-":
                            currentValue -= val;
                            break;
                        case "*":
                            currentValue *= val;
                            break;
                        case "/":
                            currentValue /= val;
                            break;
                    }
                }
            }
        }

        public int result() {
            return currentValue;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static class KAryTree<T> {
        private int degree;
        private int lastAddedIndex;
        private T[] underneath;

        public KAryTree(int degree) {
            this.degree = degree;
            lastAddedIndex = 0;
            underneath = (T[]) new Object[degree * degree];
        }

        private void init() {
            underneath = Arrays.copyOf(this.underneath, underneath.length * 2);
        }

        public int parent(int i) {
            return (degree + i - 2) / degree;
        }

        public List<Integer> getChildren(int i) {
            List<Integer> children = new LinkedList<>();

            for (int j = 0; j < degree; j++) {
                int childIndex = nthChildIndex(i, j);

                if (childIndex < 0) {
                    break;
                }

                while (childIndex >= underneath.length) {
                    init();
                }

                if (underneath[childIndex] == null) {
                    break;
                }

                children.add(childIndex);
            }

            return children;
        }

        public int nthChildIndex(int i, int j) {
            if (i == 0 && j == 0) {
                return 1;
            }

            return degree * i - (degree - 2) + j;
        }

        public void add(T value) {
            int currentIndex = 1; // root

            while (true) {
                T currentValue = underneath[currentIndex];

                if (currentValue == null) {
                    underneath[currentIndex] = value;
                    break;
                }

                List<Integer> children = getChildren(currentIndex);

                if (children.size() < 1) {
                    // Create new child
                    currentIndex = nthChildIndex(currentIndex, 0);
                } else {
                    if (children.size() >= degree) {
                        currentIndex = children.get(0);
                    } else {
                        currentIndex = nthChildIndex(currentIndex, children.size());
                    }
                }
            }
        }

        public void traverseLeftToRightTopToBot(IVisitor visitor) {
            Queue<Integer> indexesQueue = new LinkedTransferQueue<>();

            indexesQueue.add(1); // root

            while (!indexesQueue.isEmpty()) {
                int currentIndex = indexesQueue.remove();
                T currentVal = underneath[currentIndex];

                if (currentVal == null) {
                    continue;
                }

                visitor.visit(currentVal);

                indexesQueue.addAll(getChildren(currentIndex));
            }
        }

        private void traversePreorder(int node, IVisitor visitor) {
            List<Integer> children = getChildren(node);

            visitor.visit(underneath[node]);

            for (Integer child: children) {
                traversePreorder(child, visitor);
            }
        }

        public void traversePreorder(IVisitor visitor) {
            traversePreorder(1, visitor);
        }

        public void traverseInorder(int node, IVisitor visitor) {
            List<Integer> children = getChildren(node);

            for (int i = 0; i < children.size() / 2; i++) {
                traverseInorder(children.get(i), visitor);
            }

            visitor.visit(underneath[node]);

            for (int i = children.size() / 2; i < children.size(); i++) {
                traverseInorder(children.get(i), visitor);
            }
        }

        public void traverseInorder(IVisitor visitor) {
            traverseInorder(1, visitor);
        }

        public void traversePostorder(int node, IVisitor visitor) {
            List<Integer> children = getChildren(node);

            for (Integer child: children) {
                traversePreorder(child, visitor);
            }

            visitor.visit(underneath[node]);
        }

        public void traversePostorder(IVisitor visitor) {
            traversePostorder(1, visitor);
        }
    }

    public static void main(String[] args) {
        KAryTree ka = new KAryTree<String>(2);

        String input = "20 + 15 - 10 * 29";
        String[] tokens = input.split(" ");

        for (int i = 0; i < tokens.length; i++) {
            ka.add(tokens[i]);
        }

        System.out.println("\nPreorder:");
        ka.traversePreorder(new PrintVisitor<>());
        System.out.println("\nInorder:");
        ka.traverseInorder(new PrintVisitor<>());
        System.out.println("\nPostorder:");
        ka.traversePostorder(new PrintVisitor<>());
        // PF_Ring
    }
}
