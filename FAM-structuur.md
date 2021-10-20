# Structuur

De GEDCOM File blijkt te bestaan uit Individuals (I) en Families (F). Verder zijn er tags die overgeslagen kunnen worden; alle metadata.
Deze metadata is te vinden onder `0 HEAD`.

Van boven naar beneden worden komt eerst de HEAD, dan I, dan F. Het lastige is dat er al referenties in I worden gemaakt naar F alvorens
F gedefinieerd is. Daarnaast worden in F weer referenties naar I gemaakt. Hoe kan dit uitgezocht worden?
> Zoals ik er nu naar kijk, moeten eerst een INDI en FAM tabel gemaakt worden. Daarna kan linking plaats vinden.
> In plaats gebruik te maken van AUTO_INCREMENT, kan er ook naar de IDs in het GEDCOM bestand zelf gekeken worden en die IDs aangehouden
> worden. Het nadeel is wel dat je misschien geen AUTO_INCREMENT kan gebruiken bij het aanmaken van nieuwe records.

Dingen die te weinig voor komen moeten nog wel in dit document als relevant beschouwd worden; dan kan op het eind de missende onderdelen
met de hand aan de database toegevoegd worden.

## Family record

Bestaat uit `HUSB`, `WIFE`, `CHIL`, etc.

### Nog uit te zoeken

FAM_RECORD:=
n @<XREF:FAM>@ FAM
+1 RESN <RESTRICTION_NOTICE> (Komt niet voor)
+1 <<FAMILY_EVENT_STRUCTURE>>
+1 HUSB @<XREF:INDI>@
+1 WIFE @<XREF:INDI>@
+1 CHIL @<XREF:INDI>@
+1 NCHI <COUNT_OF_CHILDREN>
+1 SUBM @<XREF:SUBM>@ (Tot en met hier nog uitzoeken)
+1 <<LDS_SPOUSE_SEALING>> (Komt niet voor)
+1 REFN <USER_REFERENCE_NUMBER> {0:M}  (Komt niet voor)
+2 TYPE <USER_REFERENCE_TYPE> {0:1}  (Komt niet voor)
+1 RIN <AUTOMATED_RECORD_ID> {0:1}  (Komt niet voor)
+1 <<CHANGE_DATE>> {0:1}  (Komt niet voor)
+1 <<NOTE_STRUCTURE>> {0:M}
+1 <<SOURCE_CITATION>> {0:M} (Komt te weinig voor)  
+1 <<MULTIMEDIA_LINK>> {0:M} (Komt te weinig voor)

FAMILY_EVENT_STRUCTURE:=
n [ ANUL | CENS | DIV | DIVF ]
+1 <<FAMILY_EVENT_DETAIL>>
n [ ENGA | MARB | MARC ]
+1 <<FAMILY_EVENT_DETAIL>>
n MARR [Y|<NULL>]
+1 <<FAMILY_EVENT_DETAIL>>
n [ MARL | MARS ]
+1 <<FAMILY_EVENT_DETAIL>>
n RESI
+1 <<FAMILY_EVENT_DETAIL>>
n EVEN [<EVENT_DESCRIPTOR> | <NULL>]
+1 <<FAMILY_EVENT_DETAIL>>

### Uitgezocht

> Onderstaande structuur klopt niet maar wordt zo aangehouden om het eenvoudig te houden.
> Check ook hier op onparsebare onderdelen.
NOTE_STRUCTURE:=
n NOTE <TEXT> {0:1}
+1 [CONT|CONC] <TEXT> {0:M} (CONC = concatenatie, CONT = newline + concatenatie)
