package com.exploration;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: JavaMethodTest
 * @Author: leisure
 * @CreateDate: 2021/2/23 15:22
 * @Description:
 */
public class JavaMethodTest {

    @Test
    public void streamTraversalMapTest(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("apple",96);
        map.put("123456",false);
        map.put("Hello","Kitty");
        System.out.println("遍历map==========>");
        map.entrySet().stream().forEach(mEntry-> System.out.println(mEntry.getKey()+"---"+mEntry.getValue()));
        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
        map.entrySet().stream().forEach(mEntry->pairList.add(new BasicNameValuePair(mEntry.getKey(),String.valueOf(mEntry.getValue()))));
        System.out.println("遍历pairList==========>");
        pairList.stream().forEach(basicNameValuePair -> System.out.println(basicNameValuePair.getName()+"---"+basicNameValuePair.getValue()));
        Map<String,Object> map2 = new HashMap<String,Object>();
        System.out.println("遍历map2==========>");
        pairList.stream().forEach(basicNameValuePair -> map2.put(basicNameValuePair.getName(),basicNameValuePair.getValue()));
        map2.entrySet().stream().forEach(mEntry-> System.out.println(mEntry.getKey()+"---"+mEntry.getValue()));

    }
}
