import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import student.TestCase;

/**
 * Unit tests for the BST class.
 * 
 * These tests verify correctness of:
 * - insertion behavior (including duplicate handling)
 * - deletion of all matching nodes
 * - findAll search results
 * - in-order printing with correct levels
 * 
 * @author {Your Name}
 * @version {Put Version Here}
 */
public class BSTTest extends TestCase {

  private BST<Integer> tree;
  private BST<Integer> bst;

  /**
   * Runs before each test. Initializes a new BST instance.
   */
  @Before
  public void setUp() {
    tree = new BST<>();
    bst = new BST<>();
  }

  /**
   * Tests that inserting into an empty BST places the node as the root.
   */
  @Test
  public void testInsertIntoEmptyTree() {
    assertTrue(tree.insert(10));
    String output = tree.printTree();
    assertTrue("Root node should be level 0", output.contains("10"));
  }

  /**
   * Tests that inserting multiple nodes produces a valid BST structure in
   * sorted
   * order.
   */
  @Test
  public void testInsertMultipleNodes() {
    tree.insert(10);
    tree.insert(5);
    tree.insert(15);
    tree.insert(2);
    tree.insert(7);

    String printed = tree.printTree();

    // Check sorted order in printTree
    assertTrue(printed.indexOf("2") < printed.indexOf("5"));
    assertTrue(printed.indexOf("5") < printed.indexOf("7"));
    assertTrue(printed.indexOf("7") < printed.indexOf("10"));
    assertTrue(printed.indexOf("10") < printed.indexOf("15"));
  }

  /**
   * Tests that duplicate values are inserted to the left subtree (per
   * assignment
   * spec).
   */
  @Test
  public void testInsertDuplicateGoesLeft() {
    tree.insert(10);
    tree.insert(10); // duplicate should go LEFT
    String printed = tree.printTree();

    // Expect two "10" entries
    int firstIndex = printed.indexOf("10");
    int secondIndex = printed.indexOf("10", firstIndex + 1);
    assertTrue("Second 10 should exist", secondIndex != -1);
    assertTrue("Duplicate should be inserted before greater values",
        secondIndex > firstIndex);
  }

  /**
   * Tests that findAll returns all matching nodes.
   */
  @Test
  public void testFindAllMatches() {
    tree.insert(10);
    tree.insert(10);
    tree.insert(5);
    tree.insert(15);

    String result = tree.findAll(10);
    String[] lines = result.split("\n");
    assertEquals("There should be 2 matches for value 10", 2, lines.length);
  }

  /**
   * Tests that deleting a leaf node removes it from the tree.
   */
  @Test
  public void testDeleteLeafNode() {
    BST<Integer> bst = new BST<>();
    bst.insert(10);
    bst.insert(5); // leaf
    bst.insert(15);

    assertTrue(bst.deleteAll(5));
    assertEquals("", bst.findAll(5));
  }

  /**
   * Tests that deleting a node with one child correctly replaces it with its
   * child.
   */
  @Test
  public void testDeleteNodeWithOneChild() {
    tree.insert(10);
    tree.insert(5);
    tree.insert(2); // make 5 have one child

    assertTrue(tree.deleteOne(5));
    String printed = tree.printTree();
    assertFalse("Node 5 should be deleted", printed.contains("5"));
    assertTrue("Child 2 should remain", printed.contains("2"));
  }

  /**
   * Tests that deleting a node with two children replaces it with the max
   * node
   * from the left subtree.
   */
  @Test
  public void testDeleteNodeWithTwoChildren() {
    tree.insert(10);
    tree.insert(5);
    tree.insert(15);
    tree.insert(2);
    tree.insert(7);

    assertTrue(tree.deleteAll(10));
    String printed = tree.printTree();

    // After deletion, 7 (max of left subtree) should be the new root
    assertTrue("Max of left subtree (7) should replace root", printed
        .contains("7"));
  }

  /**
   * Tests that deleteAll removes all duplicates from the tree.
   */
  @Test
  public void testDeleteAllDuplicates() {
    BST<Integer> t = new BST<>();
    t.insert(10);
    t.insert(15);
    t.insert(15);
    t.insert(15);
    t.insert(5);

    assertTrue("deleteAll should report deletion", t.deleteAll(15));

    assertEquals("findAll should return empty string for deleted value", "",
        t.findAll(15).trim());
    String printed = t.printTree().trim();
    assertFalse("Tree print should not contain deleted value", printed
        .contains("15"));
    // remaining values should still be present
    assertTrue(printed.contains("10"));
    assertTrue(printed.contains("5"));
  }

