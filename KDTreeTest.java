import student.TestCase;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * Comprehensive test suite for KDTree implementation.
 * Tests all major functionality including insertion, deletion, search,
 * range queries, and tree structure validation.
 * 
 * @author Test Suite
 * @version 1.0
 */
public class KDTreeTest extends TestCase {

  private KDTree tree;
  private City[] testCities;

  /**
   * Sets up test fixtures before each test method.
   * Creates a fresh KDTree instance and initializes test data.
   */
  @Before
  public void setUp() {
    tree = new KDTree();
    testCities = new City[] { new City("CityA", 50, 50), new City("CityB",
        25, 75), new City("CityC", 75, 25), new City("CityD", 10, 30),
        new City("CityE", 90, 80), new City("CityF", 30, 40), new City(
            "CityG", 60, 60),
        new City("Origin", 0, 0), new City("MaxPoint",
            100, 100) };
  }

  // ===== INSERTION TESTS =====

  /**
   * Tests basic insertion functionality.
   * Verifies that cities can be successfully inserted into an empty tree.
   */
  @Test
  public void testBasicInsertion() {
    assertTrue("Should insert first city", tree.insert(testCities[0]));
    assertTrue("Should insert second city", tree.insert(testCities[1]));
    assertTrue("Should insert third city", tree.insert(testCities[2]));
  }

  /**
   * Tests insertion of duplicate coordinates.
   * According to spec, cities with identical coordinates should be rejected.
   * Note: Your City.equals() considers name, x, and y, but KDTree should only
   * check coordinates.
   */
  @Test
  public void testDuplicateInsertion() {
    City city1 = new City("First", 10, 20);
    City city2 = new City("Second", 10, 20); // Same coordinates, different
                                             // name

    assertTrue("Should insert first city", tree.insert(city1));
    assertFalse("Should reject duplicate coordinates", tree.insert(city2));
  }

  /**
   * Tests that cities with same name but different coordinates are allowed.
   * KDTree should only care about coordinate uniqueness, not name uniqueness.
   */
  @Test
  public void testSameNameDifferentCoordinates() {
    City city1 = new City("SameName", 10, 20);
    City city2 = new City("SameName", 30, 40); // Same name, different
                                               // coordinates

    assertTrue("Should insert first city", tree.insert(city1));
    assertTrue(
        "Should insert city with same name but different coordinates", tree
            .insert(city2));

    // Both should be findable
    assertNotNull("Should find first city", tree.find(10, 20));
    assertNotNull("Should find second city", tree.find(30, 40));
  }

  /**
   * Tests insertion of null city.
   * Should return false for null input.
   */
  @Test
  public void testNullInsertion() {
    assertFalse("Should reject null city", tree.insert(null));
  }

  /**
   * Tests City constructor with null name.
   * Should throw IllegalArgumentException according to City implementation.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullCityName() {
    new City(null, 10, 20);
  }

  /**
   * Tests insertion with edge case coordinates.
   * Includes negative numbers, zero, and large positive numbers.
   */
  @Test
  public void testEdgeCaseInsertion() {
    City negativeCity = new City("Negative", -10, -5);
    City zeroCity = new City("Zero", 0, 0);
    City largeCity = new City("Large", 999999, 999999);

    assertTrue("Should insert negative coordinates", tree.insert(
        negativeCity));
    assertTrue("Should insert zero coordinates", tree.insert(zeroCity));
    assertTrue("Should insert large coordinates", tree.insert(largeCity));
  }

  // ===== FIND TESTS =====

  /**
   * Tests basic find functionality.
   * Verifies that inserted cities can be found by their coordinates.
   */
  @Test
  public void testBasicFind() {
    tree.insert(testCities[0]); // CityA at (50, 50)
    tree.insert(testCities[1]); // CityB at (25, 75)

    City found = tree.find(50, 50);
    assertNotNull("Should find inserted city", found);
    assertEquals("Should find correct city", "CityA", found.getName());

    found = tree.find(25, 75);
    assertNotNull("Should find second city", found);
    assertEquals("Should find correct city", "CityB", found.getName());
  }

  /**
   * Tests finding non-existent cities.
   * Should return null for coordinates that don't exist in tree.
   */
  @Test
  public void testFindNonExistent() {
    tree.insert(testCities[0]);

    City found = tree.find(999, 999);
    assertNull("Should return null for non-existent coordinates", found);

    found = tree.find(0, 0);
    assertNull("Should return null for coordinates not in tree", found);
  }

  /**
   * Tests find in empty tree.
   * Should return null when searching empty tree.
   */
  @Test
  public void testFindInEmptyTree() {
    City found = tree.find(50, 50);
    assertNull("Should return null when searching empty tree", found);
  }

  // ===== DELETE TESTS =====

  /**
   * Tests deletion of leaf nodes.
   * Simplest deletion case - nodes with no children.
   */
  @Test
  public void testDeleteLeaf() {
    tree.insert(testCities[0]); // Root
    tree.insert(testCities[1]); // Left child

    String result = tree.delete(25, 75); // Delete leaf
    assertTrue("Should return visited count and city name", result.contains(
        "CityB"));

    // Verify deletion
    City found = tree.find(25, 75);
    assertNull("Deleted city should not be found", found);

    // Root should still exist
    found = tree.find(50, 50);
    assertNotNull("Root should still exist", found);
  }

