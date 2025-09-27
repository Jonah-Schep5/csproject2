import org.junit.Test;
import static org.junit.Assert.*;

public class CityTest {

    // --- Constructor ---
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullName() {
        new City(null, 1, 2);
    }

    @Test
    public void testConstructorValid() {
        City c = new City("Paris", 3, 4);
        assertEquals("Paris", c.getName());
        assertEquals(3, c.getX());
        assertEquals(4, c.getY());
    }

    // --- toString ---
    @Test
    public void testToString() {
        City c = new City("Berlin", 5, 6);
        assertEquals("Berlin (5, 6)", c.toString());
    }

    // --- compareTo ---
    @Test
    public void testCompareToLessThan() {
        City a = new City("Amsterdam", 0, 0);
        City b = new City("Zurich", 1, 1);
        assertTrue(a.compareTo(b) < 0);
    }

    @Test
    public void testCompareToGreaterThan() {
        City a = new City("Zurich", 0, 0);
        City b = new City("Amsterdam", 1, 1);
        assertTrue(a.compareTo(b) > 0);
    }

    @Test
    public void testCompareToEqual() {
        City a = new City("Paris", 2, 3);
        City b = new City("Paris", 4, 5);
        assertEquals(0, a.compareTo(b));
    }

    // --- equals(String) ---
    @Test
    public void testEqualsStringTrue() {
        City c = new City("London", 1, 1);
        assertTrue(c.equals("London"));
    }

    @Test
    public void testEqualsStringFalse() {
        City c = new City("London", 1, 1);
        assertFalse(c.equals("Paris"));
    }

    @Test
    public void testEqualsStringNull() {
        City c = new City("London", 1, 1);
        assertFalse(c.equals((String) null));
    }

    // --- equals(Object) ---
    @Test
    public void testEqualsSameObject() {
        City c = new City("Rome", 7, 8);
        assertTrue(c.equals(c));
    }

    @Test
    public void testEqualsEqualObject() {
        City a = new City("Madrid", 1, 2);
        City b = new City("Madrid", 1, 2);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a)); // symmetry
    }

    @Test
    public void testEqualsDifferentName() {
        City a = new City("Paris", 1, 2);
        City b = new City("Berlin", 1, 2);
        assertFalse(a.equals(b));
    }

    @Test
    public void testEqualsDifferentX() {
        City a = new City("Paris", 1, 2);
        City b = new City("Paris", 9, 2);
        assertFalse(a.equals(b));
    }

    @Test
    public void testEqualsDifferentY() {
        City a = new City("Paris", 1, 2);
        City b = new City("Paris", 1, 9);
        assertFalse(a.equals(b));
    }

    @Test
    public void testEqualsNullObject() {
        City a = new City("Paris", 1, 2);
        assertFalse(a.equals(null));
    }
    
    @Test
    public void testEqualsWithNonCityObject() {
        City c = new City("Paris", 1, 2);
        Object notACity = new Object();   // generic object, not a City
        assertFalse(c.equals(notACity));
    }
    
}
