import student.TestCase;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * Comprehensive test suite for KDTree with full line and mutation coverage.
 * All expected outputs are verified against actual KDTree behavior.
 * 
 * IMPORTANT: KDTree inserts equal values to the RIGHT (not left).
 * 
 * @author Test Suite
 * @version 2.0
 */
public class KDTreeTest extends TestCase {

  private KDTree tree;
  private City[] testCities;

  @Before
  public void setUp() {
    tree = new KDTree();
    testCities = new City[] { 
        new City("CityA", 50, 50), 
        new City("CityB", 25, 75), 
        new City("CityC", 75, 25), 
        new City("CityD", 10, 30),
        new City("CityE", 90, 80), 
        new City("CityF", 30, 40), 
        new City("CityG", 60, 60),
        new City("Origin", 0, 0), 
        new City("MaxPoint", 100, 100) 
    };
  }

  // ===== INSERTION TESTS =====

  @Test
  public void testBasicInsertion() {
    assertTrue("Should insert first city", tree.insert(testCities[0]));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());
    
    assertTrue("Should insert second city", tree.insert(testCities[1]));
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n", tree.printTree());
    
    assertTrue("Should insert third city", tree.insert(testCities[2]));
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n1  CityC (75, 25)\n", tree.printTree());
  }

  @Test
  public void testDuplicateInsertion() {
    City city1 = new City("First", 10, 20);
    City city2 = new City("Second", 10, 20);

    assertTrue("Should insert first city", tree.insert(city1));
    assertFuzzyEquals("0First (10, 20)\n", tree.printTree());
    
    assertFalse("Should reject duplicate coordinates", tree.insert(city2));
    assertFuzzyEquals("0First (10, 20)\n", tree.printTree());
  }

  @Test
  public void testSameNameDifferentCoordinates() {
    City city1 = new City("SameName", 10, 20);
    City city2 = new City("SameName", 30, 40);

    assertTrue(tree.insert(city1));
    assertTrue(tree.insert(city2));
    assertFuzzyEquals("0SameName (10, 20)\n1  SameName (30, 40)\n", tree.printTree());

    assertNotNull(tree.find(10, 20));
    assertNotNull(tree.find(30, 40));
  }

  @Test
  public void testNullInsertion() {
    assertFalse("Should reject null city", tree.insert(null));
    assertFuzzyEquals("", tree.printTree());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCityName() {
    new City(null, 10, 20);
  }
 
  @Test
  public void testEdgeCaseInsertion() {
    City negativeCity = new City("Negative", -10, -5);
    City zeroCity = new City("Zero", 0, 0);
    City largeCity = new City("Large", 999999, 999999);

    assertTrue(tree.insert(negativeCity));
    assertTrue(tree.insert(zeroCity));
    assertTrue(tree.insert(largeCity));
    assertFuzzyEquals("0Negative (-10, -5)\n1  Zero (0, 0)\n2    Large (999999, 999999)\n", tree.printTree());
  }

  @Test
  public void testEqualValuesGoRight() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("EqualX", 50, 75));
    assertFuzzyEquals("0Root (50, 50)\n1  EqualX (50, 75)\n", tree.printTree());
    
    tree = new KDTree();
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 60));
    tree.insert(new City("EqualY", 30, 60));
    assertFuzzyEquals("1  Left (25, 60)\n2    EqualY (30, 60)\n0Root (50, 50)\n", tree.printTree());
  }

  @Test
  public void testMultipleEqualXValues() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 50, 60));
    tree.insert(new City("C", 50, 40));
    tree.insert(new City("D", 50, 70));
    assertFuzzyEquals("0A (50, 50)\n2    C (50, 40)\n1  B (50, 60)\n2    D (50, 70)\n", tree.printTree());
  }

  // ===== FIND TESTS =====

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
    
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n1  CityC (75, 25)\n", tree.printTree());
  }

  @Test
  public void testFindNonExistent() {
    tree.insert(testCities[0]);
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());

    assertNull(tree.find(999, 999));
    assertNull(tree.find(0, 0));
  }

  @Test
  public void testFindInEmptyTree() {
    assertNull(tree.find(50, 50));
    assertFuzzyEquals("", tree.printTree());
  }

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
    
    assertFuzzyEquals("0A (50, 50)\n2    C (50, 40)\n1  B (50, 60)\n", tree.printTree());
  }

  @Test
  public void testFindDeepNode() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 25));
    tree.insert(new City("C", 10, 10));
    tree.insert(new City("D", 5, 5));
    
    City found = tree.find(5, 5);
    assertNotNull(found);
    assertEquals("D", found.getName());
    
    assertFuzzyEquals("3      D (5, 5)\n2    C (10, 10)\n1  B (25, 25)\n0A (50, 50)\n", tree.printTree());
  }

  // ===== DELETE TESTS =====

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
    assertFuzzyEquals("1  CityD (10, 30)\n0CityA (50, 50)\n", tree.printTree());
  }

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

  @Test
  public void testDeleteNonExistent() {
    tree.insert(testCities[0]);
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());

    String result = tree.delete(999, 999);
    assertFalse(result.trim().contains("\n"));
    assertTrue(result.matches("\\d+ ?"));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());
  }

  @Test
  public void testDeleteFromEmptyTree() {
    String result = tree.delete(50, 50);
    assertEquals("0 ", result);
    assertFuzzyEquals("", tree.printTree());
  }

  @Test
  public void testDeleteRoot() {
    tree.insert(new City("Root", 50, 50));
    
    String result = tree.delete(50, 50);
    assertTrue(result.contains("Root"));
    assertNull(tree.find(50, 50));
    assertFuzzyEquals("", tree.printTree());
  }

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
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n1  CityC (75, 25)\n", tree.printTree());
  }

  @Test
  public void testZeroRadiusSearch() {
    tree.insert(testCities[0]);
    tree.insert(testCities[1]);

    String result = tree.search(50, 50, 0);
    assertTrue(result.contains("CityA"));
    assertFalse(result.contains("CityB"));
    assertFuzzyEquals("1  CityB (25, 75)\n0CityA (50, 50)\n", tree.printTree());
  }

  @Test
  public void testNegativeRadiusSearch() {
    tree.insert(testCities[0]);
    assertEquals("", tree.search(50, 50, -5));
    assertFuzzyEquals("0CityA (50, 50)\n", tree.printTree());
  }

  @Test
  public void testSearchInEmptyTree() {
    assertEquals("0", tree.search(50, 50, 100));
    assertFuzzyEquals("", tree.printTree());
  }

  @Test
  public void testLargeRadiusSearch() {
    for (int i = 0; i < 5; i++) {
      tree.insert(testCities[i]);
    }

    String result = tree.search(50, 50, 1000);
    int cityCount = result.split("\n").length - 1;
    assertTrue(cityCount >= 3);
    assertFuzzyEquals("2    CityD (10, 30)\n1  CityB (25, 75)\n0CityA (50, 50)\n1  CityC (75, 25)\n2    CityE (90, 80)\n", tree.printTree());
  }

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

  @Test
  public void testPrintSingleNode() {
    tree.insert(new City("Solo", 50, 50));
    assertFuzzyEquals("0Solo (50, 50)\n", tree.printTree());
  }

  @Test
  public void testPrintThreeNodes() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 25));
    tree.insert(new City("Right", 75, 75));
    assertFuzzyEquals("1  Left (25, 25)\n0Root (50, 50)\n1  Right (75, 75)\n", tree.printTree());
  }

  @Test
  public void testPrintMultipleDepths() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 75));
    tree.insert(new City("C", 75, 25));
    tree.insert(new City("D", 10, 30));
    assertFuzzyEquals("2    D (10, 30)\n1  B (25, 75)\n0A (50, 50)\n1  C (75, 25)\n", tree.printTree());
  }

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
    assertFuzzyEquals("2    Baltimore (0, 300)\n3      Washington (5, 350)\n1  Atlanta (10, 500)\n2    L (11, 500)\n0Chicago (100, 150)\n1  Tacoma (1000, 100)\n2    L (101, 150)\n", tree.printTree());
  }

  @Test
  public void testPrintDeepLeftSubtree() {
    tree.insert(new City("Root", 100, 100));
    tree.insert(new City("L1", 50, 150));
    tree.insert(new City("L2", 25, 125));
    tree.insert(new City("L3", 10, 110));
    tree.insert(new City("L4", 5, 105));
    assertFuzzyEquals("4        L4 (5, 105)\n3      L3 (10, 110)\n2    L2 (25, 125)\n1  L1 (50, 150)\n0Root (100, 100)\n", tree.printTree());
  }

  @Test
  public void testPrintBalancedTree() {
    tree.insert(new City("M", 50, 50));
    tree.insert(new City("A", 25, 25));
    tree.insert(new City("Z", 75, 75));
    tree.insert(new City("D", 10, 40));
    tree.insert(new City("B", 30, 10));
    tree.insert(new City("X", 60, 80));
    tree.insert(new City("Y", 80, 60));
    assertFuzzyEquals("2    B (30, 10)\n1  A (25, 25)\n2    D (10, 40)\n0M (50, 50)\n2    Y (80, 60)\n1  Z (75, 75)\n2    X (60, 80)\n", tree.printTree());
  }

  @Test
  public void testPrintAfterDeletion() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 25, 75));
    tree.insert(new City("C", 75, 25));
    tree.insert(new City("D", 10, 30));
    tree.delete(25, 75);
    assertFuzzyEquals("1  D (10, 30)\n0A (50, 50)\n1  C (75, 25)\n", tree.printTree());
  }

  @Test
  public void testPrintEmptyTree() {
    assertEquals("", tree.printTree());
  }

  // ===== DISTANCE CALCULATION TESTS =====

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
    assertFuzzyEquals("0Origin (0, 0)\n1  East3 (3, 0)\n2    North4 (0, 4)\n3      NE5 (3, 4)\n4        Far (10, 10)\n", tree.printTree());
  }

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
    assertFuzzyEquals("0Center (10, 10)\n1  Right (13, 10)\n2    Up (10, 13)\n3      UpRight (13, 13)\n", tree.printTree());
  }

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
    assertFuzzyEquals("2    LeftDown (47, 47)\n1  Left (47, 50)\n0Center (50, 50)\n1  Down (50, 47)\n", tree.printTree());
  }

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
    assertFuzzyEquals("2    LL (15, 55)\n1  Left (20, 60)\n2    LR (25, 85)\n0Root (50, 50)\n2    RL (70, 10)\n1  Right (80, 40)\n2    RR (90, 70)\n", tree.printTree());
  }

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
    assertFuzzyEquals("1  LeftExact (40, 50)\n0Root (50, 50)\n2    DownExact (50, 40)\n1  RightExact (60, 50)\n2    UpExact (50, 60)\n", tree.printTree());
  }

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

  @Test
  public void testInsertionStructureMutation() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 30, 60));
    tree.insert(new City("C", 70, 40));
    tree.insert(new City("D", 25, 55));
    tree.insert(new City("E", 35, 65));
    assertFuzzyEquals("2    D (25, 55)\n1  B (30, 60)\n2    E (35, 65)\n0A (50, 50)\n1  C (70, 40)\n", tree.printTree());
    
    assertNotNull(tree.find(50, 50));
    assertNotNull(tree.find(30, 60));
    assertNotNull(tree.find(70, 40));
    assertNotNull(tree.find(25, 55));
    assertNotNull(tree.find(35, 65));
  }

  @Test
  public void testEqualValueInsertionMutation() {
    tree.insert(new City("A", 50, 50));
    tree.insert(new City("B", 50, 60));
    tree.insert(new City("C", 40, 60));
    tree.insert(new City("D", 45, 60));
    assertFuzzyEquals("1  C (40, 60)\n2    D (45, 60)\n0A (50, 50)\n1  B (50, 60)\n", tree.printTree());
    
    assertNotNull(tree.find(50, 60));
    assertNotNull(tree.find(40, 60));
    assertNotNull(tree.find(45, 60));
  }

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
    assertFuzzyEquals("0Origin (0, 0)\n1  X5 (5, 0)\n2    Y5 (0, 5)\n3      XY5 (3, 4)\n4        Far (8, 8)\n", tree.printTree());
  }

  @Test
  public void testAxisSwitchingMutation() {
    tree.insert(new City("D0", 50, 50));
    tree.insert(new City("D1L", 30, 70));
    tree.insert(new City("D1R", 80, 30));
    tree.insert(new City("D2LL", 20, 60));
    tree.insert(new City("D2LR", 40, 80));
    tree.insert(new City("D2RL", 70, 20));
    tree.insert(new City("D2RR", 90, 40));
    assertFuzzyEquals("2    D2LL (20, 60)\n1  D1L (30, 70)\n2    D2LR (40, 80)\n0D0 (50, 50)\n2    D2RL (70, 20)\n1  D1R (80, 30)\n2    D2RR (90, 40)\n", tree.printTree());
    
    assertNotNull(tree.find(20, 60));
    assertNotNull(tree.find(40, 80));
    assertNotNull(tree.find(70, 20));
    assertNotNull(tree.find(90, 40));
  }

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
    assertFuzzyEquals("2    Baltimore (0, 300)\n3      Washington (5, 350)\n1  Atlanta (10, 500)\n2    L (11, 500)\n0Chicago (100, 150)\n1  Tacoma (1000, 100)\n2    L (101, 150)\n", tree.printTree());
    
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

  @Test
  public void testTreeStructureIntegrity() {
    City[] orderedCities = { 
        new City("Root", 50, 50),
        new City("LeftX", 25, 60),
        new City("RightX", 75, 40),
        new City("LeftLeftY", 20, 30),
        new City("LeftRightY", 30, 70)
    };

    for (City city : orderedCities) {
      assertTrue(tree.insert(city));
    }

    for (City city : orderedCities) {
      City found = tree.find(city.getX(), city.getY());
      assertNotNull(found);
      assertEquals(city.getName(), found.getName());
    }
    assertFuzzyEquals("2    LeftLeftY (20, 30)\n1  LeftX (25, 60)\n2    LeftRightY (30, 70)\n0Root (50, 50)\n1  RightX (75, 40)\n", tree.printTree());
  }

  @Test
  public void testLargerDataset() {
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 5; y++) {
        City city = new City("City_" + x + "_" + y, x * 10, y * 10);
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

  @Test
  public void testDeleteNameRecording() {
    tree.insert(new City("Target", 50, 50));
    tree.insert(new City("Replacement", 60, 40));

    String result = tree.delete(50, 50);
    String[] parts = result.split("\n");
    assertEquals(2, parts.length);
    assertTrue(parts[0].matches("\\d+"));
    assertEquals("Target", parts[1]);
    assertFuzzyEquals("0Replacement (60, 40)\n", tree.printTree());
  }

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

  @Test
  public void testPrintTreeDepthCalculation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Left", 25, 25));
    tree.insert(new City("Right", 75, 75));
    tree.insert(new City("LL", 10, 10));
    tree.insert(new City("LR", 40, 40));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    boolean hasDepth0 = false, hasDepth1 = false, hasDepth2 = false;
    for (String line : lines) {
      if (line.startsWith("0")) hasDepth0 = true;
      if (line.startsWith("1")) hasDepth1 = true;
      if (line.startsWith("2")) hasDepth2 = true;
    }

    assertTrue(hasDepth0);
    assertTrue(hasDepth1);
    assertTrue(hasDepth2);
    assertFuzzyEquals("2    LL (10, 10)\n1  Left (25, 25)\n2    LR (40, 40)\n0Root (50, 50)\n1  Right (75, 75)\n", tree.printTree());
  }

  @Test
  public void testPrintTreeIndentation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Child", 25, 25));
    tree.insert(new City("Grand", 10, 10));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    String rootLine = null, childLine = null, grandLine = null;
    for (String line : lines) {
      if (line.contains("Root")) rootLine = line;
      if (line.contains("Child")) childLine = line;
      if (line.contains("Grand")) grandLine = line;
    }

    assertNotNull(rootLine);
    assertNotNull(childLine);
    assertNotNull(grandLine);
    assertTrue(rootLine.matches("0Root.*"));
    assertTrue(childLine.matches("1  Child.*"));
    assertTrue(grandLine.matches("2    Grand.*"));
    assertFuzzyEquals("2    Grand (10, 10)\n1  Child (25, 25)\n0Root (50, 50)\n", tree.printTree());
  }

  @Test
  public void testRootNodeNoIndentation() {
    tree.insert(new City("Root", 50, 50));
    tree.insert(new City("Child", 25, 25));

    String result = tree.printTree();
    String[] lines = result.split("\n");

    String rootLine = null, childLine = null;
    for (String line : lines) {
      if (line.contains("Root")) rootLine = line;
      if (line.contains("Child")) childLine = line;
    }

    assertNotNull(rootLine);
    assertNotNull(childLine);
    assertTrue(rootLine.startsWith("0Root"));
    assertFalse(rootLine.startsWith("0 "));
    assertTrue(childLine.startsWith("1  "));
    assertFuzzyEquals("1  Child (25, 25)\n0Root (50, 50)\n", tree.printTree());
  }

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
    assertFuzzyEquals("1  Left (25, 25)\n0Root (50, 50)\n1  Right (75, 75)\n2    RightRight (90, 90)\n3      RRR (95, 95)\n", tree.printTree());
  }

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
    assertFuzzyEquals("3      D3 (5, 5)\n2    D2 (10, 10)\n1  D1 (25, 25)\n0D0 (50, 50)\n", tree.printTree());
  }

  // ===== EDGE CASE COVERAGE =====

  @Test
  public void testCompareByAxisBothDirections() {
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("MoreX", 60, 50));
    tree.insert(new City("LessX", 40, 50));
    assertFuzzyEquals("1  LessX (40, 50)\n0Center (50, 50)\n1  MoreX (60, 50)\n", tree.printTree());
    
    tree = new KDTree();
    tree.insert(new City("Center", 50, 50));
    tree.insert(new City("LessXMoreY", 40, 60));
    tree.insert(new City("LessXLessY", 30, 40));
    assertFuzzyEquals("2    LessXLessY (30, 40)\n1  LessXMoreY (40, 60)\n0Center (50, 50)\n", tree.printTree());
  }

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
}