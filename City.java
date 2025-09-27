/**
 * Simple City record used by both BST (name-index) and KDTree (coordinate-index).
 * Implements Comparable to allow BST ordering by name (primary), then x, then y for determinism.
 */
public class City implements Comparable<City> {
    // city name (key for BST)
    private final String name;
    // x coordinate
    private final int x;
    // y coordinate
    private final int y;

    public City(String name, int x, int y) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { 
        return name; 
    }

    public int getX() { 
        return x; 
    }

    public int getY() { 
        return y; 
    }

    @Override
    public String toString() {
        // Format similar to "City(name, x, y)"
        return String.format("%s (%d, %d)", name, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City other = (City) o;
        return name.equals(other.name) && x == other.x && y == other.y;
    }

    /**
     * Compare primarily by name. If names equal, compare x then y so BST ordering is deterministic.
     * This comparator will be used by the BST.
     */
    @Override
    public int compareTo(City other) {
        int c = this.name.compareTo(other.name);
        if (c != 0) return c;
        c = Integer.compare(this.x, other.x);
        if (c != 0) return c;
        return Integer.compare(this.y, other.y);
    }
}
