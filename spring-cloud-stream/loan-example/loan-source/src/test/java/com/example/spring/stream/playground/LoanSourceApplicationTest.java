package com.example.spring.stream.playground;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.spring.stream.loansource.Loan;
import com.example.spring.stream.loansource.LoanSourceApplication;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class LoanSourceApplicationTest {

  @Test
  void supplyLoan() {
    LoanSourceApplication app = new LoanSourceApplication();
    Supplier<Loan> loan = app.supplyLoan();
    assertThat(loan).isNotNull();
    assertThat(loan.get()).isNotNull();
    assertThat(loan.get().getAmount()).isNotNull();
    assertThat(loan.get().getName()).isNotNull();
  }
}