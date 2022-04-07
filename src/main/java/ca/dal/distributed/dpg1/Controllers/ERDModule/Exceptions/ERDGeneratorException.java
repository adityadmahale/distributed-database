package ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions;

public final class ERDGeneratorException extends Exception {

  private final String errorMessage;

  public ERDGeneratorException(final String errorMessage) {
    super(errorMessage);
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String toString() {
    return "SQLERDException{" +
        "errorMessage='" + errorMessage + '\'' +
        '}';
  }
}