import student.TestCase;
import static org.junit.Assert.*;
import org.junit.Test;

public class CityTest extends TestCase {

  @Test
  public void testConstructorAndGetters() {
    City c = new City("Paris", 48, 2);
    assertEquals("Paris", c.getName());
    assertEquals(48, c.getX());
    assertEquals(2, c.getY());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorRejectsNullName() {
    new City(null, 0, 0);
  }

  @Test
  public void testToStringFormat() {
    City c = new City("London", 51, -1);
    String result = c.toString();
    assertTrue("Should contain city name", result.contains("London"));
    assertTrue("Should contain x coordinate", result.contains("51"));
    assertTrue("Should contain y coordinate", result.contains("-1"));
  }

  @Test
  public void testEquals() {
    City a = new City("Tokyo", 35, 139);
    City b = new City("Tokyo", 35, 139);
    City c = new City("Tokyo", 35, 100);

    assertEquals("Cities with same name and coordinates should be equal", a, b);
    assertNotEquals("Cities with different coordinates should not be equal", a, c);
    assertTrue("Equality should be symmetric", b.equals(a));
  }

  @Test
  public void testCompareToByName() {
    City a = new City("Amsterdam", 1, 1);
    City b = new City("Berlin", 1, 1);
    assertTrue("Amsterdam should come before Berlin", a.compareTo(b) < 0);
    assertTrue("Berlin should come after Amsterdam", b.compareTo(a) > 0);
  }

  @Test
  public void testCompareToByXWhenNamesEqual() {
    City a = new City("Rome", 10, 1);
    City b = new City("Rome", 20, 1);
    assertTrue("Smaller x should come first when names are equal", a.compareTo(b) < 0);
    assertTrue("Larger x should come after", b.compareTo(a) > 0);
  }

  @Test
  public void testCompareToByYWhenNameAndXEqual() {
    City a = new City("Oslo", 10, 5);
    City b = new City("Oslo", 10, 15);
    assertTrue("Smaller y should come first when name and x are equal", a.compareTo(b) < 0);
    assertTrue("Larger y should come after", b.compareTo(a) > 0);
  }

  @Test
  public void testCompareToZeroWhenIdentical() {
    City a = new City("Lisbon", 10, 20);
    City b = new City("Lisbon", 10, 20);
    assertEquals("Identical cities should compare as 0", 0, a.compareTo(b));
  }

  @Test
  public void testEqualsConsistentWithCompareTo() {
    City a = new City("Dublin", 12, 20);
    City b = new City("Dublin", 12, 20);
    assertTrue("equals implies compareTo == 0", a.equals(b));
    assertEquals("compareTo must return 0 for equal cities", 0, a.compareTo(b));
  }
}
