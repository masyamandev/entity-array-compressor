package com.masyaman.datapack.utils;

import java.io.ByteArrayOutputStream;

public class ByteStream extends ByteArrayOutputStream {

    private int lastPosition = 0;

    public byte[] getNewBytes() {
        byte[] bytes = toByteArray();
        byte[] newBytes = new byte[bytes.length - lastPosition];
        System.arraycopy(bytes, lastPosition, newBytes, 0, newBytes.length);
        lastPosition = bytes.length;
        return newBytes;
    }

    public static byte[] toByteArray(Object... data) {
        ByteStream bs = new ByteStream();
        for (Object o : data) {
            if (o instanceof Number) {
                bs.write(((Number) o).intValue());
            } else if (o instanceof Character) {
                bs.write(((Character) o).charValue());
            } else if (o instanceof CharSequence) {
                CharSequence cs = (CharSequence) o;
                for (int i = 0; i < cs.length(); i++) {
                    bs.write(cs.charAt(i));
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        return bs.toByteArray();
    }
}
