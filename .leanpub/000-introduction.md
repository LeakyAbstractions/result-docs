
{#introduction}
# Introduction

{width: "100%"}
![](result.svg)

{blurb, class: center}

### A library to handle success and failure without exceptions
\
***Wave goodbye to slow exceptions and embrace clean, efficient error handling by encapsulating operations that may succeed or fail in a type-safe way.***

{width: "100%", column-widths: "* * *"}
|   |   |   |
|:-:|:-:|:-:|
| ![](tachometer-alt.svg) | ![](tint.svg) | ![](bolt.svg) |
| **Boost Performance** | **Simple API** | **Streamlined Error Handling** |
| Avoid exception overhead and benefit from faster operations | Leverage a familiar interface for a smooth learning curve | Handle failure explicitly to simplify error propagation |
| ![](shield-alt.svg) | ![](glasses.svg) | ![](filter.svg) |
| **Safe Execution** | **Enhanced Readability** | **Functional Style** |
| Ensure safer and more predictable operation outcomes | Reduce complexity to make your code easier to understand | Embrace elegant, functional programming paradigms |
| ![](feather-alt.svg) | ![](balance-scale.svg) | ![](mug-hot.svg) |
| **Lightweight** | **Open Source** | **Pure Java** |
| Keep your project slim with no extra dependencies | Enjoy transparent, permissive Apache 2 licensing | Seamless compatibility from JDK8 to the latest versions |

{/blurb}

{pagebreak}

`Result` objects represent the outcome of an operation, removing the need to check for null. Operations that succeed produce results encapsulating a *success* value; operations that fail produce results with a *failure* value. Success and failure can be represented by whatever types make the most sense for each operation.

## Results in a Nutshell

In Java, methods that can fail typically do so by throwing exceptions. Then, exception-throwing methods are called from inside a `try` block to handle errors in a separate `catch` block.

![Using Exceptions](using-exceptions.png)

This approach is lengthy, and that's not the only problem — it's also very slow.

{blurb, class: information}

Conventional wisdom says **exceptional logic shouldn't be used for normal program flow**. Results make us deal with expected error situations explicitly to enforce good practices and make our programs [run faster](#benchmarks).

{/blurb}

{pagebreak}

Let's now look at how the above code could be refactored if `connect()` returned a `Result` object instead of throwing an exception.

![Using Results](using-results.png)

In the example above, we used only 4 lines of code to replace the 10 that worked for the first one. But we can effortlessly make it shorter by chaining methods. In fact, since we were returning `-1` just to signal that the underlying operation failed, we are better off returning a `Result` object upstream. This will allow us to compose operations on top of `getServerUptime()` just like we did with `connect()`.

![Embracing Results](embracing-results.png)

`Result` objects are immutable, providing thread safety without the need for synchronization. This makes them ideal for multi-threaded applications, ensuring predictability and eliminating side effects.

{pagebreak}
