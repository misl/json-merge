# json-merge

A tiny utility to merge several (javax.json) `JsonObject`'s together into a 
single `JsonObject`.

### Components

- [json-merge library](./json-merge-lib)

    Small library to do the actual merging of `JsonObject`'s

- [json-merge maven plugin](./json-merge-maven-plugin)

    Maven plugin using the library to be able to merge both json and yaml 
    files as part of you build process.

For more details, see the corresponding component.