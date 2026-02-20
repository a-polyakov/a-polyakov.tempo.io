package interview.tempo

import interview.tempo.hierarchy.LongArrayList

class ArrayBasedHierarchy(
    private val myNodeIds: LongArrayList<Long>,
    private val myDepths: LongArrayList<Long>,
) : Hierarchy {
    override val size: Long = myDepths.size()

    override fun nodeId(index: Long): Long = myNodeIds.get(index)

    override fun depth(index: Long): Long = myDepths.get(index)
}