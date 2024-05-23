---
layout: page
title: Advanced Usage
subtitle: How to take `Result` objects to the next level
description: How to take Result objects to the next level
hero_height: is-small
menubar: result_docs
---

# Advanced Usage

While retrieving success/failure values out of `Result` objects can be convenient sometimes, what's in fact more idiomatic is to manipulate the value inside a result without actually unwrapping it. Most of the time, we will apply transformations to a result instance, obtaining a possibly different result object in return. This allows us to compose behavior in a [monadic way](https://en.wikipedia.org/wiki/Monad\_\(functional\_programming\)).

<figure><img src="../.gitbook/assets/advanced-usage (1).png" alt=""><figcaption><p>Results can be filtered and transformed just like Java streams.</p></figcaption></figure>
