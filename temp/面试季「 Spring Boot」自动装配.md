# 面试季「 Spring Boot」自动装配

SpringBoot 目前已经成为了Java程序员必备的技能项了，不论你是应届毕业生还是跳槽程序员，熟练掌握SpringBoot是必不可或缺的技能。最近自己打算利用SpringBoot的自动配置原理自己来实现一个类库，借此机会也为大家分享一下我用到了一些技术。临近秋招季，希望本文能秋招的同学带来一些帮助。

来吧！灵魂三问：

- 什么是自动装配？
- 自动装配为我们装配了什么？
- 怎么实现自动装配？

同学你会吗？慌了吗？怀疑人生了吗？稳住！下面请跟着我来一探究竟，寻求答案吧。

## SpringBoot自动配置

大家刚开始学习SpringBoot的时候就知道，我们只需要在：application.properties或application.yml，进行少量的配置边可以实现一个web项目的启动和使用，SpringBoot自动帮我们装配了很多Bean定义最后通过Bean工厂生成Bean缓存到容器中供你使用。
下面给大家提供除了一些常用的配置更多配置项目，大家可以参考官方文档。

[SpringBoot配置项](https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#common-application-properties "SpringBoot配置项")

----

下面咱们就进入正文，看看SpringBoot是怎么实现自动装配的。

## SpringBoot启动类

```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {

        SpringApplication.run(App.class, args);
    }
}
```

这一个小小的注解帮我们启动了SpringBoot，其背后默认帮我们配置了很多自动配置类。其中最重要是 **@SpringBootApplication** 这个注解，我们点进去看一下。

### @SpringBootApplication

``` java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
        @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
```

解说一下三个注解：

- @SpringBootConfiguration : Spring Boot的配置类，标注在某个类上，表示这是一个Spring Boot的配置类（等于xml方式下.xml文件）。
- @EnableAutoConfiguration: 开启自动配置类，SpringBoot的精华所在。
- @ComponentScan：包扫描，等同于xml下开启并设置包扫描路径。

### @EnableAutoConfiguration
>　@EnableAutoConfiguration：告诉SpringBoot开启自动配置功能，这样自动配置才能生效。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
```

- @AutoConfigurationPackage：自动配置包
- @Import: 导入自动配置的组件
  - @Import：通常用于有时没有把某个类注入到IOC容器中，但在运用的时候需要获取该类对应的bean，此时就需要用到@Import注解。加入IOC容器的方式有很多种，当然@Bean注解也可以，但是@Import注解快速导入的方式更加便捷。

### @AutoConfigurationPackage

```java
static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata,
                BeanDefinitionRegistry registry) {
            register(registry, new PackageImport(metadata).getPackageName());
        }
```

它其实是注册了一个Bean的定义，返回了当前主程序类的同级以及子级的包组件。如果你把类写道了APP类的上级时SpringBoot便不会帮你加载报出如404的问题，这也就是为什么，我们要把App放在项目的最高级中。

- SpringApplication.run(App.class, args);为什么要传入主启动类？
  - 其一方面就是要根据该类去解析他所在包，并实现对同级和下级类的扫描。


![](https://static01.imgkr.com/temp/9f3f49e32abf445680ce26f3a7c42607.png)


### @Import

```java
@Import(AutoConfigurationImportSelector.class)
```

到这就到自动装配的核心了，离成功不远了，咱们直接看重点，AutoConfigurationImportSelector通过继承 **DeferredImportSelector，ImportSelector**重写selectImports方法。

该方法奠定了SpringBoot自动<font color ="oragin">批量装配</font>的核心功能逻辑。

```java
	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return NO_IMPORTS;
		}
		AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
				.loadMetadata(this.beanClassLoader);
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
		List<String> configurations = getCandidateConfigurations(annotationMetadata,
				attributes);
		configurations = removeDuplicates(configurations);
		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
		checkExcludedClasses(configurations, exclusions);
		configurations.removeAll(exclusions);
		configurations = filter(configurations, autoConfigurationMetadata);
		fireAutoConfigurationImportEvents(configurations, exclusions);
		return StringUtils.toStringArray(configurations);
	}
