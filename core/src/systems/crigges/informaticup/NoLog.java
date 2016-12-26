package systems.crigges.informaticup;

import org.apache.commons.logging.Log;

public class NoLog implements Log{
	
		
		@Override
		public void warn(Object arg0, Throwable arg1) {}
		
		@Override
		public void warn(Object arg0) {}
		
		@Override
		public void trace(Object arg0, Throwable arg1) {}
		
		@Override
		public void trace(Object arg0) {}
		
		@Override
		public boolean isWarnEnabled() {return false;}
		
		@Override
		public boolean isTraceEnabled() {return false;}
		
		@Override
		public boolean isInfoEnabled() {return false;}
		
		@Override
		public boolean isFatalEnabled() {return false;}
		
		@Override
		public boolean isErrorEnabled() {return false;}
		
		@Override
		public boolean isDebugEnabled() {return false;}
		
		@Override
		public void info(Object arg0, Throwable arg1) {}
		
		@Override
		public void info(Object arg0) {}
		
		@Override
		public void fatal(Object arg0, Throwable arg1) {}
		
		@Override
		public void fatal(Object arg0) {}
		
		@Override
		public void error(Object arg0, Throwable arg1) {}
		
		@Override
		public void error(Object arg0) {}
		
		@Override
		public void debug(Object arg0, Throwable arg1) {}
		
		@Override
		public void debug(Object arg0) {}

}
