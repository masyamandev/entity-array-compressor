package com.masyaman.datapack.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionReordererTest {

    @Test
    public void testReordering() throws Exception {
        CollectionReorderer reorderer = new CollectionReorderer(0);

        // Do not change order in first call
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", "X", "C"))).containsExactly("Z", "X", "C");
        // Move new objects to end of list
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", "X", "Q", "C"))).containsExactly("Z", "X", "C", "Q");
        // Reorder elements as in previous list
        assertThat(reorderer.reorderByUsage(Arrays.asList("C", "X", "Z", "A", "Q"))).containsExactly("Z", "X", "C", "Q", "A");

        // Remove object from list
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", "Q", "C"))).containsExactly("Z", "C", "Q");
        // Add objects again, it should be moved to end
        assertThat(reorderer.reorderByUsage(Arrays.asList("C", "X", "Z", "A", "Q"))).containsExactly("Z", "C", "Q", "X", "A");

        // do the same one more time with object shuffling, results must be the same
        assertThat(reorderer.reorderByUsage(Arrays.asList("C", "Q", "Z"))).containsExactly("Z", "C", "Q");
        assertThat(reorderer.reorderByUsage(Arrays.asList("Q", "A", "Z", "X", "C"))).containsExactly("Z", "C", "Q", "X", "A");
    }

    @Test
    public void testSameElements() throws Exception {
        CollectionReorderer reorderer = new CollectionReorderer(0);

        // Do not change order in first call
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", "X", "Z"))).containsExactly("Z", "X", "Z");
        // Group same objects
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", "X", "Z"))).containsExactly("Z", "Z", "X");

        // Move new objects to end of list
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", "Q", "X", "Q", "Z"))).containsExactly("Z", "Z", "X", "Q", "Q");
    }

    @Test
    public void testNulls() throws Exception {
        CollectionReorderer reorderer = new CollectionReorderer(0);

        assertThat(reorderer.reorderByUsage(Arrays.asList(null, "X", "Z"))).containsOnly("X", "Z", null);
        assertThat(reorderer.reorderByUsage(Arrays.asList("Z", null))).containsExactly("Z", null);
        assertThat(reorderer.reorderByUsage(Arrays.asList(null, "X", null, "Z", null))).containsOnly("Z", "X", null, null, null);
    }
}