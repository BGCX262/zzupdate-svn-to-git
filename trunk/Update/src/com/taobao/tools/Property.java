
package com.taobao.tools;


/**
 * @author Liupeng
 * @version CreateTime:2010-11-16
 */
public class Property {

    public static String java_path = "";
    public static String win_path = "";
    static {
        win_path = System.getProperty("user.dir");
        java_path = win_path.replaceAll("\\\\", "/");
    }
}
