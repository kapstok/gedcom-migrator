# Gedcom to Sqlite parser

## TODO

- In `INDI-structuur.md` staat dat de `PERSONAL_NAME_STRUCTURE` `n NAME` heeft en dat
`PERSONAL_NAME_PIECES` +1 is. In de code is `PERSONAL_NAME_STRUCTURE` achterwege
gelaten en is direct `PERSONAL_NAME_PIECES` geimplementeerd, maar klopt dan nog de
inspringing wel?

## Running

To get the parser running from the commandline, execute

```bash
mvn exec:java -Dexec.args="<PATH_TO_GEDCOM_FILE> <PATH_TO_SQLITE_DB>"

# alternative commands:
mvn exec:java -Dexec.args="--help"
mvn exec:java -Dexec.args="--version"
```
