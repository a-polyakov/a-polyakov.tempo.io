package interview.tempo

import interview.tempo.hierarchy.LongArrayList

/**
 * A `Hierarchy` stores an arbitrary _forest_ (an ordered collection of ordered trees)
 * as an array of node IDs in the order of DFS traversal, combined with a parallel array of node depths.
 *
 * Parent-child relationships are identified by the position in the array and the associated depth.
 * Each tree root has depth 0, its children have depth 1 and follow it in the array, their children have depth 2 and follow them, etc.
 *
 * Example:
 * ```
 * nodeIds: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
 * depths:  0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2
 * ```
 *
 * the forest can be visualized as follows:
 * ```
 * 1
 * - 2
 * - - 3
 * - - - 4
 * - 5
 * 6
 * - 7
 * 8
 * - 9
 * - 10
 * - - 11
 *```
 * 1 is a parent of 2 and 5, 2 is a parent of 3, etc. Note that depth is equal to the number of hyphens for each node.
 *
 * Invariants on the depths array:
 *  * Depth of the first element is 0.
 *  * If the depth of a node is `D`, the depth of the next node in the array can be:
 *      * `D + 1` if the next node is a child of this node;
 *      * `D` if the next node is a sibling of this node;
 *      * `d < D` - in this case the next node is not related to this node.
 */
interface Hierarchy {
    /** The number of nodes in the hierarchy. */
    val size: Long

    /**
     * Returns the unique ID of the node identified by the hierarchy index. The depth for this node will be `depth(index)`.
     * @param index must be non-negative and less than [size]
     * */
    fun nodeId(index: Long): Long

    /**
     * Returns the depth of the node identified by the hierarchy index. The unique ID for this node will be `nodeId(index)`.
     * @param index must be non-negative and less than [size]
     * */
    fun depth(index: Long): Long

    fun formatString(): String {
        return (0 until size).joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]"
        ) { i -> "${nodeId(i)}:${depth(i)}" }
    }
}

/**
 * A node is present in the filtered hierarchy iff its node ID passes the predicate and all of its ancestors pass it as well.
 */
fun Hierarchy.filter(nodeIdPredicate: (Long) -> Boolean): Hierarchy {
    if (size == 0L) {
        return ArrayBasedHierarchy(LongArrayList(0), LongArrayList(0));
    }
    val resultNodeIds = LongArrayList<Long>(size);
    val resultDepths = LongArrayList<Long>(size);

    var blockedDepth: Long = -1L;

    for (i in 0 until size) {
        val nodeId = nodeId(i);
        val depth = depth(i);

        if (blockedDepth != -1L && depth <= blockedDepth) {
            blockedDepth = -1L;
        }

        if (blockedDepth != -1L) {
            continue;
        }

        if (!nodeIdPredicate(nodeId)) {
            blockedDepth = depth;
            continue;
        }

        resultNodeIds.add(nodeId);
        resultDepths.add(depth);
    }

    return ArrayBasedHierarchy(
        resultNodeIds,
        resultDepths
    );
}