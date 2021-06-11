package test.miip.pond.suite;

import java.util.logging.Logger;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.test.core.AbstractTestSuite;
import org.condast.commons.test.core.ITestEvent;
import org.miip.waterway.model.IVessel;

import test.miip.pond.core.TestPondEnvironment;

public class TestSuite extends AbstractTestSuite<Object, Object> {

	public enum Tests{
		TEST_COLLISION_AVOIDANCE,
	}
	
	private static TestSuite suite = new TestSuite();
	TestPondEnvironment env;
	
	boolean completed;

	private IEnvironmentListener<IVessel> listener = new IEnvironmentListener<IVessel>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
			if( completed )
				return;
			completed = EventTypes.OUT_OF_BOUNDS.equals( event.getType());
		}
	};
	
	private static Logger logger = Logger.getLogger( TestSuite.class.getName() );

	
	protected TestSuite() {
		super("hoi", null);
	}

	public static TestSuite getInstance(){
		return suite;
	}

	public void runTests(  ){
		try {
			this.completed = false;
			testSuite();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	protected void testSuite() throws Exception {
		env = new TestPondEnvironment();
		Tests test = Tests.TEST_COLLISION_AVOIDANCE;
		logger.info("\n\n RUN TEST: " + test + "\n");
		try{
			env.clear();
			switch( test ){
			case TEST_COLLISION_AVOIDANCE:
				for( int i=0; i< 360; i++ ) {
					this.completed = false;
					for( int j=0; j< 360; j++ )		
						testEnvironment( env, i, j);
				}
				break;
			default:
				break;
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		logger.info("Tests completed");
	}

	private void testEnvironment( TestPondEnvironment env, int refAngle, int otherAngle ){
		logger.info("START POSITION: " + env.getName());
		env.clear(refAngle, otherAngle, 100);
		StringBuffer buffer = new StringBuffer();
		env.addListener(listener);
		while(!completed ) {
			boolean active = env.execute(1000);
			if(!completed && !active)
				completed = true;
		}
		env.removeListener(listener);
		logger.info( buffer.toString());
		logger.info("TEST COMPLETE");
	}

	@Override
	protected void onPrepare(ITestEvent<Object, Object> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPerform(ITestEvent<Object, Object> event) {
		// TODO Auto-generated method stub
		
	}

}