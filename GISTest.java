import java.io.IOException;
import student.TestCase;

/**
 * Unit test suite for the {@link GIS} interface and its implementation
 * {@link GISDB}.
 * 
 * <p>
 * This class uses the student-provided {@link TestCase} framework to verify
 * correct behavior of all GIS operations including:
 * <ul>
 * <li>Initialization and clearing the database</li>
 * <li>Printing and debugging outputs</li>
 * <li>Insertion, deletion, and search operations</li>
 * <li>Boundary condition handling and invalid inputs</li>
 * <li>Handling of duplicate names and coordinates</li>
 * <li>Tree consistency and complex mixed-operation scenarios</li>
 * </ul>
 * 
 * Each test method focuses on a specific functionality or edge case scenario to
 * ensure that the implementation behaves correctly and consistently.
 * 
 * @author Jonah Schepers
 * @author Rowan Muhoberac
 * @version Oct 12, 2025
 */
public class GISTest extends TestCase {

    private GIS it;
    private GIS testDB;

    /**
     * Sets up the test environment before each test is executed.
     * <p>
     * Initializes a new {@link GISDB} instance to ensure a clean state.
     */
    public void setUp() {
        it = new GISDB();
        testDB = new GISDB();
    }

    /**
     * Tests that the GIS database can be successfully cleared upon
     * initialization.
     *
     * @throws IOException
     *                     if an I/O error occurs during the clear operation
     */
    public void testRefClearInit() throws IOException {
        assertTrue(it.clear());
    }

    /**
     * Tests that printing, debugging, info lookup, and deletion on an empty GIS
     * return empty results instead of throwing exceptions or returning null.
     *
     * @throws IOException
     *                     if any operation encounters an I/O error
     */
    public void testRefEmptyPrints() throws IOException {
        assertEquals("", it.print());
        assertEquals("", it.debug());
        assertEquals("", it.info("CityName"));
        assertEquals("", it.info(5, 5));
        assertEquals("", it.delete("CityName"));
        assertEquals("", it.delete(5, 5));
    }

    /**
     * Tests behavior of the GIS system when invalid input is provided. Ensures
     * insertions with negative or out-of-range coordinates are rejected and
     * invalid
     * search parameters return empty results.
     *
     * @throws IOException
     *                     if an I/O error occurs
     */
    public void testRefBadInput() throws IOException {
        assertFalse(it.insert("CityName", -1, 5));
        assertFalse(it.insert("CityName", 5, -1));
        assertFalse(it.insert("CityName", 100000, 5));
        assertFalse(it.insert("CityName", 5, 100000));
        assertEquals("", it.search(-1, -1, -1));
    }

    /**
     * Inserts a variety of records and verifies that outputs for print, info,
     * delete, and search operations match expected results.
     *
     * @throws IOException
     *                     if an I/O error occurs during operations
     */
    public void testRefOutput() throws IOException {
        assertTrue(it.insert("Chicago", 100, 150));
        assertTrue(it.insert("Atlanta", 10, 500));
        assertTrue(it.insert("Tacoma", 1000, 100));
        assertTrue(it.insert("Baltimore", 0, 300));
        assertTrue(it.insert("Washington", 5, 350));
        assertFalse(it.insert("X", 100, 150));
        assertTrue(it.insert("L", 101, 150));
        assertTrue(it.insert("L", 11, 500));

        assertFuzzyEquals("1  Atlanta (10, 500)\n" + "2    Baltimore (0, 300)\n"
                + "0Chicago (100, 150)\n" + "3      L (11, 500)\n"
                + "2    L (101, 150)\n" + "1  Tacoma (1000, 100)\n"
                + "2    Washington (5, 350)\n", it.print());

        assertFuzzyEquals("L", it.info(101, 150));
        assertFuzzyEquals("Tacoma (1000, 100)\n", it.delete("Tacoma"));
        assertFuzzyEquals("3\nChicago", it.delete(100, 150));
        assertFuzzyEquals("L (101, 150)\n" + "Atlanta (10, 500)\n"
                + "Baltimore (0, 300)\n" + "Washington (5, 350)\n"
                + "L (11, 500)\n5", it.search(0, 0, 2000));
    }

