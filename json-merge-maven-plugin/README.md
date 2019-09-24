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
        <filenames> <!-- 2 or more filenames -->
          <filename>file1</filename>
          <filename>file2</filename>
          <filename>file3</filename>
        </filenames>
        <outputPath>${project.build.directory}/merged</outputPath>
        <outputFilename>my-merged-file</outputFilename>
        <outputFormat>JSONANDYAML</outputFormat>
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
`filenames` | fileanames to be merged | true |
`outputPath` | target output path | false | `${project.build.directory}/merged-json`
`outputFilename` | Output filename (without extension) | false | json-merge
`outputFormat` | Output file format (`JSON`, `YAML`, `JSONANDYAML`) | false | JSON
`encoding` | encoding to use for output files | false | ${project.build.sourceEncoding}
`prettyPrint` | whether to pretty print (true) output or not | false | true

### Sample configurations

Please see [example configurations](examples) for how to use this plugin.
