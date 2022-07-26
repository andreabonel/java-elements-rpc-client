/*
 * ElementsRpcClient-JSON-RPC-Client License
 *
 * Copyright (c) 2022, Mikhail Yevchenko, Andrea Bonel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/*
 * Repackaged with simple additions for easier maven usage by Alessandro Polverini
 */
package wf.bitcoin.javabitcoindrpcclient;


import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;


/**
 * @author andreabonel and byronhambly
 */
public class ElementsJSONRPCClient extends BitcoinJSONRPCClient {

    /*
     * Missing methods supported in 0.21.0.0 are ...
     *
     * combineblocksigs
     * consumecompactsketch
     * consumegetblocktxn
     * finalizecompactblock
     * getcompactsketch
     * getnewblockhex
     * testproposedblock
     * calculateasset
     * rawblindrawtransaction
     * rawreissueasset
     * updatepsbtpegin
     * tweakfedpegscript
     * claimpegin
     * createrawpegin
     * destroyamount
     * dumpissuanceblindingkey
     * dumpmasterblindingkey
     * getpeginaddress
     * getwalletpakinfo
     * importissuanceblindingkey
     * importmasterblindingkey
     * initpegoutwallet
     * reissueasset
     * sendtomainchain
     * signblock
     * unblindrawtransaction
     */

    //private static final Logger logger = Logger.getLogger(BitcoindRpcClient.class.getPackage().getName());

    public static final Charset QUERY_CHARSET = Charset.forName("ISO8859-1");
    public static final int CONNECT_TIMEOUT = (int) TimeUnit.MINUTES.toMillis(1);

    String user = "user";
    String password = "pass";
    String host = "localhost";
    String port = "7040";

    public int readTimeout = (int) TimeUnit.MINUTES.toMillis(5);

    public ElementsJSONRPCClient(String user, String password, String host, String port) throws MalformedURLException {
        this(new URL("http://" + user + ':' + password + "@" + host + ":" + (port == null ? "8332" : port) + "/"));
    }

    public ElementsJSONRPCClient(String rpcUrl) throws MalformedURLException {
        this(new URL(rpcUrl));
    }

    public ElementsJSONRPCClient(URL rpc) {
        //this.rpcURL = rpc;
        super(rpc);
    }

    public ElementsJSONRPCClient(Path cookieFilePath, String cookiePort) throws IOException {
        this(new URL(getUrlFromCookie(cookieFilePath, cookiePort)));
    }

