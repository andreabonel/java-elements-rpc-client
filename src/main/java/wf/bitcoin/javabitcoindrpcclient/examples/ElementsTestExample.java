package wf.bitcoin.javabitcoindrpcclient.examples;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import wf.bitcoin.javabitcoindrpcclient.*;
import wf.bitcoin.javabitcoindrpcclient.util.Chain;
import wf.bitcoin.javabitcoindrpcclient.util.Util;

public class ElementsTestExample {
    static final Logger LOGGER = Logger.getLogger(ElementsTestExample.class.getName());
    static boolean authUsingURL = true; //true for auth using URL, false for auth using cookie file path

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        ElementsJSONRPCClient client;

        String user = "user";
        String password = "pass";
        String host = "localhost";
        String port = "18884";
        if (authUsingURL) {
            client = new ElementsJSONRPCClient("http://" + user + ':' + password + "@" + host + ":" + port + "/");
            client.setUser(user);
            client.setPassword(password);
            client.setHost(host);
            client.setPort(port);
        } else {
            //cookie
            File home = new File(System.getProperty("user.home"));
            Path cookieFilePath = Paths.get(home + "/.elements/elementsregtest/.cookie");
            client = new ElementsJSONRPCClient(cookieFilePath, port);
        }
        Util.ensureRunningOnChain(Chain.ELEMENTSREGTEST, client);

