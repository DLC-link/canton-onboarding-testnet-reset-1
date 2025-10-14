def main(): Unit = {
  // Get all keys
  val allKeys = participant.keys.secret.list()
  
  // Find keys with specific names
  val namespaceKeys = allKeys.filter(key =>
    key.name.exists(_.unwrap == "cbtc-network-namespace")
  )
  
  val transactionKeys = allKeys.filter(key =>
    key.name.exists(_.unwrap == "cbtc-network-daml-transactions")
  )
  
  // Print namespace keys
  println("=== cbtc-network-namespace keys ===")
  if (namespaceKeys.isEmpty) {
    println("No cbtc-network-namespace keys found")
  } else {
    namespaceKeys.foreach { key =>
      println(s"Fingerprint: ${key.publicKey.fingerprint}")
    }
    if (namespaceKeys.length > 1) {
      println("WARNING! You have more than one cbtc-network-namespace key - contact Bitsafe")
    }
  }
  
  // Print transaction keys
  println("\n=== cbtc-network-daml-transactions keys ===")
  if (transactionKeys.isEmpty) {
    println("No cbtc-network-daml-transactions keys found")
  } else {
    transactionKeys.foreach { key =>
      println(s"Fingerprint: ${key.publicKey.fingerprint}")
    }
    if (transactionKeys.length > 1) {
      println("WARNING! You have more than one cbtc-network-daml-transactions key - contact Bitsafe")
    }
  }
  
  // Overall warning if any duplicates found
  if (namespaceKeys.length > 1 || transactionKeys.length > 1) {
    println("\n*** ATTENTION: Multiple keys detected! Please contact Bitsafe for assistance ***")
  }
}

main()

