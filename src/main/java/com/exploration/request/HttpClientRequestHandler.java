package com.exploration.request;

import com.exploration.utils.request.ResponseRet;
import com.exploration.utils.request.ResponseType;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.*;

/**
 * @ClassName: HttpClientRequestHandler
 * @Author: leisure
 * @CreateDate: 2021/2/23 11:22
 * @Description:
 */
public class HttpClientRequestHandler {
    private HttpClient httpClient;
    private HttpClientContext context;
    private HttpUriRequest requestMethod;
    private Map<String, ResponseRet> resonseRetMap;
    private Map<String, Map<String, Object>> parseMap;

    private static final Logger log = LoggerFactory.getLogger(HttpClientRequestHandler.class);
    public static final String FILE_PATH = "D:/testFolder/simulateLogin/loginJD/";
    // FileUtils.getTempDirectory().getParent()+"\\"+APPLICATION_NAME+"\\";

    //public static final String FILE_PATH = FileUtils.getTempDirectory().getParent()+"\\";


    /**
     * 默认构造方法
     */
    public HttpClientRequestHandler() {
        HttpClientBuilder hcb = HttpClients.custom();
        CloseableHttpClient httpClient = hcb.build();
        // context
        HttpClientContext context = generateHttpClientContext();
        this.httpClient = httpClient;
        this.context = context;
        this.requestMethod = null;
        this.resonseRetMap = new HashMap<String, ResponseRet>();
        this.parseMap = new HashMap<String,Map<String,Object>>();
    }

    /**
     * 指定代理服务器proxyHost
     * @param proxyHost 代理服务器
     */
    public HttpClientRequestHandler(HttpHost proxyHost) {
        HttpClientBuilder hcb = HttpClients.custom();
        CloseableHttpClient httpClient = this.setProxyStrategy(hcb)
                                             .setProxy(proxyHost)
                                             .build();

        // context
        HttpClientContext context = generateHttpClientContext();
        this.httpClient = httpClient;
        this.context = context;
        this.requestMethod = null;
        this.resonseRetMap = new HashMap<String, ResponseRet>();
        this.parseMap = new HashMap<String,Map<String,Object>>();
    }

    /**
     * 生成最基本的HttpClientContext,需要配置CookieStore
     *
     * @return
     */
    public HttpClientContext generateHttpClientContext() {
        HttpClientContext context = HttpClientContext.create();
        addRequestConfig(context);
        addCookieStore(context);

        return context;
    }

    /**
     * 为HttpClientContext添加CookieStore
     *
     * @param context
     * @return
     */
    public HttpClientContext addCookieStore(final HttpClientContext context) {
        CookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        return context;
    }
    public HttpClientContext addRequestConfig(final HttpClientContext context) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                /*.setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(7000)*/
                .build();

