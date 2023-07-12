# Structuur

De GEDCOM File blijkt te bestaan uit Individuals (I) en Families (F). Verder zijn er tags die overgeslagen kunnen worden; alle metadata.
Deze metadata is te vinden onder `0 HEAD`.

Van boven naar beneden worden komt eerst de HEAD, dan I, dan F. Het lastige is dat er al referenties in I worden gemaakt naar F alvorens
F gedefinieerd is. Daarnaast worden in F weer referenties naar I gemaakt. Hoe kan dit uitgezocht worden?

## Family record

Bestaat uit `HUSB`, `WIFE`, `CHIL`, etc.

## Individuals record

Structuur: `0 @KEY@ INDI`

- `NICK` komt regelmatig voor. Deze moet ook geinterpreteerd worden.

{MIN:MAX} = cardinaliteit

INDIVIDUAL_RECORD:=  
n @XREF:INDI@ INDI {1:1} 
+1 RESN <RESTRICTION_NOTICE> {0:1} (Komt niet voor in bestand)
+1 <<PERSONAL_NAME_STRUCTURE>> {0:M} (`NAME` is root. Een niveau lager kan er nog `GIVN`, `NICK`, `NPFX`, `NSFX`, `SPFX`, `SURN`)
+1 SEX <SEX_VALUE> {0:1}    [M|F|U|X]
+1 <<INDIVIDUAL_EVENT_STRUCTURE>> {0:M}  p. 20  
+1 <<INDIVIDUAL_ATTRIBUTE_STRUCTURE>> {0:M}    p. 19 
+1 <<LDS_INDIVIDUAL_ORDINANCE>> {0:M}  
+1 <<CHILD_TO_FAMILY_LINK>> {0:M}  
+1 <<SPOUSE_TO_FAMILY_LINK>> {0:M}  
+1 SUBM @<XREF:SUBM>@ {0:M}  
+1 <<ASSOCIATION_STRUCTURE>> {0:M}  
+1 ALIA @<XREF:INDI>@ {0:M}    p: 15  
+1 ANCI @<XREF:SUBM>@ {0:M}  
+1 DESI @<XREF:SUBM>@ {0:M}  
+1 RFN <PERMANENT_RECORD_FILE_NUMBER> {0:1}  
+1 AFN <ANCESTRAL_FILE_NUMBER> {0:1}  
+1 REFN <USER_REFERENCE_NUMBER> {0:M}  
+2 TYPE <USER_REFERENCE_TYPE> {0:1}  
+1 RIN <AUTOMATED_RECORD_ID> {0:1}  
+1 <<CHANGE_DATE>> {0:1}  
+1 <<NOTE_STRUCTURE>> {0:M}  
+1 <<SOURCE_CITATION>> {0:M}    p. 26  
+1 <<MULTIMEDIA_LINK>> {0:M}    p. 21 

## Multimedia record

Bestaat in GEDCOM bestand. Maar komt maar zo weinig voor dat deze achterwege wordt gelaten.

## Source record

Komt wel voor in bestand als onderdeel van families. Wordt ook achterwege gelaten omdat dit te weinig voorkomt.

## Submitter record

Komt niet voor in bestand. Wordt daarom overgeslagen.

## Location record

Komt te weinig voor. Wordt achterwege gelaten.
