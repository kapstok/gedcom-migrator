use std::io::{Lines, BufRead};

pub fn parseIndiRecord<T: BufRead> (mut lines: &Lines<T>, reference: &str) {
    println!("Key {}", reference);
}