        context.setRequestConfig(requestConfig);
        return context;
    }

    /**
     * 设置代理,以JAVA虚拟机做为代理,用于Fiddler抓包
     *
     * @param httpClientBuilder
     * @return
     */
    public HttpClientBuilder setProxyStrategy(HttpClientBuilder httpClientBuilder) {
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        httpClientBuilder.setRoutePlanner(routePlanner);
        return httpClientBuilder;
    }

    /**
     * 将map转换为Header[]
     *
     * @param map
     * @return
     */
    public static Header[] translateMapToHeaderArray(Map<String, String> map) {
        if (null == map || 0 == map.size()) {
            return null;
        }
        Header[] requestHeader = new Header[map.size()];
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Header header = new BasicHeader(entry.getKey(), entry.getValue());
            requestHeader[index] = header;
            index++;
        }

        return requestHeader;
    }

    /**
     * map 转换为 List<BasicNameValuePair>
     * @param map
     * @return
     */
    public static List<BasicNameValuePair> generateNameValuePairs(Map<String, Object> map) {
        if(null==map || 0==map.size()){
            return null;
        }
        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
        map.entrySet().stream().forEach(mEntry->pairList.add(new BasicNameValuePair(mEntry.getKey(),String.valueOf(mEntry.getValue()))));

        return pairList;
    }
    /**
     *
     * @Title: generateListNameValuePairs
     * @Description: 由map生成 表单数据
     * @param map
     * @return List<BasicNameValuePair>
     * @author leisure
     * @date 2018年7月4日下午3:41:59
     */
    public static List<BasicNameValuePair> generateListNameValuePairs(Map<String, String> map) {
        if(null==map || 0==map.size()){
            return null;
        }
        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
        map.entrySet().stream().forEach(mEntry->pairList.add(new BasicNameValuePair(mEntry.getKey(),mEntry.getValue())));
        return pairList;
    }

    public Cookie generateBasicClientCookie(String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name,value);
        cookie.setVersion(0);
        cookie.setDomain("jd.com");
        cookie.setPath("/");
        cookie.setExpiryDate(null);
        //cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "/");
        return cookie;
    }

    /**
     * 初始化一些京东商城特有的Cookie
     * @param requestHandler
     */
    public void initJDSpecialCookies(HttpClientRequestHandler requestHandler) {
		/*requestHeaderParam.put("Cookie",
		 *      "__jda=122270672.15154973380332050715474.1515497338.1515497338.1515497338.1;" +
				"__jdb=122270672.1.15154973380332050715474|1.1515497338; " +
				"__jdc=122270672; " +
				"__jdv=122270672|direct|-|none|-|1515497338036;" +
				"_jrda=1; " +
				"_jrdb=1515497338152; " +
				"__jdu=15154973380332050715474; " +
				"wlfstk_smdl=d49a24c8oeaav8kkrxbdh3o5ftl1r5n5; " +
				"3AB9D23F7A4B3C9B=IRNZB6RPCFBN64NU657CNVAW4BGI3R3UZCEFZ7P2TW233YCKGDITAR7O7LUUT5S46CRDX5T737EDK5RO7F34F5ECD4");*/
        HttpClientContext context = requestHandler.getContext();
        CookieStore cookieStore = context.getCookieStore();
        if(null==cookieStore) {
            cookieStore = new BasicCookieStore();
        }
        cookieStore.addCookie(generateBasicClientCookie("__jda", "122270672.15154973380332050715474.1515497338.1515497338.1515497338.1"));
        cookieStore.addCookie(generateBasicClientCookie("__jdb", "122270672.1.15154973380332050715474|1.1515497338"));
        cookieStore.addCookie(generateBasicClientCookie("__jdc", "122270672"));
        cookieStore.addCookie(generateBasicClientCookie("__jdv", "122270672|direct|-|none|-|1515497338036"));
        cookieStore.addCookie(generateBasicClientCookie("__jdu", "15154973380332050715474"));
        cookieStore.addCookie(generateBasicClientCookie("_jrda", "1"));
        cookieStore.addCookie(generateBasicClientCookie("_jrdb", "1515497338152"));
        cookieStore.addCookie(generateBasicClientCookie("wlfstk_smdl", "d49a24c8oeaav8kkrxbdh3o5ftl1r5n5"));
        cookieStore.addCookie(generateBasicClientCookie("PCSYCityID", "1"));
        cookieStore.addCookie(generateBasicClientCookie("3AB9D23F7A4B3C9B", "IRNZB6RPCFBN64NU657CNVAW4BGI3R3UZCEFZ7P2TW233YCKGDITAR7O7LUUT5S46CRDX5T737EDK5RO7F34F5ECD4"));
        context.setCookieStore(cookieStore);
    }



    /**
     * 处理Response的通用方法,
     *
     * @param requestHandler
     * @param fileNamePrefix
     *            用于保存文件时的文件名前缀
     * @return
     */
    public ResponseRet GetHttpResponse_generalMethod(HttpClientRequestHandler requestHandler, String fileNamePrefix) {
        HttpClient httpClient = requestHandler.getHttpClient();
        HttpClientContext context = requestHandler.getContext();
        HttpUriRequest requestMethod = requestHandler.getRequestMethod();
        ResponseRet responseRet = new ResponseRet();
        outputRequestHeaderAndCookies(requestHandler);
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(requestMethod, context);
            outputResponseHeaderAndCookies(requestHandler, response, responseRet);
            // 请求返回消息体
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                // 消息体的返回类型 Content-Type: text/html; charset=gbk
                // 返回Json类型 Content-Type: application/json
                // 返回图片类型 Content-Type: image/png
                Header contentTypeHeader = response.getFirstHeader("Content-Type");
                String contentValueValue = null;
                if(null!=contentTypeHeader) {
                    contentValueValue = contentTypeHeader.getValue();
                }
                // 返回的消息体类型 默认为文本类型
                String contentType = "hext/html";
                // 返回的消息体编码 默认"GBK"
                String contentEncoding = "UTF-8";
                if(null!=contentValueValue) {
                    int ctLocation = contentValueValue.indexOf(";");
                    int ceLocation = contentValueValue.indexOf("=");
                    // 没有";"
                    if (-1 == ctLocation) {
                        contentType = contentValueValue;
                        // contentEncoding = "gzip";
                    } else {
                        contentType = contentValueValue.substring(0, ctLocation);
                        // 一般来说这种情况少见 ctLocation和ceLocation要么同时为-1要么同时存在
					/*if (-1 == ceLocation) {
						contentEncoding = "gzip";
					} else {
						contentEncoding = contentValueValue.substring(ceLocation + 1);
					}*/
                        if(-1!=ceLocation) {
                            contentEncoding = contentValueValue.substring(ceLocation + 1);
                        }
                    }
                }
                System.out.println("contentType = " + contentType + "----contentEncoding = " + contentEncoding);
                // 存储到硬盘
                // 将entity保持到内存中rushToPurchasePage
                byte[] responseBody = EntityUtils.toByteArray(entity);
                String filePath = FILE_PATH;
                //String filePath = this.logFileDir;
                String fileName = fileNamePrefix + "_" + System.currentTimeMillis();
                // 默认设置
                responseRet.setRetType(ResponseType.HTML);
                responseRet.setContentEncoding(contentEncoding);

                // 返回文本类型
                if (contentType.equalsIgnoreCase("hext/html")) {
                    // responseRet.setRetString(responseBodyString);
                    fileName += ".html";
                } else if (contentType.equalsIgnoreCase("application/json")) {// 返回类型json
                    responseRet.setRetType(ResponseType.JSON);
                    fileName += ".txt";
                } else if (contentType.contains("image")) {// 返回类型Imag  image/png
                    responseRet.setRetType(ResponseType.IMAG);
                    fileName += ".png";
                } else {

                    fileName += ".txt";
                }
                responseRet.setRetContent(responseBody);
                File file = new File(filePath + fileName);
                responseRet.setFilePath(filePath + fileName);
                System.out.println("文件保存路径:"+(filePath + fileName));
                //BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                //FileUtils.writeByteArrayToFile(file, responseBody);

                EntityUtils.consume(entity);
                FileUtils.writeByteArrayToFile(file, responseBody);
                //FileUtils.writeStringToFile(file, new String(responseBody,contentEncoding), contentEncoding);
				/*bos.write(responseBody);
				bos.flush();
				bos.close();*/
                requestHandler.getResonseRetMap().put(fileNamePrefix, responseRet);
            } else {
                System.out.println("entity is null");
            }

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            // 关闭response
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseRet;
    }

    /**
     * 从CookieList中获取指定name的Cookie  name-value pair
     * @param requestHandler
     * @param name
     * @return
     */
    public Map<String,Object> getCookieByName(final HttpClientRequestHandler requestHandler, final String name){
        if(null==requestHandler || null == name) {
            return null;
        }

        List<Cookie> cookieList = requestHandler.getContext().getCookieStore().getCookies();
        Map<String,Object> map = getCookieByName(cookieList,name);
        return map;
    }

    /**
     * 从CookieList中获取指定name的Cookie  name-value pair
     * @param cookieList
     * @param name
     * @return
     */
    public Map<String,Object> getCookieByName(final List<Cookie> cookieList,final String name){
        if(null==cookieList || null == name || 0==cookieList.size() ) {
            return null;
        }
        Map<String,Object> map = new HashMap<String,Object>();

        for(Cookie cookie:cookieList) {
            if(name.equalsIgnoreCase(cookie.getName())) {
                map.put(name, cookie.getValue());
            }

        }
        return map;
    }


    /**
     * 对于有302重定向的返回的请求,该方法用于获取重定向返回的Location
     * @param requestHandler
     * @return 重定向的Location返回值
     */
    public String GetHttpResponse_parseLocationUrl(HttpClientRequestHandler requestHandler) {
        String locationUrl = null;
        HttpClient httpClient = requestHandler.getHttpClient();
        HttpClientContext context = requestHandler.getContext();
        HttpUriRequest requestMethod = requestHandler.getRequestMethod();
        CloseableHttpResponse response = null;
        ResponseRet responseRet = new ResponseRet();
        // 设置不支持重定向
        boolean flag = context.getRequestConfig().isRedirectsEnabled();
        if (flag) {
            resetRedirectConfigure(requestHandler, false);
        }
        outputRequestHeaderAndCookies(requestHandler);
        try {
            response = (CloseableHttpResponse) httpClient.execute(requestMethod, context);
            // 请求返回状态
            StatusLine statusLine = response.getStatusLine();
            outputResponseHeaderAndCookies(requestHandler, response,responseRet);
            // 重定向302
            if (302 == statusLine.getStatusCode()) {
                Header location = response.getFirstHeader("Location");
                if (null != location) {
                    locationUrl = location.getValue();
                    responseRet.setRetType(ResponseType.LOCATION);
                    responseRet.setLocationUrl(locationUrl);
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 恢复重定向设置
        if (flag) {
            resetRedirectConfigure(requestHandler, true);
        }
        return locationUrl;
    }


    /**
     * 保存图片
     * @param requestHandler
     * @param fileDir
     * @param fileNamePrefix
     * @return
     */
    public ResponseRet GetHttpResponse_saveImg(HttpClientRequestHandler requestHandler, String fileDir,String fileNamePrefix,String postfix) {
        HttpClient httpClient = requestHandler.getHttpClient();
        HttpClientContext context = requestHandler.getContext();
        HttpUriRequest requestMethod = requestHandler.getRequestMethod();
        ResponseRet responseRet = new ResponseRet();
        outputRequestHeaderAndCookies(requestHandler);
        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(requestMethod, context);
            outputResponseHeaderAndCookies(requestHandler, response, responseRet);
            // 请求返回消息体
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                // 存储到硬盘
                // 将entity保持到内存中rushToPurchasePage
                byte[] responseBody = EntityUtils.toByteArray(entity);
                String filePath = fileDir!=null?fileDir:FILE_PATH;
                String fileName = fileNamePrefix + "_" + System.currentTimeMillis()+postfix;
                filePath += fileName;
                FileUtils.writeByteArrayToFile(new File(filePath), responseBody);

                EntityUtils.consume(entity);
                responseRet.setFilePath(filePath);
                requestHandler.getResonseRetMap().put(fileNamePrefix, responseRet);
            }
        }catch (ClientProtocolException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // 关闭response
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseRet;
    }


    public void outputRequestHeaderAndCookies(HttpClientRequestHandler requestHandler) {
        HttpUriRequest requestMethod = requestHandler.getRequestMethod();
        HttpClientContext context = requestHandler.getContext();
        // 输出请求方法的Url
        String requestUrl = requestMethod.getURI().toString();
        //System.out.println("输出requset的Headers和Cookies---" + "requestUrl = " + requestUrl);
        log.info("输出requset的Headers和Cookies---" + "requestUrl = " + requestUrl);
        // 输出请求方法中的Headers
        Header[] headers = requestMethod.getAllHeaders();
        if (null != headers) {
            for (int index = 0; index < headers.length; index++) {
                System.out.println("requestHeaders[" + index + "].name = " + headers[index].getName()
                        + "--------requestHeaders[" + index + "].value = " + headers[index].getValue());

				/*log.info("requestHeaders[" + index + "].name = " + headers[index].getName()
						+ "--------requestHeaders[" + index + "].value = " + headers[index].getValue());*/
            }
        }
        // 输出Cookies
        outputCookies(context);
    }
    /**
     * 输出HttpResponse的Header和Cookies
     * @param requestHandler
     * @param response
     * @param responseRet
     * @return StatusCode 200/302等等 后面根据这个返回值做进一步的处理
     */
    public int outputResponseHeaderAndCookies(HttpClientRequestHandler requestHandler,
                                              CloseableHttpResponse response, ResponseRet responseRet) {
        HttpUriRequest requestMethod = requestHandler.getRequestMethod();
        HttpClientContext context = requestHandler.getContext();
        // 输出请求方法的Url
        String requestUrl = requestMethod.getURI().toString();
        //System.out.println("输出response的Headers和Cookies----" + "requestUrl = " + requestUrl);
        log.info("输出response的Headers和Cookies----" + "requestUrl = " + requestUrl);
        // 请求返回状态
        StatusLine statusLine = response.getStatusLine();
        responseRet.setStautsLine(statusLine);
        //statusLine.get
        System.out.println("statusLine = " + statusLine);
        // 输出请求方法中的Headers
        Header[] headers = response.getAllHeaders();
        if (null != headers) {
            responseRet.setResponseHeaders(headers);
            for (int index = 0; index < headers.length; index++) {
				/*System.out.println("responseHeaders[" + index + "].name = " + headers[index].getName()
						+ "--------responseHeaders[" + index + "].value = " + headers[index].getValue());*/

                log.info("responseHeaders[" + index + "].name = " + headers[index].getName()
                        + "--------responseHeaders[" + index + "].value = " + headers[index].getValue());
            }
        }
        // 输出Cookies
        outputCookies(context,responseRet);
        return statusLine.getStatusCode();

    }

    public void outputCookies(HttpClientContext context) {
        if(null==context){
            return;
        }
        List<Cookie> cookieList = context.getCookieStore().getCookies();
        for (int index = 0; index < cookieList.size(); index++) {
            /*
             * System.out.println("cookieList[" + index + "].name = " +
             * cookieList.get(index).getName() + "-----cookieList[" + index + "].value = " +
             * cookieList.get(index).getValue());
             */
            System.out.println("第" + index + "个Cookie:" + cookieList.get(index).toString());
            log.info("第" + index + "个Cookie:" + cookieList.get(index).toString());
        }
    }
    /**
     * 输出Cookie
     * @param context
     * @param responseRet 将Cookie保存到responseRet中
     */
    public void outputCookies(HttpClientContext context,ResponseRet responseRet) {
        if(null==context || null==responseRet){
            return;
        }
        List<Cookie> cookieList = context.getCookieStore().getCookies();

        for (int index = 0; index < cookieList.size(); index++) {
            /*
             * System.out.println("cookieList[" + index + "].name = " +
             * cookieList.get(index).getName() + "-----cookieList[" + index + "].value = " +
             * cookieList.get(index).getValue());
             */
            //System.out.println("第" + index + "个Cookie:" + cookieList.get(index).toString());
            log.info("第" + index + "个Cookie:" + cookieList.get(index).toString());

        }
        responseRet.setCookieList(cookieList);
    }

    /**
     * 根据value值重置"重定向"选择,用于配合获取Location值
     *
     * @param requestHandler
     * @param value
     */
    public HttpClientContext resetRedirectConfigure(final HttpClientRequestHandler requestHandler, final boolean value) {
        if(null==requestHandler || null==requestHandler.getContext()) {
            return null;
        }
        RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(value)
                .setCircularRedirectsAllowed(true).build();

        HttpClientContext context = requestHandler.getContext();
        context.setRequestConfig(requestConfig);
        return context;
    }


    /*********************************************************************************************************************************/
    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClientContext getContext() {
        return context;
    }

    public void setContext(HttpClientContext context) {
        this.context = context;
    }

    public HttpUriRequest getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(HttpUriRequest requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, ResponseRet> getResonseRetMap() {
        return resonseRetMap;
    }

    public void setResonseRetMap(Map<String, ResponseRet> resonseRetMap) {
        this.resonseRetMap = resonseRetMap;
    }

    public Map<String, Map<String, Object>> getParseMap() {
        return parseMap;
    }

    public void setParseMap(Map<String, Map<String, Object>> parseMap) {
        this.parseMap = parseMap;
    }
}
