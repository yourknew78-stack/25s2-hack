# COMP2100 黑客松 · 题目版骨架

从已完成的答案项目（`../untitled1/`）剥离出来的**空白骨架**：

- **保留**：题面文档、包结构、类/接口声明、全部方法签名、`abstract` 方法、枚举常量、record 组件、Javadoc
- **清空**：所有方法体与构造器体 → `throw new UnsupportedOperationException("TODO: 待实现")`
- **删除**：私有实现辅助方法、描述实现思路的注释、Task 4 的答案测试

`src/` 与 `test/` 都已用 `javac` 全量编译验证通过。

---

## 目录结构

```
hackathon-blank/
├── introduction          ← 题面原文（任务 1–5 + UML 要求）
├── algorithms.md         ← Oldest / Overview 两种摘要算法的规范
├── src/                  ← 52 个源文件，实现全部清空
│   ├── reactions/        ← 本次黑客松要写的模块
│   ├── dao/              ← DAO 模式基类与模型
│   ├── sorteddata/       ← SortedData 抽象 + SortedArrayList / BSTree / AVLTree
│   └── persistentdata/   ← CSV 持久化管线
├── test/                 ← 题目自带的 3 个测试类
│   ├── Task1BasicTest.java              （给定）
│   ├── Task2BasicTest.java              （给定）
│   ├── ReportSources.java               （给定，Task 4 的接入点）
│   ├── ReactionDAOTests.java            （空壳，Task 4 自己写）
│   └── ReactionReportOverviewTests.java （空壳，Task 4 自己写）
└── hackathon-blank.iml / .idea/
```

> `algorithms.md` 的文件名**末尾带一个空格**，这是原仓库就有的，不是笔误。

---

## 与原答案项目的差异

| 内容 | 处理 |
| --- | --- |
| `introduction` / `algorithms.md` | 原文照搬 |
| `Task1BasicTest` / `Task2BasicTest` / `ReportSources` | 原文照搬（属于题目） |
| 所有方法 / 构造器 | 签名不变，方法体换成 TODO |
| 构造器里的 `super(...)` / `this(...)` 委托 | 保留（语言必需，不是答案） |
| `private` 方法（如 `binarySearch`、`hasReaction` 之外的私有辅助） | **整体删除** |
| record 的规范构造器 | 删除，交给编译器自动生成 |
| 字段的非字面量初始值（如 `= ReactionDAO.getInstance()`） | 删除，字段保留 |
| `AbstractReactionReporter` / `OldestReactionReporter` / `OverviewReactionReporter` 的 Javadoc | 删除（描述了实现思路） |
| `ReactionDisplayTag` | 改回规范的 **2 字段**形式（见下） |
| `ReactionsFacade.getAllReactionsOnMessage` | 删除（上一版新增的死代码，题目只要求 3 个方法） |
| `ReactionDAOTests` / `ReactionReportOverviewTests` | 换成空壳 |
| 生成脚本 `strip_lib.py` / `_polish.py` | 已删除（一次性工具） |

---

## 怎么开始

**IntelliJ**：直接 `Open` 本目录即可。`.iml` 已配好，JUnit4 指向 Gradle 自带的
`junit-4.13.2.jar` 与 `hamcrest-core-1.3.jar`，无需额外下载。

**命令行验证编译**：

```bash
cd hackathon-blank
javac -d /tmp/out $(find src -name '*.java')

G=~/.gradle/wrapper/dists/gradle-8.9-bin/90cnw93cvbtalezasaz0blq0a/gradle-8.9/lib
javac -cp "/tmp/out:$G/junit-4.13.2.jar:$G/hamcrest-core-1.3.jar" -d /tmp/outt $(find test -name '*.java')
```

---

## 任务对照

