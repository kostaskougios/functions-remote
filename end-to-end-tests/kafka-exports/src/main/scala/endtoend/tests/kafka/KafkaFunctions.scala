package endtoend.tests.kafka

import endtoend.tests.kafka.model.Person

/** Mark it as exported: //> exported
  */
trait KafkaFunctions:
  def addPerson(key: String)(ttl: Long, p: Person): Unit
