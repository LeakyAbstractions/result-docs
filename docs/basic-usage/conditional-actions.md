---
layout: page
title: Conditional Actions
description: Handling success and failure
hero_height: is-small
menubar: result_docs
---

# Conditional Actions

The `if...` family of methods enables us to run some code on the wrapped success/failure value.

## Handling Exceptions

Before `Result`, we would wrap exception-throwing `foobar` method invocation inside a `try` block so that errors can be handled inside a `catch` block:

```java
try {
    final String result = foobar();
    this.commit(result);
} catch(SomeException problem) {
    this.rollback(problem);
}
```

## Handling Results

Let's now look at how the above code could be refactored if method `foobar` returned a `Result` object instead of throwing an exception:

```java
final Result<String, SomeFailure> result = foobar();
result.ifSuccessOrElse(this::commit, this::rollback);
```

The first action passed to [`Result::ifSuccessOrElse`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#ifSuccessOrElse-java.util.function.Consumer,java.util.function.Consumer-) will be performed if `foobar` succeeded; otherwise, the second one will.

The above example is not only shorter but also faster. We can make it even shorter by chaining methods together in typical functional programming style:

```java
foobar().ifSuccessOrElse(this::commit, this::rollback);
```

There are other methods [`Result::ifSuccess`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#ifSuccess-java.util.function.Consumer-) and [`Result::ifFailure`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#ifFailure-java.util.function.Consumer-) to handle either one of the success/ failure cases:

```java
foobar()
    .ifSuccess(this::commit)    // commits only if foobar succeeded
    .ifFailure(this::rollback); // rolls back only if foobar failed
```
