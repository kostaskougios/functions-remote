package {{proxypackage}}

import functions.model.*

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

object {{className}}:
  {{! ------------------------------------ Generic --------------------------------------------- }}
  // generic factories
{{{Generic}}}
  {{! ------------------------------------ Classloader transports ---------------------------------------------- }}
{{{IsolatedClassLoader}}}
  {{! ------------------------------------ Http4s transports      ---------------------------------------------- }}
{{{Http4sTransports}}}
  {{! ------------------------------------ Helidon transports     ---------------------------------------------- }}
{{{HelidonTransports}}}

