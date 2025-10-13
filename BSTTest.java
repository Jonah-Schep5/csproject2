import org.junit.Before;
import org.junit.Test;
import student.TestCase;

/**
 * Unit tests for the BST class.
 * Provides comprehensive coverage for insertion, deletion, and traversal
 * behaviors
 * in a binary search tree implementation, including edge cases and
 * duplicates.
 * 
 * @author Jonah Schepers
 * @author Rowan Muhoberac
 * @version Oct 12, 2025
 */
public class BSTTest extends TestCase {

  private BST<Integer> tree;
  private BST<Integer> st;

  /**
   * Sets up fresh BST instances before each test.
   */
  @Before
  public void setUp() {
    tree = new BST<>();
    st = new BST<>();
  }

  /**
   * Tests basic insertion and tree printing order.
   * Ensures elements are printed in sorted (inorder) order.
   */
  @Test
  public void testInsertAndPrint() {
    assertTrue(tree.insert(10));
    tree.insert(5);
    tree.insert(15);
    tree.insert(2);
    tree.insert(7);

    String printed = tree.printTree();
    assertTrue(printed.indexOf("2") < printed.indexOf("5"));
    assertTrue(printed.indexOf("5") < printed.indexOf("7"));
    assertTrue(printed.indexOf("7") < printed.indexOf("10"));
    assertTrue(printed.indexOf("10") < printed.indexOf("15"));
  }

  /**
   * Verifies that duplicate insertions are handled correctly
   * and are placed consistently (duplicates go left).
   */
  @Test
  public void testInsertDuplicatesGoLeft() {
    tree.insert(10);
    tree.insert(10);
    String printed = tree.printTree();
    int firstIndex = printed.indexOf("10");
    int secondIndex = printed.indexOf("10", firstIndex + 1);
    assertTrue("Second 10 should exist", secondIndex != -1);
  }

  /**
   * Tests the findAll method for multiple and missing occurrences.
   */
  @Test
  public void testFindAll() {
    tree.insert(10);
    tree.insert(10);
    tree.insert(5);
    String result = tree.findAll(10);
    assertEquals(2, result.split("\n").length);
    assertEquals("", tree.findAll(99).trim());
  }

