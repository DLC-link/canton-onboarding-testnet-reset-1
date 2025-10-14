def main(): Unit = {
  val name = sys.env.get("PARTY_NAME") match {
    case Some(value) => value
    case None =>
      System.err.println("Error: environment variable PARTY_NAME is missing!")
      sys.exit(1)  // stop execution with error code
  }

  val userName = sys.env.get("USER_NAME") match {
    case Some(value) => value
    case None =>
      System.err.println("Error: environment variable USER_NAME is missing!")
      sys.exit(1)
  }

  // This call requires just access to the admin_api port of the node,
  // it creates a party if it doesn't exist
  val existingParty = participant.parties.list(name).map(_.party)
  val party = existingParty match {
    case Vector(singleParty) => singleParty
    case _ => participant.parties.enable(name = name)
  }

  // This call requires a JWT token for the participantAdmin,
  // and access to the ledger_api port of the node
  val user = participant.ledger_api.users.create(
    id = userName,
    actAs = Set(party),
    readAs = Set(party),
    primaryParty = Some(party),
    participantAdmin = false,
    annotations = Map("description" -> "Attestor Party")
  )
}

main()
