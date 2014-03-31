public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database connection = new Database();
			Menu menu = new Menu(connection);

		} catch (Exception e) {
			System.err.print(e.toString());
		}
	}
}
