
>`SpringBoot`和`SpringCloud`作为时下主流的微服务框架，咱们身为求学若渴的快(Tu)乐(Tou)程序员怎么能不来碰一碰。接下来就请抹上油头，穿上小西服，走进猿生，来探一探关于`SpringBoot`、`SpringCloud`学长是如何带你从入门到放弃系列。

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X2pwZy9sbWJZNk9jbnFoNVRsdXFHcnBkT1ZxamNralRiQ3JVMHRNTEw0M0tHSGhyYnhyaDlPMzhOaWJORmljSXZNSUNSa2dOdVZZZnI0NnU2WjZtaWN2ZGliaWFMaWN5dy8w?x-oss-process=image/format,png)

**SpringBoot系列章节目录如下：**
  - 快速实现第一个HelloWorld
  - 配置文件及快速启动原理
  - 整合Swagger及Restful Api设计规范
  - 整合Mybatis-plus
  - CRUD实战

# 前言

上来先素(Bu)质(Dong)三(Ju)连(Wen)：
  - 我为什么要学？
  - 我能学到什么？
  - 我该怎么学？
  
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X2pwZy9sbWJZNk9jbnFoNVRsdXFHcnBkT1ZxamNralRiQ3JVMHUwOUF1bFI3OFRpYU9oQ3BMREV0T3NQZFhyNGg0QTdTSWlhVWttRlpod3VPQXBjUjY2OU5pY3FBUS8w?x-oss-process=image/format,png)

我们一个一个的来，

**我为什么要学？**
上来就送命题，网上关于SpringBoot的技术优势介绍有很多，这里不多说，老铁我就说句实在话：现在采用Java作为后端主要开发语言的公司，**绝大部分**都在使用Spring框架，而这些公司**越来越多**使用传统**SSM**、**SSH**架构的都在朝微服务转型。意思以后我们找Java开发岗位工作的时候这块是妥妥被安排的，总不能跟以后的工作过不去吧。

**我能学到什么？**
网上SpringBoot的相关教程也有不少，但动则就是几十讲或几十个小时，虽然更全面、更系统，但学习周期长，见效慢。那么本教程的目的就是为了让初学者能**快速上手体验操作**SpringBoot，爽完了之后 再回过头去系统性的学习一遍，会体会和理解地更深。
那么本教程设置的内容是后端开发对SpringBoot的使用，基于MVC思想，打通从接口到数据库的链路。

**我该怎么学？**
关于这点老铁少说两句，每人的学习方式不同，强调两点：**一定要动手实践**；**不懂的留言提问**。

感谢看完前言的铁子们，咋们马上进入第一讲。
  
# 快速实现第一个HelloWorld

## 环境配置
1. JDK1.8及以上
2. Maven
3. IDEA（可选）

## 创建项目
Spring官方提供Spring Initializr工具来帮助快速我们创建SpringBoot应用。
1. 进入[Spring Initializr](https://start.spring.io/)：`https://start.spring.io/`

配置顺序如图，每步操作含义：
    ① 选择Maven工程
    ② 语言选择Java
    ③ 选择目前Spring官方推荐的版本`2.3.1`
    ④ 对应`pom.xml`文件中project描述
    ⑤ 初始化包路径
    ⑥ 打包生成Jar文件
    ⑦ 选择JDK版本
    ⑧ SpringBoot基础依赖
    ⑨ 生成project文件
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoNVRsdXFHcnBkT1ZxamNralRiQ3JVMGU2dWJZQm9RcHc5aWNRNGR1V2NEMm0wSUdlN0F0d1JNN0lQSE96WG53aHVGc2liVGljaFpzbW04Zy8w?x-oss-process=image/format,png)

2. 将生成的工程压缩包解压出来，打开IDEA，点击`File`→`Open`打开解压出来的工程包，然后等待Maven自动下载导入所有依赖包

3. 待Maven解析完成后，调整IDEA对project的JDK环境配置
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoNVRsdXFHcnBkT1ZxamNralRiQ3JVMGdZaWIwUmY4b1BSeFlVQ3ZpYkZtNk9GdlFMcGxHMmdOVVlOdTJSZHVFRFltQzA1UTYxNmgwVTh3LzA?x-oss-process=image/format,png)

项目到此就算构建完成。

## 项目初始化文件解析

![项目初始化文件](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoNVRsdXFHcnBkT1ZxamNralRiQ3JVMDNFN0hpYlQ1M1d4T2xvbm96T0x5bkJqbGtTNTBpYmx3TnJBNVExaWFGMkk3eUVEa09zZkJQd3h6QS8w?x-oss-process=image/format,png)

1. `Application.java`:SpringBoot的启动类
2. `ApplicationTests.java`：初始化的单元测试类
3. `pom.xml`：pom文件，我们之前所勾选的**⑧Dependencies**都在这儿

## 实现第一个HelloWorld
1. 首先在`Application.java`同包路径下创建一个MVC结构目录，在`controller`目录下创建我们的第一个**Controller**
```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yingKang
 * @date 2020-07-11 23:44
 */
@RestController
@RequestMapping("/first")
public class FirstController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }
}
```
2. 启动SpringBoot，控制台输出启动日志
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy9sbWJZNk9jbnFoNVRsdXFHcnBkT1ZxamNralRiQ3JVMElqc0dxZXdOaWNpY1ExT0I4THdIRlNpYjh5VGZFVnBSc3gxQWxKRThwUGljeWw0UFFZV2RtS3hxV3cvMA?x-oss-process=image/format,png)
3. 浏览器地址栏：`http://127.0.0.1:8080/first/hello`，见证你的第一个**Hello World**

## 总结
相比较于Spring，SpringBoot在配置文件配置方面的便捷性相信大家已经感受到了，我们甚至没有配置一个配置文件就成功启动了项目。那么这里面是怎么实现的呢，我们下篇文章**配置文件及快速启动原理**见。

拜了个拜，周末愉快
>欢迎关注微信公众号：`Java编程之道`
关注可解锁更多Java开发知识，一同成长，一同快乐🌹