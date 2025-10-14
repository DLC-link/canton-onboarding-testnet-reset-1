def main(): Unit = {
  val governanceDarPath = "./dars/cbtc-governance-0.0.1.dar"
  val CBTCDarPath = "./dars/cbtc-0.0.1.dar"

  participant.dars.upload(CBTCDarPath)
  participant.dars.upload(governanceDarPath)
}

main()
