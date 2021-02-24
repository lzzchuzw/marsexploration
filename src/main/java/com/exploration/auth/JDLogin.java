package com.exploration.auth;

import com.exploration.request.HttpClientRequestHandler;
import com.exploration.request.HttpRequestHeaderGenerator;
import com.exploration.utils.jsoup.JSoupHandler;
import com.exploration.utils.map.MapUtils;
import com.exploration.utils.random.RandomNumberGenerator;
import com.exploration.utils.regex.RegexUtils;
import com.exploration.utils.request.ResponseRet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @ClassName: JDLogin
 * @Author: leisure
 * @CreateDate: 2021/2/23 11:13
 * @Description:
 */
public class JDLogin {


    /**
     * 访问京东登录主页--账号登录部分 获取cookie和登录提交的Form表单的input
     */
    public Map<String, Object> visitJDLoginHomePage(final HttpClientRequestHandler requestHandler) {


        // 设置访问方法GET "https://passport.jd.com/new/login.aspx"
        RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
                .setUri("https://passport.jd.com/new/login.aspx");// 设置访问的Uri
        // 设置访问的Header
        HttpRequestHeaderGenerator.setVisitJDLoginHomePageHeaders(requestBuilder);
        // 生成访问方法
        HttpUriRequest requestMethod = requestBuilder.build();
        // 保存访问方法
        requestHandler.setRequestMethod(requestMethod);
        // 发送请求
        ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "visitJDLoginHomePage");
        // 解析获取form表达里的input标签
        Map<String, Object> visitJDLoginHomePageMap = JSoupHandler.parserResponseRet_visitJDLoginHomePage(responseRet);
        MapUtils.traversalMap(visitJDLoginHomePageMap);
        // 保存到requestHandler中
        requestHandler.getParseMap().put("visitJDLoginHomePageMap", visitJDLoginHomePageMap);
        return visitJDLoginHomePageMap;
    }

    /**
     * 获取jd登录的二维码---首页获取非刷新  刷新jd登录二维码的方法与此略有不同
     * @param requestHandler
     * @param qrCodeImgUrl
     * @return
     */
    public  String fetchJDLoginQrCodImg(final HttpClientRequestHandler requestHandler, String qrCodeImgUrl) {
        if(null==requestHandler || null==qrCodeImgUrl) {
            return null;
        }
        //String qrCodePath = DIRECTORY_PATH;
        // 设置访问方法GET ""
        RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
                .setUri(qrCodeImgUrl);// 设置访问的Uri
        // 设置访问的Header
        HttpRequestHeaderGenerator.setFetchJDLoginQrCodImgHeaders(requestBuilder,qrCodeImgUrl);
        // 生成访问方法
        HttpUriRequest requestMethod = requestBuilder.build();
        // 保存访问方法
        requestHandler.setRequestMethod(requestMethod);
        // 发送请求
        ResponseRet responseRet = requestHandler.GetHttpResponse_saveImg(requestHandler, null,"fetchJDLoginQrCodImg",".png");
        String qrCodePath = responseRet.getFilePath();
        return qrCodePath;
    }

    /**
     * 系统不停检测是否扫描了登录二维码
     * @param requestHandler
     * @param appid
     * @param token cookie中的wlfstk_smdl的值
     * @return
     */
    public String checkQrcode(final HttpClientRequestHandler requestHandler,String appid,String token) {
        if(null == requestHandler || null==appid || null==token) {
            return null;
        }
        String ticket = null;
        //String qrCodePath = DIRECTORY_PATH;
        // 设置访问方法GET "https://qr.m.jd.com/check?callback=jQuery3284247&appid=133&token=vs3ckzs7l6t56t3b2dnn4owttbmbnf3j&_=1539158409365"
        StringBuilder sb = new StringBuilder("https://qr.m.jd.com/check")
                .append("?")
                .append("callback=jQuery")
                .append(RandomNumberGenerator.generateRandomString(7))
                .append("&")
                .append("appid=")
                .append(appid)
                .append("&")
                .append("token=")
                .append(token)
                .append("&")
                .append("_=")
                .append(System.currentTimeMillis());
        String url = new String(sb);
        System.out.println("---checkQrcode---url = "+url);
        RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
                .setUri(url);// 设置访问的Uri
        // 设置访问的Header
        HttpRequestHeaderGenerator.setCheckQrcodeHeaders(requestBuilder);
        // 生成访问方法
        HttpUriRequest requestMethod = requestBuilder.build();
        // 保存访问方法
        requestHandler.setRequestMethod(requestMethod);
        // 发送请求
        ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "checkQrcode");
        if (null != responseRet && null != responseRet.getRetContent()) {
            String s = null;
            try {
                s = new String(responseRet.getRetContent(), responseRet.getContentEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // ticket不存在
            if (s.contains("ticket")) {
                ticket = RegexUtils.findString(s, "(?<=\\: \\\").*?(?=\\\")");
            }
            System.out.println("ticket = "+ticket);
        }

        return ticket;
    }
    /**
     * 验证京东登录二维码被扫描后获取到的ticket值
     * @param requestHandler
     * @param ticket
     * @return
     */
    public String validateQrCodeTicket(final HttpClientRequestHandler requestHandler,String ticket) {
        if(null==requestHandler || null==ticket) {
            return null;
        }
        // 设置访问方法GET "https://passport.jd.com/uc/qrCodeTicketValidation?t=AAEAMM1zzZwCBhSooDKNHgjtIDnXvEvg7IR58pEKGhFdH5uZs2z5Xow14Pn-XzCGx-BjGw"
        String url = "https://passport.jd.com/uc/qrCodeTicketValidation?t="+ticket;
        RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
                .setUri(url);// 设置访问的Uri
        // 设置访问的Header
        HttpRequestHeaderGenerator.setValidateQrCodeTicketHeaders(requestBuilder);
        // 生成访问方法
        HttpUriRequest requestMethod = requestBuilder.build();
        // 保存访问方法
        requestHandler.setRequestMethod(requestMethod);
        // 发送请求
        ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "validateQrCodeTicket");
        String skipUrl = null;
        if (null != responseRet && null != responseRet.getRetContent()) {
            String s = null;
            try {
                s = new String(responseRet.getRetContent(), responseRet.getContentEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // ticket不存在
            if (s.contains("url")) {
                skipUrl = RegexUtils.findString(s, "(?<=\\:\\\").*?(?=\\\")");
            }
        }
        System.out.println("skipUrl = "+skipUrl);
        return skipUrl;
    }

    /**
     * 京东扫码登录成功后的跳转
     * @param requestHandler
     * @param skipUrl
     */
    public void loginSuccessRedirectAfterValidateQrCode(final HttpClientRequestHandler requestHandler,String skipUrl) {
        RequestBuilder requestBuilder = RequestBuilder.get()// Get 方法
                .setUri(skipUrl);// 设置访问的Uri
        // 设置访问的Header
        HttpRequestHeaderGenerator.setLoginSuccessRedirectAfterValidateQrCodeHeaders(requestBuilder);
        // 生成访问方法
        HttpUriRequest requestMethod = requestBuilder.build();
        // 保存访问方法
        requestHandler.setRequestMethod(requestMethod);
        // 发送请求
        ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler,
                "loginSuccessRedirectAfterValidateQrCode");
    }


}