        elementsExample(client);
    }

    //static void elementsExample(BitcoindRpcClient client)
    static void elementsExample(ElementsJSONRPCClient client) {
        try {
            Map<String, ?> createdWallet = client.createWallet("my_wallet");
            LOGGER.info("New wallet ");
            for (Map.Entry<String, ?> entry : createdWallet.entrySet()) {
                LOGGER.info(entry.getKey() + ": " + entry.getValue());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, null, ex);
            LOGGER.log(Level.WARNING, ex.toString());
        }

        LOGGER.info("=== GetWalletInfo");
        BitcoindRpcClient.WalletInfo wi = client.getWalletInfo(); //Bitcoin
        LOGGER.info("WalletInfo: " + wi);
        LOGGER.info("walletVersion: " + Long.toString(wi.walletVersion()));
        LOGGER.info("wallet - balance: " + wi.balance());
        LOGGER.info("wallet - unconfirmed_balance: " + wi.unconfirmedBalance());
        LOGGER.info("wallet- immature_balance: " + wi.immatureBalance());

        LOGGER.info("BestBlockHash: " + client.getBestBlockHash());
        LOGGER.info("RawMemPool: " + client.getRawMemPool());
        LOGGER.info("Balance: " + client.getBalance());               //Bitcoin (implemented for Elements to return just bitcoin key)
        LOGGER.info("Full Balance: " + client.getFullBalance());                                //implemented for Elements to return the whole result)
        LOGGER.info("Balance (account): " + client.getBalance("*"));                    //BTC (implemented for Elements to return just bitcoin key)
        LOGGER.info("Balance (account, minConf): " + client.getBalance("*", 6)); //BTC (implemented for Elements to return just bitcoin key)
        LOGGER.info("BlockChainInfo: " + client.getBlockChainInfo()); //Bitcoin
        LOGGER.info("BlockCount: " + client.getBlockCount());         //Bitcoin
        LOGGER.info("SideChainInfo: " + client.getSideChainInfo());   //Elements
        LOGGER.info("AssetLabels: " + client.dumpAssetLabels());      //Elements
        MapWrapperType m = client.getPakInfo();                            //Elements
        LOGGER.info("PakInfo: " + m);
        LOGGER.info("PakInfo: " + m.mapBool("reject"));
        LOGGER.info("Issued asset 1: " + client.issueAsset(10, 0));        //Elements
        LOGGER.info("Issued asset 2: " + client.issueAsset(10, 0, true, "0000000000000000000000000000000000000000000000000000000000000000"));   //Elements
        LOGGER.info("List Issuances: " + client.listIssuances());     //Elements

        //RAW ISSUE ASSET
        //Get an address to issue the asset to...
        String ASSET_ADDR = client.getNewAddress("", "legacy");
        //Get an address to issue the reissuance token to...
        String TOKEN_ADDR = client.getNewAddress("", "legacy");
        //Create the raw transaction and fund it
        BitcoinRawTxBuilder rawTxBuilder = new BitcoinRawTxBuilder(client);
        rawTxBuilder.out(null, null, "\0".getBytes());      //Bitcoin  //client.createRawTransaction('''[]''','''[{"''data''":"''00''"}]''')
        String RAWTX = rawTxBuilder.create();
        LOGGER.info("Created Raw Tx: " + RAWTX);
        Map<String, ?> FRT = client.fundRawTransaction(RAWTX);             //Bitcoin
        LOGGER.info("Funded Raw Tx: " + FRT);
        String HEXFRT = (String) FRT.get("hex"); //getOrDefault(FRT, "hex", "").toString()
        LOGGER.info("HEX Funded Raw Tx: " + HEXFRT);
        //Create the raw issuance
        List<Map<String, ?>> issuances = new ArrayList<>();
        issuances.add(new LinkedHashMap<String, Object>() {
            {
                put("asset_amount", 33);
                put("asset_address", ASSET_ADDR);
                put("token_amount", 7);
                put("token_address", TOKEN_ADDR);
                put("blind", false);
            }
        });
        //List<Map<String, ?>> RIA=client.rawIssueAsset(HEXFRT,issuances);
        List<ElementsJSONRPCClient.Issuance> RIA = client.rawIssueAsset(HEXFRT, issuances); //Elements
        LOGGER.info("Issued Asset (rawissueasset): " + RIA);
        String HEXRIA = (String) RIA.get(0).hex();
        LOGGER.info("Issued Asset (rawissueasset) HEX: " + HEXRIA);

        //Blind, sign and send the transaction that creates the asset issuance...
        List<Map<String, ?>> assetCommitments = new ArrayList<>();
        String BRT = client.blindRawTransaction(HEXRIA, true, assetCommitments, false); //Elements
        LOGGER.info("Blinded Raw Transaction: " + BRT);
        Map<String, ?> SRT = client.signRawTransactionWithWallet(BRT);  //Bitcoin
        LOGGER.info("Signed Raw Transaction: " + SRT);
        String HEXSRT = (String) SRT.get("hex");
        LOGGER.info("HEX Signed Raw Tx: " + HEXSRT);
        String ISSUETX = client.sendRawTransaction(HEXSRT);
        LOGGER.info("Issue Transaction: " + ISSUETX);
        String ADDRGEN1 = client.getNewAddress(); //Create an address to generate to
        client.generateToAddress(101, ADDRGEN1);
        //Check that worked...
        //BitcoindRpcClient.WalletInfo wi = client.getWalletInfo();
        LOGGER.info("WalletInfo: " + client.getWalletInfo());
        LOGGER.info("List Issuances: " + client.listIssuances());

        String ADDRGEN2 = client.getNewAddress();
        LOGGER.info("ADDRGEN2 to be used in dumpBlindingKey: " + ADDRGEN2);
        String BK = client.dumpBlindingKey(ADDRGEN2); //Elements (Dumps the private blinding key)
        LOGGER.info("Blinding Key: " + BK);

        ElementsJSONRPCClient.AddressInfoElements ADDRINFO = client.getAddressInfo(ADDRGEN2);
        String UNCADDR = ADDRINFO.unconfidentialAddress();
        String CK = ADDRINFO.confidentialKey();
        String BLADDR = client.createBlindedAddress(UNCADDR, CK); //Elements
        LOGGER.info("Blinded Address: " + BLADDR);

        client.importBlindingKey(ADDRGEN2, BK);

        if (authUsingURL) {
            //auth test:
            LOGGER.info("getUser(): " + client.getUser());
            LOGGER.info("getPassword(): " + client.getPassword());
            client.setUser("wrong_user");
            LOGGER.info("Setting wrong user to test url regeneration: " + client.getUser());
            try {
                LOGGER.info("BestBlockHash: " + client.getBestBlockHash());
                LOGGER.info("Unexpected success. This is an error.");
            } catch (RuntimeException e) {
                LOGGER.info("Expected error from testing url regeneration. Successful test");
            }
            client.setUser("user");
            LOGGER.info("setUser: " + client.getUser());
            LOGGER.info("BestBlockHash: " + client.getBestBlockHash());
        }
    }
}
