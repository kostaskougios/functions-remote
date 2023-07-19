package `proxypackage`

/*=imports*/

class `functionsCaller`(`function1`: `methodParams` => `function1ReturnType`, `function2`: `function1ReturnType` => Any):

  // foreach functions
  def `functionN`(`params`: String): `resultN` =
    val c  = `caseClass`(`params`)
    val r1 = `function1`(c)
    val r2 = `function2`(r1)
    r2.asInstanceOf[`resultN`]
  // end functions
