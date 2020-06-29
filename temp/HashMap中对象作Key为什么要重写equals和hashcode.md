# 蜜汁 equals & hashcode

> 哈哈！我摊牌了！阿里和美团的面试官都问过我同样的问题，你确定不了解一下？


![](https://static01.imgkr.com/temp/9c4c90eb81a34092958c1c4ef52d824a.png)


今天咱们唠点简单的，这是在`初级开发及校招`面试中经常问的一个问题。

- **HashMap的key为一个对象的时候要注意什么 ?**

- **为什么要同时重写equals和hashcode方法 ？**

给你十秒钟想想你该怎么答... ⌚

想不出来没关系，看了这篇文章后面试遇到同样的问题就是`送分题`。📕


![](https://static01.imgkr.com/temp/008090c27d1f4383b00f9159a0e949fd.png)


### 什么是equals和hashcode方法

我们知道Java中所有的类都继承于Object类及Object类是所有类的父类。当子类调用一个方法时，如果该方法没有被重写则需要往上面找到父类中的方法执行。

#### Object类

```java
 public boolean equals(Object obj) {
        return (this == obj);
 }
 public native int hashCode();
```

在Object类中，hashCode是一个本地方法简单理解为`获取对象地址`，equals方法比较自己和obj`对象地址`是否相等。在这里一定先认识到这两个方法，一个是取地址，一个是比较地址。

---

下面进入正题...

### 对象做Key会怎样

下面以一段小代码演示一下，其输出的结果是 `null` 。先说一句：在这里我们认为我的a和b是有着相同属性的同一个对象（key），我可以通过hashMap.get(b)获取到字符串 hello。但事与愿违。

```java
public class NoHashCodeAndEquals {
    public static void main(String[] args) {
        Object o = new Object();
        HashMap<Demo, String> hashMap = new HashMap<>();
        Demo a = new Demo("A");
        Demo b = new Demo("A");
        hashMap.put(a, "hello");
        String s = hashMap.get(b);
        System.out.println(s);

    }
}
class Demo {
    String key;

    Demo(String key) {
        this.key = key;
    }
}
```

可能大家一眼看过去感觉没问题，a和b不是同一个对象呀！地址肯定不一样！打住！你在说什么，我刚刚说什么来着 ？ 👆 蒙圈了吧！😵（ps：我自己也快说懵圈了🤭）。


![](https://static01.imgkr.com/temp/15fc0fb56a41496583a217113b9d73f4.png)


**稳住！！！**

咱们简单看两行HashMap源码，看完立马就清醒。

![](https://static01.imgkr.com/temp/c4d61bff32234985a0ad85f9c067e9b0.png)


- 获取hashcode计算桶下标，存放元素，看上去没什么毛病就是计算下标而已。对！只是调用key的hashCode计算一个下标值。

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

看到了hashCode方法，咱们就还差一个equals方法，HashMap中对于equals()方法的调用在哪些位置呢？

- put方法（以JDK1.7为例）

```java
public V put(K key, V value) {
...
int hash = hash(key);
// 确定桶下标
int i = indexFor(hash, table.length);
// 先找出是否已经存在键为 key 的键值对，如果存在的话就更新这个键值对的值为 value
for (Entry<K,V> e = table[i]; e != null; e = e.next) {
Object k;
if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
V oldValue = e.value;
e.value = value;
return oldValue;
}
}
...
// 插入新键值对
addEntry(hash, key, value, i)
return null;
}
```

- get方法（以JDK1.7为例）

```java
public V get(Object key) {  
    if (key == null)  
        return getForNullKey();  
    int hash = hash(key.hashCode());  
    for (Entry<K,V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {  
        Object k;  
        if (e.hash == hash && ((k = e.key) == key || key.equals(k)))  
            return e.value;  
    }  
    return null;  
}
```

如上所示：当HashMap调用插入或获取方法，需要将值（key）对应的哈希码与数组中的哈希码比较，相等时，则会通过equals方法比较key值是否相等。

再结合上面我写的代码案例，缓一缓咱们再品一下！

```java
Demo a = new Demo("A");
Demo b = new Demo("A");
```

这两行的代码的含义，我们理解为定义了两个`相同含义`(认为是同一个key)的key对象，但是大家都知道这两个key的hashcode方法的值是不一样的。

在HashMap中的比较key是这样的，先求出key的hashcode(),比较其值是否相等，若相等再比较equals(),若相等则认为他们是相等的。若equals()不相等则认为他们不相等。

- 如果只重写hashcode()不重写equals()方法，当比较equals()时，其实调用的是Object中的方法，只是看他们是否为同一对象（即进行内存地址的比较）。
- 如果只重写equals()不重写hashcode()方法，在一个判断的时候就会被拦下HashMap认为是不同的Key。

所以想以对象作为HashMap的key，**必须重写该对象的hashCode和equals方法**。确保hashCode相等的时候equals的值也是true。

- 图解如下：

![](https://static01.imgkr.com/temp/30e2036dc8b64dc0b2015461f445780b.png)


对于这个问题看似简单，可很多初级程序员竟然说不出一个所以然，一方面是对HashMap不熟悉另外就是对这两个方法的含义理解不透彻。

最后再次总结一句：在HashMap的“键”部分存放自定义的对象，`一定`要重写`equals`和`hashCode`方法。再来两句老生常谈的话！

- 两个对象==相等，则其hashcode一定相等，反之不一定成立。
- 两个对象equals相等，则其hashcode一定相等，反之不一定成立。

自己再品品呢 ~