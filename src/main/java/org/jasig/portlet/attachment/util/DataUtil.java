/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.attachment.util;

import org.apache.commons.codec.binary.Base64;

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
        return Base64.decodeBase64(encoded);
    }

    public static String decodeAsString(String encoded)
    {
        return new String(decode(encoded));
    }

    private DataUtil() { }
}
