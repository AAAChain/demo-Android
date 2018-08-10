package org.aaa.chain;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.admin.methods.response.BooleanResponse;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.geth.Geth;
import org.web3j.protocol.geth.GethFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import rx.Subscription;

public class AAAWalletUtils {

    // GAS价格
    public static BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
    // GAS上限
    public static BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000L);

    // 交易费用
    public static BigInteger GAS_VALUE = BigInteger.valueOf(1300_000L);

    private static String URL = "https://rinkeby.infura.io/YMJVwtsOg1JeHYzvNlbw";
    //private static String URL = "http://10.0.2.2:8540";
    //private static String URL = "http://localhost:8540";
    private static HttpService service = new HttpService(URL);
    private static Geth geth = GethFactory.build(service);

    public static void main(String[] args) throws IOException {
        //System.out.println("new account:" + newAccount("123321"));
        //System.out.println("web3j account size:" + getAllAccounts().size() + "---web3j account:" + getAllAccounts());
        //System.out.println("admin account:" + getAccountsFromAdmin().size() + "---admin account:" + getAccountsFromAdmin());
        //System.out.println("balance:" + getAccountBalance(getAllAccounts().get(0)));
        //System.out.println("block num:" + getCurrentBlockNumber());
        //observableBlockChain();
        //getAllAccounts();
        //
        //EthTransaction transaction = getTransactionByHash("0xa71243e362b5e5213f8544fc3188d06808c1e30b6cfc9f77325b25f87ac40c92");
        //System.out.println("----" + transaction.getTransaction().getBlockHash());
        //System.out.println(transaction.getTransaction().getHash());
        //
        //EthBlock block = getBlockByHash(transaction.getTransaction().getBlockHash(), true);
        //System.out.println("block:" + block.getResult());
    }

    public static void createWallet(String passWord)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        File keystoreFile = Environment.getExternalStoragePublicDirectory("aaakeystore");
        if (!keystoreFile.exists()) {
            keystoreFile.mkdir();
        }

        String fileName = WalletUtils.generateNewWalletFile(passWord, keystoreFile, false);

        Credentials credentials = WalletUtils.loadCredentials(passWord, new File(keystoreFile, fileName));

        String walletAddress = credentials.getAddress();
        BigInteger privatekey = credentials.getEcKeyPair().getPrivateKey();
        BigInteger publickey = credentials.getEcKeyPair().getPublicKey();

        Log.i("info", "wallet address:" + walletAddress);
        Log.i("info", "wallet private key:" + privatekey);
        Log.i("info", "wallet public key:" + publickey);
    }

    public static String getWallet() {
        String filePath = null;
        File keystoreFile = Environment.getExternalStoragePublicDirectory("aaakeystore");
        if (keystoreFile.exists() && keystoreFile.listFiles().length > 0) {
            filePath = keystoreFile.listFiles()[0].getPath();
        }
        return filePath;
    }

    public static String getAddress() throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials("123456", getWallet());
        return credentials.getAddress();
    }

    /**
     * 获取所有钱包地址列表
     */
    public static List<String> getAllAccounts() throws IOException {
        Request<?, EthAccounts> request = geth.ethAccounts();
        List<String> list = request.send().getAccounts();
        return list;
    }

    //public static List<String> getAccountsFromWeb3j() throws IOException {
    //    Request<?, EthAccounts> accountsRequest = web3j.ethAccounts();
    //    EthAccounts accounts = accountsRequest.send();
    //    return accounts.getAccounts();
    //}

    /**
     * 通过admin获取钱包地址列表
     */

    public static List<String> getAccountsFromAdmin() throws IOException {
        Request<?, PersonalListAccounts> personalListAccountsRequest = geth.personalListAccounts();
        PersonalListAccounts personalListAccounts = personalListAccountsRequest.send();
        return personalListAccounts.getAccountIds();
    }

    /**
     * 输入密码创建钱包地址
     */
    public static String newAccount(String password) throws IOException {
        Request<?, NewAccountIdentifier> request = geth.personalNewAccount(password);
        NewAccountIdentifier result = request.send();
        return result.getAccountId();
    }

    /**
     * 获得当前区块高度
     */
    public static BigInteger getCurrentBlockNumber() throws IOException {
        Request<?, EthBlockNumber> request = geth.ethBlockNumber();
        return request.send().getBlockNumber();
    }

    /**
     * 解锁账户，发送交易前需要对账户进行解锁
     *
     * @param address 地址
     * @param password 密码
     * @param duration 解锁有效时间，单位秒
     * @throws IOException
     */
    public static Boolean unlockAccount(String address, String password, BigInteger duration) throws IOException {
        Request<?, PersonalUnlockAccount> request = geth.personalUnlockAccount(address, password, duration);
        PersonalUnlockAccount account = request.send();
        return account.accountUnlocked();
    }

    /**
     * 账户解锁，使用完成之后需要锁定
     *
     * @throws IOException
     */
    public static Boolean lockAccount(String address) throws IOException {
        Request<?, BooleanResponse> request = geth.personalLockAccount(address);
        BooleanResponse response = request.send();
        return response.success();
    }

    /**
     * 根据交易hash值获取交易
     *
     * @throws IOException
     */
    public static EthTransaction getTransactionByHash(String txHash) throws IOException {
        Request<?, EthTransaction> request = geth.ethGetTransactionByHash(txHash);
        return request.send();
    }

    /**
     * 根据交易的blockhash值获取块信息
     *
     * @throws IOException
     */
    public static EthBlock getBlockByHash(String blockHash, boolean fullTransaction) throws IOException {
        Request<?, EthBlock> request = geth.ethGetBlockByHash(blockHash, fullTransaction);
        return request.send();
    }

    /**
     * 获得ethblock
     *
     * @param blockNumber 区块编号
     * @throws IOException
     */
    public static EthBlock getBlockEthBlock(Integer blockNumber) throws IOException {

        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        Request<?, EthBlock> request = geth.ethGetBlockByNumber(defaultBlockParameter, true);
        EthBlock ethBlock = request.send();
        return ethBlock;
    }

    /**
     * 发送交易并获得交易hash值
     *
     * @throws IOException
     */
    public static String sendTransaction(Transaction transaction, String password) throws IOException {
        Request<?, EthSendTransaction> request = geth.personalSendTransaction(transaction, password);
        EthSendTransaction ethSendTransaction = request.send();
        return ethSendTransaction.getTransactionHash();
    }

    /**
     * 指定地址发送交易所需nonce获取
     *
     * @param address 待发送交易地址
     * @throws IOException
     */
    public static BigInteger getNonce(String address) throws IOException {
        Request<?, EthGetTransactionCount> request = geth.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST);
        return request.send().getTransactionCount();
    }

    /**
     * 获取链上总高度
     *
     * @throws IOException
     */
    public static EthBlockNumber getBlockTotalNum() throws IOException {
        Request<?, EthBlockNumber> request = geth.ethBlockNumber();
        return request.send();
    }

    /**
     * 获取用户余额
     */
    public static BigDecimal getAccountBalance(String address) throws IOException {
        EthGetBalance ethGetBalance = geth.ethGetBalance(address, DefaultBlockParameterName.fromString("latest")).send();
        System.out.println(ethGetBalance.getBalance());
        //.new BigDecimal(ethGetBalance.getBalance() + "").divide(BigDecimal.valueOf(Math.pow(10, 18))).setScale(6)
        return Convert.fromWei(ethGetBalance.getBalance().toString(), Convert.Unit.ETHER);
    }

    /**
     * 转账
     *
     * @param fromAddress 当前钱包址
     * @param password 当前钱包地址密码
     * @param moneyNum 转账数目
     * @param keySource 转账keystore文件路径
     * @param toAddress 转账目标钱包地址
     * @throws IOException
     */
    public static EthSendTransaction transferBalance(String fromAddress, String password, String moneyNum, String keySource, String toAddress) {
        EthSendTransaction ethSendTransaction = null;
        try {
            boolean flag = unlockAccount(fromAddress, password, new BigInteger(1800 + ""));
            System.out.println(flag);

            BigInteger value = Convert.toWei(moneyNum, Convert.Unit.ETHER).toBigInteger();

            Credentials credentials = WalletUtils.loadCredentials(password, keySource);

            BigInteger nonce = getNonce(fromAddress);

            // create our transaction
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, GAS_PRICE, GAS_LIMIT, toAddress, value);

            // sign & send our transaction
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            ethSendTransaction = geth.ethSendRawTransaction(hexValue).send();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ethSendTransaction;
    }

    public static void observableBlockChain() {
        Subscription subscription = geth.blockObservable(false).subscribe(block -> {
            EthBlock.Block blk = block.getResult();
            System.out.println("当前区块高度：" + blk.getNumber() + ",上一个区块：" + blk.getParentHash() + blk.getHash());
        });
        System.out.println("订阅的" + subscription.hashCode());
    }

    /**
     * 时间戳转为正常日期
     *
     * @return String
     */
    public static String timestampToDate(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timestamp * 1000));
    }

    /**
     * wei转ether
     *
     * @return String
     */
    public static BigDecimal fromWei(String wei) {
        return Convert.fromWei(wei, Convert.Unit.ETHER);
    }

    /**
     * ether转wei
     *
     * @return String
     */
    public static BigDecimal toWei(String ether) {
        return Convert.toWei(ether, Convert.Unit.WEI);
    }
}
