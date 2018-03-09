package test.miip.pond.suite;

import java.util.logging.Logger;

import org.condast.commons.test.core.AbstractTestSuite;

import test.miip.pond.core.PondEnvironment;

public class TestSuite extends AbstractTestSuite {

	public enum Tests{
		TEST_COLLISION_AVOIDANCE,
	}
	
	private static TestSuite suite = new TestSuite();
	PondEnvironment env;

	private static Logger logger = Logger.getLogger( TestSuite.class.getName() );

	
	protected TestSuite() {
	}

	public static TestSuite getInstance(){
		return suite;
	}

	public void runTests(  ){
		try {
			testSuite();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	protected void testSuite() throws Exception {
		env = new PondEnvironment();
		Tests test = Tests.TEST_COLLISION_AVOIDANCE;
		logger.info("\n\n RUN TEST: " + test + "\n");
		try{
			env.clear();
			switch( test ){
			case TEST_COLLISION_AVOIDANCE:
				testEnvironment( env);
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

	private void testEnvironment( PondEnvironment env ){
		logger.info("START POSITION: " + env.getName());
		StringBuffer buffer = new StringBuffer();
		env.execute(1000);
		logger.info( buffer.toString());
	}

}