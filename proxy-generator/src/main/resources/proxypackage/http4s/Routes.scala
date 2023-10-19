package {{proxypackage}}

import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{Request, Response}
import cats.syntax.all.*

{{#frameworkImports}}
import {{.}}
{{/frameworkImports}}

class {{className}}{{frameworkTypeArgFull}}(
  receiver: {{exportedType.name}}Receiver{{exportedTypeTypeArgs}},
  mediaType: `Content-Type`
):
  private val dsl = new Http4sDsl{{exportedTypeTypeArgs}} {}
  import dsl.*

  def route1: PartialFunction[Request{{exportedTypeTypeArgs}}, {{frameworkTypeArg}}[Response{{exportedTypeTypeArgs}}]] =
    case req @ PUT -> Root / "catsAddR" =>
      val r = for
        inData <- req.body.compile.to(Array)
        res    <- receiver.catsAddR(inData)
      yield res
      Ok(r).map(_.withContentType(mediaType))
