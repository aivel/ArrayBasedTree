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

        public void traverseRightToLeftTopToBot(IVisitor visitor) {
            Stack<Integer> indexesStack = new Stack<>();

            indexesStack.add(1); // root

            while (!indexesStack.isEmpty()) {
                int currentIndex = indexesStack.pop();
                T currentVal = underneath[currentIndex];

                if (currentVal == null) {
                    continue;
                }

                visitor.visit(currentVal);

                indexesStack.addAll(getChildren(currentIndex));
            }
        }
    }

    public static void main(String[] args) {
        KAryTree ka = new KAryTree<String>(2);

        String input = "20 + 15 - 10 * 29";
        String[] tokens = input.split(" ");

        for (int i = 0; i < tokens.length; i++) {
            ka.add(tokens[i]);
        }

        CalculatorVisitor calcVisitor = new CalculatorVisitor();

        ka.traverseRightToLeftTopToBot(calcVisitor);

        System.out.printf("Result: %s", calcVisitor.result());

        // PF_Ring
    }
}
