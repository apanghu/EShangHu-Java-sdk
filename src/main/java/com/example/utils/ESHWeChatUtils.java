package com.example.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Jesse-liu
 * @description: 易商户小微支付工具类
 * @date 2019/8/8 12:18
 */
public class ESHWeChatUtils {

    /**
     * 生成sign签名 (小微商户)
     */
    public static String createSign(String characterEncoding, Map<String, Object> parameters, String appSecret) {
        StringBuffer sbkey = new StringBuffer();
        Set es = parameters.entrySet();  //所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            //空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sbkey.append(k + "=" + v);
            }

            if (it.hasNext()) {
                sbkey.append("&");
            }
        }

        sbkey = sbkey.append(appSecret);
        String sign = MD5Utils.md5Encrypt32Upper(sbkey.toString(), characterEncoding);
        return sign;
    }

    /**
     * map转string
     */
    public static String map2string(Map<String, Object> param) {
        StringBuilder parameterBuffer = new StringBuilder();
        if (param != null) {
            Iterator iterator = param.keySet().iterator();
            String key;
            String value;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (param.get(key) != null) {
                    value = "" + param.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }


}
