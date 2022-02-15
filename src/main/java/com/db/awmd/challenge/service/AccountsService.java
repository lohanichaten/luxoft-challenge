package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InfSufficientBalanceException;
import com.db.awmd.challenge.model.TransferBalanceRequest;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  @Getter
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository,NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService=notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  
  
  public void transferBalance(TransferBalanceRequest request) {
	 synchronized (request) {
		 Account payorAccount= accountsRepository.getAccount(request.getPayorAccountId());
		 Account payeeAccount= accountsRepository.getAccount(request.getPayeeAccountId());
		 
		 BigDecimal initialPayroBalance=payorAccount.getBalance();
		 BigDecimal intialPayeeBalance=payeeAccount.getBalance();
		 
	    try {
	    	
	    	
			  
			 BigDecimal balanceTransfer=request.getTransferAmmount();
			
			 
			 payorAccount.withdraw(balanceTransfer);
			 payeeAccount.deposit(balanceTransfer);
			 this.notificationService.notifyAboutTransfer(payorAccount, "Amount debited:"+balanceTransfer);
			 this.notificationService.notifyAboutTransfer(payeeAccount, "Amount credited:"+balanceTransfer);
			 
			
	    	
	    }catch(Exception ex ) {
	    	//set initial balance
	    	payorAccount.setBalance(initialPayroBalance);
	    	payeeAccount.setBalance(intialPayeeBalance);
	    	throw ex;
	    }
		 
		 
	}
	  
	 
  }
  
  
  
  
  
}
