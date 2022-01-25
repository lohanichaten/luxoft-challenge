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

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  
  public void transferBalance(TransferBalanceRequest request) {
	 Account payorAccount= accountsRepository.getAccount(request.getPayorAccountId());
	 Account payeeAccount= accountsRepository.getAccount(request.getPayeeAccountId());
	  
	 BigDecimal balanceTransfer=request.getTransferAmmount();
	
	 
	 payorAccount.withdraw(balanceTransfer);
	 payeeAccount.deposit(balanceTransfer);
	 
	
  }
  
  
  
  
  
}
