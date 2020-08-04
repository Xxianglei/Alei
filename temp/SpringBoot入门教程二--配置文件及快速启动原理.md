# 配置文件及快速启动原理
## 前言
在上一节中我们进行了SpringBoot的初体验，初步体会到了对于使用者的**简洁友好性**。本节将会讲解SpringBoot的配置文件使用和启动的原理。
## 配置文件讲解
在初始化项目中，我们看到在`resources`目录下有一个`application.properties`文件，properties配置文件的用法大家肯定很熟悉了，所以这不是我们的重点。SpringBoot支持并推荐`YAML`语法的配置文件，`YAML`配置文件的后缀名为`.yml`。
### 关于`YAML`语法介绍
相比较于`properties`文件平铺式地展示，`yml`文件则更像是立体的树状机构，阅读起来更加直观。在使用上有点类似于JSON的格式，采用`key: value`的形式，冒号后面要加一个空格。基本语法如下：
- 大小写敏感
- 使用换行空格缩进表示层级关系，相同层级的缩进数需相同
- 数组值采用下一层级`- value`表示，多个value换行同层级展示即可，支持嵌套数组
- `'#'`符号表示注释

**properties与yml示例比较**
```properties
A.B1.C1=x
A.B1.C2=xx
A.B1.C3=xxx
A.B2.C1=y
A.B2.C2=yy
#数组表示
testArray[0][=1
testArray[1]=2
testArray[2]=3

```

```yml
A:
  B1:
    C1: x
    C2: xx
    C3: xxx
  B2:
    C1: y
    C2: yy
#数组表示
testArray:
  - 1
  - 2
  - 3
```
可以看到，无论是在简洁性或是直观性上`yml`都更为优秀。

### 配置文件参数设置
现在我们可以新建一个`application.yml`文件来替换原有的`application.properties`文件。在其中设置所需的参数，而参数可以分为**框架定义参数**和**自定义参数**两类。对于**框架定义参数**SpringBoot启动会自动去加载，那么**自定义参数**我们又该如何使用呢？SpringBoot提供了`@Value`和`@ConfigurationProperties`两种注解来注入配置文件中自定义参数的值，直接上代码：
**application.yml**
```yml
#修改web端口为8888
server:
  port: 8888

#自定义参数
my-param:
  hello: Hello
  world: World
```
**@Value方式**
```java
    @Value(value = "${my-param.hello}")
    private String hello;

    @Value(value = "${my-param.world}")
    private String world;
```
**@ConfigurationProperties方式**
```java
/**
 * 使用@ConfigurationProperties读取配置文件中自定义参数
 * 'prefix'指定参数前缀名
 * @author yingKang
 * @Company Java编程之道
 */
@Component
@ConfigurationProperties(prefix = "my-param", ignoreUnknownFields = true)
public class MyParamConst {

    private String hello;

    private String world;

    public String getHello() { return hello; }

    public void setHello(String hello) {this.hello = hello; }

    public String getWorld() { return world; }

    public void setWorld(String world) { this.world = world; }
}
```
**测试**
```java
/**
 * @author yingKang
 * @Company Java编程之道
 */
@RestController
@RequestMapping("/first")
public class FirstController {

    @Value(value = "${my-param.hello}")
    private String hello;

    @Value(value = "${my-param.world}")
    private String world;

    @Autowired
    private MyParamConst myParamConst;

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("/hello2")
    public String helloWorld2() {
        return "@Value: " + hello + " " + world;
    }

    @GetMapping("/hello3")
    public String helloWorld3() {
        return  "@ConfigurationProperties： " + myParamConst.getHello() + " " + myParamConst.getWorld();
    }
}
```
通过访问`/hello2`和`/hello3`接口可发现均正常读取到配置文件中的自定义参数。

针对`@Value`和`@ConfigurationProperties`的使用还有许多需要注意的地方，这里先不展开，若有兴趣可以留言我们另起一章来详细说说。
## 快速启动原理分析
>说完了配置文件，我们再来看看SpringBoot的启动原理，为何能实现**零配置运行**。

