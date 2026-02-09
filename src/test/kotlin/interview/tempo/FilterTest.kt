package interview.tempo

import kotlin.test.Test
import kotlin.test.assertEquals

class FilterTest {
    @Test
    fun testFilter() {
        val unfiltered: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            intArrayOf(0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2)
        )
        val filteredActual: Hierarchy = unfiltered.filter { nodeId -> nodeId % 3 != 0 }
        val filteredExpected: Hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 2, 5, 8, 10, 11),
            intArrayOf(0, 1, 1, 0, 1, 2)
        )
        assertEquals(filteredExpected.formatString(), filteredActual.formatString())
    }

    @Test
    fun testFilter_empty() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(),
            intArrayOf()
        )

        val filtered = hierarchy.filter { true }

        assertEquals("[]", filtered.formatString())
    }

    @Test
    fun testFilter_allNodesAllowed() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 12),
            intArrayOf(0, 1, 1)
        )

        val filtered = hierarchy.filter { true }

        assertEquals(hierarchy.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_noNodesAllowed() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 12),
            intArrayOf(0, 1, 1)
        )

        val filtered = hierarchy.filter { false }

        assertEquals("[]", filtered.formatString())
    }

    @Test
    fun testFilter_cutBranch() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 111, 112, 12, 121, 122),
            intArrayOf(0, 1, 2, 2, 1, 2, 2)
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

        val filtered = hierarchy.filter { nodeId -> nodeId != 11 }

        val expected = ArrayBasedHierarchy(
            intArrayOf(1, 12, 121, 122),
            intArrayOf(0, 1, 2, 2)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutLeaf() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 111, 112, 12, 121, 122),
            intArrayOf(0, 1, 2, 2, 1, 2, 2)
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
            intArrayOf(1, 11, 12),
            intArrayOf(0, 1, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutRootAndBranch() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 2, 21, 3, 31),
            intArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId <= 2 }

        val expected = ArrayBasedHierarchy(
            intArrayOf(1, 2),
            intArrayOf(0, 0)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }

    @Test
    fun testFilter_cutFirstRoot() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 2, 21, 3, 31),
            intArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 1 }

        val expected = ArrayBasedHierarchy(
            intArrayOf(2, 21, 3, 31),
            intArrayOf(0, 1, 0, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }
    @Test
    fun testFilter_cutMidlRoot() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 2, 21, 3, 31),
            intArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 2 }

        val expected = ArrayBasedHierarchy(
            intArrayOf(1, 11, 3, 31),
            intArrayOf(0, 1, 0, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }
    @Test
    fun testFilter_cutLastRoot() {
        val hierarchy = ArrayBasedHierarchy(
            intArrayOf(1, 11, 2, 21, 3, 31),
            intArrayOf(0, 1, 0, 1, 0, 1)
        )
        /*
          1
          - 11
          2
          - 21
          3
          - 31
         */

        val filtered = hierarchy.filter { nodeId -> nodeId != 3 }

        val expected = ArrayBasedHierarchy(
            intArrayOf(1, 11, 2, 21),
            intArrayOf(0, 1, 0, 1)
        )

        assertEquals(expected.formatString(), filtered.formatString())
    }
}