package com.pcitc.openfire.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;


public class WebServiceCaller {
	public static String callWebService(String address, String param, String contentType) throws IOException {
		PostMethod postMethod = new PostMethod(address); 
		//设置POST方法请求超时
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
        try {            
            byte[] b = param.getBytes("UTF-8");
            InputStream inputStream = new ByteArrayInputStream(b, 0, b.length);
            RequestEntity re = new InputStreamRequestEntity(inputStream, b.length, contentType);
            postMethod.setRequestEntity(re);
            
            HttpClient httpClient = new HttpClient();
            HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams(); 
            // 设置连接超时时间(单位毫秒)
            managerParams.setConnectionTimeout(30000);
            // 设置读超时时间（单位毫秒）
            managerParams.setSoTimeout(600000); 
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode != HttpStatus.SC_OK)  
                throw new IllegalStateException("调用webservice错误 : " + postMethod.getStatusLine()); 
            
            String soapRequestData =  postMethod.getResponseBodyAsString();
            inputStream.close();
            return soapRequestData;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (HttpException e) {
        	throw e;
        } catch (IOException e) {
        	throw e;
        }finally{
             postMethod.releaseConnection(); 
        }
	}
	
	public static String getNamespaceFromSOAPRequest(Document docRequest) {
		XPath xPath = docRequest.createXPath("//"+docRequest.getRootElement().getNamespacePrefix()+":Body/*");
		return ((Element)xPath.selectSingleNode(docRequest)).getNamespaceURI();
	}
}