    private static String getUrlFromCookie(Path cookieFilePath, String cookiePort) {
        Path cookieFile = cookieFilePath;

        String user = "";
        String password = "";
        String host = "localhost";

        try {
            if (Files.exists(cookieFilePath)) {
                //Path cookieFilePath = cookieFile.get();

                // Format is __cookie__:tempPassword
                String cookieFileContents = new String(Files.readAllBytes(cookieFilePath));

                String[] temp = cookieFileContents.split(":");
                user = temp[0];
                password = temp[1];
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        String rpcUrl = "http://" + user + ':' + password + "@" + host + ":" + cookiePort + "/";
        return rpcUrl;
    }

    public ElementsJSONRPCClient(boolean testNet) {
        this(testNet ? DEFAULT_JSONRPC_TESTNET_URL : DEFAULT_JSONRPC_URL);
    }

    public ElementsJSONRPCClient() {
        this(DEFAULT_JSONRPC_TESTNET_URL);
    }

    public void setUser(String newUser) {
        user = newUser;
        updateConnectionInfo();
    }

    public String getUser() {
        return user;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
        updateConnectionInfo();
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String newHost) {
        host = newHost;
        updateConnectionInfo();
    }

    public String getHost() {
        return host;
    }

    public void setPort(String newPort) {
        port = newPort;
        updateConnectionInfo();
    }

    public String getPort() {
        return port;
    }

    private void updateConnectionInfo() {
        try {
            updateConnectionInfo(new URL("http://" + user + ':' + password + "@" + host + ":" + (port == null ? "8332" : port) + "/"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal getBalance() throws GenericRpcException {
        Map<String, ?> balance = (Map<String, ?>) query("getbalance");
        BigDecimal btc_balance = (BigDecimal) balance.get("bitcoin");
        return btc_balance;
    }

    @Override
    //implemented for Elements to return just bitcoin key
    public BigDecimal getBalance(String account) throws GenericRpcException {
        Map<String, ?> balance = (Map<String, ?>) query("getbalance", account);
        BigDecimal btc_balance = (BigDecimal) balance.get("bitcoin");
        return btc_balance;
    }

    @Override
    public BigDecimal getBalance(String account, int minConf) throws GenericRpcException {
        Map<String, ?> balance = (Map<String, ?>) query("getbalance", account, minConf);
        BigDecimal btc_balance = (BigDecimal) balance.get("bitcoin");
        return btc_balance;
    }

    public Map<String, ?> getFullBalance() throws GenericRpcException {
        return (Map<String, ?>) query("getbalance");
    }

    public MapWrapperType getSideChainInfo() throws GenericRpcException {
        return new MapWrapper((Map<String, ?>) query("getsidechaininfo"));
    }

    public String createBlindedAddress(String address, String blindingKey) throws GenericRpcException {
        return (String) query("createblindedaddress", address, blindingKey);
    }

    public MapWrapperType dumpAssetLabels() {
        return new MapWrapper((Map<String, ?>) query("dumpassetlabels"));
    }

    public MapWrapperType getPakInfo() throws GenericRpcException {
        //return new MapWrapper((Map<String, ?>) query("getpakinfo"));
        Map<String, ?> result = (Map<String, ?>) query("getpakinfo");
        Map<String, ?> result2 = (Map<String, ?>) result.get("block_paklist");
        return new MapWrapper(result2);
    }

    public String blindRawTransaction(String hexString) throws GenericRpcException {
        return (String) query("blindrawtransaction", hexString);
    }

    public String blindRawTransaction(String hexString, boolean ignoreBlindFail) throws GenericRpcException {
        return (String) query("blindrawtransaction", hexString, ignoreBlindFail);
    }

    public String blindRawTransaction(String hexString, boolean ignoreBlindFail, List<Map<String, ?>> assetCommitment) throws GenericRpcException {
        return (String) query("blindrawtransaction", hexString, ignoreBlindFail, assetCommitment);
    }

    public String blindRawTransaction(String hexString, boolean ignoreBlindFail, List<Map<String, ?>> assetCommitment, boolean blindIssuances) throws GenericRpcException {
        return (String) query("blindrawtransaction", hexString, ignoreBlindFail, assetCommitment, blindIssuances);
    }

    public String dumpBlindingKey(String address) throws GenericRpcException {
        return (String) query("dumpblindingkey", address);
    }

    public void importBlindingKey(String address, String hexKey) throws GenericRpcException {
        query("importblindingkey", address, hexKey);
    }

    public MapWrapperType issueAsset(int assetAmount, int tokenAmount) throws GenericRpcException {
        return new MapWrapper((Map<String, ?>) query("issueasset", assetAmount, tokenAmount));
    }

    public MapWrapperType issueAsset(int assetAmount, int tokenAmount, boolean blind) throws GenericRpcException {
        return new MapWrapper((Map<String, ?>) query("issueasset", assetAmount, tokenAmount, blind));
    }

    public MapWrapperType issueAsset(int assetAmount, int tokenAmount, boolean blind, String contractHash) throws GenericRpcException {
        return new MapWrapper((Map<String, ?>) query("issueasset", assetAmount, tokenAmount, blind, contractHash));
    }

    public List<Issuance> rawIssueAsset(String transaction, List<Map<String, ?>> issuances) throws GenericRpcException {
        return new IssuanceListMapWrapper((List<Map<String, ?>>) query("rawissueasset", transaction, issuances));
    }

    @Override
    @SuppressWarnings("unchecked")
    public WalletInfo getWalletInfo() {
        return new WalletInfoWrapperElements((Map<String, ?>) query("getwalletinfo"));
    }

    /**
     * Rescan the local blockchain for wallet related transactions. This method
     * without argument rescan all the blocks.
     * The read timeout is set to half a day. because this method take a while. It
     * depends on hardware and blockchain size. Original timeout is restore after
     * the query.
     *
     * @see <a href="https://bitcoincore.org/en/doc/0.20.0/rpc/wallet/rescanblockchain/">rescanblockchain</a>
     */
    @Override
    public void rescanBlockchain() throws GenericRpcException {
        int savedReadTimeout = this.readTimeout;
        // Change the read timeout to restore it after
        this.readTimeout = (int) TimeUnit.MINUTES.toMillis(720);
        query("rescanblockchain");
        // Restore the previous timeout
        this.readTimeout = savedReadTimeout;
    }

    public AddressInfoElements getAddressInfo(String address) throws GenericRpcException {
        return new AddressInfoElementsMapWrapper((Map<String, ?>) query("getaddressinfo", address));
    }

    @Override
    public Object importAddress(String address, String label, boolean rescan) throws GenericRpcException {
        query("importaddress", address, label, rescan);
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Number> listAccounts() throws GenericRpcException {
        return (Map<String, Number>) query("listaccounts");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Number> listAccounts(int minConf) throws GenericRpcException {
        return (Map<String, Number>) query("listaccounts", minConf);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Number> listAccounts(int minConf, boolean watchOnly) throws GenericRpcException {
        return (Map<String, Number>) query("listaccounts", minConf, watchOnly);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<LockedUnspent> listLockUnspent() {

        return new ListMapWrapper<LockedUnspent>((List<Map<String, ?>>) query("listlockunspent")) {
            protected LockedUnspent wrap(final Map m) {
                return new LockedUnspentWrapper(m);
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReceivedAddress> listReceivedByAddress() throws GenericRpcException {
        return new ReceivedAddressListWrapper((List<Map<String, ?>>) query("listreceivedbyaddress"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReceivedAddress> listReceivedByAddress(int minConf) throws GenericRpcException {
        return new ReceivedAddressListWrapper((List<Map<String, ?>>) query("listreceivedbyaddress", minConf));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ReceivedAddress> listReceivedByAddress(int minConf, boolean includeEmpty) throws GenericRpcException {
        return new ReceivedAddressListWrapper((List<Map<String, ?>>) query("listreceivedbyaddress", minConf, includeEmpty));
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionsSinceBlock listSinceBlock() throws GenericRpcException {
        return new TransactionsSinceBlockImpl((Map<String, ?>) query("listsinceblock"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionsSinceBlock listSinceBlock(String blockHash) throws GenericRpcException {
        return new TransactionsSinceBlockImpl((Map<String, ?>) query("listsinceblock", blockHash));
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations) throws GenericRpcException {
        return new TransactionsSinceBlockImpl((Map<String, ?>) query("listsinceblock", blockHash, targetConfirmations));
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations, boolean includeWatchOnly) throws GenericRpcException {
        return new TransactionsSinceBlockImpl((Map<String, ?>) query("listsinceblock", blockHash, targetConfirmations, includeWatchOnly));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> listTransactions() throws GenericRpcException {
        return new TransactionListMapWrapper((List<Map<String, ?>>) query("listtransactions"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> listTransactions(String account) throws GenericRpcException {
        return new TransactionListMapWrapper((List<Map<String, ?>>) query("listtransactions", account));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> listTransactions(String account, int count) throws GenericRpcException {
        return new TransactionListMapWrapper((List<Map<String, ?>>) query("listtransactions", account, count));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Transaction> listTransactions(String account, int count, int skip) throws GenericRpcException {
        return new TransactionListMapWrapper((List<Map<String, ?>>) query("listtransactions", account, count, skip));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Unspent> listUnspent() throws GenericRpcException {
        return new UnspentListWrapper((List<Map<String, ?>>) query("listunspent"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Unspent> listUnspent(int minConf) throws GenericRpcException {
        return new UnspentListWrapper((List<Map<String, ?>>) query("listunspent", minConf));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Unspent> listUnspent(int minConf, int maxConf) throws GenericRpcException {
        return new UnspentListWrapper((List<Map<String, ?>>) query("listunspent", minConf, maxConf));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Unspent> listUnspent(int minConf, int maxConf, String... addresses) throws GenericRpcException {
        return new UnspentListWrapper((List<Map<String, ?>>) query("listunspent", minConf, maxConf, addresses));
    }

    public boolean lockUnspent(boolean unlock, String txId, int vOut) throws GenericRpcException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("txid", txId);
        params.put("vout", vOut);
        return (boolean) query("lockunspent", unlock, Arrays.asList(params).toArray());
    }

    @Override
    public boolean move(String fromAccount, String toAddress, BigDecimal amount) throws GenericRpcException {
        return (boolean) query("move", fromAccount, toAddress, amount);
    }

    @Override
    public boolean move(String fromAccount, String toAddress, BigDecimal amount, String comment) throws GenericRpcException {
        return (boolean) query("move", fromAccount, toAddress, amount, 0, comment);
    }

    @Override
    public boolean move(String fromAccount, String toAddress, BigDecimal amount, int minConf) throws GenericRpcException {
        return (boolean) query("move", fromAccount, toAddress, amount, minConf);
    }

    @Override
    public boolean move(String fromAccount, String toAddress, BigDecimal amount, int minConf, String comment) throws GenericRpcException {
        return (boolean) query("move", fromAccount, toAddress, amount, minConf, comment);
    }

    @Override
    public String sendFrom(String fromAccount, String toAddress, BigDecimal amount) throws GenericRpcException {
        return (String) query("sendfrom", fromAccount, toAddress, amount);
    }

    @Override
    public String sendFrom(String fromAccount, String toAddress, BigDecimal amount, int minConf) throws GenericRpcException {
        return (String) query("sendfrom", fromAccount, toAddress, amount, minConf);
    }

    @Override
    public String sendFrom(String fromAccount, String toAddress, BigDecimal amount, int minConf, String comment) throws GenericRpcException {
        return (String) query("sendfrom", fromAccount, toAddress, amount, minConf, comment);
    }

    @Override
    public String sendFrom(String fromAccount, String toAddress, BigDecimal amount, int minConf, String comment, String commentTo) throws GenericRpcException {
        return (String) query("sendfrom", fromAccount, toAddress, amount, minConf, comment, commentTo);
    }

    @Override
    public String sendRawTransaction(String hex) throws GenericRpcException {
        return (String) query("sendrawtransaction", hex);
    }

    @Override
    public String sendToAddress(String toAddress, BigDecimal amount) throws GenericRpcException {
        return (String) query("sendtoaddress", toAddress, amount);
    }

    @Override
    public String sendToAddress(String toAddress, BigDecimal amount, String comment) throws GenericRpcException {
        return (String) query("sendtoaddress", toAddress, amount, comment);
    }

    @Override
    public String sendToAddress(String toAddress, BigDecimal amount, String comment, String commentTo) throws GenericRpcException {
        return (String) query("sendtoaddress", toAddress, amount, comment, commentTo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AddressValidationResult validateAddress(String address) throws GenericRpcException {
        final Map<String, ?> m = (Map<String, ?>) query("validateaddress", address);
        return new AddressValidationResultWrapper(m);
    }

    @Deprecated
    @Override
    @SuppressWarnings("unchecked")
    public List<String> generate(int numBlocks) throws BitcoinRPCException {
        return (List<String>) query("generate", numBlocks);
    }

    @Deprecated
    @Override
    @SuppressWarnings("unchecked")
    public List<String> generate(int numBlocks, long maxTries) throws BitcoinRPCException {
        return (List<String>) query("generate", numBlocks, maxTries);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> generateToAddress(int numBlocks, String address) throws BitcoinRPCException {
        return (List<String>) query("generatetoaddress", numBlocks, address);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> generateToAddress(int numBlocks, String address, long maxTries) throws BitcoinRPCException {
        return (List<String>) query("generatetoaddress", numBlocks, address, maxTries);
    }

    @Override
    public BigDecimal estimateFee(int nBlocks) throws GenericRpcException {
        return (BigDecimal) query("estimatefee", nBlocks);
    }

    @Override
    public void invalidateBlock(String hash) throws GenericRpcException {
        query("invalidateblock", hash);
    }

    @Override
    public void reconsiderBlock(String hash) throws GenericRpcException {
        query("reconsiderblock", hash);

    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PeerInfoResult> getPeerInfo() throws GenericRpcException {
        final List<Map<String, ?>> l = (List<Map<String, ?>>) query("getpeerinfo");
        return new AbstractList<PeerInfoResult>() {

            @Override
            public PeerInfoResult get(int index) {
                return new PeerInfoWrapper(l.get(index));
            }

            @Override
            public int size() {
                return l.size();
            }
        };
    }

    protected class WalletInfoWrapperElements extends WalletInfoWrapper {

        protected WalletInfoWrapperElements(Map<String, ?> m) {
            super(m);
        }

        @Override
        public BigDecimal balance() {
            Map<String, ?> balance = (Map<String, ?>) m.get("balance");
            return mapBigDecimal(balance, "bitcoin");
        }

        @Override
        public BigDecimal unconfirmedBalance() {
            Map<String, ?> u_balance = (Map<String, ?>) m.get("unconfirmed_balance");
            return mapBigDecimal(u_balance, "bitcoin");
        }

        @Override
        public BigDecimal immatureBalance() {
            Map<String, ?> i_balance = (Map<String, ?>) m.get("immature_balance");
            return mapBigDecimal(i_balance, "bitcoin");
        }
    }

    /*********** Issuances ************/
    static public interface Issuance extends MapWrapperType, Serializable {

        String txId(); //"hex",                 (string) Transaction id for issuance

        String entropy();//"hex",               (string) Entropy of the asset type

        String asset(); //"hex",                (string) Asset type for issuance if known

        String assetLabel(); //"str",           (string) Asset label for issuance if set

        String token(); //"hex",                (string) Token type for issuance

        int vin(); // n,                        (numeric) The input position of the issuance in the transaction

        int assetAmount(); // n,                (numeric) The amount of asset issued. Is -1 if blinded and unknown to wallet

        int tokenAmount(); // n,                (numeric) The reissuance token amount issued. Is -1 if blinded and unknown to wallet

        boolean isReissuance(); //true|false,   (boolean) Whether this is a reissuance

        String assetBlinds(); //"hex",          (string) Blinding factor for asset amounts

        String tokenBlinds(); //"hex"           (string) Blinding factor for token amounts

        String hex(); //"hex"
    }

    public class IssuanceWrapper extends MapWrapper implements Issuance, Serializable {

        public IssuanceWrapper(Map<String, ?> m) {
            super(m);
        }

        @Override
        public String txId() {
            return mapStr("txId");
        }

        @Override
        public String entropy() {
            return mapStr("entropy");
        }

        @Override
        public String asset() {
            return mapStr("asset");
        }

        @Override
        public String assetLabel() {
            return mapStr("assetLabel");
        }

        @Override
        public String token() {
            return mapStr("token");
        }

        @Override
        public int vin() {
            return mapInt("vin");
        }

        @Override
        public int assetAmount() {
            return mapInt("assetAmount");
        }

        @Override
        public int tokenAmount() {
            return mapInt("tokenAmount");
        }

        @Override
        public boolean isReissuance() {
            return mapBool("isReissuance");
        }

        @Override
        public String assetBlinds() {
            return mapStr("assetBlinds");
        }

        @Override
        public String tokenBlinds() {
            return mapStr("tokenBlinds");
        }

        @Override
        public String hex() {
            return mapStr("hex");
        }

        /*
        @Override
        public RawTransaction raw() {
            if (raw == null)
                try {
                    raw = getRawTransaction(txId());
                } catch (GenericRpcException ex) {
                    logger.warning(ex.getMessage());
                }
            return raw;
        }
        */
        @Override
        public String toString() {
            return m.toString();
        }
    }

    protected class IssuanceListMapWrapper extends ListMapWrapper<Issuance> {

        protected IssuanceListMapWrapper(List<Map<String, ?>> list) {
            super(list);
        }

        @Override
        protected Issuance wrap(Map<String, ?> m) {
            return new IssuanceWrapper(m);
        }
    }

    public List<Issuance> listIssuances() throws GenericRpcException {
        return new IssuanceListMapWrapper((List<Map<String, ?>>) query("listissuances"));
    }

    public interface AddressInfoElements extends AddressInfo {
        String confidentialKey();

        String unconfidentialAddress();
    }

    protected class AddressInfoElementsMapWrapper extends AddressInfoMapWrapper implements AddressInfoElements {

        protected AddressInfoElementsMapWrapper(Map<String, ?> m) {
            super(m);
        }

        public String confidentialKey() {
            return mapStr("confidential_key");
        }

        public String unconfidentialAddress() {
            return mapStr("unconfidential");
        }
    }

    /************************************************************/
}
