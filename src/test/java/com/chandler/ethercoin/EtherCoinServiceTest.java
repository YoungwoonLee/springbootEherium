package com.chandler.ethercoin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.chandler.ethercoin.service.EtherCoinService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EtherCoinServiceTest {

	String eoa1 = "0xca244eeb8ee6508213eeefa29ec8938126e1fb2e";
	String passphrase1 = "curos01";
	String eoa2 = "0x9f4acd2cf0933fc90a75272b370f59e4d436092f";
	String passphrase2 = "curos02";
	String eoa3 = "0xa8b259f0fc483cfa6b853b1282b7ba6df6461a45";
	String passphrase3 = "curos03";
	
	String newPassphrase = "temp01";
	
	
    @Autowired
    private EtherCoinService cucoinService;
    
//    private Web3j web3j ;

    @Before
	public void initialWeb3j() {
//    	web3j = cucoinService.buildHttpClientWeb3("192.168.20.164", "8545");;
	}
    
    @Test
    public void testGetClientVersion() throws Exception {
    	String clientVersion = cucoinService.getClientVersion();
    	
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    	System.out.println("@~~ clientVersion=" + clientVersion);
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    	
    	assertThat(clientVersion).startsWith("Geth/");
    }
    
    @Test
    public void testGetClientVersion2() throws Exception {
    	String clientVersion = cucoinService.getClientVersion2();

    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    	System.out.println("@~~ clientVersion=" + clientVersion);
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    	
    	assertThat(clientVersion).startsWith("Geth/");
    }
    
    /**
     * 계정 리스트 조회 테스트
     */
    @Test
    public void testGetAccounts() throws Exception {
    	List<String> eoaList = cucoinService.getAccounts();
    	
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    	for (String eoa:eoaList) {
    		System.out.println("EOA address: " + eoa);
    	}
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    }
    
    @Test
    public void testGetBalance() throws Exception {
    	BigInteger wei = cucoinService.getBalance(eoa1);
    	
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    	System.out.println("Balance of eoa1: " + wei);
    	System.out.println("@~~@~~@~~@~~@~~@~~@~~@~~@~~");
    }
    
    private AtomicInteger count = new AtomicInteger();
    @Test 
    public void testGetBalanceThreads() throws InterruptedException {
    	count.set(0);
    	System.out.println("Start main method.................................................");
        final ArrayList< Thread > threadList = new ArrayList< Thread >();
     
		for(int i = 0 ; i < 1000 ; i++ ){
			
	        Runnable runnable = new Runnable() {
	            
	        	@Override 
	            public void run() {
	        		count.incrementAndGet();

	                try {
						
	                	testGetBalance();
						
					} catch (Exception e) {
						e.printStackTrace();
					}

	            }
	        };
	        Thread thread = new Thread( runnable );
	        threadList.add(thread);
			
	        thread.start();
		}
        
		for (Thread t : threadList) {
			t.join();
		}
		System.out.println("Finish main method.................................................Total Thread=" + count);
    }
    
    
	@After
	public void finishWeb3j() {
		System.out.println("@~~ finish :: Atomic count=" + count.get());
	}
}
