import student.TestCase;
import org.junit.Test;
import org.junit.Before;

/**
 * Comprehensive test suite for KDTree with full mutation coverage.
 * Optimized version with consolidated test cases.
 * 
 * @author Jonah Schepers
 * @author Rowan Muhoberac
 * @version Oct 12, 2025
 */
public class KDTreeTest extends TestCase {

  private KDTree tree;
  private City[] testCities;

  /**
   * Sets up test fixture before each test method.
   * Initializes a new KDTree and array of test cities.
   */
  @Before
  public void setUp() {
    tree = new KDTree();
    testCities = new City[] {
        new City("CityA", 50, 50),
        new City("CityB", 25, 75),
        new City("CityC", 75, 25),
        new City("CityD", 10, 30),
        new City("CityE", 90, 80)
    };
  }

  /**
   * Tests basic insertion operations and verifies tree structure
   * after each insertion using in-order traversal output.
   */
  @Test
  public void testBasicInsertionAndStructure() {
    assertTrue(tree.insert(testCities[0]));
    assertEquals("0CityA (50, 50)\n", tree.printTree());

    assertTrue(tree.insert(testCities[1]));
    assertEquals("1  CityB (25, 75)\n0CityA (50, 50)\n",
        tree.printTree());

    assertTrue(tree.insert(testCities[2]));
    assertEquals(
        "1  CityB (25, 75)\n0CityA (50, 50)\n1  CityC (75, 25)\n",
        tree.printTree());
  }

  /**
   * Tests that duplicate coordinate insertions are rejected
   * and null city insertions are rejected.
   */
  @Test
  public void testDuplicateAndNullInsertion() {
    City city1 = new City("First", 10, 20);
    City city2 = new City("Second", 10, 20);

    assertTrue(tree.insert(city1));
    assertFalse(tree.insert(city2));
    assertFalse(tree.insert(null));
    assertEquals("0First (10, 20)\n", tree.printTree());
  }

