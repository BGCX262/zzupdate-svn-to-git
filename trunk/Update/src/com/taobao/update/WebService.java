
package com.taobao.update;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class WebService {
    public String getVersion() {
        String version = null;
        try {
            String endpoint = "http://shua.zhuzhuxc.com/soft/SoftService.asmx";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(new QName("http://shua.zhuzhuxc.com/", "Version"));
            call.setSOAPActionURI("http://shua.zhuzhuxc.com/Version");
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            version = call.invoke(new Object[] {}).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public String getMaintenanceMessage() {
        String msg = null;
        try {
            String endpoint = "http://shua.zhuzhuxc.com/soft/SoftService.asmx";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(new QName("http://shua.zhuzhuxc.com/", "MaintenanceMessage"));
            call.setSOAPActionURI("http://shua.zhuzhuxc.com/MaintenanceMessage");
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            msg = call.invoke(new Object[] {}).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String getPeriod(String name) {
        String period = null;
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL("http://shua.zhuzhuxc.com/soft/SoftService.asmx"));
            call.setOperationName(new QName("http://shua.zhuzhuxc.com/", "Period"));
            call.setSOAPActionURI("http://shua.zhuzhuxc.com/Period");
            call.addParameter("ASn", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("AUserName", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            period = (String) call.invoke(new Object[] {"2735e811f859e1293fb45e99ce3dbdd6", name});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return period;
    }

    public String getPageHomeUrl() {
        String url = null;
        try {
            String endpoint = "http://shua.zhuzhuxc.com/soft/SoftService.asmx";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(new QName("http://shua.zhuzhuxc.com/", "PageHomeUrl"));
            call.setSOAPActionURI("http://shua.zhuzhuxc.com/PageHomeUrl");
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            url = call.invoke(new Object[] {}).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static void main(String[] args) {
        System.out.println(new WebService().getVersion());
        System.out.println(new WebService().getMaintenanceMessage());
        System.out.println(new WebService().getPeriod("caicai_vip"));
        System.out.println(new WebService().getPageHomeUrl());
    }

}
