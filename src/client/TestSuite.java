package client;

public class TestSuite {

	public static void main(String[] args){

		// run some tests and make sure the output is the same
		correctnessTests();

	}

	/*
	 * Test basic correctness
	 */
	private static void correctnessTests(){
		UDPClient.run();

	}

}
