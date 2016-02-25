package com.fsggs.server;

import com.fsggs.server.configs.InitApplicationConfig;
import com.fsggs.server.configs.InitApplicationDB;
import com.fsggs.server.configs.InitLogoServer;
import com.fsggs.server.server.SocketServerInit;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

public class Application {
    public static final String APPLICATION_NAME = "FSGGS Server";
    public static final String APPLICATION_VERSION = "0.0.2";
    public static final String APPLICATION_VERSION_ID = "Solar";

    public static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static SessionFactory db;

    static public boolean SSL = System.getProperty("ssl") != null;
    static public int PORT = 32500;
    static public String IP = "0.0.0.0";
    public static String WEBSOCKET_PATH = "";
    public static String PUBLIC_DIR = "public";

    public Map<String, String> serverConfig;
    static public Map<String, Class<?>> networkPackets;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private SslContext sslContext = null;

    public Application() {
        new InitApplicationConfig(this);
        new InitLogoServer(this);
        db = (new InitApplicationDB(this)).getSessionFactory();

//        Session session = Application.db.openSession();
//
//        session.beginTransaction();
//        User user = new User();
//
//        user.setLogin("admin");
//        user.setPassword("admin");
//        user.setRegisterDate(new Date());
//        user.setLoginDate(new Date());
//
//        session.save(user);
//        session.getTransaction().commit();
    }

    public ChannelFuture start(InetSocketAddress address) throws Exception {
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        }

        ServerBootstrap boot = new ServerBootstrap();
        boot.group(workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new SocketServerInit(sslContext));

        return boot.bind(address).syncUninterruptibly();
    }

    protected void stop() {
        db.close();
        logger.info("Server shutdown.");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    static public void main(String... args) throws Exception {
        final Application server = new Application();

        try {
            ChannelFuture future = server.start(new InetSocketAddress(IP, PORT));

            System.out.println("FSGGS: Open your web browser and navigate to " + (SSL ? "https" : "http") + "://" +
                    (Objects.equals(IP, "0.0.0.0") ? "127.0.0.1" : IP) + ":" + PORT + '/');

            future.channel().closeFuture().syncUninterruptibly();
        } catch (BindException e) {
            if (Objects.equals(e.getMessage(), "Address already in use: bind")) {
                logger.error("Port " + PORT + " is already bind. Maybe another application is run at same time.");
            } else e.printStackTrace();
            server.stop();
        } finally {
            server.stop();
        }
    }
}
