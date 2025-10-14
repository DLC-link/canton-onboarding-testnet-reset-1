# Canton Onboarding - Step 0

The purpose of this step is to create a party and a user on Canton.

## Prerequisite

Please check whether you have everything set up properly [here](../misc/README.md).

## Run the Scala scripts

1. Move into the step0 folder if you are not there yet

```bash
cd step0
```

2. Set environment variables

The recommended naming should be `<bitsafe | CBTC>-attestor-<your-company-name>-<index>`

```bash
export PARTY_NAME={YOUR_CHOSEN_ATTESTOR_PARTY_NAME}
export USER_NAME={YOUR_CHOSEN_ATTESTOR_USERNAME_NAME}
```

3. Run the Scala script

```bash
canton run CreatePartiesAndUser.sc -c ../misc/connect.conf
```

4. Validate it

```bash
canton run Validate.sc -c ../misc/connect.conf
```

## Troubleshooting

### Unautenticated error

If you get the following error, it means, that the token in the `misc/connect.conf` is not valid or expired.

```
GrpcClientError: UNAUTHENTICATED/An error occurred. Please contact the operator and inquire about the request...
```

### Canton user already exists

If you get the following error, it means, the canton user already exists. You can validate it with the Validate.sc

```bash
GrpcRequestRefusedByServer: ALREADY_EXISTS/USER_ALREADY_EXISTS(10,ec21963e): creating user failed, as user "..." already exists
```