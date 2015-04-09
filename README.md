# mlg - minimal loan graph
## Introduction
Suppose you have a graph, whose nodes represent banks, individuals, companies, and whenever one party lends dough *D* to another party, we put a directed edge (also, an "arc") from lender to the debtor into the graph and assign *D* as the weight of that arc. Next, suppose all parties decide to pay their debts at one and the same moment. In the worst case, there might be quadratic amount of arcs with respect to the amount of nodes in the graph. So the problem statement is: how can we minimize the amount of arcs while retaining the equities of each node?
***mlg*** is a Java framework providing for **6** different algorithms for minimizing the amount of arcs. 3 of them are experimental (they work and correctly, but are not most efficient):
* `PartitionalSimplifierV1` (*experimental, optimal, slow*)
* `PartitionalSimplifierV2` (*experimental, optimal, faster*)
* `PartitionalSimplifierV3` (*optimal, pretty fast*)
* `PartitionalSimplifierV4` (*optimal, pretty fast*)
* `CombinatorialSimplifier` (*optimal, pretty fast*)
* `GreedyCombinatorialSimplifier` (*super fast, almost optimal*)

The task of minimizing arcs in loan graphs may be rephrased as the task of finding the largest number of **groups** in an input graph. A **group** is any non-empty set of nodes for which the sum of equities is zero. So in order to process your graph, compute the equity of each node, put them into an array and pass it to a simplifier. Upon obtaining a solution array, just march over it from left to right and whenever the accumulated sum is zero, you know that the previous equities constitute a group. For example:
```
<-10 -34 35 -34 -23 14 56 2 -7 90 11 -21 10 -42 5 -52> 
will become 
<2 5 -7> <11 14 90 -21 -42 -52> <35 56 -23 -34 -34> <-10 10>
```
In the result above, all 16 nodes may be reconnected using only 12 arcs instead of possible 240.

## Loading the framework
Just run
```
git clone git@github.com:coderodde/mlg.git
```

## Compiling and testing 
```
mvn test
```

## Running a performance demo
```
mvn exec:java
```

## Using the algorithms
```java
final long[] graph = getYourGraph();
final long[] solution = new PartitionalSimplifierV4().simplify(graph);
// Other simplifiers obey the same API.
```
