/**
 * A 2D KD-Tree data structure for storing and querying {@link City} objects
 * using integer coordinates.
 * <p>
 * This implementation uses a simple linked-node structure (no external data
 * structures) and supports standard KD-tree operations including:
 * <ul>
 * <li>Insertion</li>
 * <li>Find by coordinates</li>
 * <li>Deletion</li>
 * <li>Range search within a radius</li>
 * <li>In-order printing</li>
 * </ul>
 * <p>
 * Duplicate coordinates are not allowed. If two cities have the same name but
 * different coordinates, they are treated as distinct nodes.
 * <p>
 * Splitting alternates between X (depth % 2 == 0) and Y (depth % 2 == 1)
 * coordinates.
 * 
 * @author Jonah Schepers
 * @author Rowan Muhoberac
 * @version Oct 12, 2025
 */
public class KDTree {

    /**
     * Internal tree node representing a city and its children.
     */
    private static class Node {
        /** The {@link City} stored at this node. */
        City city;
        /** Left child (smaller values on the current axis). */
        Node left;
        /** Right child (larger or equal values on the current axis). */
        Node right;

        /**
         * Creates a new node with the given city.
         * 
         * @param c
         *          the city to store in this node
         */
        Node(City c) {
            city = c;
        }
    }

    /** The root node of the KD-tree (may be {@code null} if empty). */
    private Node root;

    /**
     * Constructs an empty KD-tree.
     */
    public KDTree() {
        root = null;
    }

    /**
     * Inserts a city into the KD-tree.
     * 
     * @param city
     *             the city to insert
     * @return {@code true} if the city was successfully inserted;
     *         {@code false} if {@code city} is {@code null} or a city with
     *         identical coordinates already exists
     */
    public boolean insert(City city) {
        if (city == null)
            return false;
        else if (find(city.getX(), city.getY()) != null) {
            return false;
        }
        root = insertRec(root, city, 0);
        return true;
    }

    /**
     * Recursive helper to insert a city into the tree.
     * 
     * @param node
     *              current node
     * @param city
     *              city to insert
     * @param depth
     *              current tree depth (determines axis)
     * @return the subtree root after insertion
     */
    private Node insertRec(Node node, City city, int depth) {
        if (node == null)
            return new Node(city);

        int axis = depth & 1;
        int cmp = compareByAxis(city, node.city, axis);

        // Equal values go RIGHT in KDTree
        if (cmp < 0) {
            node.left = insertRec(node.left, city, depth + 1);
        } else {
            node.right = insertRec(node.right, city, depth + 1);
        }
        return node;
    }

    /**
     * Finds a city in the KD-tree by its coordinates.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return the {@link City} if found, or {@code null} if not present
     */
    public City find(int x, int y) {
        return findRec(root, x, y, 0);
    }

    /**
     * Recursive helper to locate a city by coordinates.
     * 
     * @param node
     *              current node
     * @param x
     *              x-coordinate to search for
     * @param y
     *              y-coordinate to search for
     * @param depth
     *              current tree depth (determines axis)
     * @return the city if found, otherwise {@code null}
     */
    private City findRec(Node node, int x, int y, int depth) {
        if (node == null)
            return null;
        if (node.city.getX() == x && node.city.getY() == y)
            return node.city;

        int axis = depth & 1;
        int searchValue = (axis == 0) ? x : y;
        int nodeValue = (axis == 0) ? node.city.getX() : node.city.getY();

        // Equal values are on the RIGHT
        Node nextNode = (searchValue < nodeValue) ? node.left : node.right;
        return findRec(nextNode, x, y, depth + 1);
    }

    /**
     * Deletes a city from the KD-tree by its coordinates.
     * 
     * @param x
     *          the x-coordinate of the city to delete
     * @param y
     *          the y-coordinate of the city to delete
     * @return a string containing the number of visited nodes and, if deleted,
     *         the city's name
     */
    public String delete(int x, int y) {
        int[] visited = new int[] { 0 };
        StringBuilder name = new StringBuilder();
        root = deleteRec(root, x, y, 0, visited, name);
        if (name.length() == 0)
            return visited[0] + " ";
        return visited[0] + "\n" + name.toString();
    }

    /**
     * Recursive deletion helper.
     * 
     * @param node
     *                current node
     * @param x
     *                x-coordinate
     * @param y
     *                y-coordinate
     * @param depth
     *                current depth
     * @param visited
     *                node visit counter
     * @param name
     *                buffer to store deleted city name
     * @return the updated subtree root
     */
    private Node deleteRec(
            Node node,
            int x,
            int y,
            int depth,
            int[] visited,
            StringBuilder name) {
        if (node == null)
            return null;
        visited[0]++;

        if (node.city.getX() == x) {

            if (node.city.getY() == y) {

                name.append(node.city.getName());
                return deleteNode(node, depth, visited);
            }

        }

        int axis = depth & 1;
        int searchValue = (axis == 0) ? x : y;
        int nodeValue = (axis == 0) ? node.city.getX() : node.city.getY();

        if (searchValue < nodeValue) {
            node.left = deleteRec(node.left, x, y, depth + 1, visited, name);
        } else {
            node.right = deleteRec(node.right, x, y, depth + 1, visited, name);
        }
        return node;
    }

    /**
     * Deletes a node and rebalances the KD-tree using the standard KD deletion
     * strategy (replace with subtree minimum).
     * 
     * @param node
     *                node to delete
     * @param depth
     *                current depth
     * @param visited
     *                node visit counter
     * @return the new root of this subtree
     */
    private Node deleteNode(Node node, int depth, int[] visited) {
        if (node.right != null) {
            return replaceWithSuccessor(node, node.right, depth, visited,
                    false);
        }
        if (node.left != null) {
            return replaceWithSuccessor(node, node.left, depth, visited, true);
        }
        return null;
    }

