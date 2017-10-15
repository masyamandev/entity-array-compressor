package com.masyaman.datapack.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MultipleByteInputStreamHandler {
    private static final byte[] EMPTY = new byte[0];

    private List<ByteArrayInputWrapper> streams = new ArrayList<>();
    private int currentStream = 0;

    private EmptyBufferCallback emptyBufferCallback;

    public EmptyBufferCallback getEmptyBufferCallback() {
        return emptyBufferCallback;
    }

    public void setEmptyBufferCallback(EmptyBufferCallback emptyBufferCallback) {
        this.emptyBufferCallback = emptyBufferCallback;
    }

    public ByteArrayInputWrapper newStream() {
        if (streams.size() <= currentStream) {
            streams.add(new ByteArrayInputWrapper());
        }
        ByteArrayInputWrapper stream = streams.get(currentStream);
        currentStream++;
        return stream;
    }

    public List<ByteArrayInputWrapper> getStreams() {
        return streams;
    }

    public void readBuffer(int id, int len, InputStream is) throws IOException {
        while (streams.size() <= id) {
            streams.add(new ByteArrayInputWrapper());
        }
        streams.get(id).readFrom(is, len);
    }

    public class ByteArrayInputWrapper extends ByteArrayInputStream {

        public ByteArrayInputWrapper() {
            super(EMPTY, 0, 0);
        }

        @Override
        public synchronized int read() {
            while (available() < 1 && emptyBufferCallback.readBuffer());
            return super.read();
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) {
            while (available() < len && emptyBufferCallback.readBuffer());
            return super.read(b, off, len);
        }

        private void readFrom(InputStream is, int len) throws IOException {
            int available = available();
            if (available + len > buf.length) {
                byte[] oldBuf = buf;
                buf = new byte[available + len * 2];
                System.arraycopy(oldBuf, pos, buf, 0, available);
                pos = 0;
                count = available;
            } else if (count + len >= buf.length) {
                System.arraycopy(buf, pos, buf, 0, available);
                pos = 0;
                count = available;
            }
            is.read(buf, count, len);
            count += len;
        }
    }

    public interface EmptyBufferCallback {
        boolean readBuffer();
    }
}
