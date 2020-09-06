package com.jason.myapp.utils;

/**
 * Created by sunjianchao on 17/10/9.
 */
public class Constant {

    //vista超时时间
    public static final int VISTA_TIMEOUT = 15 * 1000;

    // 命名空间
    public static String nameSpace = "http://www.dadicinema.com";
    // 调用的方法名称
    public static String methodName = "printTicket";
    // EndPoint
//    private String endPoint = "http://123.57.79.206:8500/KioskWebService";
    // SOAP Action
    public static String soapAction = "http://www.dadicinema.com/" + methodName;
    public static String username = "18600856028";
    public static String password = "";

    /****************************************************************************************************************/

    /**
     http://121.32.27.26:18080/tsp-ws/services/tsp/cinema?wsdl
     影院编码： 62549174
     应用编码：MGKJ
     验证密钥：e10adc3949ba59abbe56e057f20f883e
     */

    /**
     * 晨星票务系统-命名空间
     */
    public static final String ORISTAR_NAME_SPACE = "http://soap.ws.tsp.oristartech.com/";

    /**
     * 晨星票务系统-电影票出票请求
     */
    public static final String ORISTAR_METHOD_APPLY_FETCH_TICKET = "ApplyFetchTicket";

    /**
     * 晨星票务系-电影票信息查询
     */
    public static final String ORISTAR_METHOD_QUERY_TICKET = "QueryTicketInfo";
//    public static String ORISTAR_SOAPACTION_QUERY_TICKET = "http://www.dadicinema.com/" + ORISTAR_METHOD_QUERY_TICKET;

    /**
     * 晨星票务系统-电影票出票结果确认
     */
    public static final String ORISTAR_METHOD_FETCH_TICKET = "FetchTicket";
//    public static String ORISTAR_SOAPACTION_FETCH_TICKET = "http://www.dadicinema.com/" + ORISTAR_METHOD_FETCH_TICKET;

    /**
     * 晨星票务系统-查询电影院放映计划信息
     */
    public static final String ORISTAR_METHOD_QUERY_PLAN_INFO = "QueryPlanInfo";
//    public static String ORISTAR_SOAPACTION_QUERY_PLAN_INFO = "http://www.dadicinema.com/" + ORISTAR_METHOD_QUERY_PLAN_INFO;

    //验证密钥
    public static final String ORISTAR_SECRETKEY = "b6dc76a40eaeb87f8667e1725bd3767c";

    //是否压缩(0：不压缩 1：压缩)
    public static final String ORISTAR_COMPRESS = "0";

    //应用编码
    public static final String ORISTAR_APPCODE = "MGUSER";

    //IP
//    public static final String ORISTAR_IP = "121.32.27.26";

    //端口
//    public static final int ORISTAR_PORT = 18080;


    /****************************************************************************************************************/
    /**
     * 嗨票配置
     */

    //
    public static final String HI_TICKET_URL = "http://test-cine.piaodaren.com/ypcall/admin/tcss/scan/entrance";

    //渠道编号
    public static final String HI_TICKET_PARTNER = "100001";

    //签名密钥
    public static final String HI_TICKET_KEY = "9dba326cb50f95b00ca5fcf5190c310d";

    //安全密钥
    public static final String HI_TICKET_TOKEN = "7045b49983bebb8357f1a2c37246b126cb9ba1d97b2a327cc5f1bccfac8b76a704dfd06fc122f4cb569d9e721b9c9382";
}
