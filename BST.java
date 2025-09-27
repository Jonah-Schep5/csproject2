// ----------------------------
// Generic Binary Search Tree
// ----------------------------
class BST<T extends Comparable<T>> {

    private class Node {
        T data;
        Node left, right;
        int level;

        Node(T data, int level) {
            this.data = data;
            this.level = level;
        }
    }

    private Node root;

    // Insert: duplicates go to the LEFT (per assignment spec)
    public boolean insert(T value) {
        if (root == null) {
            root = new Node(value, 0);
            return true;
        }
        return insertRec(root, value, 0);
    }

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

    // Delete ALL matching nodes
    public boolean deleteAll(T value) {
        boolean[] deleted = { false }; // tiny trick since we can't return two values
        root = deleteRec(root, value, deleted);
        return deleted[0];
    }

    private Node deleteRec(Node curr, T value, boolean[] deleted) {
        if (curr == null)
            return null;

        // First remove matches from children (post-order)
        curr.left = deleteRec(curr.left, value, deleted);
        curr.right = deleteRec(curr.right, value, deleted);

        // Now handle current node
        int cmp = value.compareTo(curr.data);
        if (cmp == 0) {
            deleted[0] = true;

            // No child -> remove node
            if (curr.left == null && curr.right == null) {
                return null;
            }
            // One child -> replace node with child
            if (curr.left == null) {
                return curr.right;
            }
            if (curr.right == null) {
                return curr.left;
            }

            // Two children -> replace with max from left subtree (per spec)
            Node maxLeft = findMax(curr.left);
            curr.data = maxLeft.data;
            // Remove the node we copied from left subtree
            curr.left = deleteRec(curr.left, maxLeft.data, deleted);
            // Note: maxLeft.data should not be equal to `value` here because we already
            // deleted
            // all occurrences of `value` in the left subtree earlier. If in some
            // pathological
            // case maxLeft.data == value, the post-order recursion ensures it will be
            // removed
            // by the call above.
        }

        return curr;
    }

    private Node findMax(Node curr) {
        while (curr.right != null)
            curr = curr.right;
        return curr;
    }

    // Find by value (return string of matches)
    public String findAll(T value) {
        StringBuilder sb = new StringBuilder();
        findRec(root, value, sb);
        return sb.toString().trim();
    }

    private void findRec(Node curr, T value, StringBuilder sb) {
        if (curr == null)
            return;
        int cmp = value.compareTo(curr.data);
        if (cmp == 0) {
            sb.append(curr.data.toString()).append("\n");
            findRec(curr.left, value, sb);
            findRec(curr.right, value, sb);
        } else if (cmp < 0) {
            findRec(curr.left, value, sb);
        } else {
            findRec(curr.right, value, sb);
        }
    }

    // Print in-order with indentation by level
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printRec(root, sb);
        return sb.toString();
    }

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
