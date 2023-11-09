package example

import endtoend.tests.kafka.KafkaFunctionsCallerFactory
import endtoend.tests.kafka.model.Person
import functions.kafka.KafkaTransport

@main
def kafkaProducer() =
  val transport = KafkaTransport("person", KafkaConf.props)
  val f         = KafkaFunctionsCallerFactory.newAvroKafkaFunctions(transport.transportFunction)
  try
    f.addPerson("kostas")(2000, Person(5, "KostasK"))
    f.removePerson("kostas-remove")(Person(6, "KostasR"))
    println("OK")
  finally transport.close()
