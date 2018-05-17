package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DateUtil;
import util.Pools;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class FullHttpServerHandler extends ChannelInboundHandlerAdapter {
    private final HttpServer server;

    private static Logger log = LoggerFactory.getLogger(FullHttpServerHandler.class);

    private final static ThreadLocal<ThreadPoolExecutor> localPool = new ThreadLocal<>();
    private final static ThreadLocal<Long> localLastTime = new ThreadLocal<>();

    public FullHttpServerHandler(HttpServer svr) {
        this.server = svr;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
         //if (resHeadDefault(ctx, (FullHttpRequest) msg)) {
         //   return;
         //}
        messageReceived(ctx, (FullHttpRequest) msg);
    }

    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) {

        ThreadPoolExecutor pool = localPool.get();
        if (pool == null) {
            final int corNum = this.server.getConfig().ioThreadCoreNum;
            final int maxNum = this.server.getConfig().ioThreadMaxNum;
            pool = new ThreadPoolExecutor(corNum,
                                          maxNum,
                                          600L,
                                          TimeUnit.SECONDS,
                                          new LinkedBlockingQueue<Runnable>(this.server.getConfig().maxWorkerInQueue));
            // pool.allowCoreThreadTimeOut(true);
            localPool.set(pool);
        }

        final long curTime = System.currentTimeMillis();

        try {
            pool.execute(new WorkerThread(curTime, ctx, this.server, msg));
        }
        catch (RejectedExecutionException e1) {
            String str = String.format("HTTP STATUS 429(502) Too Many Requests, queued for {0}, size limit is {1}, port={2}",
                                        pool.getQueue().size(),
                                        this.server.getConfig().maxWorkerInQueue,
                                        this.server.getConfig().port);
            write(ctx, HttpResponseStatus.BAD_GATEWAY, str.getBytes(), true);

            log.error(str + ", Uri={}, pool.execute been rejected: {}",
                      msg.uri(),
                      ExceptionUtils.getFullStackTrace(e1));
            return;
        }

        Long lastTime = localLastTime.get();
        if (this.server.getConfig().poolLogSpan > 0
            && (lastTime == null || curTime - lastTime > this.server.getConfig().poolLogSpan)) {
            localLastTime.set(curTime);
            log.debug("EventLoop WorkerPool: {}", pool.toString());
        }
    }

    public static class WorkerThread implements Runnable {
        private HttpServer server;
        private ChannelHandlerContext ctx;
        private FullHttpRequest httpRequest;
        private Long initTime;

        protected RequestHandler reqHandler;
        protected String requestUri;

        protected final static Logger log = LoggerFactory.getLogger(WorkerThread.class);

        public WorkerThread() {}

        public WorkerThread(long initTime,
                            ChannelHandlerContext ctx,
                            HttpServer server,
                            FullHttpRequest httpRequest) {
            this.initTime = initTime;
            this.setCtx(ctx);
            this.setServer(server);
            this.setHttpRequest(httpRequest);
        }

        @Override
        public void run() {

            try {

                long curTime = System.currentTimeMillis();
                if (curTime - this.initTime > getServer().getConfig().blockTimeout) {
                    final long blockedTime = curTime - this.initTime;
                    reqTimeoutResponse(blockedTime,
                                       getServer().getConfig().blockTimeout,
                                       getServer().getConfig().port);

                    log.error("HTTP STATUS 408 Request Timeout, blocked for {}ms, time limit is {}ms, port={}, Uri={}",
                              blockedTime,
                              getServer().getConfig().blockTimeout,
                              getServer().getConfig().port,
                              getHttpRequest().uri());
                    return;
                }

                String sUri = getHttpRequest().uri();
                int index = sUri.indexOf('?');
                if (index > 0) {
                    sUri = sUri.substring(0, index);
                }

                URI uri = new URI(sUri);
                String path = requestUri = uri.getPath();

                if (log.isInfoEnabled()) {
                    String clientIP = getHttpRequest().headers().get("X-Forwarded-For");
                    if (clientIP == null) {
                        InetSocketAddress insocket = (InetSocketAddress) getCtx().channel()
                                                                                 .remoteAddress();
                        clientIP = insocket.getAddress().getHostAddress();
                    }
                    log.info("ClientIP: {}, ReqUri: {}, ReqMethod: {}, AccessTime: {}",
                             clientIP,
                             requestUri,
                             getHttpRequest().method(),
                             DateUtil.now());
                }

                Class<? extends RequestHandler> cls = null;
                if (UrlMap.urlMap.containsKey(path)) {
                    cls = UrlMap.urlMap.get(path);
                }
                else if (path.contains("/referrer")) {
                    cls = UrlMap.urlMap.get("/referrer");
                }
                else {
                    cls = RequestHandler.class;
                }

                try {

                    if (this.getServer().getConfig().useReqObjectPool) {
                        // reqHandler =
                        // RequestHandlerFactory.getPool().borrowObject(cls);
                        reqHandler = Pools.obtain(cls);
                        if (log.isDebugEnabled()) {
                            log.debug("obtainObject ed: {}", reqHandler);
                        }
                    }
                    else {
                        reqHandler = (RequestHandler) cls.newInstance();
                        if (log.isDebugEnabled()) {
                            log.debug("newInstance ed: {}", reqHandler);
                        }
                    }

                    reqHandler.setServer(this.getServer());
                    reqHandler.setRequest(getHttpRequest());
                    reqHandler.setCtx(getCtx());

                    reqHandler.setRequestPrefix(ServerContext.getLocalRequestPrefix());
                    reqHandler.setCurTime(curTime);

                    // Url Request 计数
                    ServerContext.addUrlRequest(path);

                    // Get请求
                    if (getHttpRequest().method().equals(HttpMethod.GET)) {
                        try {
                            if (log.isDebugEnabled()) {
                                log.debug("Get Req: {}", uri.getPath());
                            }
                            long time1 = curTime;
                            reqHandler.doGet();
                            long time2 = System.currentTimeMillis();

                            if (log.isInfoEnabled()) {
                                log.info("{} Get Time consuming: {}ms", requestUri, (time2 - time1));
                            }
                        }
                        catch (Exception e1) {
                            log.error("Request Handler get error: {}",
                                      ExceptionUtils.getFullStackTrace(e1));
                        }
                        return;
                    }
                    else if (getHttpRequest().method().equals(HttpMethod.POST)) { // Post请求
                        try {
                            long time1 = curTime;
                            reqHandler.doPost();
                            long time2 = System.currentTimeMillis();

                            if (log.isInfoEnabled()) {
                                log.info("{} Post Time consuming: {}ms",
                                         requestUri,
                                         (time2 - time1));
                            }
                        }
                        catch (Exception e1) {
                            log.error("Request Handler post error: {}",
                                      ExceptionUtils.getFullStackTrace(e1));
                        }
                        return;
                    }
                    else {
                        try {
                            if (log.isDebugEnabled()) {
                                log.debug("Get Req: {}", uri.getPath());
                            }
                            long time1 = curTime;
                            reqHandler.doGet();
                            long time2 = System.currentTimeMillis();

                            if (log.isInfoEnabled()) {
                                log.info("{} Default Get Time consuming: {}ms",
                                         requestUri,
                                         (time2 - time1));
                            }
                        }
                        catch (Exception e1) {
                            log.error("Request Handler get error: {}",
                                      ExceptionUtils.getFullStackTrace(e1));
                        }
                        return;
                    }
                }
                catch (Exception e1) {
                    log.error("Request Handler error: {}", ExceptionUtils.getFullStackTrace(e1));
                }

            }
            catch (Exception e1) {
                e1.printStackTrace();
                log.error("Request messageReceived error: Req=" + getHttpRequest(), e1);
            }
            finally {
                ChannelHandlerContext ctx = getCtx();
                if (ctx != null && ctx.channel() != null) {
                    ctx.channel().close();
                }
                reset();
            }
        }

        private void reqTimeoutResponse(long blocked, int soTimeout, int port) {
            String str = String.format("HTTP STATUS 408 Request Timeout, blocked for {0}ms, time limit is {1}ms, port={2}",
                                        blocked,
                                        soTimeout,
                                        port);
            write(getCtx(), HttpResponseStatus.REQUEST_TIMEOUT, str.getBytes(), false);
        }

        public void reset() {

            if (reqHandler != null) {
                if (this.getServer().getConfig().useReqObjectPool) {
                    try {
                        // RequestHandlerFactory.getPool().returnObject(
                        // reqHandler.getClass(), reqHandler);
                        Pools.free(reqHandler);

                    }
                    catch (Exception e1) {
                        log.error("freeObject error: {}", ExceptionUtils.getFullStackTrace(e1));
                    }
                }
                reqHandler = null;
            }

            setCtx(null);

            requestUri = null;

            setServer(null);

            this.initTime = null;

            releaseHttpRequest();
        }

        public HttpServer getServer() {
            return server;
        }

        public void setServer(HttpServer server) {
            this.server = server;
        }

        public ChannelHandlerContext getCtx() {
            return ctx;
        }

        public void setCtx(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public FullHttpRequest getHttpRequest() {
            return httpRequest;
        }

        public void setHttpRequest(FullHttpRequest httpRequest) {
            this.httpRequest = httpRequest;
        }

        public void releaseHttpRequest() {
            if (this.httpRequest != null) {
                ReferenceCountUtil.release(this.httpRequest);
                this.httpRequest = null;
            }
        }
    }

    private static void write(final ChannelHandlerContext ctx,
                              final HttpResponseStatus status,
                              final byte[] data,
                              boolean isEventLoop) {

        final ByteBuf buf;
        if (data == null || data.length == 0) {
            buf = Unpooled.EMPTY_BUFFER;
        }
        else {
            buf = Unpooled.wrappedBuffer(data, 0, data.length);
        }

        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                                      status,
                                                                      buf);

        if (isEventLoop) {
            ctx.channel().writeAndFlush(response);

            ctx.close();
        }
        else {
            // final Channel channel = ctx.channel();
            // // Write the response.
            // final ChannelFuture future = channel.writeAndFlush(response);
            //
            // // Close the connection after the write operation is done if
            // // necessary.
            // future.addListener(ChannelFutureListener.CLOSE);

            ctx.channel().writeAndFlush(response);

            ctx.channel().close();
        }
    }

    private boolean resHeadDefault(ChannelHandlerContext ctx, FullHttpRequest msg) {
        boolean isDef = false;
        if ("/".equals(msg.uri()) && msg.method().equals(HttpMethod.HEAD)) {

            if (log.isDebugEnabled()) {
                log.debug("/Default HEAD: ClientIP: {}, AccessTime: {}",
                          msg.headers().get("X-Forwarded-For"),
                          DateUtil.now());
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

            boolean keepAlive = HttpUtil.isKeepAlive(msg);
            if (!keepAlive) {
                ctx.writeAndFlush(response);
            }
            else {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ctx.writeAndFlush(response);
            }
            isDef = true;
            ctx.close();
        }
        return isDef;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // ctx.flush();
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isErrorEnabled()) {
            log.error("netty handler exceptionCaught: port={}, warn={}",
                      ServerContext.httpServer.getConfig().port,
                      ExceptionUtils.getFullStackTrace(cause));
        }
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

}