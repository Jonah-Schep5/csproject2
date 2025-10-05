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
        if (city == null)
            return false;
        else if(find(city.getX(), city.getY()) != null) {
            return false;
        }
        root = insertRec(root, city, 0);
        return true;
    }

    private Node insertRec(Node node, City city, int depth) {
        if (node == null)
            return new Node(city);

        int axis = depth & 1;
        int cmp = compareByAxis(city, node.city, axis);

        // Equal values go RIGHT in KDTree
        if (cmp < 0) {
            node.left = insertRec(node.left, city, depth + 1);
        } 
        else {
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

        // Equal values are on the RIGHT
        Node nextNode = (searchValue < nodeValue) ? node.left : node.right;
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

        int axis = depth & 1;
        int searchValue = (axis == 0) ? x : y;
        int nodeValue = (axis == 0) ? node.city.getX() : node.city.getY();

        // Equal values are on the RIGHT
        if (searchValue < nodeValue) {
            node.left = deleteRec(node.left, x, y, depth + 1, visited, name);
        } else {
            node.right = deleteRec(node.right, x, y, depth + 1, visited, name);
        }
        return node;
    }

    private Node deleteNode(Node node, int depth, int[] visited) {
        // KDTree: prefer RIGHT subtree, use minimum from right
        if (node.right != null) {
            return replaceWithSuccessor(node, node.right, depth, visited, false);
        }
        if (node.left != null) {
            // If only left exists, use it but move to right
            return replaceWithSuccessor(node, node.left, depth, visited, true);
        }
        return null;
    }

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
            // We used left subtree, so move it to right and clear left
            node.right = deleteRec(node.left, successor.city.getX(),
                    successor.city.getY(), depth + 1, visited, new StringBuilder());
            node.left = null;
        } else {
            // Normal case: delete from right subtree
            node.right = deleteRec(node.right, successor.city.getX(),
                    successor.city.getY(), depth + 1, visited, new StringBuilder());
        }
        return node;
    }

    private Node findMin(Node node, int axis, int depth, int[] visited) {
        if (node == null)
            return null;
        
        visited[0]++;

        int currentAxis = depth & 1;
        if (currentAxis == axis) {
            // Can only go left (or stay here)
            Node leftMin = findMin(node.left, axis, depth + 1, visited);
            return (leftMin != null) ? leftMin : node;
        }

        // Must check all three: node, left, right
        // But prefer in preorder: node first, then left, then right
        Node leftMin = findMin(node.left, axis, depth + 1, visited);
        Node rightMin = findMin(node.right, axis, depth + 1, visited);
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

        // Visit both sides if they could contain results
        if (diff > -radius)
            searchRec(node.left, qx, qy, radius, depth + 1, sb, visited);
        if (diff <= radius)
            searchRec(node.right, qx, qy, radius, depth + 1, sb, visited);
    }

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
            sb.append("  ".repeat(depth));
        sb.append(node.city.toString()).append("\n");

        printRec(node.right, sb, depth + 1);
    }
}