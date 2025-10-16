# Canton Onboarding - Final Canton step

This is the final Canton setup for the attestor integration.

## Prerequisite

Please check whether you have everything set up properly [here](../misc/README.md).

## Grant rights

This step grants rights to your user to act as the decentralized party.

### Setup access token

You need to create a token as described in the [Create token section](../misc/README.md#create-token).

### Set username

You should set the USER_NAME to the same value that you added in step 0.

```bash
export USER_NAME=bitsafe-attestor...
```
### Run script

```bash
canton run GrantRights.sc -c ../misc/connect.conf
```

### Validate rights

```bash
canton run ValidateRights.sc -c ../misc/connect.conf
```

You should see `cbtc-network` in the resultset.

## Oauth provider

You need to set up a user on your OAuth provider so the attestor can use it to do actions on behalf of the Canton user.

The Ledger API accepts a JWT token for each request. This JWT token should contain certain fields so the Canton Network can identify the corresponding user. These fields should be:

- `aud`: Sets the OIDC Audience field
- `sub`: Contains the username that the token belongs to
- `scope`: Sets the scope where the token operates on, it must contain: `daml-ledger-api`

These fields are encoded into the JWT token if the OAuth provider is set up properly. This will be important for the attestor configuration.

### Keycloak

See [keycloak.md](keycloak.md) for detailed setup instructions.

### Auth0

See [auth0.md](auth0.md) for detailed setup instructions.

### Validate token

If you set the `CLIENT_ID` and `CLIENT_SECRET` to the values from the OAuth provider

```bash
export CLIENT_SECRET=XXX
export CLIENT_ID=client_id
sh ../misc/scripts/update-token.sh
```

and run the following, it should output the same as above:

```
canton run ValidateRights.sc -c ../misc/connect.conf
```

#### Unauthenticated

If you get the following error: `GrpcClientError: UNAUTHENTICATED/An error occurred`, you should run the following script and provide the details to the Bitsafe team, so they can assist further.

```bash
sh scripts/jwt_check {JWT_TOKEN}
```

This will output the field values, for example:

```bash
JWT Token Fields:
==================
Audience (aud): [
  "https://wallet-xxx.console.dev.canton.ibtc.network/api",
  "account"
]
Subject (sub): bitsafe-attestor-user-1
Scope: daml_ledger_api ibtc-devnet_audience profile email
```

We can assist further if you can send this output to us.