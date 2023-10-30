# functions-remote

It would be nice if there was a simple way to call remote functions as if they were local. RPC but with a twist that we'll see below.

So lets say we have a function `def add(a: Int, b: Int): Int`. With functions-remote we will be able to call it on machine A normally (i.e. `add(5,6)`) 
and it's implementation be executed on machine B. The result then becomes available on A. All these with configurable serialization and transports.

Functions-remote is a code generator for calling functions "remotely", using different serialization methods (like json or avro), and different remote transports (like http).
Remotely means we may use http as a transport (i.e. via http4s) or just use an isolated classloader as transport so that we can
execute the function locally. We'll see all these in more details below as well as why it can be useful to use different transports.

Note: Only for scala3. Currently, circe-json and avro4s serializations are supported.

The generated code is very readable and as if written by a person.

# Documentation

All documentation is in the [example project](https://github.com/kostaskougios/functions-remote-examples)