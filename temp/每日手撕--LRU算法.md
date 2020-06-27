# 手撕LRU算法

## LRU算法介绍

>`LRU`是Least Recently Used的缩写，即最近最少使用，是一种常用的页面置换算法，选择最近最久未使用的页面予以淘汰。当限定的空间已存满数据时，应当把`最久`没有被访问到的数据淘汰。

简单描述一下在《操作系统》这本书里面对于LRU算法的解说。

假定系统为某进程分配了3个物理块，进程运行时的页面走向为 7 0 1 2 0 3 0 4，开始时3个物理块均为空，那么`LRU` 算法是如下工作的：

![img](https://img2018.cnblogs.com/blog/601033/201905/601033-20190526181915995-40670856.jpg)

这就是最基本的LRU的磁盘调度逻辑，该算法运用领域比较广泛比如Redis的`内存淘汰策略`等等，该算法也是`面试中`面试官常常用来考验面试者代码能力和对LRU算法的正确理解。

以下我主要以为`双向链表+HashMap`的方式手撕一个时间复杂度为O(1)的LRU算法。

> 在Java中，其实LinkedHashMap已经实现了LRU缓存淘汰算法，需要在构造方法第三个参数传入true( accessOrder = true;)，表示按照时间顺序访问。可以直接继承LinkedHashMap来实现。

```java
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private int capacity;

    LRULinkedHashMap(int capacity) {
        //true是表示按照访问时间排序,
        super(capacity, 0.75f, true);
        //传入指定的缓存最大容量
        this.capacity = capacity;
    }

    /**
     * 实现LRU的关键方法，如果map里面的元素个数大于了缓存最大容量，则删除链表的顶端元素
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

### 算法设计思路

- 访问某个节点时，将其从原来的位置删除，并重新插入到链表头部。
- 这样就能保证链表尾部存储的就是最近最久未使用的节点，当节点数量大于缓存最大空间时就淘汰链表尾部的节点。
- 为了使删除操作时间复杂度为 O(1)，就不能采用遍历的方式找到某个节点。
- HashMap 存储着 Key 到节点的映射，通过 Key 就能以 O(1) 的时间得到节点，然后再以 O(1) 的时间将其从双向队列中删除。


![](https://static01.imgkr.com/temp/d7f5a9989a984ce2b02562fb5d4d95c3.png)


一.构建双向链表Node节点

```java
    /**
     * 定义双向链表其中K为Map中的K 降低查找时间复杂度
     */
    class Node {
        K k;
        V v;
        Node pre;
        Node next;

        Node(K k, V v) {
            this.k = k;
            this.v = v;
        }
    }
```

二.定义变量

```java
//定义缓存大小
private int size;
// 存储K和Node节点的映射 Node中会存放KV
private HashMap<K, Node> map;
private Node head;
private Node tail;
```

三.初始化结构体

```java
XLRUCache(int size) {
    this.size = size;
    map = new HashMap<>();
}
```

四.添加元素

```java
/**
 * 添加元素
 * 1.元素存在，将元素移动到队尾
 * 2.不存在，判断链表是否满。
 * 如果满，则删除队首（head）元素，新元素放入队尾元素
 * 如果不满，放入队尾（tail）元素
 */
public void put(K key, V value) {
    Node node = map.get(key);
    if (node != null) {
        //更新值
        node.v = value;
        moveNodeToTail(node);
    } else {
        Node newNode = new Node(key, value);
        //链表满，需要删除首节点
        if (map.size() == size) {
            Node delHead = removeHead();
            map.remove(delHead.k);
        }
        addLast(newNode);
        map.put(key, newNode);
    }
}
```

- 移动元素到链表尾部

```java
 public void moveNodeToTail(Node node) {
        if (tail == node) {
            return;
        }
      // 头节点直接置空
        if (head == node) {   // 备注一
            head = node.next;
            head.pre = null; 
        } else {              // 备注一
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
     // 备注三
        node.pre = tail; 
        node.next = null;
        tail.next = node;
        tail = node;
    }
```

- 看备注一&备注三如下图

![](https://static01.imgkr.com/temp/7dd02fd0675546e5ad98c9ebababcfb5.png)


- 看备注二&备注三如下图

![](https://static01.imgkr.com/temp/0cc03bd5f3ef4c2ebdd99e100b90d7e2.png)


- 删除头节点

```java
 public Node removeHead() {
       // 空链表
        if (head == null) {
            return null;
        }
        Node res = head;
       // 只有一个节点
        if (head == tail) {
            head = null;
            tail = null;
        } else {
        // 多个节点
            head = res.next;
            head.pre = null;
            res.next = null;
        }
        return res;
  }
```

map.remove(delHead.k): 删除Map中的Kv映射关系

- 添加新节点

```java
   public void addLast(Node newNode) {
       // 添加节点为空节点直接返回
        if (newNode == null) {
            return;
        }
       // 如果链表为空则直接添加
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            // 不为空则尾部添加
            tail.next = newNode;
            newNode.pre = tail;
            tail = newNode;
        }
    }
```

如果链表为空则将该元素设置成表头元素同时也是表尾元素。

五.获取元素

```java
public V get(K key) {
    Node node = map.get(key);
    if (node != null) {
        moveNodeToTail(node);
        return node.v;
    }
    return null;
}
```

调度访问后的节点需要移动到链表尾部。

### 完整代码

```java
import java.util.HashMap;

/**
 * @Auther: Xianglei
 * @Company:
 * @Date: 2020/6/27 14:52
 * @Version 1.0
 */
public class XLRUCache<K, V> {
    private int size;
    // 存储K和Node节点的映射 Node中会存放KV
    private HashMap<K, Node> map;
    private Node head;
    private Node tail;

    XLRUCache(int size) {
        this.size = size;
        map = new HashMap<>();
    }

    /**
     * 添加元素
     * 1.元素存在，将元素移动到队尾
     * 2.不存在，判断链表是否满。
     * 如果满，则删除队首元素，放入队尾元素，删除更新哈希表
     * 如果不满，放入队尾元素，更新哈希表
     */
    public void put(K key, V value) {
        Node node = map.get(key);
        if (node != null) {
            //更新值
            node.v = value;
            moveNodeToTail(node);
        } else {
            Node newNode = new Node(key, value);
            //链表满，需要删除首节点
            if (map.size() == size) {
                Node delHead = removeHead();
                map.remove(delHead.k);
            }
            addLast(newNode);
            map.put(key, newNode);
        }
    }

    public V get(K key) {
        Node node = map.get(key);
        if (node != null) {
            moveNodeToTail(node);
            return node.v;
        }
        return null;
    }

    public void addLast(Node newNode) {
        if (newNode == null) {
            return;
        }
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.pre = tail;
            tail = newNode;
        }
    }

    public void moveNodeToTail(Node node) {
        if (tail == node) {
            return;
        }
        if (head == node) {
            head = node.next;
            head.pre = null;
        } else {
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
        node.pre = tail;
        node.next = null;
        tail.next = node;
        tail = node;
    }

    public Node removeHead() {
        if (head == null) {
            return null;
        }
        Node res = head;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = res.next;
            head.pre = null;
            res.next = null;
        }
        return res;
    }

    /**
     * 定义双向链表
     */
    class Node {
        K k;
        V v;
        Node pre;
        Node next;

        Node(K k, V v) {
            this.k = k;
            this.v = v;
        }
    }
}
```
### 测试
![](https://static01.imgkr.com/temp/1f4e86877b7b416793da796170fa0ce1.png)

至此，你应该已经掌握 LRU 算法的思想和实现过程了，这里面最重要的一点是理清楚双向链表和HasMap的`映射`关系以及`节点移动`操作。自此，你知道为什么用双向链表了吗？