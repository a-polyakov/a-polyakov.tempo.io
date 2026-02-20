package interview.tempo

import interview.tempo.hierarchy.LongArrayList
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterTest {

    fun longArrayOf(vararg elements: Long): LongArrayList<Long> {
        val result = LongArrayList<Long>(elements.size.toLong())
        for (i in elements) {
            result.add(i);
        }
        return result;
    }

    @Test
    fun testFilter() {
        val unfiltered: Hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            longArrayOf(0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2)
        )
        val filteredActual: Hierarchy = unfiltered.filter { nodeId -> nodeId % 3 != 0L }
        val filteredExpected: Hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 2, 5, 8, 10, 11),
            longArrayOf(0, 1, 1, 0, 1, 2)
        )
        assertEquals(filteredExpected.formatString(), filteredActual.formatString())
    }

    @Test
    fun testFilter_empty() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(),
            longArrayOf()
        )

        val filtered = hierarchy.filter { true }

        assertEquals("[]", filtered.formatString())
    }

    @Test
    fun testFilter_allNodesAllowed() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 12),
            longArrayOf(0, 1, 1)
        )

        val filtered = hierarchy.filter { true }

        assertEquals(hierarchy.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_noNodesAllowed() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 12),
            longArrayOf(0, 1, 1)
        )

        val filtered = hierarchy.filter { false }

        assertEquals("[]", filtered.formatString())
    }

    @Test
    fun testFilter_cutBranch() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 111, 112, 12, 121, 122),
            longArrayOf(0, 1, 2, 2, 1, 2, 2)
        )
        /*
          1
          - 11
          - - 111
          - - 112
          - 12
          - - 121
          - - 122
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 11L }

        val expected = ArrayBasedHierarchy(
            longArrayOf(1, 12, 121, 122),
            longArrayOf(0, 1, 2, 2)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutLeaf() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 111, 112, 12, 121, 122),
            longArrayOf(0, 1, 2, 2, 1, 2, 2)
        )
        /*
          1
          - 11
          - - 111
          - - 112
          - 12
          - - 121
          - - 122
         */

        val filtered = hierarchy.filter { nodeId -> nodeId <= 12 }

        val expected = ArrayBasedHierarchy(
            longArrayOf(1, 11, 12),
            longArrayOf(0, 1, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutRootAndBranch() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 2, 21, 3, 31),
            longArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId <= 2L }

        val expected = ArrayBasedHierarchy(
            longArrayOf(1, 2),
            longArrayOf(0, 0)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutFirstRoot() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 2, 21, 3, 31),
            longArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 1L }

        val expected = ArrayBasedHierarchy(
            longArrayOf(2, 21, 3, 31),
            longArrayOf(0, 1, 0, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutMidlRoot() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 2, 21, 3, 31),
            longArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 2L }

        val expected = ArrayBasedHierarchy(
            longArrayOf(1, 11, 3, 31),
            longArrayOf(0, 1, 0, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutLastRoot() {
        val hierarchy = ArrayBasedHierarchy(
            longArrayOf(1, 11, 2, 21, 3, 31),
            longArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 3L }

        val expected = ArrayBasedHierarchy(
            longArrayOf(1, 11, 2, 21),
            longArrayOf(0, 1, 0, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }
}