package util;



import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Convert {

    /**
     * 根据传入的obj返回 int 类型
     * 
     * @param obj
     * @return
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }

    public static String stringTrim(Object obj){
        return (obj == null) ? "" : obj.toString().trim();
    }

    /**
     * 根据传入的obj返回 int 类型
     *
     * @param obj
     * @param defValue
     *            默认值
     * @return
     */
    public static int toInt(Object obj, int defValue) {
        try {
            String s = stringTrim(obj);
            return (StringUtils.isEmpty(s) ? defValue : Double.valueOf(s).intValue());
        }
        catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 根据传入的obj返回 long 类型
     * 
     * @param obj
     * @return
     */
    public static long toLong(Object obj) {
        return toLong(obj, 0L);
    }

    /**
     * 根据传入的obj返回 long 类型
     * 
     * @param obj
     * @param defValue
     *            默认值
     * @return
     */
    public static long toLong(Object obj, long defValue) {
        try {
            String s = stringTrim(obj);
            return (StringUtils.isEmpty(s) ? defValue : Double.valueOf(s).longValue());
        }
        catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 根据传入的obj返回 float 类型
     * 
     * @param obj
     * @return
     */
    public static float toFloat(Object obj) {
        try {
            String s = stringTrim(obj);
            return StringUtils.isEmpty(s) ? 0.0F : Double.valueOf(s).floatValue();
        }
        catch (Exception e) {
            return 0.0F;
        }
    }

    /**
     * 根据传入的obj返回 double 类型
     * 
     * @param obj
     * @return
     */
    public static double toDouble(Object obj) {
        try {
            String s = stringTrim(obj);
            return StringUtils.isEmpty(s) ? 0.0 : Double.valueOf(s);
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 根据传入的obj返回String如果为null，返回""
     * 
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    /**
     * 根据传入的obj返回String如果为null，返回默认值
     * 
     * @param obj
     * @param defValue
     *            默认值
     * @return
     */
    public static String toString(Object obj, String defValue) {
        return obj == null ? defValue : String.valueOf(obj);
    }

    /**
     * 将对象强转为List<Map<String,Object>>
     * 
     * @param <V>
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> toList(Object obj) {

        return obj == null ? new ArrayList<V>() : (List<V>) obj;
    }

    /**
     * 将对象强转为Map<String,Object>
     * 
     * @param <K>
     * @param <V>
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object obj) {

        return obj == null ? new HashMap<K, V>() : (Map<K, V>) obj;
    }

    public static int getUnsignedByte(byte data) { // 将data字节型数据转换为0~255 (0xFF
                                                   // 即 BYTE)。
        return data & 0x0FF;
    }

    public static int getUnsignedShort(short data) { // 将data字节型数据转换为0~65535
                                                     // (0xFFFF 即 WORD)。
        return data & 0x0FFFF;
    }

    public static long getUnsignedInt(int data) { // 将int数据转换为0~4294967295
                                                  // (0xFFFFFFFF 即 DWORD)。
        return data & 0x0FFFFFFFFL;
    }
}
