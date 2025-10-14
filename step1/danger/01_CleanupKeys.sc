def main(): Unit = {
  // ATTENTION! DO NOT RUN THIS WITHOUT CONTACTING WITH Bitsafe's engineers
  println("Starting cleanup of keys created by GenerateKeys.sc...")
  
  // Find and remove ALL cbtc-network-namespace keys
  val namespaceKeys = participant.keys.secret.list().filter(key =>
    key.name.exists(_.unwrap == "cbtc-network-namespace")
  )
  
  if (namespaceKeys.nonEmpty) {
    println(s"Found ${namespaceKeys.size} cbtc-network-namespace key(s)")
    namespaceKeys.foreach { key =>
      println(s"Removing cbtc-network-namespace key with fingerprint: ${key.publicKey.fingerprint}")
      try {
        participant.keys.secret.delete(key.publicKey.fingerprint, force = true)
        println("✓ Key removed successfully")
      } catch {
        case e: Exception =>
          println(s"Failed to remove key: ${e.getMessage}")
          println("Note: If prompted for confirmation, type 'y' and press Enter")
          participant.keys.secret.delete(key.publicKey.fingerprint)
          println("✓ Key removed successfully")
      }
    }
  } else {
    println("ℹ No cbtc-network-namespace keys found (already removed or never created)")
  }
  
  // Find and remove ALL cbtc-network-daml-transactions keys
  val damlKeys = participant.keys.secret.list().filter(key =>
    key.name.exists(_.unwrap == "cbtc-network-daml-transactions")
  )
  
  if (damlKeys.nonEmpty) {
    println(s"Found ${damlKeys.size} cbtc-network-daml-transactions key(s)")
    damlKeys.foreach { key =>
      println(s"Removing cbtc-network-daml-transactions key with fingerprint: ${key.publicKey.fingerprint}")
      try {
        participant.keys.secret.delete(key.publicKey.fingerprint, force = true)
        println("✓ Key removed successfully")
      } catch {
        case e: Exception =>
          println(s"Failed to remove key: ${e.getMessage}")
          println("Note: If prompted for confirmation, type 'y' and press Enter")
          participant.keys.secret.delete(key.publicKey.fingerprint)
          println("✓ Key removed successfully")
      }
    }
  } else {
    println("ℹ No cbtc-network-daml-transactions keys found (already removed or never created)")
  }
  
  println("Key cleanup completed!")
}

main()