  /**
   * Tests deletion of nodes with one child.
   * Tests the replacement algorithm for single-child deletions.
   */
  @Test
  public void testDeleteNodeWithOneChild() {
    // Build tree: root(50,50) with left child(25,75) which has left
    // child(10,30)
    tree.insert(testCities[0]); // (50, 50)
    tree.insert(testCities[1]); // (25, 75)
    tree.insert(testCities[3]); // (10, 30)

    String result = tree.delete(25, 75); // Delete node with one child
    assertTrue("Should return city name", result.contains("CityB"));

    // Verify structure is maintained
    assertNull("Deleted city should not be found", tree.find(25, 75));
    assertNotNull("Root should still exist", tree.find(50, 50));
    assertNotNull("Child should still exist", tree.find(10, 30));
  }

  /**
   * Tests deletion of nodes with two children.
   * Most complex deletion case - requires finding replacement node.
   */
  @Test
  public void testDeleteNodeWithTwoChildren() {
    // Create a more complex tree
    for (int i = 0; i < 5; i++) {
      tree.insert(testCities[i]);
    }

    String result = tree.delete(50, 50); // Delete root with two children
    assertTrue("Should return city name", result.contains("CityA"));

    // Verify structure is maintained
    assertNull("Deleted city should not be found", tree.find(50, 50));
    // Other cities should still exist
    assertNotNull("Other cities should remain", tree.find(25, 75));
    assertNotNull("Other cities should remain", tree.find(75, 25));
  }

  /**
   * Tests deletion of non-existent city.
   * Should return only visited count with no city name.
   */
  @Test
  public void testDeleteNonExistent() {
    tree.insert(testCities[0]);

    String result = tree.delete(999, 999);
    assertFalse("Should not contain city name", result.trim().split(
        " ").length > 1);
    assertTrue("Should return visited count", result.matches("\\d+ ?"));
  }

  /**
   * Tests deletion from empty tree.
   * Should return "0 " indicating no nodes visited and no city found.
   */
  @Test
  public void testDeleteFromEmptyTree() {
    String result = tree.delete(50, 50);
    assertEquals("Should return '0 ' for empty tree", "0 ", result);
  }

  // ===== SEARCH TESTS =====

  /**
   * Tests basic range search functionality.
   * Verifies that cities within specified radius are found.
   */
  @Test
  public void testBasicRangeSearch() {
    tree.insert(testCities[0]); // (50, 50)
    tree.insert(testCities[1]); // (25, 75)
    tree.insert(testCities[2]); // (75, 25)

    String result = tree.search(50, 50, 30);

    // Should find the center city
    assertTrue("Should find center city", result.contains("CityA"));
    // Should end with visited count
    String[] lines = result.split("\n");
    String lastLine = lines[lines.length - 1].trim();
    assertTrue("Last line should be a number (visited count)", lastLine
        .matches("\\d+"));
  }

  /**
   * Tests range search with radius 0.
   * Should only find cities at exact coordinates.
   */
  @Test
  public void testZeroRadiusSearch() {
    tree.insert(testCities[0]); // (50, 50)
    tree.insert(testCities[1]); // (25, 75)

    String result = tree.search(50, 50, 0);

    assertTrue("Should find exact match", result.contains("CityA"));
    assertFalse("Should not find distant city", result.contains("CityB"));
  }

  /**
   * Tests range search with negative radius.
   * Should return empty string according to spec.
   */
  @Test
  public void testNegativeRadiusSearch() {
    tree.insert(testCities[0]);

    String result = tree.search(50, 50, -5);
    assertEquals("Should return empty string for negative radius", "",
        result);
  }

  /**
   * Tests range search in empty tree.
   * Should return only "0" (visited count).
   */
  @Test
  public void testSearchInEmptyTree() {
    String result = tree.search(50, 50, 100);
    assertEquals("Should return '0' for empty tree search", "0", result);
  }

  /**
   * Tests large radius search.
   * Should find all cities when radius is large enough.
   */
  @Test
  public void testLargeRadiusSearch() {
    for (int i = 0; i < 5; i++) {
      tree.insert(testCities[i]);
    }

    String result = tree.search(50, 50, 1000); // Very large radius

    // Should find multiple cities
    int cityCount = result.split("\n").length - 1; // Subtract 1 for the
                                                   // final number
    assertTrue("Should find multiple cities", cityCount >= 3);
  }

  // ===== PRINT TREE TESTS =====

  /**
   * Tests tree printing functionality.
   * Verifies the tree structure is printed with correct indentation.
   */
  @Test
  public void testPrintTree() {
    tree.insert(testCities[0]); // Root
    tree.insert(testCities[1]); // Left child
    tree.insert(testCities[2]); // Right child

    String treeStr = tree.printTree();

    assertFalse("Tree string should not be empty", treeStr.isEmpty());
    assertTrue("Should contain city names", treeStr.contains("CityA"));
    // Check that the tree contains depth numbers (0, 1, 2, etc.)
    String[] lines = treeStr.split("\n");
    boolean hasDepthNumbers = false;
    for (String line : lines) {
      if (line.trim().matches("^\\d+.*")) {
        hasDepthNumbers = true;
        break;
      }
    }
    assertTrue("Should contain lines starting with depth numbers",
        hasDepthNumbers);
  }

  /**
   * Tests printing empty tree.
   * Should return empty string.
   */
  @Test
  public void testPrintEmptyTree() {
    String treeStr = tree.printTree();
    assertEquals("Empty tree should print as empty string", "", treeStr);
  }

  // ===== INTEGRATION TESTS =====

