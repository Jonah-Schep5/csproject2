/**
 * KDTree (2D) using integer coordinates only.
 * Pure linked node structure, no ADTs.
 */
public class KDTree {

    private static class Node {
        City city;
        Node left, right;
        Node(City c) { city = c; }
    }

    private Node root;

    public KDTree() { root = null; }

    /** Insert city; return false if a city with identical coordinates already exists. */
    public boolean insert(City city) {
        if (city == null) return false;
        if (find(city.getX(), city.getY()) != null) return false;
        root = insertRec(root, city, 0);
        return true;
    }

    private Node insertRec(Node node, City city, int depth) {
        if (node == null) return new Node(city);
        int axis = depth % 2;

        if (axis == 0) { // compare x
            if (city.getX() <= node.city.getX()) {
                node.left = insertRec(node.left, city, depth + 1);
            } else {
                node.right = insertRec(node.right, city, depth + 1);
            }
        } else { // compare y
            if (city.getY() <= node.city.getY()) {
                node.left = insertRec(node.left, city, depth + 1);
            } else {
                node.right = insertRec(node.right, city, depth + 1);
            }
        }
        return node;
    }

    /** Find city by coordinates. */
    public City find(int x, int y) {
        return findRec(root, x, y, 0);
    }

    private City findRec(Node node, int x, int y, int depth) {
        if (node == null) return null;
        if (node.city.getX() == x && node.city.getY() == y) {
            return node.city;
        }
        int axis = depth % 2;
        if ((axis == 0 && x <= node.city.getX()) ||
            (axis == 1 && y <= node.city.getY())) {
            return findRec(node.left, x, y, depth + 1);
        } else {
            return findRec(node.right, x, y, depth + 1);
        }
    }

    /** Delete by coordinates, return "<nodesVisited> <cityName>" or "<nodesVisited> " if not found. */
    public String delete(int x, int y) {
        int[] visited = new int[]{0};
        StringBuilder name = new StringBuilder();
        root = deleteRec(root, x, y, 0, visited, name);
        if (name.length() == 0) return visited[0] + " ";
        return visited[0] + " " + name.toString();
    }

    private Node deleteRec(Node node, int x, int y, int depth, int[] visited, StringBuilder name) {
        if (node == null) return null;
        visited[0]++;

        if (node.city.getX() == x && node.city.getY() == y) {
            if (name.length() == 0) name.append(node.city.getName());
            int axis = depth % 2;

            if (node.right != null) {
                Node min = findMin(node.right, axis, depth + 1);
                node.city = min.city;
                node.right = deleteRec(node.right, min.city.getX(), min.city.getY(), depth + 1, visited, new StringBuilder());
                return node;
            }
            else if (node.left != null) {
                Node min = findMin(node.left, axis, depth + 1);
                node.city = min.city;
                node.right = deleteRec(node.left, min.city.getX(), min.city.getY(), depth + 1, visited, new StringBuilder());
                node.left = null;
                return node;
            }
            else {
                return null;
            }
        }

        int axis = depth % 2;
        if ((axis == 0 && x <= node.city.getX()) ||
            (axis == 1 && y <= node.city.getY())) {
            node.left = deleteRec(node.left, x, y, depth + 1, visited, name);
        } else {
            node.right = deleteRec(node.right, x, y, depth + 1, visited, name);
        }
        return node;
    }

    private Node findMin(Node node, int axis, int depth) {
        if (node == null) return null;
        int nodeAxis = depth % 2;
        if (nodeAxis == axis) {
            if (node.left == null) return node;
            return findMin(node.left, axis, depth + 1);
        } else {
            Node leftMin = findMin(node.left, axis, depth + 1);
            Node rightMin = findMin(node.right, axis, depth + 1);
            Node min = node;
            if (leftMin != null && compareByAxis(leftMin.city, min.city, axis) < 0) min = leftMin;
            if (rightMin != null && compareByAxis(rightMin.city, min.city, axis) < 0) min = rightMin;
            return min;
        }
    }

    private int compareByAxis(City a, City b, int axis) {
        return (axis == 0) ? Integer.compare(a.getX(), b.getX())
                           : Integer.compare(a.getY(), b.getY());
    }

    /** Range search within radius. */
    public String search(int x, int y, int radius) {
        if (radius < 0) return "";
        StringBuilder sb = new StringBuilder();
        int[] visited = new int[]{0};
        searchRec(root, x, y, radius, 0, sb, visited);
        sb.append(visited[0]);
        return sb.toString();
    }

    private void searchRec(Node node, int qx, int qy, int radius, int depth,
                           StringBuilder sb, int[] visited) {
        if (node == null) return;
        visited[0]++;

        int dx = node.city.getX() - qx;
        int dy = node.city.getY() - qy;
        if (dx * dx + dy * dy <= radius * radius) {
            sb.append(node.city.toString()).append("\n");
        }

        int axis = depth % 2;
        int diff = (axis == 0) ? dx : dy;

        if (diff >= -radius) {
            searchRec(node.left, qx, qy, radius, depth + 1, sb, visited);
        }
        if (diff <= radius) {
            searchRec(node.right, qx, qy, radius, depth + 1, sb, visited);
        }
    }

    /** Preorder print with indentation. */
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printRec(root, sb, 0);
        return sb.toString();
    }

    private void printRec(Node node, StringBuilder sb, int depth) {
        if (node == null) return;
        sb.append(" ".repeat(depth * 2))
          .append(depth).append(" ")
          .append(node.city.toString()).append("\n");
        printRec(node.left, sb, depth + 1);
        printRec(node.right, sb, depth + 1);
    }
}
