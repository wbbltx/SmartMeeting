package com.newchinese.coolpensdk.utils;

/**
 * Description:   进制转换
 * author         xulei
 * Date           2017/4/25 10:46
 */
public class SystemTransformUtils {
    private static SystemTransformUtils instance;

    private SystemTransformUtils() {

    }
    public static SystemTransformUtils getInstance() {
        if (instance == null) {
            synchronized (SystemTransformUtils.class) {
                if (instance == null) {
                    instance = new SystemTransformUtils();
                }
            }
        }
        return instance;
    }


    public int hexToInt(String strHex){
        int nResult = 0;
        if ( !IsHex(strHex) )
            return nResult;
        String str = strHex.toUpperCase();
        if ( str.length() > 2 ){
            if ( str.charAt(0) == '0' && str.charAt(1) == 'X' ){
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for ( int i=0; i<nLen; ++i ){
            char ch = str.charAt(nLen-i-1);
            try {
                nResult += (GetHex(ch)*GetPower(16, i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //判断是否是16进制数
    private boolean IsHex(String strHex){
        int i = 0;
        if ( strHex.length() > 2 ){
            if ( strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x') ){
                i = 2;
            }
        }
        for ( ; i<strHex.length(); ++i ){
            char ch = strHex.charAt(i);
            if ( (ch>='0' && ch<='9') || (ch>='A' && ch<='F') || (ch>='a' && ch<='f') )
                continue;
            return false;
        }
        return true;
    }

    //计算16进制对应的数值
    private static int GetHex(char ch) throws Exception {
        if ( ch>='0' && ch<='9' )
            return (int)(ch-'0');
        if ( ch>='a' && ch<='f' )
            return (int)(ch-'a'+10);
        if ( ch>='A' && ch<='F' )
            return (int)(ch-'A'+10);
        throw new Exception("error param");
    }

    //计算幂
    private static int GetPower(int nValue, int nCount) throws Exception {
        if ( nCount <0 )
            throw new Exception("nCount can't small than 1!");
        if ( nCount == 0 )
            return 1;
        int nSum = 1;
        for ( int i=0; i<nCount; ++i ){
            nSum = nSum*nValue;
        }
        return nSum;
    }

    //16进制字符串转2进制字符串
    public static String parseHexStrToBinaryStr(String s, int radix) {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        int len = s.length();//确定数据长度
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();//数据变数组
        for (int i = 0; i < len; i++) {
            //数据解析为16进制数，又解析为二进制数据组
            String tempS = Integer.toBinaryString(Character.digit(chars[i], radix));
//            每个二进制数据组的有效位
            int length = tempS.length();
//            每个二进制数据组总共有4位,每一组的高位补0
            for (int j = 0; j < 4 - length; j++) {
                sb.append('0');
            }
            sb.append(tempS);
        }
        return sb.toString();
    }
}
