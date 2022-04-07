package ca.dal.distributed.dpg1.Controllers.ERDModule.Exceptions;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description ERD Generator common Exception class for throwing error messages
 */
public final class ERDGeneratorException extends Exception {

  private final String errorMessage;

  /**
   * @author Bharatwaaj Shankaranarayanan
   * @description Constructor for the ERD Generator Exception Class
   */
  public ERDGeneratorException(final String errorMessage) {
    super(errorMessage);
    this.errorMessage = errorMessage;
  }

  /**
   * @author Bharatwaaj Shankaranarayanan
   * @description Message to get the error message if required from the Exception
   *              Object
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @author Bharatwaaj Shankaranarayanan
   * @description Overriding toString() to print the exception message
   */
  @Override
  public String toString() {
    return "SQLERDException{" +
        "errorMessage='" + errorMessage + '\'' +
        '}';
  }
}