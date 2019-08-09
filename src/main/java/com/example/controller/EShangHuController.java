package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.config.EShangHuConfig;
import com.example.utils.ESHWeChatUtils;
import com.example.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author Jesse-liu
 * @description: 易商户小微支付控制器
 * @date 2019/8/9 9:24
 */
@Controller
@RequestMapping(value = "")
public class EShangHuController {
    private static Logger logger = LoggerFactory.getLogger(EShangHuController.class);

    public String openid = null;

    /**
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 跳转到主页面
     **/
    @RequestMapping("")
    public String home() {
        return "index";
    }

    /**
     * @param WIDout_trade_no
     * @param WIDsubject
     * @param WIDtotal_fee
     * @param body
     * @param type
     * @param request
     * @param response
     * @param model
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 支付控制函数
     **/
    @RequestMapping(value = "pay")
    public String payApi(@RequestParam(required = false, value = "WIDout_trade_no") String WIDout_trade_no,
                         @RequestParam(required = false, value = "WIDsubject") String WIDsubject,
                         @RequestParam(required = false, value = "WIDtotal_fee") String WIDtotal_fee,
                         @RequestParam(required = false, value = "body") String body,
                         @RequestParam(required = false, value = "type") String type,
                         HttpServletRequest request,
                         HttpServletResponse response, Model model) {

        if ("JSAPI".equals(type) || type == null) {
            return getOpenid();
        }

        //微信支付
        if ("Native".equals(type)) {
            Map<String, Object> parameters = new TreeMap<String, Object>();
            //
            parameters.put("app_key", EShangHuConfig.APP_KEY);
            parameters.put("sub_mch_id", EShangHuConfig.SUB_MCH_ID);
            parameters.put("total_fee", 1 * 100);
            parameters.put("out_trade_no", WIDout_trade_no + UUID.randomUUID().toString().substring(0, 5).replace("-", ""));
            parameters.put("subject", WIDsubject);
            parameters.put("extra", "extra123");
            parameters.put("notify_url", EShangHuConfig.NOTIFY_URL);
            String characterEncoding = "UTF-8";         //指定字符集UTF-8
            parameters.put("sign", ESHWeChatUtils.createSign(characterEncoding, parameters, EShangHuConfig.APP_SECRET));
            try {
                String map2string = ESHWeChatUtils.map2string(parameters);
                String result = HttpClientUtils.send(EShangHuConfig.NATIVE_PAY_API, map2string);
                logger.info("微信下单响应：" + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if ("200".equals(jsonObject.getString("code")) && "success".equals(jsonObject.getString("message"))) {
                    JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
                    logger.info("微信支付下单成功...");
                    model.addAttribute("siteName", "易商户支付测试");
                    model.addAttribute("title", "易商户支付测试");
                    model.addAttribute("total_fee", Integer.parseInt(parameters.get("total_fee").toString()) / 100);
                    model.addAttribute("subject", parameters.get("subject").toString());
                    model.addAttribute("outTradeNo", parameters.get("out_trade_no").toString());
                    model.addAttribute("createDate", new Date());
                    model.addAttribute("model", dataObject.getString("code_url"));
                    return "eShangHuPay";
                } else {
                    logger.info("微信支付下单失败...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 支付完成跳转地址
     **/
    @RequestMapping("orderPayResult")
    public String orderPayResult() {
        return "paySuccess";
    }


    /**
     * @param outTradeNo 商户订单号
     * @return : java.util.Map<java.lang.Object,java.lang.Object>
     * @author Jesse-liu
     * @date 2019/8/1
     * @description: 检查微信支付的付款结果 (因为和业务相关所以没做支付后跳转）
     **/
    @RequestMapping("wx_payOrder_state")
    @ResponseBody
    public Map<Object, Object> wx_order_state(String outTradeNo) {
        HashMap<Object, Object> map = null;
        //      map.put("status", "success");
        map.put("backurl", "/orderPayResult");
        return map;
    }

    /**
     * @return : null
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 获取openid
     **/
    public String getOpenid() {
        String url = "https://1shanghu.com/v2/wechat/login?app_key=" + EShangHuConfig.APP_KEY + "&sub_mch_id=" + EShangHuConfig.SUB_MCH_ID + "&callback=" + EShangHuConfig.CALL_BACK;
        return "redirect:" + url;
    }

    /**
     * @return : null
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 获取openid回调函数
     **/
    @RequestMapping("optionCallBack")
    public String optionCallBack(HttpServletRequest request, HttpServletResponse response, Model model) {
        logger.info(request.getParameter("openid"));
        if (request.getParameter("openid") != null && request.getParameter("openid") != "") {
            openid = request.getParameter("openid");
        }

        Map<String, Object> parameters = new TreeMap<String, Object>();
        //
        parameters.put("app_key", EShangHuConfig.APP_KEY);
        parameters.put("sub_mch_id", EShangHuConfig.SUB_MCH_ID);
        parameters.put("openid", openid);
        parameters.put("total_fee", 1 * 100);
        parameters.put("out_trade_no", UUID.randomUUID().toString().substring(0, 10).replace("-", ""));
        parameters.put("subject", "易商户小微支付测试");
        parameters.put("notify_url", EShangHuConfig.NOTIFY_URL);
        String characterEncoding = "UTF-8";         //指定字符集UTF-8
        parameters.put("sign", ESHWeChatUtils.createSign(characterEncoding, parameters, EShangHuConfig.APP_SECRET));
        try {
            //把map转string  发送的信息
            String map2string = ESHWeChatUtils.map2string(parameters);
            String result = HttpClientUtils.send(EShangHuConfig.JSAPI_PAY_API, map2string);
            logger.info("微信下单响应：" + result);

            JSONObject jsonObject = JSONObject.parseObject(result);
            if ("200".equals(jsonObject.getString("code")) && "success".equals(jsonObject.getString("message"))) {
                JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
                logger.info("微信支付下单成功...");
                model.addAttribute("jsapi_app_id", dataObject.getString("jsapi_app_id"));
                model.addAttribute("jsapi_timeStamp", dataObject.getString("jsapi_timeStamp"));
                model.addAttribute("jsapi_nonceStr", dataObject.getString("jsapi_nonceStr"));
                model.addAttribute("jsapi_package", dataObject.getString("jsapi_package"));
                model.addAttribute("jsapi_signType", dataObject.getString("jsapi_signType"));
                model.addAttribute("jsapi_paySign", dataObject.getString("jsapi_paySign"));
                return "eShangHuPayWap";
            } else {
                logger.info("微信支付下单失败...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return : null
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 去。。。
     **/
    @RequestMapping("toAPIInterface")
    public String toRefund() {
        return "APIInterface";
    }

    /**
     * @param out_trade_no 用户自定义订单编号
     * @param order_sn     易商户平台订单号
     * @param refund_fee   退款金额
     * @param model
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 退款接口
     **/
    @RequestMapping("refund")
    public String refund(String out_trade_no, String order_sn, String refund_fee, Model model) {

        Map<String, Object> parameters = new TreeMap<String, Object>();
        //
        parameters.put("app_key", EShangHuConfig.APP_KEY);
        parameters.put("sub_mch_id", EShangHuConfig.SUB_MCH_ID);
        parameters.put("refund_fee", refund_fee);
        if (out_trade_no != null && out_trade_no != "") {
            parameters.put("out_trade_no", out_trade_no);
        }
        if (order_sn != null && order_sn != "") {
            parameters.put("order_sn", order_sn);
        }
        String characterEncoding = "UTF-8";         //指定字符集UTF-8
        parameters.put("sign", ESHWeChatUtils.createSign(characterEncoding, parameters, EShangHuConfig.APP_SECRET));
        //把map转string  发送的信息
        String map2string = ESHWeChatUtils.map2string(parameters);
        String result = HttpClientUtils.send(EShangHuConfig.Refund_url, map2string);
        logger.info("微信退款响应：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("200".equals(jsonObject.getString("code")) && "success".equals(jsonObject.getString("message"))) {
            JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
            if (dataObject.getString("status_text").equals("success")) {
                logger.info("微信退款成功...");
                model.addAttribute("status_text", "微信退款成功");
            }
        } else {
            model.addAttribute("status_text", jsonObject.getString("message"));
        }
        model.addAttribute("refund_fee", refund_fee);
        model.addAttribute("order_sn", order_sn);
        model.addAttribute("out_trade_no", out_trade_no);
        return "refund";
    }

    /**
     * @param out_trade_no 用户自定义订单编号
     * @param order_sn     易商户平台订单号
     * @param refund_fee   退款金额
     * @param model
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2019/8/9
     * @description: 关闭接口
     **/
    @RequestMapping("close")
    public String close(String out_trade_no, String order_sn, String refund_fee, Model model) {

        Map<String, Object> parameters = new TreeMap<String, Object>();
        parameters.put("app_key", EShangHuConfig.APP_KEY);
        if (out_trade_no != null && out_trade_no != "") {
            parameters.put("out_trade_no", out_trade_no);
        }
        if (order_sn != null && order_sn != "") {
            parameters.put("order_sn", order_sn);
        }
        String characterEncoding = "UTF-8";         //指定字符集UTF-8
        parameters.put("sign", ESHWeChatUtils.createSign(characterEncoding, parameters, EShangHuConfig.APP_SECRET));
        //把map转string  发送的信息
        String map2string = ESHWeChatUtils.map2string(parameters);
        String result = HttpClientUtils.send(EShangHuConfig.close, map2string);
        logger.info("微信关闭订单响应：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("200".equals(jsonObject.getString("code")) && "success".equals(jsonObject.getString("message"))) {
            JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
            if (dataObject.getString("status_text").equals("已取消")) {
                logger.info("微信关闭订单成功...");
                model.addAttribute("status_text", "微信关闭订单成功");
            }
        } else {
            model.addAttribute("status_text", jsonObject.getString("message"));
        }
        model.addAttribute("order_sn", order_sn);
        model.addAttribute("out_trade_no", out_trade_no);
        return "refund";
    }


    /**
     * @param out_trade_no 用户自定义订单编号
     * @param order_sn     易商户平台订单号
     * @param refund_fee   退款金额
     * @param model
     * @return : java.lang.String
     * @author Jesse-liu
     * @date 2019/8/9
     * @description:查询接口
     **/
    @RequestMapping("query")
    public String query(String out_trade_no, String order_sn, String refund_fee, Model model) {

        Map<String, Object> parameters = new TreeMap<String, Object>();
        parameters.put("app_key", EShangHuConfig.APP_KEY);
        if (out_trade_no != null && out_trade_no != "") {
            parameters.put("out_trade_no", out_trade_no);
        }
        if (order_sn != null && order_sn != "") {
            parameters.put("order_sn", order_sn);
        }
        String characterEncoding = "UTF-8";         //指定字符集UTF-8
        parameters.put("sign", ESHWeChatUtils.createSign(characterEncoding, parameters, EShangHuConfig.APP_SECRET));
        //把map转string  发送的信息
        String map2string = ESHWeChatUtils.map2string(parameters);
        String result = HttpClientUtils.send(EShangHuConfig.query, map2string);
        logger.info("微信订单交易状态响应：" + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("200".equals(jsonObject.getString("code")) && "success".equals(jsonObject.getString("message"))) {
            JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
            logger.info("微信订单查询成功...");
            model.addAttribute("status_text", dataObject.getString("status"));
        } else {
            model.addAttribute("status_text", jsonObject.getString("message"));
        }
        model.addAttribute("order_sn", order_sn);
        model.addAttribute("out_trade_no", out_trade_no);
        return "refund";
    }
}
