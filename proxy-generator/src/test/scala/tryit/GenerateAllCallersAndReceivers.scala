package tryit

/** Dev friendly way of generating all test generated classes.
  */
@main
def generateAllCallersAndReceivers(): Unit =
  generateReceiverAndCallerApp()
  generateCatsExporterAndImporter()
  generateKafkaReceiverAndCallerApp()
  generateHelidonReceiverAndCallerApp()