  /**
   * Tests combined operations on the same tree.
   * Verifies that multiple operations work correctly together.
   */
  @Test
  public void testCombinedOperations() {
    // Insert several cities
    for (int i = 0; i < 6; i++) {
      assertTrue("Should insert city " + i, tree.insert(testCities[i]));
    }

    // Find some cities
    assertNotNull("Should find inserted city", tree.find(50, 50));
    assertNotNull("Should find inserted city", tree.find(25, 75));

    // Delete one city
    String deleteResult = tree.delete(75, 25);
    assertTrue("Should delete city", deleteResult.contains("CityC"));

    // Verify deletion
    assertNull("Deleted city should not be found", tree.find(75, 25));

    // Range search
    String searchResult = tree.search(50, 50, 50);
    assertFalse("Should not find deleted city", searchResult.contains(
        "CityC"));

    // Print tree
    String treeStr = tree.printTree();
    assertFalse("Tree should not be empty", treeStr.isEmpty());
    assertFalse("Tree should not contain deleted city", treeStr.contains(
        "CityC"));
  }

  /**
   * Tests tree structure after multiple insertions.
   * Verifies that the KD-tree maintains proper structure with alternating
   * axes.
   */
  @Test
  public void testTreeStructureIntegrity() {
    // Insert cities in a specific order to test structure
    City[] orderedCities = { new City("Root", 50, 50), // Root (x-axis)
        new City("LeftX", 25, 60), // Left of root (y-axis)
        new City("RightX", 75, 40), // Right of root (y-axis)
        new City("LeftLeftY", 20, 30), // Left of LeftX (x-axis)
        new City("LeftRightY", 30, 70) // Right of LeftX (x-axis)
    };

    for (City city : orderedCities) {
      assertTrue("Should insert " + city.getName(), tree.insert(city));
    }

    // Verify all cities can be found
    for (City city : orderedCities) {
      City found = tree.find(city.getX(), city.getY());
      assertNotNull("Should find " + city.getName(), found);
      assertEquals("Should find correct city", city.getName(), found
          .getName());
    }

    // Test that structure supports range queries
    String searchResult = tree.search(25, 60, 20);
    assertTrue("Range search should work on structured tree", searchResult
        .contains("LeftX"));
  }

