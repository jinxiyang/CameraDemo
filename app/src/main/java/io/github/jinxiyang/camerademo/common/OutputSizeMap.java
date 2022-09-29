package io.github.jinxiyang.camerademo.common;

import android.util.ArrayMap;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A collection class that automatically groups {@link OutputSize}s by their {@link AspectRatio}s.
 */
public class OutputSizeMap {

    private final ArrayMap<AspectRatio, SortedSet<OutputSize>> mRatios = new ArrayMap<>();

    /**
     * Add a new {@link OutputSize} to this collection.
     *
     * @param size The size to add.
     * @return {@code true} if it is added, {@code false} if it already exists and is not added.
     */
    public boolean add(OutputSize size) {
        for (AspectRatio ratio : mRatios.keySet()) {
            if (ratio.matches(size)) {
                final SortedSet<OutputSize> sizes = mRatios.get(ratio);
                if (sizes.contains(size)) {
                    return false;
                } else {
                    sizes.add(size);
                    return true;
                }
            }
        }
        // None of the existing ratio matches the provided size; add a new key
        SortedSet<OutputSize> sizes = new TreeSet<>();
        sizes.add(size);
        mRatios.put(AspectRatio.of(size.getWidth(), size.getHeight()), sizes);
        return true;
    }

    /**
     * Removes the specified aspect ratio and all sizes associated with it.
     *
     * @param ratio The aspect ratio to be removed.
     */
    public void remove(AspectRatio ratio) {
        mRatios.remove(ratio);
    }

    public Set<AspectRatio> ratios() {
        return mRatios.keySet();
    }

    public SortedSet<OutputSize> sizes(AspectRatio ratio) {
        return mRatios.get(ratio);
    }

    public void clear() {
        mRatios.clear();
    }

    public boolean isEmpty() {
        return mRatios.isEmpty();
    }

}
