package com.imrd.copy.network;

/**
 * Created by jarvis on 13/5/20.
 */
public class ServiceProvider {

    private static String translateUrl = "http://translate.google.com/translate_a/t?client=t&ie=UTF-8&oe=UTF-8&sl=en&tl=zh-TW&text=hello";
    
    public static String getUrl(){
    	return translateUrl;
    }
}
