# 好事说在前面

![](https://imgkr.cn-bj.ufileos.com/05b997fa-c862-4919-b10e-0ab7203c8aed.png)

告诉大家一个好消息🎉，后续会陆续推出:

- Java中的`老大难`问题系列文章如：“JDK源码”、“多线程”、“线程池”、“锁”...

- SpringBoot、SpringCloud 学长带你从入门到放弃系列

- Redis、MySQL 必知必会的底层知识

- 分布式锁、分布式事务
- ...

#### 我值得你等

**你们的文章和别人的有什么区别，我一定要等你的文章吗？  (ˉ▽￣～) 切~~**

答：我们出的文章是自己在面试中遇到的真实问题的`归总`和`解答`，你们针对一个面试点，从别的平台找答案你一定会出现这样的画面

![](https://imgkr.cn-bj.ufileos.com/d38c568c-f41a-4aa9-b290-57ed3b10f860.png)

但是从我们这里找答案你只需要`打开一页`！`一文`解答你这个阶段能遇到所有的`面试提问点`。同时相较于网络上大量的粘贴复制文、水文、转载文，我们追求的是最通俗的`原创文`，通过对知识点的提炼和转化以`聊天的`表达方式，我就不信你还能看不懂。<br>

![](https://imgkr.cn-bj.ufileos.com/ba3fab17-3d8b-4abb-9df5-d568cae1de53.png)

（Ps：何况现在又没广告，免费护眼模式！）



#### 我和我小伙伴的键盘已经敲出`火花`了！

当然有好就有坏，坏消息就是出文的频率可能会更低了，因为目前这些文章需要去研究和梳理大量的源码，在语言的组织和代码的图解上得花不少时间。

但是！！！频率低了不可怕可`质量`高了呀！

![](https://imgkr.cn-bj.ufileos.com/17a4c0b6-7b85-4633-b47c-cc409fad2639.png)


今晚连开股东大会给出了一套完美的解决方案，大家尽管`接着奏乐接着舞`。

不论是山崩地裂还是海枯石烂，我和我的小伙伴坚持以高质量原创的原则为大家分享技术。

你的关注是对我们最大的支持，希望各位朋友能帮我们友情推广！

![](https://imgkr.cn-bj.ufileos.com/e5325a08-dddf-4fa6-ae34-929aec04e9a8.png)



# LinkedList 源码剖析

今天呢，馒头就咸菜，大家就吃点素的，简单聊聊LinkedList的实现原理。


![](https://imgkr.cn-bj.ufileos.com/78ea0e1b-6347-4eb5-99d3-23afa48f9a3b.png)

>LinkedList底层的数据结构是基于双向循环链表的，且头结点中不存放数据,既然是双向链表，那么必定存在一种数据结构——我们可以称之为节点，节点实例保存业务数据，前一个节点的位置信息和后一个节点位置信息。

废话不多说，如果你有数据结构中关于链表的知识，那手写LinkedList根本不需要十分钟。源码就简单的罗列一下，其实源码就是用的**链表**的数据结构来实现的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190307164146403.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

## LinkedList源码

----

```java
 transient int size = 0;
    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;
    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;
    /**
     * Constructs an empty list.
     */
    public LinkedList() {
    } 
   public boolean add(E e) {
        linkLast(e);
        return true;
    }
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }  
    public void add(int index, E element) {
    // 判断是否越界
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }  
  public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190307164205768.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

下面就直接上纯手写的LinkedList..

还是一步一步来介绍。**思路都在注释上。**

- 创建节点Node类

```java
public class Node<E> {

	// 上一个节点
	public Node prev;
	// 节点内容
	public Object object;
	// 下一个节点
	public Node next;

}

```
- 书写构造方法，初始化头尾节点和链表大小


```java

	/**
	 * 当前链表真实长度
	 */
	private int size = 0;

	/**
	 * 第一个头节点
	 */
	private Node<E> first;

	/**
	 * 最后一个尾节点
	 */
	private Node<E> last;

	/**
	 * 构造方法
	 */
	public XLinkedList() {
	}

```
- 先来一个add方法

```java
/**
	 * 默认在链表尾部进行元素添加
	 * 
	 * @param index
	 */
	public void add(E e) {
		Node<E> node = new Node<E>();
		node.object = e;
		/**
		 * 如果链接为空则在头添加 否则在尾部添加
		 */
		if (first == null) {
			first = node;
		} else {
			last.next = node;
			node.prev = last;
		}

		last = node;
		size++;

	}

```
- 再来一个按下标添加

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019030716454696.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

```java
/**
	 * 按下标位置进行add
	 * 
	 * @return
	 */
	public void add(int index, E e) {
		checkElementIndex(index);
		Node<E> newNode = new Node<E>();
		newNode.object = e;
		// 获取原来下标位置的节点
		Node oldNode = getNode(index);
		if (oldNode != null) {
			// 拿到前一个结点 node1
			Node prevNode = oldNode.prev;
			// 拿到后一个节点node3
			Node nextNode = oldNode.next;

			if (oldNode.prev == null) {
				first = newNode;
			} else {
				if (prevNode != null) {
					prevNode.next = newNode;
					newNode.prev = prevNode;

				}
				// 判断是否是最后一个节点
				if (nextNode != null) {
					nextNode.prev = oldNode;
					oldNode.next = newNode;
				}
			}
			newNode.next = oldNode;
			oldNode.prev = newNode;
			size++;
		}

	}
```

- 删除
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190307164633129.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

```java
/**
	 * 删除链表元素 1.首先进行越界判断
	 * 
	 * @return
	 */
	public void remove(int index) {
		checkElementIndex(index);
		// 拿到被删除位置的节点 假如叫node2 其存储结构为 node1-》node2-》node3
		Node node = getNode(index);
		if (node != null) {
			// 拿到前一个结点 node1
			Node prevNode = node.prev;
			// 拿到后一个节点node3
			Node nextNode = node.next;
			// 设置上一个节点的next为当前删除节点的next
			if (prevNode != null) {
				prevNode.next = nextNode;
				node = null;
			}

			// 判断是否是最后一个节点
			if (nextNode != null) {
				nextNode.prev = prevNode;
				node = null;
			}

		}
		size--;
	}
```

- 补充一些其他方法

获取元素：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019030716461181.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

```java
/*
	 * 返回链表大小
	 */
	public int getSize() {
		return size;

	}

	/**
	 * 返回链接中元素
	 * 
	 * @param index
	 * @return
	 */
	public E get(int index) {
		Node node = getNode(index);
		return (E) node.object;
	}

	public Node getNode(int index) {
		Node node = null;
		if (first != null) {
			node = first;
			for (int i = 0; i < index; i++) {
				node = node.next;
			}
		}
		return node;
	}

	/**
	 * 越界判断
	 * 
	 * @param index
	 * @return
	 */
	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index))
			throw new IndexOutOfBoundsException("越界啦!");
	}
```

- Mian方法调用

```java
/**
 *  纯手写 LinkedList
 * @author 程序员快乐的秘密
 *
 */
public class MainTest {
	public static void main(String[] args) {
		XLinkedList<String> xLinkedList = new XLinkedList<String>();
		xLinkedList.add("欢迎关注");
		xLinkedList.add("Java编程之道");
		xLinkedList.add("我是阿磊");
		xLinkedList.add(0, "么么哒");
//		xLinkedList.remove(0);
		for (int i = 0; i < xLinkedList.getSize(); i++) {
			System.out.println(xLinkedList.get(i));
		}
	}
}
```




# 总结

链表存储和数组存储在本质上还是存在一定的区别，这个区别我也是被不少面试官问过，有时候一紧张还真想不起来这么多，趁着你还冷静，多记记！

- 存取方式上，数组可以**顺序存取或者随机存取**，而链表只能**顺序存取**；　
- 存储位置上，**数组**逻辑上相邻的元素在物理存储位置上也**相邻**，而链表不一定；　
- 存储空间上，**链表由于带有指针域**，存储密度不如数组大；　
- 按序号查找时，数组可以随机访问，时间复杂度为O(1)，而链表不支持随机访问，平均需要O(n)；　
- 按值查找时，若数组无序，数组和链表时间复杂度均为O(1)，但是当数组有序时，可以采用折半查找将时间复杂度降为O(logn)；　
- 插入和删除时，数组平均需要移动n/2个元素，而链表只需修改指针即可；　

- 空间分配方面：数组在静态存储分配情形下，存储元素数量受限制，动态存储分配情形下，虽然存储空间可以扩充，但需要移动**大量元素**，导致操作**效率降低**，而且如果内存中没有更大块连续存储空间将导致**分配失败**；链表存储的节点空间只在需要的时候**申请分配**，只要内存中有空间就可以分配，操作比较**灵活高效**；

- 数组从**栈**中分配空间, 对于程序员方便快速,但自由度小。
- 链表从**堆**中分配空间, 自由度大但申请管理比较麻烦.　