    /**
     * Replaces the given node with the minimum node in a subtree.
     * 
     * @param node
     *                 node to replace
     * @param subtree
     *                 subtree to search for replacement
     * @param depth
     *                 current depth
     * @param visited
     *                 node visit counter
     * @param usedLeft
     *                 whether the left subtree was used for replacement
     * @return the updated node
     */
    private Node replaceWithSuccessor(
            Node node,
            Node subtree,
            int depth,
            int[] visited,
            boolean usedLeft) {
        int axis = depth & 1;
        Node successor = findMin(subtree, axis, depth + 1, visited);
        node.city = successor.city;

        if (usedLeft) {
            node.right = deleteRec(node.left, successor.city.getX(),
                    successor.city.getY(), depth + 1, visited, new StringBuilder());
            node.left = null;
        } else {
            node.right = deleteRec(node.right, successor.city.getX(),
                    successor.city.getY(), depth + 1, visited, new StringBuilder());

        }
        return node;
    }

    /**
     * Finds the node with the minimum value on the given axis in a subtree.
     * 
     * @param node
     *                subtree root
     * @param axis
     *                axis to minimize (0 for x, 1 for y)
     * @param depth
     *                current depth
     * @param visited
     *                node visit counter
     * @return node with minimum coordinate on the given axis
     */
    private Node findMin(Node node, int axis, int depth, int[] visited) {
        if (node == null)
            return null;
        visited[0]++;
        Node leftMin = findMin(node.left, axis, depth + 1, visited);
        int currentAxis = depth & 1;
        if (currentAxis == axis) {

            return (leftMin != null) ? leftMin : node;
        }

        // Node leftMin = findMin(node.left, axis, depth+1 , visited);
        Node rightMin = findMin(node.right, axis, depth + 1, visited);
        return getMinOfThree(node, leftMin, rightMin, axis);
    }

    /**
     * Returns the node with the minimum coordinate on a given axis among three
     * candidates.
     * 
     * @param a
     *             first node
     * @param b
     *             second node
     * @param c
     *             third node
     * @param axis
     *             axis to compare (0 for x, 1 for y)
     * @return node with minimum coordinate
     */
    private Node getMinOfThree(Node a, Node b, Node c, int axis) {
        Node min = a;
        if (b != null && compareByAxis(b.city, min.city, axis) < 0)
            min = b;
        if (c != null && compareByAxis(c.city, min.city, axis) < 0)
            min = c;
        return min;
    }

    /**
     * Compares two cities by a specific axis.
     * 
     * @param a
     *             first city
     * @param b
     *             second city
     * @param axis
     *             axis to compare (0 for x, 1 for y)
     * @return negative if {@code a} is smaller, positive if larger, 0 if equal
     */
    private int compareByAxis(City a, City b, int axis) {
        return (axis == 0)
                ? Integer.compare(a.getX(), b.getX())
                : Integer.compare(a.getY(), b.getY());
    }

    /**
     * Performs a range search to find all cities within a given radius of a
     * point.
     * 
     * @param x
     *               query x-coordinate
     * @param y
     *               query y-coordinate
     * @param radius
     *               search radius (must be non-negative)
     * @return a string containing matching cities followed by the number of
     *         visited nodes
     */
    public String search(int x, int y, int radius) {
        if (radius < 0)
            return "";
        StringBuilder sb = new StringBuilder();
        int[] visited = { 0 };
        searchRec(root, x, y, radius, 0, sb, visited);
        sb.append(visited[0]);
        return sb.toString();
    }

    /**
     * Recursive helper for range search.
     * 
     * @param node
     *                current node
     * @param qx
     *                query x
     * @param qy
     *                query y
     * @param radius
     *                search radius
     * @param depth
     *                current depth
     * @param sb
     *                result accumulator
     * @param visited
     *                node visit counter
     */
    private void searchRec(
            Node node,
            int qx,
            int qy,
            int radius,
            int depth,
            StringBuilder sb,
            int[] visited) {
        if (node == null)
            return;
        visited[0]++;

        int dx = node.city.getX() - qx;
        int dy = node.city.getY() - qy;
        if (dx * dx + dy * dy <= radius * radius) {
            sb.append(node.city.toString()).append("\n");
        }

        int axis = depth & 1;
        int diff = (axis == 0) ? dx : dy;

        // Visit both sides if they could contain results
        if (diff > -radius)
            searchRec(node.left, qx, qy, radius, depth + 1, sb, visited);
        if (diff <= radius)
            searchRec(node.right, qx, qy, radius, depth + 1, sb, visited);
    }

    /**
     * Returns a formatted in-order traversal of the KD-tree.
     * Each line shows the depth followed by the city's {@code toString()}.
     * 
     * @return a string representation of the tree structure
     */
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printRec(root, sb, 0);
        return sb.toString();
    }

    /**
     * Recursive helper for printing the tree structure.
     * 
     * @param node
     *              current node
     * @param sb
     *              output accumulator
     * @param depth
     *              current depth
     */
    private void printRec(Node node, StringBuilder sb, int depth) {
        if (node == null)
            return;

        printRec(node.left, sb, depth + 1);

        sb.append(depth);
        if (depth > 0)
            sb.append("  ".repeat(depth));
        sb.append(node.city.toString()).append("\n");

        printRec(node.right, sb, depth + 1);
    }
}
