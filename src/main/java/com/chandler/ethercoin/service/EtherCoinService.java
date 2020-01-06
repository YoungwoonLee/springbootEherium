package com.chandler.ethercoin.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

@Service
public class EtherCoinService {
	private static Logger log = LoggerFactory.getLogger(EtherCoinService.class);

	@Value("${cucoin.client.connect}")
	private String connect;
	
	@Value("${cucoin.admin.client}")
	private String adminAllow;
	

	public Web3j getWeb3j() {
		return Web3j.build(new HttpService(connect));
	}

	public Admin getAdmin() {
		if (("true".equals(adminAllow))) {
			return Admin.build(new HttpService(connect));
		} else {
			return null;
		}
	} 

//	@Autowired
//	private CucoinWeb3j web3j;

	/**
	 * geth client 버전 조회.  ooooo
	 */
	public String getClientVersion() throws InterruptedException, ExecutionException {
		
		Web3ClientVersion client = getWeb3j().web3ClientVersion().sendAsync().get();
		
		String clientVersion = client.getWeb3ClientVersion();
		log.debug("clientVersion={}", clientVersion);
		return clientVersion;
	}
	
	/**
	 * geth client 버전 조회.  ooooo
	 */
    public String getClientVersion2() throws IOException {    	
        Web3ClientVersion client = getWeb3j().web3ClientVersion().send();
        
        String clientVersion = client.getWeb3ClientVersion();
        log.debug("clientVersion={}", clientVersion);
        return clientVersion;
    }

    /**
     * ether base 조회  eth.coinbase
     * @param web3j
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
	public EthCoinbase getCoinbase() throws InterruptedException, ExecutionException {
		return getWeb3j()
				.ethCoinbase()
				.sendAsync()
				.get();
	}
	
	/**
	 * 계정 리스트 조회.  eth.accounts
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<String> getAccounts() throws InterruptedException, ExecutionException {
		
		EthAccounts accountList =  getWeb3j().ethAccounts().sendAsync().get();
		
		return accountList.getAccounts(); 
	}
	
	/**
	 * 잔고조회  eth.getBalance(eth.accounts[0])
	 * @param eoaAddress
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
    public BigInteger getBalance(String eoaAddress) throws IOException, InterruptedException, ExecutionException {
    	// send asynchronous requests to get balance
    	EthGetBalance ethGetBalance = getWeb3j()
    												.ethGetBalance(eoaAddress, DefaultBlockParameterName.LATEST)
    												.sendAsync()
    												.get();

    	BigInteger wei = ethGetBalance.getBalance();
    	
    	log.debug("balance={}", wei);
    	return wei;
    }
    
    /**
     * 아직미검증.............  계정생성
     * @param admin
     * @param passphrase
     * @return
     * @throws IOException
     */
    public String newAccount(String passphrase) throws IOException {

    	if (null == getAdmin()) {
    		return "어드민 접속이 허용되어있지 않네요....."; // 나중에 익셉션 throws해야....
    	}
    	
    	NewAccountIdentifier newAccountIdentifier = getAdmin().personalNewAccount(passphrase).send();
		String addressOfEOA = newAccountIdentifier.getAccountId();
		log.debug("New account EOA address: {}", addressOfEOA);
		
		log.info("@~~@~~" + newAccountIdentifier.toString());
		log.info("@~~@~~" + newAccountIdentifier.getJsonrpc());
		log.info("@~~@~~" + newAccountIdentifier.getRawResponse());

		return addressOfEOA;    	
    }
    
    public Boolean unlockAccount(Admin admin, String eoa, String pass) {
		Boolean isUnlocked = false;
		// 계정 잠금 해제 시간 단위 초 기본값 300 초 :: 여기서는 60초
		BigInteger unlockDuration = BigInteger.valueOf(60L);
		try {
			PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(eoa, pass, unlockDuration).send();
			isUnlocked = personalUnlockAccount.accountUnlocked();
			log.debug("Account unlock {}", isUnlocked);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return isUnlocked;
	}
}
