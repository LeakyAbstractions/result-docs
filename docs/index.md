---
layout: page
title: Result Library Docs
description: A Java library to handle success and failure without exceptions
menubar: my_menubar
---

# Introduction

<figure><img src="https://raw.githubusercontent.com/LeakyAbstractions/result/main/docs/result-magic-ball.png" alt=""><figcaption></figcaption></figure>

The purpose of this library is to provide a type-safe encapsulation of operation results that may have succeeded or failed, instead of throwing exceptions.

If you like `Optional` but feel that it sometimes falls too short, you'll love `Result`.

The best way to think of `Result` is as a super-powered version of `Optional`. The only difference is that whereas `Optional` may contain a successful value or express the absence of a value, `Result` contains either a successful value or a failure value that explains what went wrong.

`Result` objects have methods equivalent to those in `Optional`, plus a few more to handle failure values.

| Optional          | Result            |
| ----------------- | ----------------- |
| `isPresent`       | `isSuccess`       |
| `isEmpty`         | `isFailure`       |
| `get`             |                   |
| `orElse`          | `orElse`          |
| `orElseGet`       | `orElseMap`       |
| `orElseThrow`     |                   |
|                   | `optional`        |
|                   | `optionalFailure` |
| `stream`          | `stream`          |
|                   | `streamFailure`   |
| `ifPresent`       | `ifSuccess`       |
|                   | `ifFailure`       |
| `ifPresentOrElse` | `ifSuccessOrElse` |
| `filter`          | `filter`          |
|                   | `recover`         |
| `map`             | `mapSuccess`      |
|                   | `mapFailure`      |
|                   | `map`             |
| `flatMap`         | `flatMapSuccess`  |
| `or`              | `flatMapFailure`  |
|                   | `flatMap`         |

### Result Library in a Nutshell

Before `Result`, we would wrap exception-throwing `foo` method invocation inside a `try` block so that errors can be handled inside a `catch` block.

```java
public int getFooLength() {
  int length;
  try {
    String result = foo();
    this.ok(result);
    length = result.length();
  } catch(SomeException problem) {
    this.error(problem);
    length = -1;
  }
  return length;
}
```

This approach is lengthy, and that's not the only problem -- it's also slow. Conventional wisdom says that exceptional logic shouldn't be used for normal program flow. `Result` makes us deal with expected, non-exceptional error situations explicitly as a way of enforcing good programming practices.

Let's now look at how the above code could be refactored if method `foo` returned a `Result` object instead of throwing an exception:

```java
public int getFooLength() {
  Result<String, SomeFailure> result = foo();
  result.ifSuccessOrElse(this::ok, this::error);
  Result<Integer, SomeFailure> resultLength = result.mapSuccess(String::length);
  return resultLength.orElse(-1);
}
```

In the above example, we use only four lines of code to replace the ten that worked in the first example. But we can make it even shorter by chaining methods in typical functional programming style:

```java
public int getFooLength() {
  return foo().ifSuccessOrElse(this::ok, this::error).mapSuccess(String::length)
    .orElse(-1);
}
```

In fact, since we are using `-1` here just to signal that the underlying operation failed, we'd be better off returning a `Result` object upstream:

```java
public Result<Integer, SomeFailure> getFooLength() {
  return foo().ifSuccessOrElse(this::ok, this::error).mapSuccess(String::length);
}
```

This allows others to easily compose operations on top of ours, just like we did with `foo`.
