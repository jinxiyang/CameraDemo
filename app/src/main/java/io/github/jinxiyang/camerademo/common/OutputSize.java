package io.github.jinxiyang.camerademo.common;

public class OutputSize implements Comparable<OutputSize>{

    private final int width;
    private final int height;

    public OutputSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return width + "*" + height;
    }

    @Override
    public int hashCode() {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return height ^ ((width << (Integer.SIZE / 2)) | (width >>> (Integer.SIZE / 2)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputSize that = (OutputSize) o;
        return width == that.width && height == that.height;
    }


    @Override
    public int compareTo(OutputSize o) {
        return width * height - o.width * o.height;
    }
}
