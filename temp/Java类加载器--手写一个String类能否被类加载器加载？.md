1> 谈到Java的类加载器，大家应该都不陌生。但最近在逛面经分享时看到这样一个问题：`手写一个String类能否被类加载器加载？`笔者自己试了下，发现这个问题几乎把类加载器的原理都考了一遍，不信咱们就来碰一碰它。

# 前言
在探究之前咱们先简单复习下类加载器的基本概念。
首先来张**类加载器结构图**镇场子
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6FOfIqicGzHFtJc3KTHmZz3uOmM3iaictOwl5q7868XOmViaI7aUP2DSKmRwLpoibmUiacKqtLaJh9tUlg/0?wx_fmt=png)
- **启动类加载器**：由`C++`实现，负责加载`JAVA_HOME\lib`目录中的，或通过`-Xbootclasspath`参数指定路径中的，且被虚拟机认可（按文件名识别，如rt.jar）的类。
- **扩展类加载器**：负责加载`JAVA_HOME\lib\ext`目录中的，或通过`java.ext.dirs`系统变量指定路径中的类库。
- **应用程序类加载器**：负责加载用户路径（classpath）上的类库。
- **自定义类加载器**：通过继承`java.lang.ClassLoader`实现自定义的类加载器。
```java
    /**
     * JVM自带的三种类加载器加载路径
     */
    @Test
    void testClassLoader1() {
        System.out.println("启动类加载器加载路径：");
        URL[] bootstrapUrls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for (URL url : bootstrapUrls) {
            System.out.println(url);
        }
        System.out.println("---------------------------------------");
        System.out.println("扩展类加载器加载路径：");
        URL[] extUrls = ((URLClassLoader) ClassLoader.getSystemClassLoader().getParent()).getURLs();
        for (URL url : extUrls) {
            System.out.println(url);
        }
        System.out.println("---------------------------------------");
        System.out.println("应用类加载器加载路径：");
        URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();
        for (URL url : urls) {
            System.out.println(url);
        }
    }
```
```
启动类加载器加载路径：
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/resources.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/rt.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/sunrsasign.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jsse.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jce.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/charsets.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jfr.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/classes
---------------------------------------
扩展类加载器加载路径：
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/access-bridge-64.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/cldrdata.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/dnsns.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/jaccess.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/jfxrt.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/localedata.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/nashorn.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunec.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunjce_provider.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunmscapi.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunpkcs11.jar
file:/C:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/zipfs.jar
---------------------------------------
应用类加载器加载路径：
file:/C:/Users/yingKang/AppData/Local/Temp/classpath1221438621.jar
file:/D:/Program%20Files/JetBrains/IntelliJ%20IDEA%202019.2/lib/idea_rt.jar
```

**双亲委派模型**：当一个类加载器收到类加载任务，会先交给其父类加载器去完成，因此最终加载任务都会传递到顶层的**启动类加载器**，只有当父类加载器无法完成加载任务时，才会尝试执行加载任务。

如果有小伙伴是初次接触类加载器，对以上概念不是很能理解的话，可以先放一放，把下面内容看来后再回来品一品，别有一番风味。

# Start 正文
## 问题剖析
我们知道确定一个类**完整的限定名**包含两个部分：`包路径`和`类名`。通过对标题中的问题分析，其并没有对包路径进行限制，那么我们就采用控制变量法，对类名固定为`String`，包路径不同的情况下进行探究。
## 代码验证
### 1. 包路径不为`java.lang`
首先来品一下这段代码，小伙伴们觉得会输出什么
```java
package com.example.demo.model;

/**
 * <h3>demo</h3>
 * <p>自定义String类</p>
 *
 * @author yingKang
 * @date 2020-07-05 17:09
 */
public class String {

    public static void main(String[] args) {
        Class<String> stringClass = String.class;
        System.out.println("com.example.demo.model.String的类加载器：" + stringClass.getClassLoader());
        System.out.println("com.example.demo.model.String的类名：" + stringClass.getName());
    }
}
```
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6FOfIqicGzHFtJc3KTHmZz3CeP3CVLWsHOuOGicVAIicMRJVHgLialqoLk3G0R1hFZ6oErzhV1Cia2gOQ/0?wx_fmt=png)
结果如图，编译通过，执行报错，原因就是我们的“main方法”是一个假的main方法，代码中main方法参数中的String是本地String类（即我们自定义的String），所以程序自然找不到入口。我们只需加上完整限定名`java.lang.String`即可。输入如下：
```java
com.example.demo.model.String的类加载器：sun.misc.Launcher$AppClassLoader@18b4aac2
com.example.demo.model.String的类名：com.example.demo.model.String
```
可以看到我们自定义的String类被`应用程序类加载器`成功加载。

### 2. 包路径为`java.lang`
再品下这段代码呢
```java
package java.lang;

/**
 * <h3>demo</h3>
 * <p>Custom java.lang.String Class</p>
 *
 * @author yingKang
 * @date 2020-07-05 17:53
 */
public class String {

    public String() {
        System.out.println("Custom java.lang.String Class");
    }

    public static void main(java.lang.String[] args) {
        Class<String> stringClass = String.class;
        try {
            String s = stringClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
结果理所当然的报错了
```
错误: 在类 java.lang.String 中找不到 main 方法, 请将 main 方法定义为:
   public static void main(String[] args)
