import com.google.protobuf.ByteString

def main(): Unit = {
  // Check if the cbtc-network-namespace key already exists
  val existingNamespaceKey = participant.keys.secret.list().find(key =>
    key.name.exists(_.unwrap == "cbtc-network-namespace")
  )
  
  val namespaceKey: com.digitalasset.canton.crypto.SigningPublicKey = existingNamespaceKey match {
    case Some(key) => 
      println("Found existing cbtc-network-namespace key, using fingerprint: " + key.publicKey.fingerprint)
      key.publicKey.asInstanceOf[com.digitalasset.canton.crypto.SigningPublicKey]
    case None =>
      println("Creating new cbtc-network-namespace key")
      // This creates a signing key that will only be used for namespace management. In the decentralized 
      // party context, the namespace is the unique identifier for the party's topology and governance.
      participant.keys.secret.generate_signing_key(
        "cbtc-network-namespace",
        SigningKeyUsage.NamespaceOnly
      )
  }

  // The namespace is derived from the fingerprint (hash) of your namespace signing key.
  val namespace = Namespace(namespaceKey.fingerprint)

  // Canton uses Synchronizers as trust domains for transaction coordination.
  // We'll register this namespace in the global coordination network.
  val synchronizerId = participant.synchronizers.id_of(
    com.digitalasset.canton.SynchronizerAlias.tryCreate("global")
  )

  // Check if namespace delegation already exists
  val existingDelegations = participant.topology.namespace_delegations.list(
    store = synchronizerId,
    proposals = false,
    timeQuery = com.digitalasset.canton.topology.store.TimeQuery.HeadState,
    filterNamespace = namespace.toProtoPrimitive,
  )
  
  if (existingDelegations.nonEmpty) {
    println("Namespace delegation already exists, skipping...")
  } else {
    // Propose a delegation for the namespace
    participant.topology.namespace_delegations.propose_delegation(
      namespace,
      namespaceKey,
      DelegationRestriction.CanSignAllMappings,
      store = synchronizerId
    )
    println("Successfully proposed namespace delegation")
  }

  // Check if the cbtc-network-daml-transactions key already exists
  val existingDamlKey = participant.keys.secret.list().find(key =>
    key.name.exists(_.unwrap == "cbtc-network-daml-transactions")
  )
  
  val damlKey: com.digitalasset.canton.crypto.SigningPublicKey = existingDamlKey match {
    case Some(key) => 
      println("Found existing cbtc-network-daml-transactions key, using fingerprint: " + key.publicKey.fingerprint)
      key.publicKey.asInstanceOf[com.digitalasset.canton.crypto.SigningPublicKey]
    case None =>
      println("Creating new cbtc-network-daml-transactions key")
      // Generate a DAML key for the decentralized party
      participant.keys.secret.generate_signing_key(
        "cbtc-network-daml-transactions",
        SigningKeyUsage.ProtocolOnly
      )
  }

  utils.write_to_file(
    Seq(namespaceKey.toProtoV30, damlKey.toProtoV30),
    "output/attestor-public-keys.bin"
  )
  utils.write_to_file(ByteString.copyFromUtf8(participant.id.toProtoPrimitive), "output/participant-id.bin")
  println("Keys generated and saved to output/attestor-public-keys.bin and output/participant-id.bin")
}

main()