```

可以看到getCandidateConfigurations()方法，它其实是去加载 **public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories"** 外部文件。这个外部文件里面，默认有很多自动配置的类，这些类的定义信息将会被SpringBoot批量的加载到Bean定义Map中等待被创建实例。如下：


![](https://static01.imgkr.com/temp/a38e02c0bec94789847268b429c327f5.png)


#### AutoConfigurationMetadata

> Provides access to meta-data written by the auto-configure annotation processor.
>
> 官方注释翻译：提供对自动配置注解写入的元数据的访问能力。


![](https://static01.imgkr.com/temp/0573cfb3a6f943deba3cc6805c8d5970.png)


这个Path下都有什么东西呢？我大概给大家罗列几个：

```properties
#Thu Jun 14 11:34:18 UTC 2018
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration.AutoConfigureAfter=org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration.ConditionalOnClass=com.datastax.driver.core.Cluster,org.springframework.data.cassandra.core.ReactiveCassandraTemplate,reactor.core.publisher.Flux
org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration.ConditionalOnClass=org.apache.solr.client.solrj.SolrClient,org.springframework.data.solr.repository.SolrRepository
org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration.AutoConfigureBefore=org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration.ConditionalOnClass=com.couchbase.client.java.CouchbaseBucket,com.couchbase.client.java.Cluster
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.ConditionalOnClass=org.springframework.amqp.rabbit.core.RabbitTemplate,com.rabbitmq.client.Channel
```

![](https://static01.imgkr.com/temp/147a993a8a1849fb9b549e90bc7c0095.png)


这些东西不是本文的重点，就不细说了。简单来讲 autoConfigurationMetadata 这个参数会在后面的filter方法使用到，而filter方法处理是你的配置类中某些xxxconditionOnClass的，这些在配置类中使用的Class的信息应该就是上述方法提供。

#### 获取候选配置项

> 见文知意：getCandidateConfigurations 获取候选配置项，这个候选配置项就是spring.factories下的配置项。

```java
List<String> configurations = getCandidateConfigurations(annotationMetadata,attributes);
```

下面我们来看看这个方法的细节和作用。翻译下面这句话（返回自动配置类名称）


![](https://static01.imgkr.com/temp/e574a9c6413449ec8d49e74c10e222a4.png)


在loadSpringFactorise方法中边是通过循环如读取spring.factories下的配置信息并缓存到Map中。

![](https://static01.imgkr.com/temp/a94b273ec6d44b09bcc1b8716388865b.png)
![](https://static01.imgkr.com/temp/9706a6794ec245d7b47b64f9f775d0d5.png)



- 第一个箭头首先一次性去加载一些自动配置类，减少重复加载提高效率。
- 第二个箭头则是去该路径下找spring.factories并读取其中设置的自动配置类的信息
- 第三个箭头则是将读取的结果写入Map缓存中





#### 其他方法

> 在读取完自动配置信息后，会经历下面几个方法，主要是对其进行去重处理。

- 防止自己写的自动装配类和Springboot装配的类出现重复，进行去重。

```java
configurations = removeDuplicates(configurations);
```

- 读取@SpringBootApplication中 exclude设置的属性，可以排除自动装填配类。

```java
Set<String> exclusions = getExclusions(annotationMetadata, attributes);
```
![](https://static01.imgkr.com/temp/87e8cfa7e6fe480a86b9802d5d70b3cc.png)


- 处理自动配置类中如@ConditionalOnClass等注解，如果不满足则该配置不生效等情况。
```java
  checkExcludedClasses(configurations, exclusions);
  configurations.removeAll(exclusions);
  configurations = filter(configurations, autoConfigurationMetadata);
```

![](https://static01.imgkr.com/temp/8fb865676a7d46d693ffda50e0403f40.png)


> 触发自动配置导入事件


![](https://static01.imgkr.com/temp/33b56428f43344dfb10c112916a3f7e4.png)



## 归纳总结

当我们使用`@EnableAutoConfiguration`注解激活自动装配时，实质对应着很多`XXXAutoConfiguration`类在执行装配工作，这些`XXXAutoConfiguration`类是在spring-boot-autoconfigure jar中的META-INF/spring.factories文件中配置好的，`@EnableAutoConfiguration`通过SpringFactoriesLoader机制创建`XXXAutoConfiguration`这些bean。`XXXAutoConfiguration`的bean会依次执行并判断是否需要创建对应的bean注入到Spring容器中。

在每个`XXXAutoConfiguration`类中，都会利用多种类型的条件注解`@ConditionOnXXX`对当前的应用环境做判断，如应用程序是否为Web应用、classpath路径上是否包含对应的类、Spring容器中是否已经包含了对应类型的bean。如果判断条件都成立，`XXXAutoConfiguration`就会认为需要向Spring容器中注入这个bean，否则就忽略。

今天就分享这么多，关于**SpringBoot自动装配原理**，你学会了多少？如果觉得文章对你有一丢丢帮助，请点右下角【**在看**】，让更多人看到该文章。