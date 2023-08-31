package `proxypackage`

{{#imports}}
import {{.}}
{{/imports}}

class FunctionsCaller(`function1`: `methodParams` => `function1ReturnType`, `function2`: (`methodParams`.Methods, `function1ReturnType`) => Any):

  // foreach functions
  def `functionN`(`params`: String): `resultN` =
    val c  = `caseClass`(`params`)
    val r1 = `function1`(c)
    val r2 = `function2`(`methodParams`.Methods.`caseClass`, r1)
    r2.asInstanceOf[`resultN`]
  // end functions
