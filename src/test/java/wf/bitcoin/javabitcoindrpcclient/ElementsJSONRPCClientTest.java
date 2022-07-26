package wf.bitcoin.javabitcoindrpcclient;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.*;

import org.junit.Test;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.BlockChainInfo;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.ScanObject;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.UnspentTxOutput;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.UtxoSet;
import wf.bitcoin.krotjson.JSON;

import java.util.logging.Logger;

/**
 * Created by andreabonel and byronhambly on 09-2022.
 */

public class ElementsJSONRPCClientTest {

    class MyClientTest extends ElementsJSONRPCClient {

        String expectedMethod;
        Object[] expectedObject;
        String result;

        MyClientTest(boolean testNet, String expectedMethod, Object[] expectedObject, String result) {
            super(testNet);
            this.expectedMethod = expectedMethod;
            this.expectedObject = expectedObject;
            this.result = result;
        }

        @Override
        public Object query(String method, Object... o) throws GenericRpcException {
            if (method != expectedMethod) {
                throw new GenericRpcException("wrong method");
            }
            if ((expectedObject != null) && !JSON.stringify(o).equals(JSON.stringify(expectedObject))) {
                throw new GenericRpcException("wrong object");
            }
            return JSON.parse(result);
        }
    }

    MyClientTest client;

    @Test
    public void signRawTransactionTest() throws Exception {
        Object[] obj = new Object[4];
        obj[0] = JSON.parse("\"0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000\"");
        obj[1] = JSON.parse("[]");
        obj[2] = JSON.parse("[\"cSjzx3VAM1r9iLXLvL6N61oS3zKns9Z9DcocrbkEzesPTDHWm5r4\"]");
        obj[3] = JSON.parse("\"ALL\"");
        client = new MyClientTest(false, "signrawtransaction", obj,
                "{\n" +
                        "  \"hex\": \"0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000\",\n" +
                        "  \"complete\": true\n" +
                        "}\n");
        LinkedList<BitcoindRpcClient.ExtendedTxInput> inputList = new LinkedList<BitcoindRpcClient.ExtendedTxInput>();
        LinkedList<String> privateKeys = new LinkedList<String>();
        privateKeys.add("cSjzx3VAM1r9iLXLvL6N61oS3zKns9Z9DcocrbkEzesPTDHWm5r4");
        String hex = client.signRawTransaction("0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000",
                inputList, privateKeys, "ALL");
        assertEquals("0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000",
                hex);
    }

