package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.naming.InsufficientResourcesException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.db.awmd.challenge.exception.InfSufficientBalanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }
  
  
  public  void withdraw(BigDecimal amount) {
	  if(this.balance.compareTo(amount)<0) {
		    throw new InfSufficientBalanceException("Insufficient amount");
	  }

	  this.balance=this.balance.subtract(amount);
	  
  }
  
  public  void deposit(BigDecimal amount) {
	
	  this.balance=this.balance.add(amount);
  }
  
  
  
}
