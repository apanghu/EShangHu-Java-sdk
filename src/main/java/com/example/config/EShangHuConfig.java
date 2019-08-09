package com.example.config;

/**
 * @author Jesse-liu
 * @description: 易商户小微支付配置
 * @date 2019/8/8 11:04
 */
public class EShangHuConfig {
    //扫码支付api native
    public static final String NATIVE_PAY_API = "https://api.1shanghu.com/api/v2/wechat/native";
    //退款接口
    public static final String Refund_url = "https://api.1shanghu.com/api/v2/wechat/refund";
    //关闭订单接口
    public static final String close = "https://api.1shanghu.com/api/close";
    //查询订单接口
    public static final String query = "https://api.1shanghu.com/api/query";
    // JSAPI支付api
    public static final String JSAPI_PAY_API = "https://api.1shanghu.com/api/v2/wechat/mp";
    //APP_KEY
    public static final String APP_KEY = "";
    //APP_SECRET
    public static final String APP_SECRET = "";
    //小微商户号
    public static final String SUB_MCH_ID = "";

    //支付结果通知地址
    public static final String NOTIFY_URL = "http://26x0p05904.wicp.vip/weChatPayNotify";
    //获取openid的回调接口
    public static final String CALL_BACK = "http://26x0p05904.wicp.vip/optionCallBack";
}