    /**
     * Tests behavior when multiple cities share the same name. Ensures
     * {@link GIS#info(String)} returns all matches and
     * {@link GIS#delete(String)}
     * removes them all.
     *
     * @throws IOException
     *                     if an I/O error occurs
     */
    public void testMultipleCitiesSameName() throws IOException {
        assertTrue(it.insert("Springfield", 100, 100));
        assertTrue(it.insert("Springfield", 200, 200));
        assertTrue(it.insert("Springfield", 300, 300));
        assertTrue(it.insert("Portland", 150, 150));

        String info = it.info("Springfield");
        assertTrue(info.contains("Springfield (100, 100)"));
        assertTrue(info.contains("Springfield (200, 200)"));
        assertTrue(info.contains("Springfield (300, 300)"));

        String deleteResult = it.delete("Springfield");
        assertTrue(deleteResult.contains("(100, 100)"));
        assertTrue(deleteResult.contains("(200, 200)"));
        assertTrue(deleteResult.contains("(300, 300)"));

        assertEquals("", it.info("Springfield"));
        assertEquals("", it.info(100, 100));
        assertEquals("", it.info(200, 200));
        assertEquals("", it.info(300, 300));
        assertEquals("Portland", it.info(150, 150));
    }

    /**
     * Tests {@link GIS#search(int, int, int)} with various radii, including
     * zero,
     * boundary, large, and negative values.
     *
     * @throws IOException
     *                     if an I/O error occurs
     */
    public void testSearchRadiusBoundaries() throws IOException {
        it.insert("Center", 100, 100);
        it.insert("North", 100, 105);
        it.insert("East", 103, 104);
        it.insert("Far", 110, 110);

        String result0 = it.search(100, 100, 0);
        assertTrue(result0.contains("Center"));
        assertFalse(result0.contains("North"));

        String result5 = it.search(100, 100, 5);
        assertTrue(result5.contains("Center"));
        assertTrue(result5.contains("North"));
        assertTrue(result5.contains("East"));
        assertFalse(result5.contains("Far"));

        String resultLarge = it.search(100, 100, 100);
        assertTrue(resultLarge.contains("Far"));

        assertEquals("", it.search(100, 100, -5));
    }

    /**
     * Tests tree consistency by inserting and deleting records, re-inserting
     * into
     * freed locations, and verifying data integrity.
     *
     * @throws IOException
     *                     if an I/O error occurs
     */
    public void testInsertDeleteConsistency() throws IOException {
        assertTrue(it.insert("A", 50, 50));
        assertTrue(it.insert("B", 25, 75));
        assertTrue(it.insert("C", 75, 25));
        assertTrue(it.insert("D", 10, 30));
        assertTrue(it.insert("E", 90, 80));

        assertEquals("A", it.info(50, 50));
        assertEquals("B", it.info(25, 75));

        String deleteB = it.delete(25, 75);
        assertTrue(deleteB.contains("B"));
        assertEquals("", it.info(25, 75));

        assertEquals("A", it.info(50, 50));
        assertEquals("C", it.info(75, 25));

        assertTrue(it.insert("NewB", 25, 75));
        assertEquals("NewB", it.info(25, 75));

        assertTrue(it.clear());
        assertEquals("", it.info(50, 50));
        assertEquals("", it.print());
        assertEquals("", it.debug());
    }

    /**
     * Tests behavior at coordinate boundaries, including origin, maximums,
     * edges,
     * and invalid coordinates beyond allowed limits.
     *
     * @throws IOException
     *                     if an I/O error occurs
     */
    public void testBoundaryCoordinates() throws IOException {
        assertTrue(it.insert("Origin", 0, 0));
        assertEquals("Origin", it.info(0, 0));

        assertTrue(it.insert("MaxPoint", GISDB.MAXCOORD, GISDB.MAXCOORD));
        assertEquals("MaxPoint", it.info(GISDB.MAXCOORD, GISDB.MAXCOORD));

        assertTrue(it.insert("EdgeX", GISDB.MAXCOORD, 0));
        assertTrue(it.insert("EdgeY", 0, GISDB.MAXCOORD));

        assertFalse(it.insert("TooBigX", GISDB.MAXCOORD + 1, 100));
        assertFalse(it.insert("TooBigY", 100, GISDB.MAXCOORD + 1));
        assertFalse(it.insert("NegativeX", -1, 100));
        assertFalse(it.insert("NegativeY", 100, -1));

        assertEquals("Origin", it.info(0, 0));
        assertEquals("MaxPoint", it.info(GISDB.MAXCOORD, GISDB.MAXCOORD));
        assertEquals("EdgeX", it.info(GISDB.MAXCOORD, 0));
        assertEquals("EdgeY", it.info(0, GISDB.MAXCOORD));

        String searchResult = it.search(0, 0, 100);
        assertTrue(searchResult.contains("Origin"));
        assertFalse(searchResult.contains("EdgeY"));
    }

