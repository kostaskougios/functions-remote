package codegen.proxygenerator.codegenerators.model

import codegen.proxygenerator.model.EType
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
