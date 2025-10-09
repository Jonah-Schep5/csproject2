import student.TestCase;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * Comprehensive test suite for KDTree with full line and mutation
 * coverage.
 * All expected outputs are verified against actual KDTree behavior.
 * 
 * IMPORTANT: KDTree inserts equal values to the RIGHT (not left).
 * 
 * This test suite covers:
 * - Basic insertion, deletion, and search operations
 * - Edge cases with null values, duplicates, and equal coordinates
 * - Tree structure integrity and depth calculations
 * - Distance calculations and range search pruning
 * - Mutation testing for depth arithmetic and axis switching
 * - Complex scenarios with multiple operations
 * 
 * @author Test Suite
 * @version 2.0
 */
public class KDTreeTest extends TestCase {

  /** The KDTree instance used for testing */
  private KDTree tree;

  /** Array of test cities with various coordinates */
  private City[] testCities;

  /**
   * Sets up the test fixture before each test method.
   * Initializes a new KDTree and creates an array of test cities
   * with diverse coordinate values.
   */
  @Before
  public void setUp() {
    tree = new KDTree();
    testCities = new City[] { new City("CityA", 50, 50),
        new City("CityB", 25, 75), new City("CityC", 75, 25),
        new City("CityD", 10, 30), new City("CityE", 90, 80),
        new City("CityF", 30, 40), new City("CityG", 60, 60),
        new City("Origin", 0, 0),
        new City("MaxPoint", 100, 100) };
  }

  // ===== INSERTION TESTS =====

  /**
   * Tests basic insertion of cities into the tree.
   * Verifies that cities are inserted correctly and the tree
   * structure matches the expected in-order traversal.
   */
  @Test
  public void testBasicInsertion() {
    assertTrue("Should insert first city",
        tree.insert(testCities[0]));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());