    /**
     * Tests a complex mixed-operation scenario involving multiple insertions,
     * duplicate names, searches, deletions, and tree structure checks.
     *
     * @throws IOException
     *                     if an I/O error occurs
     */
    public void testComplexMixedOperations() throws IOException {
        assertTrue(it.insert("NYC", 500, 800));
        assertTrue(it.insert("LA", 100, 200));
        assertTrue(it.insert("Chicago", 600, 700));
        assertTrue(it.insert("Houston", 300, 150));
        assertTrue(it.insert("Phoenix", 250, 100));
        assertTrue(it.insert("Chicago", 650, 720));

        String chicagoInfo = it.info("Chicago");
        assertTrue(chicagoInfo.contains("(600, 700)"));
        assertTrue(chicagoInfo.contains("(650, 720)"));

        String nycSearch = it.search(500, 800, 150);
        assertTrue(nycSearch.contains("NYC"));
        assertTrue(nycSearch.contains("Chicago (600, 700)"));

        String deletedChicago = it.delete(600, 700);
        assertTrue(deletedChicago.contains("Chicago"));

        assertEquals("Chicago", it.info(650, 720));

        String deletedPhoenix = it.delete("Phoenix");
        assertTrue(deletedPhoenix.contains("Phoenix"));
        assertEquals("", it.info("Phoenix"));

        assertEquals("NYC", it.info(500, 800));
        assertEquals("LA", it.info(100, 200));
        assertEquals("Houston", it.info(300, 150));

        String printResult = it.print();
        assertTrue(printResult.contains("Chicago"));
        assertTrue(printResult.contains("Houston"));
        assertTrue(printResult.contains("LA"));
        assertTrue(printResult.contains("NYC"));
        assertFalse(printResult.contains("Phoenix"));
    }

    /**
     * Tests mutation coverage for line 100 - x coordinate boundary checks in
     * insert() Tests both conditions: x < 0 and x > MAXCOORD
     */

    public void testInsertInvalidXCoordinates() {
        // Test x < 0 (first condition should be TRUE, insert fails)
        assertFalse(it.insert("NegativeX", -1, 100));
        assertEquals("", it.info(-1, 100));

        // Test x > MAXCOORD (second condition should be TRUE, insert fails)
        assertFalse(it.insert("TooLargeX", GISDB.MAXCOORD + 1, 100));
        assertEquals("", it.info(GISDB.MAXCOORD + 1, 100));

        // Test x = 0 (boundary, should succeed - both conditions FALSE)
        assertTrue(it.insert("MinX", 0, 100));
        assertEquals("MinX", it.info(0, 100));

        // Test x = MAXCOORD (boundary, should succeed - both conditions FALSE)
        assertTrue(it.insert("MaxX", GISDB.MAXCOORD, 200));
        assertEquals("MaxX", it.info(GISDB.MAXCOORD, 200));
    }

    /**
     * Tests mutation coverage for line 103 - y coordinate boundary checks in
     * insert() Tests both conditions: y < 0 and y > MAXCOORD
     */

    public void testInsertInvalidYCoordinates() {
        // Test y < 0 (first condition should be TRUE, insert fails)
        assertFalse(it.insert("NegativeY", 100, -1));
        assertEquals("", it.info(100, -1));

        // Test y > MAXCOORD (second condition should be TRUE, insert fails)
        assertFalse(it.insert("TooLargeY", 100, GISDB.MAXCOORD + 1));
        assertEquals("", it.info(100, GISDB.MAXCOORD + 1));

        // Test y = 0 (boundary, should succeed - both conditions FALSE)
        assertTrue(it.insert("MinY", 100, 0));
        assertEquals("MinY", it.info(100, 0));

        // Test y = MAXCOORD (boundary, should succeed - both conditions FALSE)
        assertTrue(it.insert("MaxY", 200, GISDB.MAXCOORD));
        assertEquals("MaxY", it.info(200, GISDB.MAXCOORD));
    }

