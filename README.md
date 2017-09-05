# swagger-check

Build status: [![Build Status](https://travis-ci.org/leanovate/swagger-check.svg?branch=master)](https://travis-ci.org/leanovate/swagger-check) 
[![codecov](https://codecov.io/gh/leanovate/swagger-check/branch/master/graph/badge.svg)](https://codecov.io/gh/leanovate/swagger-check)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.leanovate.swaggercheck/swagger-check-core_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.leanovate.swaggercheck/swagger-check-core_2.11)

Toolbox for property based testing of an API specified with swagger

## The problem

Swagger is a nice way to specify REST APIs, but it just can not keep up with all
the different libraries commonly (or not so commonly) used to implement this API.

If you generate your Swagger specifications automatically you probably do not need
this toolbox. But if you write your Swagger specifications by hand and implement
them later you might encounter the problem how to ensure that the specification
matches reality in the long term. `swagger-check` might help you in this case.

## Details

`swagger-check` is build around [Scala Check](https://www.scalacheck.org/) and contains two main additions:

* A generator for arbitrary json based on a Swagger model
    * You may consider this the client to server direction: I.e. will your REST service understand all the things a client implementor might send you based on the Swagger specification
* A verifier to very that an arbitrary json matches a Swagger model 
    * You may consider this the server to client direction: I.e. will your service send all the data a client implementor might expect according to the Swagger specification

As an additional party gimmic there is also ein generator to generate matches to a regex that you might consider useful for other things.

## Usage

Add the following to your `build.sbt`

```
libraryDependencies += "de.leanovate.swaggercheck" %% "swagger-check-core" % "0.99.13" % "test"
```

## Examples

You can find an example Play2 project in the [examples](examples/) folder.

## Some implementation notes

### Project structure

The project is now separated in modules to (eventually) support a multiple frameworks without ending in a dependency hell.

* json-schema-model
  * A generic model for validation of a json schema.
  * There are no dependencies to any framework, but you have to provide a `NodeAdapter` for each json implementation
* json-schema-jackson
  * Contains the adapters for jackson
  * Also contains a jackson module to actually parse a `json-schema-model` from a json file
* json-schema-play
  * Contains the adapter for the play framework
* json-schema-gen
  * Extends the `json-schema-model` with ScalaCheck generators
  * Requires `jackson-core` for json parsing and generation
* swagger-check-core
  * Uses the above to create a tool box for creating ScalaCheck tests based on a swagger definition


## Compatibility with play

* For Play Version 2.5 please use the 0.99.x versions (legacy branch)
* For Play Version 2.6 use the 1.0.x versions

## License

[MIT Licence](http://opensource.org/licenses/MIT)
