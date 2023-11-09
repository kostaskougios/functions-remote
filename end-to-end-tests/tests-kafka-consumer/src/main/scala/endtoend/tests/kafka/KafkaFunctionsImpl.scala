package endtoend.tests.kafka
import endtoend.tests.kafka.model.Person

class KafkaFunctionsImpl extends KafkaFunctions:
  override def addPerson(key: String)(ttl: Long, p: Person): Unit =
    println(s"addPerson: key = $key , value = $ttl , $p")

  override def removePerson(key: String)(p: Person): Unit =
    println(s"removePerson: key = $key , value = $p")
