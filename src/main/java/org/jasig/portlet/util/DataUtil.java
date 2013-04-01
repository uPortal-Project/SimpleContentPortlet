package org.jasig.portlet.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class DataUtil {
    private static final Base64 BASE64 = new Base64();

    public static byte[] encode(byte[] unencoded)
    {
        return BASE64.encode(unencoded);
    }

    public static String encodeAsString(byte[] unencoded)
    {
        return BASE64.encodeAsString(unencoded);
    }

    public static byte[] encode(String unencoded)
    {
        return encode(unencoded.getBytes());
    }

    public static String encodeAsString(String unencoded)
    {
        return encodeAsString(unencoded.getBytes());
    }

    public static byte[] decode(byte[] encoded)
    {
        return BASE64.decode(encoded);
    }

    public static String decodeAsString(byte[] encoded)
    {
        return new String(decode(encoded));
    }

    public static byte[] decode(String encoded)
    {
        return BASE64.decode(encoded.getBytes());
    }

    public static String decodeAsString(String encoded)
    {
        return new String(decode(encoded));
    }

    private DataUtil() { }
}
