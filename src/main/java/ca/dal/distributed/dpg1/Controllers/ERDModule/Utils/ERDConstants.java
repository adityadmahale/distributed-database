package ca.dal.distributed.dpg1.Controllers.ERDModule.Utils;

import ca.dal.distributed.dpg1.Utils.GlobalConstants;

/**
 * @author Bharatwaaj Shankaranarayanan
 * @description Constants file for the ERD Module
 */
public final class ERDConstants {

  public static final String SQL_ERD_PATH = "outputs/erds/";
  public static final String SQL_ERD_FILE_PREFIX = "ERD" + GlobalConstants.STRING_UNDERSCORE;
  public static final String ERROR_MESSAGE_FAILED_TO_GENERATE_ERD = "Error: Failed to generate ERD for database ";
  public static final String ERROR_CAUSE_MESSAGE_INVALID_DATABASE_NAME = ". Invalid database name!";
  public static final String SUCCESS_MESSAGE_ERD_GENERATION = "ERD generated successfully for database ";
}