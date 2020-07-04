# 闲谈ThreadLocal

> 前面在我的GitHub仓库 [V-LoggingTool](https://github.com/Xxianglei/V-LoggingTool) 中有简单的使用过ThreadLocal，主要用在了切面类中，功能上需要取到前置增强拦截到的用户信息暂存，执行到后置增强时从该ThreadLocal中取出用户信息并使用。

今天咱们就唠唠ThreadLocal的相关知识，了解一下他的数据结构、用法、原理等。咱们层层深入...

![](https://imgkr.cn-bj.ufileos.com/c7694f33-7d30-4d6d-9039-2358c4a04486.png)

看了网上不少关于ThreadLocal的讲解，源码比较简单但是对于Thread、ThreadLocal、ThreadLocalMap的关系讲的有点晦涩，尤其是那张亘古不变的**ThreadLocal的内部结构图**，额...我真的看了很久才明白是怎么回事。

# ThreadLocal工具类

> ThreadLocal是一个本地线程副本变量工具类，主要用于将私有线程和该线程存放的副本对象做一个映射，各个线程之间的变量互不干扰。

官方说的还是比较明白了，提炼关键字`工具类`，在我看来ThreadLocal就是提供给每个线程操作变量的工具类，做到了线程之间的变量隔离目的

## 内部结构图

![](https://user-gold-cdn.xitu.io/2020/7/2/1730fbcbacc9b26c?w=858&h=476&f=png&s=29619)

接下来就是看图说话：

- 每个Thread线程内部都有一个ThreadLocalMap。
- Map里面存储线程本地对象ThreadLocal（key）和线程的变量副本（value）。
- Thread内部的Map是由ThreadLocal维护，ThreadLocal负责向map获取和设置线程的变量值。
- 一个Thread可以有多个ThreadLocal。

每个线程都有其独有的Map结构，而Map中存有的是ThreadLocal为Key变量副本为Vaule的键值对，以此达到变量隔离的目的。

平时是怎么使用ThreadLocal的?

```java
package threadlocal;

/**
 * @Auther: Xianglei
 * @Company: Java编程之道
 * @Date: 2020/7/2 21:44
 * @Version 1.0
 */
public class main {
    private static ThreadLocal<String> sThreadLocal = new ThreadLocal<>();
    public static void main(String args[]) {
        sThreadLocal.set("这是在主线程中");
        System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
        //线程a
        new Thread(new Runnable() {
            @Override
            public void run() {
                sThreadLocal.set("这是在线程a中");
                System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
            }
        }, "线程a").start();
        //线程b
        new Thread(new Runnable() {
            @Override
            public void run() {
                sThreadLocal.set("这是在线程b中");
                System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
            }
        }, "线程b").start();
        //线程c  
        new Thread(() -> {
            sThreadLocal.set("这是在线程c中");
            System.out.println("线程名字：" + Thread.currentThread().getName() + "---" + sThreadLocal.get());
        }, "线程c").start();
    }
}

```

输出结果如下

```java
线程名字：main---这是在主线程中
线程名字：线程b---这是在线程b中
线程名字：线程a---这是在线程a中
线程名字：线程c---这是在线程c中
Process finished with exit code 0
```

可以看出每个线程各通过ThreadLocal对自己ThreadLocalMap中的数据存取并没有出现脏读的现象。就是因为每个线程内部已经存储了ThreadLocal为Key变量副本为Vaule的键值对。（隔离了）

可能你有点懵，ThreadLocal是怎么把变量复制到Thread的ThreadLocalMap中的？

咱们接着唠...

![](https://imgkr.cn-bj.ufileos.com/9252fd41-7318-40a6-96c7-1b77e4269137.png)


当我们初始化一个线程的时候其内部干去创建了一个`ThreadLocalMap的Map容器`待用。

```
public class Thread implements Runnable {
    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```

当ThreadLocalMap被创建加载的时候其静态内部类Entry也随之加载，完成初始化动作。

```
 static class Entry extends WeakReference<ThreadLocal<?>> {
        /** The value associated with this ThreadLocal. */
       Object value;
        Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
}
```

到此，线程Thread内部的Map容器初始化完毕，那么它又是如何和ThreadLocal缠上关系，ThreadLocal又是如何管理键值对的关系。


![](https://imgkr.cn-bj.ufileos.com/f8eb9b93-6de7-4cb4-a123-d96dcfadd809.png)

## ThreadLocal探析

我们就其核心方法分析一下内部的逻辑，同时解答上述存在的疑问：

- set()方法用于保存当前线程的副本变量值。

- get()方法用于获取当前线程的副本变量值。
- initialValue()为当前线程初始副本变量值。
- remove()方法移除当前线程的副本变量值。

### set方法

```java
/**
 * Sets the current thread's copy of this thread-local variable
 * to the specified value.  Most subclasses will have no need to
 * override this method, relying solely on the {@link #initialValue}
 * method to set the values of thread-locals.
 *
 * @param value the value to be stored in the current thread's copy of
 *        this thread-local.
 */
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}

void createMap(Thread t, T firstValue) {
    t.threadLocals = new ThreadLocalMap(this, firstValue);
}
```

解说一下你就懂了：

当我们在Thread内部调用set方法时:

- 第一步会去获取`调用当前方法的线程Thread`。

- 然后顺其自然的拿到当前`线程内部`的`ThreadLocalMap`容器。
- 最后就把变量`副本`给丢进去。

没了...懂了吗，ThreadLocal（就认为是个维护线程内部变量的工具！）只是在Set的时候去操作了Thread内部的·`ThreadLocalMap`将变量拷贝到了Thread内部的Map容器中，Key就是当前的ThreadLocal,Value就是变量的副本。


### get方法

```java
/**
 * Returns the value in the current thread's copy of this
 * thread-local variable.  If the variable has no value for the
 * current thread, it is first initialized to the value returned
 * by an invocation of the {@link #initialValue} method.
 *
 * @return the current thread's value of this thread-local
 */
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null)
            return (T)e.value;
    }
    return setInitialValue();
}

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}

private T setInitialValue() {
    T value = initialValue();
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
    return value;
}

protected T initialValue() {
    return null;
}
```

- 获取当前线程的ThreadLocalMap对象

- 从map中根据this（当前的threadlocal对象）获取线程存储的Entry节点。
- 从Entry节点获取存储的对应Value副本值返回。
- map为空的话返回初始值null，即线程变量副本为null。

### remove方法

清除Map中的KV

```java
/**
 * Removes the current thread's value for this thread-local
 * variable.  If this thread-local variable is subsequently
 * {@linkplain #get read} by the current thread, its value will be
 * reinitialized by invoking its {@link #initialValue} method,
 * unless its value is {@linkplain #set set} by the current thread
 * in the interim.  This may result in multiple invocations of the
 * <tt>initialValue</tt> method in the current thread.
 *
 * @since 1.5
 */
public void remove() {
 ThreadLocalMap m = getMap(Thread.currentThread());
 if (m != null)
     m.remove(this);
}

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}

 /**
  * Remove the entry for key.
  */
    private void remove(ThreadLocal<?> key) {
    Entry[] tab = table;
    int len = tab.length;
    int i = key.threadLocalHashCode & (len-1);
       for (Entry e = tab[i];
            e != null;
            e = tab[i = nextIndex(i, len)]) {
              if (e.get() == key) {
                  e.clear();
                  expungeStaleEntry(i);
                  return;
              }
          }
    }
```

下面再认识一下`ThreadLocalMap`，一个真正存储（隔离）数据的东西。

## ThreadLocalMap

ThreadLocalMap是ThreadLocal的`内部类`，实现了一套自己的Map结构，咱们看一下内部的继承关系就一目了然。

![](https://user-gold-cdn.xitu.io/2020/7/2/1730fec13ae2d54d?w=824&h=954&f=png&s=76254)

其Entry使用的是K-V方式来组织数据，Entry中key是ThreadLocal对象，且是一个弱引用（弱引用，生命周期只能存活到`下次GC前`）。

对于`弱引用`引发的问题我们`最后再说`。

```java
static class Entry extends WeakReference<ThreadLocal<?>> {
        /** The value associated with this ThreadLocal. */
         Object value;

         Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
 }
```

ThreadLocalMap的成员变量

```java
static class ThreadLocalMap {
    /**
     * The initial capacity -- MUST be a power of two.
     */
    private static final int INITIAL_CAPACITY = 16;

    /**
     * The table, resized as necessary.
     * table.length MUST always be a power of two.
     */
    private Entry[] table;

    /**
     * The number of entries in the table.
     */
    private int size = 0;

    /**
     * The next size value at which to resize.
     */
    private int threshold; // Default to 0
}
```

### HashCode 计算

ThreaLocalMap中没有采用传统的调用ThreadLocal的hashcode方法（继承自object的hashcode），而是调用`nexthashcode`，源码如下：

```java
private final int threadLocalHashCode = nextHashCode();
private static AtomicInteger nextHashCode = new AtomicInteger();
 //1640531527 能够让hash槽位分布相当均匀
private static final int HASH_INCREMENT = 0x61c88647; 
private static int nextHashCode() {
      return nextHashCode.getAndAdd(HASH_INCREMENT);
}
```

### Hash冲突

和HashMap的最大的不同在于，ThreadLocalMap解决Hash冲突的方式就是简单的步长加1或减1及线性探测，寻找下一个相邻的位置。

```java
/**
 * Increment i modulo len.
 */
private static int nextIndex(int i, int len) {
    return ((i + 1 < len) ? i + 1 : 0);
}

/**
 * Decrement i modulo len.
 */
private static int prevIndex(int i, int len) {
    return ((i - 1 >= 0) ? i - 1 : len - 1);
}
```

ThreadLocalMap采用线性探测的方式解决Hash冲突的效率很低，如有大量不同的ThreadLocal对象放入map中时发送冲突。所以建议每个线程只存一个变量（一个ThreadLocal）就不存在Hash冲突的问题，如果一个线程要保存set多个变量，就需要创建多个ThreadLocal，多个ThreadLocal放入Map中时会极大的增加Hash冲突的可能。

清楚意思吗？当你在一个线程需要保存多个变量时，你以为是多次set？你错了你得创建多个ThreadLocal，多次set的达不到存储多个变量的目的。

```java
sThreadLocal.set("这是在线程a中");
```

## Key的弱引用问题

看看官话，为什么要用弱引用。

>To help deal with very large and long-lived usages, the hash table entries use WeakReferences for keys.<br>
>为了处理`非常大`和`生命周期`非常长的线程，哈希表使用弱引用作为 key。

- 生命周期长：暂时可以想到线程池中的线程

ThreadLocal在没有外部对象强引用时如Thread，发生GC时弱引用Key会被回收，而Value是强引用不会回收，如果创建ThreadLocal的线程一直持续运行如线程池中的线程，那么这个Entry对象中的value就有可能一直得不到回收，发生内存泄露。

- key 如果使用强引用：引用的ThreadLocal的对象被回收了，但是ThreadLocalMap还持有ThreadLocal的强引用，如果没有手动删除，ThreadLocal不会被回收，导致Entry内存泄漏。

- key 使用弱引用：引用的ThreadLocal的对象被回收了，由于ThreadLocalMap持有ThreadLocal的弱引用，即使没有手动删除，ThreadLocal也会被回收。value在下一次ThreadLocalMap调用set,get，remove的时候会被清除。

Java8中已经做了一些优化如，在ThreadLocal的get()、set()、remove()方法调用的时候会清除掉线程ThreadLocalMap中所有Entry中Key为null的Value，并将整个Entry设置为null，利于下次内存回收。

Java8中for循环遍历整个Entry数组，遇到key=null的就会替换从而避免内存泄露的问题。

```java
       private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;
                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }
```


通常ThreadLocalMap的生命周期跟Thread（注意线程池中的Thread）一样长，如果没有手动删除对应key（线程使用结束归还给线程池了，其中的KV不再被使用但又不会GC回收，可认为是内存泄漏），一定会导致内存泄漏，但是使用弱引用可以多一层保障：弱引用ThreadLocal会被GC回收，不会内存泄漏，对应的value在下一次ThreadLocalMap`调用set,get,remove的时候会被清除`，Java8已经做了上面的代码优化。


![](https://imgkr.cn-bj.ufileos.com/d6769b83-1908-4db7-aa4a-f7951f4402e8.png)


## 总结

- 每个ThreadLocal只能保存一个变量副本，如果想要一个线程能够保存多个副本以上，就需要创建多个ThreadLocal。
- ThreadLocal内部的ThreadLocalMap键为弱引用，会有内存泄漏的风险。

- 每次使用完ThreadLocal，都调用它的remove()方法，清除数据。