否则 JavaFX 应用程序类必须扩展javafx.application.Application
```
来分析一波，这波的异常是在`String`类中找不到main方法，但是有的小伙伴会有疑问了：我不是已经定义了main方法，而且参数String类也指定了包路径，怎么还是找不到？

问题就出在这儿，类加载器加载的String类是否是我们自定义的呢？我们来断点下`java.lang.ClassLoader#loadClass(java.lang.String, boolean)`方法看看究竟加载的哪个类：
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6FOfIqicGzHFtJc3KTHmZz3coHyAzXavSs0JLZZehiaVSIkiaicrGxaxd1urKXvs7XJ8fISFhLxEbveA/0?wx_fmt=png)
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6FOfIqicGzHFtJc3KTHmZz3NZFtkHFoxsiaFKobzbqm5iawa6nq8qQMV3MEF2KmILpL4uia1maZ2s4xw/0?wx_fmt=png)
通过截图可以看到，加载的java.lang.String类并不是我们自定义的，而是JDK中。那这是为什么呢？这得回到我们在前言中说到的**双亲委派模型**，所有的类加载器都会从其最终父类**启动类加载器**开始从上往下加载类，
那么位于`rt.jar`中的`java.lang.String`自然会被优先加载。

> 这也是**双亲委派模型**的好处：即避免了类的重复加载，也保证了 Java 的核心 API 不被篡改。如果没有使用双亲委派模型，而是每个类加载器加载自己的话就会出现一些问题，比如我们编写一个称为`java.lang.Object`类的话，那么程序运行的时候，系统就会出现多个不同的`Object`类。

**咱们继续**，那有没有办法能加载我们自定义的`java.lang.String`类呢。既然程序内部加载始终会加载到Java中的String，那如果我们从外部加载class文件呢？不妨来试一试，JVM提供的三个类加载器均无法实现，这时该请出我们的**自定义类加载器**了。
```java
import java.io.*;

/**
 * <h3>demo</h3>
 * <p>Custom ClassLoader</p>
 *
 * @author yingKang
 * @date 2020-07-01 16:30
 */
public class LocalClassLoader extends ClassLoader {

    private String rootUrl;

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    /**
     * 重写{@link ClassLoader#findClass(String)}方法，通过从外部读取class文件来加载类
     * 因为程序外部class文件均不在前三个类加载器加载范围内，所以最终必然会执行我们的自定义类加载器
     * @param name
     * @return
     */
    @Override
    protected Class<?> findClass(String name){
        StringBuilder fileName = new StringBuilder(rootUrl);
        fileName.append(File.separator).append(name.replace(".", File.separator)).append(".class");
        try (InputStream inputStream = new FileInputStream(fileName.toString());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            byte[] classBytes = outputStream.toByteArray();
            return defineClass(name, classBytes, 0, classBytes.length);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> findLocalClass(java.lang.String name){
        return findClass(name);
    }
```
在桌面准备一个我们自定义的`java.lang.String.class`文件
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6FOfIqicGzHFtJc3KTHmZz3teCAvqeicick7wYIQG60GWj21OdHcdCUGMniaDnwpyXJibkAXAPME2HZiaA/0?wx_fmt=png)
```java
package java.lang;

public class String {

    public String() {
        System.out.println("Congratulations!");
    }

}
```
见证奇迹的时刻到咯
```java
    @Test
    void testClassLoader3() {
        LocalClassLoader localClassLoader = new LocalClassLoader();
        localClassLoader.setRootUrl("C:\\Users\\yingKang\\Desktop");
        try {
            Class<?> aClass = localClassLoader.findLocalClass("java.lang.String");
            aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
点击运行后，满怀期待的希望控制台能打印出语句`Congratulations!`，结果迎来得却是如下的红字：
![](https://mmbiz.qpic.cn/mmbiz_png/lmbY6Ocnqh6FOfIqicGzHFtJc3KTHmZz3bxlxqOiafuPBfXwMuRlrmVsEdzziaET8bB5Cz9K7JPqxuu4TIcye5uCw/0?wx_fmt=png)
究竟是怎么回事呢，我们跟进下源码看看：
```java
    /* Determine protection domain, and check that:
        - not define java.* class,
        - signer of this class matches signers for the rest of the classes in
          package.
    */
    private ProtectionDomain preDefineClass(String name,
                                            ProtectionDomain pd)
    {
        if (!checkName(name))
            throw new NoClassDefFoundError("IllegalName: " + name);

        // Note:  Checking logic in java.lang.invoke.MemberName.checkForTypeAlias
        // relies on the fact that spoofing is impossible if a class has a name
        // of the form "java.*"
        if ((name != null) && name.startsWith("java.")) {
            throw new SecurityException
                ("Prohibited package name: " +
                 name.substring(0, name.lastIndexOf('.')));
        }
        if (pd == null) {
            pd = defaultDomain;
        }

        if (name != null) checkCerts(name, pd.getCodeSource());

        return pd;
    }
```
原来Java还留有这一手：就算你躲开了我的三个类加载器，也逃不过我的检查机制。好狠的Java，**不允许加载任何包路径以java.开头的自定义类**
那么本场景就算加载失败了。
## 结果分析
经过咱们的重重验证，最终结果如下：
| 包路径不为`java.lang` |  包路径为`java.lang` |
|:----------------:| -------------:|
| 通过应用类加载器加载成功 | 当从程序内部加载自定义类时，加载失败，默认加载Java中的String；当从外部加载时，加载失败，Java加载类时存在检测机制，不允许加载任何包路径以java.开头的自定义类 |

# 总结
结合对标题问题的探究过程，大家再反过来看**前言**中对类加载器的理论介绍，会不会有另外一番感受呢。类加载涉及到的加载顺序、加载范围以及对自定义类的加载限制小伙伴们可以再理一理，希望在面试过程中关于**类加载器**的问题将不会拖你拿高薪offer的后退。