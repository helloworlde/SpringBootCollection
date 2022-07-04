import com.google.common.base.CaseFormat;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerator {

    private static final String BASE_PACKAGE = "cn.com.hellowood.mapper";

    private static final String CONTROLLER_PACKAGE = BASE_PACKAGE.concat(".controller");
    private static final String SERVICE_PACKAGE = BASE_PACKAGE.concat(".service");
    private static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE.concat(".impl");
    private static final String MAPPER_PACKAGE = BASE_PACKAGE.concat(".mapper");
    private static final String MODEL_PACKAGE = BASE_PACKAGE.concat(".model");
    private static final String MAPPER_INTERFACE_REFERENCE = BASE_PACKAGE.concat(".common.CommonMapper");

    // JDBC
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/product?useSSL=false";
    private static final String JDBC_USERNAME = "root";
    private static final String JDBC_PASSWORD = "123456";
    private static final String JDBC_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String MODULE_PATH = PROJECT_PATH.concat("/SpringBoot-MyBatisMapper");

    private static final String TEMPLATE_FILE_PATH = MODULE_PATH.concat("/src/test/resources/generator/template");

    private static final String JAVA_PATH = "/src/main/java";
    private static final String RESOURCES_PATH = "/src/main/resources";

    private static final String PACKAGE_PATH_SERVICE = packageConvertPath(SERVICE_PACKAGE);
    private static final String PACKAGE_PATH_SERVICE_IMPL = packageConvertPath(SERVICE_IMPL_PACKAGE);
    private static final String PACKAGE_PATH_CONTROLLER = packageConvertPath(CONTROLLER_PACKAGE);

    private static final String AUTHOR = "HelloWood";
    private static final String DATE = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    public static void main(String[] args) {
        generateCode("user");
    }

    private static void generateCode(String... tableNames) {
        for (String tableName : tableNames) {
            generateCodeByCustomName(tableName, null);
        }
    }

    private static void generateCodeByCustomName(String tableName, String modelName) {
        generateModelAndMapper(tableName, modelName);
        generateService(tableName, modelName);
        generateController(tableName, modelName);
    }


    private static void generateModelAndMapper(String tableName, String modelName) {

        Context context = new Context(ModelType.FLAT);
        context.setId("Potato");
        context.setTargetRuntime("MyBatis3Simple");
        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

        // JDBC config
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
        jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
        jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
        jdbcConnectionConfiguration.setDriverClass(JDBC_DRIVER_CLASS_NAME);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
        pluginConfiguration.addProperty("mappers", MAPPER_INTERFACE_REFERENCE);
        context.addPluginConfiguration(pluginConfiguration);

        // Model
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(MODULE_PATH.concat(JAVA_PATH));
        javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        // Mapper xml
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(MODULE_PATH.concat(RESOURCES_PATH));
        sqlMapGeneratorConfiguration.setTargetPackage("mappers");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        // Mapper interface
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetProject(MODULE_PATH.concat(JAVA_PATH));
        javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE);
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        if (StringUtils.isNotEmpty(modelName)) {
            tableConfiguration.setDomainObjectName(modelName);
        }
        tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
        context.addTableConfiguration(tableConfiguration);

        List<String> warnings;
        MyBatisGenerator generator;
        try {
            Configuration configuration = new Configuration();
            configuration.addContext(context);
            configuration.validate();

            boolean overwrite = true;
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            warnings = new ArrayList<>();
            generator = new MyBatisGenerator(configuration, callback, warnings);
            generator.generate(null);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("生成 Model 和 Mapper 失败", e);
        }

        if (generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
            throw new RuntimeException("生成 Model 和 Mapper 失败:" + warnings);
        }
        if (StringUtils.isEmpty(modelName)) {
            modelName = tableNameConvertUpperCamel(tableName);
        }
        System.out.println(modelName.concat(".java 生成成功"));
        System.out.println(modelName.concat("Mapper.java 生成成功"));
        System.out.println(modelName.concat("Mapper.xml 生成成功"));

    }


    private static void generateService(String tableName, String modelName) {
        try {
            freemarker.template.Configuration configuration = getFreemarkerConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", tableNameConvertLowerCamel(tableName));
            data.put("basePackage", BASE_PACKAGE);

            File file = new File(MODULE_PATH.concat(JAVA_PATH).concat(PACKAGE_PATH_SERVICE).concat(modelNameUpperCamel.concat("Service.java")));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            configuration.getTemplate("service.ftl")
                    .process(data, new FileWriter(file));

            System.out.println(modelNameUpperCamel.concat("Service.java 生成成功"));

            File serviceImplFile = new File(MODULE_PATH.concat(JAVA_PATH).concat(PACKAGE_PATH_SERVICE_IMPL).concat(modelNameUpperCamel).concat("ServiceImpl.java"));
            if (!serviceImplFile.getParentFile().exists()) {
                serviceImplFile.getParentFile().mkdirs();
            }

            configuration.getTemplate("service-impl.ftl")
                    .process(data, new FileWriter(serviceImplFile));

            System.out.println(modelNameUpperCamel.concat("ServiceImpl.java 生成成功"));

        } catch (Exception e) {
            throw new RuntimeException("生成 Service 失败", e);
        }
    }


    private static void generateController(String tableName, String modelName) {
        try {
            freemarker.template.Configuration configuration = getFreemarkerConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            data.put("baseRequestMapping", modelNameConvertMappingPtah(modelNameUpperCamel));
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
            data.put("basePackage", BASE_PACKAGE);

            File file = new File(MODULE_PATH.concat(JAVA_PATH).concat(PACKAGE_PATH_CONTROLLER).concat(modelNameUpperCamel).concat("Controller.java"));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            configuration.getTemplate("controller-restful.ftl")
                    .process(data, new FileWriter(file));
            System.out.println(modelNameUpperCamel.concat("Controller.java 生成成功"));
        } catch (Exception e) {
            throw new RuntimeException("生成 Controller 失败", e);
        }

    }

    private static String modelNameConvertMappingPtah(String modelName) {
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
        return tableNameConvertMappingPath(tableName);
    }

    private static String tableNameConvertMappingPath(String tableName) {
        tableName = tableName.toLowerCase();
        return "/".concat(tableName.contains("_") ? tableName.replaceAll("_", "/") : tableName);
    }

    private static String tableNameConvertLowerCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
    }

    private static String tableNameConvertUpperCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
    }


    private static String packageConvertPath(String packageName) {
        return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
    }

    public static freemarker.template.Configuration getFreemarkerConfiguration() throws IOException {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        configuration.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
        configuration.setDefaultEncoding("utf-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return configuration;
    }
}