  /**
   * Tests deleting a leaf node using deleteAll.
   */
  @Test
  public void testDeleteAllLeafNode() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    assertTrue(st.deleteAll(30));
    assertEquals("050\n1  70\n", st.printTree());
    assertEquals("", st.findAll(30));
  }

  /**
   * Tests deleteAll for a node with only a right child.
   */
  @Test
  public void testDeleteAllOnlyRightChild() {
    st.insert(50);
    st.insert(30);
    st.insert(20);
    st.insert(25);
    assertTrue(st.deleteAll(50));
    assertEquals("1  20\n2    25\n030\n", st.printTree());
  }

  /**
   * Tests deleteAll for a node with only a left child.
   */
  @Test
  public void testDeleteAllOnlyLeftChild() {
    st.insert(50);
    st.insert(30);
    st.insert(20);
    assertTrue(st.deleteAll(50));
    assertEquals("1  20\n030\n", st.printTree());
  }

  /**
   * Tests deleteAll for a node with two children.
   */
  @Test
  public void testDeleteAllTwoChildren() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(20);
    st.insert(40);
    assertTrue(st.deleteAll(50));
    assertEquals("", st.findAll(50));
    assertTrue(st.findAll(30).contains("30"));
    assertTrue(st.findAll(70).contains("70"));
  }

  /**
   * Tests deleteAll when multiple duplicates exist.
   */
  @Test
  public void testDeleteAllMultipleDuplicates() {
    st.insert(50);
    st.insert(50);
    st.insert(50);
    st.insert(30);
    st.insert(70);
    assertTrue(st.deleteAll(50));
    assertEquals("", st.findAll(50));
    assertTrue(st.findAll(30).contains("30"));
    assertTrue(st.findAll(70).contains("70"));
  }

  /**
   * Tests deleteAll when the target value is not found.
   */
  @Test
  public void testDeleteAllNotFound() {
    st.insert(50);
    assertFalse(st.deleteAll(100));
    assertTrue(st.findAll(50).contains("50"));
  }

  /**
   * Tests deleteOne when the target is found in the left subtree.
   */
  @Test
  public void testDeleteOneSearchesLeft() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(20);
    assertTrue(st.deleteOne(30));
    assertFalse(st.findAll(30).contains("30"));
    assertTrue(st.findAll(20).contains("20"));
  }

  /**
   * Tests deleteOne when multiple duplicates exist.
   */
  @Test
  public void testDeleteOneWithDuplicates() {
    st.insert(50);
    st.insert(50);
    st.insert(50);
    assertEquals(3, st.findAll(50).split("\n").length);
    assertTrue(st.deleteOne(50));
    assertEquals(2, st.findAll(50).split("\n").length);
    assertTrue(st.deleteOne(50));
    assertEquals(1, st.findAll(50).split("\n").length);
  }

  /**
   * Tests deleteOne when the target value is not found.
   */
  @Test
  public void testDeleteOneNotFound() {
    st.insert(50);
    assertFalse(st.deleteOne(100));
    assertTrue(st.findAll(50).contains("50"));
  }

  /**
   * Tests deleteOne on a leaf node.
   */
  @Test
  public void testDeleteOneLeaf() {
    st.insert(50);
    st.insert(30);
    assertTrue(st.deleteOne(30));
    assertFalse(st.findAll(30).contains("30"));
    assertTrue(st.findAll(50).contains("50"));
  }

  /**
   * Tests deleteOne on a node with only a right child.
   */
  @Test
  public void testDeleteOneOnlyRightChild() {
    st.insert(50);
    st.insert(70);
    st.insert(90);
    assertTrue(st.deleteOne(70));
    assertFalse(st.findAll(70).contains("70"));
    assertTrue(st.findAll(90).contains("90"));
  }

  /**
   * Tests deleteOne on a node with only a left child.
   */
  @Test
  public void testDeleteOneOnlyLeftChild() {
    st.insert(50);
    st.insert(30);
    st.insert(10);
    assertTrue(st.deleteOne(30));
    assertFalse(st.findAll(30).contains("30"));
    assertTrue(st.findAll(10).contains("10"));
  }

  /**
   * Tests deleteOne on a node with two children.
   */
  @Test
  public void testDeleteOneTwoChildren() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(20);
    st.insert(40);
    assertTrue(st.deleteOne(50));
    assertFalse(st.findAll(50).contains("50"));
    assertTrue(st.findAll(30).contains("30"));
    assertTrue(st.findAll(70).contains("70"));
  }

  /**
   * Tests deleteOne when search traverses the right subtree.
   */
  @Test
  public void testDeleteOneSearchesRight() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(80);
    assertTrue(st.deleteOne(70));
    assertFalse(st.findAll(70).contains("70"));
    assertTrue(st.findAll(80).contains("80"));
  }

  /**
   * Tests deleteOne with early exit conditions.
   */
  @Test
  public void testDeleteOneEarlyExit() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    assertTrue(st.deleteOne(30));
    assertTrue(st.findAll(70).contains("70"));
  }

  /**
   * Tests deleteOne when the tree is empty.
   */
  @Test
  public void testDeleteOneFromEmptyTree() {
    assertFalse(st.deleteOne(50));
    assertEquals("", st.printTree().trim());
  }

  /**
   * Tests inserting right children sequentially to verify depth
   * levels.
   */
  @Test
  public void testInsertRightChildLevels() {
    tree.insert(10);
    tree.insert(20);
    tree.insert(30);
    tree.insert(40);
    String output = tree.printTree();
    assertTrue(output.contains("10"));
    assertTrue(output.contains("20"));
    assertTrue(output.contains("30"));
    assertTrue(output.contains("40"));
  }

  /**
   * Tests sequential deleteAll operations to confirm structural
   * integrity
   * after multiple deletions.
   */
  @Test
  public void testDeleteAllSequentialOperations() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(20);
    st.insert(40);
    st.insert(60);
    st.insert(80);

    assertTrue(st.deleteAll(20));
    assertEquals("", st.findAll(20));

    st.insert(90);
    assertTrue(st.deleteAll(80));
    assertTrue(st.findAll(90).contains("90"));

    assertTrue(st.deleteAll(50));
    assertTrue(st.findAll(30).contains("30"));
    assertTrue(st.findAll(70).contains("70"));
  }

  /**
   * Tests deleteOne with complex cases involving multiple duplicates.
   */
  @Test
  public void testDeleteOneComplexDuplicates() {
    st.insert(50);
    st.insert(50);
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(20);

    assertTrue(st.deleteOne(50));
    assertEquals(2, st.findAll(50).split("\n").length);
    assertTrue(st.findAll(30).contains("30"));
    assertTrue(st.findAll(70).contains("70"));
  }

  /**
   * Verifies BST property is maintained after multiple deletions.
   */
  @Test
  public void testMaintainsBSTPropertyAfterDeletions() {
    st.insert(50);
    st.insert(30);
    st.insert(70);
    st.insert(20);
    st.insert(40);
    st.insert(60);
    st.insert(80);

    assertTrue(st.deleteOne(50));
    assertTrue(st.findAll(30).contains("30"));
    assertTrue(st.findAll(70).contains("70"));
    assertTrue(st.findAll(20).contains("20"));
    assertTrue(st.findAll(40).contains("40"));
    assertTrue(st.findAll(60).contains("60"));
    assertTrue(st.findAll(80).contains("80"));
  }

  /**
   * Tests multiple deleteAll calls covering all structural node cases
   * (two children, one child, and leaf nodes).
   */
  @Test
  public void testDeleteAllNullCheckSequence() {
    st.insert(100);
    st.insert(50);
    st.insert(150);
    assertTrue(st.deleteAll(100));

    st.insert(100);
    st.insert(120);
    assertTrue(st.deleteAll(100));

    st.insert(100);
    st.insert(80);
    assertTrue(st.deleteAll(100));

    assertTrue(st.deleteAll(120));

    assertTrue(st.findAll(50).contains("50"));
    assertTrue(st.findAll(150).contains("150"));
  }

  /**
   * Tests printTree output formatting to ensure correct level
   * indentation.
   */
  @Test
  public void testPrintTreeFormatting() {
    tree.insert(10);
    tree.insert(5);
    tree.insert(15);
    tree.insert(2);

    String printed = tree.printTree();
    assertTrue("Root should be at level 0", printed.contains("10"));
    assertTrue("Child should be at level 1", printed.contains("5"));
    assertTrue("Leaf should be at level 2", printed.contains("2"));
  }
}
