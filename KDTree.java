/**
 * 2D kd-tree specialized to store City objects (with double x,y coordinates).
 * Public API:
 *  - void insert(City city)
 *  - boolean delete(double x, double y)   // deletes node that matches coordinates exactly (x and y)
 *  - void printTree()                      // pretty print with indentation, shows splitting axis
 *
 * Constraints met:
 *  - nodes do NOT have parent pointers
 *  - nodes do NOT store depth/axis; axis is passed in recursion
 */
public class KDTree {
    private static class KDNode {
        City city;      // stored city record (includes coordinates)
        KDNode left;    // left subtree
        KDNode right;   // right subtree

        KDNode(City c) { city = c; left = null; right = null; }
    }

    private KDNode root;

    public KDTree() { root = null; }

    /**
     * Insert a city into the kd-tree.
     * The root is at depth 0 and we alternate axis: 0 => x, 1 => y.
     */
    public void insert(City city) {
        if (city == null) throw new IllegalArgumentException("city cannot be null");
        root = insertRec(root, city, 0);
    }

    private KDNode insertRec(KDNode node, City city, int depth) {
        if (node == null) return new KDNode(city);
        int axis = depth % 2;
        if (axis == 0) {
            // compare x
            if (city.getX() <= node.city.getX()) {
                node.left = insertRec(node.left, city, depth + 1);
            } else {
                node.right = insertRec(node.right, city, depth + 1);
            }
        } else {
            // compare y
            if (city.getY() <= node.city.getY()) {
                node.left = insertRec(node.left, city, depth + 1);
            } else {
                node.right = insertRec(node.right, city, depth + 1);
            }
        }
        return node;
    }

    /**
     * Delete a city by exact coordinates (x and y must match exactly).
     * Returns true if a node was deleted.
     */
    public boolean delete(double x, double y) {
        boolean[] deleted = new boolean[]{false};
        root = deleteRec(root, x, y, 0, deleted);
        return deleted[0];
    }

    private KDNode deleteRec(KDNode node, double x, double y, int depth, boolean[] deleted) {
        if (node == null) return null;
        // Check if node matches coordinates exactly
        if (Double.compare(node.city.getX(), x) == 0 && Double.compare(node.city.getY(), y) == 0) {
            deleted[0] = true;
            // Case: node has a right subtree -> find min in splitting dimension in right, replace
            int axis = depth % 2;
            if (node.right != null) {
                KDNode min = findMin(node.right, axis, depth + 1);
                node.city = min.city;
                node.right = deleteRec(node.right, min.city.getX(), min.city.getY(), depth + 1, new boolean[]{false});
                return node;
            } else if (node.left != null) {
                // No right subtree but left exists: find min in left, replace, then move left subtree
                KDNode min = findMin(node.left, axis, depth + 1);
                node.city = min.city;
                // After replacing with min from left, we must delete that node from left subtree
                node.left = deleteRec(node.left, min.city.getX(), min.city.getY(), depth + 1, new boolean[]{false});
                // Note: Originally some kd algorithms move left subtree to right; our approach leaves subtree structure intact
                return node;
            } else {
                // leaf node: simply remove it
                return null;
            }
        }

        int axis = depth % 2;
        if (axis == 0) {
            if (x <= node.city.getX()) {
                node.left = deleteRec(node.left, x, y, depth + 1, deleted);
            } else {
                node.right = deleteRec(node.right, x, y, depth + 1, deleted);
            }
        } else {
            if (y <= node.city.getY()) {
                node.left = deleteRec(node.left, x, y, depth + 1, deleted);
            } else {
                node.right = deleteRec(node.right, x, y, depth + 1, deleted);
            }
        }
        return node;
    }

    /**
     * Find the node with minimum value in the specified dimension (0 => x, 1 => y) within subtree rooted at node.
     * 'depth' is the depth of the 'node' in the overall tree to decide which axis to compare while descending.
     */
    private KDNode findMin(KDNode node, int dim, int depth) {
        if (node == null) return null;
        int axis = depth % 2;
        if (axis == dim) {
            // when current splitting axis equals desired dimension, min must be in left subtree or node itself
            if (node.left == null) return node;
            return findMin(node.left, dim, depth + 1);
        } else {
            // when axis differs, min could be in left, right, or current node
            KDNode leftMin = findMin(node.left, dim, depth + 1);
            KDNode rightMin = findMin(node.right, dim, depth + 1);
            KDNode minNode = node;
            if (leftMin != null && compareDim(leftMin.city, minNode.city, dim) < 0) minNode = leftMin;
            if (rightMin != null && compareDim(rightMin.city, minNode.city, dim) < 0) minNode = rightMin;
            return minNode;
        }
    }

    // compare cities in given dimension: dim 0 => x, dim 1 => y
    private int compareDim(City a, City b, int dim) {
        if (dim == 0) return Double.compare(a.getX(), b.getX());
        return Double.compare(a.getY(), b.getY());
    }

    /**
     * Print the kd-tree in a readable, indented form.
     * Example output line: "[depth 2 axis=0] CityName (x, y)"
     */
    public void printTree() {
        printRec(root, 0);
    }

    private void printRec(KDNode node, int depth) {
        if (node == null) return;
        String indent = " ".repeat(Math.min(depth * 2, 200)); // small indentation safety cap
        int axis = depth % 2;
        System.out.println(indent + "[d=" + depth + " axis=" + (axis==0? "x":"y") + "] " + node.city.toString());
        printRec(node.left, depth + 1);
        printRec(node.right, depth + 1);
    }
}
