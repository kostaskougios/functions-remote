# functions-remote

It would be nice if there was a simple way to call remote functions as if they were local. RPC but with a twist that we'll see below.

Functions-remote is a code generator for calling functions "remotely", using different serialization methods (like json or avro), and different remote transports (like http).
Remotely means we may use http as a transport (i.e. via http4s) or just use an isolated classloader as transport. We'll
see all these in more details below.

Note: Only for scala3. Currently, circe-json and avro4s serializations are supported.

The generated code is very readable and as if written by a person.

## How it works

Let's say we have the following functionality that simulates the `ls` command line tool:

```scala
trait LsFunctions:
  def ls(path: String, lsOptions: LsOptions = LsOptions.Defaults): LsResult
  def fileSize(path: String): Long
```

We can then use functions-remote to create code for us that:
- create case classes for the parameters of each method in the trait i.e. `case class Ls(path: String, lsOptions: LsOptions)`
- create serializers for json, avro etc so that we can serialize/deserialize `Ls` and `LsResult`
- create a `class LsFunctionsCaller extends LsFunctions` which can be used to call remotely our LsFunctions and use any serialization method available. The `Caller` extends our trait and internally serializes the args to `Array[Byte]` and sends them to the `transport`.
- create a `transport` for http4s, or locally via a classloader (we'll see that later on) etc
- create an `LsFunctionsReceiver` that receives the `Array[Byte]` and converts it to a call to `LsFunctionsImpl`

## Architecture and Terminology

A call to a function is done via a generated `Caller` class, serialized, transferred to the `Receiver` via a `transport` and the `Receiver` takes 
care of actually calling the function implementation.

Our code invokes `LsFunctions.ls()` -> `ls()` args are copied to the generated `Ls` case class -> `Ls` is serialized to `Array[Byte]` -> the transport is used to transfer the bytes to the `Receiver` -> on the `Receiver` side we deserialize `Ls` and use it to invoke the actual `LsFunctionsImpl` -> `LsResult` is serialized and send back to the caller

