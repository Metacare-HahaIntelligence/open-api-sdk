package com.hahacloud.openapi;

/**
 * @author: baibing
 * @date: 2021/11/4
 * @description:
 */
public class Main {

    /**
     * appKey，appSecret请联系客服申请
     * baseUrl分为测试环境与生产环境，各不相同。
     * path为要请求的具体路径
     */
    private static String appKey = "4944378585237021";
    private static String appSecret = "lOuatIdWaJ7cgeA4EXORHMHNfsqQ41QK";
    private static String baseUrl = "请输入哈哈云提供的Open-Api地址";
    private static String path = "请输入具体访问路径,可从开发文档中获取";

    public static void main(String[] args) {
        WsseHttpHeader defaultWsse = WsseHttpHeader.createDefaultWsse(appKey, appSecret);
        System.out.println(defaultWsse.getTokenHeader());
        System.out.println(defaultWsse.getWsseHeader());
    }
}
