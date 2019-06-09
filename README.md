# bucket-bloom-filter

使用HBase底层bloom filter实现的分桶bloom filter，可存储上百亿的元素，可用于数据去重。

## 介绍

布隆过滤器1970年由布隆提出的，由一个很长的二进制向量和一系列随机映射函数组成。布隆过滤器可以用于检索一个元素是否在一个集合中。如果想判断一个元素是不是在一个集合里，一般想到的是将集合中所有元素保存起来，然后通过比较确定，但是随着集合中元素的增加，我们需要的存储空间越来越大。同时检索速度也越来越慢。

布隆过滤器的原理是，当一个元素被加入集合时，通过K个hash散列函数将这个元素映射成一个位数组中的K个点，把它们置为1。检索时，如果这些点有任何一个0，则被检元素一定不在；如果都是1，则被检元素很可能在，因为可能这些点是由其它元素hash得到的位置，这也是误判存在的原因。**每次检索会返回两个结果之一：可能存在或者一定不存在。**

## Usage

创建一个分桶的bloom filter，需要指定以下参数：

- dump、load路径，用于bloom filter内存数据的导出和导入；

- 桶数量

- 每个桶预期存储的元素数量

- 错误率

总的元素存储数量即是：桶数量*每个桶的元素数量

```java
BloomFilterManager bloomFilterManager = new BloomFilterManager("/bloomfilter", 10, 10000, 0.01, 1);
```

往布隆过滤器中添加元素：

```java
 bloomFilterManager.add(Bytes.toBytes("test_1"));
```


检查元素是否存在：

```java
bloomFilterManager.check(Bytes.toBytes("test_1"));
```


从磁盘中加载布隆数据：

```java
bloomFilterManager.load();
```


将内存中的布隆数据保存到磁盘中：

```java
bloomFilterManager.dump();
```


Demo：

```java
//create a bloom filter manager with parameters: dump or load path,bucket numbers, each bucket contains elements, false positive rate, fold factor
BloomFilterManager bloomFilterManager = new BloomFilterManager("/bloomfilter", 10, 10000, 0.01, 1);

//load dump bloom filter data from files
bloomFilterManager.load();


//add any element to bloom filter using add method
bloomFilterManager.add(Bytes.toBytes("test_1"));
bloomFilterManager.add(Bytes.toBytes("test_2"));
bloomFilterManager.add(Bytes.toBytes("test_3"));

//check whether an element has been stored in the bloom filter using check method,true contains
bloomFilterManager.check(Bytes.toBytes("test_1"));
bloomFilterManager.check(Bytes.toBytes("test_2"));
bloomFilterManager.check(Bytes.toBytes("test_3"));

//false, not contains
bloomFilterManager.check(Bytes.toBytes("test_4"));
bloomFilterManager.check(Bytes.toBytes("test_5"));

//dump bloom filter data to files
bloomFilterManager.dump();
```



## 测试数据

//Todo

