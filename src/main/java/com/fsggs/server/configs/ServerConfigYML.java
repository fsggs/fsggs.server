package com.fsggs.server.configs;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fsggs.server.Application;
import com.fsggs.server.utils.FileUtils;

import java.io.*;
import java.util.Date;

class ServerConfigYML {
    private Config config;
    private String serverConfigFile = "server.yml";

    @JsonClassDescription("Server config")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Config implements Serializable {
        @JsonProperty("ip")
        String ip = "0.0.0.0";

        @JsonProperty("port")
        int port = 32500;

        @JsonProperty("ssl")
        boolean ssl = false;

        @JsonProperty("client-url")
        String clientUrl = "*";

        @JsonProperty("gate")
        String gate = "";

        @JsonProperty("master-server")
        MasterServer masterServer = new MasterServer();

        @JsonProperty("db")
        Db db = new Db();

        @JsonCreator
        public Config() {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class MasterServer implements Serializable {
            @JsonProperty("url")
            String url = "http://127.0.0.1:32500/";

            @JsonProperty("token")
            String token = "c1285e569b053955ab0d85ca3505900c";

            @JsonProperty("local")
            boolean local = true;

            @JsonCreator
            MasterServer() {
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Db implements Serializable {
            @JsonProperty("driver")
            String driver = "com.mysql.jdbc.Driver";

            @JsonProperty("url-jdbc")
            String url = "mysql://localhost:3306/test";

            @JsonProperty("user")
            String user = "root";

            @JsonProperty("password")
            String password = "";

            @JsonProperty("dialect")
            String dialect = "org.hibernate.dialect.MySQLDialect";

            @JsonProperty("pool_size")
            int poolSize = 1;

            @JsonProperty("show_sql")
            boolean showSql = true;

            @JsonCreator
            Db() {
            }
        }
    }

    ServerConfigYML() {
        try {
            if (FileUtils.isRunnedInJar()) {
                loadConfig();
            } else {
                loadConfig(true);
            }
        } catch (FileNotFoundException e1) {
            try {
                loadConfig(true).saveConfig();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            //TODO:: if only not external exists
            if (config != null) try {
                saveConfig();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    private ServerConfigYML saveConfig() throws IOException {
        File configFile = new File(serverConfigFile);
        OutputStream outputStream = new FileOutputStream(FileUtils.getApplicationPath().toString() + '/' + configFile);

        YAMLFactory yf = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yf);

        outputStream.write(("#Server config\n").getBytes());

        //yf.createGenerator(System.out).writeObject(config);
        yf.createGenerator(outputStream).writeObject(config);
        outputStream.close();

        return this;
    }

    private ServerConfigYML loadConfig() throws IOException {
        return this.loadConfig(false);
    }

    private ServerConfigYML loadConfig(boolean inJar) throws IOException {
        File configFile = new File(serverConfigFile);
        Application.logger.info("Try load " + serverConfigFile + " from " + (inJar ? "jar" : "outside jar"));

        InputStream inputStream;
        if (inJar) {
            inputStream = ClassLoader.getSystemResourceAsStream(serverConfigFile);
        } else {
            inputStream = new FileInputStream(FileUtils.getApplicationPath().toString() + '/' + configFile);
        }
        parseYmlToConfig(inputStream);
        inputStream.close();

        return this;
    }

    private void parseYmlToConfig(InputStream inputStream) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        try {
            config = mapper.readValue(inputStream, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Config getConfig() {
        return config;
    }
}
