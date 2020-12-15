package com.bc92.directoryservice.restapi;


import java.sql.Timestamp;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
  private HttpStatus status;
  private String message;

  private Timestamp timestamp;

  public ErrorMessage(final HttpStatus status, final String message) {
    this.status = status;
    this.message = message;
    timestamp = new Timestamp(System.currentTimeMillis());
  }

}