SpringBoot能实现几乎**零配置运行**的关键地方在于其**自动配置**。那这是怎么实现的呢，我们看到初始化项目中SpringBoot的启动类`Application.java`
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```
有两个特殊的地方：`main`方法和`@SpringBootApplication`注解，这也是SpringBoot能实现自动配置的入口所在。鉴于篇幅有限，本节主要对`@SpringBootApplication`展开，介绍SpringBoot是如何自动找到所需配置文件信息的。至于这些配置是如何被加载及实例化的，同样，若有兴趣可以留言我们另起一章阐述。
***

```java
/**
 * Indicates a {@link Configuration configuration} class that declares one or more
 * {@link Bean @Bean} methods and also triggers {@link EnableAutoConfiguration
 * auto-configuration} and {@link ComponentScan component scanning}. This is a convenience
 * annotation that is equivalent to declaring {@code @Configuration},
 * {@code @EnableAutoConfiguration} and {@code @ComponentScan}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
```
可以看到`@SpringBootApplication`注解类上有如上注解，除去常用注解外，剩余三个注解`@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan`即是精髓所在。
### `@SpringBootConfiguration`
进入这个注解类，发现其标注了一个`@Configuration`注解，标注了@Configuration的类相当于Spring中的配置XML，不过SpringBoot社区推荐使用JavaConfig，所以@Configuration就构建出了一个基础JavaConfig的Ioc容器。
### `@ComponentScan`
这个注解完成的是自动扫描的功能，相当于Spring XML配置文件中的：`<context:component-scan>`，可使用`basePackages`属性指定要扫描的包，及扫描的条件。如果不设置则默认扫描`@ComponentScan`注解所在类的同级类和同级目录下的所有类，所以我们的一般会把启动类(Application.java)放在顶层目录中，这样就能够保证源码目录下的所有类都能够被扫描到。
### `@EnableAutoConfiguration`
`@EnableAutoConfiguration`是SpringBoot实现自动配置的关键注解。看看其内部构造
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration
```
同样的，核心注解为`@AutoConfigurationPackage`和`@Import(AutoConfigurationImportSelector.class)`。我们来看看他们分别做了什么。
- `@AutoConfigurationPackage`

将主配置类（@SpringBootConfiguration标注的类）所在包及下面所有子包里面的所有组件扫描到Ioc容器中。

进入注解，其注解上导入了`AutoConfigurationPackages.Registrar.class`这个类，再跟入`Registrar.class`这个类,
```java
	/**
	 * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
	 * configuration.
	 */
	static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
        //将主配置类（@SpringBootConfiguration标注的类）所在包及下面所有子包里面的所有组件扫描到Ioc容器中
		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
		}

		@Override
		public Set<Object> determineImports(AnnotationMetadata metadata) {
			return Collections.singleton(new PackageImports(metadata));
		}

	}
```
通过Debug我们可以看到，这里返回的包路径就是我们`Application.java`启动类所在的包路径：
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh5jUqQbeyAsP6h69bChnRc1nwuNHKG30UGjQjEciavw1Ka8QfxDCcHCBhA7zskuournuMZK1yjfFGA/0?wx_fmt=png)
- `@Import(AutoConfigurationImportSelector.class)`
在`AutoConfigurationImportSelector`中我们发现该类重写了DeferredImportSelector类中的selectImports方法。在selectImports方法可以跟出这样一条调用链：**selectImports() -> getAutoConfigurationEntry() -> getCandidateConfigurations() -> loadFactoryNames() ->loadSpringFactories()**

在loadSpringFactories()方法中我们可以看到，传入的类加载器去读相对路径为`META-INF/spring.factories`文件中指定类对应的类名称列表
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh5jUqQbeyAsP6h69bChnRc15znMODFx3oiaMjvwboStnSDV2U6SwJwhic8bkdSpLsZ6gcgMIrByZr0g/0?wx_fmt=png)
在咱们的`@SpringBootApplication`注解所在的包下就存在这个spring.factories文件，我们来看下里面有什么
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh5jUqQbeyAsP6h69bChnRc1TzSbOtmsn4Jk1PcvMeicQCO7bwrbmXNNib0vHMr98T5Qibzjl8sP0PywA/0?wx_fmt=png)
这里都是一些starter的配置类名称，是不是有点意思咯。我们找一个配置文件看看，这里以`ServletWebServerFactoryAutoConfiguration`为例
```java
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(ServletRequest.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ServerProperties.class)
@Import({ ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class,
		ServletWebServerFactoryConfiguration.EmbeddedTomcat.class,
		ServletWebServerFactoryConfiguration.EmbeddedJetty.class,
		ServletWebServerFactoryConfiguration.EmbeddedUndertow.class })
public class ServletWebServerFactoryAutoConfiguration
```
可以看到其`@EnableConfigurationProperties(ServerProperties.class)`注解引入了`ServerProperties.class`这个文件，这个文件里有什么呢？
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh5jUqQbeyAsP6h69bChnRc1d0W1PGqBbhlbKbbRbE2ox7ibkCzl97X5CpGic4M1L3v1Pibp3wKgF5Z9A/0?wx_fmt=png)
这不和我们之前手写的配置文件读取类（MyParamConst.java）很像吗，绕了半天，咱们终于绕出来了。
## 总结
SpringBoot支持使用`YAML`语法设置配置文件；

通过`@SpringBootApplication`注解自动加载配置信息是SpringBoot的**零配置运行**核心。

》》》 To Be Continued...