# 面试必刷《剑指Offer》-- 上

> 总所周知，不论你是面试Java/C++/C...的岗位，算法是鉴定一个人基础的重要指标，良好的算法基础也是你能通过笔试的必要条件。剑指Offer是众多企业历年招聘的经典题目，刷上几遍剑指Offer，是你临阵磨枪的最好选择。

以下内容是关于剑指Offer的归总，限于篇幅分为上中下三篇文章。其中打🎉的表示是我面试期间遇到的原题或类似题。

以下算法是本人通过Java语言实现，若有错误或不足欢迎指正。

该代码和文档收录在：
**https://github.com/Xxianglei/AC_Offer**

更多详细可见：
**https://blog.csdn.net/u011583316/category_9288903.html**

## 1.数组中重复的数字🎉

### 题目描述

在一个长度为**n**的数组里的所有数字都在**0到n-1**的范围内。 数组中某些数字是重复的，但不知道有几个数字是重复的。也不知道每个数字重复几次。请找出数组中**任意一个**重复的数字。例如，如果输入长度为7的数组{2,3,1,0,2,5,3}，那么对应的输出是重复的数字2或者3。

### 解题思路

- 初级：从头到尾扫描找出重复的。**时间复杂度O（nlogn），空间复杂度:O(1)**。
- 中级：利用Hash表解决。**时间复杂度O（n）**，但是利用率额外的空间。**空间复杂度:O(n)**。
- 高级：从头到尾扫描数组A，当扫描到下标为i的数字m时，首先比较这个数字m是不是等于i，如果是，则继续扫描下一个数字；如果不是，则判断它和A[m]是否相等，如果是，则找到了第一个重复的数字（在下标为i和m的位置都出现了m）；如果不是，则把A[i]和A[m]交换，即把m放回属于它的位置；重复上述过程，直至找到一个重复的数字；**时间复杂度：O(n)，空间复杂度：O(1)**

#### 最优算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505145233839.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

##### 参考代码：

```java
package offer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * 找出所有重复的  每个元素范围都在0~n-1
 */
public class Offer3 {
    static int nums[] = {2, 3, 1, 0, 2, 5, 3};

    public static void main(String[] args) {
        // System.out.println(findRe(nums));
        //System.out.println(findReHash(nums));
        findRe2(nums);

    }
    /**
     * 排序+暴力搜索
     *
     * @param nums
     * @return
     */
    public static String findRe(int nums[]) {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] == nums[i + 1]) {
                stringBuilder.append("重复数字" + nums[i]);
            } else {
                continue;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * hash 解决
     *
     * @param nums
     * @return
     */
    private static String findReHash(int[] nums) {
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i])) {
                map.put(nums[i], map.get(nums[i]) + 1);
            } else {
                map.put(nums[i], 1);
            }
        }
        Set<Integer> keySet = map.keySet();
        for (Integer key : keySet) {
            if (map.get(key) > 1) {
                stringBuilder.append("重复数字" + key);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 排序优化
     * 2, 3, 1, 0, 2, 5, 3
     * 0  1  2  3  4  5  6
     * @param nums
     * @return
     */
    private static void findRe2(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] != i) {          // 一次判断
                int temp=nums[i];
                nums[i]=nums[temp];
                nums[temp]=temp;
                if (nums[i] == nums[nums[i]]) {      //二次判断
                    System.out.println("重复数字" + nums[i] );
                    break;
                }
            }
        }

    }

}

```

## 2.不修改数组找出重复的数字🎉

**题目描述** ：不修改数组找出重复的数字。

在一个长度为**n+1的数组里的所有数字都在1到n的范围内**，所以数组中至少有一个数字是重复的。请找出数组中任意一个重复的数字，但不能修改输入的数组。例如，如果输入长度为8的数组{2, 3, 5, 4, 3, 2, 6, 7}，那么对应的输出是重复的数字2或者3。

**解题思路**

- 　数组长度为n+1，而数字只从1到n，说明必定有重复数字。将元素组的每一个值放入到一个新数组里面，对应存放新数组下标就是该值，如果遇到当前位置已经存放了一个值，于是可以判断出该值重复。**时间复杂度O(n),空间复杂度O（n）。**
- 　可以由二分查找法拓展：把1-n的数字从中间数字m分成两部分，若前一半1-m的数字数目超过m个，说明重复数字在前一半区间，否则，在后半区间m+1~n。每次在区间中都一分为二，知道找到重复数字。**时间复杂度O(nlogn),空间复杂度O（1）。**

**参考代码**
一

```java
package offer;

/**
 * n+1数组里面  范围都在1~n     如 8 {2, 3, 5, 4, 3, 2, 6,7} 找出任意一个重复数字
 */
public class Offer3_2 {
    static int nums[] = {2, 3, 5, 4, 3, 2, 5, 7};

    public static void main(String[] args) {
        findRe1(nums);
    }

    /**
     * 开辟新数组  存入对应值对应为  判断是否存在  存在则重复
     *
     * @param nums
     */
    private static void findRe1(int[] nums) {
        int newnums[] = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (newnums[nums[i]] != nums[i]) {
                newnums[nums[i]] = nums[i];
            } else {
                System.out.println("重复数字" + nums[i]);
            }
        }
    }

}

```

二

```java
public class FindDuplication2 {
 
    /**
     * 找到数组中一个重复的数字
     * 返回-1代表无重复数字或者输入无效
     */
    public int getDuplicate(int[] arr) {
        if (arr == null || arr.length <= 0) {
            System.out.println("数组输入无效！");
            return -1;
        }
        for (int a : arr) {
            if (a < 1 || a > arr.length - 1) {
                System.out.println("数字大小超出范围！");
                return -1;
            }
        }
        int low = 1;
        int high = arr.length - 1; // high即为题目的n
        int mid, count;
        while (low <= high) {
            mid = ((high - low) >> 2) + low;
            count = countRange(arr, low, mid);
            if (low == high) {
                if (count > 1)
                    return low;
                else
                    break; // 必有重复，应该不会出现这种情况吧？
            }
            if (count > mid - low + 1) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }
 
    /**
     * 返回在[low,high]范围中数字的个数
     */
    public int countRange(int[] arr, int low, int high) {
        if (arr == null)
            return 0;
 
        int count = 0;
        for (int a : arr) {
            if (a >= low && a <= high)
                count++;
        }
        return count;
    }
 
    // ==================================测试代码==================================
    /**
     *数组为null
     */
    public void test1() {
        System.out.print("test1：");
        int[] a = null;
        int dup = getDuplicate(a);
        if (dup >= 0)
            System.out.println("重复数字为：" + dup);
    }
 
    /**
     *数组数字越界
     */
    public void test2() {
        System.out.print("test2：");
        int[] a = { 1, 2, 3, 4 };
        int dup = getDuplicate(a);
        if (dup >= 0)
            System.out.println("重复数字为：" + dup);
    }
 
    /**
     *数组带重复数字
     */
    public void test3() {
        System.out.print("test3：");
        int[] a = { 1, 2, 3, 2, 4 };
        int dup = getDuplicate(a);
        if (dup >= 0)
            System.out.println("重复数字为：" + dup);
    }
 
    public static void main(String[] args) {
        FindDuplication2 f2 = new FindDuplication2();
        f2.test1();
        f2.test2();
        f2.test3();
    }
}
```

## 3.二维数组中的查找

### 题目描述

在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。

### 解题思路

- 我记得在leetcode上也有这么一道题“**240. 搜索二维矩阵 II**”，最开始用了暴力搜索发现超时，最后我写了一个将数组分为两半查找过了。
- 二维数组是有序的，从右上角来看，向左数字递减，向下数字递增。
  因此从右上角开始查找，
  - 当要查找数字比右上角数字大时，下移；
  - 当要查找数字比右上角数字小时，左移；
  - 如果出了边界，则说明二维数组中不存在该整数。

#### 最优算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505150909647.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

##### 参考代码：

