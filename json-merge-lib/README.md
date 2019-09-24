# json-merge

A tiny simple library to merge several (javax.json) JsonObject's together into a new JsonObject.

### Get Started

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

### Merge policy

Json objects are merged one by one in the order in which they are
fed to the `JsonMerge.merge()` method. This order is important as
as overlap will be overwritten. As a result latter object take 
precedence over the former ones.

- Basic types

    Field with basic type will be added when not present. When 
    already present the value of the latter object will take 
    precedence.
    
- Objects

    When encountering objects there are a few possiblities:
    
    - The object is present in both former and latter object.
    
        In this case the fields of the object will be merged.
        
    - The object is only present in the latter object.
    
        In this case the object will be added. Or when the 
        same field is already present in the former object as
        a basic type, it will be overwritten with the object.
        
    - The object is only present in the former object.
    
        In case the field is not present in the latter object 
        the object remains untouched. However if the latter
        object has the same field with a basic type, the object
        will be overwritter with the basic type value.

- Arrays

    Are merged based on their index. Any additional elements 
    in the latter will be added to the array. 
    
    To leave array elements in the former untouched, the latter 
    can use null entries for those particular indexes.            