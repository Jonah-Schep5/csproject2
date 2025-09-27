import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import student.TestCase;

public class BSTTest extends TestCase {

  private BST<Integer> testBST;

  public void setUp() {

    testBST = new BST<>();
    testBST.insert(8);
    testBST.insert(3);
    testBST.insert(10);
    testBST.insert(1);
    testBST.insert(6);
    testBST.insert(14);
    testBST.insert(4);
    testBST.insert(7);
    testBST.insert(13);

  }

  public void testInsertAndPrintTree() {
    // Capture print output
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    testBST.printTree();

    String output = out.toString().trim();
    String[] values = output.split("\\s+");

    // BST should be sorted in-order
    String[] expected = { "1", "3", "4", "6", "7", "8", "10", "13", "14" };
    assertArrayEquals(expected, values);
  }

  @Test
  public void testInsertDuplicateGoesLeft() {
    testBST.insert(3); // duplicate
    // Should still be sorted
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    testBST.printTree();

    String output = out.toString();
    assertTrue("Duplicate value should still be present", output.contains("3"));
  }

  @Test
  public void testDeleteLeafNode() {
    // Delete leaf 13
    assertTrue(testBST.delete(13));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    testBST.printTree();

    String result = out.toString();
    assertFalse("Leaf 13 should be deleted", result.contains("13"));
  }

  @Test
  public void testDeleteNodeWithOneChild() {
    // 10 has one child (14)
    assertTrue(testBST.delete(10));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    testBST.printTree();

    String result = out.toString();
    assertFalse("Node 10 should be deleted", result.contains("10"));
    assertTrue("Child 14 should still exist", result.contains("14"));
  }

  @Test
  public void testDeleteNodeWithTwoChildren() {
    // Delete root (8), should replace with max from left subtree (7)
    assertTrue(testBST.delete(8));

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    testBST.printTree();

    String result = out.toString();
    assertFalse("Original root 8 should be deleted", result.contains("8"));
    assertTrue("Tree should still contain other elements", result.contains("7"));
  }

  @Test
  public void testDeleteNonExistentNode() {
    assertFalse("Deleting non-existent node should return false", testBST.delete(99));
  }

}
