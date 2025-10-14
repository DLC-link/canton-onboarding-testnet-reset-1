def main(): Unit = {
  val name = sys.env.get("PARTY_NAME") match {
    case Some(value) => value
    case None =>
      System.err.println("Error: environment variable PARTY_NAME is missing!")
      sys.exit(1)
  }

  val userName = sys.env.get("USER_NAME") match {
    case Some(value) => value
    case None =>
      System.err.println("Error: environment variable USER_NAME is missing!")
      sys.exit(1)
  }

  val existingParty = participant.parties.list(name)
  println("\n-- Party:")
  existingParty.foreach { result =>
    println(s"Party: ${result.party}")
    println("Participants:")
    result.participants.foreach { participantSync =>
      println(s"  Participant: ${participantSync.participant}")
      println("  Synchronizers:")
      participantSync.synchronizers.foreach { syncPermission =>
        println(s"    Synchronizer ID: ${syncPermission.synchronizerId}, Permission: ${syncPermission.permission}")
      }
    }
  }

  println("\n-- User:")
  val user = participant.ledger_api.users.get(userName)
  println(s"""User:
    |  ID: ${user.id}
    |  Primary Party: ${user.primaryParty.getOrElse("None")}
    |  Deactivated: ${user.isDeactivated}
    |  Annotations:
    |${user.annotations.map { case (k,v) => s"    $k -> $v" }.mkString("\n")}
    |  Identity Provider ID: ${user.identityProviderId}
  """.stripMargin)
}

main()