/**
 * KDTree (2D) using integer coordinates only.
 * Pure linked node structure, no ADTs.
 */
public class KDTree {

    private static class Node {
        City city;
        Node left, right;

        Node(City c) {
            city = c;
        }
    }

    private Node root;

    public KDTree() {
        root = null;
    }

    /**
     * Insert city; return false if a city with identical coordinates already
     * exists.
     */
    public boolean insert(City city) {
        if (city == null || find(city.getX(), city.getY()) != null)
            return false;
        root = insertRec(root, city, 0);
        return true;
    }

    private Node insertRec(Node node, City city, int depth) {
        if (node == null)
            return new Node(city);

        int axis = depth & 1; // Replace % with bitwise AND (equivalent to %2)
        int cmp = compareByAxis(city, node.city, axis);

        // Single conditional - equal values go LEFT
        if (cmp <= 0) {
            node.left = insertRec(node.left, city, depth + 1);
        } else {
            node.right = insertRec(node.right, city, depth + 1);
        }
        return node;
    }

    /** Find city by coordinates. */
    public City find(int x, int y) {
        return findRec(root, x, y, 0);
    }

    private City findRec(Node node, int x, int y, int depth) {
        if (node == null)
            return null;
        if (node.city.getX() == x && node.city.getY() == y)
            return node.city;

        int axis = depth & 1;
        int searchValue = (axis == 0) ? x : y;
        int nodeValue = (axis == 0) ? node.city.getX() : node.city.getY();

        // Single conditional for direction
        Node nextNode = (searchValue <= nodeValue) ? node.left : node.right;
        return findRec(nextNode, x, y, depth + 1);
    }

    public String delete(int x, int y) {
        int[] visited = new int[] { 0 };
        StringBuilder name = new StringBuilder();
        root = deleteRec(root, x, y, 0, visited, name);
        if (name.length() == 0)
            return visited[0] + " ";
        return visited[0] + "\n" + name.toString();
    }

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

        if (node.city.getX() == x && node.city.getY() == y) {
            if (name.length() == 0)
                name.append(node.city.getName());
            return deleteNode(node, depth, visited);
        }

        // Single conditional for search direction
        int axis = depth & 1;
        int searchValue = (axis == 0) ? x : y;
        int nodeValue = (axis == 0) ? node.city.getX() : node.city.getY();

        if (searchValue <= nodeValue) {
            node.left = deleteRec(node.left, x, y, depth + 1, visited, name);
        } else {
            node.right = deleteRec(node.right, x, y, depth + 1, visited, name);
        }
        return node;
    }

    private Node deleteNode(Node node, int depth, int[] visited) {
        // Handle deletion cases with minimal conditionals
        if (node.right != null) {
            return replaceWithSuccessor(node, node.right, depth, visited);
        }
        if (node.left != null) {
            return replaceWithSuccessor(node, node.left, depth, visited);
        }
        return null; // Leaf node
    }

    private Node replaceWithSuccessor(
            Node node,
            Node subtree,
            int depth,
            int[] visited) {
        int axis = depth & 1;
        Node successor = findMin(subtree, axis, depth + 1);
        node.city = successor.city;

        // If we used left subtree, move it to right and clear left
        boolean usedLeft = (subtree == node.left);
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

    private Node findMin(Node node, int axis, int depth) {
        if (node == null)
            return null;

        int currentAxis = depth & 1;
        if (currentAxis == axis) {
            // Minimum must be in left subtree or current node
            Node leftMin = findMin(node.left, axis, depth + 1);
            return (leftMin != null) ? leftMin : node;
        }

        // Check all three: current, left, right
        Node leftMin = findMin(node.left, axis, depth + 1);
        Node rightMin = findMin(node.right, axis, depth + 1);
        return getMinOfThree(node, leftMin, rightMin, axis);
    }

    private Node getMinOfThree(Node a, Node b, Node c, int axis) {
        Node min = a;
        if (b != null && compareByAxis(b.city, min.city, axis) < 0)
            min = b;
        if (c != null && compareByAxis(c.city, min.city, axis) < 0)
            min = c;
        return min;
    }

    private int compareByAxis(City a, City b, int axis) {
        return (axis == 0)
                ? Integer.compare(a.getX(), b.getX())
                : Integer.compare(a.getY(), b.getY());
    }

    /** Range search within radius. */
    public String search(int x, int y, int radius) {
        if (radius < 0)
            return "";
        StringBuilder sb = new StringBuilder();
        int[] visited = { 0 };
        searchRec(root, x, y, radius, 0, sb, visited);
        sb.append(visited[0]);
        return sb.toString();
    }

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

        // Simplified traversal logic - visit both sides if they could contain
        // results
        if (diff > -radius)
            searchRec(node.left, qx, qy, radius, depth + 1, sb, visited);
        if (diff < radius)
            searchRec(node.right, qx, qy, radius, depth + 1, sb, visited);
    }

    /** Preorder print with indentation. */
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printRec(root, sb, 0);
        return sb.toString();
    }

    private void printRec(Node node, StringBuilder sb, int depth) {
        if (node == null)
            return;

        printRec(node.left, sb, depth + 1);

        sb.append(depth);
        if (depth > 0)
            sb.append("  ".repeat(depth)); // Simplified spacing
        sb.append(node.city.toString()).append("\n");

        printRec(node.right, sb, depth + 1);
    }
}
