/*
 * File: StringRingBuffer.java Created: 02.01.2008 Author: Ferdinand Hofherr
 * <ferdinand.hofherr@uni-ulm.de>
 */
package ideogram.r;

/**
 * Ring buffer for storing the output of the Rconsole.
 * 
 * @author Ferdinand Hofherr
 */
public class RConsoleBuffer {

    private char[] buffer;

    /*
     * Pointers to start and end of buffer. The buffer is considered empty,
     * when start == end == -1. The buffer must be always read from start to
     * end!
     */
    private int start, end;

    /**
     * Create a new empty StringRingBuffer of the given size.
     * 
     * @param size
     */
    public RConsoleBuffer(int size) {
        buffer = new char[size];
        flush();
    }

    /**
     * Empty the buffer.
     */
    public synchronized void flush() {
        start = end = -1;
    }

    /**
     * Check whether buffer is empty.
     * 
     * @return true if buffer empty, else false.
     */
    public synchronized boolean isEmpty() {
        return start == -1 && end == -1;
    }

    /**
     * Calculate pointer values.
     */
    private synchronized void calcPointers() {
        // Initialize buffer if empty.
        if (isEmpty()) {
            start = end = 0;
        }
        else {
            // Recalculate end. Assure it is lower than buffer.length.
            end = (end + 1) % buffer.length;
            // Buffer overflow. Overwrite old characters.
            if (end == start) {
                start = (start + 1) % buffer.length;
            }
        }
    }

    /**
     * Insert the String s into the buffer.
     * 
     * @param s
     */
    public void insert(String s) {
        char[] sArr = s.toCharArray();
        synchronized (this) {
            for (char c : sArr) {
                calcPointers();
                buffer[end] = c;
            }
        }
    }

    /**
     * Calculate and return the number of characters currently stored in the
     * buffer.
     * 
     * @return Number of currently stored characters.
     */
    public synchronized int noChars() {
        if (isEmpty()) {
            return 0;
        }
        else if (start <= end) {
            // start == end may be the case, when there is only one character
            // in the buffer.
            return end - start + 1;
        }
        else {
            return buffer.length - start + end + 1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        char[] ca = new char[noChars()];
        synchronized (this) {
            int bPointer = start;
            for (int i = 0; i < ca.length; i++) {
                ca[i] = buffer[bPointer];
                bPointer = (bPointer + 1) % buffer.length;
            }
        }
        return new String(ca);
    }

}
