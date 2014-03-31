import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Class Database This class reads and writes to a PostgreSQL database in which
 * the program uses.
 * 
 * @author Emelie Nordstrand Lindgren
 * @version 140325
 * 
 */
public class Database {

	private final boolean DEBUG = true;

	String url;
	Statement stmt;
	Connection conn;

	/**
	 * Default constructor that creates a connection to an PostgreSQL server
	 * running on localhost.
	 */
	public Database() {
		try {
			if (DEBUG) {
				System.out.println("Connecting...");
			}

			// Loading driver
			try {
				Class.forName("org.postgresql.Driver");
			} catch (Exception e) {
				System.err.println("Error: Unable to load driver: " + e.getLocalizedMessage());
			}
			url = "jdbc:postgresql://localhost:5432/kitchenManager";

			conn = DriverManager.getConnection(url, "postgres", "emelie");
			stmt = conn.createStatement();
			if (DEBUG) {
				System.out.println("Connection established!");
			}
		} catch (Exception e) {
			System.err.println("Error: Unable to connect to server: " + e.getLocalizedMessage());
			throw new RuntimeException("Error: Unable to connect to server ", e);
		}

	}

	public Statement getStatement() {
		return stmt;
	}

	public Connection getConnection() {
		return conn;
	}
}