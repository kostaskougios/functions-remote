# functions-remote

Note: Only for scala3. Currently, circe-json and avro4s serializations are supported.


For scala, the most important ability of the language is the ability to call a function `f(x) = y` and get its results.
And while we can do that within a jvm instance, it would be useful to be able to call functions this easily across jvms that
are running in the same box or remote boxes.

Functions-remote is a code generator for calling functions "remotely", using different serialization methods (like json or avro), and different remote transports (like http).
Remotely means we may use http as a transport (i.e. via http4s) or just use an isolated classloader as transport so that we can
execute the function locally. We'll see all these in more details below as well as why it can be useful to use different transports.

Effectively functions-remote allows the simplicity of `f(x) = y` no matter where `f` will really run. 

The generated code is very readable and as if written by a person.

# Documentation

All documentation is in the [example project](https://github.com/kostaskougios/functions-remote-examples)