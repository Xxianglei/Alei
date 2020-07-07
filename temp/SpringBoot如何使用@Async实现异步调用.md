>先赞后看，养成习惯 🌹 欢迎微信关注[Java编程之道],每天进步一点点，沉淀技术分享知识。

预祝各位正在高考的小学弟学妹们考上理想的大学，`高考加油`！<br>
学长忠告:报志愿千万`别`选计算机啊~🙄

![](https://user-gold-cdn.xitu.io/2020/7/7/1732972345f4ba40?w=200&h=200&f=png&s=48977)

今天我们聊一下SpringBoot中的异步技术中的`异步线程池`，这一块的内容深入的聊内容还是很多的，所以暂时分为三个部分
- 使用`@Async`实现异步调用以及自定义线程池的实现。
- SpringBoot中异步调用线程池`内部实现原理`。
- 我是如何通过线程池技术将`10s`的任务降低到`ms`级别。

话不多说跟紧我，老司机要发车了！

![](https://user-gold-cdn.xitu.io/2020/7/7/1732975825cb21a1?w=200&h=218&f=png&s=116044)
# 异步调用

> 异步调用这个概念对于学过Java基础的同学来说并不陌生，下面我们以两端代码来直观看看异步和同步的区别以及SpringBoot中实现异步调用的方式。

## 同步任务

```java
/**
 * @Auther: 爱唠嗑的阿磊
 * @Company: Java编程之道
 * @Date: 2020/7/7 20:12
 * @Version 1.0
 */
@Component
public class MyTask {
    public static Random random =new Random();

    public void doTaskOne() throws Exception {
        System.out.println("开始做任务一");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务一，耗时：" + (end - start) + "毫秒");
    }

    public void doTaskTwo() throws Exception {
        System.out.println("开始做任务二");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务二，耗时：" + (end - start) + "毫秒");
    }

    public void doTaskThree() throws Exception {
        System.out.println("开始做任务三");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务三，耗时：" + (end - start) + "毫秒");
    }
}
```
注入MyTask对象，执行三个函数。

```java
 @RestController
    class Test{
        @Autowired
        MyTask myTask;
        @GetMapping("/")
        public void contextLoads() throws Exception {
            myTask.doTaskOne();
            myTask.doTaskTwo();
            myTask.doTaskThree();
        }
    }
```
访问http://127.0.0.1:8080/可以看到类似如下输出：
```java
开始做任务一
完成任务一，耗时：3387毫秒
开始做任务二
完成任务二，耗时：621毫秒
开始做任务三
完成任务三，耗时：4395毫秒
```
## 异步调用
接下来就通过SpringBoot中的异步调用技术，使三个不存在依赖关系的任务实现并发执行。在Spring Boot中，最简单的方式是通过@Async注解将原来的同步函数变为异步函数.

```java
/**
 * @Auther: 爱唠嗑的阿磊
 * @Company: Java编程之道
 * @Date: 2020/7/7 20:12
 * @Version 1.0
 */
@Component
public class MyTask {
    public static Random random =new Random();
    @Async
    public void doTaskOne() throws Exception {
        System.out.println("开始做任务一");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务一，耗时：" + (end - start) + "毫秒");
    }
    @Async
    public void doTaskTwo() throws Exception {
        System.out.println("开始做任务二");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务二，耗时：" + (end - start) + "毫秒");
    }
    @Async
    public void doTaskThree() throws Exception {
        System.out.println("开始做任务三");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务三，耗时：" + (end - start) + "毫秒");
    }
}
```
同时需要在Spring Boot的主程序中配置@EnableAsync使@Async注解能够生效
```java
@EnableAsync
@SpringBootApplication
public class ThreaddemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThreaddemoApplication.class, args);
    }
}
```
再次测试执行你会发现响应结果明显快了不少，但是数据的顺序是乱的。原因是三个函数候已经是异步执行了。主程序在异步调用执行之后，线程的执行顺序得不到保障。

这里可以想到为什么我在 [V-LoggingTool](https://github.com/Xxianglei/V-LoggingTool) 使用可配置的开启的线程池了，因为我存储日志并不关心线程任务的返回值,我需要程序立即往下执行，耗时任务交给线程池去执行就行了。

如果一定要拿到线程执行的结果，对于这个问题怎么处理简单来说`看场景`,可以使用Future的get来阻塞获取结果从而保证得到正确的数据。对于一些超时任务的场景可以在get中设置超时时间。

## 异步回调
> 接着上文所说的解决思路我们可以通过Future<T>来返回异步调用的结果来感知线程是否执行结束并且获取返回值。知道Future/Callable的同学应该不会感到很陌生。

将三个方法都这样处理一下

```
/**
 * @Auther: 爱唠嗑的阿磊
 * @Company: Java编程之道
 * @Date: 2020/7/7 20:12
 * @Version 1.0
 */
@Component
public class MyTask {
    public static Random random =new Random();
    @Async
    public Future<String> doTaskOne() throws Exception {
        System.out.println("开始做任务一");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务一，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<>("任务一完成");
    }
    @Async
    public Future<String> doTaskTwo() throws Exception {
        System.out.println("开始做任务二");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务二，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<>("任务二完成");
    }
    @Async
    public Future<String> doTaskThree() throws Exception {
        System.out.println("开始做任务三");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        System.out.println("完成任务三，耗时：" + (end - start) + "毫秒");
        return new AsyncResult<>("任务三完成");
    }
}

```
改造一下测试类

```
 @RestController
    class Test{
        @Autowired
        MyTask myTask;
        @GetMapping("/")
        public void contextLoads() throws Exception {
            /*myTask.doTaskOne();
            myTask.doTaskTwo();
            myTask.doTaskThree();*/
            long start = System.currentTimeMillis();
            Future<String> task1 = myTask.doTaskOne();
            Future<String> task2 = myTask.doTaskTwo();
            Future<String> task3 = myTask.doTaskThree();
            task1.get();
            task2.get();
            task3.get();
            long end = System.currentTimeMillis();
            System.out.println("任务全部完成，总耗时：" + (end - start) + "毫秒");
        }
    }
```
执行一下
```
开始做任务三
开始做任务一
开始做任务二
完成任务三，耗时：1125毫秒
完成任务二，耗时：1520毫秒
完成任务一，耗时：4344毫秒
任务全部完成，总耗时：4354毫秒
```
当然我只是举一个获取异步回调的例子，实质上，上诉这种写法不可取，因为get是一个阻塞方法，task1如果一直不执行完的话就会一直阻塞在这里。同理还可以使用其他技术来保证一个合理的返回值如：`CountDownLatch`等。

## 自定义线程池

在SpirngBoot中实现自定义线程池很简单，没有接触通过注解实现异步的时候，大家都是自己去写一个线程池然后注入到容器中，最后暴露一下任务提交的方法...但是SpringBoot为你省去了很多繁杂的操作。

- 第一步，先在配置类中定义一个线程池

```java
@EnableAsync
    @Configuration
    class TaskPoolConfig {
        @Bean("taskExecutor")
        public Executor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(10);
            executor.setMaxPoolSize(20);
            executor.setQueueCapacity(200);
            executor.setKeepAliveSeconds(60);
            executor.setThreadNamePrefix("taskExecutor-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            return executor;
        }
    }
```

- 核心线程数10：线程池创建时候初始化的线程数
- 最大线程数20：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
- 缓冲队列200：用来缓冲执行任务的队列
- 允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
- 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
- 线程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy


还有一种写法是去实现一个空接口` AsyncConfigurer` 其内部提供了初始化线程池和获异步异常处理器
```java
public interface AsyncConfigurer {
	/**
	 * The {@link Executor} instance to be used when processing async
	 * method invocations.
	 */
	@Nullable
	default Executor getAsyncExecutor() {
		return null;
	}
	/**
	 * The {@link AsyncUncaughtExceptionHandler} instance to be used
	 * when an exception is thrown during an asynchronous method execution
	 * with {@code void} return type.
	 */
	@Nullable
	default AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}
}
```
这两种常用的写法是有一些区别的，限于篇幅我们下篇文章看@Async实现异步调用的`源码`的时候再去细说。

- 使用该线程池下的线程只需要，@Async注解中指定线程池名即可，比如：

```java
    @Async("taskExecutor")
    public void doTaskOne() throws Exception {
        log.info("开始做任务一");
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(5000));
        long end = System.currentTimeMillis();
        log.info("完成任务一，耗时：" + (end - start) + "毫秒");
    }
```

通过debug发现的确是使用我们自定义的线程池在执行。

![](https://user-gold-cdn.xitu.io/2020/7/7/1732966508747ec8?w=1167&h=436&f=png&s=93887)

## 关闭线程池
引入线程池也会存在不少问题，我就针对一种场景简单说一下如何优雅的关闭线程池。

>比如线程池任务还在执行，其他异步池已经停止了如Redis或者Mysql的连接池，此时线程池访问就会报错。

### 如何解决

在初始化线程的时候加上下面这两句

```java
executor.setWaitForTasksToCompleteOnShutdown(true);
executor.setAwaitTerminationSeconds(60);
```

- setWaitForTasksToCompleteOnShutdown（true)：用来设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean，这样这些异步任务的销毁就会先于Redis线程池的销毁。
- setAwaitTerminationSeconds(60)：该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不被阻塞住。


好了今天就说这么多了，其实还是很简单的运用，希望大家持续关注，后续几天我会@Async实现异步调用的原理，以及我在开发中如何运用线程池技术缩短响应时间。🤞

![](https://user-gold-cdn.xitu.io/2020/7/7/173297d1343d3b97?w=198&h=171&f=jpeg&s=2341)
----


更多精彩好文尽在：Java编程之道 🎁<br>
欢迎各位好友前去关注！🌹