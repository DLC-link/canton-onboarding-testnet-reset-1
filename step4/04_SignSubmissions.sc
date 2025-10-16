import com.digitalasset.canton.crypto
import com.digitalasset.canton.resource
import com.digitalasset.canton.version

def main() {
  // Get fingerprint set up in the 1st step
  val keyFingerprint = participant.keys.secret
    .list()
    .find(p => p.name == Some("cbtc-network-daml-transactions"))
    .get
    .id

  // Read the prepared submissions from the input folder
  val sub = utils.read_byte_string_from_file("./input/prepared-submission-1.bin")
  val preparedSubmission =
    com.daml.ledger.api.v2.interactive.interactive_submission_service.PrepareSubmissionResponse
      .parseFrom(sub.toByteArray())

  val sub2 =
    utils.read_byte_string_from_file("./input/prepared-submission-2.bin")
  val preparedSubmission2 =
    com.daml.ledger.api.v2.interactive.interactive_submission_service.PrepareSubmissionResponse
      .parseFrom(sub2.toByteArray())

  val sub3 =
    utils.read_byte_string_from_file("./input/prepared-submission-3.bin")
  val preparedSubmission3 =
    com.daml.ledger.api.v2.interactive.interactive_submission_service.PrepareSubmissionResponse
      .parseFrom(sub3.toByteArray())

  // Create a temporary storage for Canton's crypto operations
  val storage = new resource.MemoryStorage(
    loggerFactory,
    consoleEnvironment.environmentTimeouts
  )

  // Initialize the crypto system to sign the submissions
  val cryptoCreationFuture = Crypto.create(
    CryptoConfig(),
    storage,
    crypto.store.CryptoPrivateStoreFactory.withoutKms(
      consoleEnvironment.environment.clock,
      consoleEnvironment.environment.executionContext
    ),
    crypto.kms.CommunityKmsFactory,
    version.ReleaseProtocolVersion.latest,
    nonStandardConfig = false,
    com.digitalasset.canton.concurrent.FutureSupervisor.Noop,
    consoleEnvironment.environment.clock,
    consoleEnvironment.environment.executionContext,
    consoleEnvironment.environmentTimeouts,
    loggerFactory,
    com.digitalasset.canton.tracing.NoReportingTracerProvider
  )(
    consoleEnvironment.environment.executionContext,
    com.digitalasset.canton.tracing.TraceContext.empty
  )

  // Takes the cryptoCreationFuture Future and extracts the actual crypto runtime
  val cryptoRuntime = scala.concurrent.Await
    .result(
      com.digitalasset.canton.util.EitherTUtil
        .toFutureUnlessShutdown(cryptoCreationFuture.leftMap(e => new RuntimeException(e))),
      Duration.Inf
    )
    .toRight()
    .toOption
    .get

  // Get the signing key pair from the participant's secret store
  val keyPair = crypto.CryptoKeyPair
    .fromTrustedByteString(participant.keys.secret.download(keyFingerprint))
    .toOption
    .get

  // Loads the private key into the crypto runtime's temporary storage so it can be used for signing operations
  cryptoRuntime.cryptoPrivateStore
    .asInstanceOf[crypto.store.CryptoPrivateStoreExtended]
    .storePrivateKey(keyPair.privateKey, None)(
      com.digitalasset.canton.tracing.TraceContext.empty
    )

  // Sign the first prepared submission
  val signature1 = scala.concurrent.Await
    .result(
      com.digitalasset.canton.util.EitherTUtil.toFutureUnlessShutdown(
        cryptoRuntime.privateCrypto
          .signBytes(
            preparedSubmission.preparedTransactionHash,
            keyPair.publicKey.fingerprint,
            SigningKeyUsage.ProtocolOnly
          )(com.digitalasset.canton.tracing.TraceContext.empty)
          .leftMap(e => new RuntimeException(e.toString))
      ),
      Duration.Inf
    )
    .toRight()
    .toOption
    .get

  // Sign the second prepared submission
  val signature2 = scala.concurrent.Await
    .result(
      com.digitalasset.canton.util.EitherTUtil.toFutureUnlessShutdown(
        cryptoRuntime.privateCrypto
          .signBytes(
            preparedSubmission2.preparedTransactionHash,
            keyPair.publicKey.fingerprint,
            SigningKeyUsage.ProtocolOnly
          )(com.digitalasset.canton.tracing.TraceContext.empty)
          .leftMap(e => new RuntimeException(e.toString))
      ),
      Duration.Inf
    )
    .toRight()
    .toOption
    .get

  // Sign the third prepared submission
  val signature3 = scala.concurrent.Await
    .result(
      com.digitalasset.canton.util.EitherTUtil.toFutureUnlessShutdown(
        cryptoRuntime.privateCrypto
          .signBytes(
            preparedSubmission3.preparedTransactionHash,
            keyPair.publicKey.fingerprint,
            SigningKeyUsage.ProtocolOnly
          )(com.digitalasset.canton.tracing.TraceContext.empty)
          .leftMap(e => new RuntimeException(e.toString))
      ),
      Duration.Inf
    )
    .toRight()
    .toOption
    .get

  // Write the signatures to the output folder
  utils.write_to_file(
    Seq(signature1.toProtoV30, signature2.toProtoV30, signature3.toProtoV30),
    "output/submission-signatures.bin"
  )
}

main()
