package {{proxypackage}}

import functions.model.Serializer

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
{{{Generic}}}
  {{! ------------------------------------ Http4s ---------------------------------------------- }}
{{{Http4s}}}