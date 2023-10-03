package functions.proxygenerator.codegenerators.model

import functions.tastyextractor.model.EType
import mustache.integration.model.Many

case class Vals(
    exportedType: EType,
    proxypackage: String,
    imports: Many[String],
    className: String,
    methodParams: String,
    functions: Many[Func]
)
