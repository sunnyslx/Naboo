package net.imoran.tv.sdk.network.requestdata;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jia on 2017/7/27.
 */

public class APISignUtils {
    private static final String TAG = "SignUtils";
    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";


    public static String getSignString(String stringToSign, String keySecret) {
        try {
            String result1 = new String(Base64.encode(hmacSHA1Encrypt(stringToSign, keySecret), Base64.DEFAULT));
            String result2 = Base64.encodeToString(hmac_sha1(stringToSign, keySecret).getBytes(), Base64.DEFAULT);
            return result2;
        } catch (Exception e) {
            Log.e(TAG, "getSignString: error: " + e.getMessage());
        }

        return null;
    }


    /**
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
     *
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return
     * @throws Exception
     */
    public static byte[] hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        //完成 Mac 操作
        return mac.doFinal(text);
    }


    public static String hmac_sha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            String hexBytes = byte2hex(rawHmac);
            return hexBytes.trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String byte2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1) hs = hs + "0" + stmp;
            else hs = hs + stmp;
        }
        return hs;
    }

    private static String appendEqualSign(String s) {
        int len = s.length();
        int appendNum = 8 - (int) (len / 8);
        for (int n = 0; n < appendNum; n++) {
            s += "%3D";
        }
        return s;
    }
}
