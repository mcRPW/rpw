package net.mightypork.rpw.utils.logging;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;


/**
 * Static logger class.
 *
 * @author Ondřej Hruška (MightyPork)
 * @copy (c) 2013
 */
public class Log {

    /**
     * Global logger.
     */
    private static final Logger logger = Logger.getLogger("McRpMgr");

    /**
     * Logging enabled
     */
    public static boolean loggingEnabled = Config.LOGGING_ENABLED;

    private static File logfile = OsUtils.getAppDir(Paths.FILE_LOG);
    private static File logsDir = OsUtils.getAppDir(Paths.DIR_LOGS, true);

    private static int monitorId = 0;
    private static HashMap<Integer, LogMonitor> monitors = new HashMap<Integer, LogMonitor>();


    private static void cleanup() {
        // move old logs

        for (final File f : FileUtils.listDirectory(logfile.getParentFile())) {
            if (!f.isFile()) continue;
            if (f.getName().startsWith(Paths.FILENAME_LOG)) {
                final Date d = new Date(f.lastModified());

                final int index = 0;

                final String fname = (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(d) + (index == 0 ? "" : "_" + index) + ".log";

                final File f2 = new File(logsDir, fname);

                f.renameTo(f2);
            }
        }

        // delete all but last 10 logs

        final List<File> oldLogs = FileUtils.listDirectory(logsDir);

        Collections.sort(oldLogs, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (int i = 0; i < oldLogs.size() - 10; i++) {
            oldLogs.get(i).delete();
        }

    }


    /**
     * Prepare logs for logging
     */
    public static void init() {
        try {
            cleanup();

            final FileHandler handler = new FileHandler(logfile.getPath());
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);

            loggingEnabled = true;

        } catch (final Exception e) {
            e.printStackTrace();
        }

        addMonitor(new LogToSysoutMonitor());

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        logger.info("Main logger initialized.");
        logger.info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
    }


    /**
     * Add log monitor
     *
     * @param mon monitor
     * @return assigned ID
     */
    public static int addMonitor(LogMonitor mon) {
        final int id = monitorId;
        monitorId++;
        monitors.put(id, mon);
        return id;
    }


    /**
     * Remove a monitor by ID
     *
     * @param id monitor ID
     */
    public static void removeMonitor(int id) {
        monitors.remove(id);
    }


    /**
     * Enable logging.
     *
     * @param flag do enable logging
     */
    public static void enable(boolean flag) {
        loggingEnabled = flag;
    }


    /**
     * Log INFO message
     *
     * @param msg message
     */
    private static void info(String msg) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.INFO, msg);
    }


    /**
     * Log FINE (important) message
     *
     * @param msg message
     */
    private static void fine(String msg) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.FINE, msg);
    }


    /**
     * Log FINER (loss important) message
     *
     * @param msg message
     */
    private static void finer(String msg) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.FINER, msg);
    }


    /**
     * Log FINEST (least important) message
     *
     * @param msg message
     */
    private static void finest(String msg) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.FINEST, msg);
    }


    /**
     * Log WARNING message
     *
     * @param msg message
     */
    private static void warning(String msg) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.WARNING, msg);
    }


    /**
     * Log FINE message
     *
     * @param msg message
     */
    public static void f1(String msg) {
        fine(msg);
    }


    /**
     * Log FINER message
     *
     * @param msg message
     */
    public static void f2(String msg) {
        finer(msg);
    }


    /**
     * Log FINEST message
     *
     * @param msg message
     */
    public static void f3(String msg) {
        finest(msg);
    }


    /**
     * Log INFO message
     *
     * @param msg message
     */
    public static void i(String msg) {
        info(msg);
    }


    /**
     * Log WARNING message (less severe than ERROR)
     *
     * @param msg message
     */
    public static void w(String msg) {
        warning(msg);
    }


    /**
     * Log SEVERE (critical warning) message
     *
     * @param msg message
     */
    private static void severe(String msg) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.SEVERE, msg);
    }


    /**
     * Log ERROR message
     *
     * @param msg message
     */
    public static void e(String msg) {
        severe(msg);
    }


    /**
     * Log THROWING message
     *
     * @param msg    message
     * @param thrown thrown exception
     */
    public static void e(String msg, Throwable thrown) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.SEVERE, msg + "\n" + getStackTrace(thrown));
    }


    /**
     * Log THROWING message
     *
     * @param msg    message
     * @param thrown thrown exception
     */
    public static void eh(String msg, Throwable thrown) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.SEVERE, msg + "\n" + getStackTraceHeader(thrown));
    }


    /**
     * Log exception thrown
     *
     * @param thrown thrown exception
     */
    public static void e(Throwable thrown) {
        if (!loggingEnabled) {
            return;
        }
        logger.log(Level.SEVERE, getStackTrace(thrown));
    }


    /**
     * Get stack trace from throwable
     *
     * @param t
     * @return trace
     */
    private static String getStackTrace(Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }


    /**
     * Get stack trace header from throwable
     *
     * @param t
     * @return trace
     */
    private static String getStackTraceHeader(Throwable t) {
        return t.getMessage();
    }

    /**
     * PowerCraft Log file formatter.
     *
     * @author Ondřej Hruška (MightyPork)
     * @copy (c) 2012
     */
    private static class LogFormatter extends Formatter {

        /**
         * Newline string constant
         */
        private static final String nl = System.getProperty("line.separator");


        @Override
        public String format(LogRecord record) {
            final StringBuffer buf = new StringBuffer(180);

            if (record.getMessage().equals("\n")) {
                return nl;
            }

            if (record.getMessage().charAt(0) == '\n') {
                buf.append(nl);
                record.setMessage(record.getMessage().substring(1));
            }

            final Level level = record.getLevel();
            String trail = "[ ? ]";
            if (level == Level.FINE) {
                trail = "[ # ] ";
            }
            if (level == Level.FINER) {
                trail = "[ - ] ";
            }
            if (level == Level.FINEST) {
                trail = "[   ] ";
            }
            if (level == Level.INFO) {
                trail = "[ i ] ";
            }
            if (level == Level.SEVERE) {
                trail = "[!E!] ";
            }
            if (level == Level.WARNING) {
                trail = "[!W!] ";
            }

            record.setMessage(record.getMessage().replaceAll("\n", nl + trail));

            buf.append(trail);
            buf.append(formatMessage(record));

            buf.append(nl);

            final Throwable throwable = record.getThrown();
            if (throwable != null) {
                buf.append("at ");
                buf.append(record.getSourceClassName());
                buf.append('.');
                buf.append(record.getSourceMethodName());
                buf.append(nl);

                final StringWriter sink = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sink, true));
                buf.append(sink.toString());

                buf.append(nl);
            }

            final String str = buf.toString();

            for (final LogMonitor mon : monitors.values()) {
                mon.log(level, str);
            }

            return str;
        }
    }

}
