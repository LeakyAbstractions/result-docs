
{#getting-started}
# Getting Started

> ***How to get up and running with Results in no time***

The best way to think of Results is as a super-powered version of Java's Optionals.

`Result` builds upon the familiar concept of `Optional`, enhancing it with the ability to represent both success and failure states.


## Result API

Results provide the same methods as Optionals, plus additional ones to handle failure states effectively.

| `Optional`        | `Result`          |
|-------------------|-------------------|
| `isPresent`       | `hasSuccess`      |
| `isEmpty`         | `hasFailure`      |
| `get`             | `getSuccess`      |
|                   | `getFailure`      |
| `orElse`          | `orElse`          |
| `orElseGet`       | `orElseMap`       |
| `stream`          | `streamSuccess`   |
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

{pagebreak}


## Why Results over Optionals?

`Optional` class is useful for representing values that might be present or absent, eliminating the need for null checks. However, Optionals fall short when it comes to error handling because they do not convey why a value is lacking. `Result` addresses this limitation by encapsulating both successful values and failure reasons, offering a more expressive way to reason about what went wrong.

![No need to return null or throw an exception: just return a failed result.](getting-started.png)

By leveraging Results, you can unleash a powerful tool for error handling that goes beyond the capabilities of traditional Optionals, leading to more robust and maintainable Java code.

{pagebreak}
