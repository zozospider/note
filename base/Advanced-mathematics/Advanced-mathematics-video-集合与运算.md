- [Document & Code](#document--code)
- [一. 集合](#一-集合)
    - [1.1 集合的概念](#11-集合的概念)
    - [1.2 集合关系](#12-集合关系)
    - [1.3 集合分类](#13-集合分类)
- [二. 集合的运算](#二-集合的运算)
    - [2.1 并集](#21-并集)
    - [2.2 交集](#22-交集)
    - [2.3 差集](#23-差集)
    - [2.4 全集和补集](#24-全集和补集)
- [三. 区间与邻域](#三-区间与邻域)

# Document & Code

- (需要 chrome 插件 `MathJax Plugin for Github` 支持)

- [../Advanced-mathematics-video](https://github.com/zozospider/note/blob/master/base/Advanced-mathematics/Advanced-mathematics-video.md)

---

# 一. 集合

## 1.1 集合的概念

集合是值具有某种特定性质的事物的总体. 组成这个集合的事物称为该集合的元素.

- ${a}\in{M}$: 表示 a 属于 M.
- ${a}\notin{M}$: 表示 a 不属于 M.
- $M = \\{m_1, m_2, m_3, \cdots, m_n\\}$: 使用 `列举法` 表示集合, 列出集合的所有元素.
- $M = \\{x|x\in{R}, x^2-1=0\\}$: 使用 `描述法` 表示集合, 即 $M = \\{1, -1\\}$.

## 1.2 集合关系

- `相等`: 若 $A \subset B$ 且 $A \subset B$, 那么 $A = B$, 即集合 A 与集合 B 相等.
- `子集`: 若 $x \in A$ 一定会导致 $x \in B$, 那么 $A \subset B$.
- `关系`: 若 $A \subset B$ 且 $B \subset C$, 那么 $A \subset C$.

## 1.3 集合分类

- $Z = \\{\cdots, -n, \cdots, -2, -1, 0, 1, 2, \cdots, n, \cdots\\}$: 表示 `整数集`.
- $N = \\{0, 1, 2, \cdots, n, \cdots\\}$: 表示 `自然数集`.
- $Q = \\{\frac pq|p\in{Z}, q\in{N^+} 且 p,q 互质\\}$: 表示 `有理数集`.
- $R = \\{x|x是有理数或无理数\\}$: 表示 `实数集`.
- $\emptyset$: 表示 `空集`, 即不包含任何元素, 例如 $\\{x|x\in{R}, x^2+1=0\\} = \emptyset$.

他们的集合关系为: $N \subset Z$, $Z \subset Q$, $Q \subset R$, 空集是任何集合的子集.

---

# 二. 集合的运算

## 2.1 并集

设 A 和 B 是两个集合, 由所有属于 A 或者属于 B 的元素组成的集合, 称为 A 与 B 的并集, 记作 $A \cup B$.

$$A \cup B = \\{ x | x \in A 或 x \in B \\}$$

## 2.2 交集

设 A 和 B 是两个集合, 由所有既属于 A 又属于 B 的元素组成的集合, 称为 A 与 B 的交集, 记作 $A \cap B$.

$$A \cap B = \\{ x | x \in A 且 x \in B \\}$$

## 2.3 差集

设 A 和 B 是两个集合, 由所有属于 A 而不属于 B 的元素组成的集合, 称为 A 与 B 的差集, 记作 $A \setminus B$.

$$A \setminus B = \\{ x | x \in A 且 x \notin B \\}$$

## 2.4 全集和补集

当我们研究一个问题限定在一个大的集合 I 中进行, 所研究的其他集合 A 都是 I 的子集, 我们称集合 I 为全集.

假设 A 为全集 I 中的一个集合, 那么 A 的补集就是 I 中除 A 外的集合, 记作 $A^c$.

$A = \\{ x | 0 < x \leq 1\\}$ 的补集是 $A^c = \\{ x | x \leq 0 或 x > 1\\}$

---

# 三. 区间与邻域

---
