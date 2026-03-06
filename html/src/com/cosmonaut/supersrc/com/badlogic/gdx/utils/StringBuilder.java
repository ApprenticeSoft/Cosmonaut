package com.badlogic.gdx.utils;

/**
 * Compatibility shim for older third-party source (box2dlights gwt sources)
 * that imports com.badlogic.gdx.utils.StringBuilder.
 */
public class StringBuilder implements CharSequence {
    private final java.lang.StringBuilder delegate;

    public StringBuilder() {
        this.delegate = new java.lang.StringBuilder();
    }

    public StringBuilder(int capacity) {
        this.delegate = new java.lang.StringBuilder(capacity);
    }

    public StringBuilder(String value) {
        this.delegate = new java.lang.StringBuilder(value);
    }

    public StringBuilder(CharSequence value) {
        this.delegate = new java.lang.StringBuilder(value);
    }

    public int length() {
        return delegate.length();
    }

    public int capacity() {
        return delegate.capacity();
    }

    public void setLength(int newLength) {
        delegate.setLength(newLength);
    }

    public StringBuilder append(String value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(char value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(char[] value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(char[] value, int offset, int len) {
        delegate.append(value, offset, len);
        return this;
    }

    public StringBuilder append(CharSequence value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(CharSequence value, int start, int end) {
        delegate.append(value, start, end);
        return this;
    }

    public StringBuilder append(float value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(double value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(int value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(long value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(boolean value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder append(Object value) {
        delegate.append(value);
        return this;
    }

    public StringBuilder delete(int start, int end) {
        delegate.delete(start, end);
        return this;
    }

    public StringBuilder deleteCharAt(int index) {
        delegate.deleteCharAt(index);
        return this;
    }

    public StringBuilder insert(int offset, char value) {
        delegate.insert(offset, value);
        return this;
    }

    public StringBuilder insert(int offset, String value) {
        delegate.insert(offset, value);
        return this;
    }

    public char charAt(int index) {
        return delegate.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return delegate.subSequence(start, end);
    }

    public String substring(int start) {
        return delegate.substring(start);
    }

    public String substring(int start, int end) {
        return delegate.substring(start, end);
    }

    public int indexOf(String value) {
        return delegate.indexOf(value);
    }

    public int indexOf(String value, int fromIndex) {
        return delegate.indexOf(value, fromIndex);
    }

    public StringBuilder reverse() {
        delegate.reverse();
        return this;
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        delegate.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
