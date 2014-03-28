import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;

public class Menyn {

	Database connection;
	Scanner sc;
	Statement stmt;
	ResultSet res;

	public Menyn(Database connection) {
		this.connection = connection;
		this.sc = new Scanner(System.in);
		stmt = connection.getStatement();
		printWelcomeMessage();
	}

	private void printWelcomeMessage() {
		System.out.println("Welcome to kitchenManager");
		printMenu();
	}

	private void printMenu() {
		System.out.println("1. List all ingrediens in kitchen");
		System.out.println("2. Show recipes");
		System.out.println("3. Been shopping");
		System.out.println("4. Get recipe ingrediense list");
		System.out.println("5. Finished cooking");
		System.out.println("6. Get possible recipes based on current food storage");
		System.out.println("7. Choose recipes för grocery shopping");
		System.out.println("8. Quit");

		int choice = getCommandInt();
		executeCommand(choice);
	}

	private void executeCommand(int choice) {
		switch (choice) {
			case 1:
				choice = 1;
				System.out.println("***INGREDIENS IN KITCHEN*** \n");
				System.out.println("Ingrd. \t\t amount \n");
				listAllIngKitchen();
				break;
			case 2:
				choice = 2;
				System.out.println("***RECIPE*** \n");
				listAllRec();
				break;
			case 3:
				choice = 3;
				System.out.println("ADD INGREDIENT \n");
				System.out.println("Enter ingredient name: ");
				String ingredientName = getCommandString();
				System.out.println("Enter amount: ");
				int ingredientAmount = getCommandInt();
				boolean success = addToKitchen(ingredientName, ingredientAmount);

				if (success) {
					System.out.println("Ingrediens added \n");
				} else
					System.out.println("Ingrediens not added \n");
				break;

			case 4:
				choice = 4;
				System.out.println("Enter recipes name \n");
				String recipeName = getCommandString();
				System.out.println("Ingrd. \t\t amount \n");
				listAllIngRec(recipeName);
				break;

			default:
				String invalid = "Invalid input";
				System.out.println(invalid);
				break;

		}
	}

	private boolean listAllRec() {
		String query = "SELECT * FROM recipes";
		return exQuery(query);
	}

	private boolean listAllIngKitchen() {
		String query = "SELECT * FROM inkitchen";
		return exQuery(query);

	}

	private boolean listAllIngRec(String recipeName) {
		String query = "SELECT ingrediensname, amount FROM inrecipe";
		return exQuery(query);

	}

	private boolean addToKitchen(String ingredientName, int ingredientAmount) {
		String query = "INSERT INTO inkitchen " + "(name, amount) VALUES ('" + ingredientName + "', '" + ingredientAmount + "')";
		return exQuery(query);
	}

	private int getCommandInt() {
		int choice = sc.nextInt();
		return choice;
	}

	private String getCommandString() {
		String commandString = sc.next();
		return commandString;
	}

	private boolean exQuery(String query) {
		try {
			res = stmt.executeQuery(query);
			printResult(res);
			return true;
		} catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}

	}

	private void printResult(ResultSet res) throws SQLException {
		ResultSetMetaData rsmd = res.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (res.next()) {
			for (int i = 1; i < columnsNumber + 1; i++) {
				if (columnTypeIsString(i, rsmd)) {
					System.out.print(res.getString(i) + "\t\t");
				} else
					System.out.print(res.getInt(i) + "\t\t");

			}
			System.out.println("");
		}
	}

	private boolean columnTypeIsString(int i, ResultSetMetaData rsmd) throws SQLException {
		int sqlType = rsmd.getColumnType(i);
		if (sqlType == Types.INTEGER) {
			return false;
		}
		return true;

	}
}
