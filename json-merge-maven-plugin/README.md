##json-merge-maven-plugin

---

A Maven plugin to merge several json/yaml files together into a single file.

###Usage

Add to your `build->plugins` section (default phase is `generate-sources` phase)
```xml
<plugin>
  <groupId>it.traeck.tools.json-merge</groupId>
  <artifactId>json-merge-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <goals>
        <goal>generate</goal>
      </goals>
      <configuration>
        <todo>todo</todo>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Followed by:

```
mvn clean compile
```

### General Configuration parameters

Parameter | Description | Required | Default
----------|-------------|----------|---------
`todo` | todo | todo |

### Sample configurations

Please see [example configurations](examples) for how to use this plugin.
