java-elements-rpc-client
==================

This is a lightweight java elementsd JSON-RPC client binding. It is an extension of the [bitcoin-rpc-client](https://github.com/Polve/bitcoin-rpc-client). It does not require any external dependencies.

Configuration
=====

Here is a sample of the configuration file (`<user home>/.elements/elements.conf`) that will setup elementsd to run in regtest mode and in a way compatible with this library:

```
# Run elementsd in regtest mode
chain=elementsregtest

# Maintain full transaction index, used in lookups by the getrawtransaction call
txindex=1

# Accept command line and JSON-RPC commands
server=1

# Tells elementsd that the RPC API settings on the following lines apply to the regtest RPC API
[elementsregtest]

# RPC API settings
rpcport=9997
```

Notes:
* The configuration does not contain any API credentials. The authentication is done by providing an URL in the constructor. It can also be done via a temporary token stored in a cookie file by elementsd (see [details](https://bitcoin.org/en/release/v0.12.0#rpc-random-cookie-rpc-authentication)). The approach of using rpcuser and rpcpassword is still supported, even though elementsd considers it legacy.
* In the contrib folder there are some template scripts to run tests from the command line:
    - `runtest`, to run some unit tests.
    - `runtestexample.sh`, to run some rpc calls (some of which would need the existance of some coins in the loaded wallet).