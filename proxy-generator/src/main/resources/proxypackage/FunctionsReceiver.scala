package {{proxypackage}}

import functions.model.FunctionsReceiver
{{#imports}}
import {{.}}
{{/imports}}

class {{className}}{{frameworkTypeArgFull}}(
  {{#functions}}
    {{functionN}}Deserializer: Array[Byte] => {{methodParams}}.{{caseClassName}},
    {{functionN}}ReturnTypeSerializer: {{resultNNoFramework}} => Array[Byte],
  {{/functions}}
    f: {{exportedTypeFull}}
) extends FunctionsReceiver:
  override val invoke: PartialFunction[(String, Array[Byte]) , Array[Byte]] =
    {{#functions}}
      case ({{methodParams}}.Methods.{{caseClassName}}, data) => {{functionN}}(data)
    {{/functions}}

  {{#functions}}
  def {{functionN}}(data: Array[Byte]): Array[Byte] =
    val params = {{functionN}}Deserializer(data)
    {{#mapResults}}
    f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}}).map: r=>
      {{functionN}}ReturnTypeSerializer(r)
    {{/mapResults}}
    {{^mapResults}}
    val r      = f.{{functionN}}({{#paramsRaw}}params.{{name}}{{^last}}, {{/last}}{{/paramsRaw}})
    {{functionN}}ReturnTypeSerializer(r)
    {{/mapResults}}
  {{/functions}}