| 任务 | 要改的文件 | 关键指标 |
| --- | --- | --- |
| Task 1 · 回应的存取 | `reactions/ReactionsFacade.java`、`dao/ReactionDAO.java`、`dao/model/Reaction.java` | 必须用 DAO 模式；单条消息 **100 万次 add + 1000 次 get < 1 秒** |
| Task 2 · 摘要算法 | `reactions/ReactionReportFactory.java`、`IReactionReporter` 的实现类 | 必须用模板方法 + 工厂模式；100 万条回应的报告 **< 10 微秒** |
| Task 3 · 持久化 | `persistentdata/**`、`DataManager` | 跨进程保留；10 万条读写 < 1 秒；**≤40 字节/条** |
| Task 4 · 写测试 | `test/ReactionDAOTests.java`、`test/ReactionReportOverviewTests.java` | 后者必须用参数化测试跑 `ReportSources` |
| Task 5 · 代码质量 | `reactions/SpamDetector.java`、Task 2 的代码 | 只修 bug、不算法；`SpamDetector` 允许改签名 |
| UML | `uml.png` | 必须出现聚合/组合/依赖/继承/多重性/可见性各至少一例 |

---

## 必须遵守的约束

1. **不许修改已有方法的签名，不许改类名**（自动测试依赖它们）。Task 5 的 `SpamDetector` 是唯一例外。
2. **不许修改 `ReactionType` 枚举的内容。**
3. 代码写在 `src/` 下任何位置都可以，也可以新增文件与包。

---

## 几个已经替你确认过的坑

**1. `ReactionDisplayTag` 用 2 个字段。**
题面给出的两个例子都是两参数形式 —— `ReactionDisplayTag(HAPPY, "User1")`（Oldest，label 是用户名）
和 `ReactionDisplayTag(HAPPY, "4")`（Overview，label 是次数的字符串）；
题目自带的 `Task2BasicTest` 也只断言 `.type()` 与 `.label()`。
所以这里恢复成 `record ReactionDisplayTag(ReactionType type, String label)`。
**不要给它加字段** —— 隐藏测试若用两参数构造期望值，加字段会让它编译不过，Task 2 与 Task 4 会一起拿 0 分。

**2. 原始模板的持久化读取里有一个「日期炸弹」。**
week 5 miniproject 给的 `ComputerIOFactory.reader()` 里有这么一段：

```java
if (System.currentTimeMillis() > 1764620398492L) {   // 2025-12-01 20:19:58 UTC
    throw new IOException("Incompatible operating system detected.");
}
```

过了这个时间点，`reader()` 永远返回 `null`，`DataPipeline.readTo` 会静默什么都不读。
本骨架里的实现已经清空，所以这段代码不在了；但如果你从别处取回原始 `ComputerIOFactory`，记得先删掉它。

**3. `saved/` 目录不存在时，写盘会静默失败。**
`new FileWriter("saved/xxx.txt")` 在目录不存在时抛 `IOException`，而原实现把它 catch 掉并返回 `null`，
`writeFrom` 于是直接 return —— 看起来跑成功了，其实一个字节都没写。自己实现持久化时注意这点。

**4. 性能目标不是靠"优化常数"能达成的。**
原答案用 `SortedArrayList`（插入 O(n)）加"每次 add 都全量写盘"，实测 10 万次插入要 4.5 秒，
外推到 100 万次约 455 秒（目标 1 秒）。而 Task 2 要在 10 微秒内为 100 万条回应出报告 ——
光是遍历 100 万个元素就不止 1 毫秒，所以**必须增量维护摘要**（每次 add/remove 时顺手更新每条消息的聚合），
不能每次报告都全量扫描。

---

## 关于 `reactions` 包里的三个类

`AbstractReactionReporter`、`OldestReactionReporter`、`OverviewReactionReporter` 是**上一版答案自己设计的结构**，
不属于原题给定内容（题目只给了 `IReactionReporter` 与 `ReactionReportFactory`）。
这里为了保留"模板方法"的骨架把它们留成了空壳；如果你想从零设计，直接删掉这三个类即可。

同理，`dao/model/Reaction.java`、`dao/ReactionDAO.java`、`dao/ReactionComparator.java`、
`persistentdata/serialization/ReactionSerializer.java` 也都是上一版新增的类 ——
"如何抽象地表示一条回应"正是题目要求你们组先讨论的设计决策之一，可以整个推翻重写。

---

## 注释说明

保留下来的注释绝大部分是接口说明（属于题面）。
`reactions` 包中描述实现思路的注释已经清掉，其余位置若有零星残留，不影响做题。
