package {{proxypackage}}

import functions.model.Serializer
import functions.model.ReceiverInput

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
{{{Generic}}}
  {{! ------------------------------------ Http4s ---------------------------------------------- }}
{{{Http4s}}}