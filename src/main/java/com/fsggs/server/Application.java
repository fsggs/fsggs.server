package com.fsggs.server;

import com.fsggs.server.configs.InitApplicationConfig;
import com.fsggs.server.configs.InitApplicationDB;
import com.fsggs.server.configs.InitLogoServer;
import com.fsggs.server.core.FrameworkRegistry;
import com.fsggs.server.core.db.DAOFactory;
import com.fsggs.server.core.log.FSGGSLevel;
import com.fsggs.server.server.SocketServerInit;
import com.fsggs.server.services.master.MasterService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

public class Application {
    final static public String APPLICATION_NAME = "FSGGS Server";
    final static public String APPLICATION_VERSION = "0.0.4";
    final static public String APPLICATION_VERSION_ID = "Solar";

    final static public Level FSGGS = FSGGSLevel.FSGGS;
    final static public Logger logger = LogManager.getLogger(Application.class);

    static public SessionFactory db;
    static public DAOFactory dao;
    static public FrameworkRegistry registry;

    static public boolean SSL = System.getProperty("ssl") != null;
    static public int PORT = 32500;
    static public String IP = "0.0.0.0";
    static public String WEBSOCKET_PATH = "";
    static public String PUBLIC_DIR = "public";
    static public String CLIENT_URL = "*";

    static public Map<String, String> serverConfig;
    static public boolean run = false;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private SslContext sslContext = null;

    public Application() {
        new InitApplicationConfig();
        new InitLogoServer();
        db = (new InitApplicationDB(this)).getSessionFactory();
        dao = DAOFactory.getInstance();

        /* Service Hack */
        (new MasterService()).updateMasterServerTimeTask();
    }

    private ChannelFuture start(InetSocketAddress address) throws Exception {
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

    public void stop() {
        stop(true);
    }

    private void stop(boolean customEvent) {
        Application.run = false;
        if (db != null) db.close();
        logger.info("Server shutdown.");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        if (customEvent) System.exit(-1);
    }

    static public void main(String... args) throws Exception {
        final Application server = new Application();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop(false);
            }
        });

        try {
            ChannelFuture future = server.start(new InetSocketAddress(IP, PORT));

            logger.log(FSGGS, "Open your web browser and navigate to " + (SSL ? "https" : "http") + "://" +
                    (Objects.equals(IP, "0.0.0.0") ? "127.0.0.1" : IP) + ":" + PORT);

            Application.run = true;
            MasterService.updateMasterServerTime();
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