```java
package offer;

/**
 * 二维数组查找 左到右变大  上到下变大  找右上角  小与列则删一列大于列则删一行
 */
public class Offer4 {
    /*
     * 1  2  8  9
     * 2  4  9  12
     * 4  7  10 13
     * 6  8  11 15
     */
    public static void main(String[] args) {
        int array[][] = new int[4][4];
        array[0][0] = 1;
        array[0][1] = 2;
        array[0][2] = 8;
        array[0][3] = 9;
        array[1][0] = 2;
        array[1][1] = 4;
        array[1][2] = 9;
        array[1][3] = 12;
        array[2][0] = 4;
        array[2][1] = 7;
        array[2][2] = 10;
        array[2][3] = 13;
        array[3][0] = 6;
        array[3][1] = 8;
        array[3][2] = 11;
        array[3][3] = 15;
        System.out.println(find(array, 7));
        System.out.println(find(array, 5));
    }

    private static boolean find(int[][] arr, int keyNumber) {
        if (arr.length == 0) {
        } else {
            int column = arr[0].length - 1;     // 列  最右的那列
            int row = 0;                        // 行
            while (column >= 0 && row < arr.length) {
                if (arr[row][column] == keyNumber) {
                    return true;
                } else if (arr[row][column] > keyNumber) {
                    column--;
                } else {
                    row++;
                }
            }
        }
        return false;
    }


}

```

领口上侥幸通过的写法！?

```java
class Solution {
    public  boolean searchMatrix(int[][] matrix, int target) {

		for (int i = 0; i < matrix.length / 2; i++) { // 行
			for (int j = 0; j < matrix[i].length; j++) { // 列
				if (target == matrix[i][j])
					return true;
			}

		}
		for (int i = matrix.length-1; i >= matrix.length / 2; i--) { // 行
			for (int j = 0; j < matrix[i].length; j++) { // 列
				if (target == matrix[i][j])
					return true;
			}

		}

		return false;
	}
}
```

## 4.替换空格


### 题目描述

请实现一个函数，将字符串的每个空格替换为"%20"。例如输入"We are happy",则输出"We%20are%20happy."。

### 解题思路

