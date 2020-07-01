> 先赞后看，养成习惯 🌹 欢迎微信关注 `[Java编程之道] `每天进步一点点，沉淀技术分享知识。<br>

>后续发布的文章、源码等，都会收录到GitHub:`Xxianglei/Alei`仓库之中，欢迎大家前来自取。

今天由于时间关系就聊一点简单，我正在计划后期写作的文章，近期工作上接触了一些有意思的东西，后面会跟大家唠唠。如果你有意见或建议欢迎留言。📫<br>

`***文末有惊喜***` 🎁

# List 集合
---
- List集合代表一个**有序集合**，集合中每个元素都有其对应的顺序索引。List集合允许使用重复元素，可以通过索引来访问指定位置的集合元素。
- List接口继承于Collection接口，它可以定义一个允许重复的有序集合。因为List中的元素是有序的，所以我们可以通过使用索引（元素在List中的位置，类似于数组下标）来访问List中的元素，这类似于Java的**数组**。
- List接口为Collection直接接口。List所代表的是有序的Collection，即它用某种特定的插入顺序来维护元素顺序。用户可以对列表中每个元素的插入位置进行精确地控制，同时可以根据元素的整数索引（在列表中的位置）访问元素，并搜索列表中的元素。实现List接口的集合主要有：**ArrayList、LinkedList、Vector、Stack**。

接下来先借鉴一下源码，我们照着源码的思路，来写一个自己的ArrayList集合。

在开始看源码之前我需要补充说明一下**数组扩容技术**

- Arrays.copyOf（Object[] original, int newLength）
功能是实现数组的复制，返回复制后的数组。参数是被复制的**数组**和复制的**长度**。这个没什么好说的就等于是把数组按**新**的大小给重新赋值了一份返回了一个新的数组。

- System.arraycopy(src, srcPos, dest, destPos, length);
如果是数组比较大，那么使用System.arraycopy会比较有优势，因为其使用的是内存复制，省去了大量的数组寻址访问等时间

参数解释：

- src:源数组；	
- srcPos:源数组要复制的起始位置；
- dest:目的数组；
- destPos:目的数组放置的起始位置；	length:复制的长度。

注意：src and dest都必须是同类型或者可以进行转换类型的数组．

**原理图解**：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190306133702240.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
## ArrayList 源码
---
1. 想想我们需要手写一个ArrayList、需要看点什么？构造方法？

```java
    /**
     * Default initial capacity. 
     */
    private static final int DEFAULT_CAPACITY = 10;
      /**
     * Shared empty array instance used for empty instances.
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};
    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

```
可以看到，默认无参时，数组的初始容量大小是10.

2. 带参构造方法
```java
//	带参构造方法
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        }
    }
```
3. 看看数据添加方法
```java
   /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
    
     private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }

    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
    //  数据扩容 这里用了一个位运算
     private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

```
看到这里你基本上也清楚了，ArrayList的扩容倍数是1.5倍。比如oldCapacity=2，oldCapacity + (oldCapacity >> 1)执行后就等于3。

补充说明：**位运算** 
-  <<(向左位移) 针对二进制，转换成二进制后向左移动1位，后面用0补齐
- \>>(向右位移) 针对二进制，转换成二进制后向右移动1位

4. 看看按索引添加，类似于插入吧
 ```java
   public void add(int index, E element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        size++;
    }
 ```
其实和添加没有多大的不同，就是用到了数组的赋值，如下图 原size=5，现在F要插入到下标为2的位置，所以 System.arraycopy(elementData, 2, elementData, index + 1,  5 - 2); 后移后面的三位元素。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190306135205499.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
5. 再看看remove方法

1. 按索引删除。这个很简单，原理同添加，只不过需要注意的是如果删除的是最后一位要进行一个判断，就不需要进行数组赋值，直接置空即可！

```java
    public E remove(int index) {
        rangeCheck(index);

        modCount++;
        E oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }
```

2.按对象删除. 这个相信大家都能看懂
	
```java
 public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
```

好了，源码基础的内容就看完咯。接下来就是见证奇迹的时刻了！

## 手写 ArrayList

说明以及被我写在注释里了，没有特别之处我就不做多余的提示了。

