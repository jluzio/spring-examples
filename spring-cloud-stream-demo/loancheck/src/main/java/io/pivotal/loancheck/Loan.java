package io.pivotal.loancheck;


import lombok.Data;

@Data
public class Loan {

  private String uuid, name, status;
  private long amount;

  public Loan() {
  }

  public Loan(String uuid, String name, long amount) {
    this.uuid = uuid;
    this.name = name;
    this.amount = amount;
    this.setStatus(Statuses.PENDING.name());
  }

}