- 如果有足够空间，老老实实向后遍历遇见空格把后面的字符都往后移动两个字节。**时间复杂度O(n^2)**
  ![加粗样式](https://img-blog.csdnimg.cn/20190505152515646.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
  假设字符串的长度是n。对每个空格字符，需要移动后面O(n)个字符，因此对含有O(n)个空格字符的字符串而言总的时间效率是O(n2)。

- 从后往前遍历遇见空格替换，遍历结束后反向输出。
- 先遍历一次字符串，这样就能统计出字符串中空格的总数，并可以由此计算出替换之后的字符串的总长度。从字符串的后面开始复制和替换。**时间复杂度O（n）**

#### 最优算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019050515224595.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

##### 参考代码：

```java
public static void ReplaceBlank(char[] target, int maxLength)
    {
        if (target == null || maxLength <= 0)
        {
            return;
        }

        // originalLength 为字符串target的实际长度
        int originalLength = 0;
        int blankCount = 0;
        int i = 0;

        while (target[i] != '\0')
        {
            originalLength++;
            // 计算空格数量
            if (target[i] == ' ')
            {
                blankCount++;
            }
            i++;
        }

        // newLength 为把空格替换成'%20'之后的长度
        int newLength = originalLength + 2 * blankCount;
        if (newLength > maxLength)
        {
            return;
        }

        // 设置两个指针，一个指向原始字符串的末尾，另一个指向替换之后的字符串的末尾
        int indexOfOriginal = originalLength;
        int indexOfNew = newLength;

        while (indexOfOriginal >= 0 && indexOfNew >= 0)
        {
            if (target[indexOfOriginal] == ' ')
            {
                target[indexOfNew--] = '0';
                target[indexOfNew--] = '2';
                target[indexOfNew--] = '%';
            }
            else
            {
                target[indexOfNew--] = target[indexOfOriginal];
            }

            indexOfOriginal--;
        }
    }

```

Low的写法！?

```java
package offer;


/**
 * 替换空格 换 ‘’ -> %20
 * <p>
 * 题目描述： 请实现一个函数，将字符串的每个空格替换为"%20"。
 * 例如输入"We are happy",则输出"We%20are%20happy."。
 */
public class Offer5 {

    public static void main(String[] args) {
        String str1 = "We are happy";

        System.out.println(myReplace(str1));
        System.out.println(myReplace2(str1));
    }

    /**
     * 1. 无脑写  不算算法  简直不要脸
     *
     * @param str1
     * @return
     */
    private static String myReplace(String str1) {

        String s = str1.replaceAll(" ", "%20");
        return s;

    }

    public static String myReplace2(String str) {
        if(str==null){
            return  null;
        }
        StringBuffer res = new StringBuffer();
        int len = str.length() - 1;
        for (int i = len; i >= 0; i--) {
            if (str.charAt(i) == ' ')
                res.append("02%");
            else
                res.append(str.charAt(i));
        }
        return res.reverse().toString();
    }

}

```

## 5.从尾到头打印链表🎉


### 题目描述

输入一个链表的头节点，从尾到头反过来打印出每个节点的值。

### 解题思路

- 借助栈这种数据结构，将链表的值读入栈中，由于栈先入后出的原则。将栈中的值弹出就实现了链表的逆序打印。
- 递归实现，当需要读一个节点的值得时候先读取下一个节点得值。**递归在本质上就是一个栈结构。**
- 直接将链表结构改变实现反转。后续...

#### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505154130273.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

##### 参考代码：

```java
package offer;

import java.util.Stack;

/**
 * 输入一个链表的头结点，从尾到头反过来打印出每个结点的值.
 * 1.借助栈结构实现
 * 2.借助递归算法 去查询每一个节点的下一个节点  直到查到位置
 */
public class Offer6 {
    /**
     * @param args
     */
    public static void main(String[] args) {
        //输入的链表有多个结点
        Offer6 linkedOffer6 = new Offer6();
        LinkNode<Integer> node1 = new LinkNode<Integer>();
        LinkNode<Integer> node2 = new LinkNode<Integer>();
        LinkNode<Integer> node3 = new LinkNode<Integer>();
        node1.node_value = 1;
        node2.node_value = 2;
        node3.node_value = 3;
        node1.next = node2;
        node2.next = node3;
        /**
         * 1.借助栈实现
         */
        // linkedOffer6.revers(node1);

        /**
         * 递归实现
         */
        linkedOffer6.revers2(node1);

    }

    /**
     * 借助栈空间
     * @param linkNode
     */
    public static void revers(LinkNode<Integer> linkNode) {
        Stack<Integer> stack = new Stack<Integer>();
        while (linkNode != null) {
            stack.push(linkNode.node_value);
            linkNode = linkNode.next;
        }
        while (!stack.empty()) {
            System.out.println(stack.pop());
        }

    }

    /**
     * 递归
     * @param linkNode
     */
    public static void revers2(LinkNode<Integer> linkNode) {
        if (linkNode!=null) {
            if (linkNode.next != null) {
                revers2(linkNode.next);
            }
            System.out.println(linkNode.node_value);
        }
    }

    /**
     * 模拟链表数据结构
     *
     * @param <E>
     */
    static class LinkNode<E> {
        LinkNode<E> next;
        E node_value;
    }
}

```

## 6.重建二叉树

### 题目描述

输入某二叉树的前序遍历和中序遍历结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不包含重复的数字。例如输入前序遍历序列:{1,2,4,7,3,5,6,8}和中序遍历{4,7,2,1,5,3,8,6},则重建出图中所示二叉树并且输出它的头结点。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505201952164.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

### 解题思路

- 先根据前序遍历序列的第一个数字创建**根结点**，接下来在中序遍历序列中找到根结点的位置，根节点的左边就是左子树，右边就是右子树，这样就能确定左、右子树结点的数量。随后**递归**创建左右子树。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505202006795.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505203132470.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 输入某二叉树的前序遍历和中序遍历结果，请重建出该二叉树。
 * 假设输入的前序遍历和中序遍历的结果中都不包含重复的数字。
 * 例如输入前序遍历序列:{1,2,4,7,3,5,6,8}和中序遍历{4,7,2,1,5,3,8,6},
 * 则重建出图中所示二叉树并且输出它的头结点。
 */
public class Offer7 {
    BinaryTreeNode binaryTreeNode = null;
    public static void main(String[] args) {
        int preOrder[] = {1, 2, 4, 7, 3, 5, 6, 8};
        int inOrder[] = {4, 7, 2, 1, 5, 3, 8, 6};
        Offer7 constructBinaryTree = new Offer7();
        BinaryTreeNode node = constructBinaryTree.construct(preOrder, inOrder, preOrder.length);
        // 前序打印
        printPreBinaryTree(node);
    }

    /**
     * 初始化判断
     *
     * @param preOrder
     * @param inOrder
     * @param prelength
     * @return
     */
    public BinaryTreeNode construct(int preOrder[], int inOrder[],
                                    int prelength) {

        if (prelength == 0 || preOrder == null || inOrder == null) {
            return null;
        } else {
            binaryTreeNode = coreConstruct(preOrder, 0, preOrder.length - 1, inOrder, 0, inOrder.length - 1);
        }
        return binaryTreeNode;
    }

    /**
     * 区分左右子树，找出根节点核心
     *
     * @param preOrder
     * @param startPreIndex
     * @param endPreIndex
     * @param inOrder
     * @param startInIndex
     * @param endInIndex
     * @return
     */
    public BinaryTreeNode coreConstruct(int[] preOrder, int startPreIndex, int endPreIndex, int[] inOrder, int startInIndex, int endInIndex) {
        int rootPreValue = preOrder[startPreIndex];
        /**
         *首先构建一颗只有根节点的树
         */
        BinaryTreeNode rootNode = new BinaryTreeNode(rootPreValue);
        /**
         * 只有一个元素的情况 先序遍历和中序遍历都只有一个元素
         */
        if (startPreIndex == endPreIndex && startInIndex == endInIndex
                && preOrder[startInIndex] == inOrder[endInIndex]) {
            System.out.println("只有一个元素");
            return rootNode;
        } else {
            /**
             * 在中序遍历中找到根节点  区分左右子树 分别递归创建   核心（⭐）
             */
            int rootInIndex = startInIndex;
            while (rootInIndex <= endInIndex && inOrder[rootInIndex] != rootPreValue) {
                rootInIndex++;
            }

            // 左子树长度
            int letfTreelength = rootInIndex - startInIndex;
            // 左子树右端值
            int leftPreEndIndex = startPreIndex + letfTreelength;
            // 构建左子树
            if (letfTreelength > 0) {
                rootNode.leftTree = coreConstruct(preOrder, startPreIndex + 1,
                        leftPreEndIndex, inOrder, startInIndex, rootInIndex - 1);
            }
            // 如果左子树长度小于 先序遍历的元素个数  说明存在右子树
            if (letfTreelength < endPreIndex - startPreIndex) {
                rootNode.rightTree = coreConstruct(preOrder, leftPreEndIndex + 1,
                        endPreIndex, inOrder, rootInIndex + 1, endInIndex);
            }
            return rootNode;
        }
    }

    // 按照前序遍历打印二叉树的节点
    public static void printPreBinaryTree(BinaryTreeNode root) {
        if (root == null) {
            return;
        } else {
            System.out.println(root.node_value + " ");
        }
        if (root.leftTree != null) {
            printPreBinaryTree(root.leftTree);
        }
        if (root.rightTree != null) {
            printPreBinaryTree(root.rightTree);
        }
    }
}

/**
 * 二叉树数据结构
 */
class BinaryTreeNode {
    int node_value;
    BinaryTreeNode leftTree;
    BinaryTreeNode rightTree;

    /**
     * 创建对象的时候初始化出一个无左右子节点的节点
     *
     * @param node_value
     */
    BinaryTreeNode(int node_value) {
        this.leftTree = null;
        this.rightTree = null;
        this.node_value = node_value;
    }
}
```

## 7.二叉树的下一个节点

### 题目描述

给定一个二叉树和其中的一个结点，请找出中序遍历顺序的下一个结点并且返回。注意，树中的结点不仅包含左右子结点，同时包含指向**父结点的指针father**。

![**在这里插入图片描述](https://img-blog.csdnimg.cn/20190505203458640.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
该中序遍历为：d->b->h->e->i->a->f->c->g

### 解题思路

- 如果输入的 node 节点**有**右孩子
  下一个结点就是以node结点的右子树中最左子节点。如b那么下一个节点就应该是h。
- 如果输入的node节点**没有**右孩子
  - node节点是其父结点的左孩子，那么下一个节点就是node的父节点。如f的下一个节点为c
  - node节点是父节点的右孩子，那么下一个节点就是往上找直到某个节点是他的父节点的左孩子，此时该节点的父节点就是node节点的下一个节点。如i的下一个节点就是a。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505204221556.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;


public class Offer8 {
    public static void main(String[] args) {
        //            1
        //          // \\
        //         2     3
        //       // \\
        //      4     5
        //    inorder->42513
        TreeLinkNode root = new TreeLinkNode(1);

        root.left = new TreeLinkNode(2);
        root.left.father =root;
        root.right = new TreeLinkNode(3);
        root.right.father =root;
        root.left.left = new TreeLinkNode(4);
        root.left.left.father =root.left;
        root.left.right = new TreeLinkNode(5);
        root.left.right.father =root.left;
        System.out.println(GetNext(root.left.right ).val);
    }

    public static TreeLinkNode GetNext(TreeLinkNode pNode) {
        /*这里需要注意的是pNode.next是pNode结点的父结点
        1、如果有右子树，那么下一个结点就是右子树最左边的节点。
        2、如果没有右子树，分两种情况，如果该结点的为父结点的左孩子，则该结点的父节点pNode.next则为
        该结点的下一个结点。
        第二种情况则是如果该结点的为父节点的右孩子，则向上找父节点，直到父节点为该父节点的左孩子，则该父节点的父节点
        为下一个结点。
        */
        if (pNode == null) {
            return null;
        }
        if (pNode.right != null) {//1、如果有右子树，那么下一个结点就是右子树最左边的节点。
            pNode = pNode.right;
            while (pNode.left != null) pNode = pNode.left;
            return pNode;
        }
        while (pNode.father !=null) {//这个则是在没有右子树的情况下，求下一个结点。
            if (pNode.father.left == pNode) {
                return pNode.father;
            }
            pNode = pNode.father;
        }
        return pNode;
    }
}

class TreeLinkNode {
    int val;
    TreeLinkNode left = null;
    TreeLinkNode right = null;
    TreeLinkNode father = null;

    TreeLinkNode(int val) {
        this.val = val;
    }
}

```

## 8.用两个栈实现队列🎉

### 题目描述

用两个栈实现一个队列。队列的声明如下，请实现它的两个函数appendTail和deleteHead,分别完成在队列尾部插入结点和在队列头部删除结点的功能

### 解题思路

- 当stack2不为空时，直接弹栈。当stack2为空得时候，将stack1中得剩余元素压入stack2。循环往复。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505154803861.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

import java.util.Stack;

/**
 * 用两个栈实现一个队列。队列的声明如下，
 * 请实现它的两个函数appendTail和deleteHead,
 * 分别完成在队列尾部插入结点和在队列头部删除结点的功能
 */
public class Offer9 {
    static Stack<String> stack1 = new Stack<String>();
    static Stack<String> stack2 = new Stack<String>();

    public static void main(String[] args) {
        Offer9.appendTail("xl");
        for (int i = 1; i <= 10; i++) {
            appendTail(i + "");
        }
        System.out.println(Offer9.deleteHead());
    }

    public static void appendTail(String string) {
        stack1.push(string);
    }

    public static String deleteHead() {
        if (stack2.isEmpty()) {
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }
        }
        return stack2.pop();
    }
}


```

## 9.用两个队列实现栈🎉


### 题目描述

用两个队列实现一个栈。

### 解题思路

- 由于队列有先入先出的特性，所以当队列一存入数值**需要弹出栈顶元素C时将队列元素个数减一的所有元素存入队列2同时队列1中元素出队**，实现一个后入先出，以此左右往复直到队列为空，抛出异常。

#### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505155328301.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 两个队列实现栈  先入后出
 */
public class Offer9_2 {
    static Queue<String> queue1 = new LinkedBlockingQueue<String>();
    static Queue<String> queue2 = new LinkedBlockingQueue<String>();

    public static void main(String[] args) {
        queue1.offer("xl");
        queue1.offer("xa");
        queue1.offer("xb");
        queue1.offer("xc");
        queue1.offer("xd");
        System.out.println(Offer9_2.deleteHead());
        System.out.println(Offer9_2.deleteHead());
        System.out.println(Offer9_2.deleteHead());
        System.out.println(Offer9_2.deleteHead());

    }

    private static String deleteHead() {
        if (queue2.isEmpty()) {
            int count = queue1.size();
            for (int i = 0; i < count - 1; i++) {
                queue2.offer(queue1.poll());
            }
            return queue1.poll();
        } else {
            int count = queue2.size();
            for (int i = 0; i < count - 1; i++) {
                queue1.offer(queue2.poll());
            }
            return queue2.poll();
        }


    }
}

```

