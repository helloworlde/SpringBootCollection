package cn.com.hellowood.mybatisplus;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CodeGenerator {


    private static final String PROJECT_DIR = "/Users/Admin/Downloads/Dev/SpringBoot/SpringBootApplicationCollection/SpringBoot-MyBatisPlus/";
    private static final String JAVA_DIR = PROJECT_DIR + "src/main/java/";
    private static final String RESOURCE_DIR = PROJECT_DIR + "src/main/resources/";

    private static final String XML_MAPPER_DIR = RESOURCE_DIR + "mappers/";
    private static final String XML_MAPPER_SUFFIX = "Mapper.xml";

    private static final String DB_DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/product?useSSL=false";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";

    private static final String SUPER_MAPPER_CLASS = "com.baomidou.mybatisplus.mapper.BaseMapper";
    private static final String SUPER_SERVICE_CLASS = "cn.com.hellowood.mybatisplus.service.BaseService";
    private static final String SUPER_SERVICE_IMPL_CLASS = "cn.com.hellowood.mybatisplus.service.BaseServiceImpl";
    private static final String SUPER_CONTROLLER_CLASS = "cn.com.hellowood.mybatisplus.controller.BaseController";

    private static final String PACKAGE_PARENT = "cn.com.hellowood.mybatisplus";
    private static final String MODULE_NAME = "mybatisplus";
    private static final String CONTROLLER_TEMPLATE = "/templates/controller.java";
    private static final String ENTITY_TEMPLATE = "/templates/entity.java";
    private static final String MAPPER_TEMPLATE = "/templates/mapper.java";
    private static final String XML_TEMPLATE = "/templates/mapper.xml";
    private static final String SERVICE_TEMPLATE = "/templates/service.java";
    private static final String SERVICE_IMPL_TEMPLATE = "/templates/serviceimpl.java";
    private static final String[] TABLE_PREFIX = {""};
    private static final String[] GENERATE_TABLES = {"user"};

    private static final String TEMPLATE_SUFFIX = ".vm";

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator();

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig()
                .setOutputDir(JAVA_DIR)
                .setFileOverride(true)
                .setActiveRecord(false)
                // XML 二级缓存
                .setEnableCache(false)
                // XML ResultMap
                .setBaseResultMap(true)
                // XML Column List
                .setBaseColumnList(true)
                .setKotlin(false)
                .setAuthor("HelloWood")
                // %s 会由表名填充
                .setMapperName("%sDao")
                .setXmlName("%sMapper")
                .setServiceName("%sService")
                .setServiceImplName("%sServiceImpl")
                .setControllerName("%sController");

        generator.setGlobalConfig(globalConfig);

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setDriverName(DB_DRIVER_NAME)
                .setUsername(DB_USERNAME)
                .setPassword(DB_PASSWORD)
                .setUrl(DB_URL);

        generator.setDataSource(dataSourceConfig);

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        // 全局大写
        strategyConfig.setCapitalMode(false)
                // 表前缀
                .setTablePrefix(TABLE_PREFIX)
                .setNaming(NamingStrategy.underline_to_camel)
                .setInclude(GENERATE_TABLES)
                // 自定义实体父类
//                .setSuperEntityClass("")
                // 自定义 Mapper 父类
                .setSuperMapperClass(SUPER_MAPPER_CLASS)
//                .setSuperServiceClass(SUPER_SERVICE_CLASS)
//                .setSuperServiceImplClass(SUPER_SERVICE_IMPL_CLASS)
//                .setSuperControllerClass(SUPER_CONTROLLER_CLASS)
                // 实体类是否生成字段常量（默认为 false）public static final String ID = "id";
                .setEntityColumnConstant(false)
                // 实体类是否为构建者模型 （默认为 false）public Product setName(String name){this.name = name; return this;}
                .setEntityBuilderModel(true);

        generator.setStrategy(strategyConfig);

        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                this.setMap(map);
            }
        };

        List<FileOutConfig> fileOutConfigList = new ArrayList<>();


        // 调整 XML 生成目录
        fileOutConfigList.add(new FileOutConfig(XML_TEMPLATE + TEMPLATE_SUFFIX) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return XML_MAPPER_DIR + tableInfo.getEntityName() + XML_MAPPER_SUFFIX;
            }
        });

        injectionConfig.setFileOutConfigList(fileOutConfigList);

        generator.setCfg(injectionConfig);

        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(PACKAGE_PARENT);

        generator.setPackageInfo(packageConfig);

        generator.execute();

    }
}