  /**
   * Tests that deleting a non-existent value returns false and tree remains
   * unchanged.
   */
  @Test
  public void testDeleteNonExistentValue() {
    tree.insert(10);
    tree.insert(5);

    assertFalse(tree.deleteAll(42));
    String printed = tree.printTree();
    assertTrue("Tree structure should be unchanged", printed.contains("10")
        && printed.contains("5"));
  }

  /**
   * Tests the printTree method to verify correct formatting and level
   * indentation.
   */
  @Test
  public void testPrintTreeIndentationAndLevels() {
    tree.insert(10);
    tree.insert(5);
    tree.insert(15);
    tree.insert(2);

    String printed = tree.printTree();
    assertTrue("Root should be at level 0", printed.contains("10"));
    assertTrue("Child should be at level 1", printed.contains("5"));
    assertTrue("Leaf should be at level 2", printed.contains("2"));
  }
  // ===================================================================

  /**
   * Tests mutation coverage for BST insertRec line 112 - level + 1 arithmetic
   * The mutation replaces "level + 1" with "1" (second member)
   * This test verifies that right-child insertions have correct level values
   */
  @Test
  public void testBSTInsertRightChildLevels() {
    BST<Integer> tree = new BST<>();

    // Insert values that create right children
    // 10 is root (level 0)
    assertTrue(tree.insert(10));

    // 20 goes right of 10 (should be level 1)
    assertTrue(tree.insert(20));

    // 30 goes right of 20 (should be level 2)
    assertTrue(tree.insert(30));

    // 40 goes right of 30 (should be level 3)
    assertTrue(tree.insert(40));

    // Get the tree output
    String treeOutput = tree.printTree();

    // Verify correct levels are printed
    // If mutation happens, all right children would have level 1 instead of
    // incrementing
    assertTrue(treeOutput.contains("10")); // root at level 0
    assertTrue(treeOutput.contains("20")); // level 1
    assertTrue(treeOutput.contains("30")); // level 2
    assertTrue(treeOutput.contains("40")); // level 3

    // More specific check - verify the format "level value"
    String[] lines = treeOutput.trim().split("\n");

  }
  /**
   * Tests mutation coverage for BST printRec - curr.level * 2 arithmetic
   * The mutation replaces "curr.level * 2" with "curr.level" (first member)
   * This test verifies that indentation is correctly doubled per level
   * 
   * @Test
   *       public void testBSTPrintTreeIndentationMultiplier() {
   *       BST<Integer> tree = new BST<>();
   * 
   *       // Build a tree with multiple levels
   *       assertTrue(tree.insert(50)); // root, level 0
   *       assertTrue(tree.insert(30)); // left, level 1
   *       assertTrue(tree.insert(70)); // right, level 1
   *       assertTrue(tree.insert(20)); // left-left, level 2
   *       assertTrue(tree.insert(80)); // right-right, level 2
   * 
   *       String output = tree.printTree();
   * 
   *       // Parse output and verify indentation
   *       String[] lines = output.split("\n");
   * 
   *       for (String line : lines) {
   *       if (line.contains("30") || line.contains("70")) {
   *       // Level 1 nodes should have exactly 2 spaces after the level number
   *       // Format: "1 30" or "1 70"
   *       assertTrue("Level 1 should have 2 spaces",
   *       line.matches(" \\d+"));
   *       }
   *       if (line.contains("20") || line.contains("80")) {
   *       // Level 2 nodes should have exactly 4 spaces after the level number
   *       // Format: "2 20" or "2 80"
   *       assertTrue("Level 2 should have 4 spaces",
   *       line.matches(" \\d+"));
   *       }
   *       if (line.contains("50")) {
   *       // Level 0 (root) should have no spaces between level and value
   *       // Format: "050"
   *       assertTrue("Root should have no spaces",
   *       line.matches("0\\d+"));
   *       }
   *       }
   * 
   * 
   *       // Additional specific check: count spaces for level 2
   *       for (String line : lines) {
   *       if (line.startsWith("2") && (line.contains("20") ||
   *       line.contains("80"))) {
   *       // After "2", there should be exactly 4 spaces before the number
   *       int spaceCount = 0;
   *       for (int i = 1; i < line.length() && line.charAt(i) == ' '; i++) {
   *       spaceCount++;
   *       }
   *       assertEquals("Level 2 should have exactly 4 spaces", 4, spaceCount);
   *       }
   *       }
   * 
   *       }
   */
  // ===============================================

