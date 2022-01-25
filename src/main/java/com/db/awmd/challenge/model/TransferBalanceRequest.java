package com.db.awmd.challenge.model;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class TransferBalanceRequest {

	@NotNull
	@NotEmpty
	private String payorAccountId;
	
	@NotNull
	@NotEmpty
	private String payeeAccountId;
	
	@NotNull
	@DecimalMin(value="0.0",inclusive = false)
	private BigDecimal transferAmmount;
	
	
	
}
