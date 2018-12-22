
# CO2006 18-19 - SPRINT 3 - MINIPROJECT


This assignment is worth **20%** of the overall module mark and the mark is provided out of **100**:
## Marks Achieved
* 5/5 for doing exercises in the lab sessions
* 5/5 for the checkpoint submission
* 84/90 for the release submission
* **Total: 94/100**
## Exercise

The goal of this worksheet is to test an online organizer web application developed in Spring MVC and Java. The [project containing the code base](./sprint3.miniproject/) implements ways to add, view, and delete todos. **Do not modify the name of the project. Do not modify any code except from `src/test/groovy/app/OrganizerSpec.groovy`**.

In this sprint, you are asked to implement a BDD test specification using Spock. 

### Codebase and location of specification to be implemented

Codebase to test:
* For the checkpoint you will test the class `src/main/java/app/domain/Todo.java` (similar to the one from the labs).
* For the remainder of the miniproject you will test the controllers  `src/main/java/app/controller/DisplayTodoController.java` and `src/main/java/app/controller/OrganizerController.java` and the validator `src/main/java/app/controller/TodoValidator.java`.

Place **all code you write** in a single Spock specification `src/test/groovy/app/OrganizerSpec.groovy` that you create as part of the checkpoint.

**Feature methods** (test cases): 
* Test cases must be developed using the [Spock framework](http://spockframework.org/) and they should be well documented using behaviour-driven development scenarios following the structure `given/when/then` (or equivalent). 
* Each individual test case must appear in the JUnit report generated by the command `./gradlew clean test` (or equivalent command in Windows) at `build/reports/tests/test/index.html` (test cases listed under `app>OrganizerSpec`). If you use a block `where:`, apply [method unrolling](http://spockframework.org/spock/docs/1.1/all_in_one.html#_method_unrolling) using the annotation `@Unroll` and placeholders in the name of the test case. 

**Feature method names** The marking will rely on the names of feature methods to identify your test cases. Start all names of feature methods with the number given in each exercise followed by `:`, e.g,

```
  // checkpoint
  def "0: decrease priority..." () {
  ...}
  
  // part A
  def "1: some text here..." () {
  ...}

  def "2: some other text here..." () {
  ...}
  
```

Feature methods that do not start with the corresponding number followed by `:` **will receive 0 marks**. For the last part numbers are not given and can be chosen by yourself.

## Exercises

### Rubric

In order to give an idea of the level of effort required for each exercise, exercises are tagged with a level of difficulty as follows:
  * :star: : The effort lays in implementing the description of the given test case. The challenge consists in using appropriate matchers (from `Hamcrest` or from `Spring MVC Test`) to define assertions as described.    
  * :star::star: : As above but the structure of the test case is not given. You need to determine what blocks to use and document them appropriately. The challenge consists in developing feature methods using Spock and in defining correct assertions, which are not given explicitly. 
  * :star::star::star:: As above but the test cases are not given, you need to find additional test cases with the help of [Jacoco](http://www.eclemma.org/jacoco/). The challenge consists in achieving test coverage. 
  
### <a id="checkpoint-task"></a> :star: Checkpoint [5 marks]

Create a Spock specification in file `src/test/groovy/app/OrganizerSpec.groovy`. Implement the following feature method in the class `OrganizerSpec` using `Spock` and appropriate matchers for the assertions. 

`0:` 

**Given** a todo with priority `10`<br/>
**When** I set the priority of the todo to `9`<br/>
**Then** a `RuntimeException` is thrown<br/>

#### Marking guidelines

1. When unzipping the file you submit (see [Submission procedure](#submission-procedure)), a folder `sprint3.miniproject` is obtained and this folder contains the file `build.gradle` for your project: 1 mark
2. The `./gradlew clean test` command generates Spock and JUnit test reports including the above test: 2 marks
    * The Spock test report should be generated automatically at `build/spock-reports/index.html`. In addition, a JUnit report should appear at `build/reports/tests/test/index.html`.
3. The test must pass. The check (oracle) in the test case must be correct, i.e., if no `RuntimeException` is thrown the test must fail: 2 marks. 

### :star: A. Writing feature methods [40 marks]

Implement the following feature methods in the class `OrganizerSpec` using `Spock` and appropriate matchers for the assertions. 


`1:` 

**Given** the context of the controller is setup<br/>
**When** I perform an HTTP GET `/`<br/>
**Then** the status of the HTTP response should be `302`<br/> 
**And** I should be redirected to URL `/list`<br/>

`2:`

**Given** the context of the controller is setup<br/>
**And** the organizer has no todos (use `OrganizerApp.organizer.todos = new ArrayList()`)<br/>
**When** I perform an HTTP GET `/create`<br/>
**Then** the status of the HTTP response should be `Ok` (`200`)<br/> 
**And** I should see the view `CreateTodo`<br/>
**And** the model attribute `todo` has property `description` with value `null`<br/>

`3:`

**Given** the context of the controller is setup<br/>
**And** the organizer has no todos (use `OrganizerApp.organizer.todos = new ArrayList()`)<br/>
**When** I perform an HTTP GET `/list`<br/>
**Then** the status of the HTTP response should be `Ok` (`200`)<br/> 
**And** I should see the view `NoTodo`<br/>


`4:` 

**Given** the context of the controller is setup<br/>
**And** the organizer has todos<br/>
**When** I perform an HTTP GET `/next`<br/>
**Then** the status of the HTTP response should be `Ok` (`200`)<br/> 
**And** I should see the view `NextTodo`<br/>

`5:` 

**Given** the context of the controller is setup<br/>
**When** I perform an HTTP POST `/create` with<br/>
  * `task = 'my Task'`
  * `description = 'my Description'`
  * `priority = '0'`
  * `cancel = ''`
  
**Then** the status of the HTTP response should be `302`<br/> 
**And** I should be redirected to URL `/list`<br/>

`6:` 

**Given** the context of the controller is setup<br/>
**When** I perform an HTTP POST `/create` with<br/>
  * `task = ''`
  * `description = 'my Description'`
  * `priority = '4'`
  * `important = '1'`
  * `add = ''`
  
**Then** the status of the HTTP response should be `Ok` (`200`)<br/> 
**And** I should see the view `CreateTodo`<br/>
**And** the model attribute `todo` has property `priority` equal to `4`<br/>

### :star::star: B. Building a specification from semi-informal requirements [20 marks]

Implement the following scenarios as feature methods in the class `OrganizerSpec` using `Spock` and appropriate matchers for the assertions.
  
`7:` 

Whenever the organizer has no todos, the HTTP GET request `/next` should show the view `NoTodo`. 


`8:` 

Whenever the organizer has todos, the HTTP GET request `/create` should show the view `CreateTodo`. 


`9:` 

The HTTP POST request `/create` with the values listed below should redirect to URL `/list`:
  * `task = ''`
  * `description = 'my Description'`
  * `priority = '0'`
  * `cancel = ''`


`10:` 

The HTTP POST request `/create` with the values listed below should show the view `CreateTodo` and the model attribute `todo` should have errors:
  * `task = ''`
  * `description = ''`
  * `priority = '0'`
  * `important = '1'`
  * `add = ''`
### :star::star::star: C. Complete the feature specification [30 marks]

Complete the feature specification in the class `OrganizerSpec` with additional feature methods in order to achieve  **instructions coverage and branch coverage** of `DisplayTodoController`, `OrganizerController`, and `TodoValidator`. 

That is, implement as many feature methods as necessary in order to achieve 100% instructions coverage and branch coverage of `DisplayTodoController`, `OrganizerController`, and `TodoValidator`. 

There is no limit in the number of test cases to be developed but there is a point where adding more test cases is pointless (and it is your job, as a tester, to find what this upper bound may be). However, please do not try with more than 20 test cases.
