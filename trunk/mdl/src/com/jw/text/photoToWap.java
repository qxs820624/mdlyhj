package com.jw.text;

public class photoToWap {

	public static final String[] tag1 = {
		":-)",":)",
		":-(",":(",
		":-o",":O",
		":@","//angry",
		":'(","T_T",
	};
	public static final String[] tag2 = {
		":-)",":D ",
		";-)",";) ",
		":-(",":( ",
		":-o",":-O ",
		":-D","^_^ ",
		":-P",":P ",
		":@",":@ ",
		":-S",":S' ",
		":$","#-_-# ",
		"B-)","B-) ",
		":'(","T_T ",
		":-*",":* ",
	};
	
	public static String parseText(String content,boolean isStatus){
		String result = "";
		String[] tag = tag1;
		if(!isStatus)
			tag = tag2;
		if(content !=  null){
			try{
			int len = tag.length/2;
			result = content;
			boolean finished = false;
			int index = 0;
			while(!finished){
				finished = true;
				for(int i = 0; i < len; i ++){
					int offset = result.indexOf(tag[i * 2]);
					if(offset != -1 && offset >= index){
						index = offset + 1;
						result = replaceChars(result,tag[i * 2],tag[i * 2 + 1]);
						System.out.println("result:"+result);
						finished = false;
						break;
					}
				}
			}
			}catch(Exception e){
				System.out.println("parse text error:"+e.toString());
			}
		}
		return result;
	}
	
	 /**
     * 替换特定的字符串
     * @param text 需要处理的字符串
     * @param chars 被替换的字符
     * @param content 替换的字符
     * @return
     */
    public static String replaceChars(String text, String chars, String content){
      String result = text;
      int index = 0;
      if((index = text.indexOf(chars)) > -1){
        result = "";
        if(index > 0)
          result += text.substring(0,index);
          result += content;
          result += text.substring(index + chars.length());
      }
      return result;
    }
}
