# swagger-check

Build status: [![Build Status](https://travis-ci.org/leanovate/swagger-check.svg?branch=master)](https://travis-ci.org/leanovate/swagger-check)

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
libraryDependencies += "de.leanovate" %% "swagger-check" % "0.12" % "test"
```

## Examples

Sorry, this is all still in the flow.

At the moment the best starting point might be some of tests:

* [ThingApiSpecification](src/test/scala/de/leanovate/swaggercheck/ThingApiSpecification.scala)

## License

[MIT Licence](http://opensource.org/licenses/MIT)
