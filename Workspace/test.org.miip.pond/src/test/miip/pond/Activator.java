package test.miip.pond;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import test.miip.pond.suite.TestSuite;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private TestSuite suite = TestSuite.getInstance();

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		suite.runTests();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
