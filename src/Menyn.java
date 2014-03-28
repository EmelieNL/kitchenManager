import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Menyn {

	Database connection;
	Scanner sc;

	public Menyn(Database connection) {
		this.connection = connection;
		this.sc = new Scanner(System.in);
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
		System.out.println("4. Get recipe");
		System.out.println("5. Finished cooking");
		System.out.println("6. Get possible recipes based on current food storage");
		System.out.println("7. List all ingrediens in kitchen");
		System.out.println("8. Choose recipes för grocery shopping");
		System.out.println("9. Quit");

		int choice = getCommandInt();
		executeCommand(choice);
	}

	private void executeCommand(int choice) {
		switch (choice) {
			case 1:
				choice = 1;
				listAllIng();
				break;
			case 2:
				choice = 2;
				listAllRec();
				break;
			case 3:
				choice = 3;
				System.out.println("ADD INGREDIENT");
				System.out.println("Enter ingredient name: ");
				String ingredientName = getCommandString();
				System.out.println("Enter amount: ");
				int ingredientAmount = getCommandInt();
				boolean success = addToKitchen(ingredientName, ingredientAmount);

				if (success) {
					System.out.println("Ingrediens added");
				} else
					System.out.println("Ingrediens not added");
				break;
			default:
				String invalid = "Invalid input";
				System.out.println(invalid);
				break;
		}
	}

	private void listAllRec() {
		// TODO Auto-generated method stub

	}

	private void listAllIng() {
		// TODO Auto-generated method stub

	}

	private boolean addToKitchen(String ingredientName, int ingredientAmount) {
		Statement stmt = connection.getStatement();
		String query = "INSERT INTO inkitchen " + "(name, amount) VALUES ('" + ingredientName + "', '" + ingredientAmount + "')";
		try {
			stmt.execute(query);
			return true;
		} catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
	}

	private int getCommandInt() {
		int choice = sc.nextInt();
		return choice;
	}

	private String getCommandString() {
		String commandString = sc.next();
		return commandString;
	}
}
