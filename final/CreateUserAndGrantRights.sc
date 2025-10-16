// NOTE: USER_NAME for Auth0 users should be what the Auth0 returns as the 'sub' claim in the token.
// For example: "ZmmOqQCmB4qQmZhKSd3xWp49WptIJ9sV@clients"

// Script to Create a new Canton User and grant rights
// This is a supplemental script for Auth0 users, where sub claims are not settable later.
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

  // Find the actual Party object.
  // In this script, we throw an error if the party is not found
  val existingParty = participant.parties.list(name).map(_.party)
  val party = existingParty match {
    case Vector(singleParty) => singleParty
    case _ =>
      System.err.println(s"Error: Party '$name' not found. Please set the attestor party created in Step 0 as the PARTY_NAME env var")
      sys.exit(1)
  }

  val decentralizedRegistrar = participant.parties.find("cbtc-network")

  // This call requires a JWT token for the participantAdmin,
  // and access to the ledger_api port of the node
  val user = participant.ledger_api.users.create(
    id = userName,
    actAs = Set(party, decentralizedRegistrar),
    readAs = Set(party, decentralizedRegistrar),
    primaryParty = Some(party),
    participantAdmin = false,
    annotations = Map("description" -> "Attestor Party")
  )
}

main()
