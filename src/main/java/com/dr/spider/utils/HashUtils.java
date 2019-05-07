package com.dr.spider.utils;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Hex;

public class HashUtils {

  static int num = 4096;

  private static final byte[] SALT = new byte[]{22, -45, 78, 29, -10, 76, -24, 13, -76, 126, 25, 0,
      3, -20, 113, 108};

  public static int getHashFilePath(String sn) {
    String m = Hex.encodeHexString(digest(sn.getBytes(), "MD5", 21));
    return (Math.abs(m.hashCode()) + 609) % num;
  }


  /**
   * 对字符数组进行散列, 支持md5与sha1算法.
   */
  private static byte[] digest(byte[] input, String algorithm, int iterations) {
    try {
      MessageDigest digest = MessageDigest.getInstance(algorithm);
      if (SALT != null) {
        digest.update(SALT);
      }
      byte[] result = digest.digest(input);
      for (int i = 0; i < iterations; i++) {
        digest.reset();
        result = digest.digest(result);
      }
      return result;
    } catch (GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String... a) {
    System.out.println(getHashFilePath("B0990") + "");
  }
}
