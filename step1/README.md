# Canton Onboarding - Step 1

The purpose of this step is to upload the DARS to the participant node and generate keys for the decentralized party.

## Prerequisite

Please check whether you have everything set up properly [here](../misc/README.md).

## Run the Scala scripts

1. Move into the step1 folder if you are not there yet

```bash
cd step1
```

2. Run the Scala script to upload dars

```bash
canton run 00_UploadDars.sc -c ../misc/connect.conf
```

4. Validate the success of upload

```bash
canton run 00_ValidateDars.sc -c ../misc/connect.conf
```

5. Generate keys for the decentralized party

```bash
canton run 01_GenerateKeys.sc -c ../misc/connect.conf
```

6. Validate key generation

```bash
canton run 01_ValidateKeys.sc -c ../misc/connect.conf
```

- If you see `No cbtc-network-XXX keys found` it means that the keys are not set up properly.
- If you see `ATTENTION: Multiple keys detected! ...`, please contact the Bitsafe team.

7. Output

There is an output directory with two files (`attestor-public-keys.bin` and `participant-id.bin`), provide that to the Bitsafe team.