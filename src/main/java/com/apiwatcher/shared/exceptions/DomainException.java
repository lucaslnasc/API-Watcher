package com.apiwatcher.shared.exceptions;

/**
 * Exceção base para erros de domínio.
 * Representa violações de regras de negócio.
 */
public class DomainException extends RuntimeException {

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
