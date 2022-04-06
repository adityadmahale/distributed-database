package backend.log_generator.controller;

import backend.authentication.session.BackendSession;
import backend.log_generator.constant.Constant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public final class GeneralLogController {

  private void storeLog(final String message,
                        final Instant instant) {
    try (final FileWriter queryLogFileWriter = new FileWriter(Constant.GENERAL_LOG_FILE, true)) {
      queryLogFileWriter.append("Timestamp");
      queryLogFileWriter.append(": ");
      queryLogFileWriter.append("").append(String.valueOf(System.currentTimeMillis()));
      queryLogFileWriter.append(" | ");
      queryLogFileWriter.append(BackendSession.getLoggedInUser().getUserName());
      queryLogFileWriter.append(" | ");
      queryLogFileWriter.append(message);
      queryLogFileWriter.append(" | ");
      queryLogFileWriter.append(instant.toString());
      queryLogFileWriter.append("\n");
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void storeQueryLog(final String message,
                            final Instant instant) {
    final File file = new File(Constant.GENERAL_LOG_FILE);
    if (!file.exists()) {
      try {
        final boolean isNewFileCreated = file.createNewFile();
        if (isNewFileCreated) {
          storeLog(message, instant);
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    storeLog(message, instant);
  }
}