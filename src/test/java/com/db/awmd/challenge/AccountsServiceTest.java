package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;



import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InfSufficientBalanceException;
import com.db.awmd.challenge.model.TransferBalanceRequest;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  
  
  @After
  public void tearDown() {
	 
  }
  

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }
  
  
  @Test
  public void transferBalance_withSuccess() {
	   Account payorAccount = new Account("Id-234");
	   payorAccount.setBalance(new BigDecimal(1000));
	  
	  
	   Account payeeAccount= new Account("Id-546");
	   payeeAccount.setBalance(new BigDecimal(100));
	   
	   this.accountsService.createAccount(payorAccount);
	   this.accountsService.createAccount(payeeAccount);
	   
	   TransferBalanceRequest request= new TransferBalanceRequest();
	   request.setPayorAccountId("Id-234");
	   request.setPayeeAccountId("Id-546");
	   request.setTransferAmmount(new BigDecimal("500"));
	   
	   this.accountsService.transferBalance(request);
	   
	   assertThat(payorAccount.getBalance().equals(new BigDecimal(500)));
	   assertThat(payeeAccount.getBalance().equals(new BigDecimal(600)));
	   
  }
  
  
  @Test(expected = InfSufficientBalanceException.class)
  public void transerBalance_withExcessAmount_expectError() {
	   Account payorAccount = new Account("Id-432");
	   payorAccount.setBalance(new BigDecimal(1000));
	  
	   Account payeeAccount= new Account("Id-235");
	   payeeAccount.setBalance(new BigDecimal(100));
	   
	   this.accountsService.createAccount(payorAccount);
	   this.accountsService.createAccount(payeeAccount);
	   
	   TransferBalanceRequest request= new TransferBalanceRequest();
	   request.setPayorAccountId("Id-432");
	   request.setPayeeAccountId("Id-235");
	   request.setTransferAmmount(new BigDecimal("2000"));
	   
	   this.accountsService.transferBalance(request);
	   
  }
  
  
  @Test
  public void transferBalanceConcurrentlyWithLimit_success() throws InterruptedException {
	   Account payorAccount = new Account("Id-333");
	   payorAccount.setBalance(new BigDecimal(2000));
	  
	   Account payeeAccount= new Account("Id-222");
	   payeeAccount.setBalance(new BigDecimal(0));
	   
	   this.accountsService.createAccount(payorAccount);
	   this.accountsService.createAccount(payeeAccount);
	   
	   
	   TransferBalanceRequest request= new TransferBalanceRequest();
	   request.setPayorAccountId("Id-333");
	   request.setPayeeAccountId("Id-222");
	   request.setTransferAmmount(new BigDecimal("500"));
	
	   Thread[] threads=new Thread[4];
	   
	   for(int i=0;i<4;i++) {
		   threads[i]=new Thread() {
			   
			@Override
			public void run() {
				System.out.println("transfering");
				accountsService.transferBalance(request);
			}
			   
		   };
		   
		   threads[i].start();
		   
	   }
	   
	   
	   for(Thread t:threads) {
		   t.join();
		   
	   }
	   
	    System.out.println(this.accountsService.getAccount("Id-333").getBalance());
	   
	   assertEquals(this.accountsService.getAccount("Id-333").getBalance(),BigDecimal.ZERO);
	   assertEquals(this.accountsService.getAccount("Id-222").getBalance(),new BigDecimal(2000));
	   
	   
	   
	  
  }
  
  
  @Test
  public void transferBalanceConcurrentlyWithAboveLimit() throws InterruptedException {
	   Account payorAccount = new Account("Id-444");
	   payorAccount.setBalance(new BigDecimal(2000));
	  
	   Account payeeAccount= new Account("Id-555");
	   payeeAccount.setBalance(new BigDecimal(0));
	   
	   this.accountsService.createAccount(payorAccount);
	   this.accountsService.createAccount(payeeAccount);
	   
	   
	   TransferBalanceRequest request= new TransferBalanceRequest();
	   request.setPayorAccountId("Id-444");
	   request.setPayeeAccountId("Id-555");
	   request.setTransferAmmount(new BigDecimal("500"));
	
	   Thread[] threads=new Thread[5];
	   
	   for(int i=0;i<5;i++) {
		   threads[i]=new Thread() {
			   
			@Override
			public void run() {
				try {
					accountsService.transferBalance(request);
				}catch(InfSufficientBalanceException ex) {
					ex.printStackTrace();
				}
				
			}
			   
		   };
		   
		   threads[i].start();
		   
	   }
	   
	   
	   for(Thread t:threads) {
		   t.join();
		   
	   }
	   
	    System.out.println(this.accountsService.getAccount("Id-444").getBalance());
	   
	   assertEquals(this.accountsService.getAccount("Id-444").getBalance(),BigDecimal.ZERO);
	   assertEquals(this.accountsService.getAccount("Id-555").getBalance(),new BigDecimal(2000));
	   
	   
	   
	  
  }
  
  
  
}
