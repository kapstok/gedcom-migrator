# Gedcom ~~to Sqlite parser~~ migrator

Gedcom is a standard that misses
1. Flexibility, such as the ability to do refactors or adding custom fields.
2. Portability. Displaying gedcom data in a (web) application is non-trivial and requires a library.
3. Usability. Performing complicated queries on gedcom directly is not possible.

This is an attempt to provide a generic implementation for migrating gedcom data to
a certain database. Personally I will use sqlite.

## TODO

- ~~In `INDI-structuur.md` staat dat de `PERSONAL_NAME_STRUCTURE` `n NAME` heeft en dat
`PERSONAL_NAME_PIECES` +1 is. In de code is `PERSONAL_NAME_STRUCTURE` achterwege
gelaten en is direct `PERSONAL_NAME_PIECES` geimplementeerd, maar klopt dan nog de
inspringing wel?~~ Dit is achterhaald.
- Ik zit te denken aan een wrapper om alle gedcom modellen heen en de velden daarvan.
Die wrapper geeft drie mogelijkheden:
1. Je kunt de waarde van dat veld/model lezen.
2. Je kunt een veld/model markeren als _gelezen_.
3. Een functie die de bovengenoemde twee functies combineert.

## Running

> The instructions below are obsolete.

To get the parser running from the commandline, execute

```bash
mvn exec:java -Dexec.args="<PATH_TO_GEDCOM_FILE> <PATH_TO_SQLITE_DB>"

# alternative commands:
mvn exec:java -Dexec.args="--help"
mvn exec:java -Dexec.args="--version"
```
