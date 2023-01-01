package com.mygame.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩和解压缩 数据包 工具类
 */
public class CompressUtil {

    public static byte[] compress(byte[] msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        byte[] ret;
        try {
            gzip.write(msg);
            gzip.finish();
            ret = bos.toByteArray();
        } finally {
            gzip.close();
            bos.close();
        }
        return ret;
    }

    public static byte[] decompress(byte[] msg) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(msg);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] ret;
        try {
            byte[] buf = new byte[1024];
            int num = -1;
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, num);
            }
            ret = bos.toByteArray();
            bos.flush();
        } finally {
            gzip.close();
            bis.close();
            bos.close();
        }
        return ret;
    }
}
