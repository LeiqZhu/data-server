package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final int ONE_DAY_MILLS = 1000 * 24 * 60 * 60;

    public static Date getAfter(int day) {

        return getDateAfter(new Date(), day);
    }

    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 获取今天日期的字符串
     * 
     * @return
     */
    public static String today() {
        return DateFormats.formatD(new Date());
    }

    /**
     * 获取当前时间加上 addTime后的时间
     * addTime 单位milliseconds
     * 
     * @return
     */
    public static String day(long curTime, long addTime) {
        return DateFormats.formatD(new Date(curTime + addTime));
    }

    /**
     * 获取今天日期的字符串
     * 
     * @return
     */
    public static String today(long curTime) {
        return DateFormats.formatD(new Date(curTime));
    }

    public static String todayDDMM(long curTime) {
        return DateFormats.format(new Date(curTime), DateFormats.MMDD_PATTERN);
    }

    /**
     * 获取昨天日期的字符串
     * 
     * @return
     */
    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return DateFormats.formatD(cal.getTime());
    }

    /**
     * 获取昨天日期的字符串
     * 
     * @return
     */
    public static String yesterday(long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        cal.add(Calendar.DATE, -1);
        return DateFormats.formatD(cal.getTime());
    }

    /**
     * 获取当前日期时间的字符串 yyyy-MM-dd HH:mm:ss
     * 
     * @return
     */
    public static String now() {
        return DateFormats.formatDT(new Date());
    }

    /**
     * 获取当前日期时间的字符串 yyyy-MM-dd HH:mm:ss
     * 
     * @return
     */
    public static String now(long curTime) {
        return DateFormats.formatDT(new Date(curTime));
    }

    /**
     * 获取当前日期时间的GMT字符串
     * 
     * @return
     */
    public static String nowGMT() {
        return DateFormats.formatGMT(new Date());
    }

    /**
     * 计算当前时间离下次零点还剩余的秒数
     * 
     * @param hour
     *            小时
     * @param minute
     *            分钟
     * @return 剩余的秒数
     */
    public static int calcNext00Diff() {
        return calcNext00Diff(System.currentTimeMillis());
    }

    /**
     * 计算当前时间离下次零点还剩余的秒数
     * 
     * @param hour
     *            小时
     * @param minute
     *            分钟
     * @return 剩余的秒数
     */
    public static int calcNext00Diff(long curTime) {
        return calcNextTimeDiff(curTime, 0, 0);
    }

    /**
     * 计算当前时间离下个月1号的零点还剩余的秒数
     * 
     * @param hour
     *            小时
     * @param minute
     *            分钟
     * @return 剩余的秒数
     */
    public static int calcNextMonth00Diff() {

        long curTime = System.currentTimeMillis();

        Calendar nextMonth = Calendar.getInstance();
        nextMonth.setTimeInMillis(curTime);
        nextMonth.set(Calendar.DAY_OF_MONTH, 1);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.SECOND, 0);
        nextMonth.set(Calendar.MINUTE, 0);

        nextMonth.add(Calendar.MONTH, 1);

        return (int) ((nextMonth.getTimeInMillis() - curTime) / 1000);
    }

    public static long getDayOfMonthTimeMillis(long curTime, int day) {

        Calendar nextMonth = Calendar.getInstance();
        nextMonth.setTimeInMillis(curTime);
        nextMonth.set(Calendar.DAY_OF_MONTH, day);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.SECOND, 0);
        nextMonth.set(Calendar.MINUTE, 0);

        return nextMonth.getTimeInMillis();
    }

    public static long getDayTimeMillis(long curTime) {

        Calendar nextMonth = Calendar.getInstance();
        nextMonth.setTimeInMillis(curTime);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.SECOND, 0);
        nextMonth.set(Calendar.MINUTE, 0);
        nextMonth.set(Calendar.MILLISECOND, 0);

        return nextMonth.getTimeInMillis();
    }

    /**
     * 计算当前时间距离上次零点过去了多少秒,相当于今天已经过去多少秒
     * 
     * @return
     */
    public static int calcLast00Diff() {
        return 24 * 60 * 60 - calcNextTimeDiff(System.currentTimeMillis(), 0, 0);
    }

    /**
     * 计算当前时间离下次时间还剩余的秒数
     * 
     * @param hour
     *            小时
     * @param minute
     *            分钟
     * @return 剩余的秒数
     */
    public static int calcNextTimeDiff(int hour, int minute) {
        return calcNextTimeDiff(System.currentTimeMillis(), hour, minute);
    }

    /**
     * 计算输入的时间戳离下次时间还剩余的秒数
     * 
     * @param hour
     *            小时
     * @param minute
     *            分钟
     * @return 剩余的秒数
     */
    public static int calcNextTimeDiff(long curTime, int hour, int minute) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("hour must be 0-23");
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("minute must be 0-59");
        }

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curTime);

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(curTime);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.MILLISECOND, 0);

        int seconds = (int) ((now.getTimeInMillis() - today.getTimeInMillis()) / 1000);

        if (seconds < (hour * 3600) + (minute * 60)) {
            return ((hour * 3600) + (minute * 60)) - seconds;
        }
        else {
            return (24 * 3600 + (hour * 3600)) + (minute * 60) - seconds;
        }
    }

    /**
     * 获得当天0点时间
     * 
     * @return
     */
    public static Date getToday00() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得当天24点时间
     * 
     * @return
     */
    public static Date getToday24() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定时间戳当天0点时间
     * 
     * @return
     */
    public static Date getDateTime00(long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定时间戳当天24点时间
     * 
     * @return
     */
    public static Date getDateTime24(long curTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curTime);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得本周一0点时间
     * 
     * @return
     */
    public static Date getWeekMonday00() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONDAY),
                cal.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获得本周日24点时间
     * 
     * @return
     */
    public static Date getWeekSunday24() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getWeekMonday00());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    /**
     * 获得本月第一天0点时间
     * 
     * @return
     */
    public static Date getMonthFirst00() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONDAY),
                cal.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * 获得本月最后一天24点时间
     * 
     * @return
     */
    public static Date getMonthLast24() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONDAY),
                cal.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    /**
     * 获取日期相差天数
     * 
     * @param
     * @return 日期类型时间
     * @throws ParseException
     */
    public static Long getDiffDay(String beginDate, String endDate) {
        Long checkday = 0l;
        // 开始结束相差天数
        try {
            checkday = (DateFormats.parseD(endDate).getTime()
                        - DateFormats.parseD(beginDate).getTime())
                       / ONE_DAY_MILLS;
        }
        catch (Exception e) {
            e.printStackTrace();
            checkday = null;
        }
        return checkday;
    }

    /**
     * 日期格式为1977-01-01
     * 
     * @param date
     * @return
     */
    public static Long getDiffDayTo1977(String date) {
        Long checkday = 0l;
        // 开始结束相差天数
        try {
            checkday = (DateFormats.parseD(date).getTime()
                        - DateFormats.parseD("1977-01-01").getTime())
                       / ONE_DAY_MILLS;
        }
        catch (Exception e) {
            e.printStackTrace();
            checkday = null;
        }
        return checkday;
    }

    /**
     * 获取更改时区后的日期
     * 
     * @param date
     *            日期
     * @param oldZone
     *            旧时区对象
     * @param newZone
     *            新时区对象
     * @return 日期
     */
    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }

    /**
     * 获取更改时区后的日期
     * 
     * @param date
     *            日期
     * @param newZone
     *            新时区对象
     * @return 日期
     */
    public static Date changeTimeZone(Date date, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = TimeZone.getDefault().getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }

    /**
     * 把秒数转化为小时：分：秒，04:34:45这个格式
     * 
     * @param duration
     *            秒数
     * @return 小时：分：秒，04:34:45
     */
    public static String getDuration(int duration) {
        int hours = duration / (3600);
        int leftSeconds = duration % (3600);
        int minutes = leftSeconds / 60;
        int seconds = leftSeconds % 60;

        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append(addZeroPrefix(hours));
        sBuffer.append(":");
        sBuffer.append(addZeroPrefix(minutes));
        sBuffer.append(":");
        sBuffer.append(addZeroPrefix(seconds));

        return sBuffer.toString();
    }

    /**
     * 小于10的数字补零
     * 
     * @param number
     *            数字
     * @return 补零后的字符串
     */
    public static String addZeroPrefix(int number) {
        if (number < 10) {
            return "0" + number;
        }
        else {
            return "" + number;
        }
    }

    /**
     * 将日期 "yyyy-MM-dd HH:mm:ss"转化成时间戳
     * 
     * @param dateStr
     *            日期例如 "2016-08-18 19:19:19"
     * @return 时间戳 （毫秒）
     */
    public static long dateToTimestamp(String dateStr) {
        long curTime = 0;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            curTime = sdf.parse(dateStr).getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return curTime;
    }

    /**
     * 获取当前小时数
     * 
     * @param date
     * @return
     */
    public static int getCurrentHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentHour(long curTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        return curHour;
    }

    // 获取当前day 2016-10-12 返回12
    public static int getCurrentDay(long curTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当先分钟数
     * 
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取当先分钟数
     * 
     * @param date
     * @return
     */
    public static int getMinute(long curTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取当先小时+分钟 格式为 HH:mm，以半小时为单位，向下取整
     * 
     * @return
     */
    public static String getHalfHourStr() {
        return getHalfHourStr(System.currentTimeMillis());
    }

    /**
     * 获取当先小时+分钟 格式为 HH:mm，以半小时为单位，向下取整
     * 
     * @return
     */
    public static String getHalfHourStr(long curTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(curTime);
        int curMinute = calendar.get(Calendar.MINUTE);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        String timeStr = "";
        if (curHour < 10) {
            timeStr = "0" + curHour;
        }
        else {
            timeStr = "" + curHour;
        }

        if (curMinute >= 30) {
            timeStr = timeStr + ":30";
        }
        else {
            timeStr = timeStr + ":00";
        }
        return timeStr;
    }

    /**
     * 获取当前半小时分钟 格式为 mm，以半小时为单位，向下取整
     * 
     * @return
     */
    public static String getHalfHour(int curMinute) {
        if (curMinute >= 30) {
            return "30";
        }
        return "00";
    }

    /**
     * 获取当前半小时分钟
     * 
     * @return
     */
    public static String getHalfHour(long curTime) {
        Date curDate = new Date(curTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        int curMinute = calendar.get(Calendar.MINUTE);

        String time = DateFormats.format(curDate, "yyyyMMddHH");

        if (curMinute >= 30) {
            return time + "30";
        }
        return time + "00";
    }
}