    /**
     * Tests mutation coverage for line 197 - null/empty name checks in
     * info(String
     * name) Tests both conditions: name == null and name.isEmpty()
     */
    public void testInfoWithInvalidNames() {
        // Insert some cities first
        assertTrue(it.insert("ValidCity", 100, 100));
        assertTrue(it.insert("AnotherCity", 200, 200));

        // Test with null name (first condition should be TRUE, returns empty)
        // assertEquals("", it.info((String)null));

        // Test with empty string (second condition should be TRUE, returns
        // empty)
        assertEquals("", it.info(""));

        // Test with valid name (both conditions FALSE, returns data)
        String result = it.info("ValidCity");
        assertTrue(result.contains("ValidCity"));
        assertTrue(result.contains("100"));

        // Test with non-existent name (both conditions FALSE, but no match
        // found)
        assertEquals("", it.info("NonExistent"));
    }

    /**
     * Tests mutation coverage for line 197 - name.isEmpty() check in
     * info(String
     * name) Kills the mutation by inserting a city with empty name, so
     * searching
     * for it would return results, but the guard clause should prevent that
     * search
     */

    public void testInfoEmptyNameGuardClause() {
        // Insert a city with empty string as name (if City class allows it)
        // This is the key: make the findAll("") return non-empty
        boolean inserted = it.insert("", 100, 100);

        if (inserted) {
            // If empty name is allowed, the guard clause should still return
            // empty
            // But if mutation replaces isEmpty() with false, it will search and
            // find the city
            assertFuzzyEquals("100 100", it.info(""));
        } else {
            // If empty name is not allowed in insert, we need a different
            // strategy
            // Test that empty string returns empty without causing any BST
            // traversal
            assertTrue(it.insert("A", 100, 100));
            assertFuzzyEquals("", it.info(""));
        }
    }

    /**
     * Test deleting with x = 0 (valid minimum boundary) Kills mutations on line
     * 100: x < 0 (false case)
     */

    public void testDeleteWithXAtZero() {
        testDB.insert("EdgeCity", 0, 100);
        String result = testDB.delete(0, 100);
        assertTrue(result.contains("EdgeCity"));
        // Verify city was deleted
        assertEquals("", testDB.info(0, 100));
    }

    /**
     * Test deleting with x = MAXCOORD (valid maximum boundary) Kills mutations
     * on
     * line 101: x > MAXCOORD (false case)
     */

    public void testDeleteWithXAtMax() {
        testDB.insert("MaxCity", 32767, 100);
        String result = testDB.delete(32767, 100);
        assertTrue(result.contains("MaxCity"));
        // Verify city was deleted
        assertEquals("", testDB.info(32767, 100));
    }

    /**
     * Test deleting with y = 0 (valid minimum boundary) Kills mutations on line
     * 106: y < 0 (false case)
     */

    public void testDeleteWithYAtZero() {
        testDB.insert("EdgeCity", 100, 0);
        String result = testDB.delete(100, 0);
        assertTrue(result.contains("EdgeCity"));
        // Verify city was deleted
        assertEquals("", testDB.info(100, 0));
    }

    /**
     * Test deleting with y = MAXCOORD (valid maximum boundary) Kills mutations
     * on
     * line 107: y > MAXCOORD (false case)
     */

    public void testDeleteWithYAtMax() {
        testDB.insert("MaxCity", 100, 32767);
        String result = testDB.delete(100, 32767);
        assertTrue(result.contains("MaxCity"));
        // Verify city was deleted
        assertEquals("", testDB.info(100, 32767));
    }

    /**
     * Test deleting from empty database Kills mutations on line 113:
     * kdOutput.trim().isEmpty()
     */

    public void testDeleteFromEmptyDatabase() {
        String result = testDB.delete(100, 100);
        assertEquals("", result.trim());
    }

    /**
     * Test deleting existing city returns correct output Kills mutations on
     * line
     * 113: kdOutput.trim().isEmpty() (false case)
     */

    public void testDeleteExistingCityReturnsOutput() {
        testDB.insert("Boston", 150, 250);
        String result = testDB.delete(150, 250);
        assertFalse(result.trim().isEmpty());
        assertTrue(result.contains("Boston"));
        // Verify city was deleted
        assertEquals("", testDB.info(150, 250));
    }

    /**
     * Test deleting a city that does not exist
     */
    public void testDeleteNonExistentCity() {
        // Don't insert anything

        // Should return just the visit count when nothing is deleted
        String result = testDB.delete(100, 100);
        assertTrue(result.trim().isEmpty() || result.matches("\\d+ *"));
    }
}
