use std::env::args;
use std::fs::File;
use std::io::{BufRead, BufReader, Lines};
use regex::Regex;

mod indi;

fn main() {
    let params: Vec<String> = args().collect();
    let file = File::open(&params[1]).unwrap();
    let reader = BufReader::new(file);
    let mut lines = reader.lines();
    /*
    // groups.get(1) = indent. In which level is the line defined?
    // groups.get(2) = record type. Tells about what kind of data it's about.
    // groups.get(3) = value.
    let regex = Regex::new(r"([0-9]+) (\S*) *(.*)").unwrap();
    let line = lines.next().unwrap().unwrap(); // TODO What if we reached EOF?

    if regex.is_match(&line) {
        let groups = regex.captures(&line).unwrap(); // TODO error handling. What if the file is invalid?
        
        // Check whether it's a root item
        if groups.get(1).map_or("", |m| m.as_str()) == "0" {
            match groups.get(3).map_or("", |m| m.as_str()) {
                "INDI" => {
                    println!("Found INDI item: '{}'", line);
                    indi::indi::parseIndiRecord(lines, groups.get(2).map_or("", |m| m.as_str()));
                },
                "" | "FAM" => println!("Found root item: '{}'", line),
                _ => panic!("Unknown record at: '{}'", line)
            }
        }
    } else {
        println!("Could not parse '{}'", line);
    }*/

    for line in lines {
        parseLine(line.unwrap(), lines);
    }
    
    println!("REACHED EOF");
}

fn parseLine<T: BufRead> (line: String, mut lines: Lines<T>) {
    // groups.get(1) = indent. In which level is the line defined?
    // groups.get(2) = record type. Tells about what kind of data it's about.
    // groups.get(3) = value.
    let regex = Regex::new(r"([0-9]+) (\S*) *(.*)").unwrap();
    
    if regex.is_match(&line) {
        let groups = regex.captures(&line).unwrap(); // TODO error handling. What if the file is invalid?
        
        // Check whether it's a root item
        if groups.get(1).map_or("", |m| m.as_str()) == "0" {
            match groups.get(3).map_or("", |m| m.as_str()) {
                "INDI" => {
                    println!("Found INDI item: '{}'", line);
                    indi::indi::parseIndiRecord(&lines, groups.get(2).map_or("", |m| m.as_str()));
                },
                "" | "FAM" => println!("Found root item: '{}'", line),
                _ => panic!("Unknown record at: '{}'", line)
            }
        }
    } else {
        println!("Could not parse '{}'", line);
    }
}