  /**
   * Tests that equal coordinate values are inserted to the right
   * subtree.
   * Verifies correct behavior for both X-axis and Y-axis equal
   * values.
   */
  @Test
  public void testEqualValuesGoRight() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("EqualX", 50, 75));
    assertEquals("0Root (50, 50)\n1  EqualX (50, 75)\n",
        tree.printTree());

    tree = new KDTree();
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 60));
    tree.insert(new City("EqualY", 30, 60));
    assertEquals(
        "1  Left (25, 60)\n2    EqualY (30, 60)\n0Root (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests insertion with edge case coordinates including
   * negative values, zero values, and very large values.
   */
  @Test
  public void testEdgeCaseCoordinates() {
    assertTrue(tree.insert(new City("Negative", -10, -5)));
    assertTrue(tree.insert(new City("Zero", 0, 0)));
    assertTrue(tree.insert(new City("Large", 999999, 999999)));
    assertEquals(
        "0Negative (-10, -5)\n1  Zero (0, 0)\n"
            + "2    Large (999999, 999999)\n",
        tree.printTree());
  }

  /**
   * Tests find operations for existing cities, non-existent cities,
   * and searches in an empty tree.
   */
  @Test
  public void testFindOperations() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);
    tree.insert(testCities[2]);

    City found = tree.find(50, 50);
    assertNotNull(found);
    assertEquals("CityA", found.getName());

    assertNull(tree.find(999, 999));
    assertNull(new KDTree().find(50, 50));
  }

  /**
   * Tests deletion of leaf nodes and nodes with one child.
   * Verifies that the deleted node is removed and remaining nodes
   * persist.
   */
  @Test
  public void testDeleteLeafAndOneChild() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);
    tree.insert(testCities[3]);

    String result = tree.delete(25, 75);
    assertTrue(result.contains("CityB"));
    assertNull(tree.find(25, 75));
    assertNotNull(tree.find(10, 30));
    assertEquals("1  CityD (10, 30)\n0CityA (50, 50)\n",
        tree.printTree());
  }

  /**
   * Tests deletion of nodes with two children.
   * Verifies correct replacement with minimum from appropriate
   * subtree.
   */
  @Test
  public void testDeleteTwoChildren() {
    for (int i = 0; i < 5; i++) {
      tree.insert(testCities[i]);
    }

    String result = tree.delete(50, 50);
    assertTrue(result.contains("CityA"));
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(75, 25));
  }

  /**
   * Tests deletion of non-existent nodes and deletion from empty
   * tree.
   * Verifies appropriate return values and no tree corruption.
   */
  @Test
  public void testDeleteNonExistentAndEmpty() {
    tree.insert(testCities[0]);
    String result = tree.delete(999, 999);
    assertFalse(result.trim().contains("\n"));
    assertTrue(result.matches("\\d+ ?"));

    assertEquals("0 ", new KDTree().delete(50, 50));
  }

  /**
   * Tests deletion in trees with complex subtree structures.
   * Verifies all remaining nodes are still accessible after
   * deletion.
   */
  @Test
  public void testDeleteComplexSubtrees() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Right", 70, 30));
    tree.insert(new City("RR1", 80, 40));
    tree.insert(new City("RR2", 90, 20));

    tree.delete(50, 50);
    assertNull(tree.find(50, 50));
    assertNotNull(tree.find(70, 30));
    assertNotNull(tree.find(90, 20));
  }

  /**
   * Tests basic range search within a specified radius.
   * Verifies that cities within range are found and visit count is
   * included.
   */
  @Test
  public void testBasicRangeSearch() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);
    tree.insert(testCities[2]);

    String result = tree.search(50, 50, 30);
    assertTrue(result.contains("CityA"));
    String[] lines = result.split("\n");
    assertTrue(lines[lines.length - 1].trim().matches("\\d+"));
  }

  /**
   * Tests search boundary conditions including zero radius,
   * negative radius, and searches in empty tree.
   */
  @Test
  public void testSearchBoundaries() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);

    String result = tree.search(50, 50, 0);
    assertTrue(result.contains("CityA"));
    assertFalse(result.contains("CityB"));

    assertEquals("", tree.search(50, 50, -5));
    assertEquals("0", new KDTree().search(50, 50, 100));
  }

  /**
   * Tests that range search correctly prunes branches outside
   * search radius.
   * Verifies far nodes are not included in results.
   */
  @Test
  public void testSearchPruning() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("Near", 55, 50));
    tree.insert(new City("Far", 200, 200));

    String result = tree.search(50, 50, 10);
    assertTrue(result.contains("Center"));
    assertTrue(result.contains("Near"));
    assertFalse(result.contains("Far"));
  }

  /**
   * Tests precise distance calculations in range search using
   * known coordinate patterns (3-4-5 right triangle).
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
  }

  /**
   * Tests exact distance boundary conditions.
   * Verifies cities at exactly the radius distance are included.
   */
  @Test
  public void testBoundaryDistances() {
    tree.insert(new City("Center", 10, 10));
    tree.insert(new City("Right", 13, 10));
    tree.insert(new City("Up", 10, 13));
    tree.insert(new City("UpRight", 13, 13));

    String result = tree.search(10, 10, 3);
    assertTrue(result.contains("Right"));
    assertTrue(result.contains("Up"));
    assertFalse(result.contains("UpRight"));
  }

  /**
   * Tests mutation coverage for search left boundary pruning.
   * Kills mutation where diff > -radius is replaced with true.
   * Verifies left subtree is not visited when outside radius.
   */
  @Test
  public void testSearchLeftBoundaryExcluded() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("Left", 50, 100));
    tree.insert(new City("Right", 150, 100));

    String result = tree.search(140, 100, 5);
    String[] lines = result.split("\n");
    int visitCount = Integer.parseInt(lines[lines.length - 1].trim());
    assertEquals(2, visitCount);
  }

  /**
   * Tests mutation coverage for search right boundary pruning.
   * Kills mutation where diff <= radius is replaced with true.
   * Verifies right subtree is not visited when outside radius.
   */
  @Test
  public void testSearchRightBoundaryExcluded() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("Left", 50, 100));
    tree.insert(new City("Right", 150, 100));

    String result = tree.search(60, 100, 5);
    String[] lines = result.split("\n");
    int visitCount = Integer.parseInt(lines[lines.length - 1].trim());
    assertEquals(2, visitCount);
  }

  /**
   * Tests search correctly calculates X-axis difference for
   * pruning.
   * Verifies nodes beyond search radius along X-axis are excluded.
   */
  @Test
  public void testSearchDifferenceCalculationXAxis() {
    tree.insert(new City("Root", 100, 50));
    tree.insert(new City("LeftChild", 50, 60));
    tree.insert(new City("RightChild", 150, 40));
    tree.insert(new City("FarLeft", 20, 70));
    tree.insert(new City("FarRight", 180, 30));

    String result = tree.search(100, 50, 30);
    assertTrue(result.contains("Root"));
    assertFalse(result.contains("FarLeft"));
  }

  /**
   * Tests search correctly calculates Y-axis difference for
   * pruning.
   * Verifies nodes beyond search radius along Y-axis are excluded.
   */
  @Test
  public void testSearchDifferenceCalculationYAxis() {
    tree.insert(new City("Root", 50, 100));
    tree.insert(new City("BelowRoot", 40, 100));
    tree.insert(new City("LowChild", 40, 60));
    tree.insert(new City("VeryLow", 40, 30));

    String result = tree.search(50, 100, 25);
    assertTrue(result.contains("Root"));
    assertTrue(result.contains("BelowRoot"));
    assertFalse(result.contains("VeryLow"));
  }

  /**
   * Tests mutation coverage for depth increment in left recursion.
   * Kills mutation where depth + 1 is replaced with depth or 1.
   * Verifies exact visit count to detect wrong axis calculations.
   */
  @Test
  public void testSearchLeftRecursionDepthIncrement() {
    tree.insert(new City("A", 100, 100));
    tree.insert(new City("B", 50, 150));
    tree.insert(new City("C", 25, 150));
    tree.insert(new City("D", 75, 150));
    tree.insert(new City("E", 150, 100));

    String result = tree.search(40, 150, 30);
    String expected = "B (50, 150)\nC (25, 150)\n4";
    assertEquals(expected, result);
  }

  /**
   * Tests mutation coverage for depth increment in right
   * recursion.
   * Kills mutation where depth + 1 is replaced with depth or 1.
   * Verifies exact visit count to detect wrong axis calculations.
   */
  @Test
  public void testSearchRightRecursionDepthIncrement() {
    tree.insert(new City("A", 100, 100));
    tree.insert(new City("B", 50, 100));
    tree.insert(new City("C", 150, 150));
    tree.insert(new City("D", 150, 125));
    tree.insert(new City("E", 150, 175));

    String result = tree.search(160, 150, 30);
    String expected = "C (150, 150)\nD (150, 125)\nE (150, 175)\n4";
    assertEquals(expected, result);
  }

  /**
   * Tests mutation coverage for both X and Y coordinate matching
   * in delete.
   * Kills mutations where getX()==x or getY()==y are replaced with
   * true.
   * Verifies only exact coordinate match is deleted.
   */
  @Test
  public void testDeleteCoordinateMatchingBothAxis() {
    City cityA = new City("CityA", 50, 50);
    City cityB = new City("CityB", 25, 75);
    City cityC = new City("CityC", 50, 75);
    City cityD = new City("CityD", 25, 50);

    tree.insert(cityA);
    tree.insert(cityB);
    tree.insert(cityC);
    tree.insert(cityD);

    String result = tree.delete(25, 75);
    assertTrue(result.contains("CityB"));
    assertNull(tree.find(25, 75));

    assertNotNull(tree.find(50, 50));
    assertEquals("CityA", tree.find(50, 50).getName());
    assertNotNull(tree.find(50, 75));
    assertEquals("CityC", tree.find(50, 75).getName());
    assertNotNull(tree.find(25, 50));
    assertEquals("CityD", tree.find(25, 50).getName());

    result = tree.delete(50, 75);
    assertTrue(result.contains("CityC"));
    assertNull(tree.find(50, 75));
    assertNotNull(tree.find(50, 50));
    assertEquals("CityA", tree.find(50, 50).getName());
  }

  /**
   * Tests mutation coverage for X coordinate check in delete.
   * Kills mutation where getX()==x is replaced with true.
   * Uses left traversal path to test the mutation.
   */
  @Test
  public void testDeleteXCoordinateMutation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 30, 75));
    tree.insert(new City("Target", 25, 75));

    String result = tree.delete(25, 75);
    assertTrue(result.contains("Target"));
    assertNull(tree.find(25, 75));
    assertNotNull(tree.find(30, 75));
    assertEquals("Left", tree.find(30, 75).getName());
    assertNotNull(tree.find(50, 50));
  }

  /**
   * Tests mutation coverage for depth increment in right subtree
   * deletion.
   * Kills mutation where depth + 1 is replaced with 1 in right
   * recursion.
   * Verifies tree structure integrity after deletion.
   */
  @Test
  public void testDeleteRightSubtreeDepthIncrement() {
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
   * Tests findMin explores left subtree when searching for
   * minimum.
   * Verifies correct minimum node is found along specified axis.
   */
  @Test
  public void testFindMinLine162ExplicitLeftExploration() {
    tree.insert(new City("Root", 200, 200));
    tree.insert(new City("Left", 100, 250));
    tree.insert(new City("LL", 80, 240));
    tree.insert(new City("LR", 120, 260));
    tree.insert(new City("LLL", 70, 235));
    tree.insert(new City("LLR", 90, 245));

    tree.delete(200, 200);
    assertNull(tree.find(200, 200));
    assertNotNull(tree.find(70, 235));
    assertNotNull(tree.find(100, 250));
  }

  /**
   * Tests findMin with equal axis values.
   * Verifies correct minimum selection when multiple nodes have
   * equal
   * coordinates.
   */
  @Test
  public void testFindMinWithEqualAxisValues() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("R1", 150, 100));
    tree.insert(new City("R2", 150, 90));
    tree.insert(new City("R3", 150, 95));
    tree.insert(new City("R4", 140, 90));

    tree.delete(100, 100);
    assertNull(tree.find(100, 100));
    assertNotNull(tree.find(150, 100));
    assertNotNull(tree.find(140, 90));
  }

  /**
   * Tests deletion forces findMin to search both left and right
   * subtrees.
   * Verifies correct successor selection in complex tree
   * structures.
   */
  @Test
  public void testDeleteNodeForcingFindMinBothSubtrees() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 30, 60));
    tree.insert(new City("Right", 70, 40));
    tree.insert(new City("LL", 20, 55));
    tree.insert(new City("LR", 35, 65));

    tree.delete(30, 60);
    assertNull(tree.find(30, 60));
    assertNotNull(tree.find(20, 55));
    assertNotNull(tree.find(35, 65));
  }

  /**
   * Tests deletion with deep findMin traversal.
   * Verifies correct successor removal from multiple levels deep.
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

    tree.delete(30, 60);
    assertNull(tree.find(30, 60));
    assertNotNull(tree.find(15, 52));
    assertNotNull(tree.find(40, 65));
  }

  /**
   * Tests multiple sequential deletions stress-testing findMin.
   * Verifies tree integrity after multiple complex deletions.
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
   * Tests printTree produces correct depth and indentation
   * formatting.
   * Verifies root has no indentation while children have proper
   * spacing.
   */
  @Test
  public void testPrintTreeDepthAndIndentation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 25));
    tree.insert(new City("Right", 75, 75));
    tree.insert(new City("LL", 10, 10));
    tree.insert(new City("LR", 40, 40));

    String result = tree.printTree();
    assertTrue(result.contains("0Root"));
    assertTrue(result.contains("1  Left"));
    assertTrue(result.contains("2    LL"));
  }

  /**
   * Fuzzy test with random coordinate insertions and deletions.
   * Verifies tree maintains integrity through complex operations.
   */
  @Test
  public void testFuzzyRandomInsertDeleteSequence() {
    int[][] coords = {
        { 500, 100 },
        { 400, 200 },
        { 600, 50 },
        { 300, 300 },
        { 700, 25 },
        { 350, 250 },
        { 450, 150 },
        { 250, 350 }
    };

    for (int i = 0; i < coords.length; i++) {
      tree.insert(new City("C" + i, coords[i][0], coords[i][1]));
    }

    tree.delete(500, 100);
    tree.delete(400, 200);
    tree.delete(300, 300);

    assertNotNull(tree.find(600, 50));
    assertNotNull(tree.find(700, 25));
    assertNull(tree.find(500, 100));
    assertNull(tree.find(300, 300));
  }

  /**
   * Fuzzy test with clustered coordinates going left in tree.
   * Tests depth increment mutations in left recursion paths.
   */
  @Test
  public void testFuzzyClusteredCoordinates() {
    for (int i = 95; i <= 105; i++) {
      tree.insert(new City("C" + i, i, 206 - i));
    }

    tree.delete(100, 106);
    tree.delete(101, 105);
    tree.delete(102, 104);

    assertNull(tree.find(100, 106));
    assertNull(tree.find(101, 105));
    assertNull(tree.find(102, 104));
    assertNotNull(tree.find(98, 108));
    assertNotNull(tree.find(103, 103));
  }

  /**
   * Fuzzy test with extreme depth scenario going left.
   * Tests correct axis selection at very deep levels.
   */
  @Test
  public void testFuzzyExtremeDepthScenario() {
    int[] coords = {
        2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1
    };
    for (int i = 0; i < coords.length; i++) {
      tree.insert(new City("D" + i, coords[i], 3072 - coords[i]));
    }

    tree.delete(2048, 1024);
    tree.delete(1024, 2048);
    tree.delete(512, 2560);

    assertNull(tree.find(2048, 1024));
    assertNotNull(tree.find(1, 3071));
    assertNotNull(tree.find(8, 3064));
  }

  /**
   * Fuzzy test with clustered coordinates going right in tree.
   * Tests depth increment mutations in right recursion paths.
   */
  @Test
  public void testFuzzyRightClusteredCoordinates() {
    for (int i = 95; i <= 105; i++) {
      tree.insert(new City("C" + i, 206 - i, i));
    }

    tree.delete(106, 100);
    tree.delete(105, 101);
    tree.delete(104, 102);

    assertNull(tree.find(106, 100));
    assertNull(tree.find(105, 101));
    assertNull(tree.find(104, 102));
    assertNotNull(tree.find(108, 98));
    assertNotNull(tree.find(103, 103));
  }

  /**
   * Fuzzy test with extreme depth scenario going right.
   * Tests correct axis selection at very deep levels in right
   * subtrees.
   */
  @Test
  public void testFuzzyRightExtremeDepthScenario() {
    int[] coords = {
        2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1
    };
    for (int i = 0; i < coords.length; i++) {
      tree.insert(new City("D" + i, 3072 - coords[i], coords[i]));
    }

    tree.delete(1024, 2048);
    tree.delete(2048, 1024);
    tree.delete(2560, 512);

    assertNull(tree.find(1024, 2048));
    assertNotNull(tree.find(3071, 1));
    assertNotNull(tree.find(3064, 8));
  }

  /**
   * Fuzzy test with extreme depth scenario going left.
   * Additional coverage for deep left traversal paths.
   */
  @Test
  public void testFuzzyLeftExtremeDepthScenario() {
    int[] coords = {
        2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1
    };
    for (int i = 0; i < coords.length; i++) {
      tree.insert(new City("D" + i, 3072 - coords[i], coords[i]));
    }

    tree.delete(1024, 2048);
    tree.delete(2048, 1024);
    tree.delete(2560, 512);
    assertNull(tree.find(1024, 2048));
    assertNotNull(tree.find(3071, 1));
    assertNotNull(tree.find(3064, 8));
  }

  /**
   * Tests deletion of root where successor is located deep in tree.
   * Verifies exact tree structure after deep successor removal.
   */
  @Test
  public void testDeleteWithDeepSuccessorRemoval() {
    tree.insert(new City("A", 40, 40));
    tree.insert(new City("B", 60, 50));
    tree.insert(new City("C", 70, 45));
    tree.insert(new City("D", 65, 48));
    tree.insert(new City("E", 75, 42));

    assertEquals(
        "0A (40, 40)\n3      D (65, 48)\n"
            + "2    C (70, 45)\n3      E (75, 42)\n"
            + "1  B (60, 50)\n",
        tree.printTree());

    tree.delete(40, 40);

    String afterDelete = tree.printTree();
    assertEquals(
        "0B (60, 50)\n1  E (75, 42)\n"
            + "3      D (65, 48)\n2    C (70, 45)\n",
        afterDelete);

    assertNull(tree.find(40, 40));
  }

  /**
   * Comprehensive scenario test combining multiple operations.
   * Tests insertion, finding, deletion, and search in sequence
   * with complex tree structure.
   */
  @Test
  public void testDelete() {
    assertTrue(tree.insert(new City("Chicago", 100, 150)));
    assertTrue(tree.insert(new City("Atlanta", 10, 500)));
    assertTrue(tree.insert(new City("Tacoma", 1000, 100)));
    assertTrue(tree.insert(new City("Baltimore", 0, 300)));
    assertTrue(tree.insert(new City("Washington", 5, 350)));
    assertFalse(tree.insert(new City("X", 100, 150)));
    assertTrue(tree.insert(new City("L", 101, 150)));
    assertTrue(tree.insert(new City("L", 11, 500)));
    assertEquals(
        "2    Baltimore (0, 300)\n"
            + "3      Washington (5, 350)\n"
            + "1  Atlanta (10, 500)\n2    L (11, 500)\n"
            + "0Chicago (100, 150)\n1  Tacoma (1000, 100)\n"
            + "2    L (101, 150)\n",
        tree.printTree());

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
}