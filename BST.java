/**
 * Generic Binary Search Tree that stores values of type T which implement Comparable<T>.
 * Requirements satisfied:
 *  - inserts equal values to the left
 *  - deletion: when deleting a node with two non-null children, replace with maximum from left subtree
 *
 * Public API:
 *  - void insert(T val)
 *  - boolean delete(T val)    // returns true if a node was deleted
 *  - void printTree()         // prints in-order (sorted order)
 *
 * Note: No parent pointers are stored in nodes.
 */
public class BST<T extends Comparable<T>> {
    private static class Node<E> {
        E val;             // stored value
        Node<E> left;      // left child
        Node<E> right;     // right child

        Node(E v) { val = v; left = null; right = null; }
    }

    private Node<T> root;   // root of BST

    public BST() { root = null; }

    /**
     * Insert value into BST. Equal values go to the left subtree (per spec).
     */
    public void insert(T val) {
        if (val == null) throw new IllegalArgumentException("Cannot insert null");
        root = insertRec(root, val);
    }

    private Node<T> insertRec(Node<T> node, T val) {
        if (node == null) return new Node<>(val);
        int cmp = val.compareTo(node.val);
        if (cmp <= 0) {
            // equal values go left
            node.left = insertRec(node.left, val);
        } else {
            node.right = insertRec(node.right, val);
        }
        return node;
    }

    /**
     * Delete a value from the BST. Returns true if deletion occurred, false otherwise.
     * When deleting a node with two children, it is replaced by the maximum node from the left subtree.
     */
    public boolean delete(T val) {
        if (val == null) return false;
        // use a one-element boolean array to capture whether deletion happened during recursion
        boolean[] deleted = new boolean[]{false};
        root = deleteRec(root, val, deleted);
        return deleted[0];
    }

    private Node<T> deleteRec(Node<T> node, T val, boolean[] deleted) {
        if (node == null) return null;
        int cmp = val.compareTo(node.val);
        if (cmp < 0) {
            node.left = deleteRec(node.left, val, deleted);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, val, deleted);
        } else {
            // found node to delete
            deleted[0] = true;
            if (node.left == null && node.right == null) {
                // leaf
                return null;
            } else if (node.left == null) {
                // replace with right child
                return node.right;
            } else if (node.right == null) {
                // replace with left child
                return node.left;
            } else {
                // two children: replace with maximum from left subtree (per spec)
                Node<T> maxNode = findMax(node.left);
                node.val = maxNode.val;
                // remove the max node from left subtree
                node.left = deleteMax(node.left);
                return node;
            }
        }
        return node;
    }

    // helper to find maximum node in a (non-null) subtree
    private Node<T> findMax(Node<T> node) {
        if (node == null) return null;
        while (node.right != null) node = node.right;
        return node;
    }

    // helper to delete the maximum node in subtree and return the new subtree root
    private Node<T> deleteMax(Node<T> node) {
        if (node == null) return null;
        if (node.right == null) {
            // node is max, replace by its left child (may be null)
            return node.left;
        } else {
            node.right = deleteMax(node.right);
            return node;
        }
    }

    /**
     * Print tree in-order (sorted order). Each node prints on its own line using toString().
     */
    public void printTree() {
        printInOrder(root);
    }

    private void printInOrder(Node<T> node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.println(node.val.toString());
        printInOrder(node.right);
    }
}
