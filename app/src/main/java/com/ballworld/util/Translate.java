package com.ballworld.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.PublicKey;


public class Translate {
	private static final String baseUrl = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=RgxDUKl5LEWkm4rzymNFkPrq&";
	public static final String AUTO = "auto";
	public static final String WYW = "wyw";
	/**
	 * main函数 ,for test
	 * @param args
	 */
	public static void main(String[] args){
		String test ="似乎这份情，不再是那么的单纯。彼此之间聊的话题，也不再是"
				+"小时候的那些无忧无虑。现在，各有烦恼。十三年的情，虽然珍贵。\n但，这时光"
				+"抹去的东西，再也找不回来了。 我们再也不可能回到当初。这十三年的情，对于我们而言，"
				+"只能留在昨天。今天的你我都有各自的生活，故必疏多。";
		System.out.printf("%s",translate(test,"auto","wyw"));
	}
	
	/**
	 * 中文  zh  英语  en  日语  jp  韩语  kor 西班牙语  spa  法语  fra  泰语  th  
	 * 阿拉伯语  ara  俄罗斯语  ru  葡萄牙语  pt  粤语  yue  文言文  wyw  白话文  zh  
	 * 自动检测  auto 德语  de  意大利语  it  荷兰语  nl  希腊语  el  
	 * @param src - 要翻译的文字
	 * @param from - 原来的语言类型
	 * @param to - 要翻译成的语言类型
	 * @return - 翻译结果
	 */
	public static String translate(String src,String from,String to) {
		//声明变量
		String httpUrl="";
	    BufferedReader reader = null;
	    String result = "translate error";
	    StringBuffer sbf = new StringBuffer();
	    String returnString = "";
	    
	    //拼凑访问url
	    try {
			src=URLEncoder.encode(src, "utf-8");//转码
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    httpUrl = baseUrl+"q="+src+"&from="+from+"&to="+to;
	
	    try {
	        URL url = new URL(httpUrl);
	        URLConnection connection = (URLConnection) url.openConnection();
	        
	        connection.connect();
	        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = new String(sbf.toString().getBytes("ISO-8859-1"), "UTF-8");  
	        System.out.println(result+"test");
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println(result+"test43sss");
	    }
	    int begin = result.indexOf("\",\"dst\":\"")+9;//the first paragraph
	    int end=begin;
	    while ((end=result.indexOf("\"},{\"src\":\"", end))!=-1) {
			returnString+=result.substring(begin, end)+"\n";
			
			//the next paragraph
			end+=11;
			begin=result.indexOf("\",\"dst\":\"",end)+9;
		}
	    if((end = result.indexOf("\"}]}"))!=-1)
	    	returnString+=result.substring(begin, end);//the last paragraph
	    else
	    	returnString="翻译出错了";
	    return convert(returnString);
	}
	
	/**
	 * 将unicode码转换为汉字
	 * @param utfString - 含有unicode码的字符串
	 * @return
	 */
	public static String convert(String utfString){
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;
		
		while((i=utfString.indexOf("\\u", pos)) != -1){
			sb.append(utfString.substring(pos, i));
			if(i+5 < utfString.length()){
				pos = i+6;
				sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
			}
		}
		if (sb.length()==0) {
			return utfString;
		}
		return sb.toString();
	}
}