```java
package com.xianglei.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtArrayList<E> implements ExtList<E> {
	// ArrayList底层采用数组存放
	private Object[] elementData;
	// 默认数组容量
	private static final int DEFAULT_CAPACITY = 10;
	// 记录实际ArrayList大小 初始为0
	private int size;

	// 默认容量大小为10
	public ExtArrayList() {
		this(DEFAULT_CAPACITY);
	}

	// 构造方法指定数组容量
	public ExtArrayList(int defaultCapacity) {

		if (defaultCapacity < 0) {
			throw new IllegalArgumentException("初始容量不能小于0");
		}
		elementData = new Object[defaultCapacity];

	}

	public void add(E e) {
		// 1.判断实际存放的数据容量是否大于elementData容量
		ensureExplicitCapacity(size + 1); // 判断实际存储的数据所占容量大小
		// 从下标0开始存储
		elementData[size++] = e;

	}

	// int minCapacity 最当前size+1 最小的扩容量
	private void ensureExplicitCapacity(int minCapacity) {
		// 一旦等于数组容量大小就进行扩容
		if (size == elementData.length) {
			// 原来本身elementData容量大小 2
			int oldCapacity = elementData.length;
			// 新数据容量大小 (oldCapacity >> 1)=oldCapacity/
			// <<(向左位移) 针对二进制，转换成二进制后向左移动1位，后面用0补齐
			// >>(向右位移) 针对二进制，转换成二进制后向右移动1位，
			/**
			 * 如果初始是1的话 目前扩容后 newCapacity还是1
			 */
			int newCapacity = oldCapacity + (oldCapacity >> 1);// (2+2/2)=3
			// 如果初始容量为1的时候,那么他扩容的大小为多少呢？
			if (newCapacity - minCapacity < 0)
				newCapacity = minCapacity; // 最少保证容量和minCapacity一样
			// 将老数组的值赋值到新数组里面去
			elementData = Arrays.copyOf(elementData, newCapacity);
		}
	}
	// 按下标删除
	public Object remove(int index) {
		rangeCheck(index);
		Object object = get(index);
		int numMoved;
		numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		}
		elementData[--size] = null;
		return object;
	}
	
	public boolean removeObj(E object) {
		for (int i = 0; i < elementData.length; i++) {
			Object element = elementData[i];
			if (element.equals(object)) {
				remove(i);
				return true;
			}
		}
		return false;
	}
	

	public void insert(int index, E e) {
		// 1.判断实际存放的数据容量是否大于elementData容量
		ensureExplicitCapacity(size + 1);
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		elementData[index] = e;
		size++;
	}

	private void rangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException("越界啦!");
	}

	public int getSize() {
		return size;
	}

	// 使用下标获取数组元素
	public Object get(int index) {
		rangeCheck(index);
		return elementData[index];
	}
	
	public boolean remove(E object) {
		// TODO Auto-generated method stub
		return false;
	}
}

```

做个简单的优化，使用泛型接口。

```java
public interface ExtList<E>  {
	public void add(E object);

	public void insert(int index, E object);

	public Object remove(int index);

	public boolean removeObj(E object);

	public int getSize();

	public Object get(int index);
	
}
```

大家都听说过ArrayList是**线程不安全**的，我想通过源码大家已经知道为什么不安全了。没有**synchronized**关键字。与之相反的有一个Vector集合，他就是**线程安全**的。他们俩的源码很相似，Vector无非只是加上了锁synchronized。

- Vector的add方法：
```java
  public synchronized boolean add(E e) {
        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = e;
        return true;
    }
```
----
## 总结
----
Vector是线程安全的，但是**性能**比ArrayList要**低**。

ArrayList，Vector主要区别为以下几点： 
- Vector是线程安全的，源码中的方法都用synchronized修饰，而ArrayList不是。Vector效率无法和ArrayList相比。 
- ArrayList和Vector都采用**线性连续存储空间**，当存储空间不足的时候，ArrayList默认增加为原来的50%，Vector默认增加为原来的一倍。 **也就是常知的1.5 和 2 倍**
- Vector可以设置capacityIncrement，而ArrayList不可以，从字面理解就是capacity容量，Increment增加，容量增长的参数。
- ArrayList 查询快，修改、删除、插入慢。因为除了查询操作其他都涉及大量的元素移动。

---

该文章转至我的 [CSDN博客](https://blog.csdn.net/u011583316)，近期我已经在`掘金`注册了一个新博客账号 [爱唠嗑的阿磊](https://juejin.im/user/5985a384f265da3e0f119974) ,CSDN的博客号我不会再更新了，欢迎大家来掘金溜达溜达---一个全是`好文`的地方。