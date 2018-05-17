
import main.OperationServer.GetStatHandler;
import main.OperationServer.UpdateStatHandler;
import main.OperationServer.AllStatHandler;
import server.HttpServer;
import server.UrlMap;

public class StartServer {

    public static void main(String[] args) throws Exception {
        HttpServer httpServer = new HttpServer();

        //初始化访问路径
        UrlMap.urlMap.put("/stat/update", UpdateStatHandler.class);
        UrlMap.urlMap.put("/stat/get", GetStatHandler.class);
        UrlMap.urlMap.put("/stat/all", AllStatHandler.class);

        httpServer.start();
    }
}
