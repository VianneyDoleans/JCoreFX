package JCoreFX.tools;

import JCoreFX.core.log.Log;
import java.util.logging.Level;

/**
 * Methods use to write error message on log file when an exception is catch
 */
public class ExceptionsCatch
{
    public static void PrintErrors(Exception ex)
    {
        Log.getInstance().write(Level.SEVERE, ex.toString());
        for (StackTraceElement stack : ex.getStackTrace()) {
            Log.getInstance().write(Level.SEVERE, stack);
        }
    }
}