package server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.Version;
import org.slf4j.LoggerFactory;
import util.DateUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * A HTTP server showing how to use the HTTP multipart package for file uploads.
 */
public class HttpServer {

    public volatile CharSequence curDateTime = DateUtil.nowGMT();
    public final CharSequence SERVER_NAME = String.format("Netty/%s (%s)",
                                                          Version.identify()
                                                                 .get("netty-common")
                                                                 .artifactVersion(),
                                                          System.getProperties()
                                                                .getProperty("os.name"));

    private final NettyConfig config;

    static {
        try {
            initLogbackConf();
        }
        catch (JoranException e) {
            e.printStackTrace();
        }
    }

    private static void initLogbackConf() throws JoranException {
        String xmlPath = System.getProperty("user.dir") + "/../logback.xml";

        File file = new File(xmlPath);
        if (!file.exists()) {
            xmlPath = System.getProperty("user.dir") + "/logback.xml";
        }

        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        context.reset();
        configurator.doConfigure(xmlPath);
    }

    public HttpServer() {
        this((new NettyConfig("netty.js")));
    }

    public HttpServer(NettyConfig config) {
        this.config = config;

        ServerContext.httpServer = this;
    }

    public HttpServer(int port) {
        this();

        if (port > 0) {
            this.getConfig().port = port;
        }
    }

    public void start() throws Exception {

        initNettyServer();
    }

    private void initNettyServer() throws Exception {

        // Configure SSL.
        final SslContext sslCtx;
        if (this.getConfig().isSSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        final EventLoopGroup bossGroup;
        final EventLoopGroup workerGroup;
        Class<? extends ServerChannel> serverChannelClass = null;
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(this.getConfig().ioBossNum);
            workerGroup = new EpollEventLoopGroup(this.getConfig().ioWorkerNum);
            serverChannelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(this.getConfig().ioBossNum);
            workerGroup = new NioEventLoopGroup(this.getConfig().ioWorkerNum);
            serverChannelClass = NioServerSocketChannel.class;
        }
        boolean isException = false;
        try {
            Runtime runTime = Runtime.getRuntime();
            runTime.addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ShutdownHook begin...");
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();

                    // HttpServer运行过程中会生成多个异步的Task，shutdown之后最好等待还在执行的Task安全退出，建议30s。
                    System.out.println("ShutdownHook sleep 20s. wait...");
                    try {
                        TimeUnit.SECONDS.sleep(20);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("ShutdownHook end...");
                }
            }));

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(serverChannelClass)
             .option(ChannelOption.SO_BACKLOG, this.getConfig().backlog)
             .option(ChannelOption.SO_RCVBUF, this.getConfig().revbuf)
             .option(ChannelOption.SO_SNDBUF, this.getConfig().sndbuf)
             .option(ChannelOption.SO_KEEPALIVE, this.getConfig().keepAlive)
             .option(ChannelOption.SO_REUSEADDR, this.getConfig().reuseAddr)
             .option(ChannelOption.TCP_NODELAY, this.getConfig().tcpNoDelay)
             // .option(EpollChannelOption.SO_REUSEPORT,
             // this.reuse_port)
             .option(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
             .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
             .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)

             .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
             .childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
             .childOption(ChannelOption.SO_KEEPALIVE, this.getConfig().keepAlive)
             .childOption(ChannelOption.SO_REUSEADDR, this.getConfig().reuseAddr)
             .childOption(ChannelOption.TCP_NODELAY, this.getConfig().tcpNoDelay)
             .childOption(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
             // .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,
             // this.connect_timeout)
             // .childOption(ChannelOption.SO_TIMEOUT, this.so_timeout)
             .childHandler(new HttpServerInitializer(sslCtx, this));

            // bossGroup.next().scheduleWithFixedDelay(new Runnable() {
            // @Override
            // public void run() {
            // try {
            // curDateTime = DateUtil.nowGMT();
            // } catch (Exception e) {
            // System.out.println("bossGroup.next().scheduleWithFixedDelay error:"
            // + e);
            // }
            // }
            // }, 1000, 1000, TimeUnit.MILLISECONDS);

            Channel ch = b.bind(this.getConfig().port).sync().channel();

            System.out.println(HttpServer.class.getSimpleName()
                               + " started and listen on : "
                               + ch.localAddress());

            ch.closeFuture().sync();
        }
        catch (Exception e) {
            isException = true;
            System.out.println("started exception : " + e);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            if (isException) {
                System.exit(1);
            }
        }
    }

    public void stop() throws Exception {}

    public NettyConfig getConfig() {
        return config;
    }
}