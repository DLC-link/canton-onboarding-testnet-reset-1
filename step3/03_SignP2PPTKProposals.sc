def main(): Unit = {
  import com.digitalasset.canton.version.ProtocolVersionValidation

  // Find the global synchronizer ID
  val synchronizerId = participant.synchronizers.id_of(
    com.digitalasset.canton.SynchronizerAlias.tryCreate("global")
  )

  // Read P2P proposal for signing
  val read_p2p: com.digitalasset.canton.protocol.v30.SignedTopologyTransaction =
    utils.read_first_message_from_file[
      com.digitalasset.canton.protocol.v30.SignedTopologyTransaction
    ]("input/p2p_proto.bin")
  val p2p = SignedTopologyTransaction.fromProtoV30(
    ProtocolVersionValidation.NoValidation,
    read_p2p
  )
    .getOrElse(throw new RuntimeException("Failed to convert from proto"))
    .selectMapping[PartyToParticipant]
    .getOrElse(throw new RuntimeException("Not a PartyToParticipant"))

  // Read PTK proposal for signing
  val read_ptk: com.digitalasset.canton.protocol.v30.SignedTopologyTransaction =
    utils.read_first_message_from_file[
      com.digitalasset.canton.protocol.v30.SignedTopologyTransaction
    ]("input/ptk_proto.bin")
  val ptk = SignedTopologyTransaction.fromProtoV30(
    ProtocolVersionValidation.NoValidation,
    read_ptk
  )
    .getOrElse(throw new RuntimeException("Failed to convert from proto"))
    .selectMapping[PartyToKeyMapping]
    .getOrElse(throw new RuntimeException("Not a PartyToKeyMapping"))

  // Sign the proposals
  val p2p_signed = participant.topology.transactions
    .sign(Seq(p2p), store = synchronizerId)
    .head
  val ptk_signed = participant.topology.transactions
    .sign(Seq(ptk), store = synchronizerId)
    .head

  // Save signed proposals
  utils.write_to_file(
    Seq(p2p_signed.toProtoV30, ptk_signed.toProtoV30),
    "output/signed-p2p-ptk-proposals.bin"
  )

  println("P2P and PTK proposals signed and saved.")
}

main()
