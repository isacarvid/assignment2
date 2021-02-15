# Assignment 2 : Continuous Integration
By Ayub Atif, Isac Arvidsson, Eva Despinoy, Daniel Helle

## Description of the program
This program listens for changes to the repository, compiles the project, runs the corresponding tests, and notifies the contributor of the test results via an email. 

## How to use the program? 
In the assignment2 directory: 
Run `cd CIServer`, then `./gradlew build` and lastly `./gradlew run --args='portnr', 'server-email', 'server-password'`

## Statement of contribution

### Ayub Atif
* Co-wrote function `createBody()` with corresponding tests
* Co-wrote function `sendEmail()` with corresponding tests
* Wrote class `CredentialHelper`
* Worked on removing hard-coded sections

### Isac Arvidsson
* Co-wrote class `WebhookRequest`
* Co-wrote class `BuildStatus`
* Co-wrote function `runProcess()`
* Co-wrote function `getBody()`
* Co-wrote function `compileRepo()`
* Co-wrote `testWebhookRequest()`
* Co-wrote `testCompileRepo`
* added initial class to start a server

### Eva Despinoy
* Co-wrote function `createBody()` with corresponding tests
* Co-wrote function `sendEmail()` with corresponding tests
* Wrote `README.md` :)

### Daniel Helle
* Co-wrote class `WebhookRequest`
* Co-wrote class `BuildStatus`
* Co-wrote function `runProcess()`
* Co-wrote function `getBody()`
* Co-wrote function `compileRepo()`
* Co-wrote `testWebhookRequest()`
* Co-wrote `testCompileRepo`
* Wrote in `README.md`
