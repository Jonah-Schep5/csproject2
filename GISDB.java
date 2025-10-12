// -------------------------------------------------------------------------
/**
 * Implementation of the GIS interface. This is what calls the BST and the
 * <<<<<<< HEAD
 * <<<<<<< HEAD
 * kd tree to do the work.
 *
 * @author {Your Name Here}
 * @version {Put Something Here}
 *
 */
public class GISDB implements GIS {

    /**
     * The maximum allowable value for a coordinate
     */
    public static final int MAXCOORD = 32767;

    /**
     * Dimension of the points stored in the tree
     */
    public static final int DIMENSION = 2;

    private BST<City> cityBinarySearchTree = new BST<>();
    private KDTree cityKDTree = new KDTree();

    // ----------------------------------------------------------
    /**
     * Create a new MovieRaterDB object.
     */
    GISDB() {
        cityBinarySearchTree = new BST<City>();
        cityKDTree = new KDTree();
    }

    // ----------------------------------------------------------
    /**
     * Reinitialize the database
     * 
     * @return True if the database has been cleared
     */
    public boolean clear() {
        cityBinarySearchTree = new BST<City>();
        cityKDTree = new KDTree();
        return true;
    }

    // ----------------------------------------------------------
    /**
     * A city at coordinate (x, y) with name name is entered into the database.
     * It is an error to insert two cities with identical coordinates,
     * but not an error to insert two cities with identical names.
     * 
     * @param name
     *             City name.
     * @param x
     *             City x-coordinate. Integer in the range 0 to 2^{15} − 1.
     * @param y
     *             City y-coordinate. Integer in the range 0 to 2^{15} − 1.
     * @return True iff the city is successfully entered into the database
     */
    public boolean insert(String name, int x, int y) {
        if (x < 0 || x > MAXCOORD) {
            return false;
        }
        if (y < 0 || y > MAXCOORD) {
            return false;
        }
        // Insert into both KDTREE and BST, make sure we search for the node in
        // both before inserting, ifwe find a city with identical coords, not
        // allowed.
        if (cityKDTree.find(x, y) != null) {
            return false;
        }
        City cityToAdd = new City(name, x, y);
        cityKDTree.insert(cityToAdd);
        cityBinarySearchTree.insert(cityToAdd);
        return true;
    }

    // ----------------------------------------------------------
    /**
     * The city with these coordinates is deleted from the database
     * (if it exists).
     * Print the name of the city if it exists.
     * If no city at this location exists, print the empty string.
     * 
     * @param x
     *          City x-coordinate.
     * @param y
     *          City y-coordinate.
     * @return A string with the number of nodes visited during the deletion
     *         followed by the name of the city (this is blank if nothing
     *         was deleted).
     */
    public String delete(int x, int y) {

        // Delete from KDTree
        String kdOutput = cityKDTree.delete(x, y);

        // Extract the city name from KDTree output
        String[] parts = kdOutput.trim().split("\n", 2);
        if (parts.length < 2)
            return ""; // safety
        String cityName = parts[1];

        // Delete the same city from BST
        City cityToRemove = new City(cityName, x, y);
        cityBinarySearchTree.deleteOne(cityToRemove); // generic BST deletion

        return kdOutput;
    }

    // ----------------------------------------------------------
    /**
     * The city with this name is deleted from the database (if it exists).
     * If two or more cities have this name, then ALL such cities must be
     * removed.
     * Print the coordinates of each city that is deleted.
     * If no city with this name exists, print the empty string.
     * 
     * @param name
     *             City name.
     * @return A string with the coordinates of each city that is deleted
     *         (listed in preorder as they are deleted).
     *         Print the empty string if no cites match.
     */
    public String delete(String name) {
        // Step 1: Get all matching cities from BST
        String allMatches = cityBinarySearchTree.findAll(new City(name, 0, 0));
        if (allMatches.isEmpty()) {
            return "";
        }

        // Step 2: Delete each matching city from KDTree
        String[] lines = allMatches.split("\n");
        for (String line : lines) {
            // line format: "Name (x, y)"
            int start = line.indexOf('(');
            int comma = line.indexOf(',', start);
            int end = line.indexOf(')', comma);
            int x = Integer.parseInt(line.substring(start + 1, comma).trim());
            int y = Integer.parseInt(line.substring(comma + 1, end).trim());

            cityKDTree.delete(x, y);
        }

        // Step 3: Delete all from BST
        cityBinarySearchTree.deleteAll(new City(name, 0, 0));

        return allMatches;
    }

    // ----------------------------------------------------------
    /**
     * Display the name of the city at coordinate (x, y) if it exists.
     * 
     * @param x
     *          X coordinate.
     * @param y
     *          Y coordinate.
     * @return The city name if there is such a city, empty otherwise
     */
    public String info(int x, int y) {
        if (cityKDTree.find(x, y) == null) {
            return "";
        } else {
            return cityKDTree.find(x, y).getName();
        }
    }

    // ----------------------------------------------------------
    /**
     * Display the coordinates of all cities with this name, if any exist.
     * 
     * @param name
     *             The city name.
     * @return String representing the list of cities and coordinates,
     *         empty if there are none.
     */
    public String info(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        // Temporary City object with given name, coordinates don't matter for
        // comparison
        City dummy = new City(name, 0, 0);
        return cityBinarySearchTree.findAll(dummy);
    }

    // ----------------------------------------------------------
    /**
     * All cities within radius distance from location (x, y) are listed.
     * A city that is exactly radius distance from the query point should be
     * listed.
     * This operation should be implemented so that as few nodes as possible in
     * the k-d tree are visited.
     * 
     * @param x
     *               Search circle center: X coordinate. May be negative.
     * @param y
     *               Search circle center: X coordinate. May be negative.
     * @param radius
     *               Search radius, must be non-negative.
     * @return String listing the cities found (if any) , followed by the count
     *         of the number of k-d tree nodes looked at during the
     *         search process. If the radius is bad, return an empty string.
     *         If k-d tree is empty, the number of nodes visited is zero.
     */
    public String search(int x, int y, int radius) {
        return cityKDTree.search(x, y, radius);
    }

    // ----------------------------------------------------------
    /**
     * Print a listing of the database as an inorder traversal of the k-d tree.
     * Each city should be printed on a separate line. Each line should start
     * with the level of the current node, then be indented by 2 * level spaces
     * for a node at a given level, counting the root as level 0.
     * 
     * @return String listing the cities as specified.
     */
    public String debug() {
        return cityKDTree.printTree();
    }

    // ----------------------------------------------------------
    /**
     * /**
     * Print a listing of the BST in alphabetical order (inorder traversal)
     * on the names.
     * Each city should be printed on a separate line. Each line should start
     * with the level of the current node, then be indented by 2 * level spaces
     * for a node at a given level, counting the root as level 0.
     * 
     * @return String listing the cities as specified.
     */
    public String print() {
        return cityBinarySearchTree.printTree();
    }
}
