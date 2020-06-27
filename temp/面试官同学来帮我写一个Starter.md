# 实现一个自己的Starter

前文讲到了SpringBoot如何实现自动配置，SpringBoot的自动配置极大的提升了框架的使用效率。今天我们就来说一说面试必问之如何手写starter。

> 本文以C3P0连接池为基础，实现一个C3P0-Starter。

## 前言

首先给大家介绍一些SpringBoot默认支持的部分连接池，看看它的内部是怎么实现的，咱们依葫芦画瓢，干他一个Starter。

**spring-boot-autoconfigure** :SpringBot的自动配置依赖，这一票能不能干成全看这个东西。

咱们直奔主题找到（数据源自动配置类）

```java
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

![](https://static01.imgkr.com/temp/8b85dc6f04f64daa9179da7f1f9eb195.png)


看看这些东西是不是感觉到好像有点意思？还没有？那我们接着看。

```java
 
@Configuration               
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class }) 
@EnableConfigurationProperties(DataSourceProperties.class) 
@Import({ DataSourcePoolMetadataProvidersConfiguration.class,
		DataSourceInitializationConfiguration.class })   
public class DataSourceAutoConfiguration {   
	@Configuration
	@Conditional(EmbeddedDatabaseCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import(EmbeddedDataSourceConfiguration.class)
	protected static class EmbeddedDatabaseConfiguration {
	}
	@Configuration
	@Conditional(PooledDataSourceCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import({ DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class,
			DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.Generic.class,
			DataSourceJmxConfiguration.class })       
	protected static class PooledDataSourceConfiguration {
	}
```

- @Configuration Spring配置类等同于.xml

- @ConditionalOnClass表示只有classpath中能找到DataSource.class和EmbeddedDatabaseType.class时，DataSourceAutoConfiguration这个类才会被spring容器实例化。我们有时候只需要在pom.xml添加某些依赖包，某些功能就自动打开了，正是这个注解帮我们处理了。

- @EnableConfigurationProperties引入了DataSourceProperties的配置。这是个好东西以后写框架一定能用上，看看代码吧。

```java
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties implements BeanClassLoaderAware, InitializingBean {
	private ClassLoader classLoader;
	private String name;
	private boolean generateUniqueName;
	private Class<? extends DataSource> type;
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private String jndiName;
	private DataSourceInitializationMode initializationMode = DataSourceInitializationMode.EMBEDDED;
	private String platform = "all";
	private List<String> schema;
	private String schemaUsername;
	private String schemaPassword;
	private List<String> data;
	private String dataUsername;
	private String dataPassword;
	private boolean continueOnError = false;
	private String separator = ";";
	private Charset sqlScriptEncoding;
```

看到这里你应该知道咱们平时在.yml或.properties里面的配置项是怎么来的以及它注入到哪些属性上了吧。
- @ConfigurationProperties 将配置文件中的配置，以属性的形式自动注入到实体中，要特别说明的一个注属性 `ignoreUnknownFields = false` 这个超好用，自动检查配置文件中的属性是否存在，不存在则在启动时就报错。 

- @Import 默认引入了DataSourceConfiguration的几个内部类 Hikari、Tomcat、Dbcp2、Generic

```java
    /**
	 * Tomcat Pool DataSource configuration.
	 */
	@ConditionalOnClass(org.apache.tomcat.jdbc.pool.DataSource.class)
	@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "org.apache.tomcat.jdbc.pool.DataSource", matchIfMissing = true)
	static class Tomcat extends DataSourceConfiguration {
		@Bean
		@ConfigurationProperties(prefix = "spring.datasource.tomcat")
		public org.apache.tomcat.jdbc.pool.DataSource dataSource(
				DataSourceProperties properties) {
			org.apache.tomcat.jdbc.pool.DataSource dataSource = createDataSource(
					properties, org.apache.tomcat.jdbc.pool.DataSource.class);
			DatabaseDriver databaseDriver = DatabaseDriver
					.fromJdbcUrl(properties.determineUrl());
			String validationQuery = databaseDriver.getValidationQuery();
			if (validationQuery != null) {
				dataSource.setTestOnBorrow(true);
				dataSource.setValidationQuery(validationQuery);
			}
			return dataSource;
		}
	}
	/**
	 * Hikari DataSource configuration.
	 */
	@ConditionalOnClass(HikariDataSource.class)
	@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource", matchIfMissing = true)
	static class Hikari extends DataSourceConfiguration {
		@Bean
		@ConfigurationProperties(prefix = "spring.datasource.hikari")
		public HikariDataSource dataSource(DataSourceProperties properties) {
			HikariDataSource dataSource = createDataSource(properties,
					HikariDataSource.class);
			if (StringUtils.hasText(properties.getName())) {
				dataSource.setPoolName(properties.getName());
			}
			return dataSource;
		}

	}
	/**
	 * DBCP DataSource configuration.
	 */
	@ConditionalOnClass(org.apache.commons.dbcp2.BasicDataSource.class)
	@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "org.apache.commons.dbcp2.BasicDataSource", matchIfMissing = true)
	static class Dbcp2 extends DataSourceConfiguration {
		@Bean
		@ConfigurationProperties(prefix = "spring.datasource.dbcp2")
		public org.apache.commons.dbcp2.BasicDataSource dataSource(
				DataSourceProperties properties) {
			return createDataSource(properties,
					org.apache.commons.dbcp2.BasicDataSource.class);
		}

	}
	/**
	 * Generic DataSource configuration.
	 */
	@ConditionalOnMissingBean(DataSource.class)
	@ConditionalOnProperty(name = "spring.datasource.type")
	static class Generic {
		@Bean
		public DataSource dataSource(DataSourceProperties properties) {
			return properties.initializeDataSourceBuilder().build();
		}

	}
```

-   @ConditionalOnMissingBean(HikariDataSource.class)  这里的Hikari是SpringBoot的亲儿子所以是默认支持的，在此之前还没有实例化HikariDataSource,往下走(如果我们在应用中主动实现了datasource,那默认的hikari就不会再实现了。

- @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource", matchIfMissing = true) 当配置 spring.datasource.type=com.zaxxer.hikari.HikariDataSource或该配置不存在时则程序继续执行。

给大家送上一张条件注解归总图，不熟悉的话可以作为参考，无需多言看我操作。

![](https://static01.imgkr.com/temp/14bf7c39049147a6bb5970c74c05516b.png)


## V-LoggingTool

号外！号外！天啦噜~ 隔壁程序员又写一堆Bug了，大家赶紧去吐槽啊！

代码已经托管在Github平台（https://github.com/Xxianglei/V-LoggingTool）首先欢迎大家 **start/fork** ！⭐<br>
这个项目主要目的是供大家学习如何手写SpringBoot的Starter、AOP日志场景处理、SpringBoot异步技术等。同时该类库也可以快速开发小项目的审计日志功能，提供给需要的同学相关的使用方法，达到开箱即用的目的，详情请看Readme。

拉回来，下面咱么接着唠！

目的：**手写C3P0的Starter**！别忘了咱们是要实现这个东西。

### 第一步，引入依赖库

```xml
   <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <mybatis-plus.version>3.3.2</mybatis-plus.version>
    </properties>
    <dependencies>
         <!--------------------------------------------------------->
         <!--   自动配置,那些条件注解才可以使用,最好设置为 optional 为 true 不往下传递-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
           <!-- C3P0连接池 -->
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.3</version>
        </dependency>
          <!---------------------------可要可不要------------------------------>
         <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
```

其中 spring-boot-autoconfigure 和c3p0是必须引入的，对于其他的依赖我不再强调，我觉得你应该知道是干什么的，哈哈。我的代码里面用到了@Slf4j这个是使用的web的starter里面的log实现，大家可以忽略。

### 第二步实现配置类

```java
@Slf4j
@Configuration
@ConditionalOnClass(DataSource.class)
@EnableConfigurationProperties(C3P0DataSourceProperties.class)
public class C3p0DataSourceAutoConfiguration {

    @Autowired
    C3P0DataSourceProperties dataSourceProperties;

    @Bean("c3p0Pool")
    @ConditionalOnMissingBean
    public DataSource dataSource() throws Exception {
        log.info("Start initializing c3p0 connection pool......");
        // 创建一个 c3p0 的连接池
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(dataSourceProperties.getDriver());
        dataSource.setJdbcUrl(dataSourceProperties.getJdbcUrl());
        dataSource.setUser(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        // 池大小配置
        dataSource.setAcquireIncrement(dataSourceProperties.getAcquireIncrement());
        dataSource.setInitialPoolSize(dataSourceProperties.getInitialPoolSize());
        dataSource.setMinPoolSize(dataSourceProperties.getMinPoolSize());
        dataSource.setMaxPoolSize(dataSourceProperties.getMaxPoolSize());
       
        dataSource.setMaxIdleTime(dataSourceProperties.getMaxIdleTime());
        dataSource.setMaxStatements(dataSourceProperties.getMaxStatements());
        dataSource.setIdleConnectionTestPeriod(dataSourceProperties.getIdleConnectionTestPeriod());
        dataSource.setAcquireRetryDelay(dataSourceProperties.getAcquireRetryDelay());
        dataSource.setAcquireRetryAttempts(dataSourceProperties.getAcquireRetryAttempts());
        dataSource.setBreakAfterAcquireFailure(dataSourceProperties.isBreakAfterAcquireFailure());
        dataSource.setTestConnectionOnCheckout(dataSourceProperties.isTestConnectionOnCheckout());
        log.info("Initialization of c3p0 connection pool completed");
        return dataSource;
    }
}
```

### 第三步配置注入类

```java
@ConfigurationProperties(prefix = "c3p0")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class C3P0DataSourceProperties {
    /**
     * 默认设置如下配置项并提供默认值
     */
    private String driver = "com.mysql.jdbc.Driver";
    private String jdbcUrl = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=Hongkong";
    private String username = "root";
    private String password = "root";
    private int minPoolSize = 2;
    private int initialPoolSize = 10;
    private int maxPoolSize = 50;
    private int acquireIncrement = 5;
    private int maxIdleTime = 1800000;
    private int maxStatements = 1000;
    private int idleConnectionTestPeriod = 60;
    private int acquireRetryAttempts = 30;
    private int acquireRetryDelay = 1000;
    private boolean breakAfterAcquireFailure = false;
    private boolean testConnectionOnCheckout = false;

}
```

### 第四步配置文件

请在resource下的META-INF创建 spring.factories

```xml
#启动自动配置
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.xianglei.config.C3p0DataSourceAutoConfiguration
```
spring.factories解释：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019062623140918.png)

**完事！**

剩下的大家可以自己测试一下我就不再赘述了。<br>Spring官方强烈建议非官方Starter命名应遵循<font color=oragin> {name}-spring-boot-starter</font>  的格式。

总结一下，spring-boot是通过spring-boot-autoconfigure体现了"约定优于配置"的原则,而spring-boot-autoconfigure主要用到了spring.factories和几个常用的条件注解来实现自动配置。

手写starter一共四步。

- 引依赖：主要是 spring-boot-autoconfigure

- 写自动配置类

- 配置项注入类：这一步大家也可以用@Value（“xxx:xxx”）注入参数的方式实现。看个人喜好。

- 添加配置文件

各位小伙伴，更多代码细节请光顾我的GitHub仓库（https://github.com/Xxianglei/V-LoggingTool），如有疑问欢迎大家提出Issue，想成为Collaborator大家可以私信我~ 📫