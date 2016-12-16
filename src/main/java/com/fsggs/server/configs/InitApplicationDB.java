package com.fsggs.server.configs;

import com.fsggs.server.Application;
import com.fsggs.server.models.auth.User;
import com.fsggs.server.models.game.Character;
import com.fsggs.server.models.game.maps.Galaxy;
import com.fsggs.server.models.game.maps.Map;
import com.fsggs.server.models.game.maps.Solar;
import com.fsggs.server.models.game.maps.Universe;
import com.fsggs.server.models.game.objects.GameObject;
import com.fsggs.server.models.master.Server;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class InitApplicationDB {
    private Configuration config = new Configuration();

    private final SessionFactory sessionFactory;

    public InitApplicationDB(Application application) {
        try {
            this.applyMigration();
        } catch (FlywayException e) {
            Application.logger.error(e.getMessage());
            application.stop();
        }

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

    public InitApplicationDB applyMigration() {
        Flyway flyway = new Flyway();
        flyway.setLocations("db/migration/mysql");
        flyway.setTable("system_migrations");
        flyway.setBaselineOnMigrate(true);
        flyway.setSqlMigrationSeparator("_");
        flyway.setSqlMigrationPrefix("m");
        flyway.setRepeatableSqlMigrationPrefix("r");
        flyway.setDataSource(
                Application.serverConfig.get("db_url"),
                Application.serverConfig.get("db_user"),
                Application.serverConfig.get("db_password")
        );
        flyway.migrate();
        return this;
    }

    private Configuration getConfiguration() {
        registerEntity();
        //config.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
        config.setProperty("hibernate.connection.driver_class", Application.serverConfig.get("db_driver"));
        config.setProperty("hibernate.connection.url", Application.serverConfig.get("db_url"));
        config.setProperty("hibernate.connection.username", Application.serverConfig.get("db_user"));
        config.setProperty("hibernate.connection.password", Application.serverConfig.get("db_password"));
        config.setProperty("hibernate.connection.pool_size", Application.serverConfig.get("db_pool_size"));
        config.setProperty("hibernate.show_sql", Application.serverConfig.get("db_show_sql"));
        config.setProperty("hibernate.dialect", Application.serverConfig.get("db_dialect"));
        config.setProperty("hibernate.hbm2ddl.auto", "validate");
        config.setProperty("hibernate.format_sql", "true");
        config.setProperty("hibernate.current_session_context_class", "thread");

        // Cache
        config.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        config.setProperty("net.sf.ehcache.configurationResourceName", "/ehcache.xml");
        config.setProperty("hibernate.cache.region_prefix", "");
        config.setProperty("hibernate.cache.use_second_level_cache", "true");
        config.setProperty("hibernate.cache.use_query_cache", "true");
        config.setProperty("hibernate.generate_statistics", "false");
        config.setProperty("hibernate.cache.use_structured_entries", "true");
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");

        // C3P0
        config.setProperty("hibernate.connection.provider_class", "org.hibernate.c3p0.internal.C3P0ConnectionProvider");
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
        config.addAnnotatedClass(Server.class);
        config.addAnnotatedClass(Character.class);

        config.addAnnotatedClass(Universe.class);
        config.addAnnotatedClass(Galaxy.class);
        config.addAnnotatedClass(Solar.class);
        config.addAnnotatedClass(Map.class);

        config.addAnnotatedClass(GameObject.class);
    }
}
