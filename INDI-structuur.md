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

## Individuals record

Structuur: `0 @KEY@ INDI`

{MIN:MAX} = cardinaliteit

### INDI Structuren

INDIVIDUAL_RECORD:=  
n @XREF:INDI@ INDI {1:1} 
+1 RESN <RESTRICTION_NOTICE> {0:1} (Komt niet voor in bestand)
+1 <<PERSONAL_NAME_STRUCTURE>> {0:M} (`NAME` is root. Een niveau lager kan er nog `GIVN`, `NICK`, `NPFX`, `NSFX`, `SPFX`, `SURN`)
+1 SEX <SEX_VALUE> {0:1}    [M|F|U|X]
+1 <<INDIVIDUAL_EVENT_STRUCTURE>> {0:M}
+1 <<INDIVIDUAL_ATTRIBUTE_STRUCTURE>> {0:M}
+1 <<LDS_INDIVIDUAL_ORDINANCE>> {0:M} (Komt niet voor)
+1 <<CHILD_TO_FAMILY_LINK>> {0:M}  
+1 <<SPOUSE_TO_FAMILY_LINK>> {0:M}
+1 SUBM @<XREF:SUBM>@ {0:M}  (Komt niet voor)
+1 <<ASSOCIATION_STRUCTURE>> {0:M} (Komt niet voor)
+1 ALIA @<XREF:INDI>@ {0:M} (KOmt niet voor)
+1 ANCI @<XREF:SUBM>@ {0:M}  (Komt niet voor)
+1 DESI @<XREF:SUBM>@ {0:M}  (Komt neit voor)
+1 RFN <PERMANENT_RECORD_FILE_NUMBER> {0:1}  (Komt niet voor)
+1 AFN <ANCESTRAL_FILE_NUMBER> {0:1}  (Komt niet voor)
+1 REFN <USER_REFERENCE_NUMBER> {0:M}  (Komt niet voor)
+2 TYPE <USER_REFERENCE_TYPE> {0:1}  (Komt niet voor)
+1 RIN <AUTOMATED_RECORD_ID> {0:1}  (Komt niet voor)
+1 <<CHANGE_DATE>> {0:1}  (Komt niet voor)
+1 <<NOTE_STRUCTURE>> {0:M}
+1 <<SOURCE_CITATION>> {0:M} (Komt te weinig voor)  
+1 <<MULTIMEDIA_LINK>> {0:M} (Komt te weinig voor)

> Onderstaande structuur klopt niet maar wordt zo aangehouden om het eenvoudig te houden.
> Check ook hier op onparsebare onderdelen.
NOTE_STRUCTURE:=
n NOTE <TEXT> {0:1}
+1 [CONT|CONC] <TEXT> {0:M} (CONC = concatenatie, CONT = newline + concatenatie)

SPOUSE_TO_FAMILY_LINK:=
n FAMS @<XREF:FAM>@ {1:1}
+1 <<NOTE_STRUCTURE>> {0:M}}

CHILD_TO_FAMILY_LINK:=
n FAMC @<XREF:FAM>@ {1:1}
+1 PEDI <PEDIGREE_LINKAGE_TYPE> (Komt niet voor)
+1 STAT <CHILD_LINKAGE_STATUS> (Komt niet voor)
+1 <<NOTE_STRUCTURE>>

INDIVIDUAL_ATTRIBUTE_STRUCTURE:=
n CAST <CASTE_NAME> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n DSCR <PHYSICAL_DESCRIPTION> (komt niet voor)
+1 [CONC | CONT ] <PHYSICAL_DESCRIPTION> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n EDUC <SCHOLASTIC_ACHIEVEMENT> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n IDNO <NATIONAL_ID_NUMBER> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n NATI <NATIONAL_OR_TRIBAL_ORIGIN> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n NCHI <COUNT_OF_CHILDREN> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n NMR <COUNT_OF_MARRIAGES> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n OCCU <OCCUPATION>
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n PROP <POSSESSIONS> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n RELI <RELIGIOUS_AFFILIATION> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n RESI (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Komt niet voor)
n SSN <SOCIAL_SECURITY_NUMBER> (Zet ik er niet in)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (Zet ik er niet in)
n TITL <NOBILITY_TYPE_TITLE> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> {0:1}* (Komt niet voor)
n FACT <ATTRIBUTE_DESCRIPTOR> (Komt niet voor)
+1 <<INDIVIDUAL_EVENT_DETAIL>> (komt niet voor)


PLACE_STRUCTURE:=
n PLAC <PLACE_NAME> {1:1} (Is gewone tekst)
+1 FORM <PLACE_HIERARCHY> {0:1} (Wordt niet gebruikt)
+1 FONE <PLACE_PHONETIC_VARIATION> {0:M} (Wordt niet gebruikt)
+2 TYPE <PHONETIC_TYPE> {1:1} (Wordt niet gebruikt)
+1 ROMN <PLACE_ROMANIZED_VARIATION> {0:M} (Wordt niet gebruikt)
+2 TYPE <ROMANIZED_TYPE> {1:1} (Wordt niet gebruikt)
+1 MAP {0:1} (Wordt niet gebruikt)
+2 LATI <PLACE_LATITUDE> {1:1} (Wordt niet gebruikt)
+2 LONG <PLACE_LONGITUDE> {1:1} (Wordt niet gebruikt)
+1 <<NOTE_STRUCTURE>> {0:M}

