##json-merge

---

A tiny simple library to merge several (javax.json) JsonObject's together into a new JsonObject.

###Get Started

Getting started is as easy as adding a dependency on Json-Merge:

```xml
    <dependency>
        <groupId>it.traeck.tools.json-merge</groupId>
        <artifactId>json-merge</artifactId>
        <version>${json-merge.version}</version>
    </dependency>
```

After this you can use Json-Merge in your code. For example like:

```java
    JsonObject mergeObject = JsonMerge.merge( jsonObject1, jsonObject2, jsonObject3 );
```
