---
description: How to get up and running with Results in no time
---

# Getting Started

{% hint style="success" %}

The best way to think of Results is as a super-powered version of Java's Optionals.

{% endhint %}

`Result` builds upon the familiar concept of `Optional`, enhancing it with the ability to represent both success and
failure states.

![No need to return null or throw an exception: just return a failed result.][GETTING_STARTED]

{% tabs %}

{% tab title="Why Results over Optionals?" %}

`Optional` class is useful for representing values that might be present or absent, eliminating the need for null
checks. However, Optionals fall short when it comes to error handling because they do not convey why a value is lacking.
`Result` addresses this limitation by encapsulating both successful values and failure reasons, offering a more
expressive way to reason about what went wrong.

{% endtab %}

{% tab title="Result API" %}

Results provide the same methods as Optionals, plus additional ones to handle failure states effectively.

| [`Optional`][OPTIONAL]                         | [`Result`][RESULT]                           |
|------------------------------------------------|----------------------------------------------|
| [isPresent][OPTIONAL_IS_PRESENT]               | [hasSuccess][RESULT_HAS_SUCCESS]             |
| [isEmpty][OPTIONAL_IS_EMPTY]                   | [hasFailure][RESULT_HAS_FAILURE]             |
| [get][OPTIONAL_GET]                            | [getSuccess][RESULT_GET_SUCCESS]             |
|                                                | [getFailure][RESULT_GET_FAILURE]             |
| [orElse][OPTIONAL_OR_ELSE]                     | [orElse][RESULT_OR_ELSE]                     |
| [orElseGet][OPTIONAL_OR_ELSE_GET]              | [orElseMap][RESULT_OR_ELSE_MAP]              |
| [stream][OPTIONAL_STREAM]                      | [streamSuccess][RESULT_STREAM_SUCCESS]       |
|                                                | [streamFailure][RESULT_STREAM_FAILURE]       |
| [ifPresent][OPTIONAL_IF_PRESENT]               | [ifSuccess][RESULT_IF_SUCCESS]               |
|                                                | [ifFailure][RESULT_IF_FAILURE]               |
| [ifPresentOrElse][OPTIONAL_IF_PRESENT_OR_ELSE] | [ifSuccessOrElse][RESULT_IF_SUCCESS_OR_ELSE] |
| [filter][OPTIONAL_FILTER]                      | [filter][RESULT_FILTER]                      |
|                                                | [recover][RESULT_RECOVER]                    |
| [map][OPTIONAL_MAP]                            | [mapSuccess][RESULT_MAP_SUCCESS]             |
|                                                | [mapFailure][RESULT_MAP_FAILURE]             |
|                                                | [map][RESULT_MAP]                            |
| [flatMap][OPTIONAL_FLATMAP]                    | [flatMapSuccess][RESULT_FLATMAP_SUCCESS]     |
| [or][OPTIONAL_OR]                              | [flatMapFailure][RESULT_FLATMAP_FAILURE]     |
|                                                | [flatMap][RESULT_FLATMAP]                    |

{% endtab %}

{% endtabs %}

By leveraging Results, you can unleash a powerful tool for error handling that goes beyond the capabilities of
traditional Optionals, leading to more robust and maintainable Java code.


## Results in a Nutshell

In Java, methods that can fail typically do so by throwing exceptions. Then, exception-throwing methods are called from
inside a `try` block to handle errors in a separate `catch` block.

![Using Exceptions][USING_EXCEPTIONS]

This approach is lengthy, and that's not the only problem — it's also very slow.

{% hint style="info" %}

Conventional wisdom says **exceptional logic shouldn't be used for normal program flow**. Results make us deal with
expected error situations explicitly to enforce good practices and make our programs [run faster][BENCHMARKS].

{% endhint %}

Let's now look at how the above code could be refactored if `connect()` returned a `Result` object instead of throwing
an exception.

![Using Results][USING_RESULTS]

In the example above, we used only 4 lines of code to replace the 10 that worked for the first one. But we can
effortlessly make it shorter by chaining methods. In fact, since we were returning `-1` just to signal that the
underlying operation failed, we are better off returning a `Result` object upstream. This will allow us to compose
operations on top of `getServerUptime()` just like we did with `connect()`.

![Embracing Results][EMBRACING_RESULTS]

{% hint style="success" %}

`Result` objects are immutable, providing thread safety without the need for synchronization. This makes them ideal for
multi-threaded applications, ensuring predictability and eliminating side effects.

{% endhint %}


[BENCHMARKS]:                   ../../extra/benchmarks.md
[EMBRACING_RESULTS]:            ../../.gitbook/assets/embracing-results.png
[GETTING_STARTED]:              ../../.gitbook/assets/getting-started.png
[OPTIONAL]:                     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html
[OPTIONAL_FILTER]:              https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#filter(java.util.function.Predicate)
[OPTIONAL_FLATMAP]:             https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#flatMap(java.util.function.Function)
[OPTIONAL_GET]:                 https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#get()
[OPTIONAL_IF_PRESENT]:          https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#ifPresent(java.util.function.Consumer)
[OPTIONAL_IF_PRESENT_OR_ELSE]:  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#ifPresentOrElse(java.util.function.Consumer,java.lang.Runnable)
[OPTIONAL_IS_EMPTY]:            https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#isEmpty()
[OPTIONAL_IS_PRESENT]:          https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#isPresent()
[OPTIONAL_MAP]:                 https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#map(java.util.function.Function)
[OPTIONAL_OR]:                  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#or(java.util.function.Supplier)
[OPTIONAL_OR_ELSE]:             https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#orElse(java.lang.Object)
[OPTIONAL_OR_ELSE_GET]:         https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#orElseGet(java.util.function.Supplier)
[OPTIONAL_STREAM]:              https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#stream()
[RESULT]:                       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html
[RESULT_FILTER]:                https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#filter-java.util.function.Predicate-java.util.function.Function-
[RESULT_FLATMAP]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMap-java.util.function.Function-java.util.function.Function-
[RESULT_FLATMAP_FAILURE]:       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMapFailure-java.util.function.Function-
[RESULT_FLATMAP_SUCCESS]:       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMapSuccess-java.util.function.Function-
[RESULT_GET_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getFailure--
[RESULT_GET_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getSuccess--
[RESULT_HAS_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#hasFailure--
[RESULT_HAS_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#hasSuccess--
[RESULT_IF_FAILURE]:            https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#ifFailure-java.util.function.Consumer-
[RESULT_IF_SUCCESS]:            https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#ifSuccess-java.util.function.Consumer-
[RESULT_IF_SUCCESS_OR_ELSE]:    https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#ifSuccessOrElse-java.util.function.Consumer-java.util.function.Consumer-
[RESULT_MAP]:                   https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#map-java.util.function.Function-java.util.function.Function-
[RESULT_MAP_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#mapFailure-java.util.function.Function-
[RESULT_MAP_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#mapSuccess-java.util.function.Function-
[RESULT_OR_ELSE]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#orElse-S-
[RESULT_OR_ELSE_MAP]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#orElseMap-java.util.function.Function-
[RESULT_RECOVER]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#recover-java.util.function.Predicate-java.util.function.Function-
[RESULT_STREAM_FAILURE]:        https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#streamFailure--
[RESULT_STREAM_SUCCESS]:        https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#streamSuccess--
[USING_EXCEPTIONS]:             ../../.gitbook/assets/using-exceptions.png
[USING_RESULTS]:                ../../.gitbook/assets/using-results.png
