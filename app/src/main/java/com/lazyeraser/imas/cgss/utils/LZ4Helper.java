package com.lazyeraser.imas.cgss.utils;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Decompressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.util.ByteBufferUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lazyeraser on 2017/12/4.
 */

public class LZ4Helper {

    public static byte[] compress(byte srcBytes[]) throws IOException {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        LZ4Compressor compressor = factory.fastCompressor();
        LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(
                byteOutput, 2048, compressor);
        compressedOutput.write(srcBytes);
        compressedOutput.close();
        return byteOutput.toByteArray();
    }

    public static byte[] uncompress(byte[] bytes) throws IOException {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LZ4FastDecompressor decompresser = factory.fastDecompressor();

        LZ4BlockInputStream lzis = new LZ4BlockInputStream(
                new ByteArrayInputStream(bytes), decompresser);
        int count;
        byte[] buffer = new byte[2048 * 256];
        while ((count = lzis.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }
        lzis.close();
        return baos.toByteArray();
    }

    public static byte[] uncompressCGSS(byte[] src) throws IOException {
        byte[] buf = new byte[4];
        System.arraycopy(src, 4, buf, 0, 4);
        int destL = getInt(buf, 0);


        byte[] source = new byte[src.length - 16];
        byte[] dest = new byte[destL];
        System.arraycopy(src, 16, source, 0, src.length - 16);

        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4FastDecompressor decompresser = factory.fastDecompressor();
        decompresser.decompress(source, dest, destL);
        return dest;
    }

    public static int getInt(byte[] buf, int ofs) {
        return (
                (buf[ofs] & 0xFF)
                        + ((buf[ofs + 1] & 0xFF) << 8)
                        + ((buf[ofs + 2] & 0xFF) << 16)
                        + ((buf[ofs + 3] & 0xFF) << 24)
        );
    }

    public static int byteArrayToIntL(byte[] a) {
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }


    public static String uncompress(String source) {
        String result = null;
        try {
            result = Arrays.toString(uncompress(source.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
