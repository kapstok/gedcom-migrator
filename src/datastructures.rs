pub struct Consent {
    consent: String,
    accepted: bool,
    from: String,
    to: Option<String>,
    grantedBy: Option<String>,
    note: Option<String>
}

pub struct Reference {
    source: String,
    date: String,
    note: Option<String>,
    consents: Vec<Consent>
}

pub struct Name {
    firstName: Option<String>,
    lastName: Option<String>,
    references: Vec<Reference>
}

pub struct Person {
    name: Name,
    sex: Option<String>,
    references: Vec<Reference>
}
