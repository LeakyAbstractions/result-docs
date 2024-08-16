
{#benchmarks}
## Benchmarks

> ***Measuring performance to find out how fast Results are***

Throughout these guides, we have mentioned that throwing Java exceptions is slow. But... how slow? According to our benchmarks, throwing an exception is several orders of magnitude slower than returning a failed result. This proves that using exceptional logic just to control normal program flow is a bad idea.

We should throw exceptions sparingly, even more so when developing performance-critical applications.


### Benchmarking Result Library

This library comes with [a set of benchmarks that compare performance](https://github.com/LeakyAbstractions/result-benchmark) when using results versus when using exceptions.


#### Simple Scenarios

The first scenarios compare the most basic usage: a method that returns a `String` or fails, depending on a given `int` parameter:


##### Using Exceptions

{title: "Using exceptions"}
```java
public String usingExceptions(int number) throws SimpleException {
  if (number < 0) throw new SimpleException(number);
  return "ok";
}
```


##### Using Results

{title: "Using Results"}
```java
public Result<String, SimpleFailure> usingResults(int number) {
  if (number < 0) return Results.failure(new SimpleFailure(number));
  return Results.success("ok");
}
```


#### Complex Scenarios

The next scenarios do something a little bit more elaborate: a method invokes the previous method to retrieve a `String`; if successful, then converts it to upper case; otherwise transforms the "simple" error into a "complex" error.


##### Using Exceptions

{title: "Using exceptions"}
```java
public String usingExceptions(int number) throws ComplexException {
  try {
    return simple.usingExceptions(number).toUpperCase();
  } catch (SimpleException e) {
    throw new ComplexException(e);
  }
}
```


##### Using Results

{title: "Using Results"}
```java
public Result<String, ComplexFailure> usingResults(int number) {
  return simple.usingResults(number).map(String::toUpperCase, ComplexFailure::new);
}
```


#### Conclusion

We provided insights into the Result library's performance through benchmarking. While our metrics corroborate that most codebases could benefit from using this library instead of throwing exceptions, its main goal is to help promote best practices and implement proper error handling.

{blurb, class: information}

To address performance concerns, benchmark your applications to gain reusable insights. These should guide your decisions on selecting frameworks and libraries.

{/blurb}

{pagebreak}
