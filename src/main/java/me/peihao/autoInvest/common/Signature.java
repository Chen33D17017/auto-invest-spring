package me.peihao.autoInvest.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;


public class Signature {
  /**
   * @param key the key used to sign the data.
   * @param data the data to be signed in UTF-8 format.
   * @return the data signature.
   */
  public static String encode(String key, String data) {
    try {
      Mac hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      hmac.init(secret_key);
      return new String(Hex.encodeHex(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8))));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String hmacSha(String KEY, String VALUE) {
    try {
      SecretKeySpec signingKey = new SecretKeySpec(KEY.getBytes("UTF-8"), "HmacSHA256");
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(signingKey);
      byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));
      byte[] hexArray = {(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'};
      byte[] hexChars = new byte[rawHmac.length * 2];
      for (int j = 0; j < rawHmac.length; j++) {
        int v = rawHmac[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
      }
      return new String(hexChars);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}