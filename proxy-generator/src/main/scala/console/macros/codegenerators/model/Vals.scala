package console.macros.codegenerators.model

import console.macros.model.EType
import mustache.integration.model.Many

case class Vals(
    config: Config,
    exportedType: EType,
    proxypackage: String,
    imports: Many[String],
    className: String,
    methodParams: String,
    functions: Many[Func]
)
