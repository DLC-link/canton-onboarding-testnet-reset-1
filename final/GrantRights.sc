def main(): Unit = {
  val userName = sys.env.getOrElse("USER_NAME", "testUser")

  val decentralizedRegistrar = participant.parties.find("cbtc-network")

   participant.ledger_api.users.rights.grant(
     userName,
     readAs = Set(decentralizedRegistrar),
     actAs = Set(decentralizedRegistrar)
   )
}

main()