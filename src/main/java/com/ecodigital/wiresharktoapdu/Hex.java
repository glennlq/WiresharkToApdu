/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecodigital.wiresharktoapdu;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.zip.CRC32;

/**
 *
 * @author gluque
 */
public final class Hex {
    
    private static Random random = new Random();

    public static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    }; 
    

    private Hex() {
    }

    public static boolean arrayEquals(final byte[] v, final byte[] w) {
        return Hex.arrayEquals(v, 0, v.length, w, 0, w.length);
    }

    public static boolean arrayEquals(final byte[] v, final int vOffset, final int vLen, final byte[] w, final int wOffset, final int wLen) {
        if (vLen != wLen || v.length < vOffset + vLen || w.length < wOffset + wLen) {
            return false;
        }

        for (int i = 0; i < vLen; i++) {
            if (v[i + vOffset] != w[i + wOffset]) {
                return false;
            }
        }
        return true;
    }

    public static short getShort(final byte[] data, final int offset) {
        return (short) Hex.getUnsignedInt(data, offset);
    }

    public static int getUnsignedInt(final byte[] data, final int offset) {
        return (data[offset] & 0xff) << 8 | data[offset + 1] & 0xff;
    }

    public static String hexify(final byte abyte[], final boolean separator) {
        if (abyte == null) {
            return "null";
        }
        final StringBuffer stringbuffer = new StringBuffer(256);
        boolean hasSalt = false;
        int i = 0;
        for (final byte element : abyte) {
            if(hasSalt){
                stringbuffer.append("<br/>");
                hasSalt = false;
            }
            if (separator && i > 0) {
                stringbuffer.append(' ');
            }
            stringbuffer.append(Hex.HEX_CHARS[element >> 4 & 0xf]);
            stringbuffer.append(Hex.HEX_CHARS[element & 0xf]);
            if (++i == 16) {
                if (separator) {
                    hasSalt = true;
                }
                i = 0;
            }
        }
        return stringbuffer.toString();
    }
    
    public static String hexify(final byte data) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Hex.HEX_CHARS[data >> 4 & 0xf]);
        stringBuilder.append(Hex.HEX_CHARS[data & 0xf]);
        return stringBuilder.toString();
    }


    public static byte[] subArray(final byte[] src, final int srcPos, final int length) {
        if (length == 0) {
            return null;
        }
        if (src.length < srcPos + length) {
            return null;
        }
        final byte[] temp = new byte[length];
        System.arraycopy(src, srcPos, temp, 0, length);
        return temp;
    }

    public static byte[] xor(final byte[] v, final byte[] w) {

        byte[] xored = null;
        byte[] trimmedXor = null;
        xored = new BigInteger(1, v).xor(new BigInteger(1, w)).toByteArray();
        trimmedXor = new byte[v.length];
        if (xored.length >= trimmedXor.length) {
            System.arraycopy(xored, xored.length - trimmedXor.length, trimmedXor, 0, trimmedXor.length);
        }
        else {
            System.arraycopy(xored, 0, trimmedXor, trimmedXor.length - xored.length, xored.length);
        }
        return trimmedXor;
    }

    public static byte[] intToByteArray(final int value) {
        final byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            final int offset = (b.length - 1 - i) * 8;
            b[3 - i] = (byte) (value >>> offset & 0xFF);
        }
        return b;
    }
    
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    public static byte HexStringToByte(String s) {
        return (byte) ((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16));
    }
    
    public static  byte[] intToTwoByte(final int value){
        return new byte[] {(byte)(value >>> 8),(byte)value};
    }
    
    public static byte[] CRC32(final byte[] data) throws Exception {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        byte[] crc = intToByteArray((int)crc32.getValue());
        //byte[] crc = hexStringToByteArray(Long.toHexString(crc32.getValue()));
        if(crc.length != 4)
            throw new Exception("Error al generar CRC32");
        return crc;
    }
    
    public static byte[] CRC32(final File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        FileChannel channel = in.getChannel();
        CRC32 crc32 = new CRC32();
        int length = (int) channel.size();
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);
        for (int p = 0; p < length; p++)
        {
           int c = buffer.get(p);
           crc32.update(c);
        }
        byte[] crc = intToByteArray((int)crc32.getValue());
        if(crc.length != 4)
            throw new Exception("Error al generar CRC32");
        return crc;
    }
    
    
    public static byte getByteCommit(boolean on) {
        int rest = (int)(random.nextDouble()*100000)%254;
        if(on){
            return (byte)((rest % 2 == 1) ? rest : (rest + 1));
        } else {
            return (byte)((rest % 2 == 0) ? rest : (rest + 1));
        }
    }


    public static boolean validByteCommit(byte check){
        int i = check & 0xFF;
        return (i % 2 == 1);
    }

}