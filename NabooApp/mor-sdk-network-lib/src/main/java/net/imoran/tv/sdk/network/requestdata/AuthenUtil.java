package net.imoran.tv.sdk.network.requestdata;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wukeshi on 2017/8/7.
 */

public class AuthenUtil {
    public static String keySecret = "20f61f0404c832679bd8b85bd34da2e2566d4b69";

    public static String genSign(Map<String, String> params) throws Exception {

        return genSign(params, false);
    }

    public static String genSign(Map<String, String> params, boolean toLowerCase) throws Exception {
        TreeMap<String, String> signMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            signMap.put(entry.getKey(), entry.getValue());
        }

        StringBuilder strBuilder = new StringBuilder();
        StringBuilder strBuilderWithoutJson = new StringBuilder();
        boolean needAnd = false;
        boolean needAndJson = false;
        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            if (!entry.getKey().equals("json")) {
                if (needAndJson) {
                    strBuilderWithoutJson.append("&");
                }

                strBuilderWithoutJson.append(entry.getKey().trim()).append("=").append(entry.getValue().trim());
                needAndJson = true;
            }

            if (needAnd) {
                strBuilder.append("&");
            }

            strBuilder.append(entry.getKey().trim()).append("=").append(entry.getValue().trim());
            needAnd = true;
        }
        String sign = APISignUtils.getSignString(strBuilder.toString(), keySecret);

        strBuilderWithoutJson.append("&sign=").append(sign);

        return strBuilderWithoutJson.toString();
    }
}