    @Test
    public void signRawTransactionTestException() throws Exception {
        Object[] obj = new Object[4];
        obj[0] = JSON.parse("\"0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000\"");
        obj[1] = JSON.parse("[]");
        obj[2] = JSON.parse("[]");
        obj[3] = JSON.parse("\"ALL\"");
        client = new MyClientTest(false, "signrawtransaction", obj,
                "{\n" +
                        "  \"hex\": \"0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea00100000000ffffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000\",\n" +
                        "  \"complete\": false,\n" +
                        "  \"errors\": [\n" +
                        "    {\n" +
                        "      \"txid\": \"a09e41ad19ebfdb14c7ef78b39389369b459b5d2ec24ffffc110a9ac4f24b2b8\",\n" +
                        "      \"vout\": 1,\n" +
                        "      \"scriptSig\": \"\",\n" +
                        "      \"sequence\": 4294967295,\n" +
                        "      \"error\": \"Operation not valid with the current stack size\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");
        LinkedList<BitcoindRpcClient.ExtendedTxInput> inputList = new LinkedList<BitcoindRpcClient.ExtendedTxInput>();
        LinkedList<String> privateKeys = new LinkedList<String>();
        try {
            client.signRawTransaction("0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000",
                    inputList, privateKeys, "ALL");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("Incomplete"));
        }
    }

    @Test
    public void signRawTransactionTest2() throws Exception {
        Object[] obj = new Object[4];
        obj[0] = JSON.parse("\"0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000\"");
        obj[1] = JSON.parse("null");
        obj[2] = JSON.parse("null");
        obj[3] = JSON.parse("\"ALL\"");
        client = new MyClientTest(false, "signrawtransaction", obj,
                "{\n" +
                        "  \"hex\": \"0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000\",\n" +
                        "  \"complete\": true\n" +
                        "}\n");
        String hex = client.signRawTransaction("0100000001B8B2244FACA910C1FFFF24ECD2B559B4699338398BF77E4CB1FDEB19AD419EA0010000001976A9144CB4C3B90994FEF58FABB6D8368302E917C6EFB188ACFFFFFFFF012E2600000000000017A9140B2D7ED4E5076383BA8E98B9B3BCE426B7A2EA1E8700000000");
        assertEquals("0100000001b8b2244faca910c1ffff24ecd2b559b4699338398bf77e4cb1fdeb19ad419ea0010000006b483045022100b68b7fe9cfabb32949af6747b6769dffcf2aa4170e4df2f0e9d0a4571989e94e02204cf506c210cdb6b6b4413bf251a0b57ebcf1b1b2d303ba6183239b557ef0a310012102ab46e1d7b997d8094e97bc06a21a054c2ef485fac512e2dc91eb9831af55af4effffffff012e2600000000000017a9140b2d7ed4e5076383ba8e98b9b3bce426b7a2ea1e8700000000",
                hex);
    }

    @Test
    public void scanTxOutSetTest() {
        ScanObject scanObject1 = new ScanObject("addr(mtoffFXQWh6YNP86TRsRETNn9nDaMmsKsL)", null);
        ScanObject scanObject2 = new ScanObject("addr(mi11rWuB14Eb2L5tpdqfD77DGMhschQdgx)", null);
        List<ScanObject> list = Arrays.asList(scanObject1, scanObject2);

        List<Map<String, Object>> param = new ArrayList<>();
        for (ScanObject obj : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("desc", obj.getDescriptor());
            map.put("range", obj.getRange());
            param.add(map);
        }

        String json = "{\n" +
                "  \"success\": true,\n" +
                "  \"searched_items\": 22462153,\n" +
                "  \"unspents\": [\n" +
                "    {\n" +
                "      \"txid\": \"6415d590f46344a6f72c0e1544eb183a5ac3d8ff9a2ab48435f3255794af3915\",\n" +
                "      \"vout\": 0,\n" +
                "      \"scriptPubKey\": \"76a9141b3edeb7188b1cef9996e81ae22b68dfb3f7806688ac\",\n" +
                "      \"amount\": 0.00900000,\n" +
                "      \"height\": 1442023\n" +
                "    },\n" +
                "    {\n" +
                "      \"txid\": \"2d3bb59ba7bf690b43f604d7289e76534a9a32e92dd4f1945413a59832fe0723\",\n" +
                "      \"vout\": 0,\n" +
                "      \"scriptPubKey\": \"76a91491c2d21b865e338794bc92326de5dd0c15663d8788ac\",\n" +
                "      \"amount\": 0.00300000,\n" +
                "      \"height\": 1441179\n" +
                "    },\n" +
                "    {\n" +
                "      \"txid\": \"b6573ad024dd97172238712a8d417e39ff9fbeb15e35bbae447b86966503289b\",\n" +
                "      \"vout\": 1,\n" +
                "      \"scriptPubKey\": \"76a91491c2d21b865e338794bc92326de5dd0c15663d8788ac\",\n" +
                "      \"amount\": 0.00200000,\n" +
                "      \"height\": 1440923\n" +
                "    }\n" +
                "  ],\n" +
                "  \"total_amount\": 0.01400000\n" +
                "}\n" +
                "";

        client = new MyClientTest(false, "scantxoutset", new Object[]{"start", param}, json);
        UtxoSet utxoSet = client.scanTxOutSet(list);
        assertEquals(22462153, utxoSet.searchedItems().intValue());
        assertEquals(new BigDecimal("0.01400000"), utxoSet.totalAmount());
        assertEquals(3, utxoSet.unspents().size());
        UnspentTxOutput utxo = utxoSet.unspents().get(0);
        assertEquals("6415d590f46344a6f72c0e1544eb183a5ac3d8ff9a2ab48435f3255794af3915", utxo.txid());
        assertEquals(0, utxo.vout().intValue());
    }

    @Test
    public void getWalletInfoTest() throws Exception {
        Logger logger = Logger.getLogger("tests");
        client = new MyClientTest(false, "getwalletinfo", null,
                "{\"walletname\":\"\",\"walletversion\":169900,\"format\":\"bdb\",\"balance\":{\"bitcoin\":0.00000000},\"unconfirmed_balance\":{\"bitcoin\":0.00000000},\"immature_balance\":{\"bitcoin\":0.00000000},\"txcount\":880825,\"keypoololdest\":1649536956,\"keypoolsize\":1000,\"hdseedid\":\"5e4275b332b9dfe42702a1c713b09e0c0d19bd93\",\"keypoolsize_hd_internal\":1000,\"paytxfee\":0.00000000,\"private_keys_enabled\":true,\"avoid_reuse\":false,\"scanning\":false,\"descriptors\":false},\"error\":null,\"id\":\"1\"}\n");
        BitcoindRpcClient.WalletInfo wi = client.getWalletInfo();
        assertEquals(169900, wi.walletVersion());
        String zeroString = (new BigDecimal("0.00000000")).toString();
        logger.info("BigDecimal(0.00000000): " + zeroString);
        logger.info("wi.balance: " + wi.balance());
        assertEquals(zeroString, "" + wi.balance());
        assertEquals(zeroString, "" + wi.unconfirmedBalance());
        assertEquals(zeroString, "" + wi.immatureBalance());
        assertEquals(880825, wi.txCount());
        assertEquals(1649536956, wi.keyPoolOldest());
        assertEquals(1000, wi.keyPoolSize());
        assertEquals(zeroString, "" + wi.payTxFee());
        assertEquals("5e4275b332b9dfe42702a1c713b09e0c0d19bd93", wi.hdSeedId());
    }

    @Test
    public void getBestBlockHashTest() throws Exception {
        client = new MyClientTest(false, "getbestblockhash", null, "\"7d1ad2bf1c4ce92aa0e489e2f504852d432418ddf31b983bd2fcaa0b20b035f3\",\"error\":null,\"id\":\"1\"");
        String hex = client.getBestBlockHash();
        assertEquals("7d1ad2bf1c4ce92aa0e489e2f504852d432418ddf31b983bd2fcaa0b20b035f3", hex);
    }

    @Test
    public void getRawMemPoolTest() throws Exception {
        client = new MyClientTest(false, "getrawmempool", null,
                "[\"03c1d516d0783d96ab438ab20896a8c17c15dfe4781c797346abb17824213376\",\"18687296d7a8d0e856ae75f4f0bdafd6ed4a5786cfc7309b3e08e548a11f410f\",\"f58c7beb4ae8715e28155acc67d797d72017e203b9f537c5d51df45b1e829e77\"],\"error\":null,\"id\":\"1\"");
        List<String> result = client.getRawMemPool();
        assertEquals("03c1d516d0783d96ab438ab20896a8c17c15dfe4781c797346abb17824213376", result.get(0));
        assertEquals("18687296d7a8d0e856ae75f4f0bdafd6ed4a5786cfc7309b3e08e548a11f410f", result.get(1));
        assertEquals("f58c7beb4ae8715e28155acc67d797d72017e203b9f537c5d51df45b1e829e77", result.get(2));
        assertEquals(3, result.size());
    }

    @Test
    public void getBalanceTest() throws Exception {
        client = new MyClientTest(false, "getbalance", null, "{\"bitcoin\":0.00000000},\"error\":null,\"id\":\"1\"");
        String zeroString = (new BigDecimal("0.00000000")).toString();
        BigDecimal result = client.getBalance();
        assertEquals(zeroString, "" + result);
    }

    @Test
    public void getBalanceTest_MinConf() throws Exception {
        client = new MyClientTest(false, "getbalance", new Object[]{"*", 6}, "{\"bitcoin\":0.00000000},\"error\":null,\"id\":\"1\"");
        String zeroString = (new BigDecimal("0.00000000")).toString();
        BigDecimal result = client.getBalance("*", 6);
        assertEquals(zeroString, "" + result);
    }

    @Test
    public void getFullBalance() throws Exception {
        //client = new MyClientTest(false, "getbalance", null,"{\"bitcoin\":0.00000000},\"error\":null,\"id\":\"1\"");
        client = new MyClientTest(false, "getbalance", null, "{\"bitcoin\":0.00000000}");
        String zeroString = (new BigDecimal("0.00000000")).toString();
        Map<String, ?> result = client.getFullBalance();
        assertEquals(zeroString, "" + result.get("bitcoin"));
    }

    @Test
    public void getBlockChainInfoTest() throws Exception {
        client = new MyClientTest(false, "getblockchaininfo", null, "{\"chain\":\"elementsregtest\",\"blocks\":819,\"headers\":819,\"bestblockhash\":\"f60d86d06158ea0db20de1d148002fce20f954b71f4e8d2ccfbe48313b035819\",\"mediantime\":1661529322,\"verificationprogress\":0.987,\"initialblockdownload\":false,\"size_on_disk\":724372,\"pruned\":false,\"current_params_root\":\"3700bdb2975ff8e0dadaaba2b33857b0ca2610c950a92b1db725025e3647a8e1\",\"current_signblock_asm\":\"0 4ae81572f06e1b88fd5ced7a1a000945432e83e1551e6f721ee9c00b8cc33260\",\"current_signblock_hex\":\"00204ae81572f06e1b88fd5ced7a1a000945432e83e1551e6f721ee9c00b8cc33260\",\"max_block_witness\":74,\"current_fedpeg_program\":\"a91472c44f957fc011d97e3406667dca5b1c930c402687\",\"current_fedpeg_script\":\"51\",\"extension_space\":[\"02fcba7ecf41bc7e1be4ee122d9d22e3333671eb0a3a87b5cdf099d59874e1940f02fcba7ecf41bc7e1be4ee122d9d22e3333671eb0a3a87b5cdf099d59874e1940f\"],\"epoch_length\":10,\"total_valid_epochs\":2,\"epoch_age\":9,\"softforks\":{\"bip34\":{\"type\":\"buried\",\"active\":true,\"height\":0},\"bip66\":{\"type\":\"buried\",\"active\":true,\"height\":0},\"bip65\":{\"type\":\"buried\",\"active\":true,\"height\":0},\"csv\":{\"type\":\"buried\",\"active\":true,\"height\":432},\"segwit\":{\"type\":\"buried\",\"active\":true,\"height\":0},\"dynafed\":{\"type\":\"bip9\",\"bip9\":{\"status\":\"active\",\"start_time\":-1,\"timeout\":9223372036854775807,\"since\":0,\"min_activation_height\":0},\"height\":0,\"active\":true},\"testdummy\":{\"type\":\"bip9\",\"bip9\":{\"status\":\"active\",\"start_time\":0,\"timeout\":9223372036854775807,\"since\":432,\"min_activation_height\":0},\"height\":432,\"active\":true},\"taproot\":{\"type\":\"bip9\",\"bip9\":{\"status\":\"active\",\"start_time\":-1,\"timeout\":9223372036854775807,\"since\":0,\"min_activation_height\":0},\"height\":0,\"active\":true}},\"warnings\":\"\"}");
        BlockChainInfo result = client.getBlockChainInfo();
        String zeroString = (new BigDecimal("0.00000000")).toString();
        assertEquals("elementsregtest", "" + result.chain());
        assertEquals("819", "" + result.blocks());
        assertEquals("819", "" + result.headers());
        assertEquals("f60d86d06158ea0db20de1d148002fce20f954b71f4e8d2ccfbe48313b035819", "" + result.bestBlockHash());
        assertEquals("1661529322", "" + result.medianTime());
        assertEquals("0.987", "" + result.verificationProgress());
        assertEquals(false, result.initialBlockDownload());
        assertEquals("724372", "" + result.sizeOnDisk());
        assertEquals(false, result.pruned());
        assertEquals("", "" + result.warnings());
    }

    @Test
    public void getBlockCountTest() throws Exception {
        //client = new MyClientTest(false, "getblockcount", null,"490818,\"error\":null,\"id\":\"1\"");
        client = new MyClientTest(false, "getblockcount", null, "490818");
        int result = client.getBlockCount();
        assertEquals("490818", "" + result);
    }

    @Test
    public void getSideChainInfoTest() throws Exception {
        client = new MyClientTest(false, "getsidechaininfo", null, "{\"fedpegscript\":\"51\",\"current_fedpeg_programs\":[\"a91472c44f957fc011d97e3406667dca5b1c930c402687\",\"a91472c44f957fc011d97e3406667dca5b1c930c402687\"],\"current_fedpegscripts\":[\"51\",\"51\"],\"pegged_asset\":\"b2e15d0d7a0c94e4e2ce0fe6e8691b9e451377f6e46e8045a86f7c4b5d4f0f23\",\"min_peg_diff\":\"7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\",\"parent_blockhash\":\"0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206\",\"parent_chain_has_pow\":true,\"enforce_pak\":false,\"pegin_confirmation_depth\":8}");
        MapWrapperType result = client.getSideChainInfo();
        assertEquals("51", "" + result.mapStr("fedpegscript"));
        assertEquals("[51, 51]", "" + (result.mapStr("current_fedpegscripts")));
        assertEquals("b2e15d0d7a0c94e4e2ce0fe6e8691b9e451377f6e46e8045a86f7c4b5d4f0f23", "" + result.mapStr("pegged_asset"));
        assertEquals("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "" + result.mapStr("min_peg_diff"));
        assertEquals("0f9188f13cb7b2c71f2a335e3a4fc328bf5beb436012afca590b1a11466e2206", "" + result.mapStr("parent_blockhash"));
        assertEquals(true, result.mapBool("parent_chain_has_pow"));
        assertEquals(false, result.mapBool("enforce_pak"));
        assertEquals("8", "" + result.mapInt("pegin_confirmation_depth"));
    }

    @Test
    public void dumpAssetLabelsTest() throws Exception {
        client = new MyClientTest(false, "dumpassetlabels", null, "{\"bitcoin\":\"b2e15d0d7a0c94e4e2ce0fe6e8691b9e451377f6e46e8045a86f7c4b5d4f0f23\"}");
        MapWrapperType result = client.dumpAssetLabels();
        assertEquals("b2e15d0d7a0c94e4e2ce0fe6e8691b9e451377f6e46e8045a86f7c4b5d4f0f23", "" + result.mapStr("bitcoin"));
    }

    @Test
    public void getPakInfoTest() throws Exception {
        client = new MyClientTest(false, "getpakinfo", null, "{\"block_paklist\":{\"online\":[\"02fcba7ecf41bc7e1be4ee122d9d22e3333671eb0a3a87b5cdf099d59874e1940f\"],\"offline\":[\"02fcba7ecf41bc7e1be4ee122d9d22e3333671eb0a3a87b5cdf099d59874e1940f\"],\"reject\":false}}");
        MapWrapperType result = client.getPakInfo();

        assertEquals("[02fcba7ecf41bc7e1be4ee122d9d22e3333671eb0a3a87b5cdf099d59874e1940f]", "" + result.mapStr("online"));
        assertEquals("[02fcba7ecf41bc7e1be4ee122d9d22e3333671eb0a3a87b5cdf099d59874e1940f]", "" + result.mapStr("offline"));
        assertEquals(false, result.mapBool("reject"));
    }

    @Test
    public void issueAssetTest() throws Exception {
        client = new MyClientTest(false, "issueasset", new Object[]{10, 0, true, "0000000000000000000000000000000000000000000000000000000000000000"}, "{\"txid\":\"d7ee0ac1d971c453b2a1cd06f320abc0123340b536ec3aff42863fc728a36f01\",\"vin\":0,\"entropy\":\"55f3976eab17fdc3ba3de1a156c7b761dd458becc7e0d821019291e5fc97bd44\",\"asset\":\"6ccc9b06b73b75219c03b1eea1fb910fccae6616048f483335277bca9cc1f96c\",\"token\":\"6fc6b2aeba763f99751dbd84588653a8d8a646e5c1a84ef14cdd7aee3ce2f7de\"}");
        MapWrapperType result = client.issueAsset(10, 0, true, "0000000000000000000000000000000000000000000000000000000000000000");
        assertEquals("d7ee0ac1d971c453b2a1cd06f320abc0123340b536ec3aff42863fc728a36f01", "" + result.mapStr("txid"));      //Transaction id for issuance
        assertEquals((Integer) 0, result.mapInt("vin"));                                                                                 //The input position of the issuance in the transaction
        assertEquals("55f3976eab17fdc3ba3de1a156c7b761dd458becc7e0d821019291e5fc97bd44", "" + result.mapStr("entropy"));   //Entropy of the asset type
        assertEquals("6ccc9b06b73b75219c03b1eea1fb910fccae6616048f483335277bca9cc1f96c", "" + result.mapStr("asset"));     //Asset type for issuance
        assertEquals("6fc6b2aeba763f99751dbd84588653a8d8a646e5c1a84ef14cdd7aee3ce2f7de", "" + result.mapStr("token")); //Token type for issuance
    }

    @Test
    public void listIssuancesTest() throws Exception {
        client = new MyClientTest(false, "listissuances", null, "[{\"isreissuance\":false,\"token\":\"6fc6b2aeba763f99751dbd84588653a8d8a646e5c1a84ef14cdd7aee3ce2f7de\",\"tokenamount\":-1,\"tokenblinds\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"entropy\":\"55f3976eab17fdc3ba3de1a156c7b761dd458becc7e0d821019291e5fc97bd44\",\"txid\":\"d7ee0ac1d971c453b2a1cd06f320abc0123340b536ec3aff42863fc728a36f01\",\"vin\":0,\"asset\":\"6ccc9b06b73b75219c03b1eea1fb910fccae6616048f483335277bca9cc1f96c\",\"assetamount\":10.00000000,\"assetblinds\":\"bf12158cec1e86ae9e2f2989994452c8ba0881c3b0ef695a5fba0eb4098149ba\"}]");
        List<ElementsJSONRPCClient.Issuance> result = client.listIssuances();
        assertEquals(false, (result.get(0)).mapBool("isreissuance"));
        assertEquals("6fc6b2aeba763f99751dbd84588653a8d8a646e5c1a84ef14cdd7aee3ce2f7de", (result.get(0)).mapStr("token"));
        assertEquals("-1", (result.get(0)).mapStr("tokenamount"));
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", (result.get(0)).mapStr("tokenblinds"));
        assertEquals("55f3976eab17fdc3ba3de1a156c7b761dd458becc7e0d821019291e5fc97bd44", (result.get(0)).mapStr("entropy"));
        assertEquals("d7ee0ac1d971c453b2a1cd06f320abc0123340b536ec3aff42863fc728a36f01", (result.get(0)).mapStr("txid"));
        assertEquals("0", (result.get(0)).mapStr("vin"));
        assertEquals("6ccc9b06b73b75219c03b1eea1fb910fccae6616048f483335277bca9cc1f96c", (result.get(0)).mapStr("asset"));
        assertEquals("10.00000000", (result.get(0)).mapStr("assetamount"));
        assertEquals("bf12158cec1e86ae9e2f2989994452c8ba0881c3b0ef695a5fba0eb4098149ba", (result.get(0)).mapStr("assetblinds"));
    }

    @Test
    public void getNewAddressTest() throws Exception {
        client = new MyClientTest(false, "getnewaddress", null, "\"el1qq200qy853dw9uqf7d385zm0hrvhzj4l2cdpxla60hcadg5v2qzn3f0r9f35lg4c8s4qh2c2xu8e2dgd4932yscp3arppslqch\"");
        String result = client.getNewAddress();
        assertEquals("el1qq200qy853dw9uqf7d385zm0hrvhzj4l2cdpxla60hcadg5v2qzn3f0r9f35lg4c8s4qh2c2xu8e2dgd4932yscp3arppslqch", result);
    }

    @Test
    public void dumpBlindingKeyTest() throws Exception {
        client = new MyClientTest(false, "dumpblindingkey", new Object[]{"el1qq200qy853dw9uqf7d385zm0hrvhzj4l2cdpxla60hcadg5v2qzn3f0r9f35lg4c8s4qh2c2xu8e2dgd4932yscp3arppslqch"}, "\"1d5b80eefff5b4f6f9a911fa70c13ba25f4411dfa8d60038062a2de96d066743\"");
        String result = client.dumpBlindingKey("el1qq200qy853dw9uqf7d385zm0hrvhzj4l2cdpxla60hcadg5v2qzn3f0r9f35lg4c8s4qh2c2xu8e2dgd4932yscp3arppslqch");
        assertEquals("1d5b80eefff5b4f6f9a911fa70c13ba25f4411dfa8d60038062a2de96d066743", result);
    }

    @Test
    public void createBlindedAddressTest() throws Exception {
        client = new MyClientTest(false, "createblindedaddress", new Object[]{"ert1qmudgxlfljnayunjdxh70yp6nyp92phhpzg2ayv", "03c4483f6c67ba1cf86d51ffc6e1a118e0470554a09a662bdbf9fca9e9fc012f8a"}, "\"el1qq0zys0mvv7ape7rd28ludcdprrsywp255zdxv27ml872n60uqyhc4hc6sd7nl986fe8y6d0u7gr4xgz25r0wzjl6zxupnkqvu\"");
        String result = client.createBlindedAddress("ert1qmudgxlfljnayunjdxh70yp6nyp92phhpzg2ayv", "03c4483f6c67ba1cf86d51ffc6e1a118e0470554a09a662bdbf9fca9e9fc012f8a");
        assertEquals("el1qq0zys0mvv7ape7rd28ludcdprrsywp255zdxv27ml872n60uqyhc4hc6sd7nl986fe8y6d0u7gr4xgz25r0wzjl6zxupnkqvu", result);
    }
}