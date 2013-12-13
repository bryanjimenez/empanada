/**
 * 
 * @author jloza015
 *
 */
public class SchedulerTester {

	public static void main(String args[]) {
		
		A threadA = new A();
		B threadB = new B();
		C threadC = new C();
		threadC.setPriority(Thread.MAX_PRIORITY);
		threadB.setPriority(threadA.getPriority() + 1);
		threadA.setPriority(Thread.MIN_PRIORITY);

		System.out.println("Started Thread A");
		//threadA.start();
		System.out.println("Started Thread B");
		threadB.start();
		System.out.println("Started Thread C");
		//threadC.start();
		System.out.println("End of main thread");
		
	}
}