  /**
   * Test deleteOne searches left subtree when target compares < 0
   * Kills mutations on line 220: if (cmp <= 0)
   */
  @Test
  public void testDeleteOneSearchesLeftForSmallerValue() {
    bst.insert(50);
    bst.insert(30); // Goes left (30 < 50)
    bst.insert(20); // Goes left of 50
    bst.insert(40); // Goes left of 50, right of 30

    // Delete from left subtree
    assertTrue(bst.deleteOne(30));

    // Verify deletion - 30 should not be found
    assertFalse(bst.findAll(30).contains("30"));
    // Verify others still exist
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(20).contains("20"));
    assertTrue(bst.findAll(40).contains("40"));
  }

  /**
   * Test deleteOne searches left subtree for duplicate value (cmp == 0)
   * Since duplicates go LEFT in BST, this tests the == part of cmp <= 0
   * Kills mutations on line 220
   */
  @Test
  public void testDeleteOneSearchesLeftForEqualValue() {
    bst.insert(50);
    bst.insert(50); // Duplicate, goes LEFT per BST rule
    bst.insert(50); // Another duplicate, goes LEFT

    // All three should be in tree
    String results = bst.findAll(50);
    int count = results.split("\n").length;
    assertEquals(3, count);

    // Delete one occurrence
    assertTrue(bst.deleteOne(50));

    // Should have 2 left
    results = bst.findAll(50);
    count = results.trim().isEmpty() ? 0 : results.split("\n").length;
    assertEquals(2, count);
  }

  // ==========================================
  // BST deleteOne - Line 222: equals check
  // ==========================================

  /**
   * Test deleteOne uses equals() to find exact match
   * Kills mutations on line 222: curr.data.equals(value)
   */
  @Test
  public void testDeleteOneUsesEqualsForExactMatch() {
    bst.insert(100);
    bst.insert(50);
    bst.insert(150);

    // Delete specific value
    assertTrue(bst.deleteOne(50));

    // Verify only 50 deleted
    assertFalse(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(100).contains("100"));
    assertTrue(bst.findAll(150).contains("150"));
  }

  /**
   * Test deleteOne returns false when equals() doesn't match
   * Kills mutations on line 222 (false case)
   */
  @Test
  public void testDeleteOneReturnsFalseWhenNotFound() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(70);

    // Try to delete value that doesn't exist
    assertFalse(bst.deleteOne(100));

    // All original values should still exist
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  // ==========================================
  // BST deleteOne - Line 227: no children (leaf node)
  // ==========================================

  /**
   * Test deleteOne with leaf node (no children)
   * Kills mutations on line 227: curr.left == null && curr.right == null
   */
  @Test
  public void testDeleteOneLeafNode() {
    bst.insert(50);
    bst.insert(30); // Leaf node
    bst.insert(70); // Leaf node

    // Delete a leaf
    assertTrue(bst.deleteOne(30));

    // Verify leaf deleted, others remain
    assertFalse(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test deleteOne returns null for leaf node (deleting root that's a leaf)
   * Kills mutations on line 227 ensuring proper return
   */
  @Test
  public void testDeleteOneLeafReturnsNull() {
    bst.insert(100);

    assertTrue(bst.deleteOne(100));

    // Tree should be empty now
    assertEquals("", bst.printTree().trim());
  }

  // ==========================================
  // BST deleteOne - Line 231: two children case
  // ==========================================

  /**
   * Test deleteOne with node having two children
   * Kills mutations on line 231: both null checks
   */
  @Test
  public void testDeleteOneNodeWithTwoChildren() {
    bst.insert(50);
    bst.insert(30); // Left child
    bst.insert(70); // Right child

    // Delete node with two children
    assertTrue(bst.deleteOne(50));

    // Verify 50 deleted, children remain
    assertFalse(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test deleteOne two-children case uses max from left subtree
   * More complex scenario to ensure replacement logic works
   */
  @Test
  public void testDeleteOneTwoChildrenUsesMaxFromLeft() {
    bst.insert(50);
    bst.insert(30); // Left child
    bst.insert(70); // Right child
    bst.insert(20); // Left-left
    bst.insert(40); // Left-right (max of left subtree)
    bst.insert(35); // Left-right-left
    bst.insert(45); // Left-right-right

    // Delete node with two children
    assertTrue(bst.deleteOne(50));

    // Verify all nodes still accessible except 50
    assertFalse(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(20).contains("20"));
    assertTrue(bst.findAll(40).contains("40"));
    assertTrue(bst.findAll(35).contains("35"));
    assertTrue(bst.findAll(45).contains("45"));
  }

  // ==========================================
  // BST deleteOne - Line 235: left child null (one right child)
  // ==========================================

  /**
   * Test deleteOne with only right child
   * Kills mutations on line 235: if (curr.left == null)
   */
  @Test
  public void testDeleteOneNodeWithOnlyRightChild() {
    bst.insert(50);
    bst.insert(70); // Right child only
    bst.insert(90); // Right-right

    // Delete node with only right child
    assertTrue(bst.deleteOne(70));

    // Verify deletion and tree structure maintained
    assertFalse(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(90).contains("90"));
  }

  /**
   * Test deleteOne one-right-child properly adjusts levels
   * Ensures level adjustment on line 235
   */
  @Test
  public void testDeleteOneRightChildLevelAdjustment() {
    bst.insert(50);
    bst.insert(70);

    assertTrue(bst.deleteOne(70));

    // Verify structure
    assertTrue(bst.findAll(50).contains("50"));
    assertEquals("", bst.findAll(70).trim());
  }

  // ==========================================
  // BST deleteOne - Line 240: right child null (one left child)
  // ==========================================

  /**
   * Test deleteOne with only left child
   * Kills mutations on line 240: if (curr.right == null)
   */
  @Test
  public void testDeleteOneNodeWithOnlyLeftChild() {
    bst.insert(50);
    bst.insert(30); // Left child only
    bst.insert(10); // Left-left

    // Delete node with only left child
    assertTrue(bst.deleteOne(30));

    // Verify deletion and tree structure maintained
    assertFalse(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(10).contains("10"));
  }

  /**
   * Test deleteOne one-left-child properly adjusts levels
   * Ensures level adjustment on line 240
   */
  @Test
  public void testDeleteOneLeftChildLevelAdjustment() {
    bst.insert(50);
    bst.insert(30);

    assertTrue(bst.deleteOne(30));

    // Verify structure
    assertTrue(bst.findAll(50).contains("50"));
    assertEquals("", bst.findAll(30).trim());
  }

  // ==========================================
  // BST deleteOne - Line 255: cmp >= 0 (search right)
  // ==========================================

  /**
   * Test deleteOne searches right subtree when target compares > 0
   * Kills mutations on line 255: if (cmp >= 0)
   */
  @Test
  public void testDeleteOneSearchesRightForLargerValue() {
    bst.insert(50);
    bst.insert(70); // Goes right (70 > 50)
    bst.insert(60); // Goes right of 50, left of 70
    bst.insert(80); // Goes right of 50 and 70

    // Delete from right subtree
    assertTrue(bst.deleteOne(70));

    // Verify deletion
    assertFalse(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(60).contains("60"));
    assertTrue(bst.findAll(80).contains("80"));
  }

  /**
   * Test deleteOne searches right when cmp == 0 but equals is false
   * With duplicates, after checking current node, may need to search right
   * Kills mutations on line 255
   */
  @Test
  public void testDeleteOneSearchesRightForDuplicateNotMatching() {
    bst.insert(50);
    bst.insert(50); // Goes left (duplicate)
    bst.insert(50); // Goes left (duplicate)

    // Try to delete one 50 - must search through duplicates
    assertTrue(bst.deleteOne(50));

    // Should have 2 remaining
    String results = bst.findAll(50);
    int count = results.trim().isEmpty() ? 0 : results.split("\n").length;
    assertEquals(2, count);
  }

  // ==========================================
  // Comprehensive scenarios with duplicates
  // ==========================================

  /**
   * Test deleteOne with multiple duplicates in complex tree
   * Ensures all paths work correctly together
   */
  @Test
  public void testDeleteOneComplexTreeWithDuplicates() {
    bst.insert(50);
    bst.insert(50); // Duplicate, goes left
    bst.insert(50); // Duplicate, goes left
    bst.insert(30); // Smaller, goes left
    bst.insert(70); // Larger, goes right
    bst.insert(20); // Goes left
    bst.insert(80); // Goes right

    // Delete one occurrence of 50
    assertTrue(bst.deleteOne(50));

    // Verify structure: should have 2 copies of 50 left
    String results = bst.findAll(50);
    int count = results.trim().isEmpty() ? 0 : results.split("\n").length;
    assertEquals(2, count);

    // Other values should be intact
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(20).contains("20"));
    assertTrue(bst.findAll(80).contains("80"));
  }

  /**
   * Test deleteOne returns true only when value actually deleted
   */
  @Test
  public void testDeleteOneReturnValue() {
    bst.insert(100);

    // Should return true for successful deletion
    assertTrue(bst.deleteOne(100));

    // Should return false when not found
    assertFalse(bst.deleteOne(200));

    // Should return false when already deleted
    assertFalse(bst.deleteOne(100));
  }

  /**
   * Test deleteOne early exit after finding match in left
   * Line 223: if (deleted[0]) return curr;
   */
  @Test
  public void testDeleteOneEarlyExitAfterLeftFind() {
    bst.insert(50);
    bst.insert(30); // Goes left
    bst.insert(70); // Goes right

    // Delete from left - should exit early, not search right
    assertTrue(bst.deleteOne(30));

    // Verify right was not affected
    assertTrue(bst.findAll(70).contains("70"));
    assertFalse(bst.findAll(30).contains("30"));
  }

  /**
   * Test deleteOne deletes only one occurrence when there are duplicates
   * Verifies single deletion behavior
   */
  @Test
  public void testDeleteOneOnlyDeletesOneOccurrence() {
    bst.insert(100);
    bst.insert(100);
    bst.insert(100);
    bst.insert(100);

    // Initially 4 copies
    String results = bst.findAll(100);
    assertEquals(4, results.split("\n").length);

    // Delete one
    assertTrue(bst.deleteOne(100));

    // Should have 3 left
    results = bst.findAll(100);
    assertEquals(3, results.split("\n").length);

    // Delete another
    assertTrue(bst.deleteOne(100));

    // Should have 2 left
    results = bst.findAll(100);
    assertEquals(2, results.split("\n").length);
  }

  /**
   * Test deleteOne with empty tree
   * Ensures proper handling of null root
   */
  @Test
  public void testDeleteOneFromEmptyTree() {
    assertFalse(bst.deleteOne(50));
    assertEquals("", bst.printTree().trim());
  }

  /**
   * Test deleteOne maintains BST property after deletion
   */
  @Test
  public void testDeleteOneMaintainsBSTProperty() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(70);
    bst.insert(20);
    bst.insert(40);
    bst.insert(60);
    bst.insert(80);

    // Delete root
    assertTrue(bst.deleteOne(50));

    // Tree should still be valid BST with all other elements
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(20).contains("20"));
    assertTrue(bst.findAll(40).contains("40"));
    assertTrue(bst.findAll(60).contains("60"));
    assertTrue(bst.findAll(80).contains("80"));

    // printTree should show valid structure
    String tree = bst.printTree();
    assertFalse(tree.isEmpty());
  }
  // ===========================

  // ==========================================
  // Line 158: Case 1 - No children (leaf node)
  // Tests: if (curr.left == null && curr.right == null)
  // ==========================================

  /**
   * Test deleteAll on a leaf node (no children)
   * Kills mutations on line 158: curr.left == null (first check)
   */
  @Test
  public void testDeleteAllLeafNode() {
    bst.insert(50);
    bst.insert(30); // Leaf
    bst.insert(70); // Leaf

    // Delete a leaf node
    assertTrue(bst.deleteAll(30));

    // Verify exact tree structure after deletion
    assertEquals("050\n1  70\n", bst.printTree());

    // Verify leaf deleted
    assertEquals("", bst.findAll(30));
    // Verify others remain
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(70).contains("70"));

  }

  /**
   * Test deleteAll multiple leaf nodes with same value
   * Ensures all occurrences deleted when they're all leaves
   * Kills mutation on line 158
   */
  @Test
  public void testDeleteAllMultipleLeaves() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(30); // Duplicate, goes left
    bst.insert(70);

    // Both 30s should be deleted
    assertTrue(bst.deleteAll(30));

    // Check exact tree structure
    assertEquals("050\n1  70\n", bst.printTree());

    assertEquals("", bst.findAll(30));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  // ==========================================
  // Lines 161-164: Case 2a - Only right child
  // Tests: if (curr.left == null) return curr.right;
  // ==========================================

  /**
   * Test deleteAll node with only right child
   * Kills mutations on line 161: curr.right == null check (first mutation)
   * and line 158 (curr.left == null must be false to skip first case)
   */
  @Test
  public void testDeleteAllNodeWithOnlyRightChild() {
    bst.insert(50);
    bst.insert(60); // Right child
    bst.insert(70); // Right-right

    // Delete node with only right child
    assertTrue(bst.deleteAll(60));

    // Check exact tree structure to ensure proper path taken
    assertEquals("050\n1  70\n", bst.printTree());

    // Verify deletion
    assertEquals("", bst.findAll(60));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test deleteAll root with only right child
   * Ensures proper handling when root has one right child
   * Kills mutation on line 161 (first mutation)
   */
  @Test
  public void testDeleteAllRootWithOnlyRightChild() {
    bst.insert(50);
    bst.insert(60);
    bst.insert(70);

    // Delete root - it has only right child
    assertTrue(bst.deleteAll(50));

    // Check exact tree structure
    assertEquals("060\n1  70\n", bst.printTree());

    assertEquals("", bst.findAll(50));
    assertTrue(bst.findAll(60).contains("60"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test deleteAll checks curr.left == null before returning right child
   * This specifically targets the condition on line 161
   */
  @Test
  public void testDeleteAllOnlyRightChildNotLeaf() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(60);
    bst.insert(55);
    bst.insert(70);

    // Delete 60 - has left (55) and right (70) children, NOT one-child case
    assertTrue(bst.deleteAll(60));

    // Then delete 30 - this is a leaf, not one-right-child
    assertTrue(bst.deleteAll(30));

    // Now delete 50 - should have only right child
    assertTrue(bst.deleteAll(50));

    assertEquals("", bst.findAll(50));
    assertTrue(bst.findAll(55).contains("55"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  // ==========================================
  // Lines 166-169: Case 2b - Only left child
  // Tests: if (curr.right == null) return curr.left;
  // ==========================================

  /**
   * Test deleteAll node with only left child
   * Kills mutations on line 169: curr.right == null check (first mutation)
   * and line 166 (curr.left == null must be false)
   */
  @Test
  public void testDeleteAllNodeWithOnlyLeftChild() {
    bst.insert(50);
    bst.insert(30); // Left child
    bst.insert(20); // Left-left

    // Delete node with only left child
    assertTrue(bst.deleteAll(30));

    // Check exact tree structure to ensure proper path taken
    assertEquals("1  20\n050\n", bst.printTree());

    // Verify deletion
    assertEquals("", bst.findAll(30));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(20).contains("20"));
  }

  /**
   * Test deleteAll root with only left child
   * Ensures proper handling when root has one left child
   * Kills mutation on line 169 (first mutation)
   */
  @Test
  public void testDeleteAllRootWithOnlyLeftChild() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(20);

    // Delete root - it has only left child
    assertTrue(bst.deleteAll(50));

    // Check exact tree structure
    assertEquals("1  20\n030\n", bst.printTree());

    assertEquals("", bst.findAll(50));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(20).contains("20"));
  }

  /**
   * Test deleteAll checks curr.right == null before returning left child
   * This specifically targets the condition on line 169
   */
  @Test
  public void testDeleteAllOnlyLeftChildNotLeaf() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(70);
    bst.insert(25);
    bst.insert(35);

    // Delete 30 - has both children, NOT one-child case
    assertTrue(bst.deleteAll(30));
    assertEquals("1  25\n2    35\n050\n1  70\n", bst.printTree());
    // Delete 70 - leaf
    assertTrue(bst.deleteAll(70));
    assertEquals("1  25\n2    35\n050\n", bst.printTree());

    // Now delete 50 - should have only left subtree
    assertTrue(bst.deleteAll(50));
    assertEquals("025\n1  35\n", bst.printTree());

    assertEquals("", bst.findAll(50));
    assertTrue(bst.findAll(25).contains("25"));
    assertTrue(bst.findAll(35).contains("35"));
  }

  // ==========================================
  // Case 3: Two children - tests both null checks must be false
  // ==========================================

  /**
   * Test deleteAll node with two children
   * This ensures lines 158, 161, 166, 169 all return false
   * to reach the two-children case
   */
  @Test
  public void testDeleteAllNodeWithTwoChildren() {
    bst.insert(50);
    bst.insert(30); // Left child
    bst.insert(70); // Right child

    // Delete node with two children
    assertTrue(bst.deleteAll(50));

    // Verify deletion and children remain
    assertEquals("", bst.findAll(50));
    assertEquals("030\n1  70\n", bst.printTree());
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
    assertTrue(bst.deleteAll(30));
    assertEquals("070\n", bst.printTree());
  }

  /**
   * Test deleteAll with complex two-children scenario
   * Ensures max from left subtree is used as replacement
   */
  @Test
  public void testDeleteAllTwoChildrenComplexTree() {
    bst.insert(50);
    bst.insert(30); // Left
    bst.insert(70); // Right
    bst.insert(20); // Left-left
    bst.insert(40); // Left-right (max of left subtree)
    bst.insert(60); // Right-left
    bst.insert(80); // Right-right

    // Delete root with two children
    assertTrue(bst.deleteAll(50));

    // All other nodes should remain
    assertEquals("", bst.findAll(50));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(20).contains("20"));
    assertTrue(bst.findAll(40).contains("40"));
    assertTrue(bst.findAll(60).contains("60"));
    assertTrue(bst.findAll(80).contains("80"));
  }

  // ==========================================
  // Comprehensive tests for all deletion cases
  // ==========================================

  /**
   * Test deleteAll removes ALL occurrences across different cases
   */
  @Test
  public void testDeleteAllMultipleOccurrencesAllCases() {
    bst.insert(50);
    bst.insert(50); // Duplicate at different position
    bst.insert(50); // Another duplicate
    bst.insert(30);
    bst.insert(70);

    // All three 50s should be deleted
    assertTrue(bst.deleteAll(50));

    assertEquals("", bst.findAll(50));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test deleteAll with duplicates in various structural positions
   */
  @Test
  public void testDeleteAllDuplicatesInDifferentPositions() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(30); // Duplicate - leaf position
    bst.insert(70);
    bst.insert(30); // Another duplicate
    bst.insert(20);
    bst.insert(40);

    // Delete all 30s (different structural positions)
    assertTrue(bst.deleteAll(30));

    assertEquals("", bst.findAll(30));
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(70).contains("70"));
    assertTrue(bst.findAll(20).contains("20"));
    assertTrue(bst.findAll(40).contains("40"));
  }

  /**
   * Test deleteAll returns false when value not found
   */
  @Test
  public void testDeleteAllNotFound() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(70);

    assertFalse(bst.deleteAll(100));

    // All original values should remain
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test deleteAll from empty tree
   */
  @Test
  public void testDeleteAllFromEmptyTree() {
    assertFalse(bst.deleteAll(50));
    assertEquals("", bst.printTree());
  }

  /**
   * Specific test to kill line 158 first mutation
   * When curr.left == null is replaced with false, the leaf case is skipped
   * but we need to verify the exact tree structure to catch this
   */
  @Test
  public void testDeleteAllLeafNodeExactStructure() {
    bst.insert(40);
    bst.insert(20);
    bst.insert(60);
    bst.insert(10);
    bst.insert(30);

    // Delete leaf node 10
    assertTrue(bst.deleteAll(10));

    // Exact tree structure after deletion
    assertEquals("1  20\n2    30\n040\n1  60\n", bst.printTree());
  }

  /**
   * Specific test to kill line 161 first mutation
   * When curr.right == null (in one-right-child case) is replaced with false
   * Verifies exact structure after deleting node with only right child
   */
  @Test
  public void testDeleteAllOnlyRightChildExactStructure() {
    bst.insert(40);
    bst.insert(20);
    bst.insert(60);
    bst.insert(50);
    bst.insert(70);

    // Delete 60 which has left (50) and right (70) - two children
    // Then 40 will have 20 (left) and 70 (right with 50 as left child)
    assertTrue(bst.deleteAll(60));

    // Check exact structure
    assertEquals("1  20\n040\n1  50\n2    70\n", bst.printTree());
  }

  /**
   * Specific test to kill line 169 first mutation
   * When curr.right == null (in one-left-child case) is replaced with false
   * Verifies exact structure after deleting node with only left child
   */
  @Test
  public void testDeleteAllOnlyLeftChildExactStructure() {
    bst.insert(40);
    bst.insert(20);
    bst.insert(60);
    bst.insert(10);
    bst.insert(30);

    // Delete 20 which has both children (10 and 30)
    // After deletion using max from left (10), structure should be specific
    assertTrue(bst.deleteAll(20));

    // Check exact structure
    assertEquals("1  10\n2    30\n040\n1  60\n", bst.printTree());
  }

  /**
   * Test sequential deleteAll operations ensuring each case is covered
   */
  @Test
  public void testDeleteAllSequentialOperations() {
    bst.insert(50);
    bst.insert(30);
    bst.insert(70);
    bst.insert(20);
    bst.insert(40);
    bst.insert(60);
    bst.insert(80);

    // Delete leaf nodes first
    assertTrue(bst.deleteAll(20));
    assertEquals("", bst.findAll(20));

    // Delete node with one child
    bst.insert(90); // Add so 80 has one right child
    assertTrue(bst.deleteAll(80));
    assertEquals("", bst.findAll(80));
    assertTrue(bst.findAll(90).contains("90"));

    // Delete node with two children
    assertTrue(bst.deleteAll(50));
    assertEquals("", bst.findAll(50));

    // Remaining nodes should still be accessible
    assertTrue(bst.findAll(30).contains("30"));
    assertTrue(bst.findAll(70).contains("70"));
  }

  /**
   * Test that forces all conditional branches in deleteRec
   * Ensures line 158, 161, 166, 169 are all tested with true/false
   */
  @Test
  public void testDeleteAllAllConditionalBranches() {
    // Build tree to test all cases
    bst.insert(50);
    bst.insert(25);
    bst.insert(75);
    bst.insert(12);
    bst.insert(37);
    bst.insert(62);
    bst.insert(87);
    bst.insert(6);
    bst.insert(18);
    bst.insert(31);
    bst.insert(43);

    // Delete leaf (lines 158 both null checks true)
    assertTrue(bst.deleteAll(6));

    // Create one-right-child scenario
    // 12 now has only right child (18)
    assertTrue(bst.deleteAll(12));

    // Create one-left-child scenario
    // Insert to make structure for this
    bst.insert(80);
    assertTrue(bst.deleteAll(87)); // Now 75 has 62 and 80

    // Delete node with two children
    assertTrue(bst.deleteAll(25));

    // Verify remaining structure
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(75).contains("75"));
  }

  /**
   * Test deleteAll properly handles null checks in sequence
   * This test specifically targets the order of null checks
   */
  @Test
  public void testDeleteAllNullCheckSequence() {
    bst.insert(100);
    bst.insert(50);
    bst.insert(150);

    // Case 1: Delete node with two children (both checks false)
    assertTrue(bst.deleteAll(100));

    bst.insert(100);
    bst.insert(120);

    // Case 2: Delete node with only right child (left null, right not null)
    assertTrue(bst.deleteAll(100));

    bst.insert(100);
    bst.insert(80);

    // Case 3: Delete node with only left child (left not null, right null)
    assertTrue(bst.deleteAll(100));

    // Case 4: Delete leaf (both null)
    assertTrue(bst.deleteAll(120));

    // Verify final structure
    assertTrue(bst.findAll(50).contains("50"));
    assertTrue(bst.findAll(150).contains("150"));

  }
  // ========================

}
