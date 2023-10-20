package {{proxypackage}}

import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{MediaType, Request, Response}
import cats.syntax.all.*

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

class {{className}}{{frameworkTypeArgFull}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  contentType: `Content-Type` = `Content-Type`(MediaType.application.json),
  protocol: String = "Json"
):
  private val dsl = new Http4sDsl{{exportedTypeTypeArgs}} {}
  import dsl.*
  // Override this if you want to change the paths
  def pathFor(fullClassName: String, method: String) = Root / fullClassName / method / protocol

  val allRoutes: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] = {{#functions}}{{functionN}} {{^last}}orElse{{/last}} {{/functions}}

  private def routeFor(req: Request[{{frameworkTypeArg}}], f: Array[Byte] => {{frameworkTypeArg}}[Array[Byte]]) =
    val r = for
      inData <- req.body.compile.to(Array)
      res <- f(inData)
    yield res
    Ok(r).map(_.withContentType(contentType))

{{#functions}}
  private val {{functionN}}Path = pathFor("{{proxypackage}}.{{exportedType.name}}","{{functionN}}")
  def {{functionN}}: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] =
    case req @ PUT -> p if p == {{functionN}}Path => routeFor(req, receiver.{{functionN}})


  {{/functions}}