EVENT_DETAIL:=
n TYPE <EVENT_OR_FACT_CLASSIFICATION> {0:1} (Komt niet voor)
n DATE <DATE_VALUE> {0:1} (Voorbeelden: `1998`, `20 OCT 1908`, `ABT 2001`, `ABT 01 JAN 1999`. Belangrijk is om errors te gooien bij onparsebare items. Periodes (`FROM`/`TO`) worden niet gebruikt)
n <<PLACE_STRUCTURE>> {0:1}
n <<ADDRESS_STRUCTURE>> {0:1} (Wordt niet gebruikt)
n AGNC <RESPONSIBLE_AGENCY> {0:1} (Wordt niet gebruikt)
n RELI <RELIGIOUS_AFFILIATION> {0:1} (Wordt niet gebruikt)
n CAUS <CAUSE_OF_EVENT> {0:1} (Wordt niet gebruikt)
n RESN <RESTRICTION_NOTICE> {0:1} (Wordt niet gebruikt)
n <<NOTE_STRUCTURE>> {0:M}
n <<SOURCE_CITATION>> {0:M} (wordt niet gebruikt)
n <<MULTIMEDIA_LINK>> {0:M} (Wordt te weinig gebruikt om te implementeren)

INDIVIDUAL_EVENT_STRUCTURE:=
[
n [ BIRT | CHR ] [Y|<NULL>] {1:1} (`BIRT` komt alleen voor)
+1 <<EVENT_DETAIL>> {0:1}* (Eigenlijk `INDIVIDUAL_EVENT_DETAIL`, maar dat is in deze context overbodig)
+1 FAMC @<XREF:FAM>@ {0:1} (Geen idee waar dit naar verwijst)
|
n DEAT  [Y|<NULL>] {1:1}
+1 <<EVENT_DETAIL>> {0:1}*
|
n [ BURI | CREM ]  {1:1}
+1 <<EVENT_DETAIL>> {0:1}*
|
n ADOP {1:1} (Wordt niet gebruikt)
+1 <<EVENT_DETAIL>> {0:1}* (Wordt niet gebruikt)
+1 FAMC @<XREF:FAM>@ {0:1} (Wordt niet gebruikt)
+2 ADOP <ADOPTED_BY_WHICH_PARENT> {0:1} (Wordt niet gebruikt)
|
n [ BAPM | BARM | BASM | BLES ] {1:1} (Alleen `BAPM` (gedoopt) wordt gebruikt)
+1 <<EVENT_DETAIL>> {0:1}*
|
n [ CHRA | CONF | FCOM | ORDN ] {1:1} (Wordt niet gebruikt)
+1 <<EVENT_DETAIL>> {0:1}* (Wordt niet gebruikt)
|
n [ NATU | EMIG | IMMI ] {1:1} (Wordt niet gebruikt)
+1 <<EVENT_DETAIL>> {0:1}* (Wordt niet gebruikt)
|
n [ CENS | PROB | WILL] {1:1} (Wordt niet gebruikt)
+1 <<EVENT_DETAIL>> {0:1}* (Wordt niet gebruikt)
|
35
n [ GRAD | RETI ] {1:1} (Wordt niet gebruikt)
+1 <<EVENT_DETAIL>> {0:1}* (Wordt niet gebruikt)
|
n EVEN {1:1} (Wordt niet gebruikt)
+1 <<EVENT_DETAIL>> {0:1}* (Wordt niet gebruikt)
]

PERSONAL_NAME_PIECES:=
n NPFX <NAME_PIECE_PREFIX> {0:1} Komt niet voor
n GIVN <NAME_PIECE_GIVEN> {0:1} Gaat om zo weinig dat die met de hand kunnen worden toegevoegd.
n NICK <NAME_PIECE_NICKNAME> {0:1}
n SPFX <NAME_PIECE_SURNAME_PREFIX> {0:1} Komt niet voor
n SURN <NAME_PIECE_SURNAME> {0:1} Gaat om zo weinig dat die met de hand kunnen worden toegevoegd.
n NSFX <NAME_PIECE_SUFFIX> {0:1} Komt niet voor
n <<NOTE_STRUCTURE>> {0:M} Komt volgens mij niet voor, maar toch maar implementeren want controleren is lastig.
n <<SOURCE_CITATION>> {0:M} Komt niet voor.

PERSONAL_NAME_STRUCTURE:=
n NAME <NAME_PERSONAL> {1:1}
+1 TYPE <NAME_TYPE> {0:1} Komt niet voor
+1 <<PERSONAL_NAME_PIECES>> {0:1}
+1 FONE <NAME_PHONETIC_VARIATION> {0:M} Komt niet voor
+2 TYPE <PHONETIC_TYPE> {1:1} Komt niet voor
+2 <<PERSONAL_NAME_PIECES>> {0:1} Komt niet voor
+1 ROMN <NAME_ROMANIZED_VARIATION> {0:M} Komt niet voor
+2 TYPE <ROMANIZED_TYPE> {1:1} Komt niet voor
+2 <<PERSONAL_NAME_PIECES>> {0:1} Komt niet voor

