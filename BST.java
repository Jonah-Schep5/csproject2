/**
 * A generic Binary Search Tree (BST) implementation that stores elements of any
 * type
 * that implements {@link Comparable}.
 * <p>
 * - Insertion rule: duplicate values are inserted into the
 * <strong>left</strong> subtree.
 * - Deletion rule: deletes <strong>all occurrences</strong> of a specified
 * value.
 * - Deletion replacement: when removing a node with two children, the node is
 * replaced with
 * the <strong>maximum value from its left subtree</strong>.
 * <p>
 * This class supports standard BST operations including insertion, deletion,
 * searching for all
 * occurrences of a value, and printing the tree with indentation based on
 * depth.
 * 
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * BST<Integer> tree = new BST<>();
 * tree.insert(10);
 * tree.insert(5);
 * tree.insert(15);
 * System.out.println(tree.printTree());
 * }</pre>
 *
 * @param <T>
 *            the type of elements stored in the BST, which must implement
 *            {@link Comparable}
 * @author {Your Name}
 * @version {Put Version Here}
 */
class BST<T extends Comparable<T>> {

    /**
     * Internal node class representing a single element in the BST.
     */
    private class Node {
        /** The data stored at this node. */
        T data;
        /** Reference to the left child node. */
        Node left;
        /** Reference to the right child node. */
        Node right;
        /** The depth level of this node in the tree (root = 0). */
        int level;

        /**
         * Constructs a new node containing the specified data.
         *
         * @param data
         *              the value to store at this node
         * @param level
         *              the depth level of the node in the tree
         */
        Node(T data, int level) {
            this.data = data;
            this.level = level;
        }
    }

    /** The root node of the binary search tree (may be {@code null}). */
    private Node root;

    /**
     * Inserts a value into the BST.
     * <p>
     * Duplicate values are inserted into the <strong>left</strong> subtree.
     *
     * @param value
     *              the value to insert
     * @return {@code true} if the value was successfully inserted,
     *         {@code false} otherwise
     */
    public boolean insert(T value) {
        if (root == null) {
            root = new Node(value, 0);
            return true;
        }
        return insertRec(root, value, 0);
    }

    /**
     * Recursive helper method for {@link #insert(Object)}.
     *
     * @param curr
     *              the current node being examined
     * @param value
     *              the value to insert
     * @param level
     *              the current depth level
     * @return {@code true} if insertion was successful, {@code false} otherwise
     */
    private boolean insertRec(Node curr, T value, int level) {
        int cmp = value.compareTo(curr.data);
        if (cmp <= 0) { // equal goes LEFT
            if (curr.left == null) {
                curr.left = new Node(value, level + 1);
                return true;
            }
            return insertRec(curr.left, value, level + 1);
        } else {
            if (curr.right == null) {
                curr.right = new Node(value, level + 1);
                return true;
            }
            return insertRec(curr.right, value, level + 1);
        }
    }

    /**
     * Deletes <strong>all</strong> occurrences of the specified value from the
     * BST.
     *
     * @param value
     *              the value to delete
     * @return {@code true} if at least one node was deleted, {@code false}
     *         otherwise
     */
    public boolean deleteAll(T value) {
        boolean[] deleted = { false }; // wrapper to track deletion result
        root = deleteRec(root, value, deleted);
        return deleted[0];
    }

    /**
     * Recursive helper method for {@link #deleteAll(Object)}.
     *
     * @param curr
     *                the current node being examined
     * @param value
     *                the value to delete
     * @param deleted
     *                flag indicating if a deletion occurred
     * @return the updated subtree root after deletion
     */
    private Node deleteRec(Node curr, T value, boolean[] deleted) {
        if (curr == null)
            return null;

        // Remove matches in children first (post-order traversal)
        curr.left = deleteRec(curr.left, value, deleted);
        curr.right = deleteRec(curr.right, value, deleted);

        // Handle current node
        int cmp = value.compareTo(curr.data);
        if (cmp == 0) {
            deleted[0] = true;

            // Case 1: no children
            if (curr.left == null && curr.right == null) {
                return null;
            }
            // Case 2: one child
            if (curr.left == null) {
                return curr.right;
            }
            if (curr.right == null) {
                return curr.left;
            }

            // Case 3: two children — replace with max from left subtree
            Node maxLeft = findMax(curr.left);
            curr.data = maxLeft.data;
            curr.left = deleteRec(curr.left, maxLeft.data, deleted);
        }

        return curr;
    }

    /**
     * Finds the node with the maximum value in a subtree.
     *
     * @param curr
     *             the root of the subtree
     * @return the node containing the maximum value
     */
    private Node findMax(Node curr) {
        while (curr.right != null)
            curr = curr.right;
        return curr;
    }

    /**
     * Finds and returns all occurrences of the specified value in the tree.
     * <p>
     * The results are returned as a newline-separated {@link String}.
     *
     * @param value
     *              the value to search for
     * @return a newline-separated list of all matching values, or an empty
     *         string if none found
     */
    public String findAll(T value) {
        StringBuilder sb = new StringBuilder();
        findAllRec(root, value, sb);
        return sb.toString().trim();
    }

    /**
     * Recursive helper method for {@link #findAll(Object)}.
     *
     * @param curr
     *              the current node being examined
     * @param value
     *              the value to find
     * @param sb
     *              the string builder accumulating results
     */
    private void findAllRec(Node curr, T value, StringBuilder sb) {
        if (curr == null)
            return;

        if (curr.data.compareTo(value) == 0) {
            sb.append(curr.data.toString()).append("\n");
        }
        findAllRec(curr.left, value, sb);
        findAllRec(curr.right, value, sb);
    }

    /**
     * Returns a string representation of the tree in <strong>in-order
     * traversal</strong>,
     * with indentation indicating the depth level of each node.
     *
     * @return a formatted string representation of the BST
     */
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printRec(root, sb);
        return sb.toString();
    }

    /**
     * Recursive helper for {@link #printTree()}, performing an in-order
     * traversal.
     *
     * @param curr
     *             the current node being examined
     * @param sb
     *             the string builder accumulating the tree representation
     */
    private void printRec(Node curr, StringBuilder sb) {
        if (curr == null)
            return;

        printRec(curr.left, sb);

        sb.append(curr.level);
        if (curr.level > 0) {
            sb.append(" ".repeat(curr.level * 2));
        }
        sb.append(curr.data).append("\n");

        printRec(curr.right, sb);
    }
}