    assertTrue("Should insert second city",
        tree.insert(testCities[1]));
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n",
        tree.printTree());

    assertTrue("Should insert third city",
        tree.insert(testCities[2]));
    assertFuzzyEquals(
        "1  CityB (25, 75)\n0CityA (50, 50)\n1  CityC (75, 25)\n",
        tree.printTree());
  }

  /**
   * Tests that duplicate coordinates are rejected.
   * Two cities with identical coordinates should not both be
   * inserted.
   */
  @Test
  public void testDuplicateInsertion() {
    City city1 = new City("First", 10, 20);
    City city2 = new City("Second", 10, 20);

    assertTrue("Should insert first city", tree.insert(city1));
    assertFuzzyEquals("0First (10, 20)\n", tree.printTree());

    assertFalse("Should reject duplicate coordinates",
        tree.insert(city2));
    assertFuzzyEquals("0First (10, 20)\n", tree.printTree());
  }

  /**
   * Tests that cities with the same name but different
   * coordinates
   * can both be inserted successfully.
   */
  @Test
  public void testSameNameDifferentCoordinates() {
    City city1 = new City("SameName", 10, 20);
    City city2 = new City("SameName", 30, 40);

    assertTrue(tree.insert(city1));
    assertTrue(tree.insert(city2));
    assertFuzzyEquals("0SameName (10, 20)\n1  SameName (30, 40)\n",
        tree.printTree());

    assertNotNull(tree.find(10, 20));
    assertNotNull(tree.find(30, 40));
  }

  /**
   * Tests that null city insertion is rejected.
   * The tree should not accept null values.
   */
  @Test
  public void testNullInsertion() {
    assertFalse("Should reject null city", tree.insert(null));
    assertFuzzyEquals("", tree.printTree());
  }

  /**
   * Tests that creating a City with a null name throws an
   * exception.
   * City names must not be null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullCityName() {
    new City(null, 10, 20);
  }

  /**
   * Tests insertion of cities with edge case coordinates:
   * negative values, zero values, and very large values.
   */
  @Test
  public void testEdgeCaseInsertion() {
    City negativeCity = new City("Negative", -10, -5);
    City zeroCity = new City("Zero", 0, 0);
    City largeCity = new City("Large", 999999, 999999);

    assertTrue(tree.insert(negativeCity));
    assertTrue(tree.insert(zeroCity));
    assertTrue(tree.insert(largeCity));
    assertFuzzyEquals("0Negative (-10, -5)\n1  Zero (0, 0)\n2    "
        + "Large (999999, 999999)\n", tree.printTree());
  }

  /**
   * Tests that when a coordinate value equals the splitting
   * value,
   * the node is inserted to the RIGHT subtree (not left).
   * This is a critical implementation detail for consistency.
   */
  @Test
  public void testEqualValuesGoRight() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("EqualX", 50, 75));
    assertFuzzyEquals("0Root (50, 50)\n1  EqualX (50, 75)\n",
        tree.printTree());

    tree = new KDTree();
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 60));
    tree.insert(new City("EqualY", 30, 60));
    assertFuzzyEquals(
        "1  Left (25, 60)\n2    EqualY (30, 60)\n"
            + "0Root (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests insertion of multiple cities with equal X coordinates.
   * Verifies proper tree structure when Y-axis becomes the
   * discriminator.
   */
  @Test
  public void testMultipleEqualXValues() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 50, 60));
    tree.insert(new City("C", 50, 40));
    tree.insert(new City("D", 50, 70));
    assertFuzzyEquals("0A (50, 50)\n2    C (50, 40)\n"
        + "1  B (50, 60)\n2    D (50, 70)\n",
        tree.printTree());
  }

  // ===== FIND TESTS =====

  /**
   * Tests basic find operation to locate cities by coordinates.
   * Verifies that cities can be found after insertion and that
   * the tree structure remains unchanged after find operations.
   */
  @Test
  public void testBasicFind() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);
    tree.insert(testCities[2]);

    City found = tree.find(50, 50);
    assertNotNull(found);
    assertEquals("CityA", found.getName());

    found = tree.find(25, 75);
    assertNotNull(found);
    assertEquals("CityB", found.getName());

    assertFuzzyEquals(
        "1  CityB (25, 75)\n0CityA (50, 50)\n"
            + "1  CityC (75, 25)\n",
        tree.printTree());
  }

  /**
   * Tests that finding non-existent coordinates returns null.
   */
  @Test
  public void testFindNonExistent() {
    tree.insert(testCities[0]);
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());

    assertNull(tree.find(999, 999));
    assertNull(tree.find(0, 0));
  }

  /**
   * Tests that finding in an empty tree returns null.
   */
  @Test
  public void testFindInEmptyTree() {
    assertNull(tree.find(50, 50));
    assertFuzzyEquals("", tree.printTree());
  }

  /**
   * Tests finding cities that have equal X or Y coordinates with
   * other
   * cities.
   * Verifies correct navigation through the tree when values are
   * equal.
   */
  @Test
  public void testFindWithEqualCoordinates() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 50, 60));
    tree.insert(new City("C", 50, 40));

    City found = tree.find(50, 60);
    assertNotNull(found);
    assertEquals("B", found.getName());

    found = tree.find(50, 40);
    assertNotNull(found);
    assertEquals("C", found.getName());

    assertFuzzyEquals("0A (50, 50)\n2    C (50, 40)\n"
        + "1  B (50, 60)\n", tree.printTree());
  }

  /**
   * Tests finding a node deep in the tree.
   * Verifies that the tree can locate nodes at significant
   * depths.
   */
  @Test
  public void testFindDeepNode() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 25));
    tree.insert(new City("C", 10, 10));
    tree.insert(new City("D", 5, 5));

    City found = tree.find(5, 5);
    assertNotNull(found);
    assertEquals("D", found.getName());

    assertFuzzyEquals("3      D (5, 5)\n2    C (10, 10)\n"
        + "1  B (25, 25)\n0A (50, 50)\n", tree.printTree());
  }

  // ===== DELETE TESTS =====

  /**
   * Tests deletion of a leaf node (node with no children).
   * Verifies that the node is removed and other nodes remain.
   */
  @Test
  public void testDeleteLeaf() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);

    String result = tree.delete(25, 75);
    assertTrue(result.contains("CityB"));
    assertNull(tree.find(25, 75));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());
    assertNotNull(tree.find(50, 50));
  }

  /**
   * Tests deletion of a node with one child.
   * Verifies that the child is properly promoted to replace the
   * deleted node.
   */
  @Test
  public void testDeleteNodeWithOneChild() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);
    tree.insert(testCities[3]);

    String result = tree.delete(25, 75);
    assertTrue(result.contains("CityB"));
    assertNull(tree.find(25, 75));
    assertNotNull(tree.find(50, 50));
    assertNotNull(tree.find(10, 30));
    assertFuzzyEquals("1  CityD (10, 30)\n0CityA (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests deletion of a node with two children.
   * Verifies that the in-order successor replaces the deleted
   * node
   * and the tree structure remains valid.
   */
  @Test
  public void testDeleteNodeWithTwoChildren() {
    for (int i = 0; i < 5; i++) {
      tree.insert(testCities[i]);
    }

    String result = tree.delete(50, 50);
    assertTrue(result.contains("CityA"));
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(25, 75));
    assertNotNull(tree.find(75, 25));

    String afterDelete = tree.printTree();
    assertFalse(afterDelete.contains("CityA"));
  }

  /**
   * Tests deletion of a non-existent city.
   * The operation should fail gracefully with a count and no tree
   * changes.
   */
  @Test
  public void testDeleteNonExistent() {
    tree.insert(testCities[0]);
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());

    String result = tree.delete(999, 999);
    assertFalse(result.trim().contains("\n"));
    assertTrue(result.matches("\\d+ ?"));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());
  }

  /**
   * Tests deletion from an empty tree.
   * Should return a result indicating zero nodes were visited.
   */
  @Test
  public void testDeleteFromEmptyTree() {
    String result = tree.delete(50, 50);
    assertEquals("0 ", result);
    assertFuzzyEquals("", tree.printTree());
  }

  /**
   * Tests deletion of the root node when it's the only node.
   * The tree should become empty after deletion.
   */
  @Test
  public void testDeleteRoot() {
    tree.insert(new City("Root", 50, 50));

    String result = tree.delete(50, 50);
    assertTrue(result.contains("Root"));
    assertNull(tree.find(50, 50));
    assertFuzzyEquals("", tree.printTree());
  }

  /**
   * Tests deletion of root node with only a right subtree.
   * Verifies that the minimum node from the right subtree
   * becomes the new root.
   */
  @Test
  public void testDeleteWithRightSubtreeOnly() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Right", 70, 30));
    tree.insert(new City("RR1", 80, 40));
    tree.insert(new City("RR2", 90, 20));

    String deleteResult = tree.delete(50, 50);
    assertTrue(deleteResult.contains("Root"));
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(70, 30));
    assertNotNull(tree.find(80, 40));
    assertNotNull(tree.find(90, 20));

    String afterDelete = tree.printTree();
    assertFalse(afterDelete.contains("Root"));
  }

  /**
   * Tests deletion of root node with only a left subtree.
   * The left subtree must be converted to a right subtree.
   */
  @Test
  public void testDeleteWithLeftSubtreeOnly() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 30, 60));
    tree.insert(new City("LL", 20, 70));
    tree.insert(new City("LLL", 10, 80));

    String result = tree.delete(50, 50);
    assertTrue(result.contains("Root"));
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(30, 60));
    assertNotNull(tree.find(20, 70));
    assertNotNull(tree.find(10, 80));
  }

  /**
   * Tests deletion requiring complex replacement logic.
   * Verifies correct behavior with multiple children and
   * subtrees.
   */
  @Test
  public void testDeleteComplexReplacement() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 30, 60));
    tree.insert(new City("C", 70, 40));
    tree.insert(new City("D", 20, 50));
    tree.insert(new City("E", 40, 70));
    tree.insert(new City("F", 60, 30));
    tree.insert(new City("G", 80, 50));

    String result = tree.delete(50, 50);
    assertTrue(result.contains("A"));
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(30, 60));
    assertNotNull(tree.find(70, 40));
  }

  /**
   * Tests multiple sequential deletions.
   * Verifies that the tree maintains integrity after each
   * deletion.
   */
  @Test
  public void testMultipleDeletions() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 25));
    tree.insert(new City("C", 75, 75));
    tree.insert(new City("D", 10, 10));
    tree.insert(new City("E", 30, 30));

    tree.delete(25, 25);
    assertNull(tree.find(25, 25));
    assertNotNull(tree.find(50, 50));
    assertNotNull(tree.find(10, 10));

    tree.delete(50, 50);
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(75, 75));
  }

  // ===== SEARCH TESTS =====

  /**
   * Tests basic range search within a specified radius.
   * Verifies that cities within range are found and the count is
   * correct.
   */
  @Test
  public void testBasicRangeSearch() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);
    tree.insert(testCities[2]);

    String result = tree.search(50, 50, 30);
    assertTrue(result.contains("CityA"));
    String[] lines = result.split("\n");
    String lastLine = lines[lines.length - 1].trim();
    assertTrue(lastLine.matches("\\d+"));
    assertFuzzyEquals(
        "1  CityB (25, 75)\n0CityA (50, 50)\n"
            + "1  CityC (75, 25)\n",
        tree.printTree());
  }

  /**
   * Tests range search with zero radius.
   * Should only find the city at the exact query coordinates.
   */
  @Test
  public void testZeroRadiusSearch() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);

    String result = tree.search(50, 50, 0);
    assertTrue(result.contains("CityA"));
    assertFalse(result.contains("CityB"));
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests range search with negative radius.
   * Should return empty result as negative radius is invalid.
   */
  @Test
  public void testNegativeRadiusSearch() {
    tree.insert(testCities[0]);
    assertEquals("", tree.search(50, 50, -5));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());
  }

  /**
   * Tests range search in an empty tree.
   * Should return a count of zero nodes visited.
   */
  @Test
  public void testSearchInEmptyTree() {
    assertEquals("0", tree.search(50, 50, 100));
    assertFuzzyEquals("", tree.printTree());
  }

  /**
   * Tests range search with very large radius.
   * Should find all cities in the tree.
   */
  @Test
  public void testLargeRadiusSearch() {
    for (int i = 0; i < 5; i++) {
      tree.insert(testCities[i]);
    }

    String result = tree.search(50, 50, 1000);
    int cityCount = result.split("\n").length - 1;
    assertTrue(cityCount >= 3);
    assertFuzzyEquals("2    CityD (10, 30)\n1  CityB (25, 75)\n"
        + "0CityA (50, 50)\n1  CityC (75, 25)\n"
        + "2    CityE (90, 80)\n", tree.printTree());
  }

  /**
   * Tests that search properly prunes branches outside the search
   * radius.
   * Verifies that far nodes are not included in results.
   */
  @Test
  public void testSearchPruning() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("Far", 200, 200));
    tree.insert(new City("Near", 55, 55));

    String result = tree.search(50, 50, 10);
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Near"));
    assertFalse(result.contains("Far"));
  }

  /**
   * Tests that search explores both sides of the tree when
   * necessary.
   * Verifies correct behavior when query point is near the
   * splitting plane.
   */
  @Test
  public void testSearchBothSidesOfTree() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("Left", 30, 50));
    tree.insert(new City("Right", 70, 50));
    tree.insert(new City("LL", 25, 50));
    tree.insert(new City("RR", 75, 50));

    String result = tree.search(50, 50, 25);
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Left"));
    assertTrue(result.contains("Right"));
    assertTrue(result.contains("LL"));
    assertTrue(result.contains("RR"));
  }

  // ===== PRINT TREE TESTS =====

  /**
   * Tests printing a tree with a single node.
   * Verifies correct format with depth 0 and no indentation.
   */
  @Test
  public void testPrintSingleNode() {
    tree.insert(new City("Solo", 50, 50));
    assertFuzzyEquals("0Solo (50, 50)\n", tree.printTree());
  }

  /**
   * Tests printing a tree with three nodes (root and two
   * children).
   * Verifies correct in-order traversal format.
   */
  @Test
  public void testPrintThreeNodes() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 25));
    tree.insert(new City("Right", 75, 75));
    assertFuzzyEquals(
        "1  Left (25, 25)\n0Root (50, 50)\n"
            + "1  Right (75, 75)\n",
        tree.printTree());
  }

  /**
   * Tests printing a tree with multiple depth levels.
   * Verifies that depth numbers and indentation are correct.
   */
  @Test
  public void testPrintMultipleDepths() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 75));
    tree.insert(new City("C", 75, 25));
    tree.insert(new City("D", 10, 30));
    assertFuzzyEquals("2    D (10, 30)\n1  B (25, 75)\n"
        + "0A (50, 50)\n1  C (75, 25)\n", tree.printTree());
  }

  /**
   * Tests comprehensive print scenario with complex tree
   * structure.
   * Includes multiple depths and duplicate names with different
   * coordinates.
   */
  @Test
  public void testPrintComprehensive() {
    tree.insert(new City("Chicago", 100, 150));
    tree.insert(new City("Atlanta", 10, 500));
    tree.insert(new City("Tacoma", 1000, 100));
    tree.insert(new City("Baltimore", 0, 300));
    tree.insert(new City("Washington", 5, 350));
    assertFalse(tree.insert(new City("X", 100, 150)));
    tree.insert(new City("L", 101, 150));
    tree.insert(new City("L", 11, 500));
    assertFuzzyEquals("2    Baltimore (0, 300)\n"
        + "3      Washington (5, 350)\n1  Atlanta (10, 500)\n"
        + "2    L (11, 500)\n0Chicago (100, 150)\n"
        + "1  Tacoma (1000, 100)\n2    L (101, 150)\n",
        tree.printTree());
  }

  /**
   * Tests printing a deep left-leaning subtree.
   * Verifies correct indentation at multiple levels.
   */
  @Test
  public void testPrintDeepLeftSubtree() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("L1", 50, 150));
    tree.insert(new City("L2", 25, 125));
    tree.insert(new City("L3", 10, 110));
    tree.insert(new City("L4", 5, 105));
    assertFuzzyEquals("4        L4 (5, 105)\n3      L3 (10, 110)\n"
        + "2    L2 (25, 125)\n1  L1 (50, 150)\n"
        + "0Root (100, 100)\n", tree.printTree());
  }

  /**
   * Tests printing a balanced tree structure.
   * Verifies correct ordering and indentation for balanced tree.
   */
  @Test
  public void testPrintBalancedTree() {
    tree.insert(new City("M", 50, 50));
    tree.insert(new City("A", 25, 25));
    tree.insert(new City("Z", 75, 75));
    tree.insert(new City("D", 10, 40));
    tree.insert(new City("B", 30, 10));
    tree.insert(new City("X", 60, 80));
    tree.insert(new City("Y", 80, 60));
    assertFuzzyEquals("2    B (30, 10)\n1  A (25, 25)\n"
        + "2    D (10, 40)\n0M (50, 50)\n2    Y (80, 60)\n"
        + "1  Z (75, 75)\n2    X (60, 80)\n",
        tree.printTree());
  }

  /**
   * Tests printing tree structure after a deletion.
   * Verifies that deleted node is not shown and structure is
   * correct.
   */
  @Test
  public void testPrintAfterDeletion() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 75));
    tree.insert(new City("C", 75, 25));
    tree.insert(new City("D", 10, 30));
    tree.delete(25, 75);
    assertFuzzyEquals("1  D (10, 30)\n0A (50, 50)\n"
        + "1  C (75, 25)\n", tree.printTree());
  }

  /**
   * Tests printing an empty tree.
   * Should return an empty string.
   */
  @Test
  public void testPrintEmptyTree() {
    assertEquals("", tree.printTree());
  }

  // ===== DISTANCE CALCULATION TESTS =====

  /**
   * Tests precise distance calculations in range search.
   * Uses known coordinate patterns (3-4-5 right triangle) to
   * verify
   * exact distance computation.
   */
  @Test
  public void testPreciseDistanceCalculations() {
    tree.insert(new City("Origin", 0, 0));
    tree.insert(new City("East3", 3, 0));
    tree.insert(new City("North4", 0, 4));
    tree.insert(new City("NE5", 3, 4));
    tree.insert(new City("Far", 10, 10));

    String result = tree.search(0, 0, 5);
    assertTrue(result.contains("Origin"));
    assertTrue(result.contains("East3"));
    assertTrue(result.contains("North4"));
    assertTrue(result.contains("NE5"));
    assertFalse(result.contains("Far"));
    assertFuzzyEquals("0Origin (0, 0)\n1  East3 (3, 0)\n"
        + "2    North4 (0, 4)\n3      NE5 (3, 4)\n"
        + "4        Far (10, 10)\n", tree.printTree());
  }

  /**
   * Tests boundary distance calculations.
   * Verifies correct inclusion/exclusion at exact radius
   * boundary.
   */
  @Test
  public void testBoundaryDistances() {
    tree.insert(new City("Center", 10, 10));
    tree.insert(new City("Right", 13, 10));
    tree.insert(new City("Up", 10, 13));
    tree.insert(new City("UpRight", 13, 13));

    String result = tree.search(10, 10, 3);
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Right"));
    assertTrue(result.contains("Up"));
    assertFalse(result.contains("UpRight"));
    assertFuzzyEquals("0Center (10, 10)\n1  Right (13, 10)\n"
        + "2    Up (10, 13)\n3      UpRight (13, 13)\n",
        tree.printTree());
  }

  /**
   * Tests distance calculations with negative coordinate
   * differences.
   * Verifies that absolute values are used correctly in distance
   * formula.
   */
  @Test
  public void testNegativeCoordinateDifferences() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("Left", 47, 50));
    tree.insert(new City("Down", 50, 47));
    tree.insert(new City("LeftDown", 47, 47));

    String result = tree.search(50, 50, 3);
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Left"));
    assertTrue(result.contains("Down"));
    assertFalse(result.contains("LeftDown"));
    assertFuzzyEquals("2    LeftDown (47, 47)\n"
        + "1  Left (47, 50)\n0Center (50, 50)\n"
        + "1  Down (50, 47)\n", tree.printTree());
  }

  /**
   * Tests exact radius boundary condition.
   * Verifies cities at exactly the radius distance are included.
   */
  @Test
  public void testExactRadiusBoundary() {
    tree.insert(new City("Center", 0, 0));
    tree.insert(new City("Distance5", 3, 4));
    tree.insert(new City("Distance6", 0, 6));

    String result = tree.search(0, 0, 5);
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Distance5"));
    assertFalse(result.contains("Distance6"));
  }

  // ===== PRUNING LOGIC TESTS =====

  /**
   * Tests that range search correctly prunes branches during
   * traversal.
   * When the search radius doesn't intersect with a subtree's
   * region,
   * that subtree should be skipped entirely.
   */
  @Test
  public void testRangeSearchPruning() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 20, 60));
    tree.insert(new City("Right", 80, 40));
    tree.insert(new City("LL", 15, 55));
    tree.insert(new City("LR", 25, 85));
    tree.insert(new City("RL", 70, 10));
    tree.insert(new City("RR", 90, 70));

    String result = tree.search(25, 55, 15);
    assertTrue(result.contains("Left"));
    assertTrue(result.contains("LL"));
    assertFalse(result.contains("Root"));
    assertFalse(result.contains("Right"));
    assertFuzzyEquals("2    LL (15, 55)\n1  Left (20, 60)\n"
        + "2 LR (25, 85)\n0Root (50, 50)\n2 RL (70, 10)\n"
        + "1 Right (80, 40)\n2 RR (90, 70)\n",
        tree.printTree());
  }

  /**
   * Tests pruning at exact boundary conditions.
   * Verifies that nodes at exactly the pruning distance are
   * handled
   * correctly.
   */
  @Test
  public void testPruningBoundaryConditions() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("LeftExact", 40, 50));
    tree.insert(new City("RightExact", 60, 50));
    tree.insert(new City("UpExact", 50, 60));
    tree.insert(new City("DownExact", 50, 40));

    String result = tree.search(50, 50, 10);
    assertTrue(result.contains("Root"));
    assertTrue(result.contains("LeftExact"));
    assertTrue(result.contains("RightExact"));
    assertTrue(result.contains("UpExact"));
    assertTrue(result.contains("DownExact"));
    assertFuzzyEquals("1  LeftExact (40, 50)\n0Root (50, 50)\n"
        + "2 DownExact (50, 40)\n1  RightExact (60, 50)\n"
        + "2 UpExact (50, 60)\n", tree.printTree());
  }

  /**
   * Tests pruning when only left subtree should be searched.
   * Right subtree is too far and should be pruned.
   */
  @Test
  public void testPruningLeftOnly() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("Left", 10, 10));
    tree.insert(new City("Right", 200, 200));

    String result = tree.search(10, 10, 5);
    assertTrue(result.contains("Left"));
    assertFalse(result.contains("Root"));
    assertFalse(result.contains("Right"));
  }

  /**
   * Tests pruning when only right subtree should be searched.
   * Left subtree is too far and should be pruned.
   */
  @Test
  public void testPruningRightOnly() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 10, 10));
    tree.insert(new City("Right", 100, 100));

    String result = tree.search(100, 100, 5);
    assertTrue(result.contains("Right"));
    assertFalse(result.contains("Root"));
    assertFalse(result.contains("Left"));
  }

  // ===== MUTATION KILLING TESTS =====

  /**
   * Tests that mutation changes to insertion structure are
   * caught.
   * Verifies tree maintains correct structure with multiple
   * insertions
   * at various depths and positions.
   */
  @Test
  public void testInsertionStructureMutation() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 30, 60));
    tree.insert(new City("C", 70, 40));
    tree.insert(new City("D", 25, 55));
    tree.insert(new City("E", 35, 65));
    assertFuzzyEquals("2    D (25, 55)\n1  B (30, 60)\n"
        + "2 E (35, 65)\n0A (50, 50)\n1  C (70, 40)\n",
        tree.printTree());

    assertNotNull(tree.find(50, 50));
    assertNotNull(tree.find(30, 60));
    assertNotNull(tree.find(70, 40));
    assertNotNull(tree.find(25, 55));
    assertNotNull(tree.find(35, 65));
  }

  /**
   * Tests mutation detection for equal value insertion logic.
   * Ensures equal values consistently go to the right subtree.
   */
  @Test
  public void testEqualValueInsertionMutation() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 50, 60));
    tree.insert(new City("C", 40, 60));
    tree.insert(new City("D", 45, 60));
    assertFuzzyEquals("1  C (40, 60)\n2    D (45, 60)\n"
        + "0A (50, 50)\n1  B (50, 60)\n", tree.printTree());

    assertNotNull(tree.find(50, 60));
    assertNotNull(tree.find(40, 60));
    assertNotNull(tree.find(45, 60));
  }

  /**
   * Tests that distance calculation mutations are detected.
   * Uses known distances (3-4-5 triangle) to verify correct
   * formula.
   */
  @Test
  public void testSearchDistanceMutation() {
    tree.insert(new City("Origin", 0, 0));
    tree.insert(new City("X5", 5, 0));
    tree.insert(new City("Y5", 0, 5));
    tree.insert(new City("XY5", 3, 4));
    tree.insert(new City("Far", 8, 8));

    String result = tree.search(0, 0, 5);
    assertTrue(result.contains("Origin"));
    assertTrue(result.contains("X5"));
    assertTrue(result.contains("Y5"));
    assertTrue(result.contains("XY5"));
    assertFalse(result.contains("Far"));
    assertFuzzyEquals("0Origin (0, 0)\n1  X5 (5, 0)\n"
        + "2    Y5 (0, 5)\n3      XY5 (3, 4)\n"
        + "4        Far (8, 8)\n", tree.printTree());
  }

  /**
   * Tests that axis switching mutations are detected.
   * Verifies correct alternation between X and Y axes at each
   * depth level.
   */
  @Test
  public void testAxisSwitchingMutation() {
    tree.insert(new City("D0", 50, 50));
    tree.insert(new City("D1L", 30, 70));
    tree.insert(new City("D1R", 80, 30));
    tree.insert(new City("D2LL", 20, 60));
    tree.insert(new City("D2LR", 40, 80));
    tree.insert(new City("D2RL", 70, 20));
    tree.insert(new City("D2RR", 90, 40));
    assertFuzzyEquals("2    D2LL (20, 60)\n1  D1L (30, 70)\n"
        + "2    D2LR (40, 80)\n0D0 (50, 50)\n"
        + "2    D2RL (70, 20)\n1  D1R (80, 30)\n"
        + "2    D2RR (90, 40)\n", tree.printTree());

    assertNotNull(tree.find(20, 60));
    assertNotNull(tree.find(40, 80));
    assertNotNull(tree.find(70, 20));
    assertNotNull(tree.find(90, 40));
  }

  /**
   * Comprehensive scenario test combining multiple operations.
   * Tests insertion, finding, deletion, and search in sequence
   * with complex tree structure.
   */
  @Test
  public void testComprehensiveScenario() {
    assertTrue(tree.insert(new City("Chicago", 100, 150)));
    assertTrue(tree.insert(new City("Atlanta", 10, 500)));
    assertTrue(tree.insert(new City("Tacoma", 1000, 100)));
    assertTrue(tree.insert(new City("Baltimore", 0, 300)));
    assertTrue(tree.insert(new City("Washington", 5, 350)));
    assertFalse(tree.insert(new City("X", 100, 150)));
    assertTrue(tree.insert(new City("L", 101, 150)));
    assertTrue(tree.insert(new City("L", 11, 500)));
    assertFuzzyEquals("2    Baltimore (0, 300)\n"
        + "3      Washington (5, 350)\n"
        + "1  Atlanta (10, 500)\n2    L (11, 500)\n"
        + "0Chicago (100, 150)\n1  Tacoma (1000, 100)\n"
        + "2    L (101, 150)\n", tree.printTree());

    assertNotNull(tree.find(100, 150));
    assertNotNull(tree.find(10, 500));
    assertNotNull(tree.find(0, 300));

    String searchResult = tree.search(100, 150, 50);
    assertTrue(searchResult.contains("Chicago"));
    assertTrue(searchResult.contains("L (101, 150)"));

    String deleteResult = tree.delete(0, 300);
    assertTrue(deleteResult.contains("Baltimore"));
    assertNull(tree.find(0, 300));

    String finalTree = tree.printTree();
    assertFalse(finalTree.contains("Baltimore"));
    assertTrue(finalTree.contains("Chicago"));
    assertTrue(finalTree.contains("Atlanta"));
  }

  // ===== INTEGRATION TESTS =====

  /**
   * Tests combination of insert, find, delete, and search
   * operations.
   * Verifies that all operations work correctly when used
   * together.
   */
  @Test
  public void testCombinedOperations() {
    for (int i = 0; i < 6; i++) {
      assertTrue(tree.insert(testCities[i]));
    }

    assertNotNull(tree.find(50, 50));
    assertNotNull(tree.find(25, 75));

    String deleteResult = tree.delete(75, 25);
    assertTrue(deleteResult.contains("CityC"));
    assertNull(tree.find(75, 25));

    String searchResult = tree.search(50, 50, 50);
    assertFalse(searchResult.contains("CityC"));

    String treeStr = tree.printTree();
    assertFalse(treeStr.isEmpty());
    assertFalse(treeStr.contains("CityC"));
  }

  /**
   * Tests tree structure integrity after multiple operations.
   * Verifies that all inserted cities remain findable in their
   * correct
   * positions.
   */
  @Test
  public void testTreeStructureIntegrity() {
    City[] orderedCities = { new City("Root", 50, 50),
        new City("LeftX", 25, 60), new City("RightX", 75, 40),
        new City("LeftLeftY", 20, 30),
        new City("LeftRightY", 30, 70) };

    for (City city : orderedCities) {
      assertTrue(tree.insert(city));
    }

    for (City city : orderedCities) {
      City found = tree.find(city.getX(), city.getY());
      assertNotNull(found);
      assertEquals(city.getName(), found.getName());
    }
    assertFuzzyEquals("2    LeftLeftY (20, 30)\n"
        + "1  LeftX (25, 60)\n2    LeftRightY (30, 70)\n"
        + "0Root (50, 50)\n1  RightX (75, 40)\n",
        tree.printTree());
  }

  /**
   * Tests operations with a larger dataset.
   * Inserts 50 cities and verifies find and delete work
   * correctly.
   */
  @Test
  public void testLargerDataset() {
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 5; y++) {
        City city = new City("City_" + x + "_" + y,
            x * 10, y * 10);
        assertTrue(tree.insert(city));
      }
    }

    assertNotNull(tree.find(0, 0));
    assertNotNull(tree.find(50, 20));
    assertNotNull(tree.find(90, 40));

    String deleteResult = tree.delete(50, 20);
    assertTrue(deleteResult.contains("City_5_2"));
    assertNull(tree.find(50, 20));

    String treeStr = tree.printTree();
    assertFalse(treeStr.contains("City_5_2"));
  }

  /**
   * Tests that deletion correctly calculates axis at each depth
   * level.
   * Verifies proper axis alternation during delete operations.
   */
  @Test
  public void testDeleteAxisCalculation() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 60, 30));
    tree.insert(new City("C", 55, 40));

    String result = tree.delete(50, 50);
    assertTrue(result.contains("A"));
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(60, 30));
    assertNotNull(tree.find(55, 40));

    String treeStr = tree.printTree();
    assertFalse(treeStr.contains("A (50, 50)"));
    assertTrue(treeStr.contains("B"));
  }

  /**
   * Tests that deletion records the deleted city's name in
   * output.
   * Verifies the format includes both visit count and city name.
   */
  @Test
  public void testDeleteNameRecording() {
    tree.insert(new City("Target", 50, 50));
    tree.insert(new City("Replacement", 60, 40));

    String result = tree.delete(50, 50);
    String[] parts = result.split("\n");
    assertEquals(2, parts.length);
    assertTrue(parts[0].matches("\\d+"));
    assertEquals("Target", parts[1]);
    assertFuzzyEquals("0Replacement (60, 40)\n",
        tree.printTree());
  }

  /**
   * Tests deletion in a complex tree structure with multiple
   * levels.
   * Verifies tree integrity after deleting nodes at various
   * positions.
   */
  @Test
  public void testDeleteComplexStructure() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Right", 70, 30));
    tree.insert(new City("RR1", 80, 40));
    tree.insert(new City("RR2", 90, 20));
    tree.insert(new City("RRR", 95, 25));

    String deleteResult = tree.delete(50, 50);
    assertTrue(deleteResult.contains("Root"));
    assertNotNull(tree.find(70, 30));
    assertNotNull(tree.find(80, 40));
    assertNotNull(tree.find(90, 20));
    assertNotNull(tree.find(95, 25));

    String treeStr = tree.printTree();
    assertFalse(treeStr.contains("Root"));
  }

  /**
   * Tests deletion of a node with only left children.
   * Verifies correct handling when left subtree must be moved to
   * right.
   */
  @Test
  public void testDeleteLeftOnlyNode() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("LeftOnly", 30, 60));
    tree.insert(new City("LeftChild", 20, 70));
    tree.insert(new City("LeftGrand", 10, 80));

    String result = tree.delete(30, 60);
    assertTrue(result.contains("LeftOnly"));
    assertNull(tree.find(30, 60));
    assertNotNull(tree.find(50, 50));
    assertNotNull(tree.find(20, 70));
    assertNotNull(tree.find(10, 80));

    assertTrue(tree.insert(new City("NewNode", 25, 65)));
    assertNotNull(tree.find(25, 65));
  }

  // ===== DEPTH AND INDENTATION TESTS =====

  /**
   * Tests that printTree calculates depth correctly at multiple
   * levels.
   * Verifies depth numbers 0, 1, and 2 appear in output.
   */
  @Test
  public void testPrintTreeDepthCalculation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 25));
    tree.insert(new City("Right", 75, 75));
    tree.insert(new City("LL", 10, 10));
    tree.insert(new City("LR", 40, 40));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    boolean hasDepth0 = false;
    boolean hasDepth1 = false;
    boolean hasDepth2 = false;
    for (String line : lines) {
      if (line.startsWith("0"))
        hasDepth0 = true;
      if (line.startsWith("1"))
        hasDepth1 = true;
      if (line.startsWith("2"))
        hasDepth2 = true;
    }

    assertTrue(hasDepth0);
    assertTrue(hasDepth1);
    assertTrue(hasDepth2);
    assertFuzzyEquals("2    LL (10, 10)\n1  Left (25, 25)\n"
        + "2    LR (40, 40)\n0Root (50, 50)\n"
        + "1  Right (75, 75)\n", tree.printTree());
  }

  /**
   * Tests that printTree produces correct indentation at each
   * depth.
   * Root has no spaces, depth 1 has 2 spaces, depth 2 has 4
   * spaces.
   */
  @Test
  public void testPrintTreeIndentation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Child", 25, 25));
    tree.insert(new City("Grand", 10, 10));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    String rootLine = null;
    String childLine = null;
    String grandLine = null;
    for (String line : lines) {
      if (line.contains("Root"))
        rootLine = line;
      if (line.contains("Child"))
        childLine = line;
      if (line.contains("Grand"))
        grandLine = line;
    }

    assertNotNull(rootLine);
    assertNotNull(childLine);
    assertNotNull(grandLine);
    assertTrue(rootLine.matches("0Root.*"));
    assertTrue(childLine.matches("1  Child.*"));
    assertTrue(grandLine.matches("2    Grand.*"));
    assertFuzzyEquals("2    Grand (10, 10)\n1  Child (25, 25)\n"
        + "0Root (50, 50)\n", tree.printTree());
  }

  /**
   * Tests that root node has no indentation while children do.
   * Verifies the indentation pattern starts correctly from root.
   */
  @Test
  public void testRootNodeNoIndentation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Child", 25, 25));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    String rootLine = null;
    String childLine = null;
    for (String line : lines) {
      if (line.contains("Root"))
        rootLine = line;
      if (line.contains("Child"))
        childLine = line;
    }

    assertNotNull(rootLine);
    assertNotNull(childLine);
    assertTrue(rootLine.startsWith("0Root"));
    assertFalse(rootLine.startsWith("0 "));
    assertTrue(childLine.startsWith("1  "));
    assertFuzzyEquals("1  Child (25, 25)\n0Root (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests depth calculation in right-heavy subtree.
   * Verifies correct depth numbers for deep right branches.
   */
  @Test
  public void testPrintTreeRightSubtreeDepth() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 25));
    tree.insert(new City("Right", 75, 75));
    tree.insert(new City("RightRight", 90, 90));
    tree.insert(new City("RRR", 95, 95));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    boolean hasDepth3 = false;
    String depth3Line = null;
    for (String line : lines) {
      if (line.startsWith("3")) {
        hasDepth3 = true;
        depth3Line = line;
        break;
      }
    }

    assertTrue(hasDepth3);
    assertNotNull(depth3Line);
    assertTrue(depth3Line.matches("3      .*"));
    assertFuzzyEquals("1  Left (25, 25)\n0Root (50, 50)\n"
        + "1  Right (75, 75)\n2    RightRight (90, 90)\n"
        + "3      RRR (95, 95)\n", tree.printTree());
  }

  /**
   * Tests that indentation scales correctly with depth (2 spaces
   * per level).
   * Verifies pattern: depth 0 (no spaces), depth 1 (2), depth 2
   * (4), depth 3
   * (6).
   */
  @Test
  public void testPrintTreeIndentationScaling() {
    tree.insert(new City("D0", 50, 50));
    tree.insert(new City("D1", 25, 25));
    tree.insert(new City("D2", 10, 10));
    tree.insert(new City("D3", 5, 5));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    for (String line : lines) {
      if (line.contains("D0")) {
        assertTrue(line.matches("0D0.*"));
      } else if (line.contains("D1")) {
        assertTrue(line.matches("1  D1.*"));
      } else if (line.contains("D2")) {
        assertTrue(line.matches("2    D2.*"));
      } else if (line.contains("D3")) {
        assertTrue(line.matches("3      D3.*"));
      }
    }
    assertFuzzyEquals("3      D3 (5, 5)\n2    D2 (10, 10)\n"
        + "1  D1 (25, 25)\n0D0 (50, 50)\n",
        tree.printTree());
  }

  // ===== EDGE CASE COVERAGE =====

  /**
   * Tests comparison by axis in both directions (less than and
   * greater than).
   * Verifies correct placement based on X-axis and Y-axis
   * comparisons.
   */
  @Test
  public void testCompareByAxisBothDirections() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("MoreX", 60, 50));
    tree.insert(new City("LessX", 40, 50));
    assertFuzzyEquals("1  LessX (40, 50)\n0Center (50, 50)\n"
        + "1  MoreX (60, 50)\n", tree.printTree());

    tree = new KDTree();
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("LessXMoreY", 40, 60));
    tree.insert(new City("LessXLessY", 30, 40));
    assertFuzzyEquals("2    LessXLessY (30, 40)\n"
        + "1  LessXMoreY (40, 60)\n0Center (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests that search explores both left and right subtrees when
   * necessary.
   * When query region intersects both sides, both must be
   * searched.
   */
  @Test
  public void testSearchBothSubtrees() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("L", 40, 50));
    tree.insert(new City("R", 60, 50));
    tree.insert(new City("LL", 35, 50));
    tree.insert(new City("RR", 65, 50));

    String result = tree.search(50, 50, 20);
    assertTrue(result.contains("Root"));
    assertTrue(result.contains("L"));
    assertTrue(result.contains("R"));
    assertTrue(result.contains("LL"));
    assertTrue(result.contains("RR"));
  }

  /**
   * Tests findMin method when it must explore both subtree paths.
   * Verifies correct minimum selection across both left and right
   * children.
   */
  @Test
  public void testFindMinBothPaths() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Right", 60, 40));
    tree.insert(new City("RR1", 70, 30));
    tree.insert(new City("RR2", 80, 20));

    tree.delete(50, 50);
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(60, 40));
  }

  /**
   * Tests that delete returns the correct visited node count.
   * Verifies the count reflects actual traversal during deletion.
   */
  @Test
  public void testVisitedCountInDelete() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 25));
    tree.insert(new City("C", 75, 75));
    tree.insert(new City("D", 10, 10));
    tree.insert(new City("E", 30, 30));

    String result = tree.delete(50, 50);
    String[] parts = result.split("\n");
    assertTrue(parts[0].matches("\\d+"));
    int visited = Integer.parseInt(parts[0].trim());
    assertTrue(visited > 0);
  }

  /**
   * Tests that search returns the correct visited node count.
   * The last line of search output should be the visit count.
   */
  @Test
  public void testVisitedCountInSearch() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 25));
    tree.insert(new City("C", 75, 75));

    String result = tree.search(50, 50, 10);
    String[] lines = result.split("\n");
    String lastLine = lines[lines.length - 1].trim();
    assertTrue(lastLine.matches("\\d+"));
    int visited = Integer.parseInt(lastLine);
    assertTrue(visited > 0);
  }

  // ===========================================================
  /**
   * Tests that KD-tree search correctly calculates and uses the
   * X-axis
   * difference
   * (dx) for pruning during range searches. Ensures that nodes
   * beyond the
   * search radius along the X-axis are excluded from results and
   * that the
   * tree structure remains unchanged.
   */
  @Test
  public void testSearchDifferenceCalculationXAxis() {
    // Set up a tree where X-axis pruning matters
    tree.insert(new City("Root", 100, 50));
    tree.insert(new City("LeftChild", 50, 60));
    tree.insert(new City("RightChild", 150, 40));
    tree.insert(new City("FarLeft", 20, 70));
    tree.insert(new City("FarRight", 180, 30));

    // Verify tree structure first
    assertFuzzyEquals("1  LeftChild (50, 60)\n"
        + "2    FarLeft (20, 70)\n0Root (100, 50)\n"
        + "2    FarRight (180, 30)\n"
        + "1  RightChild (150, 40)\n", tree.printTree());

    // Perform a search centered at (100, 50) with radius 30
    String result = tree.search(100, 50, 30);

    // Verify results based on dx pruning
    assertTrue(result.contains("Root"));
    assertFalse(result.contains("FarLeft"));
    assertFalse(result.contains("FarRight"));

    // Ensure tree structure remains unchanged
    assertFuzzyEquals("1  LeftChild (50, 60)\n"
        + "2    FarLeft (20, 70)\n0Root (100, 50)\n"
        + "2    FarRight (180, 30)\n"
        + "1  RightChild (150, 40)\n", tree.printTree());
  }

  /**
   * Tests that KD-tree search correctly calculates and uses the
   * Y-axis
   * difference
   * (dy) for pruning. Ensures nodes outside the vertical search
   * range are
   * excluded
   * and verifies the tree remains unchanged after search.
   */
  @Test
  public void testSearchDifferenceCalculationYAxis() {
    tree.insert(new City("Root", 50, 100));
    tree.insert(new City("BelowRoot", 40, 100));
    tree.insert(new City("LowChild", 40, 60));
    tree.insert(new City("VeryLow", 40, 30));

    // Verify initial tree structure
    assertFuzzyEquals("2    LowChild (40, 60)\n"
        + "3      VeryLow (40, 30)\n"
        + "1  BelowRoot (40, 100)\n0Root (50, 100)\n",
        tree.printTree());

    // Perform a search centered at (50, 100) with radius 25
    String result = tree.search(50, 100, 25);

    // Verify correct nodes returned based on dy pruning
    assertTrue(result.contains("Root"));
    assertTrue(result.contains("BelowRoot"));
    assertFalse(result.contains("VeryLow"));

    // Ensure tree is unchanged
    assertFuzzyEquals("2    LowChild (40, 60)\n"
        + "3      VeryLow (40, 30)\n"
        + "1  BelowRoot (40, 100)\n0Root (50, 100)\n",
        tree.printTree());
  }

  /**
   * Tests search pruning with large query coordinates to ensure
   * correct dx/dy
   * calculations. Mutations that incorrectly use only qx or qy
   * would result
   * in
   * major pruning errors, so this verifies correct search
   * behavior in that
   * case.
   */
  @Test
  public void testSearchWithLargeQueryCoordinates() {
    tree.insert(new City("A", 200, 200));
    tree.insert(new City("B", 180, 210));
    tree.insert(new City("C", 220, 190));
    tree.insert(new City("D", 170, 220));
    tree.insert(new City("E", 230, 180));

    assertFuzzyEquals("1  B (180, 210)\n2    D (170, 220)\n"
        + "0A (200, 200)\n2    E (230, 180)\n"
        + "1  C (220, 190)\n", tree.printTree());

    String result = tree.search(200, 200, 25);

    // Only nodes within distance should be included
    assertTrue(result.contains("A"));
    assertTrue(result.contains("B"));
    assertTrue(result.contains("C"));
    assertFalse(result.contains("D"));
    assertFalse(result.contains("E"));

    assertFuzzyEquals("1  B (180, 210)\n2    D (170, 220)\n"
        + "0A (200, 200)\n2    E (230, 180)\n"
        + "1  C (220, 190)\n", tree.printTree());
  }

  /**
   * Tests KD-tree pruning behavior when differences can be
   * positive or
   * negative
   * along the search axis. Ensures both sides of the search range
   * are
   * correctly
   * considered and nodes beyond the range are excluded.
   */
  @Test
  public void testSearchPruningBothDirections() {
    tree.insert(new City("Center", 500, 500));
    tree.insert(new City("Left", 460, 500));
    tree.insert(new City("Right", 540, 500));
    tree.insert(new City("LL", 440, 500));
    tree.insert(new City("RR", 560, 500));

    assertFuzzyEquals("1  Left (460, 500)\n2    LL (440, 500)\n"
        + "0Center (500, 500)\n1  Right (540, 500)\n"
        + "2    RR (560, 500)\n", tree.printTree());

    String result = tree.search(500, 500, 45);

    // Verify which nodes fall within the distance threshold
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Left"));
    assertTrue(result.contains("Right"));
    assertFalse(result.contains("LL"));
    assertFalse(result.contains("RR"));

    assertFuzzyEquals("1  Left (460, 500)\n2    LL (440, 500)\n"
        + "0Center (500, 500)\n1  Right (540, 500)\n"
        + "2    RR (560, 500)\n", tree.printTree());
  }

  /**
   * Tests deletion logic where removing a node forces findMin to
   * explore
   * both left and right subtrees. Verifies that the correct
   * successor is
   * chosen
   * and that the deleted node is no longer present.
   */
  @Test
  public void testDeleteNodeForcingFindMinBothSubtreesWithVerify() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 30, 60));
    tree.insert(new City("Right", 70, 40));
    tree.insert(new City("LL", 20, 55));
    tree.insert(new City("LR", 35, 65));

    assertFuzzyEquals("2    LL (20, 55)\n1  Left (30, 60)\n"
        + "2    LR (35, 65)\n0Root (50, 50)\n"
        + "1  Right (70, 40)\n", tree.printTree());

    tree.delete(30, 60);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("2    LL (20, 55)\n1  LR (35, 65)\n"
        + "0Root (50, 50)\n1  Right (70, 40)\n",
        afterDelete);

    assertNull(tree.find(30, 60));
  }

  /**
   * Tests deletion of the root node in a KD-tree where findMin
   * must search
   * along the X-axis. Ensures the correct successor replaces the
   * root and
   * the deleted node is removed.
   */
  @Test
  public void testDeleteRootForcingFindMinXAxis() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("R", 70, 60));
    tree.insert(new City("RL", 65, 55));
    tree.insert(new City("RR", 75, 65));

    assertFuzzyEquals("0Root (50, 50)\n2    RL (65, 55)\n"
        + "1  R (70, 60)\n2    RR (75, 65)\n",
        tree.printTree());

    tree.delete(50, 50);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("0RL (65, 55)\n1  R (70, 60)\n"
        + "2    RR (75, 65)\n", afterDelete);

    assertNull(tree.find(50, 50));
  }

  /**
   * Tests deletion of a node that triggers a deep traversal in
   * findMin.
   * Ensures that successor selection works correctly even when
   * traversal
   * depth
   * increases and that the tree structure remains valid
   * afterward.
   */
  @Test
  public void testDeleteWithDeepFindMinTraversal() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 30, 60));
    tree.insert(new City("C", 70, 40));
    tree.insert(new City("D", 20, 55));
    tree.insert(new City("E", 40, 65));
    tree.insert(new City("F", 15, 52));
    tree.insert(new City("G", 25, 58));

    assertFuzzyEquals("3      F (15, 52)\n2    D (20, 55)\n"
        + "3      G (25, 58)\n1  B (30, 60)\n"
        + "2    E (40, 65)\n0A (50, 50)\n"
        + "1  C (70, 40)\n", tree.printTree());

    tree.delete(30, 60);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("3      F (15, 52)\n2    D (20, 55)\n"
        + "3      G (25, 58)\n1  E (40, 65)\n"
        + "0A (50, 50)\n1  C (70, 40)\n", afterDelete);

    assertNull(tree.find(30, 60));
  }

  /**
   * Tests deletion where multiple candidate nodes could replace
   * the deleted
   * node.
   * Ensures that findMin chooses the correct minimum node among
   * several
   * options.
   */
  @Test
  public void testDeleteForcingFindMinWithMultipleCandidates() {
    tree.insert(new City("M", 50, 50));
    tree.insert(new City("L", 25, 60));
    tree.insert(new City("R", 75, 40));
    tree.insert(new City("LL", 10, 55));
    tree.insert(new City("LR", 30, 65));
    tree.insert(new City("LLL", 5, 52));
    tree.insert(new City("LLR", 15, 58));

    assertFuzzyEquals("3      LLL (5, 52)\n2    LL (10, 55)\n"
        + "3      LLR (15, 58)\n1  L (25, 60)\n"
        + "2    LR (30, 65)\n0M (50, 50)\n"
        + "1  R (75, 40)\n", tree.printTree());

    tree.delete(25, 60);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("3      LLL (5, 52)\n2    LL (10, 55)\n"
        + "3      LLR (15, 58)\n1  LR (30, 65)\n"
        + "0M (50, 50)\n1  R (75, 40)\n", afterDelete);

    assertNull(tree.find(25, 60));
  }

  /**
   * Tests deletion of the root node when the right subtree is
   * complex and
   * requires
   * findMin to search deeply. Ensures the correct successor is
   * selected and
   * the
   * KD-tree remains valid.
   */
  @Test
  public void testDeleteRootWithComplexRightSubtree() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("R", 150, 80));
    tree.insert(new City("RL", 130, 90));
    tree.insert(new City("RR", 170, 70));
    tree.insert(new City("RLL", 120, 85));
    tree.insert(new City("RLR", 140, 95));

    assertFuzzyEquals("0Root (100, 100)\n2    RR (170, 70)\n"
        + "1  R (150, 80)\n3      RLL (120, 85)\n"
        + "2    RL (130, 90)\n3      RLR (140, 95)\n",
        tree.printTree());

    tree.delete(100, 100);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("0RLL (120, 85)\n2    RR (170, 70)\n"
        + "1  R (150, 80)\n2    RL (130, 90)\n"
        + "3      RLR (140, 95)\n", afterDelete);

    assertNull(tree.find(100, 100));
  }

  /**
   * Tests find when the axis values have equal values
   */
  @Test
  public void testFindMinWithEqualAxisValues() {
    // Test with coordinates that have equal values
    // on splitting axis
    // Wrong depth causes wrong axis selection and
    // incorrect minimum
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("R1", 150, 100)); // Equal Y
    tree.insert(new City("R2", 150, 90)); // Equal X
    tree.insert(new City("R3", 150, 95)); // Equal X
    tree.insert(new City("R4", 140, 90)); // Different X

    assertFuzzyEquals("0Root (100, 100)\n3      R4 (140, 90)\n"
        + "2    R2 (150, 90)\n3      R3 (150, 95)\n"
        + "1  R1 (150, 100)\n", tree.printTree());

    tree.delete(100, 100);

    assertNull(tree.find(100, 100));
    assertNotNull(tree.find(150, 100));
    assertNotNull(tree.find(140, 90));
  }

  /**
   * Tests multiple deletions in a KDTree that require findMin to
   * select
   * the correct node based on depth and axis.
   * Verifies that deleted nodes are removed and remaining nodes
   * are
   * accessible.
   */
  @Test
  public void testMultipleDeletesStressingFindMin() {
    tree.insert(new City("A", 100, 100));
    tree.insert(new City("B", 80, 120));
    tree.insert(new City("C", 120, 80));
    tree.insert(new City("D", 70, 110));
    tree.insert(new City("E", 90, 130));
    tree.insert(new City("F", 110, 70));
    tree.insert(new City("G", 130, 90));
    tree.insert(new City("H", 60, 105));

    assertFuzzyEquals("3      H (60, 105)\n2    D (70, 110)\n"
        + "1  B (80, 120)\n2    E (90, 130)\n"
        + "0A (100, 100)\n2    F (110, 70)\n"
        + "1  C (120, 80)\n2    G (130, 90)\n",
        tree.printTree());

    tree.delete(100, 100);
    assertNull(tree.find(100, 100));

    tree.delete(80, 120);
    assertNull(tree.find(80, 120));

    tree.delete(120, 80);
    assertNull(tree.find(120, 80));

    assertNotNull(tree.find(70, 110));
    assertNotNull(tree.find(90, 130));
  }

  /**
   * Tests findMin in a scenario where selecting the wrong axis
   * would
   * return an incorrect node.
   * Ensures that deletion triggers findMin along the correct
   * axis.
   */
  @Test
  public void testFindMinAxisCriticalScenario() {
    tree.insert(new City("Root", 200, 200));
    tree.insert(new City("R1", 250, 180));
    tree.insert(new City("R2", 230, 190));
    tree.insert(new City("R3", 240, 170));
    tree.insert(new City("R4", 235, 185));

    assertFuzzyEquals("0Root (200, 200)\n2    R3 (240, 170)\n"
        + "1  R1 (250, 180)\n2    R2 (230, 190)\n"
        + "3      R4 (235, 185)\n", tree.printTree());

    tree.delete(200, 200);

    assertNull(tree.find(200, 200));
    assertNotNull(tree.find(230, 190));
    assertNotNull(tree.find(240, 170));
  }

  /**
   * Tests findMin with large coordinate values to catch
   * arithmetic
   * or depth errors.
   */
  @Test
  public void testFindMinWithLargeCoordinates() {
    tree.insert(new City("Root", 1000, 1000));
    tree.insert(new City("R1", 1500, 900));
    tree.insert(new City("R2", 1300, 950));
    tree.insert(new City("R3", 1700, 850));
    tree.insert(new City("R4", 1200, 925));
    tree.insert(new City("R5", 1400, 975));

    assertFuzzyEquals("0Root (1000, 1000)\n"
        + "2    R3 (1700, 850)\n1  R1 (1500, 900)\n"
        + "3      R4 (1200, 925)\n2    R2 (1300, 950)\n"
        + "3      R5 (1400, 975)\n", tree.printTree());

    tree.delete(1000, 1000);

    assertNull(tree.find(1000, 1000));
    assertNotNull(tree.find(1200, 925));
    assertNotNull(tree.find(1500, 900));
  }

  /**
   * Tests the case where only a left subtree exists.
   * Verifies that the subtree is correctly restructured when the
   * root is
   * deleted.
   */
  @Test
  public void testFindMinLeftOnlySubtreeConversion() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("L1", 30, 60));
    tree.insert(new City("L2", 20, 55));
    tree.insert(new City("L3", 10, 58));
    tree.insert(new City("L4", 25, 65));

    assertFuzzyEquals("3      L3 (10, 58)\n2    L2 (20, 55)\n"
        + "1  L1 (30, 60)\n2    L4 (25, 65)\n"
        + "0Root (50, 50)\n", tree.printTree());

    tree.delete(50, 50);

    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(30, 60));
    assertNotNull(tree.find(10, 58));
  }

  /**
   * Forces findMin to search both left and right subtrees when
   * the axis
   * differs from the current node's axis (covers line 162).
   */
  @Test
  public void testFindMinLine162Coverage() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("L", 30, 60));
    tree.insert(new City("LL", 20, 55));
    tree.insert(new City("LR", 40, 65));

    assertFuzzyEquals("2    LL (20, 55)\n1  L (30, 60)\n"
        + "2    LR (40, 65)\n0Root (50, 50)\n",
        tree.printTree());

    String result = tree.delete(50, 50);
    assertTrue(result.contains("Root"));
    assertNull(tree.find(50, 50));

    assertNotNull(tree.find(20, 55));
    assertNotNull(tree.find(30, 60));
    assertNotNull(tree.find(40, 65));

    String treeAfter = tree.printTree();
    assertFalse(treeAfter.contains("Root"));
  }

  /**
   * Fuzzy test with semi-random coordinates and multiple
   * deletions.
   * Ensures that deleted nodes are removed and remaining nodes
   * are
   * accessible.
   */
  @Test
  public void testFuzzyRandomInsertDeleteSequence1() {
    int[][] coords = { { 500, 100 }, { 400, 200 },
        { 600, 50 }, { 300, 300 }, { 700, 25 },
        { 350, 250 }, { 450, 150 }, { 250, 350 },
        { 550, 75 }, { 650, 40 } };

    for (int i = 0; i < coords.length; i++) {
      tree.insert(new City("C" + i, coords[i][0],
          coords[i][1]));
    }

    tree.delete(500, 100);
    tree.delete(400, 200);
    tree.delete(300, 300);

    assertNotNull(tree.find(600, 50));
    assertNotNull(tree.find(700, 25));
    assertNotNull(tree.find(350, 250));
    assertNotNull(tree.find(450, 150));
    assertNotNull(tree.find(250, 350));
    assertNotNull(tree.find(550, 75));
    assertNotNull(tree.find(650, 40));

    assertNull(tree.find(500, 100));
    assertNull(tree.find(400, 200));
    assertNull(tree.find(300, 300));
  }

  /**
   * Fuzzy test with alternating insertions to create deep findMin
   * traversals.
   * Verifies correct accessibility of remaining nodes.
   */
  @Test
  public void testFuzzyAlternatingPatternDeletions() {
    tree.insert(new City("A", 128, 128));
    tree.insert(new City("B", 64, 192));
    tree.insert(new City("C", 192, 64));
    tree.insert(new City("D", 32, 224));
    tree.insert(new City("E", 96, 160));
    tree.insert(new City("F", 160, 96));
    tree.insert(new City("G", 224, 32));
    tree.insert(new City("H", 16, 240));
    tree.insert(new City("I", 48, 208));
    tree.insert(new City("J", 80, 176));
    tree.insert(new City("K", 112, 144));

    tree.delete(128, 128);
    tree.delete(64, 192);
    tree.delete(192, 64);

    assertNotNull(tree.find(32, 224));
    assertNotNull(tree.find(96, 160));
    assertNotNull(tree.find(160, 96));
    assertNotNull(tree.find(224, 32));
    assertNotNull(tree.find(16, 240));
  }

  /**
   * Tests a dense left-heavy tree with deletions.
   * Ensures correct findMin traversal and tree integrity after
   * multiple
   * deletions.
   */
  @Test
  public void testFuzzyDenseLeftSubtreeDeletions() {
    tree.insert(new City("Root", 1000, 1000));
    tree.insert(new City("L1", 500, 1500));
    tree.insert(new City("L2", 250, 1250));
    tree.insert(new City("L3", 750, 1750));
    tree.insert(new City("L4", 125, 1125));
    tree.insert(new City("L5", 375, 1375));
    tree.insert(new City("L6", 625, 1625));
    tree.insert(new City("L7", 875, 1875));
    tree.insert(new City("L8", 62, 1062));
    tree.insert(new City("L9", 187, 1187));
    tree.insert(new City("L10", 312, 1312));
    tree.insert(new City("L11", 437, 1437));

    tree.delete(1000, 1000);
    tree.delete(500, 1500);
    tree.delete(250, 1250);

    assertNotNull(tree.find(62, 1062));
    assertNotNull(tree.find(187, 1187));
    assertNotNull(tree.find(312, 1312));
    assertNotNull(tree.find(437, 1437));
    assertNotNull(tree.find(750, 1750));
  }

  /**
   * Tests a mixture of small and large coordinates with
   * deletions.
   * Verifies arithmetic correctness and accessibility of
   * remaining nodes.
   */
  @Test
  public void testFuzzyMixedCoordinateRanges() {
    tree.insert(new City("A", 5000, 10));
    tree.insert(new City("B", 10, 5000));
    tree.insert(new City("C", 9000, 20));
    tree.insert(new City("D", 20, 9000));
    tree.insert(new City("E", 2500, 7500));
    tree.insert(new City("F", 7500, 2500));
    tree.insert(new City("G", 1000, 8000));
    tree.insert(new City("H", 8000, 1000));
    tree.insert(new City("I", 500, 9500));
    tree.insert(new City("J", 9500, 500));

    tree.delete(5000, 10);
    tree.delete(10, 5000);
    tree.delete(2500, 7500);

    assertNull(tree.find(5000, 10));
    assertNull(tree.find(10, 5000));
    assertNull(tree.find(2500, 7500));

    assertNotNull(tree.find(9000, 20));
    assertNotNull(tree.find(20, 9000));
    assertNotNull(tree.find(500, 9500));
  }

  /**
   * Zigzag insertion and deletion pattern to stress axis
   * alternation
   * and findMin traversal in deep trees.
   */
  @Test
  public void testFuzzyZigzagPattern() {
    tree.insert(new City("Z0", 512, 512));
    tree.insert(new City("Z1", 256, 768));
    tree.insert(new City("Z2", 128, 640));
    tree.insert(new City("Z3", 64, 704));
    tree.insert(new City("Z4", 32, 672));
    tree.insert(new City("Z5", 16, 688));
    tree.insert(new City("Z6", 8, 680));
    tree.insert(new City("Z7", 4, 684));
    tree.insert(new City("Z8", 384, 896));
    tree.insert(new City("Z9", 320, 832));
    tree.insert(new City("Z10", 352, 864));

    tree.delete(512, 512);
    tree.delete(256, 768);
    tree.delete(128, 640);

    assertNull(tree.find(512, 512));
    assertNull(tree.find(256, 768));
    assertNull(tree.find(128, 640));

    assertNotNull(tree.find(4, 684));
    assertNotNull(tree.find(8, 680));
    assertNotNull(tree.find(16, 688));
  }

  // %%%%%%%%%%%

  /**
   * Tests deletion in a tree with clustered coordinates.
   * Ensures exact comparisons are not required and remaining
   * nodes
   * are still accessible.
   */
  @Test
  public void testFuzzyClusteredCoordinates() {
    // Insert clustered cities
    tree.insert(new City("C1", 100, 100));
    tree.insert(new City("C2", 101, 99));
    tree.insert(new City("C3", 99, 101));
    tree.insert(new City("C4", 102, 98));
    tree.insert(new City("C5", 98, 102));
    tree.insert(new City("C6", 103, 97));
    tree.insert(new City("C7", 97, 103));
    tree.insert(new City("C8", 104, 96));
    tree.insert(new City("C9", 96, 104));
    tree.insert(new City("C10", 105, 95));
    tree.insert(new City("C11", 95, 105));

    // Delete several nodes
    tree.delete(100, 100);
    tree.delete(101, 99);
    tree.delete(99, 101);
    tree.delete(102, 98);

    // Deleted nodes should no longer be found
    assertNull(tree.find(100, 100));
    assertNull(tree.find(101, 99));
    assertNull(tree.find(99, 101));
    assertNull(tree.find(102, 98));

    // Remaining nodes should still be accessible
    assertNotNull(tree.find(98, 102));
    assertNotNull(tree.find(103, 97));
    assertNotNull(tree.find(97, 103));
    assertNotNull(tree.find(95, 105));
  }

  /**
   * Tests deletion and find operations in a very deep KDTree.
   * Ensures that depth tracking and axis selection remain correct
   * even at extreme depths.
   */
  @Test
  public void testFuzzyExtremeDepthScenario() {
    // Insert nodes to build a deep tree
    tree.insert(new City("D0", 2048, 2048));
    tree.insert(new City("D1", 1024, 3072));
    tree.insert(new City("D2", 512, 2560));
    tree.insert(new City("D3", 256, 2816));
    tree.insert(new City("D4", 128, 2688));
    tree.insert(new City("D5", 64, 2752));
    tree.insert(new City("D6", 32, 2720));
    tree.insert(new City("D7", 16, 2736));
    tree.insert(new City("D8", 8, 2728));
    tree.insert(new City("D9", 4, 2732));
    tree.insert(new City("D10", 2, 2730));
    tree.insert(new City("D11", 1, 2731));

    // Delete nodes at various depths
    tree.delete(2048, 2048);
    tree.delete(1024, 3072);
    tree.delete(512, 2560);

    // Ensure deleted nodes are removed
    assertNull(tree.find(2048, 2048));
    assertNull(tree.find(1024, 3072));
    assertNull(tree.find(512, 2560));

    // Very deep nodes should remain accessible
    assertNotNull(tree.find(1, 2731));
    assertNotNull(tree.find(2, 2730));
    assertNotNull(tree.find(4, 2732));
    assertNotNull(tree.find(8, 2728));
  }

  /**
   * Tests deletion in a balanced, complex KDTree.
   * Deletes nodes from different parts of the tree and verifies
   * tree integrity and accessibility of remaining nodes.
   */
  @Test
  public void testFuzzyBalancedComplexDeletions() {
    // Insert nodes for balanced structure
    tree.insert(new City("M", 400, 400));
    tree.insert(new City("L", 200, 600));
    tree.insert(new City("R", 600, 200));
    tree.insert(new City("LL", 100, 500));
    tree.insert(new City("LR", 300, 700));
    tree.insert(new City("RL", 500, 100));
    tree.insert(new City("RR", 700, 300));
    tree.insert(new City("LLL", 50, 450));
    tree.insert(new City("LLR", 150, 550));
    tree.insert(new City("LRL", 250, 650));
    tree.insert(new City("LRR", 350, 750));
    tree.insert(new City("RLL", 450, 50));
    tree.insert(new City("RLR", 550, 150));
    tree.insert(new City("RRL", 650, 250));
    tree.insert(new City("RRR", 750, 350));

    // Delete multiple nodes
    tree.delete(400, 400); // Root
    tree.delete(200, 600); // Left child
    tree.delete(600, 200); // Right child
    tree.delete(100, 500); // LL
    tree.delete(700, 300); // RR

    // Check remaining nodes
    assertNotNull(tree.find(50, 450));
    assertNotNull(tree.find(150, 550));
    assertNotNull(tree.find(300, 700));
    assertNotNull(tree.find(500, 100));
    assertNotNull(tree.find(750, 350));

    // Ensure deleted nodes are removed
    assertNull(tree.find(400, 400));
    assertNull(tree.find(200, 600));
    assertNull(tree.find(600, 200));
  }

  /**
   * Multi-pass fuzzy test: insert, delete, and reinsert nodes.
   * Ensures that KDTree handles repeated insertions and deletions
   * without corrupting the tree.
   */
  @Test
  public void testFuzzyStressTestMultiplePasses() {
    // First pass
    tree.insert(new City("P1_1", 300, 700));
    tree.insert(new City("P1_2", 700, 300));
    tree.insert(new City("P1_3", 150, 850));
    tree.insert(new City("P1_4", 850, 150));
    tree.insert(new City("P1_5", 450, 550));

    tree.delete(300, 700);
    assertNull(tree.find(300, 700));
    assertNotNull(tree.find(700, 300));

    // Second pass
    tree.insert(new City("P2_1", 600, 400));
    tree.insert(new City("P2_2", 400, 600));
    tree.insert(new City("P2_3", 200, 800));

    tree.delete(700, 300);
    tree.delete(850, 150);

    assertNull(tree.find(700, 300));
    assertNull(tree.find(850, 150));
    assertNotNull(tree.find(600, 400));
    assertNotNull(tree.find(150, 850));

    // Third pass
    tree.insert(new City("P3_1", 500, 500));
    tree.insert(new City("P3_2", 250, 750));

    assertNotNull(tree.find(500, 500));
    assertNotNull(tree.find(250, 750));
    assertNotNull(tree.find(450, 550));
  }

  /**
   * Tests that findMin correctly explores the left subtree when
   * necessary.
   * Forces a scenario where the minimum along an axis is in the
   * left subtree.
   */
  @Test
  public void testFindMinLine162ExplicitLeftExploration() {
    tree.insert(new City("Root", 200, 200));
    tree.insert(new City("Left", 100, 250));
    tree.insert(new City("LL", 80, 240));
    tree.insert(new City("LR", 120, 260));
    tree.insert(new City("LLL", 70, 235));
    tree.insert(new City("LLR", 90, 245));

    assertFuzzyEquals("3      LLL (70, 235)\n"
        + "2    LL (80, 240)\n3      LLR (90, 245)\n"
        + "1  Left (100, 250)\n2    LR (120, 260)\n"
        + "0Root (200, 200)\n", tree.printTree());

    tree.delete(200, 200);

    assertNull(tree.find(200, 200));
    assertNotNull(tree.find(70, 235)); // Minimum X
    assertNotNull(tree.find(100, 250));
    assertNotNull(tree.find(80, 240));
  }

  /**
   * Detects mutations affecting depth and axis calculation.
   * Ensures findMin returns correct node even if depth
   * calculations are
   * modified.
   */
  @Test
  public void testFindMinLine162MutationDetection() {
    tree.insert(new City("M", 500, 500));
    tree.insert(new City("L", 300, 600));
    tree.insert(new City("LL", 200, 550));
    tree.insert(new City("LR", 400, 650));
    tree.insert(new City("LLL", 150, 525));
    tree.insert(new City("LLR", 250, 575));
    tree.insert(new City("LRL", 350, 625));
    tree.insert(new City("LRR", 450, 675));

    assertFuzzyEquals("3      LLL (150, 525)\n"
        + "2    LL (200, 550)\n3      LLR (250, 575)\n"
        + "1  L (300, 600)\n3      LRL (350, 625)\n"
        + "2    LR (400, 650)\n3      LRR (450, 675)\n"
        + "0M (500, 500)\n", tree.printTree());

    tree.delete(500, 500);

    assertNull(tree.find(500, 500));
    assertNotNull(tree.find(150, 525)); // Minimum X
    assertNotNull(tree.find(300, 600));
    assertNotNull(tree.find(200, 550));
    assertNotNull(tree.find(400, 650));
  }

  /**
   * Tests recursive successor removal during deletion.
   * Verifies that the tree structure is updated correctly after
   * removing the
   * root.
   */
  @Test
  public void testDeleteRecursiveSuccessorRemoval() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("R", 70, 60));
    tree.insert(new City("RR", 80, 55));
    tree.insert(new City("RRR", 85, 58));

    assertFuzzyEquals("0Root (50, 50)\n2    RR (80, 55)\n"
        + "3      RRR (85, 58)\n1  R (70, 60)\n",
        tree.printTree());

    tree.delete(50, 50);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("0R (70, 60)\n1  RR (80, 55)\n"
        + "2    RRR (85, 58)\n", afterDelete);

    assertNull(tree.find(50, 50));
  }

  // %%%%%%%%%%%
  /**
   * Tests deletion of the root node where the successor is
   * located deep
   * in the tree. Ensures the deep successor is correctly removed
   * and
   * the tree remains consistent.
   */
  @Test
  public void testDeleteWithDeepSuccessorRemoval() {
    tree.insert(new City("A", 40, 40));
    tree.insert(new City("B", 60, 50));
    tree.insert(new City("C", 70, 45));
    tree.insert(new City("D", 65, 48));
    tree.insert(new City("E", 75, 42));

    assertFuzzyEquals("0A (40, 40)\n3      D (65, 48)\n"
        + "2    C (70, 45)\n3      E (75, 42)\n"
        + "1  B (60, 50)\n", tree.printTree());

    tree.delete(40, 40);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("0B (60, 50)\n1  E (75, 42)\n"
        + "3      D (65, 48)\n2    C (70, 45)\n",
        afterDelete);

    assertNull(tree.find(40, 40));
  }

  /**
   * Tests deletion of the root node when the right subtree has a
   * complex
   * structure. Ensures proper selection of the successor and
   * removal
   * without corrupting the tree.
   */
  @Test
  public void testDeleteNodeWithComplexRightSubtreeStructure() {
    tree.insert(new City("M", 50, 50));
    tree.insert(new City("R", 70, 40));
    tree.insert(new City("RL", 60, 45));
    tree.insert(new City("RR", 80, 35));
    tree.insert(new City("RLL", 55, 43));
    tree.insert(new City("RLR", 65, 47));

    assertFuzzyEquals("0M (50, 50)\n2    RR (80, 35)\n"
        + "1  R (70, 40)\n3      RLL (55, 43)\n"
        + "2    RL (60, 45)\n3      RLR (65, 47)\n",
        tree.printTree());

    tree.delete(50, 50);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("0RLL (55, 43)\n2    RR (80, 35)\n"
        + "1  R (70, 40)\n2    RL (60, 45)\n"
        + "3      RLR (65, 47)\n", afterDelete);

    assertNull(tree.find(50, 50));
  }

  /**
   * Tests deletion of a middle node that forces removal of its
   * successor
   * from deeper levels of the tree. Ensures tree structure
   * integrity
   * is maintained after deletion.
   */
  @Test
  public void testDeleteMiddleNodeForcingSuccessorRemoval() {
    tree.insert(new City("A", 30, 30));
    tree.insert(new City("B", 20, 40));
    tree.insert(new City("C", 50, 20));
    tree.insert(new City("D", 45, 25));
    tree.insert(new City("E", 60, 15));
    tree.insert(new City("F", 55, 18));

    assertFuzzyEquals("1  B (20, 40)\n0A (30, 30)\n"
        + "3      F (55, 18)\n2    E (60, 15)\n"
        + "1  C (50, 20)\n2    D (45, 25)\n",
        tree.printTree());

    tree.delete(30, 30);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("1  B (20, 40)\n0D (45, 25)\n"
        + "3      F (55, 18)\n2    E (60, 15)\n"
        + "1  C (50, 20)\n", afterDelete);

    assertNull(tree.find(30, 30));
  }

  /**
   * Tests deletion of the root node when only the left subtree
   * exists.
   * Ensures that the deepest node in the left subtree is promoted
   * correctly.
   */
  @Test
  public void testDeleteWithLeftSubtreeOnlySuccessorCase() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("L", 30, 60));
    tree.insert(new City("LL", 20, 55));
    tree.insert(new City("LLL", 10, 58));

    assertFuzzyEquals("3      LLL (10, 58)\n"
        + "2    LL (20, 55)\n1  L (30, 60)\n"
        + "0Root (50, 50)\n", tree.printTree());

    tree.delete(50, 50);

    String afterDelete = tree.printTree();
    assertFuzzyEquals("0LLL (10, 58)\n2    LL (20, 55)\n"
        + "1  L (30, 60)\n", afterDelete);

    assertNull(tree.find(50, 50));
  }

}