## 10.斐波拉契数列🎉


### 题目描述

写一个函数，输入n，求斐波那契数列的第n项，斐波那契数列的定义如下： n=0,f(n)=0 ;n=1,f(n)=1 n>1;f(n)=f(n-1)+f(n-2).

### 解题思路

- 使用递归思想，当需要计算f（9）时先计算f（8）+f（7）。

- 正向求解减少重复计算。计算根据f（0），f(1)计算f（2）直到f(n)。时间复杂度O（n）

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505160126639.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
从上图中不难发现：在这棵树中有很多结点是重复的，而且重复的结点数会随着n的增大而急剧增加，这意味计算量会随着n的增大而急剧增大。事实上，用递归方法计算的时间复杂度是以**n的指数**的方式递增的。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190505160706995.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 写一个函数，输入n，求斐波那契数列的第n项，斐波那契数列的定义如下： n=0,f(n)=0 ;n=1,f(n)=1 n>1;f(n)=f(n-1)+f(n-2).
 */
public class Offer10 {

    public static void main(String[] args) {
        System.out.println("low:"+LowFibonacci(3));
        System.out.println("high:"+HFibonacci(3));
    }

    public static int LowFibonacci(int n) {
        if (n <= 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return LowFibonacci(n - 1) + LowFibonacci(n - 2);
    }

    /**
     * 效率高  减少重复计算
     *
     * @param n
     * @return
     */
    public static int HFibonacci(int n) {
        if (n <= 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        int one = 0;
        int two = 1;
        int fib = 0;
        for (int i = 2; i <= n; i++) {
            fib = one + two;
            two=fib;
            one=two;
        }
        return fib;
    }
}
```

## 11.旋转数组中的最小数字🎉


### 题目描述

把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。输入一个递增排序的数组的一个旋转，输出旋转数组的最小元素。例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1;

>利用旋转数组的特性解题！

### 解题思路

- 根据旋转数组的特性我们可以看出再最小数字前面的元素都大于等于后面的元素，于是利用这一特性来进行一个**二分查找**。
  - 定义三个指针一个指向头，一个指向尾，一个指向中间
  - 如果头元素大于等于尾指针元素，则判断中间元素指针是否是大于头指针指向的元素或者是（小于）尾指针指向的元素，将mid赋值给first或者end，进行查找范围减半。
  - 直到first和end相邻的时候，end指向的就是最小元素

>注意代码鲁棒性：可能出现三个指针指的元素值相等，那么我们只能顺序查找。
>
>>如果数组为空或者是一个就是最小元素我们需要注意处理。

##### 算法图解

一
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509203904380.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
二
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509203943849.png)

#### 参考代码：

```java
package offer;

/**
 * 旋转数组最小的数字
 * 把一个数组最开始的若干个元素搬到数组的末尾，我们称之为数组的旋转。
 * 输入一个递增排序的数组的一个旋转，输出旋转数组的最小元素。例如数组{3,4,5,1,2}为{1,2,3,4,5}的一个旋转，该数组的最小值为1;
 */
public class Offer11 {

    public static void main(String[] args) {
        int nums[] = {1,0,1,1,1};
        System.out.println(findMin(nums));
    }

    static int findMin(int nums[]) {
        int first = 0;
        int mid = first;
        // 空处理
        if (nums == null) {
            try {
                throw new NullPointerException();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int end = nums.length - 1;
            while (nums[first] >= nums[end]) {
                if (end - first == 1) {
                    mid = end;
                    break;
                }
                mid = (end + first) / 2;
                // 三个指针指向的值相等
                if(nums[first]==nums[end]&&nums[first]==nums[mid]){
                    return MinInOrder(nums,first,end);
                }
                if (nums[mid] >= nums[first]) {
                    first = mid;
                } else {
                    end = mid;
                }
            }
            return nums[mid];//  如果上面的while没走的话意味着第一个就是最小的  所以mid=first 这是一个细节处理
        }
        return 0;
    }
// 顺序查找
    private static int MinInOrder(int[] nums, int first, int end) {
        int result=nums[first];
        for (int i=first+1;i<=end;i++){
            if(result>nums[i]){
                result=nums[i];
            }
        }
        return result;
    }
}

```

## 12.矩阵中的路径


### 题目描述

请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。路径可以从矩阵中的任意一个格子开始，每一步可以在矩阵中向左，向右，向上，向下移动一个格子。如果一条路径经过了矩阵中的某一个格子，则该路径不能再进入该格子。 例如在下面的3x4的矩阵中包含一条字符串"bcced"的路径（路径中的字母用斜体表示）。但是矩阵中不包含"abcb"路径，因为字符串的第一个字符b占据了矩阵中的第一行第二个格子之后，路径不能再次进入这个格子。

>我就觉得这递归思想就是一个反人类的思想！

回溯法找路径，可以看作下面的一个树结构，在每个满足条件的节点，寻找到相邻的节点，然后在相邻节点里面找个满足第i步的元素，如果找不到就回退一步。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190513205034210.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

### 解题思路

- 遍历矩阵找到一个入口。
- 定义一个数组用于储存当前节点是否遍历过的信息。
- 寻找相邻节点是否有满足条件的节点。
  - 下面代码中，当矩阵坐标为（row，col）的格子和路径字符串中下标为pathLength的字符一样时，从4个相邻的格子（row，col-1）、（row-1，col）、（row，col+1）以及（row+1，col）中去定位路径字符串中下标为pathLength+1的字符。
  - 如果4个相邻的格子都没有匹配字符串中下标为pathLength+1的字符，表明当前路径字符串中下标为pathLength的字符在矩阵中的定位不正确，我们需要回到前一个字符串（pathLength-1），然后重新定位。

一直重复这个过程，直到路径字符串上所有字符都在矩阵中找到格式的位置（此时pathLength>startch.length-1 ）。
	

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190513210219465.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

- 注意代码中对于结束递归的条件的设定！
- 是否访问数组的赋值！

#### 参考代码：

```java
package offer;

import java.util.Arrays;

public class Offer12 {
    public static void main(String[] args) {
        char nums[][] = {{'a', 'b', 't', 'g'}, {'c', 'f', 'c', 's'}, {'j', 'd', 'e', 'h'}};
        int rows = nums.length;
        int cols = nums[0].length;
        char startch[] = {'b', 'f', 'c', 'h'};
        boolean tag = hashPath(nums, rows, cols, startch);
        if (tag) {
            System.out.println("存在路径");
        } else {
            System.out.println("不存在路径");
        }

    }

    static boolean hashPath(char nums[][], int rows, int cols, char startch[]) {

        if (nums == null || rows <= 1 || cols <= 1 || startch == null) {
            return false;
        }
        boolean visited[] = new boolean[rows * cols];
        Arrays.fill(visited, false);
        int pathLength = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (hashPathCore(nums, i, j, rows, cols, startch, pathLength, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hashPathCore(char[][] nums, int row, int col, int rows, int cols, char[] startch, int pathLength, boolean[] visited) {
        if (pathLength>startch.length-1 ) {
            return true;
        }
        boolean hasPath = false;
        if (row < rows && col < cols && col >= 0 && row >= 0 && nums[row][col] == startch[pathLength] && !visited[row * cols + col]) {
            ++pathLength;
            visited[row * cols + col] = true;
            hasPath = hashPathCore(nums, row - 1, col, rows, cols, startch, pathLength, visited)
                    || hashPathCore(nums, row + 1, col, rows, cols, startch, pathLength, visited)
                    || hashPathCore(nums, row, col - 1, rows, cols, startch, pathLength, visited)
                    || hashPathCore(nums, row, col + 1, rows, cols, startch, pathLength, visited);
            if (!hasPath) {
                --pathLength;
                visited[row * cols + col] = false;
            }
        }
        return hasPath;
    }
}


```

## 13.机器人的运动范围



### 题目描述

地上有一个m行和n列的方格。一个机器人从坐标0,0的格子开始移动，每一次只能向左，右，上，下四个方向移动一格，但是不能进入行坐标和列坐标的数位之和大于k的格子。 例如，当k为18时，机器人能够进入方格（35,37），因为3+5+3+7 = 18。但是，它不能进入方格（35,38），因为3+5+3+8 = 19。请问该机器人能够达到多少个格子？


### 解题思路

- 本题使用的方法同样还是回溯法，另外还需要会计算给定整数上的各个位上数之和。
- 使用一个访问数组记录是否已经经过该格子。
- 机器人从(0,0)开始移动，当它准备进入(i,j)的格子时，通过检查坐标的数位来判断机器人是否能够进入。
- 如果机器人能进入(i,j)的格子，接着在判断它是否能进入四个相邻的格子(i,j-1),(i,j+1),(i-1,j),(i+1,j)。



##### 算法图解

[矩阵中的路径](https://blog.csdn.net/u011583316/article/details/90181317)
![加粗样式](https://img-blog.csdnimg.cn/20190524184052209.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package Solution54;
//地上有一个m行和n列的方格。
// 一个机器人从坐标0,0的格子开始移动，每一次只能向左，右，上，下四个方向移动一格，但是不能进入行坐标和列坐标的数位之和大于k的格子。
// 例如，当k为18时，机器人能够进入方格（35,37），因为3+5+3+7 = 18。但是，它不能进入方格（35,38），因为3+5+3+8 = 19。请问该机器人能够达到多少个格子？
public class Solution {
    //回溯法
    public int movingCount(int threshold, int rows, int cols) {
        if(rows<=0||cols<=0||threshold<0)   return 0;
        int[] visited=new int[rows*cols];
        return MovingCount(threshold,rows,cols,0,0,visited);
    }
 
    private int MovingCount(int threshold,int rows,int cols,int row,int col,int[] visited){
        int count=0;
        if(canWalkInto(threshold, rows, cols, row, col, visited)){
            visited[row*cols+col]=1;
            count=1+MovingCount(threshold,rows,cols,row-1,col,visited)   //往上
                    +MovingCount(threshold,rows,cols,row+1,col,visited)  //往下
                    +MovingCount(threshold, rows, cols, row, col-1, visited)   //往左
                    +MovingCount(threshold, rows, cols, row, col+1, visited);  //往右
        }
        return count;
    }
 
    private boolean canWalkInto(int threshold,int rows,int cols,int row,int col,int[] visited){
        if(row>=0 && row<rows && col>=0 && col<cols
                && getSumOfDigits(row)+getSumOfDigits(col)<=threshold
                && visited[row*cols+col]==0)
            return true;
        else
            return false;
    }
 
    private int getSumOfDigits(int number){
        int sum=0;
        while(number!=0){
            sum+=number%10;
            number/=10;
        }
        return sum;
    }
}

```

## 14.剪绳子

### 题目描述

给定一根长度为n的绳子，请把绳子剪成m段，每段绳子记为k[0],k[1]……k[m]。请问k[0]*k[1]……*k[m]可能的最大乘积是多少？例如：当绳子长度为8时，我们把它剪成长度分别为2,3,3段，此时最大乘积为18.


### 解题思路

- 先用常规的需要O(n^2)时间和O(n)空间的动态规划的思路，接着用只需要O(1)时间和空间的贪婪算法来分析解决这个问题。

  - 动态规划

  　首先定义函数f(n)为把长度为n的绳子剪成若干段后各段长度乘积的最大值。在剪第一刀的时候，我们有n-1种可能的选择，也就是剪出来的第一段绳子的可能长度为1，2，...n-1。因此f(n)=max(f(i)xf(n-i))，其中0<i<n.

  这是一个从上至下的递归公式。由于递归会有很多重复的子问题，从而有大量不必要的重复计算。一个更好的办法是按照从下而上的顺序计算，也就是说我们先得到f(2)、f(3)，再得到f(4)、f(5)，直到得到f(n)。
  当绳子的长度为2时，只可能剪成长度为1的两段，因此f(2)等于1.当绳子的长度为3时，可能把绳子剪成长度为1和2的两段或者长度都为1的三段，由于1x2>1x1x1，因此f(3)=2

  - 贪心算法
    当n = 4时，最大乘积就是4.
    当n >= 5时，尽可能多剪长度为3的绳子，当剩下为4的时候，就剪成两段2
    也就是说，n>=5时，最大乘积都由若干个3，最多两个2构成的
    证明很简单：
    n >= 5时，3(n-3) >= 2(n-2) > n  


##### 算法图解

自我感觉还是重点理解这个动态规划吧，对于后面的贪心如果面试换题，可能一时也想不出来！
![加粗样式](https://img-blog.csdnimg.cn/20190524184052209.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

代码里面有几个**不好理解**的点：

-	i / 2

>上到下和下到上都涉及了一个i/2，这个东西的实质意义也就是保证计算了1*4不会再计算4*1这样的过程
>
>-   product[1] = 1;
>        product[2] = 2; 
>        product[3] = 3; 
>   这段实质上是为后面第二个for循环方便处理的。虽然 product[i]记录的是长度为i的最大值，但在这一小段并不是这个含义。而是再计算 product[4]的时候去找这里面的能求出最大值的组合。如product[1]*product[3]  product[2]*product[2]。在product[4]往后则都是记录的最优解。 

- 对于长度为1，2，3

>直接给出了最优解分别是0，1，2。注意和上面这个存放最优解的数组区分。

#### 参考代码：

```java
package offer;

import static java.lang.StrictMath.pow;

/**
 * 剪绳子
 */
public class Offer14 {
    public static void main(String[] args) {
        int length = 10;
        System.out.println(maxProductAfterCuttingTtoD(length));
        System.out.println(maxProductAfterCuttingDtoT(length));
        System.out.println(maxProductAfterCuttingActive(length));
    }


    /**
     * 从上到下动态规划
     *
     * @return
     */
    static int maxProductAfterCuttingTtoD(int length) {
        int a[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        if (length < 2)
            return 0;
        if (length == 2)
            return 1;
        if (length == 3)
            return 2;
        int i = 4;
        while (i <= length) {
            int j = 1;
            while (j <= i / 2) {
                if (a[j] * a[i - j] > a[i])
                    a[i] = a[j] * a[i - j];
                j++;
            }
            i++;
        }
        return a[length];
    }

    /**
     * 从下到上动态规划
     *
     * @param length
     */
    private static int maxProductAfterCuttingActive(int length) {
        if (length < 2) return 0;
        else if (length == 2) return 1;
        else if (length == 3) return 2;
        else if (length >= 4) {
            int product[] = new int[length + 1];
            product[0] = 0; //这个其实写不写都行，后面的代码也用不到这个
            product[1] = 1; //这个也用不到
            product[2] = 2; //这里的 2 指的是剩下了一段长度为 2 的绳子，可以不剪
            product[3] = 3; //这里的 3 指的是剩下了一段长度为 3 的绳子，可以不剪
            /**
             * 方便后面第二个for循环计算最大值
             */
            int max = 0;
            for (int i = 4; i <= length; ++i) {
                max = 0;
                for (int j = 1; j <= i / 2; ++j) //从 1开始比较
                {
                    if (max < product[j] * product[i - j])
                        max = product[j] * product[i - j]; //比较出最大的那个情况
                }
                product[i] = max; //记录下来
            }
            return product[length]; //这个时候从 0 到 n 的最优情况都记录下来了

        }
        return 0;
    }


    /**
     * 贪心算法
     *
     * @param length
     */
    private static int maxProductAfterCuttingDtoT(int length) {

        if (length < 2)
            return 0;
        if (length == 2)
            return 1;
        if (length == 3)
            return 2;
        int timesOf3 = length / 3;    //剪长度为3的绳子 段数
        if ((length - timesOf3 * 3) == 1)   //当长度为4的时候不能去用3剪
            timesOf3 = timesOf3 - 1;
        int timesOf2 = (length - timesOf3 * 3) / 2;     // 这时应该把绳子剪成长度为2的两段：2*2>1*3
        return ((int) (pow(3, timesOf3)) * ((int) pow(2, timesOf2)));

    }
}

```

## 15.二进制中1的个数🎉

>补充知识：位运算

- 基础与、或、异或运算

| 与（\&） | 或（\|） | 异或（^）    |
| -------- | -------- | ------------ |
| 0&0=0    | 0\|0=0   | 0^0=0        |
| 0&1=0    | 1\|0=1   | 1^0=1        |
| 1&0=0    | 0\|1=1   | 0^1=1        |
| 1&1=1    | 1\|1=1   | 1^1=0        |
| 有0则0   | 有1则1   | 同出0不同出1 |

- 左移

>左移n位最左边的n为直接丢弃，右边补n个0.

00001010<<2=00101000

- 右移

>正数：直接右边n个0抛弃，左边补0；
>负数：直接右边n个0抛弃，左边补上n个1；

00001010>>2=00000010
10001010>>3=11110001

### 题目描述

 请实现一个函数，输入一个整数，输出该数二进制表示中1的个数。例如把9表示成二进制是1001，有2位是1 因此如果输入9，该函数输出2;


### 解题思路

-  将正数和1做与运算，如果为1则记录加1（循环一次整数右移一次）。可能引起死循环。
-  将1来左移，如果与运算为1就记录加1，直到结果为0.。对于int这样的数据类型(32位 左移32次  因为int占4字节  也就是32位),移动次数太多，效率低。
-  将整数-1，再与原数据做与运算，运算一次消耗掉原数据一个1，直到为0截止。只需要循环n个1次。

##### 算法图解

第二：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190506212501524.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)
第三：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190506212756230.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 请实现一个函数，输入一个整数，输出该数二进制表示中1的个数。例如把9表示成二进制是1001，有2位是1 因此如果输入9，该函数输出2;
 */
public class Offer15 {
    public static void main(String[] args) {
        int nums = 9;
      System.out.println(NumBerOf1(nums));
        //  System.out.println(HNumBerOf1(nums));
    }
    // 可能引起死循环的解法1
    public static int numberof1_1(int n) {
        int count = 0;
        while (n != 0) {
            if ((n & 1) != 0) {
                count++;
            }
            n = n >> 1;
        }
        return count;
    }
    /**
     * 常规写法  将1 来左移 与原数据按位做与运算
     * 32位 左移32次  因为int占4字节  也就是32位
     *
     * @param n
     * @return
     */
    static int NumBerOf1(int n) {
        int count = 0;
        int flag = 1;
        while (flag != 0) {
            System.out.println((n & flag));
            if ((n & flag) != 0) {

                count++;
            }
            flag = flag << 1;
        }

        return count;
    }

    /**
     * 减1后与运算 直到等于0
     * @param n
     * @return
     */
    static int HNumBerOf1(int n) {
        int count = 0;
        while (n != 0) {
           ++count;
           n=(n-1)&n;
        }

        return count;
    }
   
}

```

## 16.数值的整数次方



### 题目描述

实现函数double Power(doublebase, int exponent)，求base的exponent次方。不得使用库函数，同时**不需要考虑大数问题**。


>**double直接比较是否相等，是两数相互做差，值小于某个极限值则认为相等！**

### 解题思路

- 循环乘法。
- 全面的考虑，当指数为负数的时候；当底数（base）是零且指数是负数的时候；0的0次方的时候。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509203904380.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509203943849.png)

#### 参考代码：

```java
package offer;

/**
 * 实现函数double power(double base,int exponent),求base的exponent次方。不能使用库函数，同时不需要考虑大数问题。
 */
public class Offer16 {
    public static void main(String[] args) {
       // System.out.println(power(2, 5));
      // System.out.println(power2(2, 0));
        System.out.println(power3(2, 5));
    }

    /**
     * 无脑版本  但是忽略了指数为负数的情况  和0^0
     *
     * @param base
     * @param exponent
     * @return
     */
    static double power(double base, int exponent) {
        double result = 1.0;
        for (int i = 1; i <= exponent; i++) {
            result *= base;
        }
        return result;
    }

    /**
     * 处理了  底数为0  指数为非正数的情况
     * @param base
     * @param exponent
     * @return
     */
    static double power2(double base, int exponent) {
        int absexponent = exponent;
        if (base - 0.0<0.000000001 && exponent < 0&&0.0-base>-0.00000001) {
            return 0.0;
        }
        if (exponent < 0) {
            absexponent = -exponent;
        }

        double result = 1.0;
        for (int i = 1; i <= absexponent; i++) {
            result *= base;
        }
        if (exponent < 0) {
            result = 1.0 / result;
        }
        return result;
    }

    /**
     * 分基欧  高效公式计算  奇数&0x1 =1 偶数&0x1=0  （0x十六进制）
     * @param base
     * @param exponent
     * @return
     */
    static double power3(double base, int exponent) {
       if(exponent==0){
           return 1;
       }
       if (exponent==1){
           return base;
       }
       double result=power3(base,exponent>>1);
       result*=result;
       if((exponent&0x1)==1){
           result*=base;
       }
       return result;
    }

}

```

## 17.打印从1到最大的n位数🎉


### 题目描述

输入数字n，按顺序打印出从1到最大的n位十进制数。比如输入3，则打印出1、2、3一直到最大的3位数999。

### 解题思路

- 一般解法，直接找到最大的n位数，然后遍历。
- 全排列用递归表达，数字的每一位都可能是0~9中的一个数，然后设置下一位。递归结束的条件是我们设置了的数字的最后一位。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509210024135.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 打印1-n的最大n为数
 */
public class Offer17 {
    public static void main(String[] args) {
        int n = 3;
        //PrintMax(n);
        PrintMax2(n);
    }


    /**
     * 先求出最大的n位数再打印
     * 大数情况下存在溢出的问题
     *
     * @param n
     */
    private static void PrintMax(int n) {
        int result = 1;
        int i = 0;
        while (i++ < n) {
            result *= 10;
        }
        for (i = 1; i < result; i++) {
            System.out.println(i);
        }
    }

    /**
     * 使用递归思想 为每一位进行排序  直到个位条件结束
     *
     * @param
     */
    private static void PrintMax2(int n) {
        if (n <= 0) {
            return;
        }
        char number[] = new char[n + 1];
        for (int i = 0; i < 10; i++) {
            number[0] = (char) (i+'0');
            Next(number, n, 0);
        }

    }

    private static void Next(char[] number, int lenght, int index) {
        int nowIndex=index + 1;
        if (index == lenght - 1) {
            PrintNumber(number);
        } else {
            for (int i = 0; i < 10; i++) {
                number[nowIndex] = (char) (i+'0');
                Next(number, lenght,nowIndex );
            }
        }
    }

    private static void PrintNumber(char[] number) {
        boolean start = true;
        for (int i = 0; i < number.length; i++) {
            if (start) {
                if (number[i] != '0') {
                    start = false;
                }
            } else {
                System.out.print(number[i]);
            }
        }
        System.out.println();
    }

}

```

## 18.删除链表节点🎉


### 题目描述

请编写一个函数，使其可以删除某个链表中给定的节点，您将只被给予要求被删除的节点。

>所以实现的思路是，根据给定的要删除的节点，可以直接找到其后年的节点，把后面的节点的内容复制到当前节点处，同时将当前节点指向其后面节点的后面节点保证链表不断开，再把下一节点删掉就相当于把给定的节点删除了。

### 解题思路

- 将要删除的节点的下一个节点直接覆盖到当前节点，然后删除掉下一个节点。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509210435466.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 删除链表节点  删除的节点信息复制到前一个节点 然后删除该节点
 */
public class Offer18 {
    public static void main(String[] args) {
        LinkNode<Integer> firstNode = new LinkNode<Integer>();
        LinkNode<Integer> node2 = new LinkNode<Integer>();
        LinkNode<Integer> node3 = new LinkNode<Integer>();
        LinkNode<Integer> node4 = new LinkNode<Integer>();
        LinkNode<Integer> node5 = new LinkNode<Integer>();
        firstNode.next = node2;
        firstNode.node_value = 1;
        node2.next = node3;
        node2.node_value = 2;
        node3.next = node4;
        node3.node_value = 3;
        node4.next = node5;
        node4.node_value = 4;
        node5.node_value = 5;
        node5.next = null;
        DeleteNode(firstNode, node5);
    }

    /**
     * 头节点  尾节点  任意位置  三种情况
     *
     * @param firstNode  链表
     * @param ToBedelete 需要被删除的
     */
    private static void DeleteNode(LinkNode<Integer> firstNode, LinkNode<Integer> ToBedelete) {
        if (firstNode == null || ToBedelete == null) {
            return;
        }
        /**
         * 头尾
         */
        if (ToBedelete == firstNode) {
            System.out.println("删除头"+firstNode.node_value);
            firstNode = null;
            return;
        }
        /**
         * 不是尾节点
         */
        System.out.println("删除非头尾"+ToBedelete.node_value);
        if (ToBedelete.next != null) {
            LinkNode<Integer> nextNode = ToBedelete.next;
            ToBedelete.node_value = nextNode.node_value;
            ToBedelete.next = nextNode.next;
            ToBedelete.next = null;

        } else {
            LinkNode pointNode = firstNode;
            while (pointNode.next != ToBedelete) {
                pointNode = pointNode.next;
            }
            System.out.println("删除尾"+pointNode.next.node_value);
            pointNode.next = null;
        }
    }
}
/**
 * 模拟链表数据结构
 *
 * @param <E>
 */
class LinkNode<E> {
    LinkNode<Integer> next;
    E node_value;
}

```

## 19.调整数组顺序使奇数位与偶数的后面🎉


### 题目描述

输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数位与数组的前半部分，所有偶数位与数组的后半部分;

### 解题思路

- 遍历数组，如果遇到一个偶数将其拿出，然后将后面的所有元素向前移动一位，将当前偶数放在最后一位。时间复杂度O（n^2）。
- 遍历数组如果遇到偶数在奇数前面，直接交换。一个指针从头开始一个指针从最后一位开始。时间复杂度O（n）。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190510195748860.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 使计数位于偶数前面
 */
public class Offer21 {
    public static void main(String[] args) {
        int nums[] = {1, 2, 3, 5, 8, 13, 6, 11};
        OrderNums(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.println(nums[i]);
        }
    }

    static void OrderNums(int[] nums) {
        // 起始和尾部指针
        int start = 0;
        int end = nums.length - 1;
        while (start < end) {
            //  第一个指针往后 直到遇到一个偶数
            while ((start < end) && (nums[start] & 0x1) != 0) {
                start++;
            }
            // 第二个指针往前 直到遇到一个奇数
            while ((start < end) && (nums[end] & 0x1) == 0) {
                end--;
            }
            if (start < end) {
                int temp = 0;
                temp = nums[start];
                nums[start] = nums[end];
                nums[end] = temp;
            }
        }

    }
}

```

## 20.链表中倒数第K个节点🎉


### 题目描述

输入一个链表，输出该链表中倒数第K个结点。为了符合大多数人的习惯，从1开始计数，即链表的尾结点是倒数第一个结点。例如一个链表有6个结点，从头结点开始它们的值依次是1、2、3、4、5、6.这个链表的倒数第三个结点是值为4的结点。



### 解题思路

- 定义两个指针一个指针先走k-1步，然后两个指针同时遍历，直到第一个指针指向尾，那么前一个指针的位置就是K。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190509211304715.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 输入一个链表，输出该链表中倒数第K个结点。为了符合大多数人的习惯，
 * 从1开始计数，即链表的尾结点是倒数第一个结点。
 * 例如一个链表有6个结点，从头结点开始它们的值依次是1、2、3、4、5、6.这个链表的倒数第三个结点是值为4的结点。
 */
public class Offer22 {
    public static void main(String[] args) {
        LinkNode<Integer> LinkNode = new LinkNode<Integer>();
        LinkNode<Integer> node2 = new LinkNode<Integer>();
        LinkNode<Integer> node3 = new LinkNode<Integer>();
        LinkNode<Integer> node4 = new LinkNode<Integer>();
        LinkNode<Integer> node5 = new LinkNode<Integer>();
        LinkNode.next = node2;
        LinkNode.node_value = 1;
        node2.next = node3;
        node2.node_value = 2;
        node3.next = node4;
        node3.node_value = 3;
        node4.next = node5;
        node4.node_value = 4;
        node5.node_value = 5;
        node5.next = null;
        int K = 5;
        System.out.println(deleteOnK(LinkNode, K));
    }

    /**
     * 指针1 先走k步  指针2位于开始位置  当1走到尾  2就是k
     *
     * @param linkNode
     * @return
     */
    private static int deleteOnK(LinkNode<Integer> linkNode, int K) {
        if(linkNode==null||K==0){
            return 0;
        }
        LinkNode<Integer> firstK = linkNode;
        LinkNode<Integer> last = null;
        for (int i = 0; i < K - 1; i++) {
            if(firstK.next==null){
                return 0;
            }
            firstK = firstK.next;
        }
        last=linkNode;
        while (firstK.next != null) {
            last = last.next;
            firstK = firstK.next;
        }
        return last.node_value;
    }
}

/**
 * 模拟链表数据结构
 *
 * @param <E>
 */
class LinkNode<E> {
    LinkNode<Integer> next;
    E node_value;
}
```

## 21.链表中环的入口结点🎉

### 题目描述

给一个链表，若其中包含环，请找出该链表的环的入口结点，否则，输出null。

### 解题思路

- 判断是否存在环
  - 定义两个指针一个走的快一个走的慢如果慢的追上了快的，则存在环。 
- 如何找到入口
  - 定义两个指针让第一个指针先走环中节点个数个，然后两个指针同时走。当他们相遇则是入口。如同：[[剑指Offer]-链表中倒数第K个节点](https://blog.csdn.net/u011583316/article/details/90048069)
- 如何确定环中节点个数
  - 当两个指针第一次相遇时，一定是在环中，那么我们可以从这个节点出发，边走边计数，当回到原点则统计出了环中节点个数。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190510201214571.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 给一个链表，若其中包含环，请找出该链表的环的入口结点，否则，输出null。
 */
public class Offer23 {
    public static void main(String[] args) {
        LinkNode<Integer> LinkNode = new LinkNode<Integer>();
        LinkNode<Integer> node2 = new LinkNode<Integer>();
        LinkNode<Integer> node3 = new LinkNode<Integer>();
        LinkNode<Integer> node4 = new LinkNode<Integer>();
        LinkNode<Integer> node5 = new LinkNode<Integer>();
        LinkNode.next = node2;
        LinkNode.node_value = 1;
        node2.next = node3;
        node2.node_value = 2;
        node3.next = node4;
        node3.node_value = 3;
        node4.next = node5;
        node4.node_value = 4;
        node5.node_value = 5;
        node5.next = node3;
        System.out.println(EntryNodeOfLoop(LinkNode));
    }

    static LinkNode MeetingNode(LinkNode head) {
        if (head == null || head.next == null) return null;
        /**
         * 定义两个指针  一个快一个慢  如果相遇则有环
         */
        LinkNode slow = head;
        LinkNode fast = head.next.next;
        while (fast != null && slow != null) {
            if (fast == slow)
                return fast;
            slow = slow.next;
            fast = fast.next;
            if (fast != null)
                fast = fast.next;
        }
        return null;
    }

    static int EntryNodeOfLoop(LinkNode<Integer> head) {
        LinkNode<Integer> meetingNode = MeetingNode(head);
        if (meetingNode == null)
            return 0;
        int count = 1;
        LinkNode<Integer> node = meetingNode;
        while (node.next != meetingNode) {
            node = node.next;
            count++;
        }
        node = head;
        /**
         * 先移动node1  移动位数为 环中节点个数
         */
        for (int i = 0; i < count; i++) {
            node = node.next;
        }
        LinkNode<Integer> node1 = head;
        while (node != node1) {
            node = node1.next;
            node1 = node1.next;
        }
       int node_value = node1.node_value;
        return node_value;
    }
}

```

## 22.合并两个排序的链表🎉

### 题目描述

输入两个递增排序的链表，合并这两个链表并使新链表中的结点仍然是按照递增排序的。

>注意代码鲁棒性：链表一为空；链表二为空。

### 解题思路

- 递归思想：如果P1的当前节点小于P2，那么P1做新链接当前节点同时P1后移，递归直到结束。P2>P1同理

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190510202907973.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 输入两个递增排序的链表，合并这两个链表并使新链表中的结点仍然是按照递增排序的。
 */
public class Offer25 {
    public static void main(String[] args) {
        LinkNode<Integer> LinkNode1 = new LinkNode<Integer>();
        LinkNode<Integer> node2 = new LinkNode<Integer>();
        LinkNode<Integer> node3 = new LinkNode<Integer>();
        LinkNode<Integer> node4 = new LinkNode<Integer>();
        LinkNode<Integer> node5 = new LinkNode<Integer>();
        LinkNode1.next = node2;
        LinkNode1.node_value = 1;
        node2.next = node3;
        node2.node_value = 3;
        node3.next = node4;
        node3.node_value = 5;
        node4.next = node5;
        node4.node_value = 7;
        node4.next = null;
        LinkNode<Integer> LinkNode2 = new LinkNode<Integer>();
        LinkNode<Integer> node22 = new LinkNode<Integer>();
        LinkNode<Integer> node33 = new LinkNode<Integer>();
        LinkNode<Integer> node44 = new LinkNode<Integer>();
        LinkNode<Integer> node55 = new LinkNode<Integer>();
        LinkNode2.next = node22;
        LinkNode2.node_value = 2;
        node22.next = node33;
        node22.node_value = 4;
        node33.next = node44;
        node33.node_value = 6;
        node44.next = node55;
        node44.node_value = 8;
        node44.next = null;
        LinkNode<Integer> merge = Merge(LinkNode1, LinkNode2);

        while (merge.next != null) {
            System.out.println(merge.node_value);
            merge=merge.next;
        }
    }

    static LinkNode<Integer> Merge(LinkNode<Integer> head1, LinkNode<Integer> head2) {
        LinkNode<Integer> mergeLinkListHead = null;
        if (head1 == null) {
            return head2;
        }
        if (head2 == null) {
            return head1;
        }
        if (head1.node_value < head2.node_value) {
            mergeLinkListHead = head1;
            mergeLinkListHead.next = Merge(head1.next, head2);
        } else {
            mergeLinkListHead = head2;
            mergeLinkListHead.next = Merge(head1, head2.next);
        }

        return mergeLinkListHead;
    }

}

```

## 23.树的子结构



### 题目描述

输入两棵二叉树A和B，判断B是不是A的子结构;


### 解题思路

- 第一步 : 在树A找到和树B的根节点相同值的节点
- 第二步:  相同根节点情况下, 判断树A的子树是否含有跟树B一样的树结构

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190510204027815.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 树的子结构
 * 输入两棵二叉树A和B，判断B是不是A的子结构;
 */
public class Offer26 {
    public static void main(String[] args) {

    }

    static boolean HasSubtree(BinaryTreeNode root1, BinaryTreeNode root2) {
        if (root1 == null || root2 == null) {
            return false;
        }
        boolean result = false;

        //第一步;在树A中找到和树B根结点值相同的节点，因此需要对二叉树进行遍历
        if (root1 != null && root2 != null) {
            if (root1.node_value == root2.node_value) {
                result = DoTheJudge(root1, root2);
            }
            if (!result) {
                result = HasSubtree(root1.leftTree, root2);
            }
            if (!result) {
                result = HasSubtree(root1.rightTree, root2);
            }
        }
        return result;
    }

    private static boolean DoTheJudge(BinaryTreeNode root1, BinaryTreeNode root2) {
        //遍历完树A都没有，其中都没有完全匹配树B的子结构
        if (root1 == null && root2 != null) {
            return false;
        }
        //root2的所有节点与root1中进行了匹配
        if (root2 == null) {
            return true;
        }
        if (root1.node_value != root2.node_value) {
            return false;
        } else {
            return DoTheJudge(root1.leftTree, root2.leftTree) && DoTheJudge(root1.rightTree, root2.rightTree);
        }
    }
}

```

## 24.对称的二叉树🎉


### 题目描述

请完成一个函数，输入一个二叉树，该函数输出它的镜像。

> * 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
>   1
>   /   \\
>   2     2
>    / \    / \\
>   3  4 4  3

### 解题思路

- 我们利用对称的特性可以分析出，中左右和中右左是一回事，结合题面做个先序遍历和自定义遍历可知：先序遍历（中左右）：**1，2，3，4，2，4，3，** 自定义遍历（中右左）：**1，2，3，4，2，4，3**。

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190512105043390.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

/**
 * 给定一个二叉树，检查它是否是镜像对称的。
 * <p>
 * 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
 * <p>
 * 1
 * / \
 * 2   2
 * / \ / \
 * 3  4 4  3
 * 但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的:
 * <p>
 * 1
 * / \
 * 2   2
 * \   \
 * 3    3
 */
public class Offer28 {
    public static void main(String[] args) {

    }

    static boolean isSymmetrical() {
        return isSymmetrical();
    }

    static boolean isSymmertrical(BinaryTreeNode root1, BinaryTreeNode root2) {
        if (root1 == null || root2 == null) {
            return false;
        }
        if (root1 == null && root2 == null) {
            return true;
        }
        if (root1.node_value != root2.node_value) {
            return false;
        }

        return isSymmertrical(root1.leftTree, root2.rightTree) && isSymmertrical(root1.rightTree, root2.leftTree);
    }
}

```

## 25.顺时针打印矩阵

### 题目描述

输入一个矩阵，按照从外向里以顺时针的顺组依次打印出每一个数字

> * 例如 
>   1	&nbsp;&nbsp;&nbsp;&nbsp;2&nbsp;&nbsp;&nbsp;&nbsp;	3&nbsp;&nbsp;&nbsp;&nbsp;	4
>   5	&nbsp;&nbsp;&nbsp;&nbsp;6&nbsp;&nbsp;&nbsp;&nbsp;	7	&nbsp;&nbsp;&nbsp;&nbsp;8
>   9	&nbsp;&nbsp;&nbsp;&nbsp;10	&nbsp;&nbsp;11	&nbsp;&nbsp;12
>   13	&nbsp;&nbsp;14&nbsp;&nbsp;	15&nbsp;&nbsp;	16	

打印序列：1，2，3，4，8，12，16，15，14，13，9，5，6，7，11，10.

这题有点意思，不难但是需要自己找一定的规律性和每次循环的界限的确定。

### 解题思路

- 确定每圈开始的起点：左上角,他们的横轴坐标都是一致的。
- 每圈的四种打印顺序界限确定
  - 从左到右
  - 从上到下
  - 从右到左
  - 从下到上

##### 算法图解

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190512110537843.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTE1ODMzMTY=,size_16,color_FFFFFF,t_70)

#### 参考代码：

```java
package offer;

import java.util.ArrayList;

/**
 * 输入一个矩阵，按照从外向里以顺时针的顺组依次打印出每一个数字。
 */
public class Offer29 {
    public static void main(String[] args) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int nums[][] = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}};
        printNums(result, nums, nums.length, nums[0].length);
    }

    static void printNums(ArrayList<Integer> result, int num[][], int cols, int rows) {
        if (num == null || cols <= 0 || rows <= 0) {
            return;
        }
        int start = 0;
        while (cols > 2 * start && rows > 2 * start) {
            corePrint(result, num, cols, rows, start);
            ++start;
        }
    }

    private static void corePrint(ArrayList<Integer> result, int[][] num, int cols, int rows, int start) {
        int endY = cols - 1 - start;
        int endX = rows - 1 - start;
        //从左到右
        for (int i = start; i <= endX; i++) {
            result.add(num[start][i]);
        }
        // 从上到i下
        if (start < endY) {
            for (int i = start + 1; i <= endY; i++) {
                result.add(num[i][endX]);
            }
        }
        // 从右到左
        if (start < endX && start < endY) {
            for (int i = endX-1; i >= start; i--) {
                result.add(num[endY][i]);
            }
        }
        // 从下到上
        if (start < endX && start < endY - 1) {
            for (int i = endY - 1; i >= start + 1; i--) {
                result.add(num[i][start]);
            }
        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
        }
    }
}

```

## 26.反转链表🎉

### 题目描述

输入一个链表，反转链表后，输出新链表的表头。

### 解题思路

当对一个节点的next指针进行反转时，会造成链表的断裂，需要保存当前节点的next节点。

#### 参考代码：

```java
/*
public class ListNode {
    int val;
    ListNode next = null;

    ListNode(int val) {
        this.val = val;
    }
}*/
public class Solution {
    public ListNode ReverseList(ListNode head) {
        if(head == null || head.next == null) {
            return head;
        }
        ListNode newHead = null;
        ListNode pre = null;
        ListNode cur = head;
        while(cur != null) {
            ListNode temp = cur.next;
            if(temp == null) {
                newHead = cur;
            }
            //当前节点的next指针指向前一个节点
            cur.next = pre;
            //当前节点变为前半段链表的最后一个节点，也是下一个节点需要指向的节点
            pre = cur;
            //后半段链表的第一个节点将会成为新的当前节点
            cur = temp;
        }
        return newHead;
    }
}

```