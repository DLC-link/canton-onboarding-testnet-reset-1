def main(): Unit = {
  import com.digitalasset.canton.version.ProtocolVersionValidation

  // Find the global synchronizer ID
  val synchronizerId = participant.synchronizers.id_of(
    com.digitalasset.canton.SynchronizerAlias.tryCreate("global")
  )

  // Read and unwrap the DNS topology proposal from disk
  val read_dns: com.digitalasset.canton.protocol.v30.SignedTopologyTransaction =
    utils.read_first_message_from_file[
      com.digitalasset.canton.protocol.v30.SignedTopologyTransaction
    ]("input/dns_proto.bin")
  val unwrapped_dns = SignedTopologyTransaction.fromProtoV30(
    ProtocolVersionValidation.NoValidation,
    read_dns
  )
  val dns = unwrapped_dns
    .getOrElse(throw new RuntimeException("Failed to convert from proto"))
    .selectMapping[DecentralizedNamespaceDefinition]
    .getOrElse(
      throw new RuntimeException(
        "Deserialized transaction was not a DecentralizedNamespaceDefinition"
      )
    )

  val dns_signed = participant.topology.transactions
    .sign(Seq(dns), store = synchronizerId)
    .head

  utils.write_to_file(
    Seq(dns_signed.toProtoV30),
    "output/signed-dns-proposal.bin"
  )
}

main()
