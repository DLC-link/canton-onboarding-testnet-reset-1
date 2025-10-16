def main(): Unit = {
  val userName = sys.env.getOrElse("USER_NAME", "testUser")

  val rights = participant.ledger_api.users.rights.list(id = userName)

  println(s"User Rights for '$userName':")
  println("=" * 40)
  
  // Try to access individual rights fields
  try {
    println(s"Act As Parties: ${rights.actAs}")
    println(s"Read As Parties: ${rights.readAs}")
  } catch {
    case _: Exception =>
      println(s"Rights (raw): $rights")
      println("Note: Unable to parse individual rights fields")
  }
  
  println("=" * 40)
}

main()
