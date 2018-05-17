package main.db;

import main.bean.AirConStat;
import main.bean.FanStat;
import util.Convert;
import util.DateUtil;
import util.JacksonUtil;

import java.io.IOException;
import java.util.Map;

public class Dbsql {

    public static class AirConSql{

        public static final String querySql = "SELECT * FROM aircon_status ORDER BY time DESC LIMIT 1";
        public static final String insertSql = "INSERT IGNORE INTO aircon_status(stat,module,temp,speed,shake," +
                "timer_on,timer_off,time) VALUES(?,?,?,?,?, ?,?,?)";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {
            AirConStat airConStat = JacksonUtil.jackson.with2CamelCase()
                    .withIgnoreUnknowPro()
                    .withCamel2Lower()
                    .obj2Bean(map,AirConStat.class);

            Object[] params = {
                    airConStat.getStat(),
                    airConStat.getModule(),
                    String.valueOf(airConStat.getTemp()),
                    airConStat.getSpeed(),
                    airConStat.getShake(),

                    airConStat.getTimerOn(),
                    airConStat.getTimerOff(),
                    airConStat.getTime()};
            return params;
        }
    }

    public static class CarbonSql{

        public static final String insertSql = "INSERT IGNORE INTO carbon_sensor(carbon_concentration,time) VALUES(?,?)";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {

            Object[] params = {
                    Convert.toString(map.get("carbon_concentration")),
                    Convert.toString(map.get("time"))};
            return params;
        }
    }

    public static class LightSensorSql{

        public static final String insertSql = "INSERT IGNORE INTO light_sensor(illumination,time) VALUES(?,?)";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {

            Object[] params = {
                    Convert.toString(map.get("illumination")),
                    Convert.toString(map.get("time"))};
            return params;
        }
    }

    public static class MethanalSensorSql{

        public static final String insertSql = "INSERT IGNORE INTO methanal_sensor(methanal_concentration,time) VALUES(?,?)";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {

            Object[] params = {
                    Convert.toString(map.get("methanal_concentration")),
                    Convert.toString(map.get("time"))};
            return params;
        }
    }

    public static class OutletSql{

        public static final String insertSql = "INSERT IGNORE INTO outlet(voltage,electricity,electric_power,electricity_consumption,time) VALUES(?,?,?,?,?)";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {

            Object[] params = {
                    Convert.toString(map.get("voltage")),
                    Convert.toString(map.get("electricity")),
                    Convert.toString(map.get("electric_power")),
                    Convert.toString(map.get("electricity_consumption")),
                    Convert.toString(map.get("time"))};
            return params;
        }
    }

    public static class SwitchSql{

        public static final String insertSql = "INSERT IGNORE INTO switch(stat,voltage,electricity,electric_power,electricity_consumption,time) VALUES(?,?,?,?,?,?)";

        public static final String querySql = "SELECT * FROM switch ORDER BY time DESC LIMIT 1";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {

            Object[] params = {
                    Convert.toString(map.get("stat")),
                    Convert.toString(map.get("voltage")),
                    Convert.toString(map.get("electricity")),
                    Convert.toString(map.get("electric_power")),
                    Convert.toString(map.get("electricity_consumption")),
                    Convert.toString(map.get("time"))};
            return params;
        }
    }

    public static class ThSensorSql{

        public static final String insertSql = "INSERT IGNORE INTO th_sensor(temp,humidity,time) VALUES(?,?,?)";

        public static Object[] evlParam(Map<String,Object> map) throws IOException {

            Object[] params = {
                    Convert.toString(map.get("temp")),
                    Convert.toString(map.get("humidity")),
                    Convert.toString(map.get("time"))};
            return params;
        }
    }
}
