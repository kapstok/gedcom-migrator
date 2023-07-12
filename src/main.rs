use std::env::args;
use std::fs::File;
use std::io::{BufRead, BufReader};

use gedcom::parser::Parser;
use gedcom::GedcomData;

use regex::Regex;

mod datastructures;

pub struct GedcomImporter {
    bufferedLines: Vec<String>,
    regularEntry: Regex,
    reference: Regex,
    persons: Vec<String>,
    indent: u8
}

impl GedcomImporter {
    fn new() -> GedcomImporter {
        GedcomImporter {
            bufferedLines: Vec::new(),
            regularEntry: Regex::new(r"(\d) ([A-Z]+) ?(.*)").unwrap(),
            reference: Regex::new(r"(\d) @([A-Z|1-9]+)@ ?(.*)").unwrap(),
            persons: Vec::new(),
            indent: 0
        }
    }

    // Parses data from a gedcom file at path.
    pub fn toGedcom(&self, path: String) -> Result<(), String> {
        println!("READING GEDCOM FILE: {}", path);
        let file = File::open(path).expect("File not found.");
        let reader = BufReader::new(file);

        for line in reader.lines() {
            let line = &line.unwrap();
            /*if self.regularEntry.is_match(line) {
                match self.parseEntryLine(line) {
                    Ok(_) => {},
                    error => return error
                }
            } else if self.reference.is_match(line) {
                match self.parseReference(line) {
                    Ok(_) => {},
                    error => return error
                }
            } else {
                return Err(format!("Regex not matching line: {}", line));
            }*/
            if line.as_bytes()[0] >= self.indent {
                self.bufferedLines.push(line.to_string());
            } else {
                self.parseBuffer();
            }
        }

        Ok(())
    }

    // Reads a single line from the gedcom file and processes the data based on the kind of data
    // as defined by the Gedcom standard.
    fn parseBuffer(&self) {
        let mut note = String::new();

        for line in self.bufferedLines.iter().rev() {
            if self.regularEntry.is_match(line) {
                // groups.get(1) = indent. In which level is the line defined?
                // groups.get(2) = entry type. Tells about what kind of data it's about.
                // groups.get(3) = value.
                let groups = self.regularEntry.captures(line).unwrap();
                match groups.get(2).unwrap().as_str() { // TODO error handling
                    "CONT" => { // The CONT subrecord adds additional text to the parent line_value.
                        let value = String::from(groups.get(3).unwrap().as_str()); // TODO error handling
                        value.push_str(&note);
                        note = value;
                    },
                    "NOTE" => {
                        let value = String::from(groups.get(3).unwrap().as_str()); // TODO error handling
                        value.push_str(&note);
                        note = value;
                    },
                    "FAMS" => {

                    },
                    "FAMC" => {

                    },
                    "DEAT" => {

                    },
                    "PLAC" => {

                    },
                    "DATE" => {

                    },
                    "BIRT" => {

                    },
                    "SEX" => {

                    },
                    "NAME" => {
                        self.parsePersonalNameStructure(line);
                    },
                    unknown => panic!("Could not parse command {}", unknown)
                }
            }
        }

        self.bufferedLines = Vec::new();
    }

    // Will be used to parse NAME types (I guess..).
    fn parsePersonalNameStructure(&self, line: &str) -> datastructures::Name {
        let nameRegex = Regex::new(r"(\d) ([A-Z]+) (.*) ?(\/.+\/) ?(.*)").unwrap();
        let groups = nameRegex.captures(line).unwrap();

        /* Examples:

               Given name only or surname not known:
               1 NAME William Lee
               Surname only:
               1 NAME /Parry/
               Given name and surname:
               1 NAME William Lee /Parry/
               Surname and suffix:
               1 NAME /Parry/ Jr.
               Given name, surname, and suffix:
               1 NAME William Lee /Parry/ Jr.
        */

        let entryType = groups.get(2).unwrap().as_str();
        if entryType != "NAME" {
            panic!("Expected NAME. Got {}", entryType);
        }

        let firstName = groups.get(3).unwrap().as_str();
        
        let mut lastName = String::from(groups.get(4).unwrap().as_str());
        lastName.push_str(groups.get(5).unwrap().as_str());
        
        let firstName = Some(String::from(firstName));
        let lastName = Some(lastName);
        let references = Vec::new();
        let name = datastructures::Name{firstName, lastName, references};
        
        name
    }
}

// Sends parsed stuff from data to database.
fn toDatabase(connectionPath: &str, data: GedcomData) {
    println!("Opening up database from path: {}", connectionPath);
    let connection = sqlite::open(connectionPath).unwrap(); // TODO error handling
    
    // Init DB
    connection.execute("
        CREATE TABLE persons (id INTEGER, reference INTEGER, name STRING, sex STRING);
        CREATE TABLE names (id INTEGER, reference INTEGER, firstName STRING, lastName STRING);
        CREATE TABLE references (id INTEGER, date STRING, source STRING, note STRING, consents STRING);
        CREATE TABLE consents (id INTEGER, consent STRING, accepted INTEGER, from STRING, to STRING, note STRING, grantedBy STRING);
    ")
    .unwrap();

    // Parse persons
    /*for individual in data.individuals {

    }*/
}

fn main() {
    let params: Vec<String> = args().collect();
    let importer = GedcomImporter::new();
    let data = importer.toGedcom(params[1]);
//    let connection = sqlite::open(params[1]).unwrap(); // TODO error handling
    toDatabase(&params[2], data);
}
