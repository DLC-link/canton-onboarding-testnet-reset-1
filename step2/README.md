# Canton Onboarding - Step 2

The purpose of this step is to sign the Decentralized Namespace proposal.
## Prerequisite

Please check whether you have everything set up properly [here](../misc/README.md).

## Run the Scala scripts

1. Move into the step2 folder if you are not there yet

```bash
cd step2
```

2. Validate that the dns proposal file is in the input folder

```bash
file input/dns_proto.bin
# Output: input/dns_proto.bin: data
```

3. Run the Scala script to sign the file

```bash
canton run 02_SignProposals.sc -c ../misc/connect.conf
```

4. Output

There is an output directory with one file (`signed-dns-proposal.bin`), provide that to the Bitsafe team.