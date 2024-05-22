---
layout: page
title: Library Performance
description: Measuring performance to find out how fast results are
hero_height: is-small
menubar: result_docs
---

# Library Performance

We established that throwing Java exceptions is slow. But... how slow? According to our benchmarks, returning a failed result is several orders of magnitude faster than throwing an exception.

<figure><img src="https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-benchmark/badge.json" alt="" width="563"><figcaption><p>Returning a failed result is several orders of magnitude faster than throwing an exception</p></figcaption></figure>

This proves our initial point that it's a really bad idea to use exceptional logic just to control normal program flow.

{% include
  notification.html
  status="is-info"
  icon="fas fa-info-circle"
  message="You should throw exceptions sparingly in your code, even more so when developing performance-sensitive applications." %}

## Result Library Benchmarks

This library comes with [a set of benchmarks that compare performance](https://github.com/LeakyAbstractions/result-benchmark) when using results versus when using exceptions.

### Simple Scenarios

The first scenarios compare the most basic usage: a method that returns a `String` or fails, depending on a given `int` parameter:

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

The next scenarios do something a little bit more elaborate: a method invokes the previous method to retrieve a `String`; if successful, then converts it to upper case; otherwise transforms the "simple" error into a "complex" error.

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

While these benchmarks corroborate that most codebases could benefit in terms of performance from using this library instead of using exceptions, the main goal is to help promote best practices and implement proper error handling.

{% include
  notification.html
  status="is-info"
  icon="fas fa-info-circle"
  message="If you worry about performance, you should benchmark your own applications to gain reusable insights. These should guide your decisions regarding the adoption of frameworks and libraries." %}
