# Canton Onboarding - Step 4

The purpose of this step is to ...
## Prerequisite

Please check whether you have everything set up properly [here](../misc/README.md).

## Run the Scala scripts

1. Move into the step4 folder if you are not there yet

```bash
cd step4
```

2. Validate that the prepared submission files are in the input folder

```bash
ls -la input
# Output similar to:
# x .keep
# x prepared-submission-1.bin
# x prepared-submission-2.bin
# x prepared-submission-3.bin
```

3. Run the Scala script to sign the file

```bash
canton run 04_SignSubmissions.sc -c ../misc/connect.conf
```

4. Output

There is an output directory with one file (`submission-signatures.bin`), provide that to the Bitsafe team.