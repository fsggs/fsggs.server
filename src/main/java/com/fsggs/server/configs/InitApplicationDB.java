package com.fsggs.server.configs;

import com.fsggs.server.Application;
import com.fsggs.server.entities.auth.Authority;
import com.fsggs.server.entities.auth.User;
import com.fsggs.server.entities.master.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class InitApplicationDB {
    private Application application;
    private Configuration config = new Configuration();

    private final SessionFactory sessionFactory;

    public InitApplicationDB(Application application) {
        this.application = application;
        sessionFactory = buildSessionFactory();
    }

    private SessionFactory buildSessionFactory() {
        try {
            Configuration config = getConfiguration();
            return config.buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public void shutdown() {
        sessionFactory.close();
    }

    private Configuration getConfiguration() {
        registerEntity();
        //config.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
        config.setProperty("hibernate.connection.driver_class", application.serverConfig.get("db_driver"));
        config.setProperty("hibernate.connection.url", application.serverConfig.get("db_url"));
        config.setProperty("hibernate.connection.username", application.serverConfig.get("db_user"));
        config.setProperty("hibernate.connection.password", application.serverConfig.get("db_password"));
        config.setProperty("hibernate.connection.pool_size", application.serverConfig.get("db_pool_size"));
        config.setProperty("hibernate.show_sql", application.serverConfig.get("db_show_sql"));
        config.setProperty("hibernate.dialect", application.serverConfig.get("db_dialect"));
        config.setProperty("hibernate.hbm2ddl.auto", application.serverConfig.get("db_hbm2ddl_auto"));
        config.setProperty("hibernate.current_session_context_class", "thread");

        // C3P0
        config.setProperty("hibernate.c3p0.min_size", "10");
        config.setProperty("hibernate.c3p0.max_size", "20");
        config.setProperty("hibernate.c3p0.acquire_increment", "1");
        config.setProperty("hibernate.c3p0.idle_test_period", "3000");
        config.setProperty("hibernate.c3p0.max_statements", "50");
        config.setProperty("hibernate.c3p0.timeout", "1800");
        return config;
    }

    private void registerEntity() {
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Authority.class);
        config.addAnnotatedClass(Server.class);
    }
}
