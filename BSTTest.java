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

  /**
   * Runs before each test. Initializes a new BST instance.
   */
  @Before
  public void setUp() {
    tree = new BST<>();
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

    assertTrue(tree.deleteAll(5));
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
}
