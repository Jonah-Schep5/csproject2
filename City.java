/**
 * Represents a city with a name and 2D integer coordinates.
 * <p>
 * This class is immutable — once a {@code City} object is created,
 * its name and coordinates cannot be changed.
 * <p>
 * Cities are primarily compared by their names (lexicographically)
 * when used in sorted data structures or comparisons.
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * City c = new City("New York", 100, 200);
 * System.out.println(c.getName()); // "New York"
 * System.out.println(c); // "New York (100, 200)"
 * </pre>
 * 
 * @author Jonah Schepers
 * @author Rowan Muhoberac
 * @version Oct 12, 2025
 */
public class City implements Comparable<City> {

    /** The name of the city. Cannot be null. */
    private final String name;

    /** The x-coordinate (longitude or horizontal position) of the city. */
    private final int x;

    /** The y-coordinate (latitude or vertical position) of the city. */
    private final int y;

    /**
     * Constructs a new {@code City} with the specified name and coordinates.
     *
     * @param name
     *             the name of the city (must not be {@code null})
     * @param x
     *             the x-coordinate of the city
     * @param y
     *             the y-coordinate of the city
     * @throws IllegalArgumentException
     *                                  if {@code name} is {@code null}
     */
    public City(String name, int x, int y) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the name of this city.
     *
     * @return the name of the city
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the x-coordinate of this city.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this city.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns a string representation of this city, including its name and
     * coordinates.
     * <p>
     * Format: {@code "Name (x, y)"}.
     *
     * @return a string representation of this city
     */
    @Override
    public String toString() {
        return String.format("%s (%d, %d)", name, x, y);
    }

    /**
     * Compares this city with another based on their names, in lexicographical
     * order.
     * <p>
     * Useful for sorting or storing cities in ordered collections.
     *
     * @param other
     *              the city to be compared
     * @return a negative integer, zero, or a positive integer as this city's
     *         name
     *         is lexicographically less than, equal to, or greater than the
     *         specified city's name
     */
    @Override
    public int compareTo(City other) {
        return name.compareTo(other.name);
    }

    /**
     * Checks if this city's name equals the specified name.
     *
     * @param otherName
     *                  the name to compare with this city's name
     * @return {@code true} if the city's name equals {@code otherName};
     *         {@code false} otherwise
     */
    public boolean equals(String otherName) {
        return this.name.equals(otherName);
    }

    /**
     * Indicates whether this city is equal to another object.
     * <p>
     * Two cities are considered equal if they have the same name, x-coordinate,
     * and y-coordinate.
     *
     * @param o
     *          the object to compare with
     * @return {@code true} if the specified object is a {@code City} with the
     *         same name and coordinates;
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof City))
            return false;
        City other = (City) o;
        return name.equals(other.name) && x == other.x && y == other.y;
    }
}