  /**
   * Tests performance with larger dataset.
   * Ensures the tree can handle a reasonable number of cities.
   */
  @Test
  public void testLargerDataset() {
    // Insert 50 cities in a grid pattern
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 5; y++) {
        City city = new City("City_" + x + "_" + y, x * 10, y * 10);
        assertTrue("Should insert grid city", tree.insert(city));
      }
    }

    // Test finding cities from different areas
    assertNotNull("Should find corner city", tree.find(0, 0));
    assertNotNull("Should find middle city", tree.find(50, 20));
    assertNotNull("Should find far city", tree.find(90, 40));

    // Test range search in populated tree
    String result = tree.search(50, 20, 25);
    assertFalse("Range search should find cities", result.equals("50"));

    // Test deletion in populated tree
    String deleteResult = tree.delete(50, 20);
    assertTrue("Should delete from populated tree", deleteResult.contains(
        "City_5_2"));
    assertNull("Deleted city should not be found", tree.find(50, 20));
  }

  /**
   * Tests precise distance calculations in range search.
   * This test specifically targets arithmetic operations in distance
   * calculations.
   * Places cities at exact distances to catch mutation errors in dx/dy
   * calculations.
   */
  @Test
  public void testPreciseDistanceCalculations() {
    // Insert cities at specific distances to test arithmetic precision
    tree.insert(new City("Origin", 0, 0));
    tree.insert(new City("East3", 3, 0)); // Distance = 3
    tree.insert(new City("North4", 0, 4)); // Distance = 4
    tree.insert(new City("NE5", 3, 4)); // Distance = 5 (3-4-5 triangle)
    tree.insert(new City("Far", 10, 10)); // Distance = sqrt(200) ≈ 14.14

    // Search with radius 5 - should find cities at distances 3, 4, and 5
    String result = tree.search(0, 0, 5);

    assertTrue("Should find origin", result.contains("Origin"));
    assertTrue("Should find city at distance 3", result.contains("East3"));
    assertTrue("Should find city at distance 4", result.contains("North4"));
    assertTrue("Should find city at distance 5", result.contains("NE5"));
    assertFalse("Should NOT find city at distance ~14", result.contains(
        "Far"));

    // Search with radius 4 - should exclude the city at distance 5
    String result4 = tree.search(0, 0, 4);
    assertTrue("Should find cities within radius 4", result4.contains(
        "East3"));
    assertTrue("Should find cities within radius 4", result4.contains(
        "North4"));
    assertFalse("Should NOT find city at distance 5", result4.contains(
        "NE5"));
  }

  /**
   * Tests edge case distances to catch dx/dy arithmetic mutations.
   * Tests cities that are exactly on the boundary of the search radius.
   */
  @Test
  public void testBoundaryDistances() {
    tree.insert(new City("Center", 10, 10));
    tree.insert(new City("Right", 13, 10)); // dx=3, dy=0, distance=3
    tree.insert(new City("Up", 10, 13)); // dx=0, dy=3, distance=3
    tree.insert(new City("UpRight", 13, 13)); // dx=3, dy=3,
                                              // distance=sqrt(18)≈4.24

    // Radius 3 should find Right and Up, but not UpRight
    String result = tree.search(10, 10, 3);

    assertTrue("Should find center", result.contains("Center"));
    assertTrue("Should find Right (distance=3)", result.contains("Right"));
    assertTrue("Should find Up (distance=3)", result.contains("Up"));
    assertFalse("Should NOT find UpRight (distance>3)", result.contains(
        "UpRight"));

    // Radius 5 should find all cities
    String result5 = tree.search(10, 10, 5);
    assertTrue("Should find all cities within radius 5", result5.contains(
        "UpRight"));
  }

  /**
   * Tests negative coordinate differences in distance calculations.
   * Ensures that dx and dy calculations handle negative differences
   * correctly.
   */
  @Test
  public void testNegativeCoordinateDifferences() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("Left", 47, 50)); // dx=-3, dy=0
    tree.insert(new City("Down", 50, 47)); // dx=0, dy=-3
    tree.insert(new City("LeftDown", 47, 47)); // dx=-3, dy=-3

    // Search from center with radius 3
    String result = tree.search(50, 50, 3);

    assertTrue("Should find center", result.contains("Center"));
    assertTrue("Should find Left (negative dx)", result.contains("Left"));
    assertTrue("Should find Down (negative dy)", result.contains("Down"));
    assertFalse("Should NOT find LeftDown (distance>3)", result.contains(
        "LeftDown"));

    // Verify the distance calculation works with negative differences
    String result5 = tree.search(50, 50, 5);
    assertTrue("Should find LeftDown with larger radius", result5.contains(
        "LeftDown"));
  }

  /**
   * Tests range search pruning logic to catch mutations in subtree
   * exploration.
   * This test specifically targets the pruning conditions that determine
   * which subtrees to explore based on the splitting axis and radius.
   */
  @Test
  public void testRangeSearchPruning() {
    // Create a tree where pruning decisions are critical
    tree.insert(new City("Root", 50, 50)); // Root splits on X-axis
    tree.insert(new City("Left", 20, 60)); // Left child splits on Y-axis
    tree.insert(new City("Right", 80, 40)); // Right child splits on Y-axis
    tree.insert(new City("LL", 15, 55)); // Left-Left (X-axis) - closer to
                                         // search point
    tree.insert(new City("LR", 25, 85)); // Left-Right (X-axis)
    tree.insert(new City("RL", 70, 10)); // Right-Left (X-axis)
    tree.insert(new City("RR", 90, 70)); // Right-Right (X-axis)

    // Search near the left subtree - should prune right subtree
    // Distance from (25, 55): Root=√((50-25)² + (50-55)²) = √(625+25) =
    // √650 ≈ 25.5
    // Left = √((20-25)² + (60-55)²) = √(25+25) = √50 ≈ 7.07
    // LL = √((15-25)² + (55-55)²) = √(100+0) = 10
    String result = tree.search(25, 55, 15);

    assertTrue("Should find Left subtree cities", result.contains("Left"));
    assertTrue("Should find LL due to proximity (distance=10)", result
        .contains("LL"));
    assertFalse("Should NOT find Root (distance≈25.5 > 15)", result
        .contains("Root"));
    assertFalse("Should NOT find Right (too far)", result.contains(
        "Right"));
    assertFalse("Should NOT find RL (pruned)", result.contains("RL"));
    assertFalse("Should NOT find RR (pruned)", result.contains("RR"));

    // Search near the right subtree - should prune left subtree
    // Distance from (75, 45): Right = √((80-75)² + (40-45)²) = √(25+25) =
    // √50 ≈ 7.07
    String result2 = tree.search(75, 45, 15);

    assertTrue("Should find Right subtree cities", result2.contains(
        "Right"));
    assertFalse("Should NOT find Left (too far)", result2.contains("Left"));
    assertFalse("Should NOT find LL (pruned)", result2.contains("LL"));
    assertFalse("Should NOT find LR (pruned)", result2.contains("LR"));
  }

  /**
   * Tests axis-specific pruning conditions.
   * Targets mutations in the diff calculation and comparison logic.
   */
  @Test
  public void testAxisSpecificPruning() {
    // Build tree with specific structure for X and Y axis testing
    tree.insert(new City("Center", 50, 50)); // Root - X axis
    tree.insert(new City("WestFar", 10, 50)); // Far west - Y axis
    tree.insert(new City("EastFar", 90, 50)); // Far east - Y axis
    tree.insert(new City("WestNorth", 10, 65)); // West-North - X axis
                                                // (closer Y)
    tree.insert(new City("WestSouth", 10, 35)); // West-South - X axis
                                                // (closer Y)

    // Search that should explore left subtree but prune parts of it
    // Distance from (30, 50): WestFar = √((10-30)² + (50-50)²) = √(400+0) =
    // 20
    // Distance from (30, 50): WestNorth = √((10-30)² + (65-50)²) =
    // √(400+225) = √625 = 25
    // Distance from (30, 50): WestSouth = √((10-30)² + (35-50)²) =
    // √(400+225) = √625 = 25
    String result = tree.search(30, 50, 25);

    assertTrue("Should find Center", result.contains("Center"));
    assertTrue("Should find WestFar (distance = 20, within radius)", result
        .contains("WestFar"));
    assertFalse("Should NOT find EastFar (too far east)", result.contains(
        "EastFar"));

    // The key test: WestNorth and WestSouth should be found (distance = 25,
    // radius = 25)
    assertTrue("Should find WestNorth (distance = 25, within radius)",
        result.contains("WestNorth"));
    assertTrue("Should find WestSouth (distance = 25, within radius)",
        result.contains("WestSouth"));

    // Now test with smaller radius to force Y-axis pruning
    String result2 = tree.search(30, 50, 15);
    assertFalse("Should NOT find WestFar (distance = 20 > radius = 15)",
        result2.contains("WestFar"));
    assertFalse("Should NOT find WestNorth (distance = 25 > radius = 15)",
        result2.contains("WestNorth"));
    assertFalse("Should NOT find WestSouth (distance = 25 > radius = 15)",
        result2.contains("WestSouth"));
  }

  /**
   * Tests boundary conditions in pruning logic.
   * Specifically tests when diff == radius and diff == -radius.
   */
  @Test
  public void testPruningBoundaryConditions() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("LeftExact", 40, 50)); // X diff = -10
    tree.insert(new City("RightExact", 60, 50)); // X diff = +10
    tree.insert(new City("UpExact", 50, 60)); // Y diff = +10
    tree.insert(new City("DownExact", 50, 40)); // Y diff = -10

    // Search with radius exactly 10 - boundary case
    String result = tree.search(50, 50, 10);

    assertTrue("Should find Root", result.contains("Root"));
    assertTrue("Should find LeftExact (diff = -10, radius = 10)", result
        .contains("LeftExact"));
    assertTrue("Should find RightExact (diff = +10, radius = 10)", result
        .contains("RightExact"));
    assertTrue("Should find UpExact (diff = +10, radius = 10)", result
        .contains("UpExact"));
    assertTrue("Should find DownExact (diff = -10, radius = 10)", result
        .contains("DownExact"));

    // Search with radius 9 - should exclude boundary cities
    String result2 = tree.search(50, 50, 9);
    assertFalse("Should NOT find LeftExact (diff = -10 > radius = 9)",
        result2.contains("LeftExact"));
    assertFalse("Should NOT find RightExact (diff = +10 > radius = 9)",
        result2.contains("RightExact"));
  }

  /**
   * Tests the logical conditions for subtree exploration.
   * Specifically targets the >= and <= comparisons in pruning logic.
   */
  @Test
  public void testSubtreeExplorationLogic() {
    // Create asymmetric tree to test both left and right exploration
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("NearLeft", 95, 100)); // Just left of root
    tree.insert(new City("FarLeft", 70, 100)); // Far left
    tree.insert(new City("NearRight", 105, 100)); // Just right of root
    tree.insert(new City("FarRight", 130, 100)); // Far right

    // Search from a point that should explore both sides but with different
    // depths
    String result = tree.search(98, 100, 8);

    assertTrue("Should find Root", result.contains("Root"));
    assertTrue("Should find NearLeft (distance = 3)", result.contains(
        "NearLeft"));
    assertTrue("Should find NearRight (distance = 7)", result.contains(
        "NearRight"));
    assertFalse("Should NOT find FarLeft (distance = 28)", result.contains(
        "FarLeft"));
    assertFalse("Should NOT find FarRight (distance = 32)", result.contains(
        "FarRight"));

    // Test with different query point to change exploration pattern
    String result2 = tree.search(102, 100, 5);
    assertTrue("Should still find Root", result2.contains("Root"));
    assertTrue("Should find NearRight (distance = 3)", result2.contains(
        "NearRight"));
    assertFalse("Should NOT find NearLeft (distance = 7 > radius = 5)",
        result2.contains("NearLeft"));
  }

  /**
   * Tests depth calculation in tree printing (targets line 168: depth + 1).
   * Verifies that recursive calls use correct depth incrementation.
   */
  @Test
  public void testPrintTreeDepthCalculation() {
    // Build a tree with known structure and depths
    tree.insert(new City("Root", 50, 50)); // Depth 0
    tree.insert(new City("Left", 25, 25)); // Depth 1
    tree.insert(new City("Right", 75, 75)); // Depth 1
    tree.insert(new City("LL", 10, 10)); // Depth 2
    tree.insert(new City("LR", 40, 40)); // Depth 2

    String result = tree.printTree();
    String[] lines = result.split("\n");

    // Verify that we have lines with different depth indicators
    boolean hasDepth0 = false, hasDepth1 = false, hasDepth2 = false;

    for (String line : lines) {
      if (line.startsWith("0"))
        hasDepth0 = true;
      if (line.startsWith("1"))
        hasDepth1 = true;
      if (line.startsWith("2"))
        hasDepth2 = true;
    }

    assertTrue("Should have depth 0 nodes", hasDepth0);
    assertTrue("Should have depth 1 nodes", hasDepth1);
    assertTrue("Should have depth 2 nodes", hasDepth2);
  }

  /**
   * Tests indentation logic in tree printing (targets line 172: depth > 0 and
   * depth * 2).
   * Verifies correct spacing based on depth.
   */
  @Test
  public void testPrintTreeIndentation() {
    tree.insert(new City("Root", 50, 50)); // Depth 0 - no extra spaces
    tree.insert(new City("Child", 25, 25)); // Depth 1 - should have spaces
    tree.insert(new City("Grand", 10, 10)); // Depth 2 - should have more
                                            // spaces

    String result = tree.printTree();
    String[] lines = result.split("\n");

    // Find lines for each depth level
    String rootLine = null, childLine = null, grandLine = null;

    for (String line : lines) {
      if (line.contains("Root"))
        rootLine = line;
      if (line.contains("Child"))
        childLine = line;
      if (line.contains("Grand"))
        grandLine = line;
    }

    assertNotNull("Should find root line", rootLine);
    assertNotNull("Should find child line", childLine);
    assertNotNull("Should find grandchild line", grandLine);

    // Root (depth 0) should start with "0" and city name immediately
    assertTrue("Root should start with '0' and city name", rootLine.matches(
        "0Root.*"));

    // Child (depth 1) should have: "1" + (2 spaces) + city name
    assertTrue("Child should have proper indentation", childLine.matches(
        "1  Child.*"));

    // Grandchild (depth 2) should have: "2" + (4 spaces) + city name
    assertTrue("Grandchild should have more indentation", grandLine.matches(
        "2    Grand.*"));
  }

  /**
   * Tests edge cases in indentation logic.
   * Specifically tests the depth > 0 condition and depth * 2 arithmetic.
   */
  @Test
  public void testPrintTreeIndentationEdgeCases() {
    // Single node tree (depth 0 only)
    tree.insert(new City("OnlyRoot", 50, 50));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    assertEquals("Should have exactly one line", 1, lines.length);
    String rootLine = lines[0];

    // Root should be "0" + city name with NO extra spaces
    assertTrue("Root should have no extra spaces", rootLine.equals(
        "0OnlyRoot (50, 50)"));
    assertFalse("Root should not have extra spaces", rootLine.contains(
        "  ")); // Two spaces would indicate depth > 0
  }

  /**
   * Tests depth arithmetic in recursive calls (targets line 175: depth + 1).
   * Creates deeper tree to verify right subtree depth calculation.
   */
  @Test
  public void testPrintTreeRightSubtreeDepth() {
    // Create tree that will have right subtrees at various depths
    tree.insert(new City("Root", 50, 50)); // Depth 0
    tree.insert(new City("Left", 25, 25)); // Depth 1
    tree.insert(new City("Right", 75, 75)); // Depth 1
    tree.insert(new City("RightRight", 90, 90)); // Depth 2 (right of Right)
    tree.insert(new City("RRR", 95, 95)); // Depth 3 (right of RightRight)

    String result = tree.printTree();
    String[] lines = result.split("\n");

    // Verify deep right nodes have correct depth
    boolean hasDepth3 = false;
    String depth3Line = null;

    for (String line : lines) {
      if (line.startsWith("3")) {
        hasDepth3 = true;
        depth3Line = line;
        break;
      }
    }

    assertTrue("Should have depth 3 nodes", hasDepth3);
    assertNotNull("Should find depth 3 line", depth3Line);

    // Depth 3 should have "3" + (6 spaces) + city name
    assertTrue("Depth 3 should have correct indentation (6 spaces)",
        depth3Line.matches("3      .*"));
  }

  /**
   * Tests the multiplication operation in indentation (targets depth * 2).
   * Verifies that indentation scales correctly with depth.
   */
  @Test
  public void testPrintTreeIndentationScaling() {
    // Create nodes at different depths
    tree.insert(new City("D0", 50, 50)); // Depth 0: 0 extra spaces
    tree.insert(new City("D1", 25, 25)); // Depth 1: 2 extra spaces
    tree.insert(new City("D2", 10, 10)); // Depth 2: 4 extra spaces
    tree.insert(new City("D3", 5, 5)); // Depth 3: 6 extra spaces

    String result = tree.printTree();
    String[] lines = result.split("\n");

    // Check indentation scaling: depth * 2 spaces
    for (String line : lines) {
      if (line.contains("D0")) {
        // Depth 0: no spaces after the depth digit
        assertTrue("D0 should have 0 extra spaces", line.matches(
            "0D0.*"));
      } else if (line.contains("D1")) {
        // Depth 1: 2 spaces after the depth digit
        assertTrue("D1 should have 2 extra spaces", line.matches(
            "1  D1.*"));
      } else if (line.contains("D2")) {
        // Depth 2: 4 spaces after the depth digit
        assertTrue("D2 should have 4 extra spaces", line.matches(
            "2    D2.*"));
      } else if (line.contains("D3")) {
        // Depth 3: 6 spaces after the depth digit
        assertTrue("D3 should have 6 extra spaces", line.matches(
            "3      D3.*"));
      }
    }
  }

  /**
   * Tests that root node (depth 0) never gets indentation spaces.
   * This specifically targets the mutation that replaces "depth > 0" with
   * "true".
   * We need to test this in a tree with multiple nodes to see the difference.
   */
  @Test
  public void testRootNodeNoIndentation() {
    // Create tree with root and one child to see indentation difference
    tree.insert(new City("Root", 50, 50)); // Depth 0 - should have no
                                           // spaces
    tree.insert(new City("Child", 25, 25)); // Depth 1 - should have 2
                                            // spaces

    String result = tree.printTree();
    String[] lines = result.split("\n");

    // Find the root line and child line
    String rootLine = null, childLine = null;
    for (String line : lines) {
      if (line.contains("Root"))
        rootLine = line;
      if (line.contains("Child"))
        childLine = line;
    }

    assertNotNull("Should find root line", rootLine);
    assertNotNull("Should find child line", childLine);

    // Root should start immediately with "0Root" (no space between 0 and
    // Root)
    assertTrue("Root should have no spaces after depth", rootLine
        .startsWith("0Root"));
    assertFalse("Root should not have space after depth digit", rootLine
        .startsWith("0 "));

    // Child should have space(s) after depth digit
    assertTrue("Child should have spaces after depth", childLine.startsWith(
        "1  "));

    // The key difference: if depth > 0 becomes true, root would look like
    // child
    // Root line should be "0Root..." not "0 Root..."
    assertFalse(
        "Root should not have double-space pattern like child nodes",
        rootLine.matches("0\\s\\s.*"));
  }

  /**
   * Even simpler test - just verify the basic axis selection logic
   */
  @Test
  public void testDeleteAxisCalculationSimple() {
    // Minimal tree to test axis calculation
    tree.insert(new City("A", 50, 50)); // Root at depth 0 (X-axis)
    tree.insert(new City("B", 60, 30)); // Right child at depth 1 (Y-axis)
    tree.insert(new City("C", 55, 40)); // Left child of B at depth 2
                                        // (X-axis)

    /*
     * Tree:
     * A(50,50) [X-split]
     * \
     * B(60,30) [Y-split]
     * /
     * C(55,40) [X-split]
     */

    // When we delete A, findMin should look for minimum X-value in right
    // subtree
    // That should be C(55,40) since 55 < 60 on X-axis
    String result = tree.delete(50, 50);

    assertTrue("Should successfully delete A", result.contains("A"));

    // The key insight: if mutation changes depth%2 to something else,
    // it might look for minimum on wrong axis and pick wrong replacement

    // Verify tree still has the right structure
    assertNull("A should be deleted", tree.find(50, 50));

    // C should be the new root (moved from its original position)
    // We should be able to find it, and B should still exist
    boolean foundB = tree.find(60, 30) != null;
    boolean foundC = tree.find(55, 40) != null;

    assertTrue("Should find B", foundB);
    assertTrue("Should find C", foundC);

    assertEquals("Should have exactly 2 nodes", 2, (foundB ? 1 : 0)
        + (foundC ? 1 : 0));
  }

  /**
   * Tests deletion targeting name recording condition (name.length() == 0).
   * If this becomes 'true', it would record names for replacement nodes too.
   */
  @Test
  public void testDeleteNameRecordingMutation() {
    tree.insert(new City("Target", 50, 50));
    tree.insert(new City("Replacement", 60, 40));

    String result = tree.delete(50, 50);

    // Expected format: "<visitCount>\n<cityName>"
    String[] parts = result.split("\n");

    // Should have exactly 2 parts: visit count and city name
    assertEquals("Should have exactly 2 parts: count and name", 2,
        parts.length);

    // First part should be just the visit count (a number)
    assertTrue("First part should be a number", parts[0].matches("\\d+"));

    // Second part should be exactly "Target"
    assertEquals("Should record only target name", "Target", parts[1]);

    // If name.length() == 0 becomes 'true' (always), it might record
    // "Replacement" too
    assertFalse("Should not record replacement node name", result.contains(
        "Replacement"));

    // The result should contain "Target" exactly once
    String withoutTarget = result.replace("Target", "");
    int targetOccurrences = (result.length() - withoutTarget.length())
        / "Target".length();
    assertEquals("Target should appear exactly once", 1, targetOccurrences);

    // Verify the exact format
    assertTrue("Result should contain newline separator", result.contains(
        "\n"));
    assertTrue("Result should end with Target", result.endsWith("Target"));
  }

  /**
   * Tests depth + 1 mutations by creating scenarios where wrong depth breaks
   * tree structure.
   * Wrong depth in findMin calls would find wrong replacement nodes.
   */
  @Test
  public void testDeleteDepthMutation() {
    // Create asymmetric tree where depth matters for findMin
    tree.insert(new City("Root", 50, 50)); // Delete this
    tree.insert(new City("Right", 70, 30)); // Depth 1, will have children
    tree.insert(new City("RR1", 80, 40)); // Depth 2
    tree.insert(new City("RR2", 90, 20)); // Depth 2
    tree.insert(new City("RRR", 95, 25)); // Depth 3

    // Record tree state before deletion
    String beforePrint = tree.printTree();

    // Delete root - this triggers findMin(node.right, axis, depth + 1)
    String deleteResult = tree.delete(50, 50);
    assertTrue("Should delete root", deleteResult.contains("Root"));

    // Verify tree structure is still valid after deletion
    String afterPrint = tree.printTree();
    assertFalse("Root should not appear in tree anymore", afterPrint
        .contains("Root"));

    // All remaining cities should be findable
    assertNotNull("Right should be findable", tree.find(70, 30));
    assertNotNull("RR1 should be findable", tree.find(80, 40));
    assertNotNull("RR2 should be findable", tree.find(90, 20));
    assertNotNull("RRR should be findable", tree.find(95, 25));

    // Tree should still support range search correctly
    String searchResult = tree.search(80, 30, 30);
    int cityCount = searchResult.split("\n").length - 1;
    assertTrue("Should find multiple cities in range", cityCount >= 2);
  }

  /**
   * Tests deletion of node with only left subtree (triggers left->right
   * move).
   * This tests the second recursive deleteRec call with depth + 1.
   */
  @Test
  public void testDeleteLeftOnlyNodeDepthMutation() {
    // Build tree where a node has only left child
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("LeftOnly", 30, 60)); // This will have only left
                                               // child
    tree.insert(new City("LeftChild", 20, 70)); // Goes to left of LeftOnly
    tree.insert(new City("LeftGrand", 10, 80)); // Goes to left of LeftChild

    // Delete the node that has only left subtree
    String result = tree.delete(30, 60);
    assertTrue("Should delete LeftOnly", result.contains("LeftOnly"));

    // The left subtree should be moved to right, and node.left set to null
    assertNull("LeftOnly should be gone", tree.find(30, 60));
    assertNotNull("Root should remain", tree.find(50, 50));
    assertNotNull("LeftChild should remain", tree.find(20, 70));
    assertNotNull("LeftGrand should remain", tree.find(10, 80));

    // Verify tree structure with search
    String searchResult = tree.search(25, 65, 50);
    assertTrue("Should find remaining left nodes", searchResult.contains(
        "LeftChild"));

    // If depth + 1 mutation occurred, the tree structure would be wrong
    // Verify by checking that we can still insert and find new nodes
    assertTrue("Should be able to insert new node", tree.insert(new City(
        "NewNode", 25, 65)));
    assertNotNull("Should find newly inserted node", tree.find(25, 65));
  }

  /**
   * Tests visit counting to catch arithmetic mutations.
   * Wrong depth calculations might affect visit patterns.
   */
  @Test
  public void testDeleteVisitCountMutations() {
    // Create tree with predictable visit patterns
    tree.insert(new City("A", 50, 50)); // Root
    tree.insert(new City("B", 40, 60)); // Left
    tree.insert(new City("C", 60, 40)); // Right
    tree.insert(new City("D", 35, 65)); // Left-Left

    // Delete leaf node - should visit specific number of nodes
    String leafResult = tree.delete(35, 65);
    // New format: "<visitCount>\n<cityName>" - parse the first line
    int leafVisits = Integer.parseInt(leafResult.split("\n")[0]);
    assertTrue("Leaf deletion should visit at least 2 nodes",
        leafVisits >= 2);

    // Delete node with children - should visit different number
    String nodeResult = tree.delete(40, 60);
    int nodeVisits = Integer.parseInt(nodeResult.split("\n")[0]);
    assertTrue("Node deletion should visit at least 2 nodes",
        nodeVisits >= 2);

    // Visit counts should be consistent for similar operations
    tree.insert(new City("E", 30, 70)); // Add another leaf
    String secondLeafResult = tree.delete(30, 70);
    int secondLeafVisits = Integer.parseInt(secondLeafResult.split(
        "\n")[0]);

    // Similar deletions should have similar visit counts
    // If depth mutations occurred, visit patterns would be different
    assertTrue("Similar operations should have reasonable visit counts",
        Math.abs(leafVisits - secondLeafVisits) <= 2);

    // Additional validation: ensure all results have the expected format
    assertTrue("Leaf result should contain newline", leafResult.contains(
        "\n"));
    assertTrue("Node result should contain newline", nodeResult.contains(
        "\n"));
    assertTrue("Second leaf result should contain newline", secondLeafResult
        .contains("\n"));

    // Verify city names are recorded correctly
    assertTrue("Leaf result should contain city name", leafResult.endsWith(
        "D"));
    assertTrue("Node result should contain city name", nodeResult.endsWith(
        "B"));
    assertTrue("Second leaf result should contain city name",
        secondLeafResult.endsWith("E"));
  }

  /**
   * Tests specific case where wrong axis calculation affects findMin result.
   */
  @Test
  public void testDeleteFindMinAxisMutation() {
    // Create tree where findMin on wrong axis gives different result
    tree.insert(new City("Root", 50, 50)); // Will delete this
    tree.insert(new City("R1", 60, 30)); // Right subtree root
    tree.insert(new City("R2", 55, 40)); // Left child of R1 (smaller X,
                                         // larger Y)
    tree.insert(new City("R3", 65, 20)); // Right child of R1 (larger X,
                                         // smaller Y)

    // At depth 0: axis = 0 (X-axis), so findMin should find minimum X value
    // R2 has X=55, R3 has X=65, so R2 should be minimum on X-axis
    // But R3 has smaller Y value (20 vs 40)

    String beforeDelete = tree.printTree();
    String deleteResult = tree.delete(50, 50);
    String afterDelete = tree.printTree();

    assertTrue("Should delete Root", deleteResult.contains("Root"));

    // After deletion, the replacement should be based on correct axis
    // All original right subtree nodes should still be findable
    assertNotNull("R1 should be findable", tree.find(60, 30));
    assertNotNull("R2 should be findable", tree.find(55, 40));
    assertNotNull("R3 should be findable", tree.find(65, 20));

    // Tree should maintain KD-tree properties for search
    String searchResult = tree.search(60, 30, 20);
    assertTrue("Search should work correctly", searchResult.contains("R1"));
  }

  /**
   * Test that exposes arithmetic operation mutations on coordinates
   */
  @Test
  public void testDeleteArithmeticMutation() {
    // Create specific coordinates that would fail if arithmetic operations
    // are mutated
    tree.insert(new City("Root", 100, 200));
    tree.insert(new City("Successor", 150, 250));
    tree.insert(new City("Other", 160, 260));

    /*
     * If mutation changes successor.city.getX() to just first member (like
     * 0 or 150)
     * or successor.city.getY() to second member (like 0 or 250),
     * the deleteRec call will use wrong coordinates
     */

    String result = tree.delete(100, 200);
    assertTrue("Should delete Root", result.contains("Root"));

    // The successor coordinates (150, 250) must be used exactly
    // If mutated, the wrong node might be targeted for deletion
    assertNotNull("Successor should be findable", tree.find(150, 250));
    assertNotNull("Other should be findable", tree.find(160, 260));

    // Verify no extra nodes were accidentally deleted due to wrong
    // coordinates

  }

}
