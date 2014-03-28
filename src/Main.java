public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database connection = new Database();
			Menyn meny = new Menyn(connection);

		} catch (Exception e) {
			System.err.print(e.toString());
		}
	}
}
