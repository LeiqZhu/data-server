package main;

import io.netty.handler.codec.http.FullHttpResponse;
import main.db.Dbsql.CarbonSql;
import main.db.Dbsql.AirConSql;
import main.db.Dbsql.*;
import server.RequestHandler;
import util.Convert;
import util.JdbcUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationServer{

    public static class UpdateStatHandler extends RequestHandler{
        @Override
        public void post() {
            Map<String, Object> reqMap = this.getPostAttrs();
            operate(reqMap);

            write(sendMap);
        }

        public void operate(Map<String, Object> reqMap){
            int device = Convert.toInt(reqMap.get("device"));

            try {
                switch (device){
                    case 1:
                        JdbcUtil.jdbcUtil.insert(AirConSql.insertSql,AirConSql.evlParam(reqMap));
                        break;
                    case 2:
                        JdbcUtil.jdbcUtil.insert(CarbonSql.insertSql,CarbonSql.evlParam(reqMap));
                        break;
                    case 3:
                        JdbcUtil.jdbcUtil.insert(LightSensorSql.insertSql,LightSensorSql.evlParam(reqMap));
                        break;
                    case 4:
                        JdbcUtil.jdbcUtil.insert(MethanalSensorSql.insertSql,MethanalSensorSql.evlParam(reqMap));
                        break;
                    case 5:
                        JdbcUtil.jdbcUtil.insert(OutletSql.insertSql,OutletSql.evlParam(reqMap));
                        break;
                    case 6:
                        JdbcUtil.jdbcUtil.insert(SwitchSql.insertSql,SwitchSql.evlParam(reqMap));
                        break;
                    case 7:
                        JdbcUtil.jdbcUtil.insert(ThSensorSql.insertSql,ThSensorSql.evlParam(reqMap));
                        break;
                    default:
                            break;
                }
                sendMap.put("msg","");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void addServerHeader(final FullHttpResponse response) {
            response.headers().set("Access-Control-Allow-Origin", "*");
            response.headers().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.headers().set("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
        }
    }

    public static class GetStatHandler extends RequestHandler{
        @Override
        public void post() {
            Map<String, Object> reqMap = this.getPostAttrs();
            operate(reqMap);

            write(sendMap);
        }

        public void operate(Map<String, Object> reqMap){
            int device = Convert.toInt(reqMap.get("device"));

            Map<String,Object> resultMap = new HashMap<>();
            try {
                switch (device){
                    case 1:
                        resultMap = JdbcUtil.jdbcUtil.queryOne(AirConSql.querySql,new Object[]{});
                        break;
                    case 2:
                        resultMap = JdbcUtil.jdbcUtil.queryOne(SwitchSql.querySql,new Object[]{});
                        break;
                    default:
                        break;
                }
                sendMap.putAll(resultMap);
                sendMap.put("msg","");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void addServerHeader(final FullHttpResponse response) {
            response.headers().set("Access-Control-Allow-Origin", "*");
            response.headers().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.headers().set("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
        }
    }

    public static class AllStatHandler extends RequestHandler{
        @Override
        public void post() {
            Map<String, Object> reqMap = this.getPostAttrs();
            operate(reqMap);

            write(sendMap);
        }

        public void operate(Map<String, Object> reqMap){
            String tableName = checkDevice(reqMap);
            String start = Convert.toString(reqMap.get("start"));
            String end = Convert.toString(reqMap.get("end"));
            String sqlFormat = "SELECT * FROM %s WHERE time between '%s' AND '%s' ORDER BY time ASC";
            String sql = String.format(sqlFormat,tableName,start,end);
            System.out.println(sql);

            List<Map<String,Object>> resultMapList = new ArrayList<>();
            try {
                resultMapList = JdbcUtil.jdbcUtil.getAllMapList(sql);
                sendMap.put("list",resultMapList);
                sendMap.put("msg","");
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void addServerHeader(final FullHttpResponse response) {
            response.headers().set("Access-Control-Allow-Origin", "*");
            response.headers().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.headers().set("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
        }
    }

    public static String checkDevice(Map<String, Object> reqMap){
        int device = Convert.toInt(reqMap.get("device"));
        String tableName = "";
        switch (device){
            case 1:
                tableName = "aircon_status";
                break;
            case 2:
                tableName = "carbon_sensor";
                break;
            case 3:
                tableName = "light_sensor";
                break;
            case 4:
                tableName = "methanal_sensor";
                break;
            case 5:
                tableName = "outlet";
                break;
            case 6:
                tableName = "switch";
                break;
            case 7:
                tableName = "th_sensor";
                break;
            default:
                break;
        }
        return tableName;
    }
}
