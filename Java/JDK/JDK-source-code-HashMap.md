
# Document & Code

- [../JDK-source-code](https://github.com/zozospider/note/blob/master/Java/JDK/JDK-source-code.md)

---

# JDK7

HashMap 在 put() 方法中, 关键步骤有两个:

- 1. 通过 hash(key) 方法获取一个 hash 的中间值: 进行一系列位运算, 其中调用了 k.hashCode() 方法.
- 2. 通过 indexFor(hash, table.length) 方法获取一个 i 中间值: 进行 `h & (length-1)` 运算, 其中 length 的值为 16, 计算出的 i 的值在 0 ~ 15 范围内.

```java
public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable
{

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {
        if (key == null)
            return putForNullKey(value);
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

    /**
     * Retrieve object hash code and applies a supplemental hash function to the
     * result hash, which defends against poor quality hash functions.  This is
     * critical because HashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits. Note: Null keys always map to hash 0, thus index 0.
     */
    final int hash(Object k) {
        int h = 0;
        if (useAltHashing) {
            if (k instanceof String) {
                return sun.misc.Hashing.stringHash32((String) k);
            }
            h = hashSeed;
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        return h & (length-1);
    }
}
```

## 为什么 HashMap 扩容需要是 2 的 n 次方?

2 的 n 次方减去 1 之后, 对应的二进制可以保证后面全是 1 (如 `16` = `00001111`), 这样任意一个索引的值都有可能和当前与运算的计算结果相等. 而扩容为其他值的时候, 会出现部分索引的值永远无法和当前与运算的计算结果相等.
