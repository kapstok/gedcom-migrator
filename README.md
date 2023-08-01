# Gedcom migrator

Gedcom is a standard that misses
1. Flexibility, such as the ability to do complex refactors or adding custom fields.
2. Displaying gedcom data in a (web) application is non-trivial and requires a
library.
3. Usability. Performing complicated queries on gedcom directly is not possible.

This is an attempt to provide a generic implementation for migrating gedcom data to
another form of data, such as a database. Personally I will use implementation
for migrating to a new sqlite database.

## Structure

This multi-module project consists of three modules:

```
 - gedcom-migrator
 |--- migration-library
 |--- sqlite
```

`gedcom-migrator` is the main pom. It does not generate a JAR.

`migration-library` is a generic library to make a migration from Gedcom easier and more
correct.

`sqlite` is an implementation that uses `migration-library` for migrating Gedcom data to Sqlite.

It will be likely that `sqlite` will not directly be useful for your use case.
But you can use the source code as an example for creating your own implementation,
using `migration-library` as dependency.

## Running

To get the `sqlite` compiling and running, execute

```bash
mvn clean install
mvn exec:java -pl :sqlite
```
