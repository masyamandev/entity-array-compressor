package com.masyaman.datapack.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MultipleByteOutputStreamHandler {

    private int count = 0;
    private List<ByteArrayOutputWrapper> streams = new ArrayList<>();


    public ByteArrayOutputStream newStream() {
        ByteArrayOutputWrapper stream = new ByteArrayOutputWrapper();
        streams.add(stream);
        return stream;
    }

    public int getCount() {
        return count;
    }

    public List<ByteArrayOutputWrapper> getStreams() {
        return streams;
    }

    public class ByteArrayOutputWrapper extends ByteArrayOutputStream {
        @Override
        public synchronized void write(int b) {
            MultipleByteOutputStreamHandler.this.count++;
            super.write(b);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            MultipleByteOutputStreamHandler.this.count += len;
            super.write(b, off, len);
        }

        @Override
        public synchronized void writeTo(OutputStream out) throws IOException {
            super.writeTo(out);
            MultipleByteOutputStreamHandler.this.count -= count;
            count = 0;
        }

        public int getCount() {
            return count;
        }
    }
}
