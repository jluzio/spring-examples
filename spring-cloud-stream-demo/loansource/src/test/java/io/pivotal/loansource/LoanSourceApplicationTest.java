package io.pivotal.loansource;

import static org.assertj.core.api.Assertions.assertThat;

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