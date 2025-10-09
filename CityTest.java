import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link City} class.
 * <p>
 * This test suite verifies the correctness of the {@code City} class’s core
 * behavior,
 * including:
 * <ul>
 * <li>Constructor validation and field initialization</li>
 * <li>{@link City#toString()} output formatting</li>
 * <li>{@link City#compareTo(City)} comparison ordering</li>
 * <li>Custom {@code equals(String)} method behavior</li>
 * <li>Standard {@code equals(Object)} method semantics</li>
 * </ul>
 * <p>
 * These tests ensure that the {@code City} class behaves correctly under
 * normal,
 * boundary, and invalid input conditions.
 * 
 * @author {Your Name}
 * @version {Put Version Here}
 */
public class CityTest {

    // ---------- Constructor Tests ----------

    /**
     * Tests that the {@link City} constructor throws an
     * {@link IllegalArgumentException}
     * when {@code null} is provided as the city name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullName() {
        new City(null, 1, 2);
    }

    /**
     * Tests that the {@link City} constructor correctly initializes fields
     * when valid arguments are provided.
     */
    @Test
    public void testConstructorValid() {
        City c = new City("Paris", 3, 4);
        assertEquals("Paris", c.getName());
        assertEquals(3, c.getX());
        assertEquals(4, c.getY());
    }

    // ---------- toString Tests ----------

    /**
     * Tests that {@link City#toString()} returns the correct string
     * representation
     * in the format: {@code "Name (x, y)"}.
     */
    @Test
    public void testToString() {
        City c = new City("Berlin", 5, 6);
        assertEquals("Berlin (5, 6)", c.toString());
    }

    // ---------- compareTo Tests ----------

    /**
     * Tests that {@link City#compareTo(City)} returns a negative value when
     * the current city's name is lexicographically less than the other city's
     * name.
     */
    @Test
    public void testCompareToLessThan() {
        City a = new City("Amsterdam", 0, 0);
        City b = new City("Zurich", 1, 1);
        assertTrue(a.compareTo(b) < 0);
    }

    /**
     * Tests that {@link City#compareTo(City)} returns a positive value when
     * the current city's name is lexicographically greater than the other
     * city's name.
     */
    @Test
    public void testCompareToGreaterThan() {
        City a = new City("Zurich", 0, 0);
        City b = new City("Amsterdam", 1, 1);
        assertTrue(a.compareTo(b) > 0);
    }

    /**
     * Tests that {@link City#compareTo(City)} returns zero when both cities
     * have the same name, regardless of coordinates.
     */
    @Test
    public void testCompareToEqual() {
        City a = new City("Paris", 2, 3);
        City b = new City("Paris", 4, 5);
        assertEquals(0, a.compareTo(b));
    }

    // ---------- equals(String) Tests ----------

    /**
     * Tests that {@link City#equals(String)} returns {@code true} when
     * the provided name matches the city's name.
     */
    @Test
    public void testEqualsStringTrue() {
        City c = new City("London", 1, 1);
        assertTrue(c.equals("London"));
    }

    /**
     * Tests that {@link City#equals(String)} returns {@code false} when
     * the provided name does not match the city's name.
     */
    @Test
    public void testEqualsStringFalse() {
        City c = new City("London", 1, 1);
        assertFalse(c.equals("Paris"));
    }

    /**
     * Tests that {@link City#equals(String)} returns {@code false} when
     * the provided string is {@code null}.
     */
    @Test
    public void testEqualsStringNull() {
        City c = new City("London", 1, 1);
        assertFalse(c.equals((String) null));
    }

    // ---------- equals(Object) Tests ----------

    /**
     * Tests that {@link City#equals(Object)} returns {@code true} when
     * the object being compared is the same instance.
     */
    @Test
    public void testEqualsSameObject() {
        City c = new City("Rome", 7, 8);
        assertTrue(c.equals(c));
    }

    /**
     * Tests that {@link City#equals(Object)} returns {@code true} when
     * two {@code City} objects have the same name and coordinates.
     * Also checks symmetry of the equality relation.
     */
    @Test
    public void testEqualsEqualObject() {
        City a = new City("Madrid", 1, 2);
        City b = new City("Madrid", 1, 2);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a)); // symmetry
    }

    /**
     * Tests that {@link City#equals(Object)} returns {@code false} when
     * two cities have different names but identical coordinates.
     */
    @Test
    public void testEqualsDifferentName() {
        City a = new City("Paris", 1, 2);
        City b = new City("Berlin", 1, 2);
        assertFalse(a.equals(b));
    }

    /**
     * Tests that {@link City#equals(Object)} returns {@code false} when
     * two cities have the same name but different x-coordinates.
     */
    @Test
    public void testEqualsDifferentX() {
        City a = new City("Paris", 1, 2);
        City b = new City("Paris", 9, 2);
        assertFalse(a.equals(b));
    }

    /**
     * Tests that {@link City#equals(Object)} returns {@code false} when
     * two cities have the same name but different y-coordinates.
     */
    @Test
    public void testEqualsDifferentY() {
        City a = new City("Paris", 1, 2);
        City b = new City("Paris", 1, 9);
        assertFalse(a.equals(b));
    }

    /**
     * Tests that {@link City#equals(Object)} returns {@code false} when
     * the object being compared is {@code null}.
     */
    @Test
    public void testEqualsNullObject() {
        City a = new City("Paris", 1, 2);
        assertFalse(a.equals(null));
    }

    /**
     * Tests that {@link City#equals(Object)} returns {@code false} when
     * the object being compared is not an instance of {@code City}.
     */
    @Test
    public void testEqualsWithNonCityObject() {
        City c = new City("Paris", 1, 2);
        Object notACity = new Object(); // generic object, not a City
        assertFalse(c.equals(notACity));
    }
}
