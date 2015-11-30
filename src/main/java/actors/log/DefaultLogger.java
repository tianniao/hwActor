package actors.log;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import actors.util.StringUtils;

public class DefaultLogger implements Logger {

	private static final String myCname = DefaultLogger.class.getName();

	private static DefaultLogger instance;

	public static DefaultLogger getDefaultInstance() {
		if (instance == null) {
			instance = new DefaultLogger();
		}
		return instance;
	}

	protected static String getDefaultContext() {
		return null;
	}

	public DefaultLogger() {
		this(getDefaultContext());
	}

	public DefaultLogger(Object context) {
		this.context = context;
	}

	@Override
	public String toString() {
        return getClass().getName() + "[context=" + context + "]";
	}

    private String dateTimePattern = "%1$tY-%<tm-%<td %<tH:%<tM:%<tS.%<tL";

	private Object context;

	public Object getContext() {
		return context;
	}

	/**
	 * Defines a context (used to qualify logging messages). Some
	 * implementations use the context to filter logging.
	 * 
	 * @param context
	 *            generally a String or a Class.
	 */
	public void setContext(Object context) {
		this.context = context;
	}

	private LogLevel logLevel = LogLevel.INFO;

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

    private String safeFormat(String format, Object... args) {
        return String.format(format, args);
    }

    protected synchronized String log(LogLevel level, String message,
			Object... values) {
		String res = null;
		if (level.ordinal() >= logLevel.ordinal()) {
			Date now = new Date();
            // String nowDate = String.format(datePattern, now);
            String nowTime = String.format(dateTimePattern, now);
			PrintStream ps = getPrintStream(level);
			try {
				StringBuilder sb = new StringBuilder();
                // /////add time
                sb.append(nowTime);

                // /////add context
                if (context != null) {
					String text = context instanceof Class ? ((Class<?>) context)
							.getName() : context.toString();
                    sb.append(safeFormat(" (%s)", text));
				}

                // /////add log level
                sb.append(safeFormat(" %-7s ", level.toString()));

                // /////add thread name
                String tname = Thread.currentThread().getName();
                if (StringUtils.isNotBlank(tname)) {
                    sb.append(safeFormat("[%s] ", tname));
				}

                // /////add caller
                StackTraceElement ste = getCaller();
                if (ste != null) {
                    String cname = ste.getClassName();
                    String calledFrom = safeFormat("%s#%s:%d", cname, ste.getMethodName(), ste.getLineNumber());
                    sb.append(safeFormat("(%s)", calledFrom));
				}

                sb.append(safeFormat("- "));

                // /////add message
                sb.append((null == values || values.length == 0) ? message : safeFormat(message, values));
				sb.append('\n');

				String text = sb.toString();
				res = text;
                syncPrint(ps, text);

			} catch (Exception e) {
                syncPrint(ps, safeFormat("log exception %s, %s: %s%n",
						level, message, e));
			}
		}
		return res;
	}

	protected static StackTraceElement getCaller() {
		StackTraceElement res = null;
		StackTraceElement[] stea = Thread.currentThread().getStackTrace();
		for (int i = 0; res == null && i < stea.length; i++) {
			StackTraceElement ste = stea[i];
			if (ste.getClassName().equals(myCname)) {
				continue;
			}
			if (ste.getMethodName().indexOf("getStackTrace") == 0) {
				continue;
			}
			res = ste;
		}
		return res;
	}

	protected void syncPrint(PrintStream ps, String text) {
        synchronized (ps) {
			ps.print(text);
			ps.flush();
        }
	}

	@Override
	public void info(String message, Object... values) {
		log(LogLevel.INFO, message, values);
	}

	@Override
	public void trace(String message, Object... values) {
		log(LogLevel.TRACE, message, values);
	}

	@Override
	public void warning(String message, Object... values) {
        log(LogLevel.WARN, message, values);
	}

	protected String logSevere(LogLevel level, String message, Object... values) {
		String res = null;
		try {
			if (lastIsThrowable(values)) {
                res = log(level, safeFormat(message, values) + ": %s",
						(Throwable) values[values.length - 1]);
				String trace = logStackTrace(level,
						(Throwable) values[values.length - 1]);
                if (StringUtils.isNotBlank(trace)) {
					res += "\n\n" + trace;
				}
			} else {
				res = log(level, message, values);
			}
		} catch (Exception e) {
			System.out.printf("logSevere exception %s, %s: %s%n", level,
					message, e);
		}
		return res;
	}

	@Override
	public void error(String message, Object... values) {
		logSevere(LogLevel.ERROR, message, values);
	}

	protected boolean lastIsThrowable(Object... values) {
		return values != null && values.length > 0
				&& values[values.length - 1] instanceof Throwable;
	}

	protected String logStackTrace(LogLevel level, Throwable t) {
		String res = null;
        if (t != null) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			String text = sw.toString();
			res = text;
            getPrintStream(level).print(text);
		}
		return res;
	}

	protected PrintStream getPrintStream(LogLevel level) {
		// PrintStream out = level == LogLevel.NOTIFY ? System.err : System.out;
		PrintStream out = System.out;
		return out;
	}

    // test case
    public static void main(String[] args) {
        DefaultLogger lu = new DefaultLogger();

        lu.info("info message: %s", "sub");
        lu.warning("warn message: %s", "sub");
        lu.error("error message: %s", "sub");

        Exception e = new Exception("test exception");
        lu.error("error message", e);

        lu.setContext(DefaultLogger.class);
        lu.info("info message: %s", "sub");
    }

}
