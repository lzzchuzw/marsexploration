package com.exploration.order;

import com.exploration.request.HttpClientRequestHandler;
import com.exploration.request.HttpRequestHeaderGenerator;
import com.exploration.utils.jsoup.JSoupHandler;
import com.exploration.utils.request.ResponseRet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.util.List;

/**
 * @ClassName: JDOrderManager
 * @Author: leisure
 * @CreateDate: 2021/2/23 17:23
 * @Description:
 */
public class JDOrderManager {

    private static final String ORDER_LIST_URL = "https://order.jd.com/center/list.action";

    public void fetchOrderList(HttpClientRequestHandler requestHandler) {
        if(null==requestHandler) {
            return;
        }
        //url
        RequestBuilder requestBuilder = RequestBuilder.get()
                .setUri(ORDER_LIST_URL);
        // 设置访问的Header
        HttpRequestHeaderGenerator.setfetchOrderListHeaders(requestBuilder);
        // 生成访问方法
        HttpUriRequest requestMethod = requestBuilder.build();
        // 保存访问方法
        requestHandler.setRequestMethod(requestMethod);
        // 发送请求
        ResponseRet responseRet = requestHandler.GetHttpResponse_generalMethod(requestHandler, "fetchOrderList");
        //orderIdLinks
        List<String> orderStringList = JSoupHandler.parseOrderIdListFromOrderListPage(responseRet);
        for(int index=0;index<orderStringList.size();index++) {
            System.out.println("index = "+index+"---order = "+orderStringList.get(index));
        }
    }
}
