---
description: Measuring performance to find out how fast Results are
---

# Benchmarks

Throughout these guides, we have mentioned that throwing Java exceptions is slow. But... how slow? According to our
benchmarks, throwing an exception is several orders of magnitude slower than returning a failed result.

![Returning a failed Result object is significantly faster than throwing an exception.](https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-benchmark/badge.json)

This proves that using exceptional logic just to control normal program flow is a bad idea.

{% hint style="info" %}

We should throw exceptions sparingly, even more so when developing performance-critical applications.

{% endhint %}


## Benchmarking Result Library

This library comes with [a set of benchmarks that compare performance][RESULT_BENCHMARK] when using results versus when
using exceptions.


### Simple Scenarios

The first scenarios compare the most basic usage: a method that returns a `String` or fails, depending on a given `int`
parameter:


#### Using Exceptions

```java
public String usingExceptions(int number) throws SimpleException {
  if (number < 0) {
    throw new SimpleException(number);
  }
  return "ok";
}
```


#### Using Results

```java
public Result<String, SimpleFailure> usingResults(int number) {
  if (number < 0) {
    return Results.failure(new SimpleFailure(number));
  }
  return Results.success("ok");
}
```


### Complex Scenarios

The next scenarios do something a little bit more elaborate: a method invokes the previous method to retrieve a
`String`; if successful, then converts it to upper case; otherwise transforms the "simple" error into a "complex" error.


#### Using exceptions

```java
public String usingExceptions(int number) throws ComplexException {
  try {
    return simple.usingExceptions(number).toUpperCase();
  } catch (SimpleException e) {
    throw new ComplexException(e);
  }
}
```


#### Using results

```java
public Result<String, ComplexFailure> usingResults(int number) {
  return simple.usingResults(number)
    .map(String::toUpperCase, ComplexFailure::new);
}
```


### Conclusion

While these benchmarks corroborate that most codebases could benefit in terms of performance from using this library
instead of throwing exceptions, the main goal is to help promote best practices and implement proper error handling.

{% hint style="info" %}

If you worry about performance, you should benchmark your applications to gain reusable insights. These should guide
your decisions regarding the adoption of frameworks and libraries.

{% endhint %}


[RESULT_BENCHMARK]:             https://github.com/LeakyAbstractions/result-benchmark
