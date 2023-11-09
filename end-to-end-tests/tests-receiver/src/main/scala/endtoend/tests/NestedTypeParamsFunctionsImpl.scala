package endtoend.tests
import endtoend.tests.model.{CombinedReturn, Param1, Param2, Return1}

class NestedTypeParamsFunctionsImpl extends NestedTypeParamsFunctions:
  override def oneParam(p1: Param1): Return1                                 = Return1(p1.a)
  override def twoParams(p1: Param1, p2: Param2): Return1                    = Return1(p2.x.toInt)
  override def seqOfP1(p1l: Seq[Param1]): Seq[Return1]                       = p1l.map(oneParam)
  override def seqOfP1P2(p1l: Seq[Param1], p2l: Seq[Param2]): CombinedReturn = CombinedReturn(
    p1l.map(oneParam),
    p2l.map(p2 => Return1(p2.x.toInt))
  )
