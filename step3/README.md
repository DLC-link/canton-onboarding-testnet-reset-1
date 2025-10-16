# Canton Onboarding - Step 3

The purpose of this step is to sign Party to Participant mapping and Party to Key mapping on the topology.

## Prerequisite

Please check whether you have everything set up properly [here](../misc/README.md).

## Run the Scala scripts

1. Move into the step3 folder if you are not there yet

```bash
cd step3
```

2. Validate that the p2p and ptk proposal files are in the input folder

```bash
file input/p2p_proto.bin
# Output: input/p2p_proto.bin: data

file input/ptk_proto.bin
# Output: input/ptk_proto.bin: data
```

3. Run the Scala script to sign the file

```bash
canton run 03_SignP2PPTKProposals.sc -c ../misc/connect.conf
```

4. Output

There is an output directory with one file (`signed-p2p-ptk-proposals.bin`), provide that to the Bitsafe team.