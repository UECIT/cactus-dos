# CDS-Directory of Services
## Overview

This service implements a Directory of Services (DoS).

The DoS is responsible for responding to the $check-services operation with a list of healthcare services relevant to a Referral Request, Patient etc.

This proof of concept implementation is compliant v2.0 of the CDS API Spec and supports:

- Responding to the $check-services operation
- Transforming the $check-services input into search requests for various UCDOS Search requests.

## Source Code Location

The repo for this project is located in a public GitLab space here: https://gitlab.com/ems-test-harness/cds-dos

## Usage

### Prerequisites
Make sure you have everything installed as per the setup guide:
- Maven
- IntelliJ IDE (Recommended)

### Build Steps
To run the DoS on port 8085, simply run the maven task:

`mvn spring-boot:run`

## Project Structure
### Implementation
The DoS is a Java Spring Application. It is split into three major layers:

1. Resource Providers - These contain FHIR end points for the $check-services operation and for serving up the HealthcareService resources.
2. Transformation Layer - This contains transformations from the HAPI Library's FHIR Model to the SOAP requests for UCDOS searches.
3. Registry Layer - This layer is the data access layer for retrieving static resources.
There are also packages for:

- Utilities
- Configuration (For the spring, security and fhir server)

Static resources are provided in `resources/healthcareservice`


### Tests
Unit tests are provided for the transformation layer.

## Licence

Unless stated otherwise, the codebase is released under [the MIT License][mit].
This covers both the codebase and any sample code in the documentation.

The documentation is [© Crown copyright][copyright] and available under the terms
of the [Open Government 3.0][ogl] licence.

[rvm]: https://www.ruby-lang.org/en/documentation/installation/#managers
[bundler]: http://bundler.io/
[mit]: LICENCE
[copyright]: http://www.nationalarchives.gov.uk/information-management/re-using-public-sector-information/uk-government-licensing-framework/crown-copyright/
[ogl]: http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/
