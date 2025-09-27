/**
 * Simple City record used by both BST (name-index) and KDTree (coordinate-index).
 * Implements Comparable to allow BST ordering by name (primary), then x, then y for determinism.
 */
public class City implements Comparable<City> {
    // city name (key for BST)
    private final String name;
    // x coordinate (longitude / e.g.)
    private final double x;
    // y coordinate (latitude / e.g.)
    private final double y;

    public City(String name, double x, double y) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() {
        // Format similar to "City(name, x, y)"
        return String.format("%s (%.6f, %.6f)", name, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City other = (City) o;
        return name.equals(other.name)
               && Double.compare(x, other.x) == 0
               && Double.compare(y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        int h = name.hashCode();
        long lx = Double.doubleToLongBits(x);
        long ly = Double.doubleToLongBits(y);
        h = 31*h + (int)(lx ^ (lx >>> 32));
        h = 31*h + (int)(ly ^ (ly >>> 32));
        return h;
    }

    /**
     * Compare primarily by name. If names equal, compare x then y so BST ordering is deterministic.
     * This comparator will be used by the BST.
     */
    @Override
    public int compareTo(City other) {
        int c = this.name.compareTo(other.name);
        if (c != 0) return c;
        c = Double.compare(this.x, other.x);
        if (c != 0) return c;
        return Double.compare(this.y, other.y);